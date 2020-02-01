package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.BooleanOptInputDescription;
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

public class TD_Combo implements IIndicator, IDrawingIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final Object[] outputs = new Object[6];

    private int setupLookbackBars = 4;
    private int comboLookbackBars = 2;
    private int setupMaxBars = 9;
    private int comboMaxBars = 13;
    private boolean comboStrictVersion = true;
    private int comboLessStrictBars = 3;

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TD_C", "TD Combo", "Overlap Studies", true, false, true, 1, 6, 6);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[]{
            new OptInputParameterInfo("Setup lookback bars", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(setupLookbackBars, 2, 100, 1)),
            new OptInputParameterInfo("Combo lookback bars", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(comboLookbackBars, 2, 100, 1)),
            new OptInputParameterInfo("Setup max bars", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(setupMaxBars, 2, 100, 1)),
            new OptInputParameterInfo("Combo max bars", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(comboMaxBars, 2, 100, 1)),
            new OptInputParameterInfo("Combo strict version", OptInputParameterInfo.Type.OTHER,
                    new BooleanOptInputDescription(comboStrictVersion)),
            new OptInputParameterInfo("Combo less strict bars", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(comboLessStrictBars, 2, 100, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Bullish Setup", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP),
            new OutputParameterInfo("Bearish Setup", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN),
            new OutputParameterInfo("Bullish Combo", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_UP),
            new OutputParameterInfo("Bearish Combo", OutputParameterInfo.Type.INT, OutputParameterInfo.DrawingStyle.ARROW_SYMBOL_DOWN),
            new OutputParameterInfo("Setup Support", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, false),
            new OutputParameterInfo("Setup Resistance", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE, false)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setDrawnByIndicator(true);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[1].setDrawnByIndicator(true);
        outputParameterInfos[2].setColor(DefaultColors.YELLOW_GREEN);
        outputParameterInfos[2].setDrawnByIndicator(true);
        outputParameterInfos[3].setColor(DefaultColors.DARK_ORANGE);
        outputParameterInfos[3].setDrawnByIndicator(true);
        outputParameterInfos[4].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[4].setGapAtNaN(true);
        outputParameterInfos[5].setColor(DefaultColors.RED);
        outputParameterInfos[5].setGapAtNaN(true);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        for (int i = 0; i < outputs.length; i++) {
            if (i < 4) {
                Arrays.fill((int[]) outputs[i], 0);
            } else {
                Arrays.fill((double[]) outputs[i], Double.NaN);
            }
        }

        boolean activeSetupCount = false;
        boolean activeComboCount = false;
        boolean bullSetupCount = false;
        boolean bullComboCount = false;
        int setupCount = 0;
        int setupSRBar = 0;
        int comboCount = 0;
        int comboCompleteBar = -1;
        double setupSupport = 0.0;
        double setupResistance = 0.0;
        double comboPrice = 0.0;

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            // Check for Bearish TD Price Flip to start Bullish count
            if ((inputs[0][CLOSE][i] > inputs[0][CLOSE][i - setupLookbackBars]) &&
                    (inputs[0][CLOSE][i - 1] < inputs[0][CLOSE][i - 1 - setupLookbackBars])) {
                // Cancel Bearish Setup count
                if (activeSetupCount && !bullSetupCount) {
                    setupCount = 0;
                }
                activeSetupCount = true;
                bullSetupCount = true;
            }

            // Check for Bullish TD Price Flip to start Bearish count
            if ((inputs[0][CLOSE][i] < inputs[0][CLOSE][i - setupLookbackBars]) &&
                    (inputs[0][CLOSE][i - 1] > inputs[0][CLOSE][i - 1 - setupLookbackBars])) {
                // Cancel Bullish Setup count
                if (activeSetupCount && bullSetupCount) {
                    setupCount = 0;
                }
                activeSetupCount = true;
                bullSetupCount = false;
            }

            // Increment count if bar fits price criteria
            if (activeSetupCount && (bullSetupCount ?
                    inputs[0][CLOSE][i] > inputs[0][CLOSE][i - setupLookbackBars] :
                    inputs[0][CLOSE][i] < inputs[0][CLOSE][i - setupLookbackBars])) {
                
                setupCount++;

                // If count complete, reset count
                if (setupCount > setupMaxBars) {
                    activeSetupCount = false;
                    setupCount = 0;
                } else {
                    ((int[]) outputs[0])[j] = (bullSetupCount ? setupCount : 0);
                    ((int[]) outputs[1])[j] = (!bullSetupCount ? -setupCount : 0);
                }

                // Setup count complete
                if (setupCount == setupMaxBars) {
                    if (bullSetupCount) {
                        setupSRBar = 0;
                        for (int k = 1; k < setupMaxBars; k++) {
                            if (inputs[0][LOW][i - k] < inputs[0][LOW][i - setupSRBar]) {
                                setupSRBar = k;
                            }
                        }
                        setupSupport = inputs[0][LOW][i - setupSRBar];
                        for (int k = setupSRBar - 1; k >= 0; k--) {
                            if (inputs[0][HIGH][i - k] < setupSupport) {
                                setupSRBar = -1;
                                break;
                            }
                        }
                        if (setupSRBar >= 0) {
                            for (int k = setupSRBar; k >= 0; k--) {
                                ((double[]) outputs[4])[j - k] = setupSupport;
                            }
                        }
                    } else {
                        setupSRBar = 0;
                        for (int k = 1; k < setupMaxBars; k++) {
                            if (inputs[0][HIGH][i - k] > inputs[0][HIGH][i - setupSRBar]) {
                                setupSRBar = k;
                            }
                        }
                        setupResistance = inputs[0][HIGH][i - setupSRBar];
                        for (int k = setupSRBar - 1; k >= 0; k--) {
                            if (inputs[0][LOW][i - k] > setupResistance) {
                                setupSRBar = -1;
                                break;
                            }
                        }
                        if (setupSRBar >= 0) {
                            for (int k = setupSRBar; k >= 0; k--) {
                                ((double[]) outputs[5])[j - k] = setupResistance;
                            }
                        }
                    }

                    // Cancel active Combo count if opposite Setup count completes
                    if (activeComboCount && (bullSetupCount != bullComboCount)) {
                        activeComboCount = false;
                        comboCount = 0;
                    }

                    // Initiate Combo count if previous Combo didn't complete during current Setup count
                    if (!activeComboCount && (comboCompleteBar < i - setupMaxBars + 1)) {
                        activeComboCount = true;
                        bullComboCount = bullSetupCount;
                        comboPrice = inputs[0][CLOSE][i] + (bullComboCount ? -1 : +1);
                    }
                }

            } else {
                // Cancel Setup count if price doesn't meet criteria for continuation
                if (activeSetupCount && (setupCount < setupMaxBars)) {
                    activeSetupCount = false;
                    setupCount = 0;
                }
            }

            // Cancel active Combo count if TDST Support/Resistance broken by High/Low
            if (activeComboCount && (bullComboCount ?
                    inputs[0][HIGH][i] < setupSupport :
                    inputs[0][LOW][i] > setupResistance)) {
                activeComboCount = false;
                comboCount = 0;
            }

            if (activeComboCount) {
                // If new Combo count, cycle back through Setup count bars to initiate Combo count, else check current bar for Combo count
                for (int k = ((comboCount == 0) && (setupCount == setupMaxBars) ? setupMaxBars - 1 : 0); k >= 0; k--) {
                    if (comboStrictVersion || (comboCount < comboMaxBars - comboLessStrictBars)) {
                        if (bullComboCount ?
                                (inputs[0][CLOSE][i - k] >= inputs[0][LOW][i - k - comboLookbackBars]) &&
                                (inputs[0][LOW][i - k] >= inputs[0][LOW][i - k - 1]) &&
                                (inputs[0][CLOSE][i - k] > comboPrice) &&
                                (inputs[0][CLOSE][i - k] > inputs[0][CLOSE][i - k - 1]) :
                                (inputs[0][CLOSE][i - k] <= inputs[0][HIGH][i - k - comboLookbackBars]) &&
                                (inputs[0][HIGH][i - k] <= inputs[0][HIGH][i - k - 1]) &&
                                (inputs[0][CLOSE][i - k] < comboPrice) &&
                                (inputs[0][CLOSE][i - k] < inputs[0][CLOSE][i - k - 1])) {

                            comboCount++;
                            comboPrice = inputs[0][CLOSE][i - k];

                            ((int[]) outputs[2])[j - k] = (bullComboCount ? comboCount + (activeSetupCount ? 1000 : 0) : 0);
                            ((int[]) outputs[3])[j - k] = (!bullComboCount ? -comboCount - (activeSetupCount ? 1000 : 0) : 0);
                        }

                    } else {
                        if (bullComboCount ?
                                inputs[0][CLOSE][i - k] > comboPrice :
                                inputs[0][CLOSE][i - k] < comboPrice) {

                            comboCount++;
                            comboPrice = inputs[0][CLOSE][i - k];

                            ((int[]) outputs[2])[j - k] = (bullComboCount ? comboCount + (activeSetupCount ? 1000 : 0) : 0);
                            ((int[]) outputs[3])[j - k] = (!bullComboCount ? -comboCount - (activeSetupCount ? 1000 : 0) : 0);
                        }
                    }
                }

                // Combo count complete
                if (comboCount == comboMaxBars) {
                    activeComboCount = false;
                    comboCount = 0;
                    comboCompleteBar = i;
                }
            }
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
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            setupLookbackBars = (Integer) value;
            break;
        case 1:
            comboLookbackBars = (Integer) value;
            break;
        case 2:
            setupMaxBars = (Integer) value;
            break;
        case 3:
            comboMaxBars = (Integer) value;
            break;
        case 4:
            comboStrictVersion = (Boolean) value;
            break;
        case 5:
            comboLessStrictBars = (Integer) value;
            break;
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = array;
    }

    public int getLookback() {
        return Math.max(setupLookbackBars + 1, comboLookbackBars);
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
        int[] values = (int[]) values2;
        if ((values == null) || (values.length == 0)) {
            return null;
        }

        boolean onlyMax = false;
        Font defaultTD;
        Font defaultBigTD;
        String fontName = g.getFont().getName();
        if (indicatorDrawingSupport.getCandleWidthInPixels() == 1) {
            // reduce font size when showing candles as lines
            defaultTD = new Font(fontName, Font.PLAIN, 9);
            defaultBigTD = new Font(fontName, Font.BOLD, 11);
        } else if (indicatorDrawingSupport.getCandleWidthInPixels() > 20) {
            // increase font size when showing candles with width more than 20 pixels
            defaultTD = new Font(fontName, Font.PLAIN, 12);
            defaultBigTD = new Font(fontName, Font.BOLD, 15);
        } else {
            // default font
            defaultTD = new Font(fontName, Font.PLAIN, 10);
            defaultBigTD = new Font(fontName, Font.BOLD, 13);
        }
        if ((indicatorDrawingSupport.getCandleWidthInPixels() == 1) &&
                (indicatorDrawingSupport.getSpaceBetweenCandlesInPixels() == 0)) {
            // max zoom out
            onlyMax = true;
        }
        FontMetrics metricsTD = g.getFontMetrics(defaultTD);
        FontMetrics metricsBigTD = g.getFontMetrics(defaultBigTD);

        // check for visible candles
        for (int i = indicatorDrawingSupport.getIndexOfFirstCandleOnScreen(),
                n = indicatorDrawingSupport.getNumberOfCandlesOnScreen(); n > 0; i++, n--) {
            if ((values[i] == 0) || (values[i] == Integer.MIN_VALUE)) {
                continue;
            }

            int shift = Math.abs(values[i] / 1000);
            int value = values[i] % 1000;

            FontMetrics metrics;
            if (Math.abs(value) == (outputIdx < 2 ? setupMaxBars : comboMaxBars)) {
                g.setFont(defaultBigTD);
                metrics = metricsBigTD;
            } else if (!onlyMax) {
                g.setFont(defaultTD);
                metrics = metricsTD;
            } else {
                continue;
            }

            String text = Integer.toString(Math.abs(value));
            int x = (int) indicatorDrawingSupport.getMiddleOfCandle(i) - metrics.stringWidth(text) / 2;
            int y;
            if (value > 0) {
                y = (int) indicatorDrawingSupport.getYForValue(indicatorDrawingSupport.getCandles()[i].getHigh()) -
                        metrics.getDescent() - shift * metricsTD.getHeight();
            } else {
                y = (int) indicatorDrawingSupport.getYForValue(indicatorDrawingSupport.getCandles()[i].getLow()) +
                        metrics.getAscent() + shift * metricsTD.getHeight();
            }

            g.setColor(color);
            g.drawString(text, x, y);
        }

        return null;
    }
}
