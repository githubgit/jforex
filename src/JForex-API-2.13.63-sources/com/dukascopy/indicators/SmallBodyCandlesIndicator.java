package com.dukascopy.indicators;

import java.util.Arrays;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class SmallBodyCandlesIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final IBar[][] outputs = new IBar[3][];

    private int percentage = 50;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("SMALL_CANDLES", "Small body candles", "", true, false, false, 1, 1, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bars", InputParameterInfo.Type.BAR)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Percentage", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(percentage, 1, 100, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Bear candles", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES),
            new OutputParameterInfo("Bull candles", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES),
            new OutputParameterInfo("Small body candles", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES)
        };

        outputParameterInfos[0].setColor(DefaultColors.RED);
        outputParameterInfos[0].setShowOutput(false);
        outputParameterInfos[1].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[1].setShowOutput(false);
        outputParameterInfos[2].setColor(DefaultColors.ROYAL_BLUE);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        for (int i = 0; i < outputs.length; i++) {
            Arrays.fill(outputs[i], null);
        }

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            double koef = Math.abs(inputs[0][i].getClose() - inputs[0][i].getOpen()) / (inputs[0][i].getHigh() - inputs[0][i].getLow());

            if (koef * 100 < percentage) {
                outputs[2][j] = inputs[0][i];
            } else if (inputs[0][i].getClose() >= inputs[0][i].getOpen()) {
                outputs[1][j] = inputs[0][i];
            } else {
                outputs[0][j] = inputs[0][i];
            }
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
        inputs[index] = (IBar[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        if (index == 0) {
            percentage = (Integer) value;
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (IBar[]) array;
    }

    public int getLookback() {
        return 0;
    }

    public int getLookforward() {
        return 0;
    }
}
