package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class GannHiLoActivator implements IIndicator {

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

    private IIndicator ma;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("GANN_HILO", "Gann HiLo Activator", "", true, false, false, 1, 2, 1);

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
            new OptInputParameterInfo("Lookback Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 2, 2000, 1)),
            new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        ma = context.getIndicatorsProvider().getIndicator("MA");
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

        double[][] maOutputs = new double[2][endIndex - startIndex + 1 + 1];
        ma.setInputParameter(0, inputs[0][HIGH]);
        ma.setOutputParameter(0, maOutputs[0]);
        ma.calculate(startIndex - 1, endIndex);

        ma.setInputParameter(0, inputs[0][LOW]);
        ma.setOutputParameter(0, maOutputs[1]);
        ma.calculate(startIndex - 1, endIndex);

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            outputs[0][j] = (inputs[0][CLOSE][i] < maOutputs[1][j] ? maOutputs[0][j + 1] : maOutputs[1][j + 1]);
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
        inputs[index] = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int period = (Integer) value;
            ma.setOptInputParameter(0, period);
            break;
        case 1:
            int maType = (Integer) value;
            ma.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(ma.getIndicatorInfo().isUnstablePeriod());
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
        return ma.getLookback() + 1;
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
