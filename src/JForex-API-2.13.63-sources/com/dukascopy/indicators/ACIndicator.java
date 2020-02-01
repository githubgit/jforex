package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * @author S.Vishnyakov
 * Date: Dec 18, 2009
 */
public class ACIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[2][];

    private int fastPeriod = 5;
    private int slowPeriod = 34;

    private IIndicator sma1;
    private IIndicator sma2;
    private IIndicator sma3;

    public void onStart(IIndicatorContext context) {
        sma1 = context.getIndicatorsProvider().getIndicator("SMA");
        sma2 = context.getIndicatorsProvider().getIndicator("SMA");
        sma3 = context.getIndicatorsProvider().getIndicator("SMA");

        indicatorInfo = new IndicatorInfo("AC", "Accelerator/Decelerator Oscillator", "Bill Williams", false, false, false, 1, 2, 2);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Fast Period", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(5, 2, 100, 1)),
            new OptInputParameterInfo("Slow Period", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(34, 2, 100, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Positive", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Negative", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[1].setColor(DefaultColors.RED);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {        
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        sma1.setOptInputParameter(0, fastPeriod);
        sma2.setOptInputParameter(0, slowPeriod);
        
        double[] smaA1 = new double[endIndex - startIndex + 1 + sma3.getLookback()];
        double[] smaA2 = new double[endIndex - startIndex + 1 + sma3.getLookback()];
        double[] ao = new double[endIndex  - startIndex + 1 + sma3.getLookback()];
        double[] smaA3 = new double[endIndex - startIndex + 1];

        sma1.setInputParameter(0, inputs[0]);
        sma2.setInputParameter(0, inputs[0]);
        sma1.setOutputParameter(0, smaA1);
        sma2.setOutputParameter(0, smaA2);
        
        sma1.calculate(startIndex - sma3.getLookback(), endIndex);
        IndicatorResult dSMA2Result = sma2.calculate(startIndex - sma3.getLookback(), endIndex);

        sma3.setOptInputParameter(0, fastPeriod);
        int z;
        for (z = 0; z < dSMA2Result.getNumberOfElements(); z++){
            ao[z] = smaA1[z] - smaA2[z];
        }

        sma3.setInputParameter(0, ao);
        sma3.setOutputParameter(0, smaA3);
      
        IndicatorResult dSMA3Result = sma3.calculate(0, dSMA2Result.getNumberOfElements() - 1);
        
        double[] aoShortened = new double[dSMA3Result.getNumberOfElements()];
        System.arraycopy(ao, dSMA3Result.getFirstValueIndex(), aoShortened, 0, dSMA3Result.getNumberOfElements());
        
        int i, k;
        for (i = 0, k = (dSMA3Result.getNumberOfElements()); i < k; i++) {            
        	double value = aoShortened[i] - smaA3[i];
            if (i > 0) {
                double prevValuePos = 0;
                if (outputs[1][i - 1] == 0) {
                    prevValuePos = outputs[0][i-1];
                }else {
                    prevValuePos = outputs[1][i-1];
                }
                if (value > prevValuePos) {
                    outputs[0][i] =  value;
                    outputs[1][i] =  0;
                } else {
                    outputs[0][i] =  0;
                    outputs[1][i] =  value;
                }
            } else {
                if (value > 0) {
                    outputs[0][i] =  value;
                    outputs[1][i] =  0;
                } else {
                    outputs[0][i] =  0;
                    outputs[1][i] =  value;
                }
            }
        }

        return new IndicatorResult(dSMA2Result.getFirstValueIndex() + dSMA3Result.getFirstValueIndex(), dSMA3Result.getNumberOfElements());
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
        if (index == 0) {
            fastPeriod = (Integer) value;
            sma1.setOptInputParameter(0, fastPeriod);
            sma3.setOptInputParameter(0, fastPeriod);
        } else {
            slowPeriod = (Integer) value;
            sma2.setOptInputParameter(0, slowPeriod);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
    	return Math.max(sma1.getLookback(), sma2.getLookback()) + sma3.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
