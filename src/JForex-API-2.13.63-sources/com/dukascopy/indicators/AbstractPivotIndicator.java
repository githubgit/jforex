package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.IBar;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IFormulaTimeData;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.OutputParameterInfo;

abstract class AbstractPivotIndicator implements IIndicator, IDrawingIndicator {
	
    protected IIndicatorContext context;

    protected boolean showHistoricalLevels = false;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00000");
    private final GeneralPath generalPath = new GeneralPath();
    private final List<Point> tmpHandlesPoints = new ArrayList<>();
    
    private int maxDistanceBetweenTwoSeparators;

    @Override
    public void onStart(IIndicatorContext context) {
    	this.context = context;
    }
    
    protected OutputParameterInfo createOutputParameterInfo(String name,
            OutputParameterInfo.Type type, OutputParameterInfo.DrawingStyle drawingStyle, boolean showOutput) {

    	OutputParameterInfo info = new OutputParameterInfo(name, type, drawingStyle, false);
        info.setDrawnByIndicator(true);
        info.setShowOutput(showOutput);

        return info;
    }

	@Override
	public Point drawOutput(
			Graphics g,
			int outputIdx,
			Object values,
			Color color,
			Stroke stroke,
			IIndicatorDrawingSupport indicatorDrawingSupport,
			List<Shape> shapes,
			Map<Color, List<Point>> handles
	) {
		if (values == null || stroke == null) {
            return null;
		}

        int length = Array.getLength(values);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(stroke);
        generalPath.reset();
        tmpHandlesPoints.clear();

        int firstCandleIndex = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen();
        int lastCandleIndex = firstCandleIndex + indicatorDrawingSupport.getNumberOfCandlesOnScreen() - 1;

        int si = firstCandleIndex - 1;
        int ei = indicatorDrawingSupport.getShiftedIndex(lastCandleIndex, 1);
        if (si < 0) {
            si = 0;
        }
        if (ei > length - 1) {
            ei = length - 1;
        }

        float candleWidth = indicatorDrawingSupport.getCandleWidthInPixels();

        boolean formulaHasSmallerPeriod = false;
        Period formulaPeriod = indicatorDrawingSupport.getFormulaPeriod();
        if (formulaPeriod != null) {
            if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                Period chartPeriod = indicatorDrawingSupport.getPeriod();
                if (chartPeriod != Period.TICK) {
                    formulaHasSmallerPeriod = formulaPeriod.isSmallerThan(chartPeriod);
                    if (formulaHasSmallerPeriod) {
                        float koef = chartPeriod.getInterval() / formulaPeriod.getInterval();
                        candleWidth /= koef;
                    }
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        if (outputIdx == getFirstEnabledOutputIndex()) {
            maxDistanceBetweenTwoSeparators = calculateMaxDistanceBetweenTwoSeparators(
                    indicatorDrawingSupport,
                    formulaHasSmallerPeriod,
                    si,
                    ei
            );
        }

        int fontSize = calculateFontSize(maxDistanceBetweenTwoSeparators, (int) candleWidth);
        boolean drawValues = canDrawValues(fontSize);

        Point lastPoint = null;

        if (outputIdx == getIndicatorInfo().getNumberOfOutputs() - 1) {
            if (drawValues) {
                drawSeparators(
            			(Object[]) values,
                        indicatorDrawingSupport,
                        generalPath,
                        maxDistanceBetweenTwoSeparators,
                        formulaHasSmallerPeriod,
                        si,
                        ei
                );
            }
        }
        else {
            lastPoint = drawPivotLevels(
                    g2,
                    outputIdx,
                    (double[]) values,
                    indicatorDrawingSupport,
                    generalPath,
                    fontSize,
                    drawValues,
                    maxDistanceBetweenTwoSeparators,
                    candleWidth,
                    formulaHasSmallerPeriod,
                    si,
                    ei
            );
        }

        g2.draw(generalPath);

        shapes.add((Shape) generalPath.clone()); // cloning path, so when checking for intersection each indicator has its own path
        handles.put(color, new ArrayList<>(tmpHandlesPoints));

		return lastPoint;
	}

	private void drawSeparators(
			Object[] output,
			IIndicatorDrawingSupport indicatorDrawingSupport,
			GeneralPath generalPath,
			int maxDistanceBetweenTwoSeparators,
            boolean formulaHasSmallerPeriod,
			int si,
			int ei
	) {
		int maxWidth = indicatorDrawingSupport.getChartWidth() + maxDistanceBetweenTwoSeparators;
		int maxHeight = indicatorDrawingSupport.getChartHeight();
		
        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

		Long lastSeparatorTime = null;

        main_loop:
		for (int i = ei; i >= si; i--) {
            if (output[i] == null) {
                continue;
            }

            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, si, ei);
                    }
                    formulaBars = formulaAllBars.get(i);
                } else {
                    if (!formulaTimeData.isFormulaBarStart(timeData[i], i > 0 ? timeData[i - 1] : null)) {
                        continue;
                    }
                }
            }

            for (int valueIdx = (formulaBars != null ? formulaBars.length - 1 : 0); valueIdx >= 0; valueIdx--) {
                long barTime;
                if (formulaBars != null) {
                    barTime = formulaBars[valueIdx].getTime();
                } else {
                    barTime = getBarStart(timeData[i].getTime(), indicatorDrawingSupport);
                }

                if (!showHistoricalLevels) {
                    long currentBarTime = getCurrentBarStart(indicatorDrawingSupport);
                    if (barTime != currentBarTime) {
                        break main_loop;
                    }
                }

                if (lastSeparatorTime == null) {
                    lastSeparatorTime = barTime;
                }

                int x;
                if (formulaBars != null) {
                    x = indicatorDrawingSupport.getXForTime(barTime, false);
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                }

                if (x < 0) {
                    /*
                     * Drawing is from right to left
                     * Stop drawing if we are out of screen
                     */
                    break main_loop;
                }

                drawSeparator(
                        generalPath,
                        x,
                        maxWidth,
                        maxHeight
                );

                if (!showHistoricalLevels) {
                    /*
                     * Don't draw separators further if the user doesn't want them
                     */
                    break main_loop;
                }
            }
		}
		
		if (lastSeparatorTime != null && indicatorDrawingSupport.isTimeAggregatedPeriod()) {
            long nextBarTime = getNextBarStart(lastSeparatorTime, indicatorDrawingSupport);

            int x;
            if (formulaTimeData != null && formulaHasSmallerPeriod) {
                x = indicatorDrawingSupport.getXForTime(nextBarTime, false);
            } else {
                x = indicatorDrawingSupport.getXForTime(nextBarTime);
            }

            drawSeparator(
                    generalPath,
                    x,
                    maxWidth,
                    maxHeight
            );
	    }
	}

	private int calculateMaxDistanceBetweenTwoSeparators(
			IIndicatorDrawingSupport indicatorDrawingSupport,
            boolean formulaHasSmallerPeriod,
			int si,
            int ei
	) {
		int maxDistance = Integer.MIN_VALUE;
		
        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

		Integer previousX = null;

        main_loop:
		for (int i = ei; i >= si; i--) {
            int j = i;

            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, si, ei);
                    }
                    formulaBars = formulaAllBars.get(i);
                } else {
                    if (!formulaTimeData.isFormulaBarStart(timeData[i], i > 0 ? timeData[i - 1] : null)) {
                        if (i > si) {
                            continue;
                        } else {
                            j = indicatorDrawingSupport.getShiftedIndex(j, -1);
                            if (j != i) {
                                j = indicatorDrawingSupport.getShiftedIndex(j, 1);
                            }
                        }
                    }
                }
            }

            for (int valueIdx = (formulaBars != null ? formulaBars.length - 1 : 0); valueIdx >= 0; valueIdx--) {
                long barTime;
                if (formulaBars != null) {
                    barTime = formulaBars[valueIdx].getTime();
                } else {
                    barTime = getBarStart(timeData[i].getTime(), indicatorDrawingSupport);
                }

                if (!showHistoricalLevels) {
                    long currentBarTime = getCurrentBarStart(indicatorDrawingSupport);
                    if (barTime != currentBarTime) {
                        break main_loop;
                    }
                }

                int x;
                if (formulaBars != null) {
                    x = indicatorDrawingSupport.getXForTime(barTime, false);
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(j);
                }

                if (previousX == null) {
                    if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                        long nextBarTime = getNextBarStart(barTime, indicatorDrawingSupport);
                        if (formulaBars != null) {
                            previousX = indicatorDrawingSupport.getXForTime(nextBarTime, false);
                        } else {
                            previousX = indicatorDrawingSupport.getXForTime(nextBarTime);
                        }
                    } else {
                        previousX = indicatorDrawingSupport.getChartWidth();
                    }
                }

                int distance = Math.abs(x - previousX);
                if (maxDistance < distance) {
                    maxDistance = distance;
                }

                previousX = x;

                if (!showHistoricalLevels) {
                    break main_loop;
                }
            }
		}

		return maxDistance;
	}

	private Point drawPivotLevels(
			Graphics2D g2,
			int outputIdx,
			double[] output,
			IIndicatorDrawingSupport indicatorDrawingSupport,
			GeneralPath generalPath,
			int fontSize,
			boolean drawValues,
			int maxDistanceBetweenTwoSeparators,
            float candleWidth,
            boolean formulaHasSmallerPeriod,
			int si,
			int ei
	) {
		g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), fontSize));
		
		int maxX = indicatorDrawingSupport.getChartWidth() + maxDistanceBetweenTwoSeparators; //JFOREX-2432
		int minX = -maxDistanceBetweenTwoSeparators;
		
        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

		Integer previousX = null;
        Point lastPoint = null;

        main_loop:
		for (int i = ei; i >= si; i--) {
            int j = i;

            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, si, ei);
                    }
                    formulaBars = formulaAllBars.get(i);
                } else {
                    if (!formulaTimeData.isFormulaBarStart(timeData[i], i > 0 ? timeData[i - 1] : null)) {
                        if (i > si) {
                            continue;
                        } else {
                            j = indicatorDrawingSupport.getShiftedIndex(j, -1);
                            if (j != i) {
                                j = indicatorDrawingSupport.getShiftedIndex(j, 1);
                            }
                        }
                    }
                }
            }

            double value = output[i];

            for (int valueIdx = (formulaBars != null ? formulaBars.length - 1 : 0); valueIdx >= 0; valueIdx--) {
                long barTime;
                if (formulaBars != null) {
                    barTime = formulaBars[valueIdx].getTime();
                } else {
                    barTime = getBarStart(timeData[i].getTime(), indicatorDrawingSupport);
                }

                if (!showHistoricalLevels) {
                    long currentBarTime = getCurrentBarStart(indicatorDrawingSupport);
                    if (barTime != currentBarTime) {
                        break main_loop;
                    }
                }

                if (formulaBars != null && formulaTimeData != null) {
                    Object formulaValue = formulaTimeData.getFormulaValue(formulaBars[valueIdx], outputIdx);
                    if (formulaValue instanceof Double) {
                        value = (Double) formulaValue;
                    }
                }

                if (Double.isNaN(value)) {
                    continue;
                }

                int x;
                if (formulaBars != null) {
                    x = indicatorDrawingSupport.getXForTime(barTime, false);
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(j);
                }

                int y = (int) indicatorDrawingSupport.getYForValue(value);

                if (previousX == null) {
                    if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                        long nextBarTime = getNextBarStart(barTime, indicatorDrawingSupport);
                        if (formulaBars != null) {
                            previousX = indicatorDrawingSupport.getXForTime(nextBarTime, false);
                        } else {
                            previousX = indicatorDrawingSupport.getXForTime(nextBarTime);
                        }
                    } else {
                        previousX = indicatorDrawingSupport.getChartWidth();
                    }
                }

                if (
                        (y >= 0 && y <= indicatorDrawingSupport.getChartHeight()) &&
                        ! (previousX < 0 && x < 0) &&
                        ! (previousX > indicatorDrawingSupport.getChartWidth() && x > indicatorDrawingSupport.getChartWidth()) &&
                        (
                                (minX <= previousX && previousX <= maxX) ||
                                (minX <= x && x <= maxX)
                        )
                ) {
                    previousX = Math.max(0, previousX);
                    previousX = Math.min(indicatorDrawingSupport.getChartWidth(), previousX);
                    x = Math.min(indicatorDrawingSupport.getChartWidth(), x);

                    generalPath.moveTo(previousX, y);
                    generalPath.lineTo(Math.max(0, x), y);

                    if (drawValues) {
                        String valueStr = decimalFormat.format(value);
                        String name = getOutputParameterInfo(outputIdx).getName();
                        String lineCode = name.substring(name.lastIndexOf("(") + 1, name.length() - 1);
                        String result = lineCode + ": " + valueStr;

                        int lineCodeX = Math.max(0, x) + 1;
                        int distance = Math.abs(Math.max(0, x) - previousX);
                        int newFontSize = calculateFontSize(distance, (int) candleWidth);
                        boolean canDrawValues = canDrawValues(newFontSize);

                        if (canDrawValues) {
                            if (newFontSize != fontSize) {
                                fontSize = newFontSize;
                                g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), fontSize));
                            }
                            g2.drawString(result, lineCodeX, y - 2);
                        }
                    }

                    if (lastPoint == null) {
                        lastPoint = new Point(previousX, y);
                    }
                }

                previousX = x;

                if (!showHistoricalLevels) {
                    break main_loop;
                }
            }
		}

        return lastPoint;
	}
	
	private boolean canDrawValues(int fontSize) {
		final int MIN_FONT_SIZE = 4;
		
		return fontSize > MIN_FONT_SIZE;
	}

	private int calculateFontSize(
			int spaceBetweenTwoSeparators,
			int candleWidthInPixels
	) {
		final int MAX_FONT_SIZE = 12;
		final int DIVISION_COEF = 7;
		
		spaceBetweenTwoSeparators /= DIVISION_COEF;
		spaceBetweenTwoSeparators = spaceBetweenTwoSeparators < 0 ? candleWidthInPixels : spaceBetweenTwoSeparators;
		
		return spaceBetweenTwoSeparators > MAX_FONT_SIZE ? MAX_FONT_SIZE : spaceBetweenTwoSeparators;
	}

	private void drawSeparator(
			GeneralPath generalPath,
			int x,
			int maxWidth,
			int maxHeight
	) {
		if (0 <= x && x <= maxWidth) {
			generalPath.moveTo(x, 0);
			generalPath.lineTo(x, maxHeight);
			
			tmpHandlesPoints.add(new Point(x, 5));
			tmpHandlesPoints.add(new Point(x, maxHeight/2));
			tmpHandlesPoints.add(new Point(x, maxHeight - 5));
		}
	}
	
	private int getFirstEnabledOutputIndex(){
		for (int i = 0, n = getIndicatorInfo().getNumberOfOutputs(); i < n; i++) {
			if (getOutputParameterInfo(i).isShowOutput()) {
				return i; 
			}
		}
		return -1;
	}

    private long getBarStart(long barTime, IIndicatorDrawingSupport indicatorDrawingSupport) {
        try {
            Period period = indicatorDrawingSupport.getFormulaPeriod();
            if (period == null) {
                period = indicatorDrawingSupport.getPeriod();
            }
            barTime = context.getHistory().getBarStart(period, barTime);
        } catch (JFException ex) {
            context.getConsole().getErr().println(ex.toString());
        }
        return barTime;
    }

    private long getNextBarStart(long barTime, IIndicatorDrawingSupport indicatorDrawingSupport) {
        long nextBarTime = barTime;
        try {
            Period chartPeriod = indicatorDrawingSupport.getPeriod();
            Period formulaPeriod = indicatorDrawingSupport.getFormulaPeriod();
            if (formulaPeriod == null) {
                nextBarTime = context.getHistory().getNextBarStart(chartPeriod, barTime);
            } else {
                nextBarTime = context.getHistory().getNextBarStart(formulaPeriod, barTime);
                if (!formulaPeriod.isSmallerThan(chartPeriod) &&
                        context.getHistory().getBarStart(chartPeriod, nextBarTime) == barTime)  {
                    nextBarTime = context.getHistory().getNextBarStart(formulaPeriod, nextBarTime);
                }
            }
        } catch (JFException ex) {
            context.getConsole().getErr().println(ex.toString());
        }
        return nextBarTime;
    }

    private long getCurrentBarStart(IIndicatorDrawingSupport indicatorDrawingSupport) {
        long currentBarTime = Long.MIN_VALUE;
        try {
            Instrument instrument = indicatorDrawingSupport.getInstrument();
            Period period = indicatorDrawingSupport.getFormulaPeriod();
            if (period == null) {
                period = indicatorDrawingSupport.getPeriod();
            }
            currentBarTime = context.getHistory().getStartTimeOfCurrentBar(instrument, period);
        } catch (JFException ex) {
            context.getConsole().getErr().println(ex.toString());
        }
        return currentBarTime;
    }
}
