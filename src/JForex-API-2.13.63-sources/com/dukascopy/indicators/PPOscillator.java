package com.dukascopy.indicators;

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

/**
 * 
 * @author anatoly.pokusayev
 *
 */
public class PPOscillator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private int fastPeriod = 12;
    private int slowPeriod = 26;

    private IIndicator firstMA;
    private IIndicator secondMA;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("PPO", "PPO", "Momentum Indicators", false, false, false, 1, 3, 1);

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
            new OptInputParameterInfo("Fast period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Slow period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("MA type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Output", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        firstMA = indicatorsProvider.getIndicator("MA");
        firstMA.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
        secondMA = indicatorsProvider.getIndicator("MA");
        secondMA.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        if (fastPeriod > slowPeriod){
            int temp = fastPeriod;
            fastPeriod = slowPeriod;
            slowPeriod = temp;

            firstMA.setOptInputParameter(0, fastPeriod);
            secondMA.setOptInputParameter(0, slowPeriod);
        }

        double[] firstMAOutput = new double[endIndex - startIndex + 1];
        double[] secondMAOutput = new double[endIndex - startIndex + 1];

        firstMA.setInputParameter(0, inputs[0]);
        secondMA.setInputParameter(0, inputs[0]);
        firstMA.setOutputParameter(0, firstMAOutput);
        secondMA.setOutputParameter(0, secondMAOutput);

        IndicatorResult fastRes = firstMA.calculate(startIndex, endIndex);
        IndicatorResult slowRes = secondMA.calculate(startIndex, endIndex);

        int i, j;
        for(i = 0, j = slowRes.getFirstValueIndex() - fastRes.getFirstValueIndex(); i < slowRes.getNumberOfElements(); i++, j++){
            if (secondMAOutput[i] == 0) {
                outputs[0][i] = 0;
            } else {
                outputs[0][i] = (firstMAOutput[j] - secondMAOutput[i]) / secondMAOutput[i] * 100;
            }
        }

        return new IndicatorResult(slowRes.getFirstValueIndex(), slowRes.getNumberOfElements());
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
            fastPeriod = (Integer) value;
            firstMA.setOptInputParameter(0, fastPeriod);
            break;
        case 1:
            slowPeriod = (Integer) value;
            secondMA.setOptInputParameter(0, slowPeriod);
            break;
        case 2:
            int maType = (Integer) value;
            firstMA.setOptInputParameter(1, maType);
            secondMA.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(firstMA.getIndicatorInfo().isUnstablePeriod());
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
        return Math.max(firstMA.getLookback(), secondMA.getLookback());
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
