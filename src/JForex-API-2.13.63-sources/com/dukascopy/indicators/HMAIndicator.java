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
 * @author anatoly.pokusayev
 *
 */

public class HMAIndicator implements IIndicator {
    private IIndicator fullPeriodWMA, halfPeriodWMA, actualWMA;
    private int timePeriod = 15;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[1][];

    public void onStart(IIndicatorContext context) {
    	fullPeriodWMA = context.getIndicatorsProvider().getIndicator("WMA");
    	halfPeriodWMA = context.getIndicatorsProvider().getIndicator("WMA");
    	actualWMA = context.getIndicatorsProvider().getIndicator("WMA");    	

        indicatorInfo = new IndicatorInfo("HMA", "Hull Moving Average", "Overlap Studies", true, false, false, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[]{
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };
        optInputParameterInfos = new OptInputParameterInfo[]{
            new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 2, 500, 1))
        };
        outputParameterInfos = new OutputParameterInfo[]{
            new OutputParameterInfo("HMA", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }    	
          
        fullPeriodWMA.setInputParameter(0, inputs[0]);                        
        double[] outWMAFullPeriod = 
        	new double[endIndex - startIndex + actualWMA.getLookback() + 1];       
        fullPeriodWMA.setOutputParameter(0, outWMAFullPeriod);
        IndicatorResult wmaFullPeriodRes = fullPeriodWMA.calculate(startIndex - actualWMA.getLookback(), endIndex); 

        double[] outWMAHalfPeriod = new double[endIndex - startIndex + actualWMA.getLookback() + 1];
        halfPeriodWMA.setInputParameter(0, inputs[0]);
        halfPeriodWMA.setOptInputParameter(0, (int)(timePeriod / 2) < 2 ? 2 : (int)(timePeriod / 2));        
        halfPeriodWMA.setOutputParameter(0, outWMAHalfPeriod); 
        halfPeriodWMA.calculate(startIndex - actualWMA.getLookback(), endIndex);        
                
        double[] wmaInput = new double[wmaFullPeriodRes.getNumberOfElements()];
        for (int i = 0; i < wmaFullPeriodRes.getNumberOfElements(); i++) {
        	wmaInput[i] = 2 * outWMAHalfPeriod[i] - outWMAFullPeriod[i];
        }
      
        actualWMA.setInputParameter(0, wmaInput);        
        actualWMA.setOutputParameter(0, outputs[0]);                
        IndicatorResult wmaRes = actualWMA.calculate(0, wmaFullPeriodRes.getNumberOfElements() - 1);
      
       return new IndicatorResult(wmaFullPeriodRes.getFirstValueIndex() + wmaRes.getFirstValueIndex(), wmaRes.getNumberOfElements());      
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
    	return fullPeriodWMA.getLookback() + actualWMA.getLookback();  	
    }

    public int getLookforward() {
        return 0;
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

    public void setOutputParameter(int index, Object array) {
        switch (index) {
            case 0:
                outputs[index] = (double[]) array;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                timePeriod = (Integer) value;                
                fullPeriodWMA.setOptInputParameter(0, timePeriod);                
                actualWMA.setOptInputParameter(0, (int)Math.sqrt(timePeriod) < 2 ? 2 : (int)Math.sqrt(timePeriod));
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}