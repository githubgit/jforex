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

public class TrendIntensityIndex implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private IIndicator ma;
    private IIndicator sum;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TII", "Trend Intensity Index", "", false, false, false, 1, 2, 1);

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
            new OptInputParameterInfo("Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(30, 2, 2000, 1)),
            new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 80, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 20, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1),
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ma = indicatorsProvider.getIndicator("MA");
        ma.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
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

        double[] maOutput = new double[endIndex - startIndex + 1 + sum.getLookback()];
        ma.setInputParameter(0, inputs[0]);
        ma.setOutputParameter(0, maOutput);
        ma.calculate(startIndex - sum.getLookback(), endIndex);

        double[][] sumInputs = new double[2][endIndex - startIndex + 1 + sum.getLookback()];
        double[][] sumOutputs = new double[2][endIndex - startIndex + 1];
        for (int i = startIndex - sum.getLookback(), j = 0; i <= endIndex; i++, j++) {
            sumInputs[0][j] = (inputs[0][i] > maOutput[j] ? inputs[0][i] - maOutput[j] : 0);
            sumInputs[1][j] = (inputs[0][i] < maOutput[j] ? maOutput[j] - inputs[0][i] : 0);
        }

        for (int i = 0; i < 2; i++) {
            sum.setInputParameter(0, sumInputs[i]);
            sum.setOutputParameter(0, sumOutputs[i]);
            sum.calculate(sum.getLookback(), sumInputs[i].length - 1);
        }

        for (int i = 0; i < endIndex - startIndex + 1; i++){
           outputs[0][i] = 100 * sumOutputs[0][i] / (sumOutputs[0][i] + sumOutputs[1][i]);
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
            int period = (Integer) value;
            ma.setOptInputParameter(0, 2 * period);
            sum.setOptInputParameter(0, period);
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
        return ma.getLookback() + sum.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
