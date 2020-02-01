/*
 * Copyright 2009 Dukascopy (Suisse) SA. All rights reserved.
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
 * Date: Oct 21, 2009
 * Time: 1:59:16 PM
 */
public class Pivot2Indicator extends AbstractPivotIndicator {
	
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    
    private final IBar[][] inputs = new IBar[1][];
    private final Object[] outputs = new Object[14];

    @Override
    public void onStart(IIndicatorContext context) {
    	super.onStart(context);
    	
        indicatorInfo = new IndicatorInfo("PIVOT2", "Pivot", "Overlap Studies", true, false, false, 1, 1, 14);
        
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
        	createOutputParameterInfo("Mid Point (P-R1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false),
        	createOutputParameterInfo("Mid Point (P-S1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false),
        	createOutputParameterInfo("Mid Point (R1-R2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false),
        	createOutputParameterInfo("Mid Point (S1-S2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false),
        	createOutputParameterInfo("Mid Point (R2-R3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false),
        	createOutputParameterInfo("Mid Point (S2-S3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false),
        	createOutputParameterInfo("Separators", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LEVEL_FINE_DASHED_LINE, true)
        };

        outputParameterInfos[13].setColor(DefaultColors.STEEL_BLUE);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
    	if (startIndex - getLookback() < 0) {
    		startIndex = getLookback();
    	}
    	if (startIndex > endIndex) {
    		return new IndicatorResult(0, 0);
    	}

        IBar previousBar = inputs[0][startIndex - 1];

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            IBar currentBar = inputs[0][i];

            // P
            double p = (previousBar.getClose() + previousBar.getHigh() + previousBar.getLow()) / 3;
            double r1 = 2 * p - previousBar.getLow();
            double s1 = 2 * p - previousBar.getHigh();
            double r2 = p + previousBar.getHigh() - previousBar.getLow();
            double s2 = p - previousBar.getHigh() + previousBar.getLow();
            double r3 = previousBar.getHigh() + 2 * (p - previousBar.getLow());
            double s3 = previousBar.getLow() - 2 * (previousBar.getHigh() - p);

            ((double[]) outputs[0])[j] = p;
            // R1
            ((double[]) outputs[1])[j] = r1;
            // S1
            ((double[]) outputs[2])[j] = s1;
            // R2
            ((double[]) outputs[3])[j] = r2;
            // S2
            ((double[]) outputs[4])[j] = s2;
            // R3
            ((double[]) outputs[5])[j] = r3;
            // S3
            ((double[]) outputs[6])[j] = s3;

            //Mid Points
            //P-R1
            ((double[]) outputs[7])[j] = p + (r1 - p) / 2;
            //P-S1
            ((double[]) outputs[8])[j] = p - (p - s1) / 2;
            //R1-R2
            ((double[]) outputs[9])[j] = r1 + (r2 - r1) / 2;
            //S1-S2
            ((double[]) outputs[10])[j] = s2 + (s1 - s2) / 2;
            //R2-R3
            ((double[]) outputs[11])[j] = r2 + (r3 - r2) / 2;
            //S2-S3
            ((double[]) outputs[12])[j] = s3 + (s2 - s3) / 2;

            ((Object[]) outputs[13])[j] = Double.NaN;

            previousBar = currentBar;
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
