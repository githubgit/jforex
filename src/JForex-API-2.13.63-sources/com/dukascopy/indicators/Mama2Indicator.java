package com.dukascopy.indicators;

import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class Mama2Indicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[2][];

    private double fastLimit = 0.5;
    private double slowLimit = 0.05;
    private int lookback = 32;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MAMA2", "MESA Adaptive Moving Average", "Overlap Studies", true, false, true, 1, 3, 2);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Fast Limit", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(fastLimit, 0.01, 0.99, 0.01, 2)),
            new OptInputParameterInfo("Slow Limit", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(slowLimit, 0.01, 0.99, 0.01, 2)),
            new OptInputParameterInfo("Lookback", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(lookback, 12, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("MAMA", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("FAMA", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)
        };
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex < getLookback()) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int outIdx, i;
        int today;
        double tempReal, tempReal2;
        double adjustedPrevPeriod, period;
        int trailingWMAIdx;
        double periodWMASum, periodWMASub, trailingWMAValue;
        double smoothedValue;
        final double a = 0.0962;
        final double b = 0.5769;
        double hilbertTempReal;
        int hilbertIdx;
        double[] detrender_Odd = new double[3];
        double[] detrender_Even = new double[3];
        double detrender;
        double prev_detrender_Odd;
        double prev_detrender_Even;
        double prev_detrender_input_Odd;
        double prev_detrender_input_Even;
        double[] Q1_Odd = new double[3];
        double[] Q1_Even = new double[3];
        double Q1;
        double prev_Q1_Odd;
        double prev_Q1_Even;
        double prev_Q1_input_Odd;
        double prev_Q1_input_Even;
        double[] jI_Odd = new double[3];
        double[] jI_Even = new double[3];
        double jI;
        double prev_jI_Odd;
        double prev_jI_Even;
        double prev_jI_input_Odd;
        double prev_jI_input_Even;
        double[] jQ_Odd = new double[3];
        double[] jQ_Even = new double[3];
        double jQ;
        double prev_jQ_Odd;
        double prev_jQ_Even;
        double prev_jQ_input_Odd;
        double prev_jQ_input_Even;
        double Q2, I2, prevQ2, prevI2, Re, Im;
        double I1ForOddPrev2, I1ForOddPrev3;
        double I1ForEvenPrev2, I1ForEvenPrev3;
        double rad2Deg;
        double mama, fama, todayValue, prevPhase;

        rad2Deg = 180.0 / (4.0 * Math.atan(1));
        trailingWMAIdx = startIndex - lookback;
        today = trailingWMAIdx;
        tempReal = inputs[0][today++];
        periodWMASub = tempReal;
        periodWMASum = tempReal;
        tempReal = inputs[0][today++];
        periodWMASub += tempReal;
        periodWMASum += tempReal * 2.0;
        tempReal = inputs[0][today++];
        periodWMASub += tempReal;
        periodWMASum += tempReal * 3.0;
        trailingWMAValue = 0.0;
        i = 9;

        do {
            tempReal = inputs[0][today++];
            periodWMASub += tempReal;
            periodWMASub -= trailingWMAValue;
            periodWMASum += tempReal * 4.0;
            trailingWMAValue = inputs[0][trailingWMAIdx++];
            smoothedValue = periodWMASum * 0.1;
            periodWMASum -= periodWMASub;
        } while (--i != 0);

        hilbertIdx = 0;
        detrender_Odd[0] = 0.0;
        detrender_Odd[1] = 0.0;
        detrender_Odd[2] = 0.0;
        detrender_Even[0] = 0.0;
        detrender_Even[1] = 0.0;
        detrender_Even[2] = 0.0;
        detrender = 0.0;
        prev_detrender_Odd = 0.0;
        prev_detrender_Even = 0.0;
        prev_detrender_input_Odd = 0.0;
        prev_detrender_input_Even = 0.0;

        Q1_Odd[0] = 0.0;
        Q1_Odd[1] = 0.0;
        Q1_Odd[2] = 0.0;
        Q1_Even[0] = 0.0;
        Q1_Even[1] = 0.0;
        Q1_Even[2] = 0.0;
        Q1 = 0.0;
        prev_Q1_Odd = 0.0;
        prev_Q1_Even = 0.0;
        prev_Q1_input_Odd = 0.0;
        prev_Q1_input_Even = 0.0;

        jI_Odd[0] = 0.0;
        jI_Odd[1] = 0.0;
        jI_Odd[2] = 0.0;
        jI_Even[0] = 0.0;
        jI_Even[1] = 0.0;
        jI_Even[2] = 0.0;
        jI = 0.0;
        prev_jI_Odd = 0.0;
        prev_jI_Even = 0.0;
        prev_jI_input_Odd = 0.0;
        prev_jI_input_Even = 0.0;

        jQ_Odd[0] = 0.0;
        jQ_Odd[1] = 0.0;
        jQ_Odd[2] = 0.0;
        jQ_Even[0] = 0.0;
        jQ_Even[1] = 0.0;
        jQ_Even[2] = 0.0;
        jQ = 0.0;
        prev_jQ_Odd = 0.0;
        prev_jQ_Even = 0.0;
        prev_jQ_input_Odd = 0.0;
        prev_jQ_input_Even = 0.0;

        period = 0.0;
        outIdx = 0;
        prevI2 = prevQ2 = 0.0;
        Re = Im = 0.0;
        mama = fama = 0.0;
        I1ForOddPrev3 = I1ForEvenPrev3 = 0.0;
        I1ForOddPrev2 = I1ForEvenPrev2 = 0.0;
        prevPhase = 0.0;

        while (today <= endIndex) {
            adjustedPrevPeriod = 0.075 * period + 0.54;
            todayValue = inputs[0][today];

            periodWMASub += todayValue;
            periodWMASub -= trailingWMAValue;
            periodWMASum += todayValue * 4.0;
            trailingWMAValue = inputs[0][trailingWMAIdx++];
            smoothedValue = periodWMASum * 0.1;
            periodWMASum -= periodWMASub;

            if (today % 2 == 0) {
                hilbertTempReal = a * smoothedValue;
                detrender = -detrender_Even[hilbertIdx];
                detrender_Even[hilbertIdx] = hilbertTempReal;
                detrender += hilbertTempReal;
                detrender -= prev_detrender_Even;
                prev_detrender_Even = b * prev_detrender_input_Even;
                detrender += prev_detrender_Even;
                prev_detrender_input_Even = smoothedValue;
                detrender *= adjustedPrevPeriod;

                hilbertTempReal = a * detrender;
                Q1 = -Q1_Even[hilbertIdx];
                Q1_Even[hilbertIdx] = hilbertTempReal;
                Q1 += hilbertTempReal;
                Q1 -= prev_Q1_Even;
                prev_Q1_Even = b * prev_Q1_input_Even;
                Q1 += prev_Q1_Even;
                prev_Q1_input_Even = detrender;
                Q1 *= adjustedPrevPeriod;

                hilbertTempReal = a * I1ForEvenPrev3;
                jI = -jI_Even[hilbertIdx];
                jI_Even[hilbertIdx] = hilbertTempReal;
                jI += hilbertTempReal;
                jI -= prev_jI_Even;
                prev_jI_Even = b * prev_jI_input_Even;
                jI += prev_jI_Even;
                prev_jI_input_Even = I1ForEvenPrev3;
                jI *= adjustedPrevPeriod;

                hilbertTempReal = a * Q1;
                jQ = -jQ_Even[hilbertIdx];
                jQ_Even[hilbertIdx] = hilbertTempReal;
                jQ += hilbertTempReal;
                jQ -= prev_jQ_Even;
                prev_jQ_Even = b * prev_jQ_input_Even;
                jQ += prev_jQ_Even;
                prev_jQ_input_Even = Q1;
                jQ *= adjustedPrevPeriod;

                if (++hilbertIdx == 3) {
                    hilbertIdx = 0;
                }
                Q2 = 0.2 * (Q1 + jI) + 0.8 * prevQ2;
                I2 = 0.2 * (I1ForEvenPrev3 - jQ) + 0.8 * prevI2;
                I1ForOddPrev3 = I1ForOddPrev2;
                I1ForOddPrev2 = detrender;
                if (I1ForEvenPrev3 != 0.0) {
                    tempReal2 = Math.atan(Q1 / I1ForEvenPrev3) * rad2Deg;
                } else {
                    tempReal2 = 0.0;
                }

            } else {
                hilbertTempReal = a * smoothedValue;
                detrender = -detrender_Odd[hilbertIdx];
                detrender_Odd[hilbertIdx] = hilbertTempReal;
                detrender += hilbertTempReal;
                detrender -= prev_detrender_Odd;
                prev_detrender_Odd = b * prev_detrender_input_Odd;
                detrender += prev_detrender_Odd;
                prev_detrender_input_Odd = smoothedValue;
                detrender *= adjustedPrevPeriod;

                hilbertTempReal = a * detrender;
                Q1 = -Q1_Odd[hilbertIdx];
                Q1_Odd[hilbertIdx] = hilbertTempReal;
                Q1 += hilbertTempReal;
                Q1 -= prev_Q1_Odd;
                prev_Q1_Odd = b * prev_Q1_input_Odd;
                Q1 += prev_Q1_Odd;
                prev_Q1_input_Odd = detrender;
                Q1 *= adjustedPrevPeriod;

                hilbertTempReal = a * I1ForOddPrev3;
                jI = -jI_Odd[hilbertIdx];
                jI_Odd[hilbertIdx] = hilbertTempReal;
                jI += hilbertTempReal;
                jI -= prev_jI_Odd;
                prev_jI_Odd = b * prev_jI_input_Odd;
                jI += prev_jI_Odd;
                prev_jI_input_Odd = I1ForOddPrev3;
                jI *= adjustedPrevPeriod;

                hilbertTempReal = a * Q1;
                jQ = -jQ_Odd[hilbertIdx];
                jQ_Odd[hilbertIdx] = hilbertTempReal;
                jQ += hilbertTempReal;
                jQ -= prev_jQ_Odd;
                prev_jQ_Odd = b * prev_jQ_input_Odd;
                jQ += prev_jQ_Odd;
                prev_jQ_input_Odd = Q1;
                jQ *= adjustedPrevPeriod;

                Q2 = 0.2 * (Q1 + jI) + 0.8 * prevQ2;
                I2 = 0.2 * (I1ForOddPrev3 - jQ) + 0.8 * prevI2;
                I1ForEvenPrev3 = I1ForEvenPrev2;
                I1ForEvenPrev2 = detrender;
                if (I1ForOddPrev3 != 0.0) {
                    tempReal2 = Math.atan(Q1 / I1ForOddPrev3) * rad2Deg;
                } else {
                    tempReal2 = 0.0;
                }
            }

            tempReal = prevPhase - tempReal2;
            prevPhase = tempReal2;
            if (tempReal < 1.0) {
                tempReal = 1.0;
            }
            if (tempReal > 1.0) {
                tempReal = fastLimit / tempReal;
                if (tempReal < slowLimit) {
                    tempReal = slowLimit;
                }
            } else {
                tempReal = fastLimit;
            }

            mama = tempReal * todayValue + (1 - tempReal) * mama;
            tempReal *= 0.5;
            fama = tempReal * mama + (1 - tempReal) * fama;

            if (today >= startIndex) {
                outputs[0][outIdx] = mama;
                outputs[1][outIdx++] = fama;
            }

            Re = 0.2 * (I2 * prevI2 + Q2 * prevQ2) + 0.8 * Re;
            Im = 0.2 * (I2 * prevQ2 - Q2 * prevI2) + 0.8 * Im;
            prevQ2 = Q2;
            prevI2 = I2;
            tempReal = period;
            if (Im != 0.0 && Re != 0.0) {
                period = 360.0 / (Math.atan(Im / Re) * rad2Deg);
            }
            tempReal2 = 1.5 * tempReal;
            if (period > tempReal2) {
                period = tempReal2;
            }
            tempReal2 = 0.67 * tempReal;
            if (period < tempReal2) {
                period = tempReal2;
            }
            if (period < 6) {
                period = 6;
            } else if (period > 50) {
                period = 50;
            }
            period = 0.2 * period + 0.8 * tempReal;

            today++;
        }

        return new IndicatorResult(startIndex, outIdx);
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
        switch (index) {
        case 0:
            fastLimit = (Double) value;
            break;
        case 1:
            slowLimit = (Double) value;
            break;
        case 2:
            lookback = (Integer) value;
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
        return lookback;
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
