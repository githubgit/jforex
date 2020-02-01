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
 * Date: Feb 12, 2009
 * Time: 2:08:35 PM
 */
public class SupportResistanceIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[2][];
    private int barsOnSides = 2;

    // Fractals
    private IIndicator fractalsIndicator;

    public void onStart(IIndicatorContext context) {
        fractalsIndicator = context.getIndicatorsProvider().getIndicator("FRACTAL");
        fractalsIndicator.setOptInputParameter(0, barsOnSides);

        indicatorInfo = new IndicatorInfo("S&R", "Support and Resistance", "", true, false, true, 1, 0, 2);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Maximums", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DOTS),
            new OutputParameterInfo("Minimums", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DOTS)
		};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        //calculating endIndex taking into account lookforward value
        if (endIndex + getLookforward() >= inputs[0][0].length) {
            endIndex = inputs[0][0].length - 1 - getLookforward();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] fractalsOutput;

        fractalsOutput = new double[2][endIndex - startIndex + fractalsIndicator.getLookforward() + fractalsIndicator.getLookback()];

        fractalsIndicator.setInputParameter(0, inputs[0]);
        fractalsIndicator.setOutputParameter(0, fractalsOutput[0]);
        fractalsIndicator.setOutputParameter(1, fractalsOutput[1]);
        IndicatorResult dMinSmaResult = fractalsIndicator.calculate(startIndex - 1, endIndex);

        double value1, value2;
        int i, j, k;
        for (i = 1, j = startIndex, k = dMinSmaResult.getNumberOfElements(); i < k; i++, j++) {
            value1 = fractalsOutput[0][i];
            value2 = fractalsOutput[1][i];
            if ((i == 1) || (value1 > 0)) {
                //  open, close, high, low, volume
                outputs[0][i - 1] = inputs[0][2][j];
            } else {
                outputs[0][i - 1] = outputs[0][i - 2];
            }
            if ((i == 1) || (value2 > 0)) {
                outputs[1][i - 1] = inputs[0][3][j];
            } else {
                outputs[1][i - 1] = outputs[1][i - 2];
            }
        }

        return new IndicatorResult(startIndex, i - 1, endIndex);
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
         return (fractalsIndicator.getLookback() + 1);
    }

    public int getLookforward() {
        return fractalsIndicator.getLookforward();
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

    public void setOptInputParameter(int index, Object value) {}

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
