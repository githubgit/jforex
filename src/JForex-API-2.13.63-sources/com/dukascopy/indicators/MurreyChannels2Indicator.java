package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ITimedData;
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

/**
 * Created by: S.Vishnyakov
 * Date: Feb 23, 2010
 * Time: 10:44:05 AM
 */
public class MurreyChannels2Indicator implements IIndicator, IDrawingIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    
    private final IBar[][] inputs = new IBar[1][];
    private final double[][] outputs = new double[13][];

    private int p = 90;
    private int stepBack = 0;
    private boolean showHistoricalLevels = false;

    private final GeneralPath generalPath = new GeneralPath();
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00000");
    
    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MURRCH2", "Murrey Channels", "Momentum Indicators", true, false, false, 1, 3, 13);
        
        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Input data", InputParameterInfo.Type.BAR)
        };
        
        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("N Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(p, 1, 400, 1)),
            new OptInputParameterInfo("Step Back", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(stepBack, 0, 100, 1)),
            new OptInputParameterInfo("Show historical levels", OptInputParameterInfo.Type.OTHER, new BooleanOptInputDescription(showHistoricalLevels))
        };
        
        outputParameterInfos = new OutputParameterInfo[] {
        	createOutputParameterInfo("[-2/8P]", DefaultColors.GRAY),
        	createOutputParameterInfo("[-1/8P]", DefaultColors.GRAY),
        	createOutputParameterInfo("[0/8P]", DefaultColors.CORNFLOWER),
        	createOutputParameterInfo("[1/8P]", DefaultColors.YELLOW),
        	createOutputParameterInfo("[2/8P]", DefaultColors.RED),
        	createOutputParameterInfo("[3/8P]", DefaultColors.OLIVE_DRAB),
        	createOutputParameterInfo("[4/8P]", DefaultColors.ROYAL_BLUE),
        	createOutputParameterInfo("[5/8P]", DefaultColors.OLIVE_DRAB),
        	createOutputParameterInfo("[6/8P]", DefaultColors.RED),
        	createOutputParameterInfo("[7/8P]", DefaultColors.YELLOW),
        	createOutputParameterInfo("[8/8P]", DefaultColors.CORNFLOWER),
        	createOutputParameterInfo("[+1/8P]", DefaultColors.GRAY),
        	createOutputParameterInfo("[+2/8P]", DefaultColors.GRAY)
        };
    }

    private OutputParameterInfo createOutputParameterInfo(String name, Color color) {
    	OutputParameterInfo param = new OutputParameterInfo(name, OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LEVEL_LINE, false);
        param.setColor(color);
        param.setDrawnByIndicator(true);
        return param;
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }
        
        int step = stepBack;        
        if (stepBack >= p) {
            step = Math.max(p - 2, 0);
        }
        
        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            double[] v = calcValues(i - step - p + 1, i - step);
            
            outputs[0][j] = v[0] - v[1] * 2;
            for (int k = 1; k < indicatorInfo.getNumberOfOutputs(); k++) {
            	outputs[k][j] = outputs[k - 1][j] + v[1];
            }
        }

        fixOutputs();

        return new IndicatorResult(startIndex, j);
    }

    private double[] calcValues(int si, int ei) {
        double[] lowHigh = findLowHigh(si, ei);
        double v1 = lowHigh[0];
        double v2 = lowHigh[1];

        double fractal = identifyFractal(v2);

        double range = v2 - v1;
        double sum = Math.floor(Math.log(fractal / range) / Math.log(2));
        double octave = fractal * Math.pow(0.5, sum);
        double mn = Math.floor(v1 / octave) * octave;

        double mx;
        if (mn + octave > v2) {
            mx = mn + octave;
        } else {
            mx = mn + 2 * octave;
        }

        double x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, y1 = 0, y2 = 0, y3 = 0, y4 = 0, y5 = 0, y6 = 0, finalH = 0, finalL = 0;

        //x2
        if ( (v1 >= (3 * (mx - mn) / 16 + mn)) && (v2 <= (9 * (mx - mn) / 16 + mn))){
        	x2 = mn + (mx - mn) / 2;
        }

        //x1
        if ((v1 >= (mn - (mx - mn) / 8)) && (v2 <= (5 * (mx - mn) / 8 + mn)) && (x2 == 0)){
        	x1 = mn + (mx - mn) / 2;
        }

        //x4
        if ((v1 >= (mn + 7 * (mx - mn) / 16)) && (v2 <= (13 * (mx - mn) / 16 + mn))){
        	x4 = mn + 3 * (mx - mn) / 4;
        }

        //x5
        if ((v1 >= (mn + 3 * (mx - mn) / 8)) && (v2 <= (9 * (mx - mn) / 8 + mn)) && (x4 == 0)){
        	x5 = mx;
        }

        //x3
        if ((v1 >= (mn + (mx - mn) / 8)) && (v2 <= (7 * (mx - mn) / 8 + mn)) && (x1 == 0) && (x2 == 0) && (x4 == 0) && (x5 == 0)){
        	x3 = mn + 3 * (mx - mn) / 4;
        }

        //x6
        if ((x1 + x2 + x3 + x4 + x5) == 0){
        	x6 = mx;
        }

        finalH = x1 + x2 + x3 + x4 + x5 + x6;

        //y1
        if (x1 > 0){
        	y1 = mn;
        }

        //y2
        if (x2 > 0){
        	y2 = mn + (mx - mn) / 4;
        }

        //y3
        if (x3 > 0){
        	y3 = mn + (mx - mn) / 4;
        }

        //y4
        if (x4 > 0){
        	y4 = mn + (mx - mn) / 2;
        }

        //y5
        if (x5 > 0){
        	y5 = mn + (mx - mn) / 2;
        }

        //y6
        if ((finalH > 0) && ((y1 + y2 + y3 + y4 + y5) == 0)){
        	y6 = mn;
        }

        finalL = y1 + y2 + y3 + y4 + y5 + y6;

        double dmml = (finalH - finalL) / 8;

        return new double[] {finalL, dmml};
    }

    private double[] findLowHigh(int si, int ei){
    	double low = inputs[0][si].getLow();
    	double high = inputs[0][si].getHigh();

        for (int i = si + 1; i <= ei; i++) {
         	IBar bar = inputs[0][i];
         	if (low > bar.getLow()) {
                 low = bar.getLow();
            }
            if (high < bar.getHigh()) {
                high = bar.getHigh();
            }
    	}

        return new double[]{low, high};
    }

    private double identifyFractal(double v2){
    	double fractal = 0;

    	if (v2 <= 250000 && v2 > 25000){
    		fractal = 100000;
    	}
    	else if (v2 <= 25000 && v2 > 2500){
    		fractal = 10000;
    	}
    	else if (v2 <= 2500 && v2 > 250){
    		fractal = 1000;
    	}
    	else if (v2 <= 250 && v2 > 25){
    		fractal = 100;
    	}
    	else if (v2 <= 25 && v2 > 12.5){
    		fractal = 12.5;
    	}
    	else if (v2 <= 12.5 && v2 > 6.25){
    		fractal = 12.5;
    	}
    	else if (v2 <= 6.25 && v2 > 3.125){
    		fractal = 6.25;
    	}
    	else if (v2 <= 3.125 && v2 > 1.5625){
    		fractal = 3.125;
    	}
    	else if (v2 <= 1.5625 && v2 > 0.390625){
    		fractal = 1.5625;
    	}
    	else if (v2 <= 0.390625 && v2 > 0){
    		fractal = 0.1953125;
    	}

    	return fractal;
    }

    private void fixOutputs() {
        for (int i = outputs[0].length - 2; i >= 0; i--) {
            boolean changed = false;
            for (int j = 0; j < indicatorInfo.getNumberOfOutputs(); j++) {
                if (outputs[j][i] != outputs[j][i + 1]) {
                    changed = true;
                    break;
                }
            }

            if (changed) {
                // one of levels changed its value
                for (int j = 0; j < indicatorInfo.getNumberOfOutputs(); j++) {
                    if (outputs[j][i] == outputs[j][i + 1]) {
                        // but some other level remains unchanged
                        int k = 0;
                        while (i - k >= 0 && outputs[j][i - k] == outputs[j][i + 1]) {
                            // add smallest significant offset to separate historical levels
                            outputs[j][i - k] = Double.longBitsToDouble(Double.doubleToLongBits(outputs[j][i - k]) + 1);
                            k++;
                        }
                    }
                }
            }
        }
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
        inputs[index] = (IBar[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
            case 0:
                p = (Integer) value;
                break;
            case 1:
                stepBack = (Integer) value;
                break;
            case 2:
                showHistoricalLevels = (Boolean) value;
                break;
            default:
            	throw new IllegalArgumentException("Invalid optional parameter index!");
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return stepBack + p - 1;
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
        if (values == null) {
            return null;
        }

        double[] output = (double[]) values;

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(stroke);
        g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), 9));

        generalPath.reset();

        int scale = indicatorDrawingSupport.getInstrument().getTickScale();
        decimalFormat.setMaximumFractionDigits(scale);
        decimalFormat.setMinimumFractionDigits(scale);

        boolean formulaHasSmallerPeriod = false;
        Period formulaPeriod = indicatorDrawingSupport.getFormulaPeriod();
        if (formulaPeriod != null) {
            if (indicatorDrawingSupport.isTimeAggregatedPeriod()) {
                Period chartPeriod = indicatorDrawingSupport.getPeriod();
                if (chartPeriod != Period.TICK) {
                    formulaHasSmallerPeriod = formulaPeriod.isSmallerThan(chartPeriod);
                }
            } else {
                formulaHasSmallerPeriod = true;
            }
        }

        IBar[] timeData = indicatorDrawingSupport.getCandles();
        IFormulaTimeData formulaTimeData = indicatorDrawingSupport.getFormulaTimeData();

        int width = indicatorDrawingSupport.getChartWidth();
        int height = indicatorDrawingSupport.getChartHeight();

        int previousX = width + 1;
        double lastValue = Double.NaN;
        int lastX = width + 1;
        int lastY = -1;

        main_loop:
        for (int i = output.length - 1; i >= 0; i--) {
            List<ITimedData> formulaBars = null;
            if (formulaTimeData != null) {
                if (formulaHasSmallerPeriod) {
                    formulaBars = formulaTimeData.getFormulaBars(
                            timeData[i], i > 0 ? timeData[i - 1] : null, i < timeData.length - 1 ? timeData[i + 1] : null);
                } else {
                    if (i > 0 && !formulaTimeData.isFormulaBarStart(timeData[i], timeData[i - 1])) {
                        continue;
                    }
                }
            }

            double value = output[i];

            for (int valueIdx = (formulaBars != null ? formulaBars.size() - 1 : 0); valueIdx >= 0; valueIdx--) {
                if (formulaTimeData != null && formulaBars != null) {
                    Object formulaValue = formulaTimeData.getFormulaValue(formulaBars.get(valueIdx), outputIdx);
                    if (formulaValue instanceof Double) {
                        value = (Double) formulaValue;
                    }
                }

                if (Double.isNaN(value)) {
                    continue;
                }

                int x;
                if (formulaBars != null) {
                    long barTime = formulaBars.get(valueIdx).getTime();
                    x = indicatorDrawingSupport.getXForTime(barTime, false);
                } else {
                    x = (int) indicatorDrawingSupport.getMiddleOfCandle(i);
                }

                int y = (int) indicatorDrawingSupport.getYForValue(value);

                if (Double.isNaN(lastValue)) {
                    lastValue = value;
                    lastY = y;
                }
                if (lastValue == value) {
                    lastX = x;
                    if (valueIdx > 0 || i > 0) {
                        continue;
                    }
                }

                drawLevelLine(g2, outputIdx, lastValue, previousX, lastX, lastY, width, height);

                if (!showHistoricalLevels && !(previousX > width && lastX > width)) {
                    break main_loop;
                }

                previousX = lastX;
                lastValue = value;
                lastX = x;
                lastY = y;
            }
        }

        if (showHistoricalLevels && previousX > lastX) {
            drawLevelLine(g2, outputIdx, lastValue, previousX, lastX, lastY, width, height);
        }

        g2.draw(generalPath);
        shapes.add((Shape) generalPath.clone());

        return null;
    }

    private void drawLevelLine(
            Graphics2D g2,
            int outputIdx,
            double levelValue,
            int rightX,
            int leftX,
            int levelY,
            int width,
            int height
    ) {
        if (!(rightX > width && leftX > width) && !(rightX < 0 && leftX < 0) && (levelY >= 0 && levelY <= height)) {
            rightX = Math.max(0, rightX);
            rightX = Math.min(width, rightX);
            leftX = Math.max(0, leftX);
            leftX = Math.min(width, leftX);

            String label = outputParameterInfos[outputIdx].getName() + ": " + decimalFormat.format(levelValue);

            FontMetrics fm = g2.getFontMetrics();
            int strWidth = fm.stringWidth(label);

            int dx = 20;
            int dy = fm.getHeight() / 2 - fm.getAscent();

            if (rightX - leftX >= dx * 2 + strWidth) {
                g2.drawString(label, rightX - dx - strWidth, levelY - dy);

                generalPath.moveTo(rightX, levelY);
                generalPath.lineTo(rightX - dx, levelY);
                generalPath.moveTo(rightX - dx - strWidth, levelY);
                generalPath.lineTo(leftX, levelY);

            } else {
                generalPath.moveTo(rightX, levelY);
                generalPath.lineTo(leftX, levelY);
            }
        }
    }
}
