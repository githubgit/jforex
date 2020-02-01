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
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * @author Dmitry Shohov
 */
public class TD_IIndicator implements IIndicator {
    private IIndicator smaIndicator;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[3][];
    
    public void onStart(IIndicatorContext context) {
        smaIndicator = context.getIndicatorsProvider().getIndicator("SMA");

        indicatorInfo = new IndicatorInfo("TD_I", "Tom DeMark Indicator", "Momentum Indicators", false, false, false, 1, 1, 3);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(14, 2, 200, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Low Risk", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("High Risk", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
		};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        double[] dmax;
        double[] dmin;
        if (outputs[1].length >= endIndex - startIndex + 1 + smaIndicator.getLookback()) {
            //no need to allocate one more array
            dmax = outputs[1];
        } else {
            dmax = new double[endIndex - startIndex + 1 + smaIndicator.getLookback()];
        }
        if (outputs[2].length >= endIndex - startIndex + 1 + smaIndicator.getLookback()) {
            //no need to allocate one more array
            dmin = outputs[2];
        } else {
            dmin = new double[endIndex - startIndex + 1 + smaIndicator.getLookback()];
        }
        for (int i = startIndex - smaIndicator.getLookback(), j = 0; i <= endIndex; i++, j++) {
            if (inputs[0][2][i] > inputs[0][2][i - 1]) {
                dmax[j] = inputs[0][2][i] - inputs[0][2][i - 1];
            } else {
                dmax[j] = 0;
            }
            if (inputs[0][3][i] < inputs[0][3][i - 1]) {
                dmin[j] = inputs[0][3][i - 1] - inputs[0][3][i];
            } else {
                dmin[j] = 0;
            }
        }
        
        smaIndicator.setInputParameter(0, dmax);
        smaIndicator.setOutputParameter(0, outputs[0]);
        IndicatorResult dMaxSmaResult = smaIndicator.calculate(0, dmax.length - 1);
        
        smaIndicator.setInputParameter(0, dmin);
        smaIndicator.setOutputParameter(0, dmax);
        smaIndicator.calculate(0, dmin.length - 1);
        
        for (int i = 0, k = dMaxSmaResult.getNumberOfElements() - 1; i <= k; i++) {
            outputs[0][i] = outputs[0][i] / (outputs[0][i] + dmax[i]);
            outputs[1][i] = 0.7d;
            outputs[2][i] = 0.3d;
        }
        
        return new IndicatorResult(startIndex, dMaxSmaResult.getNumberOfElements());
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
        return smaIndicator.getLookback() + 1;
    }
    
    public int getLookforward() {
        return 0;
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
        if (index == 0) {
            smaIndicator.setOptInputParameter(0, value);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}