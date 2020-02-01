/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
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
 * @author Dmitry Shohov
 */
public class FractalIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[2][];
    private int barsOnSides = 2;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("FRACTAL", "Fractal indicator", "Bill Williams", true, false, false, 1, 1, 2);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};
        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Number of bars on sides", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(2, 1, 600, 1))
		};
        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Maximums", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN),
            new OutputParameterInfo("Minimums", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP)
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        //calculating endIndex taking into account lookforward value
        if (endIndex + getLookforward() >= inputs[0][0].length) {
            endIndex = inputs[0][0].length - 1 - getLookforward();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            boolean isMax = true;
            double high = inputs[0][2][i];
            for (int k = i - barsOnSides; k <= i + barsOnSides; k++) {
                if (i == k) {
                    continue;
                }
                if (high < inputs[0][2][k]) {
                    isMax = false;
                    break;
                }
            }

            double low = inputs[0][3][i];
            boolean isMin = true;
            for (int k = i - barsOnSides; k <= i + barsOnSides; k++) {
                if (i == k) {
                    continue;
                }
                if (low > inputs[0][3][k]) {
                    isMin = false;
                    break;
                }
            }

            outputs[0][j] = isMax ? high : Double.NaN;
            outputs[1][j] = isMin ? low : Double.NaN;
        }

        return new IndicatorResult(startIndex, j, endIndex);
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
        return barsOnSides;
    }

    public int getLookforward() {
        return barsOnSides;
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
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}