package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Dec 17, 2009
 */

public class ForceIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][][] inputsPrice = new double[1][][];
    private int timePeriod = 13;
    private double[][] outputs = new double[1][];
    InputParameterInfo maInput;
    private IIndicator ma;
    IIndicatorsProvider provider;
    private int maType;    

    public void onStart(IIndicatorContext context) {
        provider = context.getIndicatorsProvider();
        ma = provider.getIndicator("SMA");
        indicatorInfo = new IndicatorInfo("FORCEI", "Force Index", "Momentum Indicators",
        		false, false, true, 2, 2, 1);

        int[] maValues = new int[IIndicators.MaType.values().length - 2];
        String[] maNames = new String[IIndicators.MaType.values().length - 2];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        inputParameterInfos = new InputParameterInfo[] {
             new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE),
             new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };
        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(13, 2, 100, 1)),
            new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
                };
        outputParameterInfos = new OutputParameterInfo[] {new OutputParameterInfo("out", OutputParameterInfo.Type.DOUBLE,
                OutputParameterInfo.DrawingStyle.LINE)};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {        
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
                
        double[] maA = new double[endIndex + 2 - ma.getLookback()];        
        ma.setInputParameter(0, inputs[0]);
        ma.setOutputParameter(0, maA);
        if (IIndicators.MaType.values()[maType] == IIndicators.MaType.MAMA){
        	double[] maDummy = new double[endIndex + 2 - ma.getLookback()];        	
        	ma.setOptInputParameter(0, 0.5);
        	ma.setOptInputParameter(1, 0.05);        		       
            ma.setOutputParameter(1, maDummy);         
        }
        else {
	        ma.setOptInputParameter(0, timePeriod);	        	        
        }
        IndicatorResult dMAResult = ma.calculate(startIndex - 1, endIndex);
        int i, k;
        for (i = 1, k = dMAResult.getNumberOfElements(); i < k; i++) {
            outputs[0][i-1] = inputsPrice[0][4][i-1] * (maA[i] - maA[i-1]) ;
        }
        return new IndicatorResult(startIndex, i-1);
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
    	return ma.getLookback() + 1;
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
        switch (index) {
            case 0:
                inputs[0] = (double[]) array;
                break;
            case 1:
                inputsPrice[0] = (double[][]) array;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOptInputParameter(int index, Object value) {
        if (index == 0) {
            timePeriod = (Integer) value;            
        } else if (index == 1) {
            int fastMaType = (Integer) value;
            ma = provider.getIndicator(IIndicators.MaType.values()[fastMaType].name());
            
            this.maType = fastMaType;
            if (!IIndicators.MaType.values()[maType].equals(IIndicators.MaType.MAMA)){
            	ma.setOptInputParameter(0, timePeriod);
            }                        
       	}
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}