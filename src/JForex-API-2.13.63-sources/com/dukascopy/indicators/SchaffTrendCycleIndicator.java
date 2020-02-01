package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class SchaffTrendCycleIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private IIndicator emaShort;
    private IIndicator emaLong;
    private IIndicator emaCycle;
    private IIndicator sub;
    private IIndicator max;
    private IIndicator min;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("ST_CYCLE", "Schaff Trend Cycle", "", false, false, true, 1, 3, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.TYPICAL_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Short-Term Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(23, 2, 2000, 1)),
            new OptInputParameterInfo("Long-Term Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(50, 2, 2000, 1)),
            new OptInputParameterInfo("Schaff Cycle Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(10, 4, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.RED);

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 80, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.ROYAL_BLUE, 1, 1),
            new LevelInfo("", 70, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.ROYAL_BLUE, 1, 1),
            new LevelInfo("", 30, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.ROYAL_BLUE, 1, 1),
            new LevelInfo("", 20, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.ROYAL_BLUE, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        emaShort = indicatorsProvider.getIndicator("EMA");
        emaLong = indicatorsProvider.getIndicator("EMA");
        emaCycle = indicatorsProvider.getIndicator("EMA");
        sub = indicatorsProvider.getIndicator("SUB");
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

        double[] emaShortOutput = new double[endIndex - startIndex + 1 + max.getLookback() + emaCycle.getLookback()];
        emaShort.setInputParameter(0, inputs[0]);
        emaShort.setOutputParameter(0, emaShortOutput);
        emaShort.calculate(startIndex - max.getLookback() - emaCycle.getLookback(), endIndex);

        double[] emaLongOutput = new double[emaShortOutput.length];
        emaLong.setInputParameter(0, inputs[0]);
        emaLong.setOutputParameter(0, emaLongOutput);
        emaLong.calculate(startIndex - max.getLookback() - emaCycle.getLookback(), endIndex);

        double[] subOutput = new double[emaShortOutput.length];
        sub.setInputParameter(0, emaShortOutput);
        sub.setInputParameter(1, emaLongOutput);
        sub.setOutputParameter(0, subOutput);
        sub.calculate(0, emaShortOutput.length - 1);

        double[] maxOutput = new double[subOutput.length - max.getLookback()];
        max.setInputParameter(0, subOutput);
        max.setOutputParameter(0, maxOutput);
        max.calculate(max.getLookback(), subOutput.length - 1);

        double[] minOutput = new double[subOutput.length - min.getLookback()];
        min.setInputParameter(0, subOutput);
        min.setOutputParameter(0, minOutput);
        min.calculate(min.getLookback(), subOutput.length - 1);

        double[] emaCycleInput = new double[maxOutput.length];
        for (int i = 0; i < maxOutput.length; i++) {
            emaCycleInput[i] = (subOutput[i + max.getLookback()] - minOutput[i]) / (maxOutput[i] - minOutput[i]) * 100;
        }

        emaCycle.setInputParameter(0, emaCycleInput);
        emaCycle.setOutputParameter(0, outputs[0]);
        emaCycle.calculate(emaCycle.getLookback(), emaCycleInput.length - 1);

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
            int shortPeriod = (Integer) value;
            emaShort.setOptInputParameter(0, shortPeriod);
            break;
        case 1:
            int longPeriod = (Integer) value;
            emaLong.setOptInputParameter(0, longPeriod);
            break;
        case 2:
            int cyclePeriod = (Integer) value;
            emaCycle.setOptInputParameter(0, cyclePeriod / 2);
            max.setOptInputParameter(0, cyclePeriod);
            min.setOptInputParameter(0, cyclePeriod);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(emaShort.getLookback(), emaLong.getLookback()) + max.getLookback() + emaCycle.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
