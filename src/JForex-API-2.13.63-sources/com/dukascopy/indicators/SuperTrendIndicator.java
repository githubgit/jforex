package com.dukascopy.indicators;

import java.util.Arrays;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class SuperTrendIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final Object[] inputs = new Object[2];
    private final double[][] outputs = new double[2][];

    private double multiplier = 1.7;

    private IIndicator atr;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("SUPERTREND", "Super Trend", "Overlap Studies", true, false, true, 2, 2, 2);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE),
            new InputParameterInfo("OHLC", InputParameterInfo.Type.PRICE)
        };

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("ATR Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(10, 2, 2000, 1)),
            new OptInputParameterInfo("Multiplier", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(multiplier, 1, 100, 0.1, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Trend Up", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Trend Down", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setGapAtNaN(true);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[1].setGapAtNaN(true);

        atr = context.getIndicatorsProvider().getIndicator("ATR");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        Arrays.fill(outputs[0], Double.NaN);
        Arrays.fill(outputs[1], Double.NaN);

        double[] atrOutput = new double[endIndex - startIndex + 1];
        atr.setInputParameter(0, inputs[1]);
        atr.setOutputParameter(0, atrOutput);
        atr.calculate(startIndex, endIndex);

        double[] superTrend = new double[endIndex - startIndex + 1];
        double prevClose = Double.NaN;

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            double upperLevel = ((double[]) inputs[0])[i] + atrOutput[j] * multiplier;
            double lowerLevel = ((double[]) inputs[0])[i] - atrOutput[j] * multiplier;
            double closePrice = ((double[][]) inputs[1])[CLOSE][i];

            if (j == 0) {
                superTrend[j] = upperLevel;
            } else if (closePrice > superTrend[j - 1] && prevClose <= superTrend[j - 1]) {
                superTrend[j] = lowerLevel;
            } else if (closePrice < superTrend[j - 1] && prevClose >= superTrend[j - 1]) {
                superTrend[j] = upperLevel;
            } else if (superTrend[j - 1] < lowerLevel) {
                superTrend[j] = lowerLevel;
            } else if (superTrend[j - 1] > upperLevel) {
                superTrend[j] = upperLevel;
            } else {
                superTrend[j] = superTrend[j - 1];
            }

            if (closePrice > superTrend[j] || closePrice == superTrend[j] && (j == 0 || prevClose > superTrend[j - 1])) {
                outputs[0][j] = superTrend[j];
                if (j > 0 && Double.isNaN(outputs[0][j - 1])) {
                    outputs[1][j] = outputs[0][j];
                }
            } else if (closePrice < superTrend[j] || closePrice == superTrend[j] && (j == 0 || prevClose < superTrend[j - 1])) {
                outputs[1][j] = superTrend[j];
                if (j > 0 && Double.isNaN(outputs[1][j - 1])) {
                    outputs[0][j] = outputs[1][j];
                }
            } else if (j > 0) {
                outputs[0][j] = outputs[0][j - 1];
                outputs[1][j] = outputs[1][j - 1];
            }

            prevClose = closePrice;
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
        inputs[index] = array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int atrPeriod = (Integer) value;
            atr.setOptInputParameter(0, atrPeriod);
            break;
        case 1:
            multiplier = (Double) value;
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
        return atr.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
