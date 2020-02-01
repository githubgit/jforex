package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IIndicators.MaType;
import com.dukascopy.api.ITimedData;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.BooleanOptInputDescription;
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

public class ADRIndicator implements IIndicator, IDrawingIndicator {
	
    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    private static final int MIN_DISTANCE_BETWEEN_SEPARATORS = 50;

    private IIndicatorContext context;
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    
    private final double[][][] inputs = new double[1][][];
    private final Object[] outputs = new Object[3];

    private boolean showHistoricalLevels = false;

    private IIndicator ma;

    private final GeneralPath path = new GeneralPath();
    private final List<Point> handlePoints = new ArrayList<>();

    @Override
    public void onStart(IIndicatorContext context) {
    	this.context = context;
    	
        indicatorInfo = new IndicatorInfo("ADR", "Average Daily Range", "Volatility Indicators", true, false, false, 1, 2, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Prices", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 1, 2000, 1)),
            new OptInputParameterInfo("Show historical levels", OptInputParameterInfo.Type.OTHER, new BooleanOptInputDescription(showHistoricalLevels))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("ADR High", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE),
            new OutputParameterInfo("ADR Low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE),
        	new OutputParameterInfo("Separators", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LEVEL_DOT_LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setDrawnByIndicator(true);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[1].setDrawnByIndicator(true);
        outputParameterInfos[2].setColor(DefaultColors.STEEL_BLUE);
        outputParameterInfos[2].setDrawnByIndicator(true);

        ma = context.getIndicatorsProvider().getIndicator("SMA");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
    	if (startIndex - getLookback() < 0) {
    		startIndex = getLookback();
    	}
    	if (startIndex > endIndex) {
    		return new IndicatorResult(0, 0);
    	}

        double[] maInput = new double[endIndex - startIndex + 1 + ma.getLookback()];
        double[] maOutput = new double[endIndex - startIndex + 1];

        for (int i = startIndex - 1 - ma.getLookback(), j = 0; i <= endIndex - 1; i++, j++) {
            maInput[j] = inputs[0][HIGH][i] - inputs[0][LOW][i];
        }

        ma.setInputParameter(0, maInput);
        ma.setOutputParameter(0, maOutput);
        ma.calculate(ma.getLookback(), maInput.length - 1);

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            ((double[]) outputs[0])[j] = inputs[0][LOW][i] + maOutput[j];
            ((double[]) outputs[1])[j] = inputs[0][HIGH][i] - maOutput[j];
            ((Object[]) outputs[2])[j] = Double.NaN;
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
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
            ma.setOptInputParameter(0, value);
            break;
        case 1:
            showHistoricalLevels = (Boolean) value;
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = array;
    }

    @Override
    public int getLookback() {
        return ma.getLookback() + 1;
    }

    @Override
    public int getLookforward() {
        return 0;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(stroke);

        path.reset();
        handlePoints.clear();

        int length = Array.getLength(values);
        int si = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen() - 1;
        int ei = si + indicatorDrawingSupport.getNumberOfCandlesOnScreen();
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
                } else {
                    chartPeriod = Period.ONE_SEC;
                }
                if (formulaHasSmallerPeriod) {
                    candleWidth /= chartPeriod.getInterval() / formulaPeriod.getInterval();
                } else {
                    candleWidth *= formulaPeriod.getInterval() / chartPeriod.getInterval();
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        Point lastPoint = null;

        if (outputIdx != 2) {
            lastPoint = drawLevels(outputIdx, (double[]) values, indicatorDrawingSupport, formulaHasSmallerPeriod, si, ei);

        } else if (!showHistoricalLevels || candleWidth >= MIN_DISTANCE_BETWEEN_SEPARATORS) {
            drawSeparators(indicatorDrawingSupport, formulaHasSmallerPeriod, si, ei);
        }

        g2d.draw(path);

        shapes.add((Shape) path.clone());
        handles.put(color, new ArrayList<>(handlePoints));

		return lastPoint;
	}

	private Point drawLevels(
			int outputIdx,
			double[] values,
			IIndicatorDrawingSupport indicatorDrawingSupport,
            boolean formulaHasSmallerPeriod,
			int si,
			int ei
	) {
        Point lastPoint = null;

        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

        int width = indicatorDrawingSupport.getChartWidth();
        int height = indicatorDrawingSupport.getChartHeight();
		int previousX = width + 1;

        main_loop:
		for (int i = ei; i >= si; i--) {
            ITimedData[] formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    if (formulaAllBars == null) {
                        formulaAllBars = formulaTimeData.getFormulaBars(timeData, si, ei);
                    }
                    formulaBars = formulaAllBars.get(i);
                } else {
                    if (i > si && !formulaTimeData.isFormulaBarStart(timeData[i], timeData[i - 1])) {
                        continue;
                    }
                }
            }

            double value = values[i];

            for (int valueIdx = (formulaBars != null ? formulaBars.length - 1 : 0); valueIdx >= 0; valueIdx--) {
                long formulaBarTime;
                if (formulaBars != null) {
                    formulaBarTime = formulaBars[valueIdx].getTime();
                } else {
                    formulaBarTime = getFormulaBarTime(timeData[i].getTime(), indicatorDrawingSupport);
                }

                if (!showHistoricalLevels) {
                    long currentBarTime = getCurrentBarTime(indicatorDrawingSupport);
                    if (formulaBarTime != currentBarTime) {
                        break main_loop;
                    }
                }

                if (formulaTimeData != null && formulaBars != null) {
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
                    x = indicatorDrawingSupport.getXForTime(formulaBarTime, false);
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                }

                int y = (int) indicatorDrawingSupport.getYForValue(value);

                if (!(previousX < 0 && x < 0) && !(previousX > width && x > width) && (y >= 0 && y <= height)) {
                    previousX = Math.min(previousX, width);

                    path.moveTo(previousX, y);
                    path.lineTo(Math.max(x, 0), y);

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

	private void drawSeparators(
			IIndicatorDrawingSupport indicatorDrawingSupport,
            boolean formulaHasSmallerPeriod,
			int si,
			int ei
	) {
        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();
        List<ITimedData[]> formulaAllBars = null;

		int height = indicatorDrawingSupport.getChartHeight();

        main_loop:
		for (int i = ei; i >= si; i--) {
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
                long formulaBarTime;
                if (formulaBars != null) {
                    formulaBarTime = formulaBars[valueIdx].getTime();
                } else {
                    formulaBarTime = getFormulaBarTime(timeData[i].getTime(), indicatorDrawingSupport);
                }

                if (!showHistoricalLevels) {
                    long currentBarTime = getCurrentBarTime(indicatorDrawingSupport);
                    if (formulaBarTime != currentBarTime) {
                        break main_loop;
                    }
                }

                int x;
                if (formulaBars != null) {
                    x = indicatorDrawingSupport.getXForTime(formulaBarTime, false);
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                }

                if (x < 0) {
                    break main_loop;
                }

                path.moveTo(x, 0);
                path.lineTo(x, height);

                handlePoints.add(new Point(x, 5));
                handlePoints.add(new Point(x, height / 2));
                handlePoints.add(new Point(x, height - 5));

                if (!showHistoricalLevels) {
                    break main_loop;
                }
            }
		}
	}

    private long getFormulaBarTime(long chartBarTime, IIndicatorDrawingSupport indicatorDrawingSupport) {
        long formulaBarTime = chartBarTime;
        try {
            Period formulaPeriod = indicatorDrawingSupport.getFormulaPeriod();
            if (formulaPeriod != null) {
                formulaBarTime = context.getHistory().getBarStart(formulaPeriod, chartBarTime);
            }
        } catch (JFException ex) {
            context.getConsole().getErr().println(ex.toString());
        }
        return formulaBarTime;
    }

    private long getCurrentBarTime(IIndicatorDrawingSupport indicatorDrawingSupport) {
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
