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
public class StochasticRSIIndicator implements IIndicator {
    private IIndicatorsProvider indicatorsProvider;
    private IIndicator stochFastIndicator;
    private IIndicator rsiIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[2][];

    public void onStart(IIndicatorContext context) {
        indicatorsProvider = context.getIndicatorsProvider();
        rsiIndicator = indicatorsProvider.getIndicator("RSI");
        stochFastIndicator = indicatorsProvider.getIndicator("STOCHF");

        indicatorInfo = new IndicatorInfo("STOCHRSI", "Stochastic Relative Strength Indicator", "Momentum Indicators", false, false, true, 1, 4, 2);

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
            new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(14, 2, 2000, 1)),
            new OptInputParameterInfo("Fast %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 1, 2000, 1)),
            new OptInputParameterInfo("Fast %D Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 1, 2000, 1)),
            new OptInputParameterInfo("Fast %D MAType", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Fast %K", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Fast %D", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] rsiOutput;
        rsiOutput = new double[endIndex - startIndex + 1 + stochFastIndicator.getLookback()];

        rsiIndicator.setInputParameter(0, inputs[0]);
        rsiIndicator.setOutputParameter(0, rsiOutput);
        IndicatorResult rsiRes = rsiIndicator.calculate(startIndex - stochFastIndicator.getLookback(), endIndex);

        if (rsiRes.getNumberOfElements() == 0) {
            return new IndicatorResult(0, 0);
        }

        stochFastIndicator.setInputParameter(0, new double[][]{rsiOutput, rsiOutput, rsiOutput, rsiOutput, new double[rsiOutput.length]});
        stochFastIndicator.setOutputParameter(0, outputs[0]);
        stochFastIndicator.setOutputParameter(1, outputs[1]);
        IndicatorResult res = stochFastIndicator.calculate(0, rsiRes.getNumberOfElements() - 1);

        if (res.getNumberOfElements() == 0) {
            return new IndicatorResult(0, 0);
        }

        return new IndicatorResult(rsiRes.getFirstValueIndex() + res.getFirstValueIndex(), res.getNumberOfElements());
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
                int timePeriod = (Integer) value;
                rsiIndicator.setOptInputParameter(0, timePeriod);
                break;
            case 1:
                int fastKPeriod = (Integer) value;
                stochFastIndicator.setOptInputParameter(0, fastKPeriod);
                break;
            case 2:
                int fastDPeriod = (Integer) value;
                stochFastIndicator.setOptInputParameter(1, fastDPeriod);
                break;
            case 3:
                int fastDMaType = (Integer) value;
                stochFastIndicator.setOptInputParameter(2, fastDMaType);
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return stochFastIndicator.getLookback() + rsiIndicator.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
