package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class ChandeVariableIndexDynamicAverage implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[1][];

    private int vidyaPeriod = 20;

    private IIndicator cmo;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("VIDYA", "Chande's Variable Index Dynamic Average", "", true, false, true, 1, 2, 1);
        indicatorInfo.setRecalculateAll(true);
        indicatorInfo.setRecalculateOnNewCandleOnly(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("CMO Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(9, 3, 2000, 1)),
            new OptInputParameterInfo("VIDYA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(vidyaPeriod, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        cmo = context.getIndicatorsProvider().getIndicator("CMO");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] cmoOutput = new double[endIndex - startIndex + 1];
        cmo.setInputParameter(0, inputs[0]);
        cmo.setOutputParameter(0, cmoOutput);
        cmo.calculate(startIndex, endIndex);

        double f = 2.0 / (vidyaPeriod + 1);
        outputs[0][0] = inputs[0][startIndex];

        for (int i = startIndex + 1, j = 1; i <= endIndex; i++, j++) {
            double cmo = Math.abs(cmoOutput[j]) / 100;
            outputs[0][j] = inputs[0][i] * f * cmo + outputs[0][j - 1] * (1 - f * cmo);
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
        switch (index) {
            case 0:
                int cmoPeriod = (Integer) value;
                cmo.setOptInputParameter(0, cmoPeriod);
                break;
            case 1:
                vidyaPeriod = (Integer) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return cmo.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
