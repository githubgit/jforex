/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.beans.PropertyChangeListener;

import com.dukascopy.api.IChart.Type;
import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Represents graphical object on the chart
 *
 * @author Denis Larka
 */
public interface IChartObject {
	/**
	 * String constant to be used in PropertyChangeListener.
	 */
	public static final String PROPERTY_FOREGROUND_COLOR = "color";
	/**
	 * String constant to be used in PropertyChangeListener.
	 */
	public static final String PROPERTY_FOREGROUND_ALPHA = "alpha";
	/**
	 * String constant to be used in PropertyChangeListener.
	 */
	public static final String PROPERTY_STROKE = "stroke";
	/**
	 * String constant to be used in PropertyChangeListener.
	 */
	public static final String PROPERTY_FONT = "font";
	/**
	 * String constant to be used in PropertyChangeListener.
	 */
	public static final String PROPERTY_STICKY = "sticky";
	/**
	 * String constant to be used in PropertyChangeListener.
	 * <code>PropertyChangeListener</code> fires <code>IndexedPropertyChangeEvent</code> with index of modified point
	 */
	public static final String PROPERTY_POINT_TIME = "time";
	/**
	 * String constant to be used in PropertyChangeListener.
	 * <code>PropertyChangeListener</code> fires <code>IndexedPropertyChangeEvent</code> with index of modified point
	 */
	public static final String PROPERTY_POINT_PRICE = "price";


	/**
	 * @deprecated
	 */
	@Deprecated
    enum ATTR_LONG {
        TIME1,                  // Datetime value to set/get first coordinate time part.
        TIME2,                  // Datetime value to set/get second coordinate time part.
        TIME3                   // Datetime value to set/get third coordinate time part.
    }

	/**
	 * @deprecated
	 */
	@Deprecated
    enum ATTR_DOUBLE {
        PRICE1,                 // Double value to set/get first coordinate price part.
        PRICE2,                 // Double value to set/get second coordinate price part.
        PRICE3,                 // Double value to set/get third coordinate price part.
        SCALE,                  // Double value to set/get scale object property.
        ANGLE,                  // Double value to set/get angle object property in degrees.
        DEVIATION,              // Double value to set/get deviation property for Standard deviation objects.
        FILL_OPACITY,			// Double value to set/get opacity alpha property for filling closed path shapes (triangles, rectangles, ellipses)
        FIBO_LEVEL1,            // Fibonacci object level index 1 to set/get.
        FIBO_LEVEL2,            // Fibonacci object level index 2 to set/get.
        FIBO_LEVEL3,            // Fibonacci object level index 3 to set/get.
        FIBO_LEVEL4,            // Fibonacci object level index 4 to set/get.
        FIBO_LEVEL5,            // Fibonacci object level index 5 to set/get.
        FIBO_LEVEL6,            // Fibonacci object level index 6 to set/get.
        FIBO_LEVEL7,            // Fibonacci object level index 7 to set/get.
        FIBO_LEVEL8,            // Fibonacci object level index 8 to set/get.
        FIBO_LEVEL9,            // Fibonacci object level index 9 to set/get.
        FIBO_LEVEL10,           // Fibonacci object level index 10 to set/get.
        FIBO_LEVEL11,           // Fibonacci object level index 11 to set/get.
        FIBO_LEVEL12,           // Fibonacci object level index 12 to set/get.
        FIBO_LEVEL13,           // Fibonacci object level index 13 to set/get.
        FIBO_LEVEL14,           // Fibonacci object level index 14 to set/get.
        FIBO_LEVEL15,           // Fibonacci object level index 15 to set/get.
        FIBO_LEVEL16,           // Fibonacci object level index 16 to set/get.
        FIBO_LEVEL17,           // Fibonacci object level index 17 to set/get.
        FIBO_LEVEL18,           // Fibonacci object level index 18 to set/get.
        FIBO_LEVEL19,           // Fibonacci object level index 19 to set/get.
        FIBO_LEVEL20,           // Fibonacci object level index 20 to set/get.
        FIBO_LEVEL21,           // Fibonacci object level index 21 to set/get.
        FIBO_LEVEL22,           // Fibonacci object level index 22 to set/get.
        FIBO_LEVEL23,           // Fibonacci object level index 23 to set/get.
        FIBO_LEVEL24,           // Fibonacci object level index 24 to set/get.
        FIBO_LEVEL25,           // Fibonacci object level index 25 to set/get.
        FIBO_LEVEL26,           // Fibonacci object level index 26 to set/get.
        FIBO_LEVEL27,           // Fibonacci object level index 27 to set/get.
        FIBO_LEVEL28,           // Fibonacci object level index 28 to set/get.
        FIBO_LEVEL29,           // Fibonacci object level index 29 to set/get.
        FIBO_LEVEL30,           // Fibonacci object level index 30 to set/get.
        FIBO_LEVEL31,           // Fibonacci object level index 31 to set/get.
        FIBO_LEVEL32,           // Fibonacci object level index 32 to set/get.
    }

	/**
     * @deprecated
     */
	@Deprecated
    enum ATTR_INT {
        STYLE,                  // Value is one of STYLE_SOLID, STYLE_DASH, STYLE_DOT, STYLE_DASHDOT, STYLE_DASHDOTDOT constants to set/get object line style.
        WIDTH,                  // Integer value to set/get object line width. Can be from 1 to 5.
        FONTSIZE,               // Integer value to set/get font size for text objects.
        LEVELSTYLE,             // Value is one of STYLE_SOLID, STYLE_DASH, STYLE_DOT, STYLE_DASHDOT, STYLE_DASHDOTDOT constants to set/get object level line style.
        FIBOLEVELS,             // Integer value to set/get Fibonacci object level count. Can be from 0 to 32.
        LEVELWIDTH,             // Integer value to set/get object level line width. Can be from 1 to 5.
        ARROWCODE,              // Integer value or arrow enumeration to set/get arrow code object property.
        TIMEFRAMES,             // Value can be one or combination (bitwise addition) of object visibility constants to set/get timeframe object property.
        CORNER,                 // Integer value to set/get anchor corner property for label objects. Must be from 0-3.
        XDISTANCE,              // Integer value to set/get anchor X distance object property in pixels.
        YDISTANCE,              // Integer value is to set/get anchor Y distance object property in pixels.
    }

	/**
     * @deprecated
     */
	@Deprecated
    enum ATTR_COLOR {
        LEVELCOLOR,             // Color value to set/get object level line color.
        COLOR,                  // Color value to set/get object color.
        FILLCOLOR,				// Color value to set/get object fill color for closed path shapes (triangles, rectangles, ellipses)
        LEVEL1,					// Color value to set/get object level index 1
        LEVEL2,					// Color value to set/get object level index 2
        LEVEL3,					// Color value to set/get object level index 3
        LEVEL4,					// Color value to set/get object level index 4
        LEVEL5,					// Color value to set/get object level index 5
        LEVEL6,					// Color value to set/get object level index 6
        LEVEL7,					// Color value to set/get object level index 7
        LEVEL8,					// Color value to set/get object level index 8
        LEVEL9,					// Color value to set/get object level index 9
        LEVEL10,				// Color value to set/get object level index 10
        LEVEL11,				// Color value to set/get object level index 11
        LEVEL12,				// Color value to set/get object level index 12
        LEVEL13,				// Color value to set/get object level index 13
        LEVEL14,				// Color value to set/get object level index 14
        LEVEL15,				// Color value to set/get object level index 15
        LEVEL16,				// Color value to set/get object level index 16
        LEVEL17,				// Color value to set/get object level index 17
        LEVEL18,				// Color value to set/get object level index 18
        LEVEL19,				// Color value to set/get object level index 19
        LEVEL20,				// Color value to set/get object level index 20
        LEVEL21,				// Color value to set/get object level index 21
        LEVEL22,				// Color value to set/get object level index 22
        LEVEL23,				// Color value to set/get object level index 23
        LEVEL24,				// Color value to set/get object level index 24
        LEVEL25,				// Color value to set/get object level index 25
        LEVEL26,				// Color value to set/get object level index 26
        LEVEL27,				// Color value to set/get object level index 27
        LEVEL28,				// Color value to set/get object level index 28
        LEVEL29,				// Color value to set/get object level index 29
        LEVEL30,				// Color value to set/get object level index 30
        LEVEL31,				// Color value to set/get object level index 31
        LEVEL32					// Color value to set/get object level index 32
    }

	/**
     * @deprecated
     */
	@Deprecated
    enum ATTR_TEXT {
        LEVEL1,					// Text value to set/get object level index 1
        LEVEL2,					// Text value to set/get object level index 2
        LEVEL3,					// Text value to set/get object level index 3
        LEVEL4,					// Text value to set/get object level index 4
        LEVEL5,					// Text value to set/get object level index 5
        LEVEL6,					// Text value to set/get object level index 6
        LEVEL7,					// Text value to set/get object level index 7
        LEVEL8,					// Text value to set/get object level index 8
        LEVEL9,					// Text value to set/get object level index 9
        LEVEL10,				// Text value to set/get object level index 10
        LEVEL11,				// Text value to set/get object level index 11
        LEVEL12,				// Text value to set/get object level index 12
        LEVEL13,				// Text value to set/get object level index 13
        LEVEL14,				// Text value to set/get object level index 14
        LEVEL15,				// Text value to set/get object level index 15
        LEVEL16,				// Text value to set/get object level index 16
        LEVEL17,				// Text value to set/get object level index 17
        LEVEL18,				// Text value to set/get object level index 18
        LEVEL19,				// Text value to set/get object level index 19
        LEVEL20,				// Text value to set/get object level index 20
        LEVEL21,				// Text value to set/get object level index 21
        LEVEL22,				// Text value to set/get object level index 22
        LEVEL23,				// Text value to set/get object level index 23
        LEVEL24,				// Text value to set/get object level index 24
        LEVEL25,				// Text value to set/get object level index 25
        LEVEL26,				// Text value to set/get object level index 26
        LEVEL27,				// Text value to set/get object level index 27
        LEVEL28,				// Text value to set/get object level index 28
        LEVEL29,				// Text value to set/get object level index 29
        LEVEL30,				// Text value to set/get object level index 30
        LEVEL31,				// Text value to set/get object level index 31
        LEVEL32					// Text value to set/get object level index 32
    }

	/**
     * @deprecated
     */
	@Deprecated
    enum ATTR_BOOLEAN {
        BACK,                   // Boolean value to set/get background drawing flag for object.
        RAY,                    // Boolean value to set/get ray flag of object.
        ELLIPSE                 // Boolean value to set/get ellipse flag for fibo arcs.
    }

    /**
     * Adds listener to receive events about user changes on the object. Works only with unlocked objects
     *
     * @param listener listener for events
     */
    void setChartObjectListener(ChartObjectListener listener);

    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list.
     * The listener is registered for all properties.
     * <p>
     * A <code>PropertyChangeEvent</code> will get fired in response to setting
     * a bound property, e.g. <code>setColor</code>, <code>setOpacity</code>,
     * or <code>setStroke</code>.
     * Note that if the current component is inheriting its property
     * from its container, then no event will be
     * fired in response to a change in the inherited property.
     *
     * @param listener  The <code>PropertyChangeListener</code> to be added
     *
     * @see #removePropertyChangeListener
     * @see #getPropertyChangeListeners()
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list.
     * This removes a <code>PropertyChangeListener</code> that was registered
     * for all properties.
     *
     * @param listener  the <code>PropertyChangeListener</code> to be removed
     * @see #addPropertyChangeListener
     *
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns an array of all the <code>PropertyChangeListener</code>s added
     * to this ChartObject with addPropertyChangeListener().
     *
     * @return all of the <code>PropertyChangeListener</code>s added or an empty
     *         array if no listeners have been added
     *
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    PropertyChangeListener[] getPropertyChangeListeners();

    /**
     * Assigns value of type <code>long</code> to field
     * @param field chart object field
     * @param value to be assigned
     * @deprecated
     */
    @Deprecated
    void setAttrLong(ATTR_LONG field, long value);

    /**
     * Retrieves value of field
     * @param field chart object field
     * @return value of field
     * @deprecated
     */
    @Deprecated
    long getAttrLong(ATTR_LONG field);

    /**
     * Assigns value of type <code>double</code> to field
     * @param field chart object field
     * @param value to be assigned
     * @deprecated
     */
    @Deprecated
    void setAttrDouble(ATTR_DOUBLE field, double value);

    /**
     * Retrieves value of field
     * @param field chart object field
     * @return value of field
     * @deprecated
     */
    @Deprecated
    double getAttrDouble(ATTR_DOUBLE field);

    /**
     * Assigns value of type <code>int</code> to field
     * @param field chart object field
     * @param value to be assigned
     * @deprecated
     */
    @Deprecated
    void setAttrInt(ATTR_INT field, int value);

    /**
     * Retrieves value of field
     * @param field chart object field
     * @return value of field
     * @deprecated
     */
    @Deprecated
    int getAttrInt(ATTR_INT field);

    /**
     * Assigns value of type <code>boolean</code> to field
     * @param field chart object field
     * @param value to be assigned
     * @deprecated
     */
    @Deprecated
    void setAttrBoolean(ATTR_BOOLEAN field, boolean value);

    /**
     * Retrieves value of field
     * @param field chart object field
     * @return value of field
     * @deprecated
     */
    @Deprecated
    boolean getAttrBoolean(ATTR_BOOLEAN field);

    /**
     * Assigns value of type <code>Color</code> to field
     * @param field chart object field
     * @param value to be assigned
     * @deprecated
     */
    @Deprecated
    void setAttrColor(ATTR_COLOR field, Color value);

    /**
     * Retrieves value of field
     * @param field chart object field
     * @return value of field
     * @deprecated
     */
    @Deprecated
    Color getAttrColor(ATTR_COLOR field);

    /**
     * Assigns value of type <code>String</code> to field
     * @param field chart object field
     * @param value to be assigned
     * @deprecated
     */
    @Deprecated
    void setAttrText(ATTR_TEXT field, String value);

    /**
     * Retrieves value of field
     * @param field chart object field
     * @return value of field
     * @deprecated
     */
    @Deprecated
    String getAttrText(ATTR_TEXT field);

    /**
     * Moves first point of the object to the new position dragging whole object with all other points.
     * Do not initiate chart repaint immediately, use IChart.repaint() method if necessary.
     *
     * @param time new time of the first point
     * @param price new price of the first point
     */
    void move(long time, double price);

    /**
     * Returns object's text
     * @return text
     */
    String getText();

    /**
     * Sets text to show with the object. Ignored if object doesn't support text
     *
     * @param text text to show
     */
    void setText(String text);

    /**
     * Sets text to show with the object. Ignored if object doesn't support text
     *
     * @param text text to show
     * @param font font to use for text rendering
     */
    void setText(String text, Font font);

    /**
     * Sets text to show with the object. Ignored if object doesn't support text
     *
     * @param text text to show
     * @param horizontalAlignment text horizontal position according to specified time.
     * Available options for horizontalAlignment = {SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.RIGHT}
     * Default value is SwingConstants.LEFT
     */
    void setText(String text, int horizontalAlignment);

    /**
     * Sets text to show with the object. Ignored if object doesn't support text
     *
     * @param text text to show
     * @param font font to use for text rendering
     * @param horizontalAlignment text horizontal position according to specified time.
     * Available options for horizontalAlignment = {SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.RIGHT}
     * Default value is SwingConstants.LEFT
     */
    void setText(String text, Font font, int horizontalAlignment);

    /**
     * Returns object text's maximum displayable character count. Returns -1 if it is unrestricted.
     * If the <code>getText()</code> length exceeds the count, the string on chart gets truncated according to this parameter value
     * and gets postfixed by "...".
     *
     * <p>For <code>ISignalDownChartObject</code>, <code>ISignalUpChartObject</code> and all <code>IHorizontalLineChartObject</code> implementations
     * the default is 30, for the rest it is -1. The parameter does not apply to <code>ITextChartObject</code>.
     *
     * @return object text's maximum displayable character count
     */
    int getTextTruncation();

    /**
     * Sets object text's maximum displayable character count. Passing -1 makes it unrestricted.
     * If the <code>getText()</code> length exceeds the count, the string on chart gets truncated according to this parameter value
     * and gets postfixed by "...".
     * The parameter does not apply to <code>ITextChartObject</code>.
     *
     * @param charCount maximum displayable character count
     */
    void setTextTruncation(int charCount);

    /**
     * Returns color of the object
     *
     * @return color
     */
    Color getColor();

    /**
     * Sets color of the object
     *
     * @param color color to use for object rendering
     */
    void setColor(Color color);

    /**
     * Returns opacity alpha of the object
     *
     * @return alpha 0-1 (0: transparent; 1: opaque)
     */
    float getOpacity();

    /**
     * Sets alpha of the object
     *
     * @param alpha values: 0-1 (0: transparent; 1: opaque)
     */
    void setOpacity(float alpha);

    /**
     * Returns stroke that is used for object rendering
     *
     * @return stroke
     */
    Stroke getStroke();

    /**
     * Sets stroke for object rendering
     *
     * @param stroke stroke to use for object rendering
     */
    void setStroke(Stroke stroke);

    /**
     * Gets stroke style
     *
     * @return line style
     * @see LineStyle
     */
    int getLineStyle();

    /**
     * Sets stroke style
     *
     * @param lineStyle one of LineStyle constant
     * @see LineStyle
     */
    void setLineStyle(int lineStyle);

    /**
     * Returns stroke width
     *
     * @return stroke width
     */
    float getLineWidth();

    /**
     * Sets stroke width
     *
     * @param width width of stroke line
     */
    void setLineWidth(float width);

    /**
     * Returns type of the graphical object
     *
     * @return type of the graphical object
     */
    Type getType();

    /**
     * @return unique key of this object
     */
    String getKey();

    /**
     * Returns instrument used for drawing chart object or <code>null</code> for using primary chart instrument.
     *
     * @return instrument for drawing chart object
     */
    Instrument getInstrument();

    /**
     * Sets instrument used for drawing chart object or <code>null</code> for using primary chart instrument.
     *
     * @param instrument instrument for drawing chart object
     */
    void setInstrument(Instrument instrument);

    /**
     * Returns financial instrument used for drawing chart object or <code>null</code> for using primary chart instrument.
     *
     * @return financial instrument for drawing chart object
     */
    @Deprecated
    IFinancialInstrument getFinancialInstrument();

    /**
     * Sets financial instrument used for drawing chart object or <code>null</code> for using primary chart instrument.
     *
     * @param financialInstrument instrument for drawing chart object
     */
    @Deprecated
    void setFinancialInstrument(IFinancialInstrument financialInstrument);

    /**
     * @param pointIndex which determines which coordinate should be used to obtain time
     * @return time of the coordinate
     */
    long getTime(int pointIndex);

    /**
     *
     * @param pointIndex which determines which coordinate should be used to obtain price
     * @return price of the coordinate
     */
    double getPrice(int pointIndex);

    /**
     * @return minimal count of points which are used to set this chart object
     */
    int getPointsCount();

    /**
     * Controls drawing stickiness. True by default
     *
     * @param sticky if true then drawing will adjust itself to closest high/low value for candle under cursor
     */
    void setSticky(boolean sticky);

    /**
     * If true then drawing will adjust itself to closest high/low value for candle under cursor
     *
     * @return true when drawing is sticky
     */
    boolean isSticky();

    /**
     * If false then the menu is never shown on chart for this drawing, if true - menu will be shown on right button click
     *
     * @return true when menu is enabled for the drawing
     */
	boolean isMenuEnabled();

	/**
	 *
	 * @param menuEnabled which determines show or not show the menu for the drawing
	 */
	void setMenuEnabled(boolean menuEnabled);

	/**
	 * If false then text value is never shown on chart for this drawing, if true - the label can be added/updated for this drawing object
	 * @return true when label is enabled for the drawing
	 */
	boolean isLabelEnabled();

    /**
     * Sets show label option.
     *
     * System default value: <b>true</b>
     * @param showLabel show label option
     */
    void setShowLabel(boolean showLabel);

    /**
     * Returns show label option.
     *
     * System default value: <b>true</b>
     * @return show label option
     */
    boolean isLabelShown();

	/**
	 * Sets popup tooltip which shows when mouse over the figure.
     * @param tooltip tooltip text
     * @deprecated use {@link #setText(String)} instead
	 */
    @Deprecated
	void setTooltip(String tooltip);

	/**
	 * Returns chart object whether is visible or hidden in Workspace Tree.
	 * Default value: <b>true</b>.
	 * Note: locked chart objects are not visible in workspace tree
     * @return true if chart object is visible in Workspace Tree or false - if it is hidden
	 * @see #isLocked()
	 * @see #setLocked(boolean)
	 */
	boolean isVisibleInWorkspaceTree();

	/**
	 * Make chart object visible or hidden in Workspace Tree.
	 * Default value: <b>true</b>.
	 * @param visible is visible
	 * @see #isLocked()
     * @see #setLocked(boolean)
	 */
	void setVisibleInWorkspaceTree(boolean visible);

	/**
	 * Returns chart object lock state.
	 * When chart object is locked it is not visible in workspace tree and is not editable on UI.
	 * Default value: <b>false</b>.
     * @return chart object lock state
	 */
	boolean isLocked();

	/**
	 * Sets chart object lock state.
     * When chart object is locked it is not visible in workspace tree and is not editable on UI.
     * Default value: <b>false</b>.
     * @param locked chart object lock state
	 */
	void setLocked(boolean locked);

	/**
	 * Clones the chart object with a random key
	 *
	 * @return a cloned chart object
	 */
	IChartObject clone();
}
