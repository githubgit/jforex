package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;

import com.dukascopy.api.DataType;
import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.LoadingDataListener;
import com.dukascopy.api.LoadingProgressListener;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IPriceAggregationBar;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IFormulaTimeData;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class MiddlePricesIndicator implements IIndicator, IDrawingIndicator {

    private static final long MAX_LOADING_PERIOD = 100_000_000; // ms
    private static final int NUMBER_OF_OUTPUTS = 5;

    private IIndicatorContext context;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final Object[] outputs = new Object[NUMBER_OF_OUTPUTS];

    private final IBar[][] prevInputs = new IBar[1][];
    private final Object[] prevOutputs = new Object[NUMBER_OF_OUTPUTS];

    private IFeedDescriptor prevFeedDescriptor;
    private int prevInputStart;
    private int prevInputEnd;

    private MPLoadingDataListener loadingDataListener;
    private MPLoadingProgressListener loadingProgressListener;

    @Override
    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("MIDDLE_PRICES", "Middle prices", "", true, false, false, 1, 0, NUMBER_OF_OUTPUTS);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bars", InputParameterInfo.Type.BAR)
        };

        optInputParameterInfos = new OptInputParameterInfo[0];

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Middle prices", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES),
            new OutputParameterInfo("High Ask", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("High Bid", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Low Ask", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Low Bid", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.FOREST_GREEN);
        outputParameterInfos[0].setColor2(DefaultColors.DARK_RED);
        outputParameterInfos[0].setOpacityAlpha(0.5f);
        outputParameterInfos[1].setColor(DefaultColors.DARK_ORANGE);
        outputParameterInfos[1].setDrawnByIndicator(true);
        outputParameterInfos[2].setColor(DefaultColors.RED);
        outputParameterInfos[2].setDrawnByIndicator(true);
        outputParameterInfos[3].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[3].setDrawnByIndicator(true);
        outputParameterInfos[4].setColor(DefaultColors.GRAY);
        outputParameterInfos[4].setDrawnByIndicator(true);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0, 0);
        }

        for (int i = 0; i < outputs.length; i++) {
            if (i == 0) {
                Arrays.fill((IBar[]) outputs[i], null);
            } else {
                Arrays.fill((double[]) outputs[i], Double.NaN);
            }
        }

        IFeedDescriptor feedDescriptor = context.getFeedDescriptor();
        Instrument instrument = feedDescriptor.getInstrument();
        Period period = feedDescriptor.getPeriod();
        DataType dataType = feedDescriptor.getDataType();

        List<int[]> gaps = new ArrayList<>();
        gaps.add(new int[] {startIndex, endIndex});
        boolean usePrevData = (prevInputs != null && prevOutputs != null && feedDescriptor.equals(prevFeedDescriptor));

        if (!usePrevData && loadingProgressListener != null) {
            loadingProgressListener.cancelLoading();
        }

        // try to use previously calculated values
        for (int inputIndex = startIndex, outputIndex = 0, prevInputIndex = prevInputStart, prevOutputIndex = 0;
                inputIndex <= endIndex && usePrevData; inputIndex++, outputIndex++) {

            boolean isGap = true;
            while (prevInputIndex <= prevInputEnd && prevInputs[0][prevInputIndex].getTime() < inputs[0][inputIndex].getTime()) {
                prevInputIndex++;
                prevOutputIndex++;
            }
            if (prevInputIndex > prevInputEnd) {
                usePrevData = false;
            } else if (prevInputs[0][prevInputIndex].getTime() == inputs[0][inputIndex].getTime()) {
                isGap = false;
                for (int outputSeries = 0; outputSeries < outputs.length; outputSeries++) {
                    if (outputSeries == 0) {
                        if (((IBar[]) prevOutputs[outputSeries])[prevOutputIndex] == null) {
                            isGap = true;
                            break;
                        }
                        ((IBar[]) outputs[outputSeries])[outputIndex] = ((IBar[]) prevOutputs[outputSeries])[prevOutputIndex];
                    } else {
                        if (Double.isNaN(((double[]) prevOutputs[outputSeries])[prevOutputIndex])) {
                            isGap = true;
                            break;
                        }
                        ((double[]) outputs[outputSeries])[outputIndex] = ((double[]) prevOutputs[outputSeries])[prevOutputIndex];
                    }
                }
            }

            int[] lastGap = gaps.get(gaps.size() - 1);
            if (isGap) {
                if (inputIndex > lastGap[1]) {
                    if (lastGap[0] <= lastGap[1]) {
                        lastGap = new int[2];
                        gaps.add(lastGap);
                    }
                    lastGap[0] = inputIndex;
                    lastGap[1] = endIndex;
                }
            } else {
                if (inputIndex <= lastGap[1]) {
                    lastGap[1] = inputIndex - 1;
                }
            }
        }

        prevFeedDescriptor = feedDescriptor;
        prevInputStart = startIndex;
        prevInputEnd = endIndex;

        try {
            ListIterator<int[]> gapsIter = gaps.listIterator(gaps.size());
            while (gapsIter.hasPrevious()) {
                int[] gap = gapsIter.previous();
                int fromIndex = gap[0];
                int toIndex = gap[1];
                if (fromIndex > toIndex) {
                    continue;
                }

                long fromTime = inputs[0][fromIndex].getTime();
                long toTime;
                if (toIndex + 1 < inputs[0].length) {
                    toTime = inputs[0][toIndex + 1].getTime() - 1;
                } else {
                    if (dataType.equals(DataType.TIME_PERIOD_AGGREGATION) || dataType.equals(DataType.TICKS)) {
                        if (!period.isTickBasedPeriod()) {
                            toTime = context.getHistory().getNextBarStart(period, inputs[0][toIndex].getTime()) - 1;
                        } else {
                            toTime = inputs[0][toIndex].getTime() + Period.ONE_SEC.getInterval() - 1;
                        }
                        long lastTime = context.getHistory().getTimeOfLastTick(instrument);
                        if (toTime > lastTime) {
                            toTime = lastTime;
                            prevInputEnd--;
                        }
                    } else {
                        toTime = ((IPriceAggregationBar) inputs[0][toIndex]).getEndTime();
                        prevInputEnd--;
                    }
                }
                if (toTime - fromTime > MAX_LOADING_PERIOD) {
                    fromTime = toTime - MAX_LOADING_PERIOD;
                    if (dataType.equals(DataType.TIME_PERIOD_AGGREGATION) || dataType.equals(DataType.TICKS)) {
                        long interval = (!period.isTickBasedPeriod() ? period.getInterval() : 1000);
                        fromIndex = Math.max(toIndex - (int) Math.ceil((double) MAX_LOADING_PERIOD / interval) + 1, fromIndex);
                    }
                    while (inputs[0][fromIndex].getTime() < fromTime && fromIndex < toIndex) {
                        fromIndex++;
                    }
                    fromTime = inputs[0][fromIndex].getTime();
                    if (fromIndex > gap[0]) {
                        gapsIter.add(new int[] {gap[0], fromIndex - 1});
                        gap[0] = fromIndex;
                    }
                }
                if (fromTime > toTime) {
                    continue;
                }

                boolean isLastGap = (gapsIter.nextIndex() == gaps.size() - 1);
                boolean loadingCurrentGap = false;

                if (loadingProgressListener != null) {
                    loadingCurrentGap = (loadingProgressListener.getTimeFrom() == fromTime && loadingProgressListener.getTimeTo() == toTime);

                    if (!loadingCurrentGap && !isLastGap) {
//                        System.out.println("cancel loading: " +
//                                com.dukascopy.api.util.DateUtils.format(new java.util.Date(loadingProgressListener.getTimeFrom())) + " - " +
//                                com.dukascopy.api.util.DateUtils.format(new java.util.Date(loadingProgressListener.getTimeTo())));

                        loadingProgressListener.cancelLoading();
                    }

                    if (!loadingProgressListener.isLoadingFinished() && loadingCurrentGap) {
                        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);

                    } else if (loadingProgressListener.isLoadingCanceled()) {
                        loadingDataListener = null;
                        loadingProgressListener = null;
                    }
                }

                if (loadingProgressListener == null && !isLastGap) {
//                    System.out.println("start loading: " +
//                            com.dukascopy.api.util.DateUtils.format(new java.util.Date(fromTime)) + " - " +
//                            com.dukascopy.api.util.DateUtils.format(new java.util.Date(toTime)));

                    loadingDataListener = new MPLoadingDataListener();
                    loadingProgressListener = new MPLoadingProgressListener(fromTime, toTime);

                    context.getHistory().readTicks(instrument, fromTime, toTime, loadingDataListener, loadingProgressListener);

                    return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
                }

                List<ITick> ticks;
                if (loadingProgressListener != null && loadingCurrentGap) {
//                    System.out.println("loading finished: " +
//                            com.dukascopy.api.util.DateUtils.format(new java.util.Date(fromTime)) + " - " +
//                            com.dukascopy.api.util.DateUtils.format(new java.util.Date(toTime)));

                    ticks = loadingDataListener.getResult();
                    loadingDataListener = null;
                    loadingProgressListener = null;

                } else {
//                    System.out.println("loading data: " +
//                            com.dukascopy.api.util.DateUtils.format(new java.util.Date(fromTime)) + " - " +
//                            com.dukascopy.api.util.DateUtils.format(new java.util.Date(toTime)));

                    ticks = context.getHistory().getTicks(instrument, fromTime, toTime);
                }

//                System.out.println(ticks.size() + " : " + ticks);

                for (int i = fromIndex, j = fromIndex - startIndex, k = 0; i <= toIndex; i++, j++) {
                    while (k < ticks.size() && ticks.get(k).getTime() < inputs[0][i].getTime()) {
                        k++;
                    }

                    long time = inputs[0][i].getTime();
                    double openMid = Double.NaN, highMid = Double.NaN, lowMid = Double.NaN, closeMid = Double.NaN, volumeMid = Double.NaN;
                    double highAsk = Double.NaN, highBid = Double.NaN, lowAsk = Double.NaN, lowBid = Double.NaN;
                    int count = 0;

                    while (k < ticks.size() && (i + 1 >= inputs[0].length || ticks.get(k).getTime() < inputs[0][i + 1].getTime())) {

                        double midPrice = round((ticks.get(k).getAsk() + ticks.get(k).getBid()) / 2, instrument.getTickScale());
                        double midVolume = (ticks.get(k).getAskVolume() + ticks.get(k).getBidVolume()) / 2;

                        if (Double.isNaN(openMid)) {
                            openMid = midPrice;
                        }
                        if (Double.isNaN(highMid) || highMid < midPrice) {
                            highMid = midPrice;
                        }
                        if (Double.isNaN(lowMid) || lowMid > midPrice) {
                            lowMid = midPrice;
                        }
                        closeMid = midPrice;
                        if (Double.isNaN(volumeMid)) {
                            volumeMid = midVolume;
                        } else {
                            volumeMid += midVolume;
                        }

                        if (Double.isNaN(highAsk) || highAsk < ticks.get(k).getAsk()) {
                            highAsk = ticks.get(k).getAsk();
                        }
                        if (Double.isNaN(highBid) || highBid < ticks.get(k).getBid()) {
                            highBid = ticks.get(k).getBid();
                        }
                        if (Double.isNaN(lowAsk) || lowAsk > ticks.get(k).getAsk()) {
                            lowAsk = ticks.get(k).getAsk();
                        }
                        if (Double.isNaN(lowBid) || lowBid > ticks.get(k).getBid()) {
                            lowBid = ticks.get(k).getBid();
                        }

                        count++;
                        k++;
                    }

                    if (count == 0) {
                        // flat bar
                        openMid = inputs[0][i].getOpen();
                        highMid = inputs[0][i].getHigh();
                        lowMid = inputs[0][i].getLow();
                        closeMid = inputs[0][i].getClose();
                        volumeMid = inputs[0][i].getVolume();

                        highAsk = inputs[0][i].getHigh();
                        highBid = inputs[0][i].getHigh();
                        lowAsk = inputs[0][i].getLow();
                        lowBid = inputs[0][i].getLow();

                    } else {
                        volumeMid = round(volumeMid, 3);
                    }

                    ((IBar[]) outputs[0])[j] = new Candle(time, openMid, highMid, lowMid, closeMid, volumeMid);
                    ((double[]) outputs[1])[j] = highAsk;
                    ((double[]) outputs[2])[j] = highBid;
                    ((double[]) outputs[3])[j] = lowAsk;
                    ((double[]) outputs[4])[j] = lowBid;
                }
            }

        } catch (JFException ex) {
            context.getConsole().getErr().println(ex);
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
    }

    private double round(double amount, int decimalPlaces) {
        return new BigDecimal(amount).setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
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
        inputs[index] = (IBar[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        prevOutputs[index] = outputs[index];
        outputs[index] = array;
    }

    @Override
    public int getLookback() {
        return 0;
    }

    @Override
    public int getLookforward() {
        return 0;
    }

    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values, Color color, Stroke stroke,
            IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> shapes, Map<Color, List<Point>> handles) {

        GeneralPath path = new GeneralPath();

        boolean formulaHasSmallerPeriod = false;
        float candleWidth = indicatorDrawingSupport.getCandleWidthInPixels();
        float spaceBetweenCandles = indicatorDrawingSupport.getSpaceBetweenCandlesInPixels();

        Period chartPeriod = indicatorDrawingSupport.getPeriod();
        Period formulaPeriod = indicatorDrawingSupport.getFormulaPeriod();
        if (formulaPeriod != null && chartPeriod != Period.TICK) {
            if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                formulaHasSmallerPeriod = formulaPeriod.isSmallerThan(chartPeriod);
                if (formulaHasSmallerPeriod) {
                    float koef = chartPeriod.getInterval() / formulaPeriod.getInterval();
                    candleWidth /= koef;
                    spaceBetweenCandles /= koef;
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        int firstIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() - 1;
        int lastIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() + indicatorDrawingSupport.getNumberOfCandlesOnScreen();

        int shift = outputParameterInfos[outputIdx].getShift();
        if (shift > 0) {
            firstIndex = indicatorDrawingSupport.getShiftedIndex(firstIndex, -shift);
        } else if (shift < 0) {
            lastIndex = indicatorDrawingSupport.getShiftedIndex(lastIndex, -shift);
        }

        int length = ((double[]) values).length;
        if (firstIndex < 0) {
            firstIndex = 0;
        }
        if (lastIndex > length - 1) {
            lastIndex = length - 1;
        }

        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

        long lastTime;
        if (timeData[lastIndex] instanceof IPriceAggregationBar) {
            lastTime = ((IPriceAggregationBar) timeData[lastIndex]).getEndTime();
        } else {
            lastTime = timeData[lastIndex].getTime();
        }

        for (int idx = firstIndex; idx <= lastIndex; idx++) {
            double value = ((double[]) values)[idx];
            int shiftedIdx = idx;

            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, firstIndex, lastIndex);
                    }
                    formulaBars = formulaAllBars.get(idx);
                } else {
                    if (idx > firstIndex &&
                            !formulaTimeData.isFormulaBarStart(timeData[idx], idx > 0 ? timeData[idx - 1] : null)) {
                        continue;
                    }
                    if (shift != 0) {
                        shiftedIdx = indicatorDrawingSupport.getShiftedIndex(idx, shift);
                    }
                }
            } else {
                if (shift != 0) {
                    shiftedIdx = indicatorDrawingSupport.getShiftedIndex(idx, shift);
                }
            }

            for (int valueIdx = 0; valueIdx < (formulaBars != null ? formulaBars.length : 1); valueIdx++) {
                if (formulaTimeData != null && formulaBars != null) {
                    Double formulaValue = (Double) formulaTimeData.getFormulaValue(formulaBars[valueIdx], outputIdx);
                    if (formulaValue != null) {
                        value = formulaValue.doubleValue();
                    }
                }

                if (Double.isNaN(value)) {
                    continue;
                }

                int x;
                if (formulaBars != null) {
                    long time = formulaBars[valueIdx].getTime();
                    long shiftedTime = indicatorDrawingSupport.getShiftedTime(time, shift);
                    x = indicatorDrawingSupport.getXForTime(shiftedTime, false);
                    if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                        x += (spaceBetweenCandles + candleWidth) / 2;
                    }
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(shiftedIdx);
                }

                int y = (int) indicatorDrawingSupport.getYForValue(value);

                int x1 = x - (int) (candleWidth / 2);
                int x2 = x + (int) (candleWidth / 2);

                if (formulaTimeData != null) {
                    if (formulaBars != null) {
                        if (!indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                            long time = formulaBars[valueIdx].getTime();
                            long shiftedTime = indicatorDrawingSupport.getShiftedTime(time, shift);
                            shiftedTime = getNextBarStart(formulaPeriod, shiftedTime) - 1;
                            if (shift == 0 && shiftedTime > lastTime) {
                                shiftedTime = lastTime;
                            }
                            x2 = indicatorDrawingSupport.getXForTime(shiftedTime, false);
                            x1 = x;
                        }

                    } else {
                        int idx2 = idx;
                        ITimedData nextFormulaBar = formulaTimeData.getNextFormulaBar(timeData[idx]);
                        while (idx2 + 1 <= lastIndex && (nextFormulaBar == null ||
                                timeData[idx2 + 1].getTime() < nextFormulaBar.getTime())) {
                            double value2 = ((double[]) values)[idx2 + 1];
                            if (!Double.isNaN(value2)) {
                                idx2++;
                            } else {
                                break;
                            }
                        }
                        if (idx2 != idx) {
                            int shiftedIdx2 = shiftedIdx + (idx2 - idx);
                            x2 = (int) indicatorDrawingSupport.getMiddleOfCandle(shiftedIdx2);
                            x2 += candleWidth / 2;
                        }
                    }
                }

                path.moveTo(x1, y);
                path.lineTo(x2, y);
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(stroke);

        g2.setColor(color);
        g2.draw(path);

        g2.setStroke(oldStroke);

        shapes.add(path);

        return null;
    }

    private long getNextBarStart(Period period, long barTime) {
        long nextBarTime = barTime;
        try {
            nextBarTime = context.getHistory().getNextBarStart(period, barTime);
        } catch (JFException ex) {
            context.getConsole().getErr().println(ex.toString());
        }
        return nextBarTime;
    }


    private static class Tick implements ITick {
        private final long time;
        private final double ask;
        private final double bid;
        private final double askVolume;
        private final double bidVolume;

        public Tick(long time, double ask, double bid, double askVolume, double bidVolume) {
            this.time = time;
            this.ask = ask;
            this.bid = bid;
            this.askVolume = askVolume;
            this.bidVolume = bidVolume;
        }

        @Override
        public long getTime() {
            return time;
        }

        @Override
        public double getAsk() {
            return ask;
        }

        @Override
        public double getBid() {
            return bid;
        }

        @Override
        public double getAskVolume() {
            return askVolume;
        }

        @Override
        public double getBidVolume() {
            return bidVolume;
        }

        @Override
        public double[] getAsks() {
            return null;
        }

        @Override
        public double[] getBids() {
            return null;
        }

        @Override
        public double[] getAskVolumes() {
            return null;
        }

        @Override
        public double[] getBidVolumes() {
            return null;
        }

        @Override
        public double getTotalAskVolume() {
            return Double.NaN;
        }

        @Override
        public double getTotalBidVolume() {
            return Double.NaN;
        }

        @Override
        public String toString() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            StringBuilder str = new StringBuilder();
            str.append(format.format(time));
            str.append(" Ask: ").append(ask);
            str.append(" Bid: ").append(bid);
            str.append(" Ask Vol: ").append(askVolume);
            str.append(" Bid Vol: ").append(bidVolume);

            return str.toString();
        }
    }

    private static class Candle implements IBar {
        private final long time;
        private final double open;
        private final double high;
        private final double low;
        private final double close;
        private final double volume;

        public Candle(long time, double open, double high, double low, double close, double volume) {
            this.time = time;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }

        @Override
        public long getTime() {
            return time;
        }

        @Override
        public double getOpen() {
            return open;
        }

        @Override
        public double getHigh() {
            return high;
        }

        @Override
        public double getLow() {
            return low;
        }

        @Override
        public double getClose() {
            return close;
        }

        @Override
        public double getVolume() {
            return volume;
        }

        @Override
        public String toString() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            StringBuilder str = new StringBuilder();
            str.append(format.format(time));
            str.append(" O: ").append(open);
            str.append(" H: ").append(high);
            str.append(" L: ").append(low);
            str.append(" C: ").append(close);
            str.append(" V: ").append(volume);

            return str.toString();
        }
    }

    private static class MPLoadingDataListener implements LoadingDataListener {
        private final List<ITick> result = new ArrayList<>();

        @Override
        public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
            result.add(new Tick(time, ask, bid, askVol, bidVol));
        }

        @Override
        public void newBar(Instrument instrument, Period period, OfferSide side,
                long time, double open, double close, double low, double high, double vol) {
        }

        public synchronized List<ITick> getResult() {
            return result;
        }
    };

    private static class MPLoadingProgressListener implements LoadingProgressListener {
        private final long timeFrom;
        private final long timeTo;

        private volatile boolean loadingFinished = false;
        private volatile boolean loadingCanceled = false;

        public MPLoadingProgressListener(long timeFrom, long timeTo) {
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
            return loadingCanceled;
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
            loadingCanceled = true;
        }

        public boolean isLoadingCanceled() {
            return loadingCanceled;
        }
    };
}
