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
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class RainbowChartsIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[10][];

    private IIndicator ma1;
    private IIndicator ma2;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("RAINBOW", "Rainbow Charts", "", true, false, false, 1, 4, 10);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(2, 2, 2000, 1)),
            new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Recursive Smoothing Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(2, 2, 2000, 1)),
            new OptInputParameterInfo("Recursive Smoothing MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("MA - Least Smooth", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R1", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R2", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R3", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R4", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R5", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R6", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R7", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R8", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("R9 - Smoothest", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.RED);
        outputParameterInfos[1].setColor(DefaultColors.DARK_ORANGE);
        outputParameterInfos[2].setColor(DefaultColors.GOLD);
        outputParameterInfos[3].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[4].setColor(DefaultColors.FOREST_GREEN);
        outputParameterInfos[5].setColor(DefaultColors.AQUA_FOREST);
        outputParameterInfos[6].setColor(DefaultColors.STEEL_BLUE);
        outputParameterInfos[7].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[8].setColor(DefaultColors.NEON_BLUE);
        outputParameterInfos[9].setColor(DefaultColors.DEEP_MAGENTA);

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ma1 = indicatorsProvider.getIndicator("MA");
        ma1.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
        ma2 = indicatorsProvider.getIndicator("MA");
        ma2.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] maOutputs = new double[10][];
        maOutputs[0] = new double[endIndex - startIndex + 1 + 9 * ma2.getLookback()];

        ma1.setInputParameter(0, inputs[0]);
        ma1.setOutputParameter(0, maOutputs[0]);
        ma1.calculate(startIndex - 9 * ma2.getLookback(), endIndex);

        System.arraycopy(maOutputs[0], 9 * ma2.getLookback(), outputs[0], 0, endIndex - startIndex + 1);

        for (int i = 1; i < 10; i++) {
            maOutputs[i] = new double[endIndex - startIndex + 1 + (9 - i) * ma2.getLookback()];

            ma2.setInputParameter(0, maOutputs[i - 1]);
            ma2.setOutputParameter(0, maOutputs[i]);
            ma2.calculate(ma2.getLookback(), maOutputs[i - 1].length - 1);

            System.arraycopy(maOutputs[i], (9 - i) * ma2.getLookback(), outputs[i], 0, endIndex - startIndex + 1);
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
        inputs[index] = (double[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int maPeriod1 = (Integer) value;
            ma1.setOptInputParameter(0, maPeriod1);
            break;
        case 1:
            int maType1 = (Integer) value;
            ma1.setOptInputParameter(1, maType1);
            indicatorInfo.setUnstablePeriod(
                    ma1.getIndicatorInfo().isUnstablePeriod() ||
                    ma2.getIndicatorInfo().isUnstablePeriod());
            break;
        case 2:
            int maPeriod2 = (Integer) value;
            ma2.setOptInputParameter(0, maPeriod2);
            break;
        case 3:
            int maType2 = (Integer) value;
            ma2.setOptInputParameter(1, maType2);
            indicatorInfo.setUnstablePeriod(
                    ma1.getIndicatorInfo().isUnstablePeriod() ||
                    ma2.getIndicatorInfo().isUnstablePeriod());
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
        return ma1.getLookback() + 9 * ma2.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
