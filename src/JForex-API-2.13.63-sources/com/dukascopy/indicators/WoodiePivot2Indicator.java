/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.indicators.BooleanOptInputDescription;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Mar 1, 2010
 * Time: 10:09:46 AM
 */
public class WoodiePivot2Indicator extends AbstractPivotIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final Object[] outputs = new Object[8];

    @Override
    public void onStart(IIndicatorContext context) {
    	super.onStart(context);

        indicatorInfo = new IndicatorInfo("WOODPIVOT2", "Woodie Pivot", "Overlap Studies", true, false, false, 1, 1, 8);
        
        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Input data", InputParameterInfo.Type.BAR)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Show historical levels", OptInputParameterInfo.Type.OTHER, new BooleanOptInputDescription(showHistoricalLevels))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            createOutputParameterInfo("Central Point (P)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, true),
            createOutputParameterInfo("Resistance (R1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, true),
            createOutputParameterInfo("Support (S1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, true),
            createOutputParameterInfo("Resistance (R2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, true),
            createOutputParameterInfo("Support (S2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, true),
            createOutputParameterInfo("Resistance (R3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, true),
            createOutputParameterInfo("Support (S3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, true),
        	createOutputParameterInfo("Separators", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LEVEL_FINE_DASHED_LINE, true)
        };

        outputParameterInfos[7].setColor(DefaultColors.STEEL_BLUE);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
       
        IBar prevBar = inputs[0][startIndex - 1];

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++){
            // P
            double p = (2 * prevBar.getClose() + prevBar.getHigh() + prevBar.getLow()) / 4;
            ((double[]) outputs[0])[j] = p;
            // R1
            ((double[]) outputs[1])[j] = 2 * p - prevBar.getLow();
            // S1
            ((double[]) outputs[2])[j] = 2 * p - prevBar.getHigh();
            // R2
            ((double[]) outputs[3])[j] = p + prevBar.getHigh() -  prevBar.getLow();
            // S2
            ((double[]) outputs[4])[j] = p - (prevBar.getHigh() - prevBar.getLow());
            // R3
            ((double[]) outputs[5])[j] = prevBar.getHigh() + 2 * (p - prevBar.getLow());
            // S3
            ((double[]) outputs[6])[j] = prevBar.getLow() - 2 * (prevBar.getHigh() - p);

            ((Object[]) outputs[7])[j] = Double.NaN;

            prevBar = inputs[0][i];
        }
        
        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
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
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
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
        inputs[index] = (IBar[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        showHistoricalLevels = (Boolean) value;
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = array;
    }

    @Override
    public int getLookback() {
        return 1;
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
