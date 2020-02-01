package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class InertiaIndicator implements IIndicator {

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[1][];

    private IIndicator stddev;
    private IIndicator ema;
    private IIndicator ma;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("INERTIA", "Inertia", "", false, false, true, 1, 2, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Smoothing Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1)),
            new OptInputParameterInfo("Smoothing Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 50, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        stddev = indicatorsProvider.getIndicator("STDDEV");
        ema = indicatorsProvider.getIndicator("EMA");
        ma = indicatorsProvider.getIndicator("MA");
        ma.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] stddevOutputs = new double[2][endIndex - startIndex + 1 + ema.getLookback() + ma.getLookback()];
        stddev.setInputParameter(0, inputs[0][HIGH]);
        stddev.setOutputParameter(0, stddevOutputs[0]);
        stddev.calculate(startIndex - ema.getLookback() - ma.getLookback(), endIndex);

        stddev.setInputParameter(0, inputs[0][LOW]);
        stddev.setOutputParameter(0, stddevOutputs[1]);
        stddev.calculate(startIndex - ema.getLookback() - ma.getLookback(), endIndex);

        double[][] emaInputs = new double[4][stddevOutputs[0].length];
        double[][] emaOutputs = new double[4][endIndex - startIndex + 1 + ma.getLookback()];
        for (int i = startIndex - ema.getLookback() - ma.getLookback(), j = 0; i <= endIndex; i++, j++) {
            emaInputs[0][j] = (inputs[0][HIGH][i] > inputs[0][HIGH][i - 1] ? stddevOutputs[0][j] : 0);
            emaInputs[1][j] = (inputs[0][HIGH][i] < inputs[0][HIGH][i - 1] ? stddevOutputs[0][j] : 0);
            emaInputs[2][j] = (inputs[0][LOW][i] > inputs[0][LOW][i - 1] ? stddevOutputs[1][j] : 0);
            emaInputs[3][j] = (inputs[0][LOW][i] < inputs[0][LOW][i - 1] ? stddevOutputs[1][j] : 0);
        }

        for (int i = 0; i < emaInputs.length; i++) {
            ema.setInputParameter(0, emaInputs[i]);
            ema.setOutputParameter(0, emaOutputs[i]);
            ema.calculate(ema.getLookback(), emaInputs[i].length - 1);
        }

        double[] maInput = new double[emaOutputs[0].length];
        for (int i = 0; i < maInput.length; i++) {
            maInput[i] = (100 * emaOutputs[0][i] / (emaOutputs[0][i] + emaOutputs[1][i]) +
                    100 * emaOutputs[2][i] / (emaOutputs[2][i] + emaOutputs[3][i])) / 2;
        }

        ma.setInputParameter(0, maInput);
        ma.setOutputParameter(0, outputs[0]);
        ma.calculate(ma.getLookback(), maInput.length - 1);

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
        inputs[index] = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int timePeriod = (Integer) value;
            ma.setOptInputParameter(0, timePeriod);
            stddev.setOptInputParameter(0, 10);
            ema.setOptInputParameter(0, 14);
            break;
        case 1:
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
        return stddev.getLookback() + ema.getLookback() + ma.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
