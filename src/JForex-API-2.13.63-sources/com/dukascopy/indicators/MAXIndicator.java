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
 * 
 * @author anatoly.pokusayev
 *
 */

public class MAXIndicator implements IIndicator{
	private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private int timePeriod = 30;
    private double[][] outputs = new double[1][];	    
    
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MAX", "Highest value over a specified period", "Math Operators", true, false, false, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)};
        optInputParameterInfos = new OptInputParameterInfo[] {
        		new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 1, 200, 1))};
        outputParameterInfos = new OutputParameterInfo[] {
        		new OutputParameterInfo("Out", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)	        			  	        			        		
        };	        	        
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {	    	
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }   
        int i, j;
        if (timePeriod == 1){        	
        	for (i = startIndex, j = 0; i <= endIndex; i++, j++){
        		outputs[0][j] = inputs[0][i];
        	}
        	return new IndicatorResult(startIndex, j);
        }
               
        int outIdx, nbInitialElementNeeded = getLookback();
        int trailingIdx, today, highestIdx;                     
        double highest, tmp;
              
        outIdx = 0;
        today = startIndex;
        trailingIdx = startIndex - nbInitialElementNeeded;
        highestIdx = -1;
        highest = 0.0;
        while(today <= endIndex){
           tmp = inputs[0][today];
           if(highestIdx < trailingIdx){
              highestIdx = trailingIdx;
              highest = inputs[0][highestIdx];
              i = highestIdx;
              while(++i <= today){
                 tmp = inputs[0][i];
                 if(tmp > highest){
                    highestIdx = i;
                    highest = tmp;
                 }
              }
           }
           else if(tmp >= highest){
              highestIdx = today;
              highest = tmp;
           }
           outputs[0][outIdx++] = highest;
           trailingIdx++;
           today++;
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

    public int getLookback() {
        return timePeriod - 1;
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
        inputs[index] = (double[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }	    
 }