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
public class CenterOfGravityIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[2][];

    private int timePeriod = 10;
    private int smoothPeriod = 3;

    private IIndicator indicatorMA;
    private IIndicator signalMA;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("COG", "Center of gravity", "", false, false, false, 1, 3, 2);

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
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(timePeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Smooth period", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(smoothPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Smooth type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("CG Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Signal line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        indicatorMA = indicatorsProvider.getIndicator("SMA");
        signalMA = indicatorsProvider.getIndicator("MA");
        signalMA.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] numInput = new double[endIndex - startIndex + 1 + signalMA.getLookback()];
        double[] maOutput = new double[endIndex - startIndex + 1 + signalMA.getLookback()];

        indicatorMA.setInputParameter(0, inputs[0]);
        indicatorMA.setOutputParameter(0, maOutput);
        IndicatorResult maResult = indicatorMA.calculate(startIndex - signalMA.getLookback(), endIndex);

        double num = 0;
        int i, j;
        for (i = startIndex - signalMA.getLookback(), j = 0; i <= endIndex; i++, j++) {
            num = 0;
            for (int k = (timePeriod - 1); k > 0; k--) {
                num += inputs[0][i - k] * (k + 1);
            }
            numInput[j] = (-1) * num / (maOutput[j] * timePeriod);
        }

        signalMA.setInputParameter(0, numInput);
        signalMA.setOutputParameter(0, outputs[1]);
        IndicatorResult signalResult = signalMA.calculate(0, numInput.length - 1);

        if (signalResult.getNumberOfElements() == 0) {
            return new IndicatorResult(0, 0);
        }

        System.arraycopy(numInput, signalResult.getFirstValueIndex(), outputs[0], 0, signalResult.getNumberOfElements());

        return new IndicatorResult(signalResult.getFirstValueIndex() + maResult.getFirstValueIndex(), signalResult.getNumberOfElements());
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
            timePeriod = (Integer) value;
            indicatorMA.setOptInputParameter(0, timePeriod);
            break;
        case 1:
            smoothPeriod = (Integer) value;
            signalMA.setOptInputParameter(0, smoothPeriod);
            break;
        case 2:
            int maType = (Integer) value;
            signalMA.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(signalMA.getIndicatorInfo().isUnstablePeriod());
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
        return indicatorMA.getLookback() + signalMA.getLookback() + (timePeriod - 1);
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
