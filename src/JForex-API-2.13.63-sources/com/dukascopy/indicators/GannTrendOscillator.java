package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class GannTrendOscillator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];

    private IIndicator swing;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("GANN_TREND", "Gann Trend Oscillator", "", false, false, true, 1, 0, 1);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        swing = context.getIndicatorsProvider().getIndicator("GANN_SWING");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        // Swing Direction
        double[] Sd = new double[endIndex - startIndex + 1];
        swing.setInputParameter(0, inputs[0]);
        swing.setOutputParameter(0, Sd);
        swing.calculate(startIndex, endIndex);

        int[] Sch = new int[endIndex - startIndex + 1];
        int[] Scl = new int[endIndex - startIndex + 1];
        double[] Hc = new double[endIndex - startIndex + 1];
        double[] Lc = new double[endIndex - startIndex + 1];
        double[] Pv = new double[endIndex - startIndex + 1];
        double[] Tv = new double[endIndex - startIndex + 1];

        Sch[0] = 0;
        Scl[0] = 0;
        Hc[0] = inputs[0][HIGH][startIndex];
        Lc[0] = inputs[0][LOW][startIndex];
        Pv[0] = 0;
        Tv[0] = 0;
        outputs[0][0] = 0;

        for (int i = startIndex + 1, j = 1; i <= endIndex; i++, j++) {
            // Swing Change High
            Sch[j] = ((Sd[j] == 1) && (Sd[j - 1] == -1) ? 1 : 0);
            Hc[j] = ((Sch[j] == 1) || (inputs[0][HIGH][i] > Hc[j - 1]) ? inputs[0][HIGH][i] : Hc[j - 1]);
            // Swing Change Low
            Scl[j] = ((Sd[j] == -1) && (Sd[j - 1] == 1) ? 1 : 0);
            Lc[j] = ((Scl[j] == 1) || (inputs[0][LOW][i] < Lc[j - 1]) ? inputs[0][LOW][i] : Lc[j - 1]);
            // Peak Value
            Pv[j] = (Scl[j] == 1 ? Hc[j] : Pv[j - 1]);
            // Trough Value
            Tv[j] = (Sch[j] == 1 ? Lc[j] : Tv[j - 1]);
            // Trend Direction
            outputs[0][j] = (inputs[0][HIGH][i] > Pv[j] ? 1 : (inputs[0][LOW][i] < Tv[j] ? -1 : outputs[0][j - 1]));
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
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return swing.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
