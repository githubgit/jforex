package com.dukascopy.indicators;

import java.util.Arrays;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class VolumeWAP2 implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] inputsAveragePrice = new double[1][];
    private double[][] outputs = new double[1][];

    private static final int UNSTABLE_PERIOD_LOOKBACK = 100;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("VolumeWAP2", "Volume Weighted Average Price", "Overlap Studies", true, false, true, 2, 0, 1);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Prices", InputParameterInfo.Type.PRICE),
    		new InputParameterInfo("Avg price", InputParameterInfo.Type.DOUBLE)
        };
        inputParameterInfos[1].setAppliedPrice(IIndicators.AppliedPrice.TYPICAL_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
		};

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("VWAP", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
		};
        outputParameterInfos[0].setGapAtNaN(true);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        Arrays.fill(outputs[0], Double.NaN);

        double sumPrice = 0;
        double sumDiv = 0;

        for (int i = startIndex + UNSTABLE_PERIOD_LOOKBACK, j = UNSTABLE_PERIOD_LOOKBACK; i <= endIndex; i++, j++) {
            // Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            sumPrice += inputs[0][4][i] * inputsAveragePrice[0][i];
        	sumDiv += inputs[0][4][i];
            
            if (sumDiv == 0) {
        		outputs[0][j] = Double.NaN;
        	} else {
        		outputs[0][j] = sumPrice / sumDiv;
        	}
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
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
        switch (index) {
            case 0:
                inputs[0] = (double[][]) array;
                break;
            case 1:
                inputsAveragePrice[0] = (double[]) array;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOptInputParameter(int index, Object value) {
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return 0;
    }

    public int getLookforward() {
        return 0;
    }
}
