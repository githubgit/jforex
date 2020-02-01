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
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. 
 * 
 * Created by: S.Vishnyakov
 * Date: Jul 24, 2009
 * Time: 10:42:30 AM
 */
public class ButterworthFilterIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[1][];
    private int timePeriod = 34;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("Butterworth", "Butterworth Filter", "Overlap Studies", true, false, true, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
		};
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(34, 3, 100, 1))
		};        
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Butterworth", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
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

        int resIndex = 0;
        double a = 0;
        double b = 0;
        double c = 0;
        double result = 0;
        for (int i = startIndex; i <= endIndex; i++, resIndex++) {
            a = Math.exp(-Math.PI / timePeriod);
            b = 2 * a * Math.cos(1.738 * Math.PI / timePeriod);
            c = Math.exp(-2 * Math.PI / timePeriod);
            result =((1 - b + c) * (1 - c) / 8) * (inputs[0][i] + 3 * inputs[0][i - 1] + 3 * inputs[0][i - 2] +
                    inputs[0][i - 3]);
            if (resIndex == 0) {

            } else if (resIndex == 1) {
                result = result + (b + c) * outputs[0][resIndex - 1];
            } else if (resIndex == 2) {
                result = result + (b + c) * outputs[0][resIndex - 1] - (c + b * c) * outputs[0][resIndex - 2];
            } else if (resIndex >= 3) {
                result = result + (b + c) * outputs[0][resIndex - 1] - (c + b * c) * outputs[0][resIndex - 2] +
                        c * c * outputs[0][resIndex - 3];
            }
            outputs[0][resIndex] = result;
        }
        return new IndicatorResult(startIndex, resIndex);
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
        return timePeriod;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[0] = (double[]) array;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
    	if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public void setOptInputParameter(int index, Object value) {
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookforward() {
        return 0;
    }
}