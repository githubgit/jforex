/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.IBar;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Instrument;
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
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class. * 
 * 
 * Created by: S.Vishnyakov
 * Date: May 20, 2009
 * Time: 7:00:10 PM
 */
public class ZigZagIndicator implements IIndicator, IDrawingIndicator {
    private IIndicatorContext indicatorContext;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[2][];

    private int extDepth = 12;
    private int extDeviation = 5;
    private int extBackstep = 3;

    private double instrPips = Instrument.EURUSD.getPipValue();

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    public void onStart(IIndicatorContext context) {
        indicatorContext = context;

        indicatorInfo = new IndicatorInfo("ZigZag", "ZigZag", "", true, false, false, 1, 3, 2);
        indicatorInfo.setRecalculateAll(true);
        indicatorInfo.setSparseIndicator(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("ExtDepth", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(extDepth, 1, 600, 1)),
            new OptInputParameterInfo("ExtDeviation", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(extDeviation, 5, 600, 1)),
            new OptInputParameterInfo("ExtBackstep", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(extBackstep, 3, 600, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("ZigZag", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Distances", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[1].setDrawnByIndicator(true);
        outputParameterInfos[1].setShowOutput(false);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        if (indicatorContext.getFeedDescriptor() != null) {
            instrPips = indicatorContext.getFeedDescriptor().getInstrument().getPipValue();
        }

        double[] lowMapBuffer = new double[endIndex + 1];
        double[] highMapBuffer = new double[endIndex + 1];
        double[] zigzagBuffer = new double[endIndex + 1];

        int whatlookfor = 0;
        int shift, back, lasthighpos = 0, lastlowpos = 0;
        double val, res;
        double curlow = 0, curhigh = 0, lasthigh = 0, lastlow = 0;

        Arrays.fill(lowMapBuffer, 0);
        Arrays.fill(highMapBuffer, 0);
        Arrays.fill(zigzagBuffer, 0);

        for (shift = startIndex; shift <= endIndex; shift++) {
            val = inputs[0][3][shift];
            for (int j = shift - 1; j > (shift - extDepth); j--) {
                val = Math.min(val, inputs[0][3][j]);
            }
            if (val == lastlow) val = 0.0;
            else {
                lastlow = val;
                if ((inputs[0][3][shift] - val) > (extDeviation * instrPips)) {
                    val = 0.0;
                } else {
                    for (back = 1; back <= extBackstep; back++) {
                        res = lowMapBuffer[shift - back];
                        if ((res != 0) && (res > val)) {
                            lowMapBuffer[shift - back] = 0.0;
                        }
                    }
                }
            }
            if (inputs[0][3][shift] == val) {
                lowMapBuffer[shift] = val;
            } else {
                lowMapBuffer[shift] = 0.0;
            }
            //--- high
            val = inputs[0][2][shift];
            for (int j = shift - 1; j > (shift - extDepth); j--) {
                val = Math.max(val, inputs[0][2][j]);
            }
            if (val == lasthigh) {
                val = 0.0;
            } else {
                lasthigh = val;
                if ((val - inputs[0][2][shift]) > (extDeviation * instrPips)) {
                    val = 0.0;
                } else {
                    for (back = 1; back <= extBackstep; back++) {
                        res = highMapBuffer[shift - back];
                        if ((res != 0) && (res < val)) highMapBuffer[shift - back] = 0.0;
                    }
                }
            }
            if (inputs[0][2][shift] == val) {
                highMapBuffer[shift] = val;
            } else {
                highMapBuffer[shift] = 0.0;
            }
        }

        // final cutting
        if (whatlookfor == 0) {
            lastlow = 0;
            lasthigh = 0;
        } else {
            lastlow = curlow;
            lasthigh = curhigh;
        }

        for (shift = startIndex; shift <= endIndex; shift++) {
            switch (whatlookfor) {
                case 0: // look for peak or lawn
                    if (highMapBuffer[shift] != 0) {
                        lasthigh = inputs[0][2][shift];
                        lasthighpos = shift;
                        whatlookfor = -1;
                        zigzagBuffer[shift] = lasthigh;
                    }
                    if (lowMapBuffer[shift] != 0) {
                        lastlow = inputs[0][3][shift];
                        lastlowpos = shift;
                        whatlookfor = 1;
                        zigzagBuffer[shift] = lastlow;
                    }
                    break;
                case 1: // look for peak
                    if (lowMapBuffer[shift] != 0.0 && lowMapBuffer[shift] < lastlow && highMapBuffer[shift] == 0.0) {
                        zigzagBuffer[lastlowpos] = 0.0;
                        lastlowpos = shift;
                        lastlow = lowMapBuffer[shift];
                        zigzagBuffer[shift] = lastlow;
                    }
                    if (highMapBuffer[shift] != 0.0 && lowMapBuffer[shift] == 0.0) {
                        lasthigh = highMapBuffer[shift];
                        lasthighpos = shift;
                        zigzagBuffer[shift] = lasthigh;
                        whatlookfor = -1;
                    }
                    break;
                case-1: // look for lawn
                    if (highMapBuffer[shift] != 0.0 && highMapBuffer[shift] > lasthigh && lowMapBuffer[shift] == 0.0) {
                        zigzagBuffer[lasthighpos] = 0.0;
                        lasthighpos = shift;
                        lasthigh = highMapBuffer[shift];
                        zigzagBuffer[shift] = lasthigh;
                    }
                    if (lowMapBuffer[shift] != 0.0 && highMapBuffer[shift] == 0.0) {
                        lastlow = lowMapBuffer[shift];
                        lastlowpos = shift;
                        zigzagBuffer[shift] = lastlow;
                        whatlookfor = 1;
                    }
                    break;
            }
        }

        double prevValue = Double.NaN;

        int iz, j;
        for (iz = 0, j = startIndex; j <= endIndex; iz++, j++) {
            if (zigzagBuffer[j] != 0) {
                outputs[0][iz] = zigzagBuffer[j];
                if (!Double.isNaN(prevValue)) {
                    outputs[1][iz] = round((zigzagBuffer[j] - prevValue) / instrPips, 1);
                } else {
                    outputs[1][iz] = Double.NaN;
                }
                prevValue = zigzagBuffer[j];
            } else {
                outputs[0][iz] = Double.NaN;
                outputs[1][iz] = Double.NaN;
            }
        }

        return new IndicatorResult(startIndex, iz);
    }

    private double round(double value, int scale) {
        if (!Double.isNaN(value)) {
            value = BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_EVEN).doubleValue();
        }
        return value;
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
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                extDepth = (Integer) value;
                break;
            case 1:
                extDeviation = (Integer) value;
                break;
            case 2:
                extBackstep = (Integer) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(extBackstep, Math.max(extDepth, extDeviation));
    }

    public int getLookforward() {
        return 0;
    }

    public Point drawOutput(
            Graphics g,
            int outputIdx,
            Object values2,
            Color color,
            Stroke stroke,
            IIndicatorDrawingSupport indicatorDrawingSupport,
            List<Shape> shapes,
            Map<Color, List<Point>> handles
    ) {
        double[] values = (double[]) values2;
        if (values == null || values.length == 0) {
            return null;
        }

        int shift = outputParameterInfos[outputIdx].getShift();

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
                } else {
                    chartPeriod = Period.ONE_SEC;
                }
                if (formulaHasSmallerPeriod) {
                    float koef = chartPeriod.getInterval() / formulaPeriod.getInterval();
                    candleWidth /= koef;
                    spaceBetweenCandles /= koef;
                } else {
                    candleWidth *= formulaPeriod.getInterval() / chartPeriod.getInterval();
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        String fontName = g.getFont().getName();
        int fontStyle = (stroke instanceof BasicStroke && ((BasicStroke) stroke).getLineWidth() > 1 ? Font.BOLD : Font.PLAIN);
        int fontSize = (candleWidth <= 1 ? 9 : candleWidth > 20 ? 12 : 10);

        Font prevFont = g.getFont();
        g.setFont(new Font(fontName, fontStyle, fontSize));
        FontMetrics fm = g.getFontMetrics();

        Color prevColor = g.getColor();
        Color color2 = indicatorDrawingSupport.getDowntrendColor();

        int firstCandleIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen();
        int lastCandleIndex = firstCandleIndex + indicatorDrawingSupport.getNumberOfCandlesOnScreen() - 1;
        if (shift > 0) {
            firstCandleIndex = indicatorDrawingSupport.getShiftedIndex(firstCandleIndex, -shift, displayMode);
        } else if (shift < 0) {
            lastCandleIndex = indicatorDrawingSupport.getShiftedIndex(lastCandleIndex, -shift, displayMode);
        }
        if (firstCandleIndex < 0) {
            firstCandleIndex = 0;
        }
        if (lastCandleIndex > values.length - 1) {
            lastCandleIndex = values.length - 1;
        }

        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

		for (int idx = firstCandleIndex; idx <= lastCandleIndex; idx++) {
            int shiftedIdx = idx;
            double value = values[idx];
            double price = (value > 0 ? timeData[idx].getHigh() : timeData[idx].getLow());

            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, firstCandleIndex, lastCandleIndex);
                    }
                    formulaBars = formulaAllBars.get(idx);
                } else {
                    if (!formulaTimeData.isFormulaBarDisplayTime(timeData, idx, displayMode)) {
                        continue;
                    }
                    if (shift != 0) {
                        shiftedIdx = indicatorDrawingSupport.getShiftedIndex(idx, shift, displayMode);
                    }
                }
            } else {
                if (shift != 0) {
                    shiftedIdx = indicatorDrawingSupport.getShiftedIndex(idx, shift, displayMode);
                }
            }

            for (int valueIdx = 0; valueIdx < (formulaBars != null ? formulaBars.length : 1); valueIdx++) {
                if (formulaTimeData != null) {
                    Object formulaPrice;
                    if (formulaBars != null) {
                        Object formulaValue = formulaTimeData.getFormulaValue(formulaBars[valueIdx], outputIdx);
                        formulaPrice = formulaTimeData.getFormulaValue(formulaBars[valueIdx], 0);
                        if (formulaValue instanceof Double) {
                            value = (Double) formulaValue;
                        }
                    } else {
                        formulaPrice = formulaTimeData.getFormulaValue(timeData[idx], 0);
                    }
                    if (formulaPrice instanceof Double) {
                        price = (Double) formulaPrice;
                    }
                }

                if (Double.isNaN(value) || Double.isNaN(price)) {
                    continue;
                }

                float x;
                if (formulaBars != null) {
                    long time = formulaBars[valueIdx].getTime();
                    long shiftedTime = indicatorDrawingSupport.getShiftedTime(time, shift);
                    x = indicatorDrawingSupport.getXForTime(shiftedTime, false);
                    if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                        x += (spaceBetweenCandles + candleWidth) / 2;
                    }
                } else {
                    x = indicatorDrawingSupport.getMiddleOfCandle(shiftedIdx);
                }
                if (Float.isNaN(x)) {
                    continue;
                }

                int y = (int) indicatorDrawingSupport.getYForValue(price);

                String text = DECIMAL_FORMAT.format(value);
                int textWidth = fm.stringWidth(text);

                if (value > 0) {
                    g.setColor(color);
                    g.drawString(text, (int) x - textWidth / 2, y - fm.getDescent());
                } else {
                    g.setColor(color2);
                    g.drawString(text, (int) x - textWidth / 2, y + fm.getAscent());
                }
            }
		}

        g.setFont(prevFont);
        g.setColor(prevColor);

        return null;
    }
}
