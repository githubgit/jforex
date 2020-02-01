package com.dukascopy.indicators;

import java.util.Arrays;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class RegressionChannel implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private int degree = 3;
    private double kstd = 2;
    private int bars = 250;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("REGR_CHANNEL", "Regression Channel", "Statistic Functions", true, false, false, 1, 3, 3);
        indicatorInfo.setRecalculateAll(true);
        indicatorInfo.setRecalculateOnNewCandleOnly(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Degree", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(degree, 1, 10, 1)),
            new OptInputParameterInfo("Kstd", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(kstd, 0, 10, 0.1, 1)),
            new OptInputParameterInfo("Bars", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(bars, 100, 1000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Regression Trend", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Upper Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Lower Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.YELLOW_GREEN);
        outputParameterInfos[1].setColor(DefaultColors.YELLOW);
        outputParameterInfos[2].setColor(DefaultColors.YELLOW);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        for (int i = 0; i < outputs.length; i++) {
            Arrays.fill(outputs[i], Double.NaN);
        }

        double[] sx = new double[2 * degree + 1];
        sx[0] = bars + 1;
        for (int mi = 1; mi <= 2 * degree; mi++) {
            double sum = 0;
            for (int n = 0; n <= bars; n++) {
                sum += Math.pow(n, mi);
            }
            sx[mi] = sum;
        }

        double[][] ai = new double[degree + 1][degree + 1];
        for (int i = 0; i <= degree; i++) {
            for (int j = 0; j <= degree; j++) {
                ai[i][j] = sx[i + j];
            }
        }

        double[] b = new double[degree + 1];
        for (int mi = 0; mi <= degree; mi++) {
            double sum = 0;
            for (int n = 0; n <= bars; n++) {
                if (mi == 0) {
                    sum += inputs[0][endIndex - bars + n];
                } else {
                    sum += inputs[0][endIndex - bars + n] * Math.pow(n, mi);
                }
            }
            b[mi] = sum;
        }

        for (int k = 0; k <= degree - 1; k++) {
            int l = 0;
            double m = 0;

            for (int i = k; i <= degree; i++) {
                if (Math.abs(ai[i][k]) > m) {
                    m = Math.abs(ai[i][k]);
                    l = i;
                }
            }

            if (l != k) {
                double t;
                for (int j = 0; j <= degree; j++) {
                    t = ai[k][j];
                    ai[k][j] = ai[l][j];
                    ai[l][j] = t;
                }
                t = b[k];
                b[k] = b[l];
                b[l] = t;
            }

            for (int i = k + 1; i <= degree; i++) {
                double q = ai[i][k] / ai[k][k];
                for (int j = 0; j <= degree; j++) {
                    if (j == k) {
                        ai[i][j] = 0;
                    } else {
                        ai[i][j] = ai[i][j] - q * ai[k][j];
                    }
                }
                b[i] = b[i] - q * b[k];
            }
        }

        double[] x = new double[degree + 1];
        x[degree] = b[degree] / ai[degree][degree];
        for (int i = degree - 1; i >= 0; i--) {
            double t = 0;
            for (int j = 0; j <= degree - 1 - i; j++) {
                t += ai[i][i + j + 1] * x[i + j + 1];
                x[i] = (b[i] - t) / ai[i][i];
            }
        }

        double[] fx = new double[bars + 1];
        for (int n = 0; n <= bars; n++) {
            double sum = x[0];
            for (int k = 1; k <= degree; k++) {
                sum += x[k] * Math.pow(n, k);
            }
            fx[n] = sum;
        }

        double sq = 0;
        for (int n = 0; n <= bars; n++) {
            sq += Math.pow(inputs[0][endIndex - bars + n] - fx[n], 2);
        }
        sq = Math.sqrt(sq / (bars + 1)) * kstd;

        for (int n = 0; n <= bars; n++) {
            if (endIndex - startIndex - bars + n < 0) {
                continue;
            }
            outputs[0][endIndex - startIndex - bars + n] = fx[n];
            outputs[1][endIndex - startIndex - bars + n] = fx[n] + sq;
            outputs[2][endIndex - startIndex - bars + n] = fx[n] - sq;
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
            degree = (Integer) value;
            break;
        case 1:
            kstd = (Double) value;
            break;
        case 2:
            bars = (Integer) value;
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return bars;
    }

    public int getLookforward() {
        return 0;
    }
}
