package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.DoubleRangeDescription;
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
public class BollingerBands implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private IIndicator movingAverage;
    private IIndicator stdDevUp;
    private IIndicator stdDevDown;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("BBANDS", "Bollinger Bands", "Overlap Studies", true, false, true, 1, 4, 3);

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
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1)),
            new OptInputParameterInfo("Nb Dev Up", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(2, -10000, 10000, 0.01, 3)),
            new OptInputParameterInfo("Nb Dev Dn", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(2, -10000, 10000, 0.01, 3)),
            new OptInputParameterInfo("MA type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.EMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Upper Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Middle Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Lower Band", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        stdDevUp = indicatorsProvider.getIndicator("STDDEV");
        stdDevDown = indicatorsProvider.getIndicator("STDDEV");
        movingAverage = indicatorsProvider.getIndicator("MA");
        movingAverage.setOptInputParameter(1, IIndicators.MaType.EMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] maOutput = new double[endIndex - startIndex + 1];
        movingAverage.setInputParameter(0, inputs[0]);
        movingAverage.setOutputParameter(0, maOutput);

        double[] stdDevUpOutput = new double[endIndex - startIndex + 1];
        stdDevUp.setInputParameter(0, inputs[0]);
        stdDevUp.setOutputParameter(0, stdDevUpOutput);

        double[] stdDevDownOutput = new double[endIndex - startIndex + 1];
        stdDevDown.setInputParameter(0, inputs[0]);
        stdDevDown.setOutputParameter(0, stdDevDownOutput);

        IndicatorResult maRes = movingAverage.calculate(startIndex, endIndex);
        stdDevUp.calculate(startIndex, endIndex);
        stdDevDown.calculate(startIndex, endIndex);

        int k;
        for (k = 0; k < maRes.getNumberOfElements(); k++){
           outputs[1][k] = maOutput[k]; 
           outputs[0][k] = maOutput[k] + stdDevUpOutput[k];
           outputs[2][k] = maOutput[k] - stdDevDownOutput[k];
        }

        return new IndicatorResult(startIndex, maRes.getNumberOfElements());
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
            int timePeriod = (Integer) value;
            movingAverage.setOptInputParameter(0, timePeriod);
            stdDevUp.setOptInputParameter(0, timePeriod);
            stdDevDown.setOptInputParameter(0, timePeriod);
            break;
        case 1:
            double nbDevUp = (Double) value;
            stdDevUp.setOptInputParameter(1, nbDevUp);
            break;
        case 2:
            double nbDevDown = (Double) value;
            stdDevDown.setOptInputParameter(1, nbDevDown);
            break;
        case 3:
            int maType = (Integer) value;
            movingAverage.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(movingAverage.getIndicatorInfo().isUnstablePeriod());
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
        return Math.max(movingAverage.getLookback(), stdDevUp.getLookback());
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
