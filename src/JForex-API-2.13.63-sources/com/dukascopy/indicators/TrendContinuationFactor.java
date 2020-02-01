package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class TrendContinuationFactor implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[2][];

    private IIndicator sum;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TCF", "Trend Continuation Factor", "", false, false, true, 1, 1, 2);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(35, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("PlusTCF", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("MinusTCF", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[1].setColor(DefaultColors.RED);

        sum = context.getIndicatorsProvider().getIndicator("SUM");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] sumInputs = new double[4][endIndex - startIndex + 1 + sum.getLookback()];
        double[][] sumOutputs = new double[4][endIndex - startIndex + 1];

        for (int i = startIndex - sum.getLookback(), j = 0; i <= endIndex; i++, j++) {
            sumInputs[0][j] = (inputs[0][i] > inputs[0][i - 1] ? inputs[0][i] - inputs[0][i - 1] : 0);
            sumInputs[1][j] = (inputs[0][i] < inputs[0][i - 1] ? inputs[0][i - 1] - inputs[0][i] : 0);
            sumInputs[2][j] = (sumInputs[0][j] != 0 ? (j > 0 ? sumInputs[2][j - 1] : 0) + sumInputs[0][j] : 0);
            sumInputs[3][j] = (sumInputs[1][j] != 0 ? (j > 0 ? sumInputs[3][j - 1] : 0) + sumInputs[1][j] : 0);
        }

        for (int i = 0; i < 4; i++) {
            sum.setInputParameter(0, sumInputs[i]);
            sum.setOutputParameter(0, sumOutputs[i]);
            sum.calculate(sum.getLookback(), sumInputs[i].length - 1);
        }

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
            outputs[0][i] = sumOutputs[0][i] - sumOutputs[3][i];
            outputs[1][i] = sumOutputs[1][i] - sumOutputs[2][i];
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
        sum.setOptInputParameter(0, period);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return sum.getLookback() + 1;
    }

    public int getLookforward() {
        return 0;
    }
}
