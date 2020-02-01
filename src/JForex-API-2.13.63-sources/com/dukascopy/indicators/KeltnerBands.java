package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class KeltnerBands implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final Object[] inputs = new Object[2];
    private final double[][] outputs = new double[3][];

    private double multiplier = 2.5;

    private IIndicator ema;
    private IIndicator atr;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("KBANDS", "Keltner Bands (Based on ATR)", "Overlap Studies", true, false, true, 2, 3, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE),
            new InputParameterInfo("OHLC", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("EMA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1)),
            new OptInputParameterInfo("ATR Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(10, 2, 2000, 1)),
            new OptInputParameterInfo("ATR Multiple", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(multiplier, 1, 10, 0.1, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Upper Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Middle EMA", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("Lower Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.DEEP_MAGENTA);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[2].setColor(DefaultColors.DEEP_MAGENTA);

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ema = indicatorsProvider.getIndicator("EMA");
        atr = indicatorsProvider.getIndicator("ATR");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        ema.setInputParameter(0, inputs[0]);
        ema.setOutputParameter(0, outputs[1]);
        ema.calculate(startIndex, endIndex);

        double[] atrOutput = new double[endIndex - startIndex + 1];
        atr.setInputParameter(0, inputs[1]);
        atr.setOutputParameter(0, atrOutput);
        atr.calculate(startIndex, endIndex);

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
            outputs[0][i] = outputs[1][i] + atrOutput[i] * multiplier;
            outputs[2][i] = outputs[1][i] - atrOutput[i] * multiplier;
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
        inputs[index] = array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int emaPeriod = (Integer) value;
            ema.setOptInputParameter(0, emaPeriod);
            break;
        case 1:
            int atrPeriod = (Integer) value;
            atr.setOptInputParameter(0, atrPeriod);
            break;
        case 2:
            multiplier = (Double) value;
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return Math.max(ema.getLookback(), atr.getLookback());
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
