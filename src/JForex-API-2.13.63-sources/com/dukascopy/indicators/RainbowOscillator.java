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

public class RainbowOscillator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private int oscPeriod = 10;

    private IIndicator rainbow;
    private IIndicator max;
    private IIndicator min;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("RAINBOW_OSC", "Rainbow Oscillator", "", false, false, false, 1, 5, 3);

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
            new OptInputParameterInfo("Rainbow Charts Periods", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(2, 2, 2000, 1)),
            new OptInputParameterInfo("Rainbow Charts MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Rainbow Charts Recursive Smoothing Periods", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(2, 2, 2000, 1)),
            new OptInputParameterInfo("Rainbow Charts Recursive Smoothing MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Rainbow Oscillator Periods", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(oscPeriod, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("RB Oscillator", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("RB Lower Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("RB Upper Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[1].setColor(DefaultColors.STEEL_BLUE);
        outputParameterInfos[2].setColor(DefaultColors.STEEL_BLUE);

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 30, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", -30, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        rainbow = indicatorsProvider.getIndicator("RAINBOW");
        max = indicatorsProvider.getIndicator("MAX");
        min = indicatorsProvider.getIndicator("MIN");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] rainbowOutputs = new double[10][endIndex - startIndex + 1];
        rainbow.setInputParameter(0, inputs[0]);
        for (int i = 0; i < 10; i++) {
            rainbow.setOutputParameter(i, rainbowOutputs[i]);
        }
        rainbow.calculate(startIndex, endIndex);

        double[] maxOutput = new double[endIndex - startIndex + 1];
        max.setInputParameter(0, inputs[0]);
        max.setOutputParameter(0, maxOutput);
        max.calculate(startIndex, endIndex);

        double[] minOutput = new double[endIndex - startIndex + 1];
        min.setInputParameter(0, inputs[0]);
        min.setOutputParameter(0, minOutput);
        min.calculate(startIndex, endIndex);

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            double rainbowMax = Double.MIN_VALUE;
            double rainbowMin = Double.MAX_VALUE;
            double rainbowSum = 0;
            for (int k = 0; k < 10; k++) {
                rainbowMax = Math.max(rainbowMax, rainbowOutputs[k][j]);
                rainbowMin = Math.min(rainbowMin, rainbowOutputs[k][j]);
                rainbowSum += rainbowOutputs[k][j];
            }

            outputs[0][j] = 100 * (inputs[0][i] - rainbowSum / oscPeriod) / (maxOutput[j] - minOutput[j]);
            outputs[1][j] = -100 * (rainbowMin - rainbowMax) / (maxOutput[j] - minOutput[j]);
            outputs[2][j] = -outputs[1][j];
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
    }

    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    public InputParameterInfo getInputParameterInfo(int index) {
        if (index < inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int maPeriod1 = (Integer) value;
            rainbow.setOptInputParameter(0, maPeriod1);
            break;
        case 1:
            int maType1 = (Integer) value;
            rainbow.setOptInputParameter(1, maType1);
            indicatorInfo.setUnstablePeriod(rainbow.getIndicatorInfo().isUnstablePeriod());
            break;
        case 2:
            int maPeriod2 = (Integer) value;
            rainbow.setOptInputParameter(2, maPeriod2);
            break;
        case 3:
            int maType2 = (Integer) value;
            rainbow.setOptInputParameter(3, maType2);
            indicatorInfo.setUnstablePeriod(rainbow.getIndicatorInfo().isUnstablePeriod());
            break;
        case 4:
            oscPeriod = (Integer) value;
            max.setOptInputParameter(0, oscPeriod);
            min.setOptInputParameter(0, oscPeriod);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(rainbow.getLookback(), max.getLookback());
    }

    public int getLookforward() {
        return 0;
    }
}
