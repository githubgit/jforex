package com.dukascopy.indicators;

import com.dukascopy.api.*;
import com.dukascopy.api.indicators.*;

public class HeikinAshiSmoothLines implements IIndicator {
    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;

    private IndicatorInfo indicatorInfo;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] outputs = new double[4][];
    private IIndicator ha;

    public void onStart(IIndicatorContext context) {
        ha = context.getIndicatorsProvider().getIndicator("HeikinAshiSmooth");

        outputParameterInfos = new OutputParameterInfo[]{
            new OutputParameterInfo("HA open",  OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("HA close", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("HA high",  OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("HA low",   OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
        };

        indicatorInfo = new IndicatorInfo("HeikinAshiSmLines", "Heikin Ashi Smoothed Lines", "", true, false, true,
            ha.getIndicatorInfo().getNumberOfInputs(), ha.getIndicatorInfo().getNumberOfOptionalInputs(), 4);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        IBar[] haOutput = new IBar[outputs[0].length];
        ha.setOutputParameter(0, haOutput);

        IndicatorResult res = ha.calculate(startIndex, endIndex);

        for (int i = 0; i < res.getNumberOfElements(); i++) {
            outputs[OPEN][i] = haOutput[i].getOpen();
            outputs[CLOSE][i] = haOutput[i].getClose();
            outputs[HIGH][i] = haOutput[i].getHigh();
            outputs[LOW][i] = haOutput[i].getLow();
        }

        return res;
    }

    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    public InputParameterInfo getInputParameterInfo(int index) {
        return ha.getInputParameterInfo(index);
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        return ha.getOptInputParameterInfo(index);
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public int getLookback() {
        return ha.getLookback();
    }

    public int getLookforward() {
        return ha.getLookforward();
    }

    public void setInputParameter(int index, Object array) {
        ha.setInputParameter(index, array);
    }

    public void setOptInputParameter(int index, Object value) {
        ha.setOptInputParameter(index, value);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
