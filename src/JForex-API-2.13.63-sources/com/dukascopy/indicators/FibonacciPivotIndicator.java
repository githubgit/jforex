/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.*;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * 
 * Created by: S.Vishnyakov
 * Date: Oct 21, 2009
 * Time: 1:59:16 PM
 */
public class FibonacciPivotIndicator implements IIndicator, IDrawingIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private ITimedData[][] inputs = new ITimedData[2][];
    private double[][] outputs = new double[7][];
    private InputParameterInfo childInput;
    private final GeneralPath generalPath = new GeneralPath();
    private DecimalFormat decimalFormat = new DecimalFormat("0.00000");
    private int[] outputXValues;
    private IIndicatorContext indicatorContext;
    
    private List<Period> periods = new ArrayList<Period>();

    public void onStart(IIndicatorContext context) {
    	this.indicatorContext = context;
        indicatorInfo = new IndicatorInfo("FIBPIVOT", "Fibonacci Pivot", "Overlap Studies", true, false, true, 2, 1, 7);
        indicatorInfo.setSparseIndicator(true);
        indicatorInfo.setRecalculateAll(true);
        childInput = new InputParameterInfo("Input data", InputParameterInfo.Type.BAR);
        childInput.setPeriod(Period.DAILY);
        childInput.setFilter(Filter.WEEKENDS);
        inputParameterInfos = new InputParameterInfo[]{new InputParameterInfo("Main Input data", InputParameterInfo.Type.BAR), childInput};

        for (Period p : Period.values()){
        	if (p.isTickBasedPeriod() || p.equals(Period.ONE_YEAR)){
        		continue;
        	}
        	periods.add(p);
        }
        optInputParameterInfos = new OptInputParameterInfo[]{new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, 
        		 new PeriodListDescription(Period.DAILY, periods.toArray(new Period[periods.size()])))};

        outputParameterInfos = new OutputParameterInfo[]{
        		new OutputParameterInfo("Central Point (P) ", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Resistance (R1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Support (S1)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Resistance (R2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Support (S2)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Resistance (R3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false) {
            {
                setDrawnByIndicator(true);
            }
        }, new OutputParameterInfo("Support (S3)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false) {
            {
                setDrawnByIndicator(true);
            }
        }};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        if(inputs[1] != null) {
            IBar prevBar = null;
            for (int i = 0; i < inputs[1].length; i++) {
                int timeIndex = getTimeIndex(inputs[1][i].getTime(), inputs[0]);

                if (prevBar != null && timeIndex > -1 &&
                        timeIndex >= startIndex && timeIndex <= endIndex) {
                    // P
                    double p = (prevBar.getClose() + prevBar.getHigh() + prevBar.getLow()) / 3;
                    outputs[0][timeIndex - startIndex] = p;
                    // R1
                    outputs[1][timeIndex - startIndex] = p + 0.382 * (prevBar.getHigh() - prevBar.getLow());
                    // S1
                    outputs[2][timeIndex - startIndex] = p - 0.382 * (prevBar.getHigh() - prevBar.getLow());
                    // R2
                    outputs[3][timeIndex - startIndex] = p + 0.618 * (prevBar.getHigh() - prevBar.getLow());
                    // S2
                    outputs[4][timeIndex - startIndex] = p - 0.618 * (prevBar.getHigh() - prevBar.getLow());
                    // R3
                    outputs[5][timeIndex - startIndex] = p + prevBar.getHigh() - prevBar.getLow();
                    // S3
                    outputs[6][timeIndex - startIndex] = p - (prevBar.getHigh() - prevBar.getLow());
                }
                if (timeIndex > -1) {
                    prevBar = (IBar) inputs[1][i];
                }
            }
        }

        fixOutput(outputs[0]);
        fixOutput(outputs[1]);
        fixOutput(outputs[2]);
        fixOutput(outputs[3]);
        fixOutput(outputs[4]);
        fixOutput(outputs[5]);
        fixOutput(outputs[6]);

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
    }

    private void fixOutput(double[] arr) {
        double last0 = 0;
        for(int i = 0; i < arr.length; i++) {
            if(Math.abs(arr[i]) < Double.MIN_VALUE) {
                arr[i] = last0;
            } else {
                last0 = arr[i];
            }
        }
    }

    public int getTimeIndex(long time, ITimedData[] source) {
    	if (source == null) {
    		return -1;
    	}

	    int curIndex = 0;
	    int upto = source.length;
	    
	    while (curIndex < upto) {
	        int midIndex = (curIndex + upto) / 2;
	        int nextToMidIndex = midIndex + 1;
	        
	        ITimedData midBar = source[midIndex];
            ITimedData nextToMidBar = nextToMidIndex >= 0 && nextToMidIndex < source.length ? source[nextToMidIndex] : null;
	        	       
	        if (midBar.getTime() == time) {
	        	return midIndex;
	        }
	        else if (nextToMidBar != null && midBar.getTime() < time && time <= nextToMidBar.getTime()){
	        	if (time == nextToMidBar.getTime()){
	        		return nextToMidIndex;
	        	}
	        	else {
	        		if (Math.abs(midBar.getTime() - time) < indicatorContext.getFeedDescriptor().getPeriod().getInterval()){
		        		return midIndex;
		        	}
		        	else {
		        		return nextToMidIndex;
		        	}
	        	}
	        }
        	else if (time < midBar.getTime()) {
	            upto = midIndex;
	        } 
	        else if (time > midBar.getTime()) {
	        	curIndex = midIndex + 1;
	        } 
	    }
	    
	    return -1;
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
        return 0;
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
        inputs[index] = (ITimedData[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
    	Period period = Period.DAILY;
    	if (value instanceof Integer){
    		period = mapToPredefPeriodOrdinal((Integer)value);
    	}
    	else {
			period = (Period)value;
    	}
    	
        boolean found = false;
        for (Period p : periods) {
            if (p.getInterval() == period.getInterval()) {
                found = true;
                break;
            }
        }
        if (!found) {
    		throw new IllegalArgumentException("Period not supported");
    	}
    	    	
    	childInput.setPeriod(period);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }


    @Override
    public Point drawOutput(
    		Graphics g, 
    		int outputIdx, 
    		Object values, 
    		Color color, 
    		Stroke stroke, 
    		IIndicatorDrawingSupport indicatorDrawingSupport, 
    		java.util.List<Shape> shapes, 
    		Map<Color, java.util.List<Point>> handles
    ) {
        if (values != null) {
        	double[] outputs = ((double[]) values);
        	
            Graphics2D g2 = (Graphics2D) g;
            generalPath.reset();

            g2.setColor(color);
            g2.setStroke(stroke);
            int fontSize = 9;
            g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), fontSize));

            Integer previousX = null;
            int maxWidth = indicatorDrawingSupport.getChartWidth() + 50;
            double lastValue = 0;            
            
            if (outputIdx == getFirstEnabledOutputIndex()){
            	outputXValues = new int[outputs.length];
            	Arrays.fill(outputXValues, -1);
            }
            for (int i = 0; i < outputs.length; i++) {                
            	double currentValue = ((double[]) values)[i];
                
            	if (Math.abs(currentValue) < Double.MIN_VALUE ) {
                    continue;
                }
            	
            	if (outputIdx == getFirstEnabledOutputIndex()){                 
                	outputXValues[i] = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                }
            	int x = (outputXValues == null || i > outputXValues.length - 1 || outputXValues[i] == -1) ? 
            			(int) indicatorDrawingSupport.getMiddleOfCandle(i) : outputXValues[i];             	                
            	
            	int y = (int) indicatorDrawingSupport.getYForValue(lastValue);

                if (previousX != null && (previousX >= 0 && previousX <= maxWidth || x >= 0 && x <= maxWidth) ) {                   
                	generalPath.moveTo(previousX.intValue(), y);
                	generalPath.lineTo(x, y);
                }
                previousX = x;               
                lastValue = currentValue;
            }
            if (previousX != null && lastValue != 0){
            	int y = (int) indicatorDrawingSupport.getYForValue(lastValue);
	            generalPath.moveTo(previousX.intValue(), y);
	            generalPath.lineTo(previousX, y);
	            
	            String valueStr = decimalFormat.format(lastValue);
	            String name = outputParameterInfos[outputIdx].getName();
	            String lineCode = name.substring(name.length() - 5, name.length());         
	            
	            g2.drawString(lineCode + " - " + valueStr, previousX, y - 4);
	            generalPath.lineTo(maxWidth, y);
            }
           
            g2.draw(generalPath);
            shapes.add((Shape)generalPath.clone());
        }
        return null;
    }
    
    public int getFirstEnabledOutputIndex(){
		for (int i = 0; i < getIndicatorInfo().getNumberOfOutputs(); i++){
			if (outputParameterInfos[i].isShowOutput()) {
				return i; 
			}
		}
		return -1;
	}
    private static Period mapToPredefPeriodOrdinal(int oldPeriod){
		Period res;
		
		switch (oldPeriod) {
		case 0:
			res = Period.ONE_MIN;
			break;
		case 1:
			res = Period.FIVE_MINS;
			break;
		case 2:
			res = Period.TEN_MINS;
			break;
		case 3:
			res = Period.FIFTEEN_MINS;
			break;
		case 4:
			res = Period.THIRTY_MINS;
			break;
		case 5:
			res = Period.ONE_HOUR;
			break;
		case 6:
			res = Period.FOUR_HOURS;
			break;
		case 7:
			res = Period.DAILY;
			break;
		case 8:
			res = Period.WEEKLY;
			break;
		case 9:
			res = Period.MONTHLY;
			break;

		default:
			res = Period.DAILY;
		}
		
		return res;
	}
}
