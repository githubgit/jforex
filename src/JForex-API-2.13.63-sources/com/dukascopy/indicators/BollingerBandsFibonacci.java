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

public class BollingerBandsFibonacci implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final Object[] inputs = new Object[2];
    private final double[][] outputs = new double[7][];

    private double factor1 = 1.618;
    private double factor2 = 2.618;
    private double factor3 = 4.236;

    private IIndicator ma;
    private IIndicator atr;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("BBANDSFIB", "Bollinger Bands - Fibonacci Ratios", "Overlap Studies", true, false, true, 2, 5, 7);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE),
            new InputParameterInfo("OHLC", InputParameterInfo.Type.PRICE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1)),
            new OptInputParameterInfo("MA type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Fibonacci Ratio 1", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(factor1, 1, 13, 0.01, 3)),
            new OptInputParameterInfo("Fibonacci Ratio 2", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(factor2, 1, 13, 0.01, 3)),
            new OptInputParameterInfo("Fibonacci Ratio 3", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(factor3, 1, 13, 0.01, 3))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Upper Band 3", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Upper Band 2", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Upper Band 1", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("Middle Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("Lower Band 1", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("Lower Band 2", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Lower Band 3", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setLineWidth(2);
        outputParameterInfos[0].setColor(DefaultColors.CORNFLOWER);
        outputParameterInfos[1].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[2].setColor(DefaultColors.GOLD);
        outputParameterInfos[3].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[4].setColor(DefaultColors.GOLD);
        outputParameterInfos[5].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[6].setLineWidth(2);
        outputParameterInfos[6].setColor(DefaultColors.CORNFLOWER);

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
           outputs[0][i] = maOutput[i] + factor3 * atrOutput[i];
           outputs[1][i] = maOutput[i] + factor2 * atrOutput[i];
           outputs[2][i] = maOutput[i] + factor1 * atrOutput[i];
           outputs[3][i] = maOutput[i];
           outputs[4][i] = maOutput[i] - factor1 * atrOutput[i];
           outputs[5][i] = maOutput[i] - factor2 * atrOutput[i];
           outputs[6][i] = maOutput[i] - factor3 * atrOutput[i];
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
            int timePeriod = (Integer) value;
            ma.setOptInputParameter(0, timePeriod);
            atr.setOptInputParameter(0, timePeriod);
            break;
        case 1:
            int maType = (Integer) value;
            ma.setOptInputParameter(1, maType);
            break;
        case 2:
            factor1 = (Double) value;
            break;
        case 3:
            factor2 = (Double) value;
            break;
        case 4:
            factor3 = (Double) value;
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
