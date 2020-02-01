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

/**
 * @author S.Vishnyakov
 * Date: Dec 1, 2009
 */
public class BearPowerIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];
    private int timePeriod = 13;
    private IIndicator ema;

    public void onStart(IIndicatorContext context) {
        IIndicatorsProvider provider = context.getIndicatorsProvider();
        ema = provider.getIndicator("EMA");

        indicatorInfo = new IndicatorInfo("BEARP", "Bear Power", "Momentum Indicators", false, false, true, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };
        optInputParameterInfos = new OptInputParameterInfo[]{new OptInputParameterInfo("Time Period",
                OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(13, 2, 100, 1))};
        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Bears", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
           return new IndicatorResult(0, 0);
        }

        double[] emaA = new double[endIndex - startIndex + 1 + ema.getLookback()];
        ema.setInputParameter(0, inputs[0][1]);
        ema.setOptInputParameter(0, timePeriod);
        ema.setOutputParameter(0, emaA);

        ema.calculate(startIndex - 1, endIndex);

        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            //System.out.println(i + " " + inputs[0][3][i] + " " + emaA[j]);
            outputs[0][j] = inputs[0][3][i] - emaA[j + 1];
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
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}