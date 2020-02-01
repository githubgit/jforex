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

public class MACDIndicator implements IIndicator {
    private IIndicator fastEMA;
    private IIndicator slowEMA;
    private IIndicator signalEMA;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[5][];

    private int fastPeriod = 12;
    private int slowPeriod = 26;
    private int signalPeriod = 9;

    public void onStart(IIndicatorContext context) {
        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        fastEMA = indicatorsProvider.getIndicator("EMA");
        slowEMA = indicatorsProvider.getIndicator("EMA");
        signalEMA = indicatorsProvider.getIndicator("EMA");

        indicatorInfo = new IndicatorInfo("MACD", "Moving Average Covergence/Divergence", "Momentum Indicators", false, false, true, 1, 3, 5);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Fast Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Slow Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Signal Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(signalPeriod, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("MACD", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("MACD Signal", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("MACD Hist", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Positive Hist", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Negative Hist", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };

        outputParameterInfos[3].setShowOutput(false);
        outputParameterInfos[4].setShowOutput(false);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        if (slowPeriod < fastPeriod) {
            int tempInteger = slowPeriod;
            slowPeriod = fastPeriod;
            fastPeriod = tempInteger;

            fastEMA.setOptInputParameter(0, fastPeriod);
            slowEMA.setOptInputParameter(0, slowPeriod);
        }

        double[] fastMAOutput = new double[endIndex - startIndex + 1 + signalEMA.getLookback()];
        fastEMA.setInputParameter(0, inputs[0]);
        fastEMA.setOutputParameter(0, fastMAOutput);

        double[] slowMAOutput = new double[endIndex - startIndex + 1 + signalEMA.getLookback()];
        slowEMA.setInputParameter(0, inputs[0]);
        slowEMA.setOutputParameter(0, slowMAOutput);

        fastEMA.calculate(startIndex - signalEMA.getLookback(), endIndex);
        IndicatorResult slowRes = slowEMA.calculate(startIndex - signalEMA.getLookback(), endIndex);

        double[] macd = new double[slowRes.getNumberOfElements()];
        int k;
        for (k = 0; k < slowRes.getNumberOfElements(); k++){
           macd[k] = fastMAOutput[k] - slowMAOutput[k];
        }

        double[] signalOutput = new double[endIndex - startIndex + 1];
        signalEMA.setInputParameter(0, macd);
        signalEMA.setOutputParameter(0, signalOutput);
        IndicatorResult signalRes = signalEMA.calculate(0, slowRes.getNumberOfElements() - 1);

        System.arraycopy(macd, signalRes.getFirstValueIndex(), outputs[0], 0, signalRes.getNumberOfElements());

        for (k = 0; k < signalRes.getNumberOfElements(); k++){
            outputs[1][k] = signalOutput[k];
            outputs[2][k] = outputs[0][k] - signalOutput[k];
            if (outputs[3] != null) {
                outputs[3][k] = (outputs[2][k] > 0 ? outputs[2][k] : 0);
            }
            if (outputs[4] != null) {
                outputs[4][k] = (outputs[2][k] < 0 ? outputs[2][k] : 0);
            }
        }

        return new IndicatorResult(slowRes.getFirstValueIndex() + signalRes.getFirstValueIndex(), signalRes.getNumberOfElements());
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
            fastPeriod = (Integer) value;
            fastEMA.setOptInputParameter(0, fastPeriod);
            break;
        case 1:
            slowPeriod = (Integer) value;
            slowEMA.setOptInputParameter(0, slowPeriod);
            break;
        case 2:
            signalPeriod = (Integer) value;
            signalEMA.setOptInputParameter(0, signalPeriod);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(fastEMA.getLookback(), slowEMA.getLookback()) + signalEMA.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
