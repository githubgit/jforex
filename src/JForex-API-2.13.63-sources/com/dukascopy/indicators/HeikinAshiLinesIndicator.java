package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class HeikinAshiLinesIndicator implements IIndicator {
    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[4][];
    private double[][] outputs = new double[4][];

    public void onStart(IIndicatorContext context) {
        inputParameterInfos = new InputParameterInfo[]{
                new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };
        outputParameterInfos = new OutputParameterInfo[]{
                new OutputParameterInfo("HA open", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
                new OutputParameterInfo("HA close", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
                new OutputParameterInfo("HA high", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
                new OutputParameterInfo("HA low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
        };
        indicatorInfo = new IndicatorInfo("HeikinAshiLines", "Heikin Ashi Lines", "", true, false, true, inputParameterInfos.length, 0, outputParameterInfos.length);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        double open = (inputs[OPEN][startIndex - 1] + inputs[CLOSE][startIndex - 1]) / 2;
        double close = (inputs[OPEN][startIndex - 1] + inputs[CLOSE][startIndex - 1] + inputs[HIGH][startIndex - 1] + inputs[LOW][startIndex - 1]) / 4;
        int resIndex = 0;

        for (int i = startIndex; i <= endIndex; i++, resIndex++) {
            open = (open + close) / 2;
            close = (inputs[OPEN][i] + inputs[CLOSE][i] + inputs[HIGH][i] + inputs[LOW][i]) / 4;

            double hi = max(open, close, inputs[HIGH][i]);
            double min = min(open, close, inputs[LOW][i]);

            outputs[OPEN][resIndex] = open;
            outputs[CLOSE][resIndex] = close;
            outputs[HIGH][resIndex] = hi;
            outputs[LOW][resIndex]  = min;
        }
        return new IndicatorResult(startIndex, resIndex);
    }

    public double max(double x, double y, double z) {
        double max;
        if(x > y) {
            max = x;
        } else {
            max = y;
        }
        if(z > max) {
            max = z;
        }
        return max;
    }

    public double min(double x, double y, double z) {
        double min;
        if(x > y) {
            min = y;
        } else {
            min = x;
        }
        if(z < min) {
            min = z;
        }
        return min;
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

    public int getLookback() {
        return 1;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs = (double[][]) array;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        return null;
    }

    public void setOptInputParameter(int index, Object value) {

    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}