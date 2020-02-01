package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class GannSwingOscillator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];

    private IIndicator sum;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("GANN_SWING", "Gann Swing Oscillator", "", false, false, true, 1, 0, 1);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        sum = context.getIndicatorsProvider().getIndicator("SUM");
        sum.setOptInputParameter(0, 2);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] sumInputs = new double[2][endIndex - startIndex + 1 + sum.getLookback()];
        double[][] sumOutputs = new double[2][endIndex - startIndex + 1];

        for (int i = startIndex - sum.getLookback(), j = 0; i <= endIndex; i++, j++) {
            sumInputs[0][j] = (inputs[0][HIGH][i] > inputs[0][HIGH][i - 1] ? 1 : 0);
            sumInputs[1][j] = (inputs[0][LOW][i] < inputs[0][LOW][i - 1] ? 1 : 0);
        }

        sum.setInputParameter(0, sumInputs[0]);
        sum.setOutputParameter(0, sumOutputs[0]);
        sum.calculate(sum.getLookback(), sumInputs[0].length - 1);

        sum.setInputParameter(0, sumInputs[1]);
        sum.setOutputParameter(0, sumOutputs[1]);
        sum.calculate(sum.getLookback(), sumInputs[1].length - 1);

        int[] Us = new int[endIndex - startIndex + 1];
        int[] Ds = new int[endIndex - startIndex + 1];
        double[] Hc = new double[endIndex - startIndex + 1];
        double[] Lc = new double[endIndex - startIndex + 1];
        int[] Sd1 = new int[endIndex - startIndex + 1];
        int[] Sd2 = new int[endIndex - startIndex + 1];
        int[] Sd1s = new int[endIndex - startIndex + 1];
        int[] Sd1sm = new int[endIndex - startIndex + 1];

        Us[0] = 0;
        Ds[0] = 0;
        Hc[0] = inputs[0][HIGH][startIndex];
        Lc[0] = inputs[0][LOW][startIndex];
        Sd1[0] = 0;
        Sd1s[0] = 0;
        Sd1sm[0] = 0;
        Sd2[0] = 0;
        outputs[0][0] = 0;

        for (int i = startIndex + 1, j = 1; i <= endIndex; i++, j++) {
            Us[j] = (sumOutputs[0][j] == 2 ? 0 : Us[j - 1] + 1);
            Ds[j] = (sumOutputs[1][j] == 2 ? 0 : Ds[j - 1] + 1);
            Hc[j] = ((Us[j] == 0) || (inputs[0][HIGH][i] > Hc[j - 1]) ? inputs[0][HIGH][i] : Hc[j - 1]);
            Lc[j] = ((Ds[j] == 0) || (inputs[0][LOW][i] < Lc[j - 1]) ? inputs[0][LOW][i] : Lc[j - 1]);
            Sd1[j] = (Us[j] == 0 ? ((inputs[0][LOW][i] != Lc[j]) && (inputs[0][LOW][i - 1] != Lc[j]) ? 1 : 0) :
                    (Ds[j] == 0 ? ((inputs[0][HIGH][i] != Hc[j]) && (inputs[0][HIGH][i - 1] != Hc[j]) ? -1 : 0) : 0));
            Sd1s[j] = (Sd1[j] == 1 ? 0 : Sd1s[j - 1] + 1);
            Sd1sm[j] = (Sd1[j] == -1 ? 0 : Sd1sm[j - 1] + 1);
            Sd2[j] = (Sd1[j] == 1 ? (Sd1s[j - 1] > Sd1sm[j - 1] ? 1 : 0) :
                    (Sd1[j] == -1 ? (Sd1s[j - 1] < Sd1sm[j - 1] ? -1 : 0) : 0));
            outputs[0][j] = (Sd2[j] != 0 ? Sd2[j] : outputs[0][j - 1]);
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
        return sum.getLookback() + 1;
    }

    public int getLookforward() {
        return 0;
    }
}
