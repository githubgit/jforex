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

public class DarvasBoxIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[2][];

    private IIndicator max;
    private IIndicator min;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("DARVAS_BOX", "Darvas Box", "", true, false, true, 1, 1, 2);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(100, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Top", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Bottom", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[1].setColor(DefaultColors.RED);

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
        max.setInputParameter(0, inputs[0][HIGH]);
        max.setOutputParameter(0, maxOutput);
        max.calculate(startIndex - 4, endIndex - 4);

        double[] minOutput = new double[endIndex - startIndex + 1];
        min.setInputParameter(0, inputs[0][LOW]);
        min.setOutputParameter(0, minOutput);
        min.calculate(startIndex, endIndex);

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            if (inputs[0][HIGH][i - 3] >= maxOutput[j] &&
                    inputs[0][HIGH][i - 2] < inputs[0][HIGH][i - 3] &&
                    inputs[0][HIGH][i - 1] < inputs[0][HIGH][i - 3] &&
                    inputs[0][HIGH][i] < inputs[0][HIGH][i - 3]) {

                outputs[0][j] = inputs[0][HIGH][i - 3];
                outputs[1][j] = minOutput[j];

            } else {
                outputs[0][j] = (j > 0 ? outputs[0][j - 1] : Double.NaN);
                outputs[1][j] = (j > 0 ? outputs[1][j - 1] : Double.NaN);
            }
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
        int period = (Integer) value;
        max.setOptInputParameter(0, period);
        min.setOptInputParameter(0, 4);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return max.getLookback() + 4;
    }

    public int getLookforward() {
        return 0;
    }
}
