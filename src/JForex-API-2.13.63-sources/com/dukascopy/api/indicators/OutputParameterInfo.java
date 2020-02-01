/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.awt.Color;
import java.util.Arrays;

import com.dukascopy.api.Instrument;

/**
 * Describes output
 *
 * @author Dmitry Shohov
 */
public class OutputParameterInfo {

    /**
     * Show output as line
     * @deprecated use {@link DrawingStyle#LINE} instead
     */
    @Deprecated
    public static final int LINE = 0x00000001;

    /**
     * Show output as dotted line
     * @deprecated use {@link DrawingStyle#DOT_LINE} instead
     */
    @Deprecated
    public static final int DOT_LINE = 0x00000002;

    /**
     * Show output as dashed line
     * @deprecated use {@link DrawingStyle#DASH_LINE} instead
     */
    @Deprecated
    public static final int DASH_LINE = 0x00000004;

    /**
     * Show output as dots
     * @deprecated use {@link DrawingStyle#DOTS} instead
     */
    @Deprecated
    public static final int DOT = 0x00000008;

    /**
     * Show output as histogram
     * @deprecated use {@link DrawingStyle#HISTOGRAM} instead
     */
    @Deprecated
    public static final int HISTO = 0x00000010;

    /**
     * If value is not zero, then draw pattern
     * @deprecated use {@link DrawingStyle#PATTERN_BOOL} instead
     */
    @Deprecated
    public static final int PATTERN_BOOL = 0x00000020;

    /**
     * If value:
     *      == 0 - no pattern,
     *      &gt; 0 - bullish,
     *      &lt; 0 - bearish
     * @deprecated use {@link DrawingStyle#PATTERN_BULL_BEAR} instead
     */
    @Deprecated
    public static final int PATTERN_BULL_BEAR = 0x00000040;

    /**
     * If value:
     *      == 0 - neutral
     *      ]0..100] getting bullish
     *      ]100..200] bullish
     *      [-100..0[ getting bearish
     *      [-200..100[ bearish
     * @deprecated use {@link DrawingStyle#PATTERN_STRENGTH} instead
     */
    @Deprecated
    public static final int PATTERN_STRENGTH = 0x00000080;

    /**
     * Specifies how to draw output
     *
     * @author Dmitry Shohov
     */
    public enum DrawingStyle {
        /**
         * Don't draw anything
         */
        NONE(0),
        /**
         * Show output as line
         */
        LINE(0x00000001),
        /**
         * Show output as dashed line
         */
        DASH_LINE(0x00000004),
        /**
         * Show output as fine dashed line
         */
        FINE_DASHED_LINE(0),
        /**
         * Show output as dotted line
         */
        DOT_LINE(0x00000002),
        /**
         * Show output as dashed and dotted line
         */
        DASHDOT_LINE(0),
        /**
         * Show output as dashed and double dotted line
         */
        DASHDOTDOT_LINE(0),
        /**
         * Show output as long and short dashed line
         */
        LONG_AND_SHORT_DASH_LINE(0),
        /**
         * Show output as level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_LINE(0),
        /**
         * Show output as dashed level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DASH_LINE(0),
        /**
         * Show output as fine dashed level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_FINE_DASHED_LINE(0),
        /**
         * Show output as dotted level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DOT_LINE(0),
        /**
         * Show output as dashed and dotted level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DASHDOT_LINE(0),
        /**
         * Show output as dashed and double dotted level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_DASHDOTDOT_LINE(0),
        /**
         * Show output as long and short dashed level. Levels are horizontal lines that are drawn by the price at last candle
         */
        LEVEL_LONG_AND_SHORT_DASH_LINE(0),
        /**
         * Show output as dots
         */
        DOTS(0x00000008),
        /**
         * Show output as histogram
         */
        HISTOGRAM(0x00000010),
        /**
         * Draws arrow symbol at non NaN or no Integer.MIN_VALUE values
         */
        ARROW_SYMBOL_UP(0),
        /**
         * Draw arrow symbol at non NaN or no Integer.MIN_VALUE values
         */
        ARROW_SYMBOL_DOWN(0),
        /**
         * If value is not zero, then draw pattern
         */
        PATTERN_BOOL(0x00000020),
        /**
         * If value:
         *      == 0 - no pattern,
         *      &gt; 0 - bullish,
         *      &lt; 0 - bearish
         */
        PATTERN_BULL_BEAR(0x00000040),
        /**
         * If value:
         *      == 0 - neutral
         *      ]0..100] getting bullish
         *      ]100.. bullish
         *      [-100..0[ getting bearish
         *      ..100[ bearish
         */
        PATTERN_STRENGTH(0x00000080),
        /**
         * Draw candles based on returned array of IBar objects
         */
        CANDLES(0);

        public static final DrawingStyle[] LINE_STYLE = new DrawingStyle[] {
    		DrawingStyle.LINE,
    		DrawingStyle.DASH_LINE,
    		DrawingStyle.FINE_DASHED_LINE,
    		DrawingStyle.DOT_LINE,
    		DrawingStyle.DASHDOT_LINE,
    		DrawingStyle.DASHDOTDOT_LINE,
    		DrawingStyle.LONG_AND_SHORT_DASH_LINE
    	};

        private final int flag;

        private DrawingStyle(int flag) {
            this.flag = flag;
        }

        public static DrawingStyle fromFlagValue(int flag) {
            for (DrawingStyle style : DrawingStyle.values()) {
                if ((style.flag & flag) > 0) {
                    return style;
                }
            }
            return NONE;
        }

        public int getFlagValue() {
            return flag;
        }

        public boolean isOutputAsLine(){
        	return Arrays.binarySearch(LINE_STYLE, this) >= 0;
        }
    }

    /**
     * Type of the output
     *
     * @author Dmitry Shohov
     */
    public enum Type {
        /**
         * Output is array of doubles, Double.NaN values means there is no value at that point
         */
        DOUBLE,
        /**
         * Output is array of integers, Integer.MIN_VALUE values means there is no value at that point
         */
        INT,
        /**
         * Any object, outputs with this type can be only interpreted by strategies or drawn by selfdrawing indicators
         */
        OBJECT,
        /**
         * Array of IBar objects
         */
        CANDLE
    }

    private String name;
    private Type type;
	private Instrument instrument;
    private DrawingStyle drawingStyle;
    private Color color;
    private Color color2;
    private char arrowSymbol;
    private boolean histogramTwoColor = true;
    private int shift;
    private int lineWidth;
    private boolean drawnByIndicator;
    private boolean gapAtNaN;
    private boolean showValueOnChart = true;
    private boolean showPopupInfo = true;
    private boolean showOutput = true;
    private float opacityAlpha = 1f;
    private int displayOrder = -1;

    /**
     * Creates empty parameter description without setting any field
     */
    public OutputParameterInfo() {
    }

    /**
     * Creates output parameter descriptor and sets all fields
     *
     * @param name name of the output
     * @param type type of the output
     * @param flags flags of the output
     * @deprecated use {@link #OutputParameterInfo(String, Type, DrawingStyle)} instead
     */
    public OutputParameterInfo(String name, Type type, int flags) {
        this.drawingStyle = DrawingStyle.fromFlagValue(flags);
        this.name = name;
        this.type = type;
    }

    /**
     * Creates output parameter descriptor and sets all fields.
     *
     * @param name name of the output
     * @param type type of the output
     * @param drawingStyle specifies how to draw this output
     */
    public OutputParameterInfo(String name, Type type, DrawingStyle drawingStyle) {
        this(name, type, drawingStyle, true);
    }

    /**
     * Creates output parameter descriptor and sets all fields.
     *
     * @param name name of the output
     * @param type type of the output
     * @param drawingStyle specifies how to draw this output
     * @param lastValueOnChart <code>true</code> to show indicator's last value
     * for this output on chart.
     */
    public OutputParameterInfo(String name, Type type, DrawingStyle drawingStyle, boolean lastValueOnChart) {
        this.drawingStyle = drawingStyle;
        this.name = name;
        this.type = type;
        if (drawingStyle == DrawingStyle.ARROW_SYMBOL_DOWN) {
            arrowSymbol = '\u21D3';
        } else if (drawingStyle == DrawingStyle.ARROW_SYMBOL_UP) {
            arrowSymbol = '\u21D1';
        }
        setShowValueOnChart(lastValueOnChart);
    }

    /**
     * Returns name of the output
     *
     * @return name of the output
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the output
     *
     * @param name name of the output
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns type of the output
     *
     * @return type of the output
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets type of the output
     *
     * @param type type of the output
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns instrument of this output, or null if no instrument was set
     *
     * @return instrument of the output
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * Sets instrument of this output. This allows indicator to draw values on the secondary instruments of chart
     *
     * @param instrument instrument of the output
     */
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Returns flags of the output
     *
     * @return flags of the output
     * @deprecated use {@link #getDrawingStyle()} instead
     */
    public int getFlags() {
        return drawingStyle.flag;
    }

    /**
     * Sets flags of the output. Use | operator to specify more than one flag
     *
     * @param flags flags of the output
     * @deprecated use {@link #setDrawingStyle(DrawingStyle)} instead
     */
    public void setFlags(int flags) {
        this.drawingStyle = DrawingStyle.fromFlagValue(flags);
    }

    /**
     * Returns style that specifies how to draw this output
     *
     * @return style that specifies how to draw this output
     */
    public DrawingStyle getDrawingStyle() {
        return drawingStyle;
    }

    /**
     * Sets style that specifies how to draw this output
     *
     * @param drawingStyle specifies how to draw this output
     */
    public void setDrawingStyle(DrawingStyle drawingStyle) {
        this.drawingStyle = drawingStyle;
    }

    /**
     * Returns default color for output or null if no color was set
     * Used as <b>trend up</b> color for outputs with {@link DrawingStyle#isOutputAsLine()} is <code>true</code>
     *
     * @return default color for output or null
     * @see #getColor2()
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets default color to use when drawing this output
     *
     * @param color color to draw with
     */
    public void setColor(Color color) {
        this.color = color;
    }

	/**
	 * Used as <b>trend down</b> color for outputs with {@link DrawingStyle#isOutputAsLine()} is <code>true</code>
	 * By default equals to {@link #getColor() color}
     *
	 * @return the color2
	 * @see #getColor()
	 */
	public Color getColor2() {
		if (color2 == null){
			color2 = getColor();
		}
		return color2;
	}

	/**
	 * Sets default color used when drawing <b>trend down</b> outputs
     *
	 * @param color2 the color2 to set
	 */
	public void setColor2(Color color2) {
		this.color2 = color2;
	}

	/**
     * Returns arrow character for outputs with {@link DrawingStyle#ARROW_SYMBOL_UP} or {@link DrawingStyle#ARROW_SYMBOL_DOWN} style
     *
     * @return arrow character
     */
    public char getArrowSymbol() {
        return arrowSymbol;
    }

    /**
     * Sets arrow character for outputs with {@link DrawingStyle#ARROW_SYMBOL_UP} or {@link DrawingStyle#ARROW_SYMBOL_DOWN} style
     *
     * @param arrowSymbol arrow character
     */
    public void setArrowSymbol(char arrowSymbol) {
        this.arrowSymbol = arrowSymbol;
    }

    /**
     * Returns true if histogram should be shown in two colors
     *
     * @return true if histogram should be shown in two colors
     */
    public boolean isHistogramTwoColor() {
        return histogramTwoColor;
    }

    /**
     * If set to true and drawing style is histogram, then it will be shown in two colors, positive values with green and negative with red
     *
     * @param histogramTwoColor if true, then show histogram in two colors
     */
    public void setHistogramTwoColor(boolean histogramTwoColor) {
        this.histogramTwoColor = histogramTwoColor;
    }

    /**
     * Shifts output by number of candles returned
     *
     * @return number of candles to shift this output
     */
    public int getShift() {
        return shift;
    }

    /**
     * Sets number of candles to shift this output
     *
     * @param shift number of candles to shift this output
    */
    public void setShift(int shift) {
        this.shift = shift;
    }

    /**
     * Returns true if indicator draws this output itself
     *
     * @return true if indicator draws this output itself
     */
    public boolean isDrawnByIndicator() {
        return drawnByIndicator;
    }

    /**
     * If set to true, than indicator should have public method<br>
     * {@code public void drawOutput(Graphics g, int outputIdx, Object values, Color color,
     * IIndicatorDrawingSupport indicatorDrawingSupport, List<Shape> shapes, Map<Color, List<Point>>Map<Color, List<Point>> handles)}<br>
     * Also it's possible to define<br>
     * {@code public double[] getMinMax(int outputIdx, Object values)}<br>
     * method, that will be called to define minimum and maximum values for scale<br>
     * Please look at the description of IIndicator interface for more info
     *
     * @param drawnByIndicator true if indicator draws this output itself
    */
    public void setDrawnByIndicator(boolean drawnByIndicator) {
        this.drawnByIndicator = drawnByIndicator;
    }

    /**
     * Returns true if drawing logic should make a gap in lines when there is a NaN or Integer.MIN_VALUE in output
     *
     * @return true for gaps
     */
    public boolean isGapAtNaN() {
        return gapAtNaN;
    }

    /**
     * Set to true to make gaps at candles with Double.NaN or Integer.MIN_VALUE in output
     *
     * @param gapAtNaN true for gaps
     */
    public void setGapAtNaN(boolean gapAtNaN) {
        this.gapAtNaN = gapAtNaN;
    }

    /**
     * Checks should last value be specially drawn on chart or not.
     *
     * @return <code>true</code> to draw the last value, <code>false</code> to not.
     */
    public boolean isShowValueOnChart() {
        return showValueOnChart;
    }

    /**
     * Defines should last value be specially drawn on chart or not.
     *
     * @param showValueOnChart <code>true</code> to draw the last value,
     * <code>false</code> to not.
    */
    public void setShowValueOnChart(boolean showValueOnChart) {
        this.showValueOnChart = showValueOnChart;
    }

    /**
     * Checks should tooltip with info appear on mouse over the output or not.
     *
     * @return true if tooltip should be shown
     */
    public boolean isShowPopupInfo() {
        return showPopupInfo;
    }

    /**
     * Defines should tooltip with info appear on mouse over the output or not.
     *
     * @param showPopupInfo true of tooltip should be shown
     */
    public void setShowPopupInfo(boolean showPopupInfo) {
        this.showPopupInfo = showPopupInfo;
    }

    /**
     * Checks should output be drawn or not.
     *
     * @return <code>true</code> to draw the output, <code>false</code> to not.
     */
    public boolean isShowOutput() {
		return showOutput;
	}

    /**
     * Defines should output be drawn or not.
     *
     * @param showOutput <code>true</code> to draw the output, <code>false</code> to not.
     */
	public void setShowOutput(boolean showOutput) {
		this.showOutput = showOutput;
	}

	/**
	 * Returns transparency alpha value
     *
	 * @return transparency alpha value
	 */
	public float getOpacityAlpha() {
    	return opacityAlpha;
    }

	/**
	 * Sets the transparency alpha value
     *
	 * @param alpha ranges from 0 to 1
	 */
	public void setOpacityAlpha(float alpha) {
    	this.opacityAlpha = alpha;
    }

	/**
	 * Returns drawing line width
     *
	 * @return drawing line width
	 */
    public int getLineWidth() {
        return lineWidth;
    }

	/**
	 * Sets drawing line width
     *
	 * @param lineWidth  drawing line width
	 */
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * Returns default display order of indicator output.
     * 
     * @return default display order (-1 if not defined)
     */
    public int getDisplayOrder() {
        return displayOrder;
    }

    /**
     * Sets default display order of indicator output.
     *
     * @param displayOrder default display order
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
