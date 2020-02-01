package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class StollerAverageRangeChannels implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final Object[] inputs = new Object[2];
    private final double[][] outputs = new double[3][];

    private double multiplier = 2;

    private IIndicator ma;
    private IIndicator atr;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("STARC_BANDS", "Stoller Average Range Channels (STARC Bands)", "Overlap Studies", true, false, true, 2, 4, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE),
            new InputParameterInfo("OHLC", InputParameterInfo.Type.PRICE)
        };

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.TYPICAL_PRICE);

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("ATR Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(15, 2, 2000, 1)),
            new OptInputParameterInfo("ATR Multiplier", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(multiplier, 0.1, 10, 0.1, 1)),
            new OptInputParameterInfo("STARC Bands Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(6, 2, 2000, 1)),
            new OptInputParameterInfo("STARC Bands Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Upper Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Middle Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("Lower Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.DEEP_MAGENTA);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[2].setColor(DefaultColors.DEEP_MAGENTA);

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ma = indicatorsProvider.getIndicator("MA");
        ma.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
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

        double[] maOutput = new double[endIndex - startIndex + 1];
        ma.setInputParameter(0, inputs[0]);
        ma.setOutputParameter(0, maOutput);
        ma.calculate(startIndex, endIndex);

        double[] atrOutput = new double[endIndex - startIndex + 1];
        atr.setInputParameter(0, inputs[1]);
        atr.setOutputParameter(0, atrOutput);
        atr.calculate(startIndex, endIndex);

        for (int i = 0; i < endIndex - startIndex + 1; i++){
           outputs[0][i] = maOutput[i] + multiplier * atrOutput[i];
           outputs[1][i] = maOutput[i];
           outputs[2][i] = maOutput[i] - multiplier * atrOutput[i];
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
            int atrPeriod = (Integer) value;
            atr.setOptInputParameter(0, atrPeriod);
            break;
        case 1:
            multiplier = (Double) value;
            break;
        case 2:
            int maPeriod = (Integer) value;
            ma.setOptInputParameter(0, maPeriod);
            break;
        case 3:
            int maType = (Integer) value;
            ma.setOptInputParameter(1, maType);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return Math.max(ma.getLookback(), atr.getLookback());
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
