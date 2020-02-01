package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class SMIIndicator implements IIndicator{			   	    
    private int fastKPeriod = 2;
    private int slowKPeriod = 5;
    private int slowDPeriod = 8;	    
    private int momPeriod = 5;	    
    private IIndicator kMA;
    private IIndicator dMA;
    private IIndicator signalMA;	    	    
    private IIndicatorsProvider indicatorsProvider;	    
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[2][];

    public void onStart(IIndicatorContext context) {
        indicatorsProvider = context.getIndicatorsProvider();
        kMA = indicatorsProvider.getIndicator("EMA");
        kMA.setOptInputParameter(0, slowKPeriod);
        dMA = indicatorsProvider.getIndicator("EMA");
        dMA.setOptInputParameter(0, slowDPeriod);
        signalMA = indicatorsProvider.getIndicator("EMA");
        signalMA.setOptInputParameter(0, momPeriod);

        indicatorInfo = new IndicatorInfo("SMI", "Stochastic Momentum Index", "Momentum Indicators", false, false, true, 1, 4, 2);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input", InputParameterInfo.Type.PRICE)};
        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("fastKPeriod", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastKPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("slowKPeriod", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowKPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("slowDPeriod", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowDPeriod, 2, 500, 1)),
            new OptInputParameterInfo("SmoothingPeriod", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(momPeriod, 2, 500, 1))
        };
        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("SMI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)    		,
            new OutputParameterInfo("Signal", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };
    }			

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] kBuffer1 = new double[endIndex - startIndex + 1 + dMA.getLookback() + kMA.getLookback() + signalMA.getLookback()];
        double[] kBuffer2 = new double[endIndex - startIndex + 1 + dMA.getLookback() + kMA.getLookback() + signalMA.getLookback()];

        int i, j;
        for (i = startIndex - dMA.getLookback() - kMA.getLookback() - signalMA.getLookback(), j = 0; i <= endIndex; i++, j++) {	        	
            double highestHigh = inputs[0][2][i], lowestLow = inputs[0][3][i], close = inputs[0][1][i];
            for (int k = fastKPeriod; k > 0; k--) {
                highestHigh = inputs[0][2][i - k] > highestHigh ? inputs[0][2][i - k] : highestHigh;
                lowestLow = inputs[0][3][i - k] < lowestLow ? inputs[0][3][i - k] : lowestLow;
            }	            
            kBuffer1[j] = highestHigh - lowestLow;
            kBuffer2[j] = close - (highestHigh + lowestLow) / 2;
        }		 	   	        

        double[] kOutput1 = new double[kBuffer1.length - kMA.getLookback()];
        double[] kOutput2 = new double[kBuffer2.length - kMA.getLookback()];
        double[] dOutput1 = new double[kOutput1.length - dMA.getLookback()];
        double[] dOutput2 = new double[kOutput2.length - dMA.getLookback()];

        kMA.setInputParameter(0, kBuffer1);	       
        kMA.setOutputParameter(0, kOutput1);	        
        kMA.calculate(0, kBuffer1.length - 1);

        kMA.setInputParameter(0, kBuffer2);	       
        kMA.setOutputParameter(0, kOutput2);	        
        kMA.calculate(0, kBuffer2.length - 1);

        dMA.setInputParameter(0, kOutput1);	       
        dMA.setOutputParameter(0, dOutput1);	        
        dMA.calculate(0, kOutput1.length - 1);

        dMA.setInputParameter(0, kOutput2);	       
        dMA.setOutputParameter(0, dOutput2);	        
        dMA.calculate(0, kOutput2.length - 1);

        double[] smiInput = new double[endIndex - startIndex + 1 + signalMA.getLookback()]; 
        for (i = 0, j = 0; i <= dOutput1.length - 1; i++, j++){
            if (dOutput1[i] == 0){
                smiInput[j] = 0;
            }
            else{
                smiInput[j] = 100 * (dOutput2[i] / (0.5 * dOutput1[i]));
            }
        }

        signalMA.setInputParameter(0, smiInput);	       	        
        signalMA.setOutputParameter(0, outputs[1]);	        
        IndicatorResult res = signalMA.calculate(0, smiInput.length - 1);

        System.arraycopy(smiInput, res.getFirstValueIndex(), outputs[0], 0, res.getNumberOfElements());		        	       

        return new IndicatorResult(res.getFirstValueIndex() + fastKPeriod + dMA.getLookback() + kMA.getLookback(), res.getNumberOfElements());
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
         return dMA.getLookback() + kMA.getLookback() + fastKPeriod + signalMA.getLookback(); 
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
        switch (index) {
            case 0:
                fastKPeriod = (Integer) value;               
                break;
            case 1:
                slowKPeriod = (Integer) value;	                
                kMA.setOptInputParameter(0, slowKPeriod);	                
                break;            
            case 2:
                slowDPeriod = (Integer) value;
                dMA.setOptInputParameter(0, slowDPeriod);	                 
                break;                           
            case 3:
                momPeriod = (Integer) value;
                signalMA.setOptInputParameter(0, momPeriod);	                 
                break;                           	            	           
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
