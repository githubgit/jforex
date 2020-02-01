/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. 
 * @author Dmitry Shohov
 */
public class VolumeIndicator implements IIndicator {

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[3][];
    
    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("VOLUME", "Volumes", "Volume Indicators", false, false, false, 1, 0, 3);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Volumes", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
    		new OutputParameterInfo("Bullish volume (Close ≥ Open)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
    		new OutputParameterInfo("Bearish volume (Close < Open)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
		};

        outputParameterInfos[0].setColor(DefaultColors.STEEL_BLUE);
        outputParameterInfos[1].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[1].setShowOutput(false);
        outputParameterInfos[2].setColor(DefaultColors.DARK_RED);
        outputParameterInfos[2].setShowOutput(false);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            outputs[0][j] = inputs[0][VOLUME][i];

            if (inputs[0][CLOSE][i] >= inputs[0][OPEN][i]) {
                outputs[1][j] = outputs[0][j];
                outputs[2][j] = Double.NaN;
            } else {
                outputs[1][j] = Double.NaN;
                outputs[2][j] = outputs[0][j];
            }
        }

        return new IndicatorResult(startIndex, j);
    }

    @Override
    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    @Override
    public InputParameterInfo getInputParameterInfo(int index) {
        if (index < inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    @Override
    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        return null;
    }

    @Override
    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    @Override
    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return 0;
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
