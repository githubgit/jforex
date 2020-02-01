package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class TrixIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[1][];

    private IIndicator ema;
    private IIndicator roc;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TRIX", "1-day Rate-Of-Change (ROC) of a Triple Smooth EMA", "Momentum Indicators", false, false, true, 1, 1, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(30, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        IIndicatorsProvider indicators = context.getIndicatorsProvider();
        ema = indicators.getIndicator("EMA");
        roc = indicators.getIndicator("ROC");
        roc.setOptInputParameter(0, 1);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex < getLookback()) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int totalLookback = getLookback();
        int emaLookback = ema.getLookback();

        double[][] emaOutputs = new double[3][];
        emaOutputs[0] = new double[endIndex - startIndex + 1 + totalLookback - emaLookback];
        emaOutputs[1] = new double[emaOutputs[0].length - emaLookback];
        emaOutputs[2] = new double[emaOutputs[1].length - emaLookback];

        ema.setInputParameter(0, inputs[0]);
        ema.setOutputParameter(0, emaOutputs[0]);
        ema.calculate(startIndex - totalLookback + emaLookback, endIndex);

        ema.setInputParameter(0, emaOutputs[0]);
        ema.setOutputParameter(0, emaOutputs[1]);
        ema.calculate(0, emaOutputs[0].length - 1);

        ema.setInputParameter(0, emaOutputs[1]);
        ema.setOutputParameter(0, emaOutputs[2]);
        ema.calculate(0, emaOutputs[1].length - 1);

        roc.setInputParameter(0, emaOutputs[2]);
        roc.setOutputParameter(0, outputs[0]);
        roc.calculate(0, emaOutputs[2].length - 1);

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
        inputs[index] = (double[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        ema.setOptInputParameter(0, value);
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return 3 * ema.getLookback() + roc.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
