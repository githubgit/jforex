package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.*;

public class HeikinAshiSmooth implements IIndicator {
    
    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;

    private IndicatorInfo           indicatorInfo;

    // INPUTS
    private InputParameterInfo[]    inputParameterInfos;
    private double[][]              inPrices;

    // OPT INPUTS
    private OptInputParameterInfo[] optInputParameterInfos;
    private int                     maType = IIndicators.MaType.SMMA.ordinal();
    private int                     period = 6;
    private int                     maType2 = IIndicators.MaType.LWMA.ordinal();
    private int                     period2 = 2;

    // OUTPUTS
    private OutputParameterInfo[]   outputParameterInfos;
    private IBar[]                  resultBars;

    private IIndicator              ma;
    private IIndicator              ma2;

    @Override
    public void onStart(IIndicatorContext context) {

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        int[] maTypeValues = new int[IIndicators.MaType.values().length];
        String[] maTypeNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maTypeValues.length; i++) {
            maTypeValues[i] = i;
            maTypeNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("MA Type",
                    OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(maType, maTypeValues, maTypeNames)),
            new OptInputParameterInfo("MA Period",
                    OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(period, 2, 100, 1)),
            new OptInputParameterInfo("Secondary MA Type",
                    OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(maType2, maTypeValues, maTypeNames)),
            new OptInputParameterInfo("Secondary MA Period",
                    OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(period2, 2, 100, 1)),
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Heikin Ashi Candles Smoothed", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES)
        };

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[0].setColor2(DefaultColors.RED);

        indicatorInfo = new IndicatorInfo("HeikinAshiSmooth", "Heikin Ashi with smoothed indicator", "", true, false, true,
                inputParameterInfos.length, optInputParameterInfos.length, outputParameterInfos.length);

        ma = context.getIndicatorsProvider().getIndicator("MA");
        ma.setOptInputParameter(1, maType);
        ma2 = context.getIndicatorsProvider().getIndicator("MA");
        ma2.setOptInputParameter(1, maType2);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int len = endIndex - startIndex + 1;
        int extra = ma2.getLookback() + 1;
        double[][] maArr = new double[4][len + extra];

        ma.setInputParameter(0, inPrices[OPEN]);
        ma.setOutputParameter(0, maArr[OPEN]);
        ma.calculate(startIndex - extra, endIndex);

        ma.setInputParameter(0, inPrices[CLOSE]);
        ma.setOutputParameter(0, maArr[CLOSE]);
        ma.calculate(startIndex - extra, endIndex);

        ma.setInputParameter(0, inPrices[HIGH]);
        ma.setOutputParameter(0, maArr[HIGH]);
        ma.calculate(startIndex - extra, endIndex);

        ma.setInputParameter(0, inPrices[LOW]);
        ma.setOutputParameter(0, maArr[LOW]);
        ma.calculate(startIndex - extra, endIndex);

        double[][] heikinAshi = new double[4][maArr[0].length - 1];
        double[] haClose = new double[maArr[0].length];
        double[] haOpen = new double[maArr[0].length];

        haClose[0] = (maArr[OPEN][0] + maArr[CLOSE][0] + maArr[HIGH][0] + maArr[LOW][0]) / 4;
        haOpen[0] = haClose[0];

        for(int i = 1; i < maArr[0].length; i++) {
            haClose[i] = (maArr[OPEN][i] + maArr[CLOSE][i] + maArr[HIGH][i] + maArr[LOW][i]) / 4;
            haOpen[i] = (haClose[i - 1] + haOpen[i - 1]) / 2;

            heikinAshi[0][i - 1] = haOpen[i];
            heikinAshi[1][i - 1] = haClose[i];
            double hi = 0;
            double lo = 0;

            if(haClose[i] > haOpen[i]) {
                hi = haClose[i];
                lo = haOpen[i];
            } else {
                hi = haOpen[i];
                lo = haClose[i];
            }
            if(maArr[HIGH][i] > hi) {
                hi = maArr[HIGH][i];
            }
            if(maArr[LOW][i] < lo) {
                lo = maArr[LOW][i];
            }

            heikinAshi[2][i - 1] = hi;
            heikinAshi[3][i - 1] = lo;
        }

        double[][] resultDoubleArr = new double[4][resultBars.length];

        ma2.setInputParameter(0, heikinAshi[OPEN]);
        ma2.setOutputParameter(0, resultDoubleArr[OPEN]);
        ma2.calculate(0, heikinAshi[OPEN].length - 1);

        ma2.setInputParameter(0, heikinAshi[CLOSE]);
        ma2.setOutputParameter(0, resultDoubleArr[CLOSE]);
        ma2.calculate(0, heikinAshi[CLOSE].length - 1);

        ma2.setInputParameter(0, heikinAshi[HIGH]);
        ma2.setOutputParameter(0, resultDoubleArr[HIGH]);
        ma2.calculate(0, heikinAshi[HIGH].length - 1);

        ma2.setInputParameter(0, heikinAshi[LOW]);
        ma2.setOutputParameter(0, resultDoubleArr[LOW]);
        ma2.calculate(0, heikinAshi[HIGH].length - 1);

        for(int i = 0; i < resultBars.length; i++) {
            Bar bar = new Bar();
            bar.setOpen(resultDoubleArr[0][i]);
            bar.setClose(resultDoubleArr[1][i]);
            bar.setHigh(resultDoubleArr[2][i]);
            bar.setLow(resultDoubleArr[3][i]);
            resultBars[i] = bar;
        }

        return new IndicatorResult(startIndex, len);
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
        inPrices = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            maType = (Integer) value;
            ma.setOptInputParameter(1, maType);
            break;
        case 1:
            period = (Integer) value;
            ma.setOptInputParameter(0, period);
            break;
        case 2:
            maType2 = (Integer) value;
            ma2.setOptInputParameter(1, maType2);
            break;
        case 3:
            period2 = (Integer) value;
            ma2.setOptInputParameter(0, period2);
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        resultBars = (IBar[])array;
    }

    @Override
    public int getLookback() {
        return ma.getLookback() + ma2.getLookback() + 1;
    }

    @Override
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
