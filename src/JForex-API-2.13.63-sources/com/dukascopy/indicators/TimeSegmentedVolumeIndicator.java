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
 * Date: Aug 27, 2009
 * Time: 1:22:08 PM
 */
public class TimeSegmentedVolumeIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];
    private int timePeriod = 18;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TVS", "Time Segmented Volume", "Volume Indicators", false, false, false, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[] {
        		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(18, 1, 100, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("TVS", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
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

        //multPrice
        int resIndex = 0;

        for (int z = startIndex; z <= endIndex; z++, resIndex++) {
            double sum = 0;
            for (int i = resIndex; i < (resIndex + getLookback()); i++) {
                double tmp = 0;
                if (i != resIndex) {
                    //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
                    if  (inputs[0][1][i] > inputs[0][1][i - 1]) {
                        tmp = inputs[0][4][i] * (inputs[0][1][i] - inputs[0][1][i - 1]);
                    } else if (inputs[0][1][i] < inputs[0][1][i - 1]) {
                        tmp = (-1) * inputs[0][4][i] * (-inputs[0][1][i] + inputs[0][1][i - 1]);
                    }   else {
                        tmp = 0;
                    }
                }
                sum = sum + tmp;
            }
            outputs[0][resIndex] = sum;
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
        inputs[index] = (double[][]) array;
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