package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class KaufmanEfficiencyRatioIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private int period;

    private IIndicator sum;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("KER", "Kaufman's Efficiency Ratio", "", false, false, false, 1, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 30, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", -30, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1)
        });

        sum = context.getIndicatorsProvider().getIndicator("SUM");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] sumInput = new double[endIndex - startIndex + 1 + sum.getLookback()];
        double[] sumOutput = new double[endIndex - startIndex + 1];
        for (int i = startIndex - sum.getLookback(), j = 0; i <= endIndex; i++, j++) {
            sumInput[j] = Math.abs(inputs[0][i] - inputs[0][i - 1]);
        }

        sum.setInputParameter(0, sumInput);
        sum.setOutputParameter(0, sumOutput);
        sum.calculate(sum.getLookback(), sumInput.length - 1);

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            outputs[0][j] = (inputs[0][i] - inputs[0][i - period]) / sumOutput[j] * 100;
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
        period = (Integer) value;
        sum.setOptInputParameter(0, period);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return period;
    }

    public int getLookforward() {
        return 0;
    }
}
