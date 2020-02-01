package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class PearsonCorrelationCoefficient implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[2][];
    private double[][] outputs = new double[1][];    
    private int optInTimePeriod = 30;
//    private final double aboveZero = 0.00000001;
        
    public void onStart(IIndicatorContext context) {    
        indicatorInfo = new IndicatorInfo("CORREL", "Pearson's Correlation Coefficient(r)", "Statistic Functions", false, false, false, 2, 1, 1);
        
        inputParameterInfos = new InputParameterInfo[] {
        		new InputParameterInfo("Price 0", InputParameterInfo.Type.DOUBLE),
        		new InputParameterInfo("Price 1", InputParameterInfo.Type.DOUBLE)};

        optInputParameterInfos = new OptInputParameterInfo[] {new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(optInTimePeriod, 1, 2000, 1))};
        outputParameterInfos = new OutputParameterInfo[] {new OutputParameterInfo("Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)};        
    }
    
    public IndicatorResult calculate(int startIndex, int endIndex) {        
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        int trailingIdx = startIndex - getLookback(), today, outIdx;
        double x, y, trailingX, trailingY, tempReal;
        
        /* Calculate the initial values */
        double sumXY = 0, sumX = 0, sumY = 0, sumX2 = 0, sumY2 = 0;
        for(today = trailingIdx; today <= startIndex; today++ )
        {
           x = inputs[0][today];
           sumX  += x;
           sumX2 += x*x;

           y = inputs[1][today];
           sumXY += x*y;
           sumY  += y;
           sumY2 += y*y;
        }

        /* Write the first output. 
         * Save first the trailing values since the input
         * and output might be the same array,
         */
        trailingX = inputs[0][trailingIdx];
        trailingY = inputs[1][trailingIdx++];
        tempReal = (sumX2-sumX*sumX/optInTimePeriod) * (sumY2-sumY*sumY/optInTimePeriod);

        // JFOREX-5349
//        if(tempReal >= aboveZero)
        	outputs[0][0] = (sumXY-((sumX*sumY)/optInTimePeriod)) / Math.sqrt(tempReal);
//        else
//        	outputs[0][0] = 0;

        /* Tight loop to do subsequent values. */
        outIdx = 1;
        while( today <= endIndex )
        {
           /* Remove trailing values */
           sumX  -= trailingX;
           sumX2 -= trailingX*trailingX;

           sumXY -= trailingX*trailingY;
           sumY  -= trailingY;
           sumY2 -= trailingY*trailingY;

           /* Add new values */
           x = inputs[0][today];
           sumX  += x;
           sumX2 += x*x;

           y = inputs[1][today++];
           sumXY += x*y;
           sumY  += y;
           sumY2 += y*y;

           /* Output new coefficient.
            * Save first the trailing values since the input
            * and output might be the same array,
            */
           trailingX = inputs[0][trailingIdx];
           trailingY = inputs[1][trailingIdx++];
           tempReal = (sumX2-((sumX*sumX)/optInTimePeriod)) * (sumY2-((sumY*sumY)/optInTimePeriod));
//           if(tempReal >= aboveZero) {
        	   outputs[0][outIdx++] = (sumXY-((sumX*sumY)/optInTimePeriod)) / Math.sqrt(tempReal);
//           } else
//        	   outputs[0][outIdx++] = 0;
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
        return optInTimePeriod - 1;
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
    	optInTimePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
