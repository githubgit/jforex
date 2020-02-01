/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

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
 * Created by: S.Vishnyakov
 * Date: Jan 23, 2009
 * Time: 1:59:16 PM
 */
public class VolumeWAP implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] inputsAveragePrice = new double[1][];
    private double[][] outputs = new double[1][];
    private int timePeriod = 4;

    private IIndicator sumPriceIndicator;
    private IIndicator sumDivPriceIndicator;

    public void onStart(IIndicatorContext context) {
        sumPriceIndicator = context.getIndicatorsProvider().getIndicator("SUM");
        sumDivPriceIndicator = context.getIndicatorsProvider().getIndicator("SUM");

        indicatorInfo = new IndicatorInfo("VolumeWAP", "Volume Weighted Average Price", "Overlap Studies", true, false, false, 2, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Prices", InputParameterInfo.Type.PRICE),
    		new InputParameterInfo("Avg price", InputParameterInfo.Type.DOUBLE)
        };
        inputParameterInfos[1].setAppliedPrice(IIndicators.AppliedPrice.TYPICAL_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 2, 2000, 1))
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

        double[] multPrice = new double[endIndex - startIndex + 1 + getLookback()];
        double[] sumPrice = new double[endIndex - startIndex + 1];
        double[] sumDiv = new double[endIndex - startIndex + 1];

        for (int i = startIndex - getLookback(), j = 0; i <= endIndex; i++, j++) {
            // Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            multPrice[j] = inputs[0][4][i] * inputsAveragePrice[0][i];
        }

        sumPriceIndicator.setInputParameter(0, multPrice);
        sumPriceIndicator.setOutputParameter(0, sumPrice);
        sumPriceIndicator.calculate(0, multPrice.length - 1);

        sumDivPriceIndicator.setInputParameter(0, inputs[0][4]);
        sumDivPriceIndicator.setOutputParameter(0, sumDiv);
        sumDivPriceIndicator.calculate(startIndex, endIndex);

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
        	if (sumDiv[i] == 0) {
        		outputs[0][i] = Double.NaN;
        	} else {
        		outputs[0][i] = sumPrice[i] / sumDiv[i];
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
        timePeriod = (Integer) value;
        sumPriceIndicator.setOptInputParameter(0, timePeriod);
        sumDivPriceIndicator.setOptInputParameter(0, timePeriod);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return sumPriceIndicator.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
