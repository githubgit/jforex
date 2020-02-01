package com.dukascopy.indicators;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.dukascopy.api.DataType;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ICloseOrder;
import com.dukascopy.api.ICurrency;
import com.dukascopy.api.IFillOrder;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.LoadingOrdersListener;
import com.dukascopy.api.LoadingProgressListener;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.IChartInstrumentsListener;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class ProfitLossIndicator implements IIndicator, IChartInstrumentsListener {

    private static final long START_TIME = 946684800000L; // 01.01.2000

    private IIndicatorContext context;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final Object[] inputs = new Object[3];
    private final double[][] outputs = new double[3][];
    private int showMode = 0;

    private final Object[] prevInputs = new Object[3];
    private final double[][] prevOutputs = new double[3][];
    private int prevShowMode;

    private Instrument prevInstrument;
    private Period prevPeriod;
    private int prevInputStart;
    private int prevInputEnd;
    private boolean prevUnfinished;

    private PLLoadingOrdersListener loadingOrdersListener;
    private PLLoadingProgressListener loadingProgressListener;

    private PLCalculationTask calculationTask;
    private Future<?> calculationFuture;

    @Override
    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("P/L", "Profit/Loss", "Account Statistics", false, false, false, 3, 1, 3);
        indicatorInfo.setSupportedDataTypes(DataType.TIME_PERIOD_AGGREGATION, DataType.TICKS);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bars", InputParameterInfo.Type.BAR),
            new InputParameterInfo("Bid", InputParameterInfo.Type.DOUBLE),
            new InputParameterInfo("Ask", InputParameterInfo.Type.DOUBLE)
        };

        inputParameterInfos[1].setOfferSide(OfferSide.BID);
        inputParameterInfos[2].setOfferSide(OfferSide.ASK);

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Show P/L in", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(0, new int[] {0}, new String[] {""}))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Total", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Closed", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Open", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[1].setShowOutput(false);
        outputParameterInfos[2].setShowOutput(false);

        onInstrumentsChanged(context.getChartInstruments());
        context.addChartInstrumentsListener(this);
    }

    @Override
    public void onInstrumentsChanged(Instrument[] chartInstr) {
        if (context.getAccount() != null && chartInstr != null) {
            String accountCurrency = context.getAccount().getAccountCurrency().getCurrencyCode();
            String chartCurrency = chartInstr[0].getSecondaryJFCurrency().getCurrencyCode();

            if (!chartCurrency.equals(accountCurrency)) {
                optInputParameterInfos[0].setDescription(
                        new IntegerListDescription(0, new int[] {0, 1}, new String[] {accountCurrency, chartCurrency}));
            } else {
                optInputParameterInfos[0].setDescription(
                        new IntegerListDescription(0, new int[] {0}, new String[] {accountCurrency}));
            }
        }
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (endIndex + getLookforward() > ((IBar[]) inputs[0]).length - 1) {
            endIndex = ((IBar[]) inputs[0]).length - 1 - getLookforward();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0, 0);
        }

        for (int i = 0; i < outputs.length; i++) {
            Arrays.fill(outputs[i], Double.NaN);
        }

        DataType dataType = context.getFeedDescriptor().getDataType();
        Instrument instrument = context.getFeedDescriptor().getInstrument();
        Period period = (dataType.equals(DataType.TICKS) ? Period.ONE_SEC : context.getFeedDescriptor().getPeriod());

        int fromIndex = startIndex;
        int toIndex = endIndex;

        if (showMode == prevShowMode && instrument.equals(prevInstrument) && period.equals(prevPeriod)) {
            // try to use previously calculated values
            history_loop:
            for (int i1 = startIndex, j1 = 0, i2 = prevInputStart, j2 = 0; i1 <= endIndex; i1++, j1++) {
                while (i2 <= prevInputEnd &&
                        ((IBar[]) prevInputs[0])[i2].getTime() < ((IBar[]) inputs[0])[i1].getTime()) {
                    i2++;
                    j2++;
                }
                if (i2 > prevInputEnd ||
                        ((IBar[]) prevInputs[0])[i2].getTime() != ((IBar[]) inputs[0])[i1].getTime() ||
                        (((double[]) prevInputs[1])[i2] != ((double[]) inputs[1])[i1] ||
                        ((double[]) prevInputs[2])[i2] != ((double[]) inputs[2])[i1]) &&
                        (i2 < prevInputEnd || !prevUnfinished)) {
                    break;
                }
                for (int k = 0; k < outputs.length; k++) {
                    if (Double.isNaN(prevOutputs[k][j2])) {
                        break history_loop;
                    }
                    outputs[k][j1] = prevOutputs[k][j2];
                }
                if (i2 < prevInputEnd || !prevUnfinished) {
                    fromIndex++;
                }
            }

        } else {
            if (loadingProgressListener != null) {
                loadingProgressListener.cancelLoading();
            }
            if (calculationFuture != null) {
                calculationFuture.cancel(true);
            }
        }

        prevShowMode = showMode;
        prevInstrument = instrument;
        prevPeriod = period;
        prevInputStart = startIndex;
        prevInputEnd = endIndex;
        prevUnfinished = false;

        if (fromIndex > toIndex) {
            return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
        }

        try {
            long fromTime, toTime;
            if (fromIndex == startIndex) {
                fromTime = START_TIME;
            } else {
                fromTime = ((IBar[]) inputs[0])[fromIndex].getTime();
            }
            if (toIndex + 1 < ((IBar[]) inputs[0]).length) {
                toTime = ((IBar[]) inputs[0])[toIndex + 1].getTime() - 1;
            } else {
                toTime = context.getHistory().getNextBarStart(period, ((IBar[]) inputs[0])[toIndex].getTime()) - 1;
                long lastTime = context.getHistory().getTimeOfLastTick(instrument);
                if (toTime > lastTime) {
                    toTime = lastTime;
                    prevUnfinished = true;
                }
            }
            if (fromTime > toTime) {
                return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
            }

            if (loadingProgressListener != null) {
                if (loadingProgressListener.getTimeFrom() > fromTime) {
//                    System.out.println("cancel previous loading");
                    loadingProgressListener.cancelLoading();

                } else if (loadingProgressListener.getTimeTo() < (prevUnfinished ? ((IBar[]) inputs[0])[toIndex].getTime() - 1 : toTime)) {
                    int tmpIndex = toIndex;
                    do {
                        tmpIndex--;
                    } while (tmpIndex >= fromIndex && loadingProgressListener.getTimeTo() < ((IBar[]) inputs[0])[tmpIndex + 1].getTime() - 1);

                    if (tmpIndex >= fromIndex) {
                        toIndex = tmpIndex;
                        toTime = ((IBar[]) inputs[0])[tmpIndex + 1].getTime() - 1;
                    } else {
//                        System.out.println("cancel previous loading");
                        loadingProgressListener.cancelLoading();
                    }
                }

                if (!loadingProgressListener.isLoadingFinished()) {
//                    System.out.println("waiting for loading finished...");
                    return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);

                } else if (loadingProgressListener.isLoadingCancelled()) {
                    loadingOrdersListener = null;
                    loadingProgressListener = null;
                }
            }

            if (calculationFuture != null) {
                if (calculationTask.getTimeFrom() > fromTime) {
//                    System.out.println("cancel previous calculation");
                    calculationFuture.cancel(true);

                } else if (calculationTask.getTimeTo() < (prevUnfinished ? ((IBar[]) inputs[0])[toIndex].getTime() - 1 : toTime)) {
                    int tmpIndex = toIndex;
                    do {
                        tmpIndex--;
                    } while (tmpIndex >= fromIndex && calculationTask.getTimeTo() < ((IBar[]) inputs[0])[tmpIndex + 1].getTime() - 1);

                    if (tmpIndex >= fromIndex) {
                        toIndex = tmpIndex;
                        toTime = ((IBar[]) inputs[0])[tmpIndex + 1].getTime() - 1;
                    } else {
//                        System.out.println("cancel previous calculation");
                        calculationFuture.cancel(true);
                    }
                }

                if (!calculationFuture.isDone()) {
//                    System.out.println("waiting for calculation finished...");
                    return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);

                } else if (calculationFuture.isCancelled()) {
                    calculationTask = null;
                    calculationFuture = null;
                }
            }

            if (loadingProgressListener == null && calculationFuture == null) {
//                java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//                System.out.println("loading orders: " + dateFormat.format(new java.util.Date(fromTime)) +
//                        " - " + dateFormat.format(new java.util.Date(toTime)));

                loadingOrdersListener = new PLLoadingOrdersListener();
                loadingProgressListener = new PLLoadingProgressListener(fromTime, toTime);

                context.getHistory().readOrdersHistory(instrument, fromTime, toTime, loadingOrdersListener, loadingProgressListener);

                return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
            }

            if (loadingProgressListener != null) {
                Collection<IOrder> orders = loadingOrdersListener.getResult();

                loadingOrdersListener = null;
                loadingProgressListener = null;

                orders.addAll(context.getHistory().getOpenOrders(instrument, Long.MIN_VALUE, toTime));

//                System.out.println(orders.toString().replace(", [", ",\r\n["));

//                java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//                System.out.println("processing orders: " + dateFormat.format(new java.util.Date(fromTime)) +
//                        " - " + dateFormat.format(new java.util.Date(toTime)));

                calculationTask = new PLCalculationTask(
                        Arrays.copyOfRange((IBar[]) inputs[0], fromIndex, toIndex + 1),
                        Arrays.copyOfRange((double[]) inputs[1], fromIndex, toIndex + 1),
                        Arrays.copyOfRange((double[]) inputs[2], fromIndex, toIndex + 1),
                        orders,
                        fromTime,
                        toTime,
                        showMode == 0 && ((IntegerListDescription) optInputParameterInfos[0].getDescription()).getValues().length > 1 ?
                                context.getAccount().getAccountCurrency() : null
                );

                calculationFuture = context.calculateAsynchronously(calculationTask);

                return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
            }

            IBar[] inputBars;
            double[] closedPL;
            double[] openPL;

            try {
                calculationFuture.get();

                inputBars = calculationTask.getInputBars();
                closedPL = calculationTask.getClosedPL();
                openPL = calculationTask.getOpenPL();

            } finally {
                calculationTask = null;
                calculationFuture = null;
            }

            int i = 0;
            while (inputBars[i].getTime() < ((IBar[]) inputs[0])[fromIndex].getTime()) {
                i++;
            }

            if (inputBars[i].getTime() == ((IBar[]) inputs[0])[fromIndex].getTime()) {
                for (int j = fromIndex - startIndex; i < closedPL.length && j < outputs[0].length; i++, j++) {
                    outputs[1][j] = closedPL[i] + (j > 0 ? outputs[1][j - 1] : 0);
                    outputs[2][j] = openPL[i];
                    outputs[0][j] = outputs[1][j] + outputs[2][j];
                }

//                System.out.println(Arrays.toString(Arrays.copyOfRange(outputs[0], fromIndex - startIndex, toIndex - startIndex + 1)));
            }

        } catch (ExecutionException ex) {
            context.getConsole().getErr().println(ex.getCause());

        } catch (Exception ex) {
            context.getConsole().getErr().println(ex);
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
    }

    @Override
    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    @Override
    public InputParameterInfo getInputParameterInfo(int index) {
        if (index < inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    @Override
    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    @Override
    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    @Override
    public void setInputParameter(int index, Object array) {
        prevInputs[index] = inputs[index];
        inputs[index] = array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        showMode = (Integer) value;
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        prevOutputs[index] = outputs[index];
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return 0;
    }

    @Override
    public int getLookforward() {
        return (indicatorInfo.isRecalculateOnNewCandleOnly() ? 1 : 0);
    }

    private static class PLLoadingOrdersListener implements LoadingOrdersListener {

        private final Collection<IOrder> result = new TreeSet<>(Comparator.comparingLong(IOrder::getFillTime));

        @Override
        public synchronized void newOrder(Instrument instrument, IOrder orderData) {
            result.add(orderData);
        }

        public synchronized Collection<IOrder> getResult() {
            return result;
        }
    }

    private static class PLLoadingProgressListener implements LoadingProgressListener {

        private final long timeFrom;
        private final long timeTo;

        private volatile boolean loadingFinished = false;
        private volatile boolean loadingCancelled = false;

        public PLLoadingProgressListener(long timeFrom, long timeTo) {
            this.timeFrom = timeFrom;
            this.timeTo = timeTo;
        }

        @Override
        public void dataLoaded(long start, long end, long currentPosition, String information) {
        }

        @Override
        public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
            loadingFinished = true;
        }

        @Override
        public boolean stopJob() {
            return loadingCancelled;
        }

        public long getTimeFrom() {
            return timeFrom;
        }

        public long getTimeTo() {
            return timeTo;
        }

        public boolean isLoadingFinished() {
            return loadingFinished;
        }

        public void cancelLoading() {
            loadingCancelled = true;
        }

        public boolean isLoadingCancelled() {
            return loadingCancelled;
        }
    }

    private class PLCalculationTask implements Callable<Void> {

        private final IBar[] inputBars;
        private final double[] bidPrices;
        private final double[] askPrices;

        private final Collection<IOrder> orders;

        private final long timeFrom;
        private final long timeTo;
        private final ICurrency targetCurrency;

        private final double[] closedPL;
        private final double[] openPL;

        private int curPercent = -1;

        public PLCalculationTask(
                IBar[] inputBars,
                double[] bidPrices,
                double[] askPrices,
                Collection<IOrder> orders,
                long timeFrom,
                long timeTo,
                ICurrency targetCurrency
        ) {
            this.inputBars = inputBars;
            this.bidPrices = bidPrices;
            this.askPrices = askPrices;

            this.orders = orders;

            this.timeFrom = timeFrom;
            this.timeTo = timeTo;
            this.targetCurrency = targetCurrency;

            closedPL = new double[inputBars.length];
            openPL = new double[inputBars.length];
        }

        @Override
        public Void call() throws Exception {

            Iterator<IOrder> iterator = orders.iterator();
            IOrder order = (iterator.hasNext() ? iterator.next() : null);

            // skip earlier orders
            while (order != null && order.getCloseTime() != Long.MIN_VALUE && order.getCloseTime() < timeFrom) {
                order = (iterator.hasNext() ? iterator.next() : null);
            }

            for (int i = 0; i < inputBars.length && !Thread.currentThread().isInterrupted(); i++) {
                long nextBarTime = (i + 1 < inputBars.length ? inputBars[i + 1].getTime() : timeTo + 1);

                // iterate through already open orders
                while (order != null && order.getFillTime() < nextBarTime && !Thread.currentThread().isInterrupted()) {

                    // correct values for next bars until order closing
                    for (int n = 0; i + n < inputBars.length && !Thread.currentThread().isInterrupted(); n++) {
                        long nBarTime = inputBars[i + n].getTime();
                        long nNextBarTime = (i + n + 1 < inputBars.length ? inputBars[i + n + 1].getTime() : timeTo + 1);

                        long fillTime = order.getFillTime();
                        double openPrice = order.getOpenPrice();
                        double openAmount = order.getOriginalAmount();

                        for (IFillOrder fill : order.getFillHistory()) {
                            if (fill.getTime() >= nNextBarTime) {
                                break;
                            }
                            // order filled/merged
                            fillTime = fill.getTime();
                            openPrice = fill.getPrice();
                            openAmount = fill.getAmount();
                        }

                        for (ICloseOrder close : order.getCloseHistory()) {
                            if (close.getTime() >= nNextBarTime) {
                                break;
                            }

                            if (close.getTime() >= (i + n == 0 ? timeFrom : nBarTime)) {
                                // order (partially) closed during this bar
                                double prevOpenPrice = openPrice;
                                for (IFillOrder fill : order.getFillHistory()) {
                                    if (fill.getTime() > close.getTime()) {
                                        break;
                                    }
                                    prevOpenPrice = fill.getPrice();
                                }

                                closedPL[i + n] += getProfitLoss(order, prevOpenPrice, close.getPrice(), close.getAmount(), close.getTime());
                            }

                            if (close.getTime() >= fillTime) {
                                openAmount -= close.getAmount();
                                openAmount = round(openAmount, 3);
                            }
                        }

                        if (order.getCloseTime() != Long.MIN_VALUE && order.getCloseTime() < nNextBarTime) {
                            // order fully closed
                            break;
                        }

                        // order still open
                        openPL[i + n] += getProfitLoss(order, openPrice, order.isLong() ? bidPrices[i + n] : askPrices[i + n], openAmount, nBarTime);

                        updateProgress(i + n);
                    }

                    order = (iterator.hasNext() ? iterator.next() : null);
                }

                updateProgress(i);
            }

            return null;
        }

        private double getProfitLoss(IOrder order, double openPrice, double closePrice, double amount, long time) {

            double plSum = (closePrice - openPrice) * (order.isLong() ? +1 : -1) * amount * order.getInstrument().getAmountPerContract() * 1000000;

            if (targetCurrency != null) {
                // convert to account currency
                plSum = context.getCurrencyConverter().convertProfitLoss(plSum, order.getInstrument().getSecondaryJFCurrency(), targetCurrency, 2, time);
            }

            return plSum;
        }

        private double round(double value, int scale) {
            if (!Double.isNaN(value)) {
                value = new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            return value;
        }

        private void updateProgress(int i) {
            int percent = (int) ((i + 1) * 100.0 / inputBars.length);
            if (curPercent < percent) {
                context.updateCalculationProgress(percent);
                curPercent = percent;
            }
        }

        public IBar[] getInputBars() {
            return inputBars;
        }

        public long getTimeFrom() {
            return timeFrom;
        }

        public long getTimeTo() {
            return timeTo;
        }

        public double[] getClosedPL() {
            return closedPL;
        }

        public double[] getOpenPL() {
            return openPL;
        }
    }
}
