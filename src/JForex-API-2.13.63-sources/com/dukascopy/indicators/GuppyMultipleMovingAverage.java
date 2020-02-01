package com.dukascopy.indicators;

import java.util.Arrays;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class GuppyMultipleMovingAverage implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[12][];

    private final int[] periods = new int[] {3, 5, 8, 10, 12, 15, 30, 35, 40, 45, 50, 60};

    private IIndicator ema;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("GMMA", "Guppy Multiple Moving Average", "", true, false, true, 1, 12, 12);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[indicatorInfo.getNumberOfOptionalInputs()];

        for (int i = 0; i < optInputParameterInfos.length; i++) {
            optInputParameterInfos[i] = new OptInputParameterInfo((i < 6 ? "Short" : "Long") + " EMA" + (i % 6 + 1) + " Period",
                    OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(periods[i], 2, 2000, 1));
        }

        outputParameterInfos = new OutputParameterInfo[indicatorInfo.getNumberOfOutputs()];

        for (int i = 0; i < outputParameterInfos.length; i++) {
            outputParameterInfos[i] = new OutputParameterInfo((i < 6 ? "Short" : "Long") + " EMA" + (i % 6 + 1),
                    OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE);
            outputParameterInfos[i].setColor(i < 6 ? DefaultColors.ROYAL_BLUE : DefaultColors.RED);
        }

        ema = context.getIndicatorsProvider().getIndicator("EMA");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        ema.setInputParameter(0, inputs[0]);

        for (int i = 0; i < outputs.length; i++) {
            ema.setOptInputParameter(0, periods[i]);
            ema.setOutputParameter(0, outputs[i]);
            ema.calculate(startIndex, endIndex);
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
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
        periods[index] = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Arrays.stream(periods).max().getAsInt() - 1;
    }

    public int getLookforward() {
        return 0;
    }
}
