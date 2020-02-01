package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class HeikinAshiSingle implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][][] inputs = new double[1][][];
    private IBar[] candles;
    
    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("HeikinAshiSingle", "Heikin Ashi with a single candle output", "", true, false, true, 1, 0, 1);

        inputParameterInfos = new InputParameterInfo[]{
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};

        outputParameterInfos = new OutputParameterInfo[]{
    		new OutputParameterInfo("Heikin Ashi Candles", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES)
		};

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[0].setColor2(DefaultColors.RED);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] prices = inputs[0];
        Bar firstHACandle = new Bar();
        
        firstHACandle.setOpen((prices[OPEN][startIndex - 1] + prices[CLOSE][startIndex - 1]) / 2);
        firstHACandle.setClose((prices[OPEN][startIndex - 1] + prices[CLOSE][startIndex - 1] + prices[HIGH][startIndex - 1] + prices[LOW][startIndex - 1]) / 4);
        firstHACandle.setHigh(Math.max(Math.max(prices[HIGH][startIndex - 1], firstHACandle.getOpen()), firstHACandle.getClose()));
        firstHACandle.setLow(Math.min(Math.min(prices[LOW][startIndex - 1], firstHACandle.getOpen()), firstHACandle.getClose()));
        
        int resIndex = 0;
        for (int i = startIndex; i <= endIndex; i++, resIndex++) {
        	IBar prevCandle = i == startIndex ? firstHACandle : candles[resIndex - 1];        	        	
        	Bar newHACandle = new Bar();
        	
        	newHACandle.setOpen((prevCandle.getOpen() + prevCandle.getClose()) / 2);
			newHACandle.setClose((prices[OPEN][i] + prices[CLOSE][i] + prices[HIGH][i] + prices[LOW][i]) / 4);
			newHACandle.setHigh(Math.max(Math.max(prices[HIGH][i], newHACandle.getOpen()), newHACandle.getClose()));
			newHACandle.setLow(Math.min(Math.min(prices[LOW][i], newHACandle.getOpen()), newHACandle.getClose()));
			
            candles[resIndex] = newHACandle;
        }

        return new IndicatorResult(startIndex, resIndex);
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
    }

    public void setOutputParameter(int index, Object array) {
    	candles = (IBar[]) array;
    }

    public int getLookback() {
        return 1;
    }

    public int getLookforward() {
        return 0;
    }


    private static class Bar implements IBar {
    	
    	private double open;
		private double close;
    	private double high;
    	private double low;

    	public Bar(){}
    	
    	public Bar(
    	    double open,
    	    double close,
    	    double high,
    	    double low
    	){
    		this.open = open;
    		this.close = close;
    		this.high = high;
    		this.low = low;
    	}
    	
    	public void setOpen(double open) {
			this.open = open;
		}

		public void setClose(double close) {
			this.close = close;
		}

		public void setHigh(double high) {
			this.high = high;
		}

		public void setLow(double low) {
			this.low = low;
		}
    	
		@Override
		public long getTime() {
			return 0;
		}

		@Override
		public double getOpen() {
			return open;
		}

		@Override
		public double getClose() {
			return close;
		}

		@Override
		public double getLow() {
			return low;
		}

		@Override
		public double getHigh() {
			return high;
		}

		@Override
		public double getVolume() {
			return Double.NaN;
		}

        @Override
        public String toString() {
            String result = "O: "+ open+ " C: "+ close+ " H: "+ high+ " L: "+ low;
            return result;
        }
    }
}
