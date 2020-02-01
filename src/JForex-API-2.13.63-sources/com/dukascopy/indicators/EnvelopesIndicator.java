/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.indicators.DoubleRangeDescription;
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
 * Date: Jul 30, 2009
 * Time: 5:30:14 PM
 */
public class EnvelopesIndicator implements IIndicator {
    private IIndicator sma;
    private int timePeriod = 14;
    private double deviation = 0.1;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[2][];

    public void onStart(IIndicatorContext context) {
        sma = context.getIndicatorsProvider().getIndicator("SMA");

        indicatorInfo = new IndicatorInfo("MAEnvelope", "MA Envelope", "Overlap Studies", true, false, false, 1, 2, 2);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
		};
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 2, 500, 1)),
            new OptInputParameterInfo("Deviation", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(deviation, 0.01, 100, 0.01, 2))
        };
        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Low Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("High Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] dsma;

        dsma = new double[endIndex - startIndex + 2 + sma.getLookback()];

        sma.setInputParameter(0, inputs[0]);
        sma.setOptInputParameter(0, timePeriod);
        sma.setOutputParameter(0, dsma);

        IndicatorResult dSmaResult = sma.calculate(startIndex - 1, endIndex);

        double valueLow, valueHigh;
        int i, k;
        for (i = 1, k = dSmaResult.getNumberOfElements(); i < k; i++) {
            valueLow =  (1 - deviation/100) *dsma[i];
            valueHigh = (1 + deviation/100) *dsma[i];
            outputs[0][i - 1] = valueLow;
            outputs[1][i - 1] = valueHigh;
        }

        return new IndicatorResult(startIndex, i - 1);
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
            case 1:
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
                break;
            case 1:
                deviation = (Double) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}