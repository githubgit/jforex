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

public class KurtosisIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private int period = 3;

    private IIndicator ma1;
    private IIndicator ma2;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("KURTOSIS", "Kurtosis", "", false, false, true, 1, 5, 1);

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
            new OptInputParameterInfo("Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(period, 2, 2000, 1)),
            new OptInputParameterInfo("Smoothing Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(66, 2, 2000, 1)),
            new OptInputParameterInfo("Smoothing Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.EMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Double Smoothing Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 2, 2000, 1)),
            new OptInputParameterInfo("Double Smoothing Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ma1 = indicatorsProvider.getIndicator("MA");
        ma1.setOptInputParameter(1, IIndicators.MaType.EMA.ordinal());
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

        double[] maInput = new double[endIndex - startIndex + 1 + ma1.getLookback() + ma2.getLookback()];
        double[] maOutput = new double[endIndex - startIndex + 1 + ma2.getLookback()];
        for (int i = startIndex - ma1.getLookback() - ma2.getLookback(), j = 0; i < endIndex; i++, j++) {
            maInput[j] = (inputs[0][i] - inputs[0][i - period]) - (inputs[0][i - 1] - inputs[0][i - 1 - period]);
        }

        ma1.setInputParameter(0, maInput);
        ma1.setOutputParameter(0, maOutput);
        ma1.calculate(ma1.getLookback(), maInput.length - 1);

        ma2.setInputParameter(0, maOutput);
        ma2.setOutputParameter(0, outputs[0]);
        ma2.calculate(ma2.getLookback(), maOutput.length - 1);

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
            period = (Integer) value;
            break;
        case 1:
            int maPeriod1 = (Integer) value;
            ma1.setOptInputParameter(0, maPeriod1);
            break;
        case 2:
            int maType1 = (Integer) value;
            ma1.setOptInputParameter(1, maType1);
            indicatorInfo.setUnstablePeriod(
                    ma1.getIndicatorInfo().isUnstablePeriod() ||
                    ma2.getIndicatorInfo().isUnstablePeriod());
            break;
        case 3:
            int maPeriod2 = (Integer) value;
            ma2.setOptInputParameter(0, maPeriod2);
            break;
        case 4:
            int maType2 = (Integer) value;
            ma2.setOptInputParameter(1, maType2);
            indicatorInfo.setUnstablePeriod(
                    ma1.getIndicatorInfo().isUnstablePeriod() ||
                    ma2.getIndicatorInfo().isUnstablePeriod());
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return period + 1 + ma1.getLookback() + ma2.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
