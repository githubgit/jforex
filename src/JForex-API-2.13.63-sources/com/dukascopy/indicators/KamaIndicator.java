package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class KamaIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];    
    private double[][] outputs = new double[1][];
    private int timePeriod = 30;
    private int fastMAperiod = 2;
    private int slowMAperiod = 30;
    
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("KAMA", "Kaufman Adaptive Moving Average", "Overlap Studies", true, false, true, 1, 3, 1);
        indicatorInfo.setRecalculateAll(true);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE)};
        optInputParameterInfos = new OptInputParameterInfo[] {
        		new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 1, 500, 1)),
        		new OptInputParameterInfo("Fast MA period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastMAperiod, 1, 500, 1)),
        		new OptInputParameterInfo("Slow MA period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowMAperiod, 1, 500, 1))
        };
        outputParameterInfos = new OutputParameterInfo[] {new OutputParameterInfo("Output", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
    	if (startIndex - getLookback() < 0) {
    		startIndex = getLookback();
    	}
    	if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        final double constMax = 2.0/(slowMAperiod + 1.0);
        final double constDiff = 2.0/(fastMAperiod + 1.0) - constMax;
        
        double sumROC1 = 0.0;
        int today = startIndex - getLookback();
        int trailingIdx = today;
        double tempReal = 0, tempReal2 = 0;
        int i = timePeriod;
        while(i-- > 0 )
        {      
           tempReal  = inputs[0][today++];
           tempReal -= inputs[0][today];
           sumROC1  += Math.abs(tempReal);
        }

        double prevKAMA = inputs[0][today-1];

        tempReal  = inputs[0][today];
        tempReal2 = inputs[0][trailingIdx++];
        double periodROC = tempReal-tempReal2;

        double trailingValue = tempReal2;

        if(sumROC1 <= periodROC || sumROC1 == 0)
           tempReal = 1.0;
        else
           tempReal = Math.abs(periodROC/sumROC1);

        tempReal  = (tempReal*constDiff)+constMax;
        tempReal *= tempReal;

        prevKAMA = ((inputs[0][today++]-prevKAMA)*tempReal) + prevKAMA;
        outputs[0][0] = prevKAMA;

        int outIdx = 1;
        int outBegIdx = today-1;

        while( today <= endIndex )
        {
           tempReal  = inputs[0][today];
           tempReal2 = inputs[0][trailingIdx++];
           periodROC = tempReal-tempReal2;

           sumROC1 -= Math.abs(trailingValue-tempReal2);
           sumROC1 += Math.abs(tempReal-inputs[0][today-1]);

           trailingValue = tempReal2;

           if( (sumROC1 <= periodROC) || sumROC1 == 0)
              tempReal = 1.0;
           else
              tempReal = Math.abs(periodROC / sumROC1);

           tempReal  = (tempReal*constDiff)+constMax;
           tempReal *= tempReal;

           prevKAMA = ((inputs[0][today++]-prevKAMA)*tempReal) + prevKAMA;
           outputs[0][outIdx++] = prevKAMA;
        }
        
        return new IndicatorResult(outBegIdx, outIdx);
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
    	switch (index) {
        	case 0:
        		timePeriod = (Integer) value;               
        		break;
        	case 1:
        		fastMAperiod = (Integer) value;	                        			               
        		break;            
        	case 2:
        		slowMAperiod = (Integer) value;	                        			                	               
        		break;	            	           
        	default:
        		throw new ArrayIndexOutOfBoundsException(index);
    	}
    }
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
