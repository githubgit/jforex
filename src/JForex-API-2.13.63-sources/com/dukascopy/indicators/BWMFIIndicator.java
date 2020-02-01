/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IMinMax;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. 
 * @author S.Vishnyakov
 * Date: Nov 25, 2009
 */
public class BWMFIIndicator implements IIndicator, IMinMax {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[5][];

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("BWMFI", "Market Facilitation Index", "Bill Williams", false, false, false, 1, 0, 5);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("MFI Up, Volume Up", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("MFI Down, Volume Down", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("MFI Up, Volume Down", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("MFI Down, Volume Up", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.NONE)
        };

        outputParameterInfos[0].setColor(DefaultColors.FOREST_GREEN);
        outputParameterInfos[1].setColor(DefaultColors.DARK_RED);
        outputParameterInfos[2].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[3].setColor(DefaultColors.DARK_ORANGE);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
           return new IndicatorResult(0, 0);
        }
        
        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            double mfiNow = (inputs[0][2][i] - inputs[0][3][i])/inputs[0][4][i];
            double mfiPrev = (inputs[0][2][i - 1] - inputs[0][3][i - 1])/inputs[0][4][i - 1];
            boolean isVolumeUp = false;
            boolean isMFIUp = false;
            if (inputs[0][4][i - 1] < inputs[0][4][i]) {
                isVolumeUp = true;
            }
            if (mfiNow > mfiPrev) {
                isMFIUp = true;
            }
            if (isVolumeUp && isMFIUp) {
            	outputs[0][j] = mfiNow;
                outputs[1][j] = 0;
                outputs[2][j] = 0;
                outputs[3][j] = 0;
            } else if (!isVolumeUp && !isMFIUp) {
                outputs[0][j] = 0;
                outputs[1][j] = mfiNow;
                outputs[2][j] = 0;
                outputs[3][j] = 0;
            } else if (!isVolumeUp && isMFIUp) {
                outputs[0][j] = 0;
                outputs[1][j] = 0;
                outputs[2][j] = mfiNow;
                outputs[3][j] = 0;
            } else {
                outputs[0][j] = 0;
                outputs[1][j] = 0;
                outputs[2][j] = 0;
                outputs[3][j] = mfiNow;
            }
            outputs[4][j] = mfiNow;	//put all meaningful values in one output
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
        return 1;
    }

    public int getLookforward() {
        return 0;
    }

    //the getMinMax method was added here in order to avoid situations when output colors for lesser values are almost indistinguishable
    //since the colors of indicator outputs are important for the BWMFI 
    @Override
    public double[] getMinMax(int outputIdx, Object values, int firstVisibleValueIndex, int lastVisibleValueIndex) {    	
    	if (outputIdx != 4 || values == null) {	
    		return new double[]{0, 0};
    	}
		double[] vals = (double[]) values;
		//min value must be at least the specified percentage of the max value or greater, otherwise max value is ignored
		final double perc = 0.3; 
		
		double minVal = Double.NaN;
		for (int i = 0; i < vals.length; i++){
			double cur = vals[i];
			if (Double.isNaN(cur) || cur == 0){
				continue;
			}
			if(Double.isNaN(minVal) || cur < minVal){
				minVal = cur;
			}
		}
		double maxVal = minVal;
		for (int i = 0; i < vals.length; i++){
			double cur = vals[i];
			if (Double.isNaN(cur)){
				continue;
			}
			if (cur > maxVal && minVal / cur >= perc ){
				maxVal = cur;
			}
		}
		return new double[]{minVal, maxVal};
    }
}
