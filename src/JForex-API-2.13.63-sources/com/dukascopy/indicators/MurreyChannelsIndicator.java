package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
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
import com.dukascopy.api.indicators.PeriodListDescription;

/**
 * Created by: S.Vishnyakov
 * Date: Feb 23, 2010
 * Time: 10:44:05 AM
 */
public class MurreyChannelsIndicator implements IIndicator, IDrawingIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    
    private InputParameterInfo dailyInput;

    private final IBar[][] inputs = new IBar[1][];
    private final double[][] outputs = new double[13][];

    private int p = 90;
    private int stepBack = 0;

    private final GeneralPath generalPath = new GeneralPath();

    private final List<Period> periods = new ArrayList<>();
    
    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MURRCH", "Murrey Channels", "Momentum Indicators", true, false, false, 1, 3, 13);
        indicatorInfo.setSparseIndicator(true);
        indicatorInfo.setRecalculateAll(true);
        
        dailyInput = new InputParameterInfo("Input data", InputParameterInfo.Type.BAR);
        dailyInput.setPeriod(Period.DAILY);
        dailyInput.setFilter(Filter.WEEKENDS);
        
        inputParameterInfos = new InputParameterInfo[] {
            dailyInput
        };
        
        for (Period p : Period.values()){
        	if (p.isTickBasedPeriod() || p.equals(Period.ONE_YEAR)){
        		continue;
        	}
        	periods.add(p);
        }
        
        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("N Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(p, 1, 400, 1)),
            new OptInputParameterInfo("Candle Period", OptInputParameterInfo.Type.OTHER,
                new PeriodListDescription(Period.DAILY, periods.toArray(new Period[periods.size()]))),
            new OptInputParameterInfo("Step Back", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(stepBack, 0, 100, 1))
        };
        
        outputParameterInfos = new OutputParameterInfo[] {
        	createOutputParameter("[-2/8P]", DefaultColors.GRAY),
        	createOutputParameter("[-1/8P]", DefaultColors.GRAY),
        	createOutputParameter("[0/8P]", DefaultColors.CORNFLOWER),
        	createOutputParameter("[1/8P]", DefaultColors.YELLOW),
        	createOutputParameter("[2/8P]", DefaultColors.RED),
        	createOutputParameter("[3/8P]", DefaultColors.OLIVE_DRAB),
        	createOutputParameter("[4/8P]", DefaultColors.ROYAL_BLUE),
        	createOutputParameter("[5/8P]", DefaultColors.OLIVE_DRAB),
        	createOutputParameter("[6/8P]", DefaultColors.RED),
        	createOutputParameter("[7/8P]", DefaultColors.YELLOW),
        	createOutputParameter("[8/8P]", DefaultColors.CORNFLOWER),
        	createOutputParameter("[+1/8P]", DefaultColors.GRAY),
        	createOutputParameter("[+2/8P]", DefaultColors.GRAY)
        };
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        int step = stepBack;        
        if (stepBack >= p) {
            step = p - 2;
        }
        
        if (inputs[0] == null || inputs[0].length < stepBack + p){
        	return emptyResult(startIndex, endIndex);
        }
        
        int[] siei = identifyStartEndIndexes(p, step);
        int si = siei[0];
        int ei = siei[1];
        
        double[] lowHigh = findLowHigh(si, ei);
        double v1 = lowHigh[0];
        double v2 = lowHigh[1];
        
        double fractal = identifyFractal(v2);

        double range = v2 - v1;
        double sum = Math.floor(Math.log(fractal / range) / Math.log(2));
        double octave = fractal * Math.pow(0.5, sum);
        double mn = Math.floor(v1 / octave) * octave;
        
        double mx;
        if (mn + octave > v2) {
            mx = mn + octave;
        } else {
            mx = mn + 2 * octave;
        }

        double x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0, y6 = 0, finalH = 0, finalL = 0;

        //x2
        if ( (v1 >= (3 * (mx - mn) / 16 + mn)) && (v2 <= (9 * (mx - mn) / 16 + mn))){
        	x2 = mn + (mx - mn) / 2;
        }

        //x1
        if ((v1 >= (mn - (mx - mn) / 8)) && (v2 <= (5 * (mx - mn) / 8 + mn)) && (x2 == 0)){
        	x1 = mn + (mx - mn) / 2;
        }

        //x4
        if ((v1 >= (mn + 7 * (mx - mn) / 16)) && (v2 <= (13 * (mx - mn) / 16 + mn))){
        	x4 = mn + 3 * (mx - mn) / 4;
        }

        //x5
        if ((v1 >= (mn + 3 * (mx - mn) / 8)) && (v2 <= (9 * (mx - mn) / 8 + mn)) && (x4 == 0)){
        	x5 = mx;
        }

        //x3
        if ((v1 >= (mn + (mx - mn) / 8)) && (v2 <= (7 * (mx - mn) / 8 + mn)) && (x1 == 0) && (x2 == 0) && (x4 == 0) && (x5 == 0)){
        	x3 = mn + 3 * (mx - mn) / 4;
        }

        //x6
        if ((x1 + x2 + x3 + x4 + x5) == 0){
        	x6 = mx;
        }

        finalH = x1 + x2 + x3 + x4 + x5 + x6;

        //y1
        if (x1 > 0){
        	y1 = mn;
        }

        //y2
        if (x2 > 0){
        	y2 = mn + (mx - mn) / 4;
        }

        //y3
        if (x3 > 0){
        	y3 = mn + (mx - mn) / 4;
        }

        //y4
        if (x4 > 0){
        	y4 = mn + (mx - mn) / 2;
        }

        //y5
        if (x5 > 0){
        	y5 = mn + (mx - mn) / 2;
        }

        //y6
        if ((finalH > 0) && ((y1 + y2 + y3 + y4 + y5) == 0)){
        	y6 = mn;
        }

        finalL = y1 + y2 + y3 + y4 + y5 + y6;

        double dmml = (finalH - finalL) / 8;

        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            outputs[0][j] = (finalL - dmml * 2);
            for (int k = 1; k < indicatorInfo.getNumberOfOutputs(); k++){
            	outputs[k][j] = outputs[k - 1][j] + dmml;
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
        switch (index) {
        case 0:
            p = (Integer) value;
            break;
        case 1:
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
            dailyInput.setPeriod(period);
            break;
        case 2:
            stepBack = (Integer) value;
            break;
        default:
            throw new IllegalArgumentException("Invalid optional parameter index!");
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return p + stepBack;
    }

    @Override
    public int getLookforward() {
        return 0;
    }
    
    @Override
    public Point drawOutput(
    		Graphics g, 
    		int outputIdx, 
    		Object values,
            Color color, 
            Stroke stroke,
            IIndicatorDrawingSupport indicatorDrawingSupport,
            List<Shape> shapes, 
            Map<Color, List<Point>> handles
    ) {
    	if (values == null) {
            return null;
        }

        generalPath.reset();
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(stroke);             
        g2.setFont(new Font("Dialog Input", Font.PLAIN, 9));
        FontMetrics fontMetrics = g2.getFontMetrics();

        double[] output = (double[]) values;    	                                                                    
        if (output.length == 0){
            return null;
        }

        int x1 = (int)indicatorDrawingSupport.getMiddleOfCandle(indicatorDrawingSupport.getIndexOfFirstCandleOnScreen()) - 
            (int) (indicatorDrawingSupport.getCandleWidthInPixels() / 2);
        int y = (int)indicatorDrawingSupport.getYForValue(output[output.length - 1]);
        int x2 = indicatorDrawingSupport.getChartWidth();

        double roundedPrice = 
            Math.round(output[output.length - 1] * 
            Math.pow(10, indicatorDrawingSupport.getInstrument().getTickScale())) / 
            Math.pow(10, indicatorDrawingSupport.getInstrument().getTickScale());                                                                                          

        String label = outputParameterInfos[outputIdx].getName() + ": " + Double.toString(roundedPrice);
        int width = fontMetrics.stringWidth(label);
        int height = fontMetrics.getHeight();                                                                                            
        Rectangle rect = new Rectangle(x2 - width - 20 - 2, y - fontMetrics.getHeight() / 2, width, height);

        generalPath.moveTo(x1, y);
        generalPath.lineTo(rect.x, y);           
        generalPath.moveTo(rect.x + rect.width, y);
        generalPath.lineTo(x2, y);
        g2.drawString(label, rect.x, rect.y + rect.height - 3);
        g2.draw(generalPath);            
        shapes.add((Shape)generalPath.clone());

        return null;
    }
    
    protected OutputParameterInfo createOutputParameter(String name, Color color){
    	OutputParameterInfo param = 
    		new OutputParameterInfo(name, OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE); 
        param.setColor(color);
        param.setDrawnByIndicator(true);
        
        return param;
    }
    
    protected IndicatorResult emptyResult(int si, int ei){
    	return new IndicatorResult(si, ei - si + 1);
    }
    
    public double[] findLowHigh(int si, int ei){
    	double low = inputs[0][si].getLow();
    	double high = inputs[0][si].getHigh();
         
        for (int i = si + 1; i <= ei; i++) {
         	IBar bar = inputs[0][i];
         	if (low > bar.getLow()) {
                 low = bar.getLow();
            } 
            if (high < bar.getHigh()) {
                high = bar.getHigh();
            }            
    	}
         
        return new double[]{low, high};
    }
    
    public double identifyFractal(double v2){
    	double fractal = 0;
    	
    	if (v2 <= 250000 && v2 > 25000){
    		fractal = 100000;
    	}
    	else if (v2 <= 25000 && v2 > 2500){
    		fractal = 10000;
    	}
    	else if (v2 <= 2500 && v2 > 250){
    		fractal = 1000;
    	}
    	else if (v2 <= 250 && v2 > 25){
    		fractal = 100;
    	}
    	else if (v2 <= 25 && v2 > 12.5){
    		fractal = 12.5;
    	}
    	else if (v2 <= 12.5 && v2 > 6.25){
    		fractal = 12.5;
    	}
    	else if (v2 <= 6.25 && v2 > 3.125){
    		fractal = 6.25;
    	}
    	else if (v2 <= 3.125 && v2 > 1.5625){
    		fractal = 3.125;
    	}
    	else if (v2 <= 1.5625 && v2 > 0.390625){
    		fractal = 1.5625;
    	}
    	else if (v2 <= 0.390625 && v2 > 0){
    		fractal = 0.1953125;
    	}
    	
    	return fractal;
    }
    
    public int[] identifyStartEndIndexes(int p, int stepBack){
    	int ei = inputs[0].length - 1 - stepBack; 
    	int si = inputs[0].length - stepBack - p;
         
        return new int[]{si, ei};
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
