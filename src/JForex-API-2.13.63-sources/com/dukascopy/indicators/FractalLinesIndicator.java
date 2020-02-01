package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class FractalLinesIndicator implements IIndicator{
	private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;    
    private int barsOnSides = 2;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[2][];       
    private IIndicator fractal;    
    
    public void onStart(IIndicatorContext context) {   
    	fractal = context.getIndicatorsProvider().getIndicator("FRACTAL");     
    	fractal.setOptInputParameter(0, barsOnSides);
    	
        indicatorInfo = new IndicatorInfo("FRACTALLINES", "Fractal Lines Indicator", "", true, false, true, 1, 1, 2);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)};
        optInputParameterInfos = new OptInputParameterInfo[] {
        		new OptInputParameterInfo("bars", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(barsOnSides, 1, 200, 1))};
        outputParameterInfos = new OutputParameterInfo[] {
        		new OutputParameterInfo("High", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE), 
                new OutputParameterInfo("Low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)};                                      
    }    
   
    public IndicatorResult calculate(int startIndex, int endIndex) {       
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
                   
        int i, j;
        double[][] fractalOutput = new double[2][endIndex - startIndex + 2 + fractal.getLookback()];        
        fractal.setInputParameter(0, inputs[0]);
        fractal.setOptInputParameter(0, barsOnSides);
        fractal.setOutputParameter(0, fractalOutput[0]);
        fractal.setOutputParameter(1, fractalOutput[1]);                
        fractal.calculate(startIndex - 1, endIndex);        
               
        double high = Double.NaN, low = Double.NaN;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {                            
        	if(fractalOutput[0][j] >= inputs[0][2][i]){
        		high = fractalOutput[0][j];
        	}
        	if(fractalOutput[1][j] <= inputs[0][3][i] && !Double.isNaN(fractalOutput[1][j]) && fractalOutput[1][j] > 0){
        		low = fractalOutput[1][j];
        	}
	        outputs[0][j] = high;
	        outputs[1][j] = low;
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

    public int getLookback() {
        return fractal.getLookback() + fractal.getLookforward();
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
             barsOnSides = (Integer) value;
             fractal.setOptInputParameter(0, barsOnSides);
         }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
