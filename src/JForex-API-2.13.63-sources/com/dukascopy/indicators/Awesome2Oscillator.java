/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
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
public class Awesome2Oscillator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[2][];

    private int fastTimePeriod = 5;
    private int slowTimePeriod = 34;

    private IIndicator fastMa;
    private IIndicator slowMa;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("Awesome2", "Awesome Oscillator", "Bill Williams", false, false, false, 1, 4, 2);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);

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
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Zero", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setColor2(DefaultColors.RED);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        fastMa = indicatorsProvider.getIndicator("MA");
        fastMa.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
        slowMa = indicatorsProvider.getIndicator("MA");
        slowMa.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] dmax;
        double[] dmin;

        dmax = new double[endIndex - startIndex + 1];
        dmin = new double[endIndex - startIndex + 1];

        slowMa.setInputParameter(0, inputs[0]);
        fastMa.setInputParameter(0, inputs[0]);

        slowMa.setOutputParameter(0, dmax);
        fastMa.setOutputParameter(0, dmin);

        IndicatorResult dMaxSmaResult = slowMa.calculate(startIndex, endIndex);
        IndicatorResult dMinSmaResult = fastMa.calculate(startIndex, endIndex);

        if (dMinSmaResult.getFirstValueIndex() != dMaxSmaResult.getFirstValueIndex() ||
                dMaxSmaResult.getNumberOfElements() != dMinSmaResult.getNumberOfElements()) {
            throw new RuntimeException("Something wrong in ma calculation");
        }

        int i, k;
        for (i = 0, k = dMaxSmaResult.getNumberOfElements(); i < k; i++) {
            outputs[0][i] = dmin[i] - dmax[i];
            outputs[1][i] = 0;
        }

        return new IndicatorResult(startIndex, i);
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
            fastTimePeriod = (Integer) value;
            fastMa.setOptInputParameter(0, fastTimePeriod);
            break;
        case 1:
            int fastMaType = (Integer) value;
            fastMa.setOptInputParameter(1, fastMaType);
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
            slowMa.setOptInputParameter(1, slowMaType);
            indicatorInfo.setUnstablePeriod(
                    fastMa.getIndicatorInfo().isUnstablePeriod() ||
                    slowMa.getIndicatorInfo().isUnstablePeriod());
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
        return Math.max(slowMa.getLookback(), fastMa.getLookback());
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
