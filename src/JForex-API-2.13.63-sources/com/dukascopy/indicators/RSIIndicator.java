/*
 * Copyright 2011 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. 
 * @author anatoly.pokusayev
 *
 */
public class RSIIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private int timePeriod = 14;
        
    public void onStart(IIndicatorContext context) {    
        indicatorInfo = new IndicatorInfo("RSI", "Relative Strength Index", "Momentum Indicators", false, false, true, 1, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(14, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("out", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 30, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 70, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });
    }
    
    public IndicatorResult calculate(int startIndex, int endIndex) {        
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        int i, today = startIndex - getLookback(), outIdx = 0;                        
        double prevValue = inputs[0][today], prevGain = 0, prevLoss = 0, tempValue1, tempValue2;                
        today++;                       
        
        for (i = timePeriod; i > 0; i--) {
        	tempValue1 = inputs[0][today++];
        	tempValue2 = tempValue1 - prevValue;
        	prevValue  = tempValue1;
            if( tempValue2 < 0 ) prevLoss -= tempValue2;
            else prevGain += tempValue2;             
        }
        prevGain /= timePeriod;	
        prevLoss /= timePeriod;        
        
        if( today > startIndex){
           tempValue1 = prevGain + prevLoss;
           outputs[0][outIdx++] = tempValue1 == 0 ? 0 : 100 * (prevGain / tempValue1);
        }
        
        while(today <= endIndex){        	        	
        	tempValue1 = inputs[0][today++];
            tempValue2 = tempValue1 - prevValue;
            prevValue  = tempValue1;

            prevLoss *= (timePeriod - 1);
            prevGain *= (timePeriod - 1);
            
      	  	if( tempValue2 < 0 ) prevLoss -= tempValue2;
            else prevGain += tempValue2;

            prevLoss /= timePeriod;
            prevGain /= timePeriod;
            tempValue1 = prevLoss + prevGain;
            outputs[0][outIdx++] = tempValue1 == 0 ? 0 : 100 * (prevGain / tempValue1);        	
        }
        return new IndicatorResult(startIndex, outIdx);
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
        inputs[index] = (double[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return timePeriod;
    }

    public int getLookforward() {
        return 0;
    }
}
