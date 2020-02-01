package com.dukascopy.indicators;

import java.util.Arrays;
import java.util.List;

import com.dukascopy.api.DataType;
import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.BooleanOptInputDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class CurrencySlopeStrength implements IIndicator {

    private static final String[] currencyNames = new String[] {"USD", "EUR", "GBP", "CHF", "JPY", "AUD", "CAD", "NZD"};

    private static final Instrument[] instruments = new Instrument[] {
        Instrument.AUDCAD, Instrument.AUDCHF, Instrument.AUDJPY, Instrument.AUDNZD, Instrument.AUDUSD, Instrument.CADJPY, Instrument.CHFJPY,
        Instrument.EURAUD, Instrument.EURCAD, Instrument.EURJPY, Instrument.EURNZD, Instrument.EURUSD, Instrument.GBPAUD, Instrument.GBPCAD,
        Instrument.GBPCHF, Instrument.GBPJPY, Instrument.GBPNZD, Instrument.GBPUSD, Instrument.NZDCHF, Instrument.NZDJPY, Instrument.NZDUSD,
        Instrument.USDCAD, Instrument.USDCHF, Instrument.USDJPY, Instrument.CADCHF, Instrument.NZDCAD, Instrument.EURCHF, Instrument.EURGBP};

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    private IIndicatorContext context;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final double[][] outputs = new double[currencyNames.length][];

    private int atrShift = 10;
    private int maFactor1 = 231;
    private int maFactor2 = 20;
    private boolean ignoreFuture = true;

    private final boolean[] useCurrency = new boolean[currencyNames.length];
    private final boolean[] useInstrument = new boolean[instruments.length];

    private IIndicator ma;
    private IIndicator atr;

    @Override
    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("CSS", "Currency Slope Strength", "", false, false, true, 1, 7 + currencyNames.length, currencyNames.length);
        indicatorInfo.setRecalculateAll(true);
        indicatorInfo.setRecalculateOnNewCandleOnly(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bars", InputParameterInfo.Type.BAR)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("ATR period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(100, 2, 2000, 1)),
            new OptInputParameterInfo("ATR shift", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(atrShift, 0, 2000, 1)),
            new OptInputParameterInfo("MA period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(21, 2, 2000, 1)),
            new OptInputParameterInfo("MA type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.LWMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("MA factor 1", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(maFactor1, 1, 2000, 1)),
            new OptInputParameterInfo("MA factor 2", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(maFactor2, 1, 2000, 1)),
            new OptInputParameterInfo("Ignore Future", OptInputParameterInfo.Type.OTHER, new BooleanOptInputDescription(ignoreFuture))
        };

        Arrays.fill(useCurrency, true);
        Arrays.fill(useInstrument, true);

        optInputParameterInfos = Arrays.copyOf(optInputParameterInfos, optInputParameterInfos.length + currencyNames.length);
        for (int currencyIdx = 0; currencyIdx < currencyNames.length; currencyIdx++) {
            optInputParameterInfos[optInputParameterInfos.length - currencyNames.length + currencyIdx] = new OptInputParameterInfo(
                    currencyNames[currencyIdx], OptInputParameterInfo.Type.OTHER, new BooleanOptInputDescription(true));
        }

        outputParameterInfos = new OutputParameterInfo[currencyNames.length];
        for (int currencyIdx = 0; currencyIdx < currencyNames.length; currencyIdx++) {
            outputParameterInfos[currencyIdx] = new OutputParameterInfo(
                    currencyNames[currencyIdx], OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE);
        }

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 0.2, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.OLIVE_DRAB, 1, 1),
            new LevelInfo("", -0.2, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        atr = indicatorsProvider.getIndicator("ATR");
        ma = indicatorsProvider.getIndicator("MA");
        ma.setOptInputParameter(1, IIndicators.MaType.LWMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (endIndex + getLookforward() >= inputs[0].length) {
            endIndex = inputs[0].length - 1 - getLookforward();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0, 0);
        }

        for (int i = 0; i < outputs.length; i++) {
            Arrays.fill(outputs[i], 0);
        }

        DataType dataType = context.getFeedDescriptor().getDataType();
        if (!dataType.equals(DataType.TIME_PERIOD_AGGREGATION)) {
            return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
        }

        try {
            Period period = context.getFeedDescriptor().getPeriod();
            OfferSide offerSide = context.getFeedDescriptor().getOfferSide();
            Filter filter = context.getFeedDescriptor().getFilter();
            if (filter == null) {
                filter = Filter.NO_FILTER;
            }
            long from = inputs[0][0].getTime();
            long to = inputs[0][inputs[0].length - 1].getTime();

            int[] currencyOccurrences = new int[currencyNames.length];
            Arrays.fill(currencyOccurrences, 0);

            for (int instrumentIdx = 0; instrumentIdx < instruments.length; instrumentIdx++) {
                if (!useInstrument[instrumentIdx]) {
                    continue;
                }

                int primaryCurrencyIdx = getCurrencyIdx(instruments[instrumentIdx].getPrimaryJFCurrency().getCurrencyCode());
                int secondaryCurrencyIdx = getCurrencyIdx(instruments[instrumentIdx].getSecondaryJFCurrency().getCurrencyCode());
                if (primaryCurrencyIdx < 0 && secondaryCurrencyIdx < 0) {
                    continue;
                }
                if (primaryCurrencyIdx >= 0 && !useCurrency[primaryCurrencyIdx] ||
                        secondaryCurrencyIdx >= 0 && !useCurrency[secondaryCurrencyIdx]) {
                    continue;
                }

                List<IBar> bars = null;
                try {
                    long currentBarTime = context.getHistory().getStartTimeOfCurrentBar(instruments[instrumentIdx], period);
                    long correctedTo = (to <= currentBarTime ? to : currentBarTime);
                    bars = context.getHistory().getBars(instruments[instrumentIdx], period, offerSide, filter, from, correctedTo);
                    correctedTo = (bars.size() > 0 ? bars.get(bars.size() - 1).getTime() : to);
                    while (correctedTo < to) {
                        correctedTo = context.getHistory().getNextBarStart(period, correctedTo);
                        bars.add(new FlatBar(correctedTo, bars.get(bars.size() - 1).getClose()));
                    }
                    if (bars.size() != inputs[0].length) {
                        continue;
                    }
                } catch (JFException ex) {
                    if (ex.getMessage().contains("is not subscribed")) {
                        context.getConsole().getWarn().println(ex.getMessage());
                        useInstrument[instrumentIdx] = false;
                        continue;
                    }
                    throw ex;
                }

                if (primaryCurrencyIdx >= 0) {
                    currencyOccurrences[primaryCurrencyIdx]++;
                }
                if (secondaryCurrencyIdx >= 0) {
                    currencyOccurrences[secondaryCurrencyIdx]++;
                }

                double[][] atrInput = new double[5][bars.size()];
                for (int i = 0; i < bars.size(); i++) {
                    atrInput[OPEN][i] = bars.get(i).getOpen();
                    atrInput[CLOSE][i] = bars.get(i).getClose();
                    atrInput[HIGH][i] = bars.get(i).getHigh();
                    atrInput[LOW][i] = bars.get(i).getLow();
                    atrInput[VOLUME][i] = bars.get(i).getVolume();
                }

                double[] atrOutput = new double[endIndex - startIndex + 1];
                atr.setInputParameter(0, atrInput);
                atr.setOutputParameter(0, atrOutput);
                atr.calculate(startIndex - atrShift, endIndex - atrShift);

                double[] tmaInput = new double[bars.size()];
                for (int i = 0; i < bars.size(); i++) {
                    tmaInput[i] = bars.get(i).getClose();
                }

                double[] tmaOutput = new double[endIndex - startIndex + 2];
                if (ignoreFuture) {
                    ma.setInputParameter(0, tmaInput);
                    ma.setOutputParameter(0, tmaOutput);
                    ma.calculate(startIndex - 1, endIndex);
                } else {
                    for (int i = startIndex - 1, j = 0; i <= endIndex; i++, j++) {
                        tmaOutput[j] = calcTma(tmaInput, i);
                    }
                }

                for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
                    double tma = tmaOutput[j + 1];
                    double prev = tmaOutput[j];
                    if (ignoreFuture) {
                        prev = (prev * maFactor1 + tmaInput[i] * maFactor2) / (maFactor1 + maFactor2);
                    }
                    double atr = atrOutput[j] / atrShift;
                    double slope = (tma - prev) / atr;

                    if (primaryCurrencyIdx >= 0) {
                        outputs[primaryCurrencyIdx][j] += slope;
                    }
                    if (secondaryCurrencyIdx >= 0) {
                        outputs[secondaryCurrencyIdx][j] -= slope;
                    }
                }
            }

            for (int currencyIdx = 0; currencyIdx < currencyNames.length; currencyIdx++) {
                if (currencyOccurrences[currencyIdx] == 0) {
                    continue;
                }
                for (int i = 0; i < outputs[currencyIdx].length; i++) {
                    outputs[currencyIdx][i] /= currencyOccurrences[currencyIdx];
                }
            }

        } catch (JFException ex) {
            context.getConsole().getErr().println(ex.toString());
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
    }

    private int getCurrencyIdx(String currencyName) {
        int currencyIdx = -1;
        for (int idx = 0; idx < currencyNames.length; idx++) {
            if (currencyNames[idx].equals(currencyName)) {
                currencyIdx = idx;
                break;
            }
        }
        return currencyIdx;
    }

    private double calcTma(double[] tmaInput, int idx) {
        int maLookback = ma.getLookback();
        double sum = tmaInput[idx] * (maLookback + 1);
        double sumw = maLookback + 1;

        for (int jnx = 1, knx = maLookback; jnx <= maLookback; jnx++, knx--) {
            if (idx - jnx >= 0) {
                sum += tmaInput[idx - jnx] * knx;
                sumw += knx;
            }

            if (idx + jnx < tmaInput.length) {
                sum += tmaInput[idx + jnx] * knx;
                sumw += knx;
            }
        }

        return (sum / sumw);
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
        inputs[index] = (IBar[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int atrPeriod = (Integer) value;
            atr.setOptInputParameter(0, atrPeriod);
            break;
        case 1:
            atrShift = (Integer) value;
            break;
        case 2:
            int maPeriod = (Integer) value;
            ma.setOptInputParameter(0, maPeriod);
            break;
        case 3:
            int maType = (Integer) value;
            ma.setOptInputParameter(1, maType);
            break;
        case 4:
            maFactor1 = (Integer) value;
            break;
        case 5:
            maFactor2 = (Integer) value;
            break;
        case 6:
            ignoreFuture = (Boolean) value;
            break;
        default:
            int delta = optInputParameterInfos.length - currencyNames.length;
            if (index - delta >= 0 && index - delta < currencyNames.length) {
                useCurrency[index - delta] = (Boolean) value;
            } else {
                throw new ArrayIndexOutOfBoundsException(index);
            }
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return Math.max(atr.getLookback() + atrShift, ma.getLookback() + 1);
    }

    @Override
    public int getLookforward() {
        return (ignoreFuture ? 0 : ma.getLookback());
    }


    private static class FlatBar implements IBar {
        private final long time;
        private final double price;

        public FlatBar(long time, double price) {
            this.time = time;
            this.price = price;
        }

        @Override
        public double getOpen() {
            return price;
        }

        @Override
        public double getClose() {
            return price;
        }

        @Override
        public double getLow() {
            return price;
        }

        @Override
        public double getHigh() {
            return price;
        }

        @Override
        public double getVolume() {
            return 0;
        }

        @Override
        public long getTime() {
            return time;
        }
    }
}
