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
public class StochasticFastIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[2][];

    private int fastKPeriod = 5;
    private int fastDPeriod = 3;

    private IIndicator fastDMa;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("STOCHF", "Stochastic Fast", "Momentum Indicators", false, false, false, 1, 3, 2);

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
            new OptInputParameterInfo("Fast %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastKPeriod, 1, 2000, 1)),
            new OptInputParameterInfo("Fast %D Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastDPeriod, 1, 2000, 1)),
            new OptInputParameterInfo("Fast %D MAType", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Fast %K", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Fast %D", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        fastDMa = indicatorsProvider.getIndicator("MA");
        fastDMa.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int fastKLookBack = fastKPeriod - 1;
        double[] kInputs = new double[endIndex - startIndex + 1 + fastDMa.getLookback()];

        int i, j;
        for (i = startIndex - fastDMa.getLookback(), j = 0; i <= endIndex; i++, j++) {
            double highestHigh = inputs[0][2][i], lowestLow = inputs[0][3][i];
            for (int k = fastKLookBack; k > 0; k--) {
                highestHigh = inputs[0][2][i - k] > highestHigh ? inputs[0][2][i - k] : highestHigh;
                lowestLow = inputs[0][3][i - k] < lowestLow ? inputs[0][3][i - k] : lowestLow;
            }

            if (highestHigh - lowestLow == 0){
                kInputs[j] = 0;
            }
            else{ 
                kInputs[j] = 100 * ((inputs[0][1][i] - lowestLow) / (highestHigh - lowestLow));
            }
        }

        fastDMa.setInputParameter(0, kInputs);
        fastDMa.setOutputParameter(0, outputs[1]);
        IndicatorResult res = fastDMa.calculate(0, kInputs.length - 1);

        if (res.getNumberOfElements() == 0) {
            return new IndicatorResult(0, 0);
        }

        System.arraycopy(kInputs, res.getFirstValueIndex(), outputs[0], 0, res.getNumberOfElements());

        return new IndicatorResult(fastKLookBack + res.getFirstValueIndex(), res.getNumberOfElements());
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
            fastKPeriod = (Integer) value;
            break;
        case 1:
            fastDPeriod = (Integer) value;
            fastDMa.setOptInputParameter(0, fastDPeriod);
            break;
        case 2:
            int fastDMaType = (Integer) value;
            fastDMa.setOptInputParameter(1, fastDMaType);
            indicatorInfo.setUnstablePeriod(fastDMa.getIndicatorInfo().isUnstablePeriod());
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
        return fastDMa.getLookback() + (fastKPeriod - 1);
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
