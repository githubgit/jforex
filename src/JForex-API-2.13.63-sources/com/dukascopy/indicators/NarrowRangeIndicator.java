package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class NarrowRangeIndicator implements IIndicator {

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[1][];

    private IIndicator min;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("NR", "Narrow Range", "Thomas Bulkowski", true, false, false, 1, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};

        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(4, 2, 2000, 1))
		};

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Narrow Range", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN)
        };

        min = context.getIndicatorsProvider().getIndicator("MIN");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex < getLookback()) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] minInput = new double[endIndex - startIndex + 1 + getLookback()];
        for (int i = startIndex - getLookback(), j = 0; i <= endIndex; i++, j++) {
            minInput[j] = inputs[0][HIGH][i] - inputs[0][LOW][i];
        }

        double[] minOutput = new double[endIndex - startIndex + 1];
        min.setInputParameter(0, minInput);
        min.setOutputParameter(0, minOutput);
        min.calculate(getLookback(), minInput.length - 1);

        for (int i = getLookback(), j = 0; i <= minInput.length - 1; i++, j++) {
            if (minOutput[j] == minInput[i]) {
                outputs[0][j] = inputs[0][HIGH][startIndex + j];
            } else {
                outputs[0][j] = Double.NaN;
            }
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
        inputs[index] = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        if (index == 0) {
            int period = (Integer) value;
            min.setOptInputParameter(0, period);
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return min.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
