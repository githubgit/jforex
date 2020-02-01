package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
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
public class KDJIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    
    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[3][];

    private double kMultiplier = -2;
    private double dMultiplier = 3;

    private IIndicator stochastic;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("KDJ", "Random Index", "Momentum Indicators", false, false, false, 1, 7, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Input data", InputParameterInfo.Type.PRICE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Fast %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %K MAType", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Slow %D Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %D MAType", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("%K Multiplier", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(kMultiplier, -10, 10, 1, 1)),
            new OptInputParameterInfo("%D Multiplier", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(dMultiplier, -10, 10, 1, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("%K", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("%D", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("%J", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        stochastic = context.getIndicatorsProvider().getIndicator("STOCH");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        stochastic.setInputParameter(0, inputs[0]);
        stochastic.setOutputParameter(0, outputs[0]);
        stochastic.setOutputParameter(1, outputs[1]);
        
        IndicatorResult res = stochastic.calculate(startIndex, endIndex);
        
        for (int i = 0; i < outputs[0].length; i++) {
            outputs[2][i] = kMultiplier * outputs[0][i] + dMultiplier * outputs[1][i];
        }
        
        return res;
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
        case 1:
        case 2:
        case 3:
        case 4:
            stochastic.setOptInputParameter(index, value);
            if (index == 2 || index == 4) {
                indicatorInfo.setUnstablePeriod(stochastic.getIndicatorInfo().isUnstablePeriod());
            }
            break;
        case 5:
            kMultiplier = (Double) value;
            break;
        case 6:
            dMultiplier = (Double) value;
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
        return stochastic.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
