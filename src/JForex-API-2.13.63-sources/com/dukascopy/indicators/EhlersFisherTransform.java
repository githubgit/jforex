package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
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

public class EhlersFisherTransform implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[2][];

    private IIndicator max;
    private IIndicator min;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("EF_TRANSFORM", "Ehlers Fisher Transform", "", false, false, true, 1, 1, 2);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(10, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Fisher", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Signal Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
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

        double[] maxOutput = new double[endIndex - startIndex + 1];
        max.setInputParameter(0, inputs[0]);
        max.setOutputParameter(0, maxOutput);
        max.calculate(startIndex, endIndex);

        double[] minOutput = new double[endIndex - startIndex + 1];
        min.setInputParameter(0, inputs[0]);
        min.setOutputParameter(0, minOutput);
        min.calculate(startIndex, endIndex);

        double prevVal1 = 0;
        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            double val1 = 0.33 * 2 * ((inputs[0][i] - minOutput[j]) / (maxOutput[j] - minOutput[j]) - 0.5) + 0.67 * prevVal1;
            double val2 = (val1 > 0.99 ? 0.999 : (val1 < -0.99 ? -0.999 : val1));
            outputs[0][j] = 0.5 * Math.log((1 + val2) / (1 - val2)) + 0.5 * (j > 0 ? outputs[0][j - 1] : 0);
            outputs[1][j] = (j > 0 ? outputs[0][j - 1] : Double.NaN);
            prevVal1 = val1;
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
        inputs[index] = (double[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        int period = (Integer) value;
        max.setOptInputParameter(0, period);
        min.setOptInputParameter(0, period);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return max.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
