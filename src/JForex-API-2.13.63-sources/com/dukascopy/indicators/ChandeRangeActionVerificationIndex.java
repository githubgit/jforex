package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class ChandeRangeActionVerificationIndex implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private IIndicator smaShort;
    private IIndicator smaLong;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("RAVI", "Chande's Range Action Verification Index", "", false, false, false, 1, 2, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Short Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(7, 2, 2000, 1)),
            new OptInputParameterInfo("Long Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(65, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 3, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        smaShort = indicatorsProvider.getIndicator("SMA");
        smaLong = indicatorsProvider.getIndicator("SMA");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] smaShortOutput = new double[endIndex - startIndex + 1];
        smaShort.setInputParameter(0, inputs[0]);
        smaShort.setOutputParameter(0, smaShortOutput);
        smaShort.calculate(startIndex, endIndex);

        double[] smaLongOutput = new double[endIndex - startIndex + 1];
        smaLong.setInputParameter(0, inputs[0]);
        smaLong.setOutputParameter(0, smaLongOutput);
        smaLong.calculate(startIndex, endIndex);

        for (int i = 0; i < outputs[0].length; i++) {
            outputs[0][i] = 1000 * Math.abs(smaShortOutput[i] - smaLongOutput[i]) / smaLongOutput[0];
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
        switch (index) {
        case 0:
            int smaShortPeriod = (Integer) value;
            smaShort.setOptInputParameter(0, smaShortPeriod);
            break;
        case 1:
            int smaLongPeriod = (Integer) value;
            smaLong.setOptInputParameter(0, smaLongPeriod);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(smaShort.getLookback(), smaLong.getLookback());
    }

    public int getLookforward() {
        return 0;
    }
}
