package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IIndicators.MaType;
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

public class TradersDynamicIndex implements IIndicator {

    private static final int NUMBER_OF_INPUTS = 1;
    private static final int NUMBER_OF_OPTIONAL_INPUTS = 7;
    private static final int NUMBER_OF_OUPUTS = 5;

    private static final int RSI_PERIOD_IDX = 0;
    private static final int PRICE_PERIOD_IDX = 1;
    private static final int PRICE_MATYPE_IDX = 2;
    private static final int SIGNAL_PERIOD_IDX = 3;
    private static final int SIGNAL_MATYPE_IDX = 4;
    private static final int VOLATILITY_BAND_IDX = 5;
    private static final int VOLATILITY_MATYPE_IDX = 6;

    private static final int VB_HIGH = 0;
    private static final int VB_LOW = 1;
    private static final int MARKET_BASE_LINE = 2;    
    private static final int SIGNAL_LINE = 3;
    private static final int PRICE_LINE = 4;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[NUMBER_OF_INPUTS][];
    private final double[][] outputs = new double[NUMBER_OF_OUPUTS][];

    private IIndicator rsi;
    private IIndicator maRsi;
    private IIndicator maSignal;
    private IIndicator bbands;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TDI", "Traders Dynamic Index", "", false, false, true,
                NUMBER_OF_INPUTS, NUMBER_OF_OPTIONAL_INPUTS, NUMBER_OF_OUPUTS);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("RSI Period", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(13, 2, 2000, 1)),
            new OptInputParameterInfo("RSI Price Line", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(2, 1, 2000, 1)),
            new OptInputParameterInfo("Price MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Trade Signal Line", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(7, 1, 2000, 1)),
            new OptInputParameterInfo("Signal MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Volatility Band", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(34, 2, 2000, 1)),
            new OptInputParameterInfo("Volatility MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("VB High", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, false),
            new OutputParameterInfo("VB Low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, false),
            new OutputParameterInfo("Market Base Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, false),
            new OutputParameterInfo("Trade Signal Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, false),
            new OutputParameterInfo("RSI Price Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, false)
        };

        outputParameterInfos[VB_HIGH].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[VB_LOW].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[MARKET_BASE_LINE].setColor(DefaultColors.GOLD);
        outputParameterInfos[SIGNAL_LINE].setColor(DefaultColors.RED);
        outputParameterInfos[PRICE_LINE].setColor(DefaultColors.OLIVE_DRAB);

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 32, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 50, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 68, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        rsi = indicatorsProvider.getIndicator("RSI");
        maRsi = indicatorsProvider.getIndicator("MA");
        maRsi.setOptInputParameter(1, MaType.SMA.ordinal());
        maSignal = indicatorsProvider.getIndicator("MA");
        maSignal.setOptInputParameter(1, MaType.SMA.ordinal());
        bbands = indicatorsProvider.getIndicator("BBANDS");
        bbands.setOptInputParameter(1, 1.6185);
        bbands.setOptInputParameter(2, 1.6185);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {        
        if (startIndex < getLookback()) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {    
            return new IndicatorResult(0, 0);
        } 

        int maxMaLookback = getLookback() - rsi.getLookback();
        double[] rsiOutput = new double[endIndex - startIndex + 1 + maxMaLookback];

        rsi.setInputParameter(0, inputs[0]);
        rsi.setOutputParameter(0, rsiOutput);
        rsi.calculate(startIndex - maxMaLookback, endIndex);

        maRsi.setInputParameter(0, rsiOutput);
        maRsi.setOutputParameter(0, outputs[PRICE_LINE]);
        maRsi.calculate(maxMaLookback, rsiOutput.length - 1);

        maSignal.setInputParameter(0, rsiOutput);
        maSignal.setOutputParameter(0, outputs[SIGNAL_LINE]);
        maSignal.calculate(maxMaLookback, rsiOutput.length - 1);

        bbands.setInputParameter(0, rsiOutput);
        bbands.setOutputParameter(0, outputs[VB_HIGH]);
        bbands.setOutputParameter(1, outputs[MARKET_BASE_LINE]);
        bbands.setOutputParameter(2, outputs[VB_LOW]);
        bbands.calculate(maxMaLookback, rsiOutput.length - 1);

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
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
        inputs[index] = (double[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case RSI_PERIOD_IDX:
            rsi.setOptInputParameter(0, value);
            break;

        case PRICE_PERIOD_IDX:
            maRsi.setOptInputParameter(0, value);
            break;

        case PRICE_MATYPE_IDX:
            maRsi.setOptInputParameter(1, (Integer) value);
            break;

        case SIGNAL_PERIOD_IDX:
            maSignal.setOptInputParameter(0, value);
            break;

        case SIGNAL_MATYPE_IDX:
            maSignal.setOptInputParameter(1, (Integer) value);
            break;

        case VOLATILITY_BAND_IDX:
            bbands.setOptInputParameter(0, value);
            break;

        case VOLATILITY_MATYPE_IDX:
            bbands.setOptInputParameter(3, (Integer) value);
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return rsi.getLookback() + Math.max(maRsi.getLookback(), Math.max(maSignal.getLookback(), bbands.getLookback()));
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
