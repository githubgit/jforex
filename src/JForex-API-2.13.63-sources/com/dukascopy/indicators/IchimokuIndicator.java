/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.DisplayMode;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IFormulaTimeData;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Aug 26, 2009
 * Time: 2:55:39 PM
 */
public class IchimokuIndicator implements IIndicator, IDrawingIndicator {

    public static final int SENKOU_A = 0;
    public static final int SENKOU_B = 1;

    private IIndicator tenkanMaxIndicator;
    private IIndicator tenkanMinIndicator;
    private IIndicator kijunMaxIndicator;
    private IIndicator kijunMinIndicator;
    private IIndicator senkouBMaxIndicator;
    private IIndicator senkouBMinIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[5][];
    private final Object[][] cloud = new Object[1][];

    private int tenkan = 9;
    private int kijun = 26;
    private int senkou = 52;

    @Override
    public void onStart(IIndicatorContext context) {
        tenkanMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        tenkanMaxIndicator.setOptInputParameter(0, tenkan);
        tenkanMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");
        tenkanMinIndicator.setOptInputParameter(0, tenkan);
        kijunMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        kijunMaxIndicator.setOptInputParameter(0, kijun);
        kijunMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");
        kijunMinIndicator.setOptInputParameter(0, kijun);
        senkouBMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        senkouBMaxIndicator.setOptInputParameter(0, senkou);
        senkouBMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");
        senkouBMinIndicator.setOptInputParameter(0, senkou);

        indicatorInfo = new IndicatorInfo("ICHIMOKU", "Ichimoku", "Overlap Studies", true, false, false, 1, 3, 6);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Tenkan", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(tenkan, 1, 400, 1)),
    		new OptInputParameterInfo("Kijun", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(kijun, 2, 400, 1)),
    		new OptInputParameterInfo("Senkou", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(senkou, 2, 400, 1))
		};

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Tenkan Sen", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Kijun Sen", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Chikou Span", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
			new OutputParameterInfo("Senkou A", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
			new OutputParameterInfo("Senkou B", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
			new OutputParameterInfo("Cloud", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.DARK_ORANGE);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[2].setColor(DefaultColors.DEEP_MAGENTA);
        outputParameterInfos[2].setShift(-kijun);
        outputParameterInfos[3].setColor(DefaultColors.RED);
        outputParameterInfos[3].setShift(kijun);
        outputParameterInfos[4].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[4].setShift(kijun);
		outputParameterInfos[5].setColor(DefaultColors.FOREST_GREEN);
		outputParameterInfos[5].setColor2(DefaultColors.DARK_RED);
		outputParameterInfos[5].setShift(kijun);
        outputParameterInfos[5].setOpacityAlpha(0.5f);
		outputParameterInfos[5].setDrawnByIndicator(true);
        outputParameterInfos[5].setDisplayOrder(0);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (endIndex + getLookforward() >= inputs[0][0].length) {
            endIndex = inputs[0][0].length - 1 - getLookforward();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] tenkanMax = new double[endIndex - startIndex + 1];
        double[] tenkanMin = new double[endIndex - startIndex + 1];
        double[] kijunMax = new double[endIndex - startIndex + 1];
        double[] kijunMin = new double[endIndex - startIndex + 1];
        double[] senkouBMax = new double[endIndex - startIndex + 1];
        double[] senkouBMin = new double[endIndex - startIndex + 1];

        // high value for max
        tenkanMaxIndicator.setInputParameter(0, inputs[0][2]);
        kijunMaxIndicator.setInputParameter(0, inputs[0][2]);
        senkouBMaxIndicator.setInputParameter(0, inputs[0][2]);
        // low value for min
        tenkanMinIndicator.setInputParameter(0, inputs[0][3]);
        kijunMinIndicator.setInputParameter(0, inputs[0][3]);
        senkouBMinIndicator.setInputParameter(0, inputs[0][3]);

        tenkanMaxIndicator.setOutputParameter(0, tenkanMax);
        tenkanMinIndicator.setOutputParameter(0, tenkanMin);
        kijunMaxIndicator.setOutputParameter(0, kijunMax);
        kijunMinIndicator.setOutputParameter(0, kijunMin);
        senkouBMaxIndicator.setOutputParameter(0, senkouBMax);
        senkouBMinIndicator.setOutputParameter(0, senkouBMin);

        IndicatorResult dtenkanMaxResult = tenkanMaxIndicator.calculate(startIndex, endIndex);
        IndicatorResult dtenkanMinResult = tenkanMinIndicator.calculate(startIndex, endIndex);
        IndicatorResult dkijunMaxResult = kijunMaxIndicator.calculate(startIndex, endIndex);
        IndicatorResult dkijunMinResult = kijunMinIndicator.calculate(startIndex, endIndex);
        IndicatorResult dsenkouBMaxResult = senkouBMaxIndicator.calculate(startIndex, endIndex);
        IndicatorResult dsenkouBMinResult = senkouBMinIndicator.calculate(startIndex, endIndex);

        int i, k;
        for (i = 0, k = dtenkanMaxResult.getNumberOfElements(); i < k; i++) {
            outputs[0][i] = (tenkanMax[i] + tenkanMin[i]) / 2;
            outputs[1][i] = (kijunMax[i] + kijunMin[i]) / 2;

            // senkou A
            outputs[3][i] = (outputs[1][i] + outputs[0][i]) / 2;
            outputs[4][i] = (senkouBMax[i] + senkouBMin[i]) / 2;

            cloud[0][i] = new double[] {outputs[3][i], outputs[4][i]};
        }

        int resIndex = 0;
        // chikou = close with shift by tenkan param
        for (int z = startIndex; z <= endIndex; z++, resIndex++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            outputs[2][resIndex] = inputs[0][1][z];
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
        inputs[index] = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                tenkan = (Integer) value;

                tenkanMaxIndicator.setOptInputParameter(0, tenkan);
                tenkanMinIndicator.setOptInputParameter(0, tenkan);
                break;

            case 1:
                if (kijun != (Integer) value) {
                    kijun = (Integer) value;

                    kijunMaxIndicator.setOptInputParameter(0, kijun);
                    kijunMinIndicator.setOptInputParameter(0, kijun);

                    outputParameterInfos[2].setShift(-kijun);
                    outputParameterInfos[3].setShift(kijun);
                    outputParameterInfos[4].setShift(kijun);
                    outputParameterInfos[5].setShift(kijun);
                }
                break;

            case 2:
                senkou = (Integer) value;

                senkouBMaxIndicator.setOptInputParameter(0, senkou);
                senkouBMinIndicator.setOptInputParameter(0, senkou);
                break;

            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        if (index < 5) {
            outputs[index] = (double[]) array;
        } else {
            cloud[0] = (Object[]) array;
        }
    }

    @Override
    public int getLookback() {
        return Math.max(tenkan, Math.max(kijun, senkou)) - 1;
    }

    @Override
    public int getLookforward() {
        return 0;
    }

    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values2, Color color, Stroke stroke,
                           IIndicatorDrawingSupport indicatorDrawingSupport, java.util.List<Shape> shapes,
                           Map<Color, java.util.List<Point>> handles) {
        Object[] values = (Object[]) values2;
        if (values == null) {
            return null;
        }

        float candleWidth = indicatorDrawingSupport.getCandleWidthInPixels();
        float spaceBetweenCandles = indicatorDrawingSupport.getSpaceBetweenCandlesInPixels();

        boolean formulaHasSmallerPeriod = false;
        Period formulaPeriod = indicatorDrawingSupport.getFormulaPeriod();
        DisplayMode displayMode = indicatorDrawingSupport.getDisplayMode();
        if (formulaPeriod != null) {
            if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                Period chartPeriod = indicatorDrawingSupport.getPeriod();
                if (chartPeriod != Period.TICK) {
                    formulaHasSmallerPeriod = formulaPeriod.isSmallerThan(chartPeriod);
                    if (formulaHasSmallerPeriod) {
                        float koef = chartPeriod.getInterval() / formulaPeriod.getInterval();
                        candleWidth /= koef;
                        spaceBetweenCandles /= koef;
                    }
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        int shiftA = outputParameterInfos[3].getShift();
        int shiftB = outputParameterInfos[4].getShift();
        int maxShift = Math.max(shiftA, shiftB);

        int firstCandleIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen();
        int lastCandleIndex = firstCandleIndex + indicatorDrawingSupport.getNumberOfCandlesOnScreen() - 1;
        firstCandleIndex--;
        lastCandleIndex++;
        if (maxShift > 0) {
            lastCandleIndex = indicatorDrawingSupport.getShiftedIndex(lastCandleIndex, maxShift, displayMode);
        }
        if (firstCandleIndex < 0) {
            firstCandleIndex = 0;
        }

        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();

        int xPrevA = Integer.MIN_VALUE, xPrevB = Integer.MIN_VALUE;
        double valuePrevA = Double.NaN, valuePrevB = Double.NaN;
        Color color2 = indicatorDrawingSupport.getDowntrendColor();

        for (int idx = firstCandleIndex; idx <= lastCandleIndex; idx++) {
            List<ITimedData> formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (idx < timeData.length) {
                        formulaBars = formulaTimeData.getFormulaBars(
                                timeData[idx], idx > 0 ? timeData[idx - 1] : null, idx < timeData.length - 1 ? timeData[idx + 1] : null);
                    } else {
                        formulaBars = Collections.emptyList();
                    }
                } else if (idx > firstCandleIndex && idx < lastCandleIndex) {
                    if (idx < timeData.length && formulaTimeData.getNextFormulaBar(timeData[idx]) != null) {
                        if (!formulaTimeData.isFormulaBarDisplayTime(timeData, idx, displayMode)) {
                            continue;
                        }
                    } else {
                        int idx2 = indicatorDrawingSupport.getShiftedIndex(idx, -1, displayMode);
                        idx2 = indicatorDrawingSupport.getShiftedIndex(idx2, 1, displayMode);
                        if (idx2 != idx) {
                            continue;
                        }
                    }
                }
            }

            int shiftedIdxA = indicatorDrawingSupport.getShiftedIndex(idx, -shiftA, displayMode);
            int shiftedIdxB = indicatorDrawingSupport.getShiftedIndex(idx, -shiftB, displayMode);

            double valueA = (shiftedIdxA >= 0 && shiftedIdxA <= values.length - 1 && values[shiftedIdxA] != null ?
                    ((double[]) values[shiftedIdxA])[SENKOU_A] : Double.NaN);
            double valueB = (shiftedIdxB >= 0 && shiftedIdxB <= values.length - 1 && values[shiftedIdxB] != null ?
                    ((double[]) values[shiftedIdxB])[SENKOU_B] : Double.NaN);

            for (int valueIdx = 0; valueIdx < (formulaBars != null ? formulaBars.size() : 1); valueIdx++) {
                if (formulaBars != null && formulaTimeData != null) {
                    long time = formulaBars.get(valueIdx).getTime();
                    long shiftedTime = indicatorDrawingSupport.getShiftedTime(time, -shiftA);
                    Object formulaValue = formulaTimeData.getFormulaValue(shiftedTime, outputIdx);
                    if (formulaValue instanceof double[]) {
                        valueA = ((double[]) formulaValue)[SENKOU_A];
                    }
                    shiftedTime = indicatorDrawingSupport.getShiftedTime(time, -shiftB);
                    formulaValue = formulaTimeData.getFormulaValue(shiftedTime, outputIdx);
                    if (formulaValue instanceof double[]) {
                        valueB = ((double[]) formulaValue)[SENKOU_B];
                    }
                }

                int[] x = new int[4];
                x[0] = xPrevA;
                if (formulaBars != null) {
                    long time = formulaBars.get(valueIdx).getTime();
                    x[1] = indicatorDrawingSupport.getXForTime(time, false);
                    if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                        x[1] += (spaceBetweenCandles + candleWidth) / 2;
                    }
                } else {
                    x[1] = (int) indicatorDrawingSupport.getMiddleOfCandle(idx);
                }
                x[2] = x[1];
                x[3] = xPrevB;

                if (xPrevA != Integer.MIN_VALUE && xPrevB != Integer.MIN_VALUE &&
                        !Double.isNaN(valueA) && !Double.isNaN(valueB) && !Double.isNaN(valuePrevA) && !Double.isNaN(valuePrevB)) {

                    int[] y = new int[4];
                    y[0] = (int) indicatorDrawingSupport.getYForValue(valuePrevA);
                    y[1] = (int) indicatorDrawingSupport.getYForValue(valueA);
                    y[2] = (int) indicatorDrawingSupport.getYForValue(valueB);
                    y[3] = (int) indicatorDrawingSupport.getYForValue(valuePrevB);

                    if ((y[0] <= y[3]) == (y[1] <= y[2])) {
                        g.setColor(y[0] <= y[3] ? color : color2);
                        g.fillPolygon(x, y, 4);

                    } else if ((y[1]-y[0])+(y[3]-y[2]) != 0 && x[3]-x[2] != 0) {
                        int xc = ((x[0]*y[1]-x[1]*y[0])+(x[2]*y[3]-x[3]*y[2]))/((y[1]-y[0])+(y[3]-y[2]));
                        int yc = ((y[3]-y[2])*xc-(x[2]*y[3]-x[3]*y[2]))/(x[3]-x[2]);

                        g.setColor(y[0] <= y[3] ? color : color2);
                        g.fillPolygon(new int[] {x[0], xc, x[3]}, new int[] {y[0], yc, y[3]}, 3);

                        g.setColor(y[1] <= y[2] ? color : color2);
                        g.fillPolygon(new int[] {xc, x[1], x[2]}, new int[] {yc, y[1], y[2]}, 3);
                    }
                }

                xPrevA = x[1];
                xPrevB = x[2];
                valuePrevA = valueA;
                valuePrevB = valueB;
            }
        }

        return null;
    }
}
