package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class EhlersLaguerreRelativeStrengthIndex implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private double dampingFactor = 0.5;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("LRSI", "Ehlers Laguerre Relative Strength Index", "Momentum Indicators", false, false, true, 1, 1, 1);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Damping Factor", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(dampingFactor, 0.5, 0.85, 0.01, 2))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 80, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 20, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.RED, 1, 1)
        });
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] L = new double[4][endIndex - startIndex + 1];
        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            L[0][j] = (1 - dampingFactor) * inputs[0][i] + dampingFactor * (j > 0 ? L[0][j - 1] : 0);
            L[1][j] = -dampingFactor * L[0][j] + (j > 0 ? L[0][j - 1] + dampingFactor * L[1][j - 1] : 0);
            L[2][j] = -dampingFactor * L[1][j] + (j > 0 ? L[1][j - 1] + dampingFactor * L[2][j - 1] : 0);
            L[3][j] = -dampingFactor * L[2][j] + (j > 0 ? L[2][j - 1] + dampingFactor * L[3][j - 1] : 0);

            double cu =
                    (L[0][j] > L[1][j] ? L[0][j] - L[1][j] : 0) +
                    (L[1][j] > L[2][j] ? L[1][j] - L[2][j] : 0) +
                    (L[2][j] > L[3][j] ? L[2][j] - L[3][j] : 0);
            double cd =
                    (L[0][j] < L[1][j] ? L[1][j] - L[0][j] : 0) +
                    (L[1][j] < L[2][j] ? L[2][j] - L[1][j] : 0) +
                    (L[2][j] < L[3][j] ? L[3][j] - L[2][j] : 0);

            outputs[0][j] = (cu + cd != 0 ? 100 * cu / (cu + cd) : 0);
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
        dampingFactor = (double) value;
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
