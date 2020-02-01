package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.*;

public class HiLowIndicator implements IIndicator, IDrawingIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    
    private final IBar[][] inputs = new IBar[2][];
    private final double[][] outputs = new double[3][];
    private final Object[][] objOutput = new Object[1][];

    private Units unit = Units.HOURS;
    private int startingHour = 0;
    private int length;

    private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));


    public enum Units {
    	HOURS(24, Period.ONE_HOUR, Calendar.HOUR_OF_DAY), 
    	DAYS(3, Period.DAILY, Calendar.DAY_OF_MONTH), 
    	WEEKS(1, Period.WEEKLY, Calendar.WEEK_OF_MONTH), 
    	MONTHS(1, Period.MONTHLY, Calendar.MONTH);
    	
    	private final int max;
    	private final Period period;
    	private final int calField;

    	private Units(int max, Period period, int calField){
    		this.max = max;
    		this.period = period;
    		this.calField = calField;
    	}

    	public int getMax(){
    		return this.max;
    	}

    	public Period getPeriod(){
    		return this.period;
    	}

    	public int getCalendarField(){
    		return this.calField;
    	}

    	public static int[] indexes = new int[Units.values().length];
    	public static String[] names = new String[Units.values().length];

    	static {    		
    		for (Units u : Units.values()){
    			indexes[u.ordinal()] = u.ordinal();
    			names[u.ordinal()] = u.toString(); 
    		}
    	}
    };
    
    public class HiLow{
    	private final int startIndex;
		private final int endIndex;
    	private final double high;
    	private final double low;
    	
    	public HiLow(int si, int ei, double h, double l){
    		startIndex = si;
    		endIndex = ei;
    		high = h;
    		low = l;
    	}
    	
    	public int getStartIndex() {
			return startIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}

		public double getHigh() {
			return high;
		}

		public double getLow() {
			return low;
		}
    }

    
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("HILO", "Displays highs and lows for a given period starting at the specified hour", "", true, false, false, 2, 3, 4);
        indicatorInfo.setSparseIndicator(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Chart data", InputParameterInfo.Type.BAR),
            new InputParameterInfo("Hourly data", InputParameterInfo.Type.BAR)
        };

        inputParameterInfos[1].setPeriod(Units.HOURS.getPeriod());

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Starting hour", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(startingHour, 0, Units.HOURS.getMax(), 1)),
            new OptInputParameterInfo("Units", OptInputParameterInfo.Type.OTHER,
                new IntegerListDescription(unit.ordinal(), Units.indexes, Units.names)),
            new OptInputParameterInfo("Length", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(1, 1, Units.HOURS.getMax(), 1))
        };
        
        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("High", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE),
            new OutputParameterInfo("Low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE),
            new OutputParameterInfo("Separator", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE),
            new OutputParameterInfo("", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.NONE)
        };

        outputParameterInfos[0].setColor(DefaultColors.GRAY);
        outputParameterInfos[0].setDrawnByIndicator(true);
        outputParameterInfos[1].setColor(DefaultColors.GRAY);
        outputParameterInfos[1].setDrawnByIndicator(true);
        outputParameterInfos[2].setColor(DefaultColors.GRAY);
        outputParameterInfos[2].setDrawnByIndicator(true);
        outputParameterInfos[3].setDrawnByIndicator(true);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
    	
    	if (length > unit.getMax()){
    		length = unit.getMax();
    	}
    	
    	long t2Prev = 0;
        for (int i = 0, j = 0; i < inputs[1].length; i++, j++){
        	
        	IBar bar = inputs[1][i];
        	
        	if (bar.getTime() < t2Prev){
        		continue;
        	}
        	
        	calendar.setTimeInMillis(bar.getTime());
        	
        	if (calendar.get(Calendar.HOUR_OF_DAY) != startingHour){
        		continue;
        	}
        	
        	long t1 = calendar.getTimeInMillis();        	
        	calendar.add(unit.getCalendarField(), length);	

        	long t2 = calendar.getTimeInMillis();
        	
        	int index1 = getTimeIndex(t1, inputs[0]);        	
        	if (index1 < 0){
        		continue;
        	}
        	
        	int index2 = getTimeIndex(t2, inputs[0]);        	        	
        	if (index2 < 0){
        		continue;
        	}
        	
        	t2Prev = t2;
        	
        	double highest = inputs[0][index1].getHigh();
        	double lowest = inputs[0][index1].getLow(); 
        	for (int k = index1 + 1; k <= index2; k++){
        		IBar current = inputs[0][k];
        		if (current.getHigh() > highest){
        			highest = current.getHigh(); 
        		}
        		if (current.getLow() < lowest){
        			lowest = current.getLow();
        		}
        	}
        	
        	outputs[0][index1] = highest;
        	outputs[1][index1] = lowest;         	   
        	objOutput[0][index1] = new HiLow(index1, index2, highest, lowest); 
        }
       
        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
    }
    
    public int getTimeIndex(long time, IBar[] source) {
    	if (source == null) {
    		return -1;
    	}

	    int curIndex = 0;
	    int sourceLength = source.length;
	    int upto = sourceLength;
	    
	    while (curIndex < upto) {
	        int midIndex = (curIndex + upto) / 2;
	        
	        IBar midBar = source[midIndex];
	        
	        if (midBar.getTime() == time) {
	        	return midIndex;
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
        inputs[index] = (IBar[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        if (index == 0){
        	startingHour = (Integer)value;
        }
        else if (index == 1){
        	unit = Units.values()[(Integer) value];
//        	inputParameterInfos[0].setPeriod(unit.getPeriod());
        }
        else if (index == 2){
        	length = (Integer)value;
        }
    }

    public void setOutputParameter(int index, Object array) {
    	if (index == 3){
    		objOutput[0] = (Object[]) array;
    	}
    	else {
    		outputs[index] = (double[]) array;
    	}
    }

    public int getLookback() {
        return 0;
    }

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
			List<Shape> shapes, Map<Color, List<Point>> handles) 
	{
		if (outputIdx != 3){
			return null;
		}
		
		Graphics2D g2 = (Graphics2D) g;
		GeneralPath p = new GeneralPath();
		
		Color separatorClr = outputParameterInfos[2].getColor();
		Color highClr = outputParameterInfos[0].getColor();
		Color lowClr = outputParameterInfos[1].getColor();
		
		int chartHeight = indicatorDrawingSupport.getChartHeight();
		
		Object[] outputValues = (Object[]) values;
						
		int firstCandleIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen(); 
		int si = startIndex(firstCandleIndex, outputValues);
		
		int candlesSkipped = candlesSkipped(firstCandleIndex, si);
		int num = numProcess(indicatorDrawingSupport.getNumberOfCandlesOnScreen(), candlesSkipped, outputValues.length);
			
		for (int i = si, j = 0; j < num; i++, j++) {  
			
			if (outputValues[i] == null){
				continue;
			}
			
			HiLow hilo = (HiLow)outputValues[i];
			int left = (int) indicatorDrawingSupport.getMiddleOfCandle(hilo.startIndex);
			int right = (int) indicatorDrawingSupport.getMiddleOfCandle(hilo.endIndex);
			float y1 = indicatorDrawingSupport.getYForValue(hilo.low);			
			float y2 = indicatorDrawingSupport.getYForValue(hilo.high);			
			
			 
			p.moveTo(left, 0);	
			p.lineTo(left, chartHeight);	
			p.moveTo(right, 0);
			p.lineTo(right, chartHeight);	
			draw(p, separatorClr, g2, shapes);
						
			p.moveTo(left, y1);
			p.lineTo(right, y1);
			draw(p, lowClr, g2, shapes);
			
			p.moveTo(left, y2);
			p.lineTo(right, y2);
			draw(p, highClr, g2, shapes);
		}
		
		return null;
	}
	
	public int startIndex(int si, Object[] outputValues){	
		int res = 0;
		if (si > outputValues.length - 1){
			return res;
		}
		
		if (outputValues[si] != null){
			return si;
		}
		
		while (si > 0){	
				si--;
				if (outputValues[si] != null){
					res = si;
					break;
				}
		}
		
		return res;
	}
	
	public int numProcess(int candlesOnScreen, int candlesSkipped, int valuesTotal){
		int res = candlesOnScreen + candlesSkipped;
		if (res >= valuesTotal){
			res = valuesTotal - 1;
		}
		
		return res;
	}
	
	public int candlesSkipped(int previ, int curi){		
		int res;
		if (previ == curi){
			res = 0;
		}
		else {
			res = previ - curi;
		}
		
		return res;
	}
	
	private void draw(GeneralPath p, Color clr, Graphics2D g2d, List<Shape> shapes){		
		g2d.setColor(clr);
		g2d.draw(p);		
		shapes.add((Shape)p.clone());
		p.reset();
	}
}
