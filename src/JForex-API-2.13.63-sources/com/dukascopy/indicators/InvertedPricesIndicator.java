package com.dukascopy.indicators;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class InvertedPricesIndicator implements IIndicator {
    private IIndicatorContext context;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final IBar[][] outputs = new IBar[1][];

    @Override
    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("INV_PRICES", "Inverted prices", "", false, false, false, 1, 0, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Prices", InputParameterInfo.Type.BAR),
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Inverted prices", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setColor2(DefaultColors.RED);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        Instrument instrument = context.getFeedDescriptor().getInstrument();
        int scale = instrument.getTickScale();
        int precision = (int) Math.round(Math.log10(inputs[0][startIndex].getHigh()));
        scale = Math.max(scale + 2 * precision, 0);

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            long time     = inputs[0][i].getTime();
            double open   = round(1 / inputs[0][i].getOpen(), scale);
            double high   = round(1 / inputs[0][i].getLow(), scale);
            double low    = round(1 / inputs[0][i].getHigh(), scale);
            double close  = round(1 / inputs[0][i].getClose(), scale);
            double volume = inputs[0][i].getVolume();
            outputs[0][i] = new Candle(time, open, high, low, close, volume);
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
    }

    private double round(double amount, int decimalPlaces) {
        return new BigDecimal(amount).setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP).doubleValue();
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
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (IBar[]) array;
    }

    @Override
    public int getLookback() {
        return 0;
    }

    @Override
    public int getLookforward() {
        return 0;
    }


    private static class Candle implements IBar {
        private final long time;
        private final double open;
        private final double high;
        private final double low;
        private final double close;
        private final double volume;

        public Candle(long time, double open, double high, double low, double close, double volume) {
            this.time = time;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }

        @Override
        public long getTime() {
            return time;
        }

        @Override
        public double getOpen() {
            return open;
        }

        @Override
        public double getHigh() {
            return high;
        }

        @Override
        public double getLow() {
            return low;
        }

        @Override
        public double getClose() {
            return close;
        }

        @Override
        public double getVolume() {
            return volume;
        }

        @Override
        public String toString() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            StringBuilder str = new StringBuilder();
            str.append(format.format(time));
            str.append(" O: ").append(open);
            str.append(" H: ").append(high);
            str.append(" L: ").append(low);
            str.append(" C: ").append(close);
            str.append(" V: ").append(volume);

            return str.toString();
        }
    }
}
