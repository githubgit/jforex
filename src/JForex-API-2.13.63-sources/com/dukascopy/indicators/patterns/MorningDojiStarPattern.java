package com.dukascopy.indicators.patterns;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Mar 19, 2010
 * Time: 6:40:07 PM
 */
public class MorningDojiStarPattern implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private double[][][] inputs = new double[1][][];
    private int[][] outputs = new int[1][];

    public void onStart(IIndicatorContext context) {
        indicatorInfo =
                new IndicatorInfo("CDLMORNINGDOJISTAR", "Morning Doji Star", "Pattern Recognition", true, false, true, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[]{new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)};

        optInputParameterInfos =
                new OptInputParameterInfo[]{new OptInputParameterInfo("Nothing", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 0, 300, 1))};
        outputParameterInfos =
                new OutputParameterInfo[]{new OutputParameterInfo("Morning Doji Star", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.PATTERN_BOOL)};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        //calculating startIndex taking into account lookback value
        if (startIndex - getLookback() < 0) {
            startIndex -= startIndex - getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            boolean isStar = true;
            // first candle is black
            // second candle is white
            if ((inputs[0][0][i - 2] > inputs[0][1][i - 2]) && (inputs[0][0][i] < inputs[0][1][i])) {
                if (inputs[0][0][i - 1] < inputs[0][1][i - 2] && (inputs[0][1][i - 1] < inputs[0][0][i])) {
                    if (inputs[0][0][i - 1] == inputs[0][1][i - 1]) {

                    } else {
                        isStar = false;
                    }

                } else {
                    isStar = false;
                }
            } else {
                isStar = false;
            }
            if (j > 0 && isStar) {
                outputs[0][j - 1] = 1;
            } else {
                outputs[0][j] = 0;
            }
        }
        return new IndicatorResult(startIndex, j);
    }

    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    public InputParameterInfo getInputParameterInfo(int index) {
        if (index <= inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    public int getLookback() {
        return 4;
    }

    public int getLookforward() {
        return 0;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index <= optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index <= outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {

    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (int[]) array;
    }
}
