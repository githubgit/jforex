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
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * 
 * @author anatoly.pokusayev
 *
 */
public class MAIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private int timePeriod = 30;
    private int maType = IIndicators.MaType.SMA.ordinal();

    private IIndicatorsProvider indicatorsProvider;
    private IIndicator ma;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MA", "Moving average", "Overlap Studies", true, false, false, 1, 2, 1);

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
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 1, 2000, 1)),
            new OptInputParameterInfo("MA type", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(maType, maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Output", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorsProvider = context.getIndicatorsProvider();
        ma = indicatorsProvider.getIndicator(IIndicators.MaType.values()[maType].name());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        if (timePeriod == 1){
            int i, j;
            for (i = startIndex, j = 0; i <= endIndex; i++, j++){
                outputs[0][j] = inputs[0][i];
            }
            return new IndicatorResult(startIndex, j);
        }

        ma.setInputParameter(0, inputs[0]);
        ma.setOutputParameter(0, outputs[0]);
        if (IIndicators.MaType.values()[maType] == IIndicators.MaType.MAMA){
            double[] maDummy = new double[outputs[0].length];
            ma.setOutputParameter(1, maDummy);
        }

        return ma.calculate(startIndex, endIndex); 
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
            break;
        case 1:
            maType = (Integer) value;
            ma = indicatorsProvider.getIndicator(IIndicators.MaType.values()[maType].name());
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
        if (timePeriod == 1){
            return 0;
        }
        else if (IIndicators.MaType.values()[maType] == IIndicators.MaType.MAMA){
            ma.setOptInputParameter(0, 0.5);
            ma.setOptInputParameter(1, 0.05);
        }
        else if (IIndicators.MaType.values()[maType] == IIndicators.MaType.T3){
            ma.setOptInputParameter(0, timePeriod);
            ma.setOptInputParameter(1, 0.7);
        }
        else{
            ma.setOptInputParameter(0, timePeriod);
        }
        return ma.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
