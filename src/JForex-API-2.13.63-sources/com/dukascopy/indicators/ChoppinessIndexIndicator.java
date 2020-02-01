package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class ChoppinessIndexIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[1][];

    private int period = 14;
    private double log10Period;

    private IIndicator atr;
    private IIndicator sum;
    private IIndicator max;
    private IIndicator min;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("CHOP_INDEX", "Choppiness Index", "", false, false, true, 1, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(period, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 61.8, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 38.2, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        atr = indicatorsProvider.getIndicator("ATR");
        sum = indicatorsProvider.getIndicator("SUM");
        max = indicatorsProvider.getIndicator("MAX");
        min = indicatorsProvider.getIndicator("MIN");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] atrOutput = new double[endIndex - startIndex + 1 + sum.getLookback()];
        atr.setInputParameter(0, inputs[0]);
        atr.setOutputParameter(0, atrOutput);
        atr.calculate(startIndex - sum.getLookback(), endIndex);

        double[] sumOutput = new double[endIndex - startIndex + 1];
        sum.setInputParameter(0, atrOutput);
        sum.setOutputParameter(0, sumOutput);
        sum.calculate(sum.getLookback(), atrOutput.length - 1);

        double[] maxOutput = new double[endIndex - startIndex + 1];
        max.setInputParameter(0, inputs[0][HIGH]);
        max.setOutputParameter(0, maxOutput);
        max.calculate(startIndex, endIndex);

        double[] minOutput = new double[endIndex - startIndex + 1];
        min.setInputParameter(0, inputs[0][LOW]);
        min.setOutputParameter(0, minOutput);
        min.calculate(startIndex, endIndex);

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
            outputs[0][i] = Math.log10(sumOutput[i] / (maxOutput[i] - minOutput[i])) / log10Period * 100;
        }

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
        period = (Integer) value;
        log10Period = Math.log10(period);
        atr.setOptInputParameter(0, 1);
        sum.setOptInputParameter(0, period);
        max.setOptInputParameter(0, period);
        min.setOptInputParameter(0, period);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return atr.getLookback() + sum.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
