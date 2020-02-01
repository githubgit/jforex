/*
 * Copyright 2010 Dukascopy® Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
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
 * @author Sergey Vishnyakov
 */
public class TrendEnvelopesIndicator implements IIndicator {
    private IIndicator lwma;

    private int timePeriod = 14;
    private double deviation = 0.1;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[2][];

    public void onStart(IIndicatorContext context) {
        lwma = context.getIndicatorsProvider().getIndicator("LWMA");

        indicatorInfo = new IndicatorInfo("TrendEnvelopes", "Trend Envelope", "Overlap Studies", true, false, true, 1, 2, 2);
        
        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };
        
        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 2, 500, 1)),
            new OptInputParameterInfo("Deviation", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(deviation, 0.01, 100, 0.01, 2))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Low Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("High Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[0].setGapAtNaN(true);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[1].setGapAtNaN(true);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] dsma = new double[endIndex - startIndex + 1];
        lwma.setInputParameter(0, inputs[0][1]);
        lwma.setOptInputParameter(0, timePeriod);
        lwma.setOutputParameter(0, dsma);
        lwma.calculate(startIndex - 1, endIndex - 1);

        double[] valuesLow = new double[endIndex - startIndex + 1];
        double[] valuesHigh = new double[endIndex - startIndex + 1];
        int trend = 0;
        
        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            valuesLow[j] = (1 - deviation / 100) * dsma[j];
            valuesHigh[j] = (1 + deviation / 100) * dsma[j];

            if (inputs[0][1][i] > valuesHigh[j]) {
                trend = 1;
            } else if (inputs[0][1][i] < valuesLow[j]) {
                trend = -1;
            }

            if (trend > 0) {
                outputs[1][j] = Double.NaN;
                if (j > 0 && valuesLow[j] < valuesLow[j - 1]) {
                    valuesLow[j] = valuesLow[j - 1];
                }
                outputs[0][j] = valuesLow[j];
            } else {
                outputs[0][j] = Double.NaN;
                if (j > 0 && valuesHigh[j] > valuesHigh[j - 1]) {
                    valuesHigh[j] = valuesHigh[j - 1];
                }
                outputs[1][j] = valuesHigh[j];
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
        inputs[index] = (double[][]) array;
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

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                timePeriod = (Integer) value;
                lwma.setOptInputParameter(0, timePeriod);    
                break;
            case 1:
                deviation = (Double) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public int getLookback() {
        return lwma.getLookback() + 1;
    }

    public int getLookforward() {
        return 0;
    }
}
