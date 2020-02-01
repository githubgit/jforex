package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class VolatilityQualityIndex implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[3][];

    private IIndicator atr;
    private IIndicator smaShort;
    private IIndicator smaLong;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("VQI", "Volatility Quality Index", "Volatility Indicators", false, false, true, 1, 2, 3);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Short MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(9, 2, 2000, 1)),
            new OptInputParameterInfo("Long MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(200, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("VQI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("VQI Short MA", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("VQI Long MA", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[2].setColor(DefaultColors.YELLOW);

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        atr = indicatorsProvider.getIndicator("ATR");
        atr.setOptInputParameter(0, 1);
        smaShort = indicatorsProvider.getIndicator("SMA");
        smaLong = indicatorsProvider.getIndicator("SMA");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] atrOutput = new double[endIndex - startIndex + 1 + Math.max(smaShort.getLookback(), smaLong.getLookback())];
        atr.setInputParameter(0, inputs[0]);
        atr.setOutputParameter(0, atrOutput);
        atr.calculate(startIndex - Math.max(smaShort.getLookback(), smaLong.getLookback()), endIndex);

        double[] smaInput = new double[endIndex - startIndex + 1 + Math.max(smaShort.getLookback(), smaLong.getLookback())];
        double prevVqi = 0;

        for (int i = startIndex - Math.max(smaShort.getLookback(), smaLong.getLookback()), j = 0; i <= endIndex; i++, j++) {
            double vqi = ((atrOutput[j] != 0) && (inputs[0][HIGH][i] != inputs[0][LOW][i]) ?
                    (inputs[0][CLOSE][i] - inputs[0][CLOSE][i - 1] / atrOutput[j] +
                    (inputs[0][CLOSE][i] - inputs[0][OPEN][i]) / (inputs[0][HIGH][i] - inputs[0][LOW][i])) / 2 : prevVqi);

            smaInput[j] = (j > 0 ? smaInput[j - 1] : 0) +
                    Math.abs(vqi) * (2 * inputs[0][CLOSE][i] - inputs[0][CLOSE][i - 1] - inputs[0][OPEN][i]) / 2;

            prevVqi = vqi;
        }

        for (int i = Math.max(smaShort.getLookback(), smaLong.getLookback()), j = 0; i < smaInput.length; i++, j++) {
            outputs[0][j] = smaInput[i];
        }

        smaShort.setInputParameter(0, smaInput);
        smaShort.setOutputParameter(0, outputs[1]);
        smaShort.calculate(Math.max(smaShort.getLookback(), smaLong.getLookback()), smaInput.length - 1);

        smaLong.setInputParameter(0, smaInput);
        smaLong.setOutputParameter(0, outputs[2]);
        smaLong.calculate(Math.max(smaShort.getLookback(), smaLong.getLookback()), smaInput.length - 1);

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
    }

    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    public InputParameterInfo getInputParameterInfo(int index) {
        if (index < inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int shortPeriod = (Integer) value;
            smaShort.setOptInputParameter(0, shortPeriod);
            break;
        case 1:
            int longPeriod = (Integer) value;
            smaLong.setOptInputParameter(0, longPeriod);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return atr.getLookback() + Math.max(smaShort.getLookback(), smaLong.getLookback());
    }

    public int getLookforward() {
        return 0;
    }
}
