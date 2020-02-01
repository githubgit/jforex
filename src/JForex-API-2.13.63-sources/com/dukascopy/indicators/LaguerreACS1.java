package com.dukascopy.indicators;

import com.dukascopy.api.indicators.*;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * @author Sergey Vishnyakov
 *         Date:    Sep 3, 2010
 *         Time:    4:35:00 PM
 *         Copyright (c) 2010 Dukascopy Bank SA
 */
public class LaguerreACS1 implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private int MA = 2;
    private double gamma = 0;
    private double[][] outputs = new double[1][];
    private double[] arrayVariables;
    private int lookback = 1000;


    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("LAGACS1", "Laguerre-ACS1", "Momentum Indicators", false, false, true, 1, 3, 1);
        inputParameterInfos = new InputParameterInfo[]{new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE)};
        optInputParameterInfos = new OptInputParameterInfo[]{new OptInputParameterInfo("MA", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(2, 0, 10000, 1)), new OptInputParameterInfo("gamma", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(0.6, 0, 100, 0.01, 2)), new OptInputParameterInfo("lookback", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(1000, 0, 10000, 100)),};
        outputParameterInfos = new OutputParameterInfo[]{new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)};

    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double L0 = 0.0;
        double L1 = 0.0;
        double L2 = 0.0;
        double L3 = 0.0;
        double L0A = 0.0;
        double L1A = 0.0;
        double L2A = 0.0;
        double L3A = 0.0;
        double LRSI = 0.0;
        double CU = 0.0;
        double CD = 0.0;
        double summ = 0;

        int i, j;

        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            summ = 0;
            L0A = L0;
            L1A = L1;
            L2A = L2;
            L3A = L3;
            L0 = (1 - gamma) * inputs[0][i] + gamma * L0A;
            L1 = (-gamma) * L0 + L0A + gamma * L1A;
            L2 = (-gamma) * L1 + L1A + gamma * L2A;
            L3 = (-gamma) * L2 + L2A + gamma * L3A;
            CU = 0;
            CD = 0;

            if (L0 >= L1) CU = L0 - L1;
            else CD = L1 - L0;
            if (L1 >= L2) CU = CU + L1 - L2;
            else CD = CD + L2 - L1;
            if (L2 >= L3) CU = CU + L2 - L3;
            else CD = CD + L3 - L2;
            if (CU + CD != 0.0) LRSI = CU / (CU + CD);
            arrayVariables[j] = LRSI;
            if (MA < 2) outputs[0][j] = arrayVariables[j];
            else {
                if (j > (MA - 1)) {
                    for (int z = j - (MA - 1); z <= j; z++) summ += arrayVariables[z];
                }
                outputs[0][j] = summ / MA;
            }
        }
        return new IndicatorResult(startIndex, j);
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
        return lookback;
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
        int length = inputs[index].length;
        arrayVariables = new double[length];
    }

    public void setOptInputParameter(int index, Object value) {
        if (index == 0) MA = (Integer) value;
        if (index == 1) gamma = (Double) value;
        if (index == 2) lookback = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}
