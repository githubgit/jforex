/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * Created by: S.Vishnyakov
 * Date: Jul 23, 2009
 * Time: 11:48:57 AM
 */
public class HeikinAshiIndicator implements IIndicator, IDrawingIndicator {

    public static final int OPEN = 0;
    public static final int CLOSE = 1;
    public static final int HIGH = 2;
    public static final int LOW = 3;

    private IIndicatorContext context;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final Object[][] outputs = new Object[2][];

    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("HeikinAshi", "Heikin Ashi", "", true, false, true, 1, 0, 2);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("HeikinAshi border", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("HeikinAshi candle", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.CANDLES)
		};

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[0].setColor2(DefaultColors.RED);
	    outputParameterInfos[0].setDrawnByIndicator(true);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[1].setColor2(DefaultColors.RED);
	    outputParameterInfos[1].setDrawnByIndicator(true);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] prices = inputs[0];
        double[] firstHACandle = new double[4];
        
        firstHACandle[OPEN] = (prices[OPEN][startIndex - 1] + prices[CLOSE][startIndex - 1]) / 2;
        firstHACandle[CLOSE] = (prices[OPEN][startIndex - 1] + prices[CLOSE][startIndex - 1] + prices[HIGH][startIndex - 1] + prices[LOW][startIndex - 1]) / 4;
        firstHACandle[HIGH] = Math.max(Math.max(prices[HIGH][startIndex - 1], firstHACandle[OPEN]), firstHACandle[CLOSE]);
        firstHACandle[LOW] = Math.min(Math.min(prices[LOW][startIndex - 1], firstHACandle[OPEN]), firstHACandle[CLOSE]);
        
        int resIndex = 0;
        for (int i = startIndex; i <= endIndex; i++, resIndex++) {
        	double[] prevCandle = i == startIndex ? firstHACandle : (double[])outputs[0][resIndex - 1];        	        	
        	double[] newHACandle = new double[4];
        	
        	newHACandle[OPEN] = (prevCandle[OPEN] + prevCandle[CLOSE]) / 2;
			newHACandle[CLOSE] = (prices[OPEN][i] + prices[CLOSE][i] + prices[HIGH][i] + prices[LOW][i]) / 4;
			newHACandle[HIGH] = Math.max(Math.max(prices[HIGH][i], newHACandle[OPEN]), newHACandle[CLOSE]);
			newHACandle[LOW] = Math.min(Math.min(prices[LOW][i], newHACandle[OPEN]), newHACandle[CLOSE]);
			
			outputs[0][resIndex] = newHACandle;
            outputs[1][resIndex] = newHACandle;            
        }

        return new IndicatorResult(startIndex, resIndex);
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
        return null;
    }

    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    public void setInputParameter(int index, Object array) {
        inputs[0] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (Object[]) array;
    }

    public int getLookback() {
        return 1;
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
    		IIndicatorDrawingSupport drawingSupport,
    		List<Shape> shapes,
    		Map<Color, List<Point>> handles
    ) {
        if (values2 == null) {
            return null;
        }

		Graphics2D g2 = (Graphics2D) g;
		Stroke oldStroke = g2.getStroke();
        if (stroke != null) {
            g2.setStroke(stroke);
        }

        Object[] values = (Object[]) values2;
        GeneralPath indicatorPath = new GeneralPath();

        int firstCandleIndex = drawingSupport.getIndexOfFirstCandleOnScreen();
        int lastCandleIndex = firstCandleIndex + drawingSupport.getNumberOfCandlesOnScreen() - 1;
        float candleWidth = drawingSupport.getCandleWidthInPixels();
        float spaceBetweenCandles = drawingSupport.getSpaceBetweenCandlesInPixels();

        int firstCalculatedIndex = drawingSupport.getShiftedIndex(firstCandleIndex, -1);
        if (firstCalculatedIndex < 0) {
            firstCalculatedIndex = 0;
        }
        int lastCalculatedIndex = drawingSupport.getShiftedIndex(lastCandleIndex, 1);
        if (lastCalculatedIndex > values.length - 1) {
            lastCalculatedIndex = values.length - 1;
        }

        boolean formulaHasSmallerPeriod = false;
        Period formulaPeriod = drawingSupport.getFormulaPeriod();
        if (formulaPeriod != null) {
            if (drawingSupport.isTimeAggregatedPeriod()) {
                if (drawingSupport.getPeriod() != Period.TICK) {
                    formulaHasSmallerPeriod = formulaPeriod.isSmallerThan(drawingSupport.getPeriod());
                    if (formulaHasSmallerPeriod) {
                        float koef = drawingSupport.getPeriod().getInterval() / formulaPeriod.getInterval();
                        candleWidth /= koef;
                        spaceBetweenCandles /= koef;
                    }
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        IBar[] timeData = drawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = drawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

        long lastTime;
        if (timeData[lastCalculatedIndex] instanceof IPriceAggregationBar) {
            lastTime = ((IPriceAggregationBar) timeData[lastCalculatedIndex]).getEndTime();
        } else {
            lastTime = timeData[lastCalculatedIndex].getTime();
        }

        int halfOfCandle = (int) (candleWidth / 2);
        int halfOfSpace = (int) (spaceBetweenCandles / 2);

        for (int idx = firstCalculatedIndex; idx <= lastCalculatedIndex; idx++){
            double[] candle = (double[]) values[idx];

            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, firstCalculatedIndex, lastCalculatedIndex);
                    }
                    formulaBars = formulaAllBars.get(idx);
                } else {
                    if (idx > firstCalculatedIndex &&
                            !formulaTimeData.isFormulaBarStart(timeData[idx], idx > 0 ? timeData[idx - 1] : null)) {
                        continue;
                    }
                }
            }

            for (int valueIdx = 0; valueIdx < (formulaBars != null ? formulaBars.length : 1); valueIdx++) {
                if (formulaTimeData != null && formulaBars != null) {
                    Object formulaValue = formulaTimeData.getFormulaValue(formulaBars[valueIdx], outputIdx);
                    if (formulaValue instanceof double[]) {
                        candle = (double[]) formulaValue;
                    }
                }

                if (candle == null) {
                    continue;
                }

                int bodyMiddle;
                if (formulaBars != null) {
                    long time = formulaBars[valueIdx].getTime();
                    bodyMiddle = drawingSupport.getXForTime(time, false);
                    if (drawingSupport.isTimeAggregatedPeriod()) {
                        bodyMiddle += halfOfSpace + halfOfCandle;
                    }
                } else {
                    bodyMiddle = (int) drawingSupport.getMiddleOfCandle(idx);
                }

                int bodyX1 = bodyMiddle - halfOfCandle;
                int bodyX2 = bodyMiddle + halfOfCandle;

                if (formulaTimeData != null) {
                    if (formulaBars != null) {
                        if (!drawingSupport.isTimeAggregatedPeriod()) {
                            long time = formulaBars[valueIdx].getTime();
                            time = getNextBarStart(formulaPeriod, time) - 1;
                            if (time > lastTime) {
                                time = lastTime;
                            }
                            int bodyMiddle2 = drawingSupport.getXForTime(time, false);
                            bodyX1 = bodyMiddle;
                            bodyX2 = bodyMiddle2;
                            if (bodyX2 - bodyX1 > 2) {
                                bodyX1 += 1;
                                bodyX2 -= 1;
                            }
                            bodyMiddle = (bodyX1 + bodyX2) / 2;
                        }

                    } else {
                        int idx2 = drawingSupport.getShiftedIndex(idx, 1);
                        idx2--;
                        if (idx2 > timeData.length - 1) {
                            idx2 = timeData.length - 1;
                        }
                        if (idx2 != idx) {
                            int bodyMiddle2 = (int) drawingSupport.getMiddleOfCandle(idx2);
                            bodyX2 = bodyMiddle2 + halfOfCandle;
                            bodyMiddle = (bodyX1 + bodyX2) / 2;
                        }
                    }
                }

                boolean bullish = (candle[CLOSE] >= candle[OPEN]);

                float yOpen = drawingSupport.getYForValue(candle[OPEN]);
                float yClose = drawingSupport.getYForValue(candle[CLOSE]);
                float yHigh = drawingSupport.getYForValue(candle[HIGH]);
                float yLow = drawingSupport.getYForValue(candle[LOW]);

                int bodyY1 = (int) (bullish ? yClose : yOpen);
                int bodyY2 = (int) (bullish ? yOpen : yClose);

                int bodyWidth = bodyX2 - bodyX1 + 1;
                int bodyHeight = bodyY2 - bodyY1 + 1;

                if (outputIdx == 0) {
                    if (bodyWidth >= 1) {
                        bodyWidth -= 1;
                    }
                    if (bodyHeight >= 1) {
                        bodyHeight -= 1;
                    }
                } else {
                    if (bodyWidth >= 2) {
                        bodyX1 += 1;
                        bodyWidth -= 2;
                    }
                    if (bodyHeight >= 2) {
                        bodyY1 += 1;
                        bodyHeight -= 2;
                    }
                }

                g2.setColor(bullish ? color : drawingSupport.getDowntrendColor());

                Shape body;
                if (bodyWidth <= 0) {
                    body = new GeneralPath();
                    ((GeneralPath) body).moveTo(bodyMiddle, bodyY1);
                    ((GeneralPath) body).lineTo(bodyMiddle, bodyY2);

                    g2.draw(body);

                } else if (bodyHeight <= 0) {
                    body = new GeneralPath();
                    ((GeneralPath) body).moveTo(bodyX1, bodyY1);
                    ((GeneralPath) body).lineTo(bodyX2, bodyY1);

                    g2.draw(body);

                } else {
                    body = new Rectangle(bodyX1, bodyY1, bodyWidth, bodyHeight);

                    if (outputIdx == 0) {
                        g2.draw(body);
                    } else {
                        g2.fill(body);
                    }
                }

                if (outputIdx == 0) {
                    indicatorPath.append(body, false);
                } else {
                    shapes.add(body);
                }

                if (outputIdx == 0) {
                    g2.drawLine(bodyMiddle, bodyY1, bodyMiddle, (int) yHigh);
                    g2.drawLine(bodyMiddle, bodyY2, bodyMiddle, (int) yLow);

                    indicatorPath.moveTo(bodyMiddle, bodyY1);
                    indicatorPath.lineTo(bodyMiddle, yHigh);
                    indicatorPath.moveTo(bodyMiddle, bodyY2);
                    indicatorPath.lineTo(bodyMiddle, yLow);
                }
            }
        }

        if (indicatorPath.getCurrentPoint() != null) {
            shapes.add(indicatorPath);
        }

		g2.setStroke(oldStroke);

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
