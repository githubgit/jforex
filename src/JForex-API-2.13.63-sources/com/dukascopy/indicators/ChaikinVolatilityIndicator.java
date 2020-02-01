package com.dukascopy.indicators;

import com.dukascopy.api.indicators.*;

public class ChaikinVolatilityIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];
    private int period1 = 10;
    private int period2 = 10;

    private IIndicator ema;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("CHAIKIN_VOLATILITY", "Chaikin's Volatility", "Volatility Indicators", false, false, true, 1, 2, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE),
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("High-Low Average Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(period1, 2, 2000, 1)),
            new OptInputParameterInfo("Rate of Change Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(period2, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Volatility", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, true)
        };

        ema = context.getIndicatorsProvider().getIndicator("EMA");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] inputs1 = new double[endIndex - startIndex + 1 + period1 - 1];
        double[] outputs1 = new double[endIndex - startIndex + 1];
        for (int i = startIndex - period1 + 1, j = 0; i <= endIndex; i++, j++) {
            inputs1[j] = inputs[0][HIGH][i] - inputs[0][LOW][i];
        }

        ema.setInputParameter(0, inputs1);
        ema.setOptInputParameter(0, period1);
        ema.setOutputParameter(0, outputs1);
        ema.calculate(period1 - 1, inputs1.length - 1);

        double[] inputs2 = new double[endIndex - startIndex + 1 + period1 - 1];
        double[] outputs2 = new double[endIndex - startIndex + 1];
        for (int i = startIndex - period2 - period1 + 1, j = 0; i <= endIndex - period2; i++, j++) {
            inputs2[j] = inputs[0][HIGH][i] - inputs[0][LOW][i];
        }

        ema.setInputParameter(0, inputs2);
        ema.setOptInputParameter(0, period1);
        ema.setOutputParameter(0, outputs2);
        ema.calculate(period1 - 1, inputs2.length - 1);

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
            outputs[0][i] = (outputs1[i] - outputs2[i]) / outputs2[i] * 100;
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
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
        inputs[index] = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            period1 = (Integer) value;
            break;
        case 1:
            period2 = (Integer) value;
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return period1 - 1 + period2;
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
