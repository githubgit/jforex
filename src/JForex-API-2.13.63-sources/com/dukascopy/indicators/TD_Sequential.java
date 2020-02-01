/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * 
 * Created by: S.Vishnyakov
 * Date: Jan 7, 2009
 * Time: 4:25:31 PM
 */
public class TD_Sequential implements IIndicator, IDrawingIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final int[][] outputs = new int[2][];

    private int timePeriod = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TD_S", "TD Sequential", "Overlap Studies", true, false, true, 1, 1, 2);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
		};

        optInputParameterInfos = new OptInputParameterInfo[]{
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 2, 100, 1))
		};

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Positive", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP),
    		new OutputParameterInfo("Negative", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP)
		};

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[0].setDrawnByIndicator(true);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[1].setDrawnByIndicator(true);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int i, j = 0;
        for (i = startIndex; i <= endIndex; i++, j++) {        
        	calculateSetupValue(i, j);            
        }

        return new IndicatorResult(startIndex, j);
    }

    private void calculateSetupValue(int inputIndex, int outputIndex){
    	int positiveAccumulatedValue = 0;
    	int negativeAccumulatedValue = 0;
    	
    	if (outputIndex > 0){
    		positiveAccumulatedValue = outputs[0][outputIndex - 1];
    		negativeAccumulatedValue = outputs[1][outputIndex - 1];
    	}
    	
    	double currentBarPrice = inputs[0][inputIndex];
        double timePeriodBarPrice = inputs[0][inputIndex - timePeriod];
                
        double priorSetupPrice = inputs[0][inputIndex - 1];
        double flipPrice = inputs[0][inputIndex - 1 - timePeriod];
    	
        if(positiveAccumulatedValue == 0 && priorSetupPrice <= flipPrice && currentBarPrice > timePeriodBarPrice){        
        	outputs[0][outputIndex] = 1;
        }        
        else if (positiveAccumulatedValue != 0 && currentBarPrice > timePeriodBarPrice) {
        	outputs[0][outputIndex] = positiveAccumulatedValue + 1;
        }
        else if (positiveAccumulatedValue != 0 && currentBarPrice < timePeriodBarPrice){
        	outputs[0][outputIndex] = 0;
        }
        
        if(negativeAccumulatedValue == 0 && priorSetupPrice >= flipPrice && currentBarPrice < timePeriodBarPrice){        
        	outputs[1][outputIndex] = -1;
        }        
        else if (negativeAccumulatedValue != 0 && currentBarPrice < timePeriodBarPrice) {
        	outputs[1][outputIndex] = negativeAccumulatedValue - 1;
        } 
        else if (negativeAccumulatedValue != 0 && currentBarPrice > timePeriodBarPrice){
        	outputs[1][outputIndex] = 0;
        }
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
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (int[]) array;
    }

    public int getLookback() {
        return timePeriod + 1;
    }

    public int getLookforward() {
        return 0;
    }

    public Point drawOutput(
    		Graphics g,
    		int outputIdx,
    		Object values2,
    		Color color,
    		Stroke stroke,
            IIndicatorDrawingSupport indicatorDrawingSupport,
            List<Shape> shapes,
            Map<Color, List<Point>> handles
    ){
        int[] values = (int[]) values2;
        if (values != null && values.length > 0) {
            boolean only9s = false;
            Font defaultTD;
            Font defaultBigTD;
            String fontName = g.getFont().getName();
            if (indicatorDrawingSupport.getCandleWidthInPixels() == 1) {
                //reduce font size when showing candles as lines
                defaultTD = new Font(fontName, Font.PLAIN, 9);
                defaultBigTD = new Font(fontName, Font.BOLD, 11);
            } else if (indicatorDrawingSupport.getCandleWidthInPixels() > 20) {
                //increase font size when showing candles with width more than 20 pixels
                defaultTD = new Font(fontName, Font.PLAIN, 12);
                defaultBigTD = new Font(fontName, Font.BOLD, 15);
            } else {
                //default font
                defaultTD = new Font(fontName, Font.PLAIN, 10);
                defaultBigTD = new Font(fontName, Font.BOLD, 13);
            }
            if (indicatorDrawingSupport.getCandleWidthInPixels() == 1 && indicatorDrawingSupport.getSpaceBetweenCandlesInPixels() == 0) {
                //max zoom out
                only9s = true;
            }
            FontMetrics metricsTD = g.getFontMetrics(defaultTD);
            FontMetrics metricsBigTD = g.getFontMetrics(defaultTD);

            // check for visible candles
            for (int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen(), j = indicatorDrawingSupport.getNumberOfCandlesOnScreen(); j > 0; i++, j--) {
                if ((outputIdx == 0 && values[i] > 0) || (outputIdx == 1 && values[i] < 0)) {
                    FontMetrics metrics;
                    if (values[i] == 9 || values[i] == -9) {
                        g.setFont(defaultBigTD);
                        metrics = metricsBigTD;
                    } else if (!only9s) {
                        g.setFont(defaultTD);
                        metrics = metricsTD;
                    } else {
                        continue;
                    }

                    if (values[i] > 0) {
                        g.setColor(color);
                        String text = Integer.toString(values[i]);
                        int textWidth = metrics.stringWidth(text);
                        g.drawString(text, (int) indicatorDrawingSupport.getMiddleOfCandle(i) - (textWidth / 2),
                                (int) indicatorDrawingSupport.getYForValue(indicatorDrawingSupport.getCandles()[i].getHigh()) - metrics.getDescent());
                    } else if (values[i] < 0 && values[i] != Integer.MIN_VALUE) {
                        g.setColor(color);
                        String text = Integer.toString(Math.abs(values[i]));
                        int textWidth = metrics.stringWidth(text);
                        g.drawString(text, (int) indicatorDrawingSupport.getMiddleOfCandle(i) - (textWidth / 2),
                                (int) indicatorDrawingSupport.getYForValue(indicatorDrawingSupport.getCandles()[i].getLow()) + metrics.getAscent() + (values[i] == -9 ? 2 : 0));
                    }
                }
            }
        }
        return null;
    }
}
