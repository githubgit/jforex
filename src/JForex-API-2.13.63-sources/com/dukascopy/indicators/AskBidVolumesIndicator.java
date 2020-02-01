package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class AskBidVolumesIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final IBar[][] inputs = new IBar[2][];
    private final double[][] outputs = new double[3][];

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("AskBidVolumes", "Ask and Bid Volumes", "Volume Indicators", false, false, false, 2, 0, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Ask", InputParameterInfo.Type.BAR),
            new InputParameterInfo("Bid", InputParameterInfo.Type.BAR)
        };

        inputParameterInfos[0].setOfferSide(OfferSide.ASK);
        inputParameterInfos[1].setOfferSide(OfferSide.BID);

        optInputParameterInfos = new OptInputParameterInfo[] {
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Total Volume (Ask + Bid)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Bullish Volume (Ask > Bid)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Bearish Volume (Bid > Ask)", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };

        outputParameterInfos[0].setColor(DefaultColors.STEEL_BLUE);
        outputParameterInfos[0].setOpacityAlpha(0.3f);
        outputParameterInfos[1].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[2].setColor(DefaultColors.DARK_RED);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex < getLookback()) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            double askVolume = inputs[0][i].getVolume();
            double bidVolume = inputs[1][i].getVolume();

            outputs[0][j] = askVolume + bidVolume;

            if (askVolume >= bidVolume) {
                outputs[1][j] = askVolume - bidVolume;
                outputs[2][j] = Double.NaN;
            } else {
                outputs[1][j] = Double.NaN;
                outputs[2][j] = askVolume - bidVolume;
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
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return 0;
    }

    public int getLookforward() {
        return 0;
    }
}
