/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Jan 21, 2009
 * Time: 10:54:21 AM
 */
public class WaddahAttarTrend implements IIndicator {
    // MACD
    private IIndicator macdIndicator;
    // BBolinger
    private IIndicator bollingerIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[2][];

    // Pips Value
    private double pipsValue = 0.0001;

    public void onStart(IIndicatorContext context) {
        macdIndicator = context.getIndicatorsProvider().getIndicator("MACD");
        macdIndicator.setOptInputParameter(0, 20);
        macdIndicator.setOptInputParameter(1, 40);
        macdIndicator.setOptInputParameter(2, 9);

        bollingerIndicator = context.getIndicatorsProvider().getIndicator("BBANDS");
        bollingerIndicator.setOptInputParameter(0, 5);
        bollingerIndicator.setOptInputParameter(1, 2.00d);
        bollingerIndicator.setOptInputParameter(2, 2.00d);
        bollingerIndicator.setOptInputParameter(3, 0);

        indicatorInfo = new IndicatorInfo("WaddahAT", "Waddah Attar Trend", "", false, false, true, 1, 0, 2);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Zero", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] macdOutput, macdOutput2, macdOutput3, macdOutput4, macdOutput5;
        macdOutput = new double[endIndex - startIndex + 2];
        macdOutput2 = new double[endIndex - startIndex + 2];
        macdOutput3 = new double[endIndex - startIndex + 2];
        macdOutput4 = new double[endIndex - startIndex + 2];
        macdOutput5 = new double[endIndex - startIndex + 2];

        macdIndicator.setInputParameter(0, inputs[0][1]);
        macdIndicator.setOutputParameter(0, macdOutput);
        macdIndicator.setOutputParameter(1, macdOutput2);
        macdIndicator.setOutputParameter(2, macdOutput3);
        macdIndicator.setOutputParameter(3, macdOutput4);
        macdIndicator.setOutputParameter(4, macdOutput5);
        IndicatorResult dMACDResult = macdIndicator.calculate(startIndex - 1, endIndex);

        double[] bollOutput, bollOutput2, bollOutput3;
        bollOutput = new double[endIndex - startIndex + 2];
        bollOutput2 = new double[endIndex - startIndex + 2];
        bollOutput3 = new double[endIndex - startIndex + 2];

        bollingerIndicator.setInputParameter(0, inputs[0][1]);
        bollingerIndicator.setOutputParameter(0, bollOutput3);
        bollingerIndicator.setOutputParameter(1, bollOutput);
        bollingerIndicator.setOutputParameter(2, bollOutput2);
        IndicatorResult dBBANDSResult = bollingerIndicator.calculate(startIndex - 1, endIndex);

        if (dMACDResult.getFirstValueIndex() != dBBANDSResult.getFirstValueIndex() ||
                dBBANDSResult.getNumberOfElements() != dMACDResult.getNumberOfElements()) {
            throw new RuntimeException("Something wrong in ma calculation");
        }

        double trend, explo, value;
        int i, k;
        for (i = 1, k = dBBANDSResult.getNumberOfElements(); i < k; i++) {
            trend = (macdOutput[i] - macdOutput[i - 1]) / pipsValue;
            explo = (bollOutput3[i] - bollOutput2[i]) / pipsValue;
            value = trend * explo;
            outputs[0][i - 1] = Math.abs(value) < 0.000001d ? Double.NaN : value;
            outputs[1][i - 1] = 0;
        }

        return new IndicatorResult(startIndex, i - 1);
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
        return Math.max(macdIndicator.getLookback(), bollingerIndicator.getLookback()) + 1;
    }

    public int getLookforward() {
        return 0;
    }
}
