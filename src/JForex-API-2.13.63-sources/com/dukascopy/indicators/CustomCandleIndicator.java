package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IPriceAggregationBar;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IFormulaTimeData;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class CustomCandleIndicator implements IIndicator, IDrawingIndicator {
    private IIndicatorContext context;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final Object[] outputs = new Object[5];

    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("CUSTOMCANDLE", "Custom candles", "", true, false, false, 1, 0, 5);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bars", InputParameterInfo.Type.BAR)
        };

        optInputParameterInfos = new OptInputParameterInfo[0];

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Custom candles", OutputParameterInfo.Type.CANDLE, OutputParameterInfo.DrawingStyle.CANDLES),
            new OutputParameterInfo("Open price", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("High price", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Low price", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Close price", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setColor2(DefaultColors.RED);
        outputParameterInfos[0].setOpacityAlpha(0.5f);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[1].setDrawnByIndicator(true);
        outputParameterInfos[1].setShowOutput(false);
        outputParameterInfos[2].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[2].setDrawnByIndicator(true);
        outputParameterInfos[2].setShowOutput(false);
        outputParameterInfos[3].setColor(DefaultColors.RED);
        outputParameterInfos[3].setDrawnByIndicator(true);
        outputParameterInfos[3].setShowOutput(false);
        outputParameterInfos[4].setColor(DefaultColors.DARK_ORANGE);
        outputParameterInfos[4].setDrawnByIndicator(true);
        outputParameterInfos[4].setShowOutput(false);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            ((IBar[]) outputs[0])[j] = inputs[0][i];
            ((double[]) outputs[1])[j] = inputs[0][i].getOpen();
            ((double[]) outputs[2])[j] = inputs[0][i].getHigh();
            ((double[]) outputs[3])[j] = inputs[0][i].getLow();
            ((double[]) outputs[4])[j] = inputs[0][i].getClose();
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
        inputs[index] = (IBar[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = array;
    }

    public int getLookback() {
        return 0;
    }

    public int getLookforward() {
        return 0;
    }

    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values, Color color, Stroke stroke,
            IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> shapes, Map<Color, List<Point>> handles) {

        GeneralPath path = new GeneralPath();

        boolean formulaHasSmallerPeriod = false;
        float candleWidth = indicatorDrawingSupport.getCandleWidthInPixels();
        float spaceBetweenCandles = indicatorDrawingSupport.getSpaceBetweenCandlesInPixels();

        Period chartPeriod = indicatorDrawingSupport.getPeriod();
        Period formulaPeriod = indicatorDrawingSupport.getFormulaPeriod();
        if (formulaPeriod != null && chartPeriod != Period.TICK) {
            if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                formulaHasSmallerPeriod = formulaPeriod.isSmallerThan(chartPeriod);
                if (formulaHasSmallerPeriod) {
                    float koef = chartPeriod.getInterval() / formulaPeriod.getInterval();
                    candleWidth /= koef;
                    spaceBetweenCandles /= koef;
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        int firstIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() - 1;
        int lastIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() + indicatorDrawingSupport.getNumberOfCandlesOnScreen();

        int shift = outputParameterInfos[outputIdx].getShift();
        if (shift > 0) {
            firstIndex = indicatorDrawingSupport.getShiftedIndex(firstIndex, -shift);
        } else if (shift < 0) {
            lastIndex = indicatorDrawingSupport.getShiftedIndex(lastIndex, -shift);
        }

        int length = ((double[]) values).length;
        if (firstIndex < 0) {
            firstIndex = 0;
        }
        if (lastIndex > length - 1) {
            lastIndex = length - 1;
        }

        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

        long lastTime;
        if (timeData[lastIndex] instanceof IPriceAggregationBar) {
            lastTime = ((IPriceAggregationBar) timeData[lastIndex]).getEndTime();
        } else {
            lastTime = timeData[lastIndex].getTime();
        }

        for (int idx = firstIndex; idx <= lastIndex; idx++) {
            double value = ((double[]) values)[idx];
            int shiftedIdx = idx;

            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, firstIndex, lastIndex);
                    }
                    formulaBars = formulaAllBars.get(idx);
                } else {
                    if (idx > firstIndex &&
                            !formulaTimeData.isFormulaBarStart(timeData[idx], idx > 0 ? timeData[idx - 1] : null)) {
                        continue;
                    }
                    if (shift != 0) {
                        shiftedIdx = indicatorDrawingSupport.getShiftedIndex(idx, shift);
                    }
                }
            } else {
                if (shift != 0) {
                    shiftedIdx = indicatorDrawingSupport.getShiftedIndex(idx, shift);
                }
            }

            for (int valueIdx = 0; valueIdx < (formulaBars != null ? formulaBars.length : 1); valueIdx++) {
                if (formulaBars != null && formulaTimeData != null) {
                    Double formulaValue = (Double) formulaTimeData.getFormulaValue(formulaBars[valueIdx], outputIdx);
                    if (formulaValue != null) {
                        value = formulaValue.doubleValue();
                    }
                }

                if (Double.isNaN(value)) {
                    continue;
                }

                int x;
                if (formulaBars != null) {
                    long time = formulaBars[valueIdx].getTime();
                    long shiftedTime = indicatorDrawingSupport.getShiftedTime(time, shift);
                    x = indicatorDrawingSupport.getXForTime(shiftedTime, false);
                    if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                        x += (spaceBetweenCandles + candleWidth) / 2;
                    }
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(shiftedIdx);
                }

                int y = (int) indicatorDrawingSupport.getYForValue(value);

                int x1 = x - (int) (candleWidth / 2);
                int x2 = x + (int) (candleWidth / 2);

                if (formulaTimeData != null) {
                    if (formulaBars != null) {
                        if (!indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                            long time = formulaBars[valueIdx].getTime();
                            long shiftedTime = indicatorDrawingSupport.getShiftedTime(time, shift);
                            shiftedTime = getNextBarStart(formulaPeriod, shiftedTime) - 1;
                            if (shift == 0 && shiftedTime > lastTime) {
                                shiftedTime = lastTime;
                            }
                            x2 = indicatorDrawingSupport.getXForTime(shiftedTime, false);
                            x1 = x;
                        }

                    } else {
                        int idx2 = idx;
                        ITimedData nextFormulaBar = formulaTimeData.getNextFormulaBar(timeData[idx]);
                        while (idx2 + 1 <= lastIndex && (nextFormulaBar == null ||
                                timeData[idx2 + 1].getTime() < nextFormulaBar.getTime())) {
                            double value2 = ((double[]) values)[idx2 + 1];
                            if (!Double.isNaN(value2)) {
                                idx2++;
                            } else {
                                break;
                            }
                        }
                        if (idx2 != idx) {
                            int shiftedIdx2 = shiftedIdx + (idx2 - idx);
                            x2 = (int) indicatorDrawingSupport.getMiddleOfCandle(shiftedIdx2);
                            x2 += candleWidth / 2;
                        }
                    }
                }

                path.moveTo(x1, y);
                path.lineTo(x2, y);
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();
        g2.setStroke(stroke);

        g2.setColor(color);
        g2.draw(path);

        g2.setStroke(oldStroke);

        shapes.add(path);

        return null;
    }

    private long getNextBarStart(Period period, long barTime) {
        long nextBarTime = barTime;
        try {
            nextBarTime = context.getHistory().getNextBarStart(period, barTime);
        } catch (JFException ex) {
            context.getConsole().getErr().println(ex.toString());
        }
        return nextBarTime;
    }
}
