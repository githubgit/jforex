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

public class RSquaredIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[2][];

    private int n = 14;

    private IIndicator sum;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("R2", "R-Squared", "", false, false, false, 1, 1, 2);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(n, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("R2", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Critical Value - 95%", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.GRAY);
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

        double[][] sumInputs = new double[5][endIndex - startIndex + 1 + getLookback()];
        double[][] sumOutputs = new double[5][endIndex - startIndex + 1];

        for (int i = startIndex - getLookback(), j = 0; i <= endIndex; i++, j++) {
            sumInputs[0][j] = j + 1;
            sumInputs[1][j] = Math.pow(sumInputs[0][j], 2);
            sumInputs[2][j] = inputs[0][i];
            sumInputs[3][j] = Math.pow(sumInputs[2][j], 2);
            sumInputs[4][j] = sumInputs[0][j] * sumInputs[2][j];
        }

        for (int i = 0; i < 5; i++) {
            sum.setInputParameter(0, sumInputs[i]);
            sum.setOutputParameter(0, sumOutputs[i]);
            sum.calculate(getLookback(), sumInputs[i].length - 1);
        }

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
            double a = n * sumOutputs[1][i] - Math.pow(sumOutputs[0][i], 2);
            double b = n * sumOutputs[3][i] - Math.pow(sumOutputs[2][i], 2);
            double r = (n * sumOutputs[4][i] - sumOutputs[0][i] * sumOutputs[2][i]) / (Math.sqrt(a) * Math.sqrt(b));

            outputs[0][i] = Math.pow(r, 2) * 100;
            outputs[1][i] = (n == 5 ? 77 : n == 10 ? 40 : n == 14 ? 27 : n == 20 ? 20 : n == 25 ? 16 :
                    n == 30 ? 13 : n == 50 ? 8 : n == 60 ? 120 : n == 120 ? 3 : Double.NaN);
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
        n = (Integer) value;
        sum.setOptInputParameter(0, n);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return sum.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
