/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Feb 9, 2009
 * Time: 11:34:34 AM
 */
public class AwesomeOscillator implements IIndicator {
    private IIndicatorsProvider indicatorsProvider;
    private IIndicator fastMa;
    private IIndicator slowMa;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private double[][] inputs = new double[1][];
    private double[][] outputs = new double[3][];

    private int fastTimePeriod = 5;
    private int slowTimePeriod = 34;

    public void onStart(IIndicatorContext context) {
        indicatorsProvider = context.getIndicatorsProvider();
        fastMa = indicatorsProvider.getIndicator("MA");
        slowMa = indicatorsProvider.getIndicator("MA");	 

        indicatorInfo = new IndicatorInfo("Awesome", "Awesome Oscillator", "Bill Williams", false, false, false, 1, 4, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE) {{
                setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);
            }}
        };

        int[] maValues = new int[IIndicators.MaType.values().length - 2];
        String[] maNames = new String[IIndicators.MaType.values().length - 2];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("FasterMA Time Period", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(fastTimePeriod, 2, 2000, 1)),
            new OptInputParameterInfo("FasterMA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("SlowerMA Time Period", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(slowTimePeriod, 2, 2000, 1)),
            new OptInputParameterInfo("SlowerMA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Zero", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("Positive", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Negative", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] dmax;
        double[] dmin;

        dmax = new double[endIndex - startIndex + 2];
        dmin = new double[endIndex - startIndex + 2];

        slowMa.setInputParameter(0, inputs[0]);
        fastMa.setInputParameter(0, inputs[0]);

        slowMa.setOutputParameter(0, dmax);
        fastMa.setOutputParameter(0, dmin);

        IndicatorResult dMaxSmaResult = slowMa.calculate(startIndex - 1, endIndex);
        IndicatorResult dMinSmaResult = fastMa.calculate(startIndex - 1, endIndex);

        if (dMinSmaResult.getFirstValueIndex() != dMaxSmaResult.getFirstValueIndex() ||
                dMaxSmaResult.getNumberOfElements() != dMinSmaResult.getNumberOfElements()) {
            throw new RuntimeException("Something wrong in ma calculation");
        }

        double value, valueLast;
        int i, k;
        for (i = 1, k = dMaxSmaResult.getNumberOfElements(); i < k; i++) {
            double dminNow = dmin[i];
            double dminPrev = dmin[i - 1];
            double dmaxNow = dmax[i];
            double dmaxPrev = dmax[i - 1];
            valueLast = dminPrev - dmaxPrev;
            value = dminNow - dmaxNow;
            outputs[0][i - 1] = 0;
            if (value >= valueLast) {
                outputs[2][i - 1] = Double.NaN;
                outputs[1][i - 1] = value;
            } else {
                outputs[1][i - 1] = Double.NaN;
                outputs[2][i - 1] = value;
            }
        }

        return new IndicatorResult(startIndex, i - 1);
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
                fastTimePeriod = (Integer) value;
                fastMa.setOptInputParameter(0, fastTimePeriod);
                break;
            case 1:
                int fastMaType = (Integer) value;
                fastMa.setOptInputParameter(1, IIndicators.MaType.values()[fastMaType].ordinal());
                indicatorInfo.setUnstablePeriod(
                        fastMa.getIndicatorInfo().isUnstablePeriod() ||
                        slowMa.getIndicatorInfo().isUnstablePeriod());
                break;
            case 2:
                slowTimePeriod = (Integer) value;
                slowMa.setOptInputParameter(0, slowTimePeriod);
                break;
            case 3:
                int slowMaType = (Integer) value;
                slowMa.setOptInputParameter(1, IIndicators.MaType.values()[slowMaType].ordinal());
                indicatorInfo.setUnstablePeriod(
                        fastMa.getIndicatorInfo().isUnstablePeriod() ||
                        slowMa.getIndicatorInfo().isUnstablePeriod());
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(slowMa.getLookback(), fastMa.getLookback()) + 1;
    }

    public int getLookforward() {
        return 0;
    }
}
