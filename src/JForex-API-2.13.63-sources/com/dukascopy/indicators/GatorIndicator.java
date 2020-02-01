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
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IDrawingIndicator;
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
 * Date: Dec 17, 2009
 */
public class GatorIndicator implements IIndicator, IDrawingIndicator {

    private IIndicator jawSmmaIndicator;
    private IIndicator teethSmmaIndicator;
    private IIndicator lipsSmmaIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private int jawPeriod = 13;
    private int teethPeriod = 8;
    private int lipsPeriod = 5;

    private final int jawShift = 8;
    private final int teethShift = 5;
    private final int lipsShift = 3;

    public void onStart(IIndicatorContext context) {
        jawSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");
        teethSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");
        lipsSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");

        indicatorInfo = new IndicatorInfo("GATOR", "Gator Oscillator", "Bill Williams", false, false, true, 1, 3, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Jaw Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(jawPeriod, 2, 200, 1)),
            new OptInputParameterInfo("Teeth Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(teethPeriod, 2, 200, 1)),
            new OptInputParameterInfo("Lips Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(lipsPeriod, 2, 200, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Positive", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Negative", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Zero", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)
        };

        outputParameterInfos[0].setShift(teethShift);
        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setDrawnByIndicator(true);
        outputParameterInfos[1].setShift(lipsShift);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[1].setDrawnByIndicator(true);
        outputParameterInfos[2].setShift(teethShift);
        outputParameterInfos[2].setColor(DefaultColors.ROYAL_BLUE);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] jawSma = new double[endIndex - startIndex + 2 + getLookback()];
        double[] teethSma = new double[endIndex - startIndex + 2 + getLookback()];
        double[] lipsSma = new double[endIndex - startIndex + 2 + getLookback()];

        jawSmmaIndicator.setInputParameter(0, inputs[0]);
        teethSmmaIndicator.setInputParameter(0, inputs[0]);
        lipsSmmaIndicator.setInputParameter(0, inputs[0]);

        jawSmmaIndicator.setOutputParameter(0, jawSma);
        teethSmmaIndicator.setOutputParameter(0, teethSma);
        lipsSmmaIndicator.setOutputParameter(0, lipsSma);

        IndicatorResult dJawSmaResult = jawSmmaIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dTeethSmaResult = teethSmmaIndicator.calculate(startIndex - 1, endIndex);
        IndicatorResult dLipsSmaResult = lipsSmmaIndicator.calculate(startIndex - 1, endIndex);

        int i, k;
        for (i = 1, k = dJawSmaResult.getNumberOfElements(); i < k; i++) {
            double value = Math.abs(jawSma[i] - teethSma[i]);
            outputs[0][i - 1] = value;
            outputs[1][i - 1] = -1 * Math.abs(teethSma[i] - lipsSma[i]);
            outputs[2][i - 1] = 0;
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
                jawPeriod = (Integer) value;
                jawSmmaIndicator.setOptInputParameter(0, jawPeriod);
                break;
            case 1:
                teethPeriod = (Integer) value;
                teethSmmaIndicator.setOptInputParameter(0, teethPeriod);
                break;
            case 2:
                lipsPeriod = (Integer) value;
                lipsSmmaIndicator.setOptInputParameter(0, lipsPeriod);
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(teethPeriod + teethShift, Math.max(jawPeriod + jawShift, lipsPeriod + lipsShift)) + 1;
    }

    public int getLookforward() {
        return 0;
    }    

    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values2, Color color, Stroke stroke,
            IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> shapes, Map<Color, List<Point>> handles
    ) {
        double[] values = (double[]) values2;
        if (values == null) {
            return null;
        }

        int lastX = -1;
        int lastY = -1;

        int shift = teethShift;
        if (outputIdx == 1){
            shift = lipsShift;
        }

        Color color2 = indicatorDrawingSupport.getDowntrendColor();

        for (int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() - shift, k =
                i + indicatorDrawingSupport.getNumberOfCandlesOnScreen() + shift; i < k; i++) {

            if (i >=0 && values.length >= i &&((outputIdx == 0 && values[i]!=0) || (outputIdx == 1 && values[i]!=0))) {
                if (outputIdx == 0) {
                    if (i > 1) {
                        if (Math.abs(values[i]) > Math.abs(values[i-1]))  {
                            g.setColor(color);
                        } else {
                            g.setColor(color2);
                        }
                    } else {
                        g.setColor(color);
                    }

                    int x = (int) indicatorDrawingSupport.getMiddleOfCandle(i + shift) - 4;
                    int y = (int) indicatorDrawingSupport.getYForValue(values[i]);
                    int width = 4;
                    int height = - (int) indicatorDrawingSupport.getYForValue(values[i]) + (int) indicatorDrawingSupport.getYForValue(0);

                	g.fillRect(x, y, width, height);

                    if (lastX < x + width) {
                        lastX = x + width;
                        lastY = y;
                    }

                } else if (outputIdx == 1) {
                    if (i > 1) {
                        if (Math.abs(values[i]) > Math.abs(values[i-1]))  {
                            g.setColor(color);
                        } else {
                            g.setColor(color2);
                        }
                    } else {
                        g.setColor(color);
                    }

                    int x = (int) indicatorDrawingSupport.getMiddleOfCandle(i + shift) - 4;
                    int y =    (int) indicatorDrawingSupport.getYForValue(0);
                    int width = 4;
                    int height = + (int) indicatorDrawingSupport.getYForValue(values[i]) - (int) indicatorDrawingSupport.getYForValue(0);

                	g.fillRect(x, y, width, height);

                    if (lastX < x + width) {
                        lastX = x + width;
                        lastY = y;
                    }
                }
            }
        }

        return new Point(lastX, lastY);
    }
}
