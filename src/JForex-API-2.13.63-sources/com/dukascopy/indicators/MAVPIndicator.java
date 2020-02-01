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
public class MAVPIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[2][];
    private final double[][] outputs = new double[1][];

    private int minPeriod = 2;
    private int maxPeriod = 30;

    private int maType = IIndicators.MaType.SMA.ordinal();

    private IIndicator movingAverage;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MAVP", "Moving average with variable period", "Overlap Studies", true, false, false, 2, 3, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE),
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Max period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(minPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Min period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(maxPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("MA type", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(maType, maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        movingAverage = indicatorsProvider.getIndicator("MA");
        movingAverage.setOptInputParameter(1, maType);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int outputSize = endIndex - startIndex + 1;
        double[] periods = new double[outputSize];
        double[] maOutput = new double[outputSize];

        int tempInt;
        for(int i = 0; i < outputSize; i++ ){
            tempInt = (int)(inputs[1][startIndex + i]);
            if(tempInt < minPeriod){
                tempInt = minPeriod;
            }
            else if (tempInt > maxPeriod){
                tempInt = maxPeriod;
            }
            periods[i] = tempInt;
        } 

        double curPeriod;
        int i, j;
        for(i = 0; i < outputSize; i++ ){
            curPeriod = periods[i];
            if (curPeriod != 0 ){
                movingAverage.setOutputParameter(0, maOutput);
                movingAverage.setInputParameter(0, inputs[0]);
                movingAverage.setOptInputParameter(0, (int)curPeriod);
                movingAverage.calculate(startIndex, endIndex);

                outputs[0][i] = maOutput[i];

                for(j = i + 1; j < outputSize; j++ ){
                    if(periods[j] == curPeriod ){
                        periods[j] = 0;
                        outputs[0][j] = maOutput[j];
                    }
                }
            }
        }

        return new IndicatorResult(startIndex, i);
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
            minPeriod = (Integer) value;
            break;
        case 1:
            maxPeriod = (Integer) value;
            break;
        case 2:
            maType = (Integer) value;
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
        movingAverage.setOptInputParameter(0, maxPeriod);
        return movingAverage.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
