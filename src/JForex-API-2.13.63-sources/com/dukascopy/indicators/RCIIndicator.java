package com.dukascopy.indicators;

import java.util.Arrays;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class RCIIndicator implements IIndicator {
    private int timePeriod = 14;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[1][];

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("RCI", "Rank Correlation Index", "", false, false, false, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Input", InputParameterInfo.Type.DOUBLE)
        };
        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 2, 500, 1))
        };
        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("RCI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)    		
		};
    }			

    public IndicatorResult calculate(int startIndex, int endIndex) {
    	if (startIndex - getLookback() < 0) {
    		startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        int i, j, m, n;
        int[] timeRanks = new int[timePeriod];
        double[] valueRanks = new double[timePeriod];
        double[] prices = new double[timePeriod];
        double[] sortedPrices = new double[timePeriod];
        int[] occurrences = new int[timePeriod];
        
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {	        	        
        	System.arraycopy(inputs[0], i - timePeriod + 1, prices, 0, timePeriod);	                	
	        System.arraycopy(prices, 0, sortedPrices, 0, prices.length);
			Arrays.sort(sortedPrices);
	        									
			int counter;
			for (m = 0; m < sortedPrices.length; m++) {
				counter = 0;
				for (n = 0; n < sortedPrices.length; n++){
					if (sortedPrices[m] == sortedPrices[n]){
						counter++;
					}
				}			
				occurrences[m] = counter;
			}							        	                
	        for (n = 0; n < timePeriod; n++){
				timeRanks[n] = timeRanks.length - n;
				valueRanks[n] = identifyValueRank(prices[n], occurrences, sortedPrices);				
			}		            
	        double diffSum = 0;
	        for (m = 0; m < timePeriod; m++){
	        	diffSum += Math.pow(timeRanks[m] - valueRanks[m], 2);
	        }	                	
        	outputs[0][j] = (1 - 6 * diffSum / (timePeriod * (Math.pow(timePeriod, 2) - 1)) ) * 100;   	        		
        }
        
        return new IndicatorResult(startIndex, j);        
    }
    
    private double identifyValueRank(double value, int[] occurrences, double[] sortedPrices){    	
    	int index = -1;
		for (int j = 0; j < sortedPrices.length; j++){
			if (sortedPrices[j] == value){
				index = j;
				break;
			}				
		} 
		if (occurrences[index] == 1){
			return sortedPrices.length - index;
		}
		else{				
			int lastIndex = index + occurrences[index] - 1;
			return (((sortedPrices.length - index) + (sortedPrices.length - lastIndex )) / 2d) * occurrences[index] / occurrences[index];
		}
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