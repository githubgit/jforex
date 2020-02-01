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

public class TrendTriggerFactor implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[1][];

    private int period = 15;

    private IIndicator max;
    private IIndicator min;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TTF", "Trend Trigger Factor", "", false, false, false, 1, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(period, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 100, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", -100, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
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

        double[] maxOutput = new double[endIndex - startIndex + 1 + period];
        max.setInputParameter(0, inputs[0][HIGH]);
        max.setOutputParameter(0, maxOutput);
        max.calculate(startIndex - period, endIndex);

        double[] minOutput = new double[endIndex - startIndex + 1 + period];
        min.setInputParameter(0, inputs[0][LOW]);
        min.setOutputParameter(0, minOutput);
        min.calculate(startIndex - period, endIndex);

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
            double bp = maxOutput[i + period] - minOutput[i];
            double sp = maxOutput[i] - minOutput[i + period];
            outputs[0][i] = 100 * 2 * (bp - sp) / (bp + sp);
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
        max.setOptInputParameter(0, period);
        min.setOptInputParameter(0, period);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return max.getLookback() + period;
    }

    public int getLookforward() {
        return 0;
    }
}
