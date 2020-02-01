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

public class MassIndexIndicator implements IIndicator {

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
    private IIndicator sum;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MASS_INDEX", "Mass Index", "", false, false, true, 1, 3, 1);

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
            new OptInputParameterInfo("MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(9, 2, 2000, 1)),
            new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.EMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Summation Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(25, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 27, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.DARK_RED, 1, 1),
            new LevelInfo("", 26.5, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ma = indicatorsProvider.getIndicator("MA");
        ma.setOptInputParameter(1, IIndicators.MaType.EMA.ordinal());
        sum = indicatorsProvider.getIndicator("SUM");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] maInput = new double[endIndex - startIndex + 1 + getLookback()];
        double[][] maOutputs = new double[2][];
        maOutputs[0] = new double[endIndex - startIndex + 1 + ma.getLookback() + sum.getLookback()];
        maOutputs[1] = new double[endIndex - startIndex + 1 + sum.getLookback()];
        for (int i = startIndex - getLookback(), j = 0; i <= endIndex; i++, j++) {
            maInput[j] = inputs[0][HIGH][i] - inputs[0][LOW][i];
        }

        ma.setInputParameter(0, maInput);
        ma.setOutputParameter(0, maOutputs[0]);
        ma.calculate(ma.getLookback(), maInput.length - 1);

        ma.setInputParameter(0, maOutputs[0]);
        ma.setOutputParameter(0, maOutputs[1]);
        ma.calculate(ma.getLookback(), maOutputs[0].length - 1);

        double[] sumInput = new double[endIndex - startIndex + 1 + sum.getLookback()];
        for (int i = 0; i < sumInput.length; i++) {
            sumInput[i] = maOutputs[0][i + ma.getLookback()] / maOutputs[1][i];
        }

        sum.setInputParameter(0, sumInput);
        sum.setOutputParameter(0, outputs[0]);
        sum.calculate(sum.getLookback(), sumInput.length - 1);

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
            int maPeriod = (Integer) value;
            ma.setOptInputParameter(0, maPeriod);
            break;
        case 1:
            int maType = (Integer) value;
            ma.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(ma.getIndicatorInfo().isUnstablePeriod());
            break;
        case 2:
            int sumPeriod = (Integer) value;
            sum.setOptInputParameter(0, sumPeriod);
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
        return 2 * ma.getLookback() + sum.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
