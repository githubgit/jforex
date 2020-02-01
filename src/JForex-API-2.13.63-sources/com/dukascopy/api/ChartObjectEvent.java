/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.EventObject;
import java.awt.Color;

/**
 * Event used to pass parameters of chart object change
 *
 * @author Dmitry Shohov
 */
public class ChartObjectEvent extends EventObject {
    /**
     * Type of the attribute change event
     */
    public enum AttrType {
        /**
         * Attribute of type long
         * @see com.dukascopy.api.IChartObject.ATTR_LONG
         */
        TYPE_LONG,

        /**
         * Attribute of type double
         * @see com.dukascopy.api.IChartObject.ATTR_DOUBLE
         */
        TYPE_DOUBLE,

        /**
         * Attribute of type int
         * @see com.dukascopy.api.IChartObject.ATTR_INT
         */
        TYPE_INT,

        /**
         * Attribute of type color
         * @see com.dukascopy.api.IChartObject.ATTR_COLOR
         */
        TYPE_COLOR,

        /**
         * Attribute of type boolean
         * @see com.dukascopy.api.IChartObject.ATTR_BOOLEAN
         */
        TYPE_BOOLEAN
    }

    private AttrType type;

    private IChartObject.ATTR_LONG longAttrType;
    private long oldLong;
    private long newLong;

    private IChartObject.ATTR_DOUBLE doubleAttrType;
    private double oldDouble;
    private double newDouble;

    private IChartObject.ATTR_INT intAttrType;
    private int oldInt;
    private int newInt;

    private IChartObject.ATTR_COLOR colorAttrType;
    private Color oldColor;
    private Color newColor;

    private IChartObject.ATTR_BOOLEAN booleanAttrType;
    private boolean oldBoolean;
    private boolean newBoolean;

    private boolean isAdjusting;

    private boolean canceled;

    /**
     * Constructor for highlighted, selected and deleted events
     *
     * @param source related IChartObject
     */
    public ChartObjectEvent(Object source) {
        super(source);
    }

    /**
     * Constructor for moved event
     *
     * @param source related IChartObject
     * @param oldLong old long
     * @param newLong new long
     * @param oldDouble old double
     * @param newDouble new double
     * @param isAdjusting true if event is not about final action
     */
    public ChartObjectEvent(Object source, long oldLong, long newLong, double oldDouble, double newDouble, boolean isAdjusting) {
        super(source);
        this.oldLong = oldLong;
        this.newLong = newLong;
        this.oldDouble = oldDouble;
        this.newDouble = newDouble;
        this.isAdjusting = isAdjusting;
    }

    /**
     * Constructor for attribute change event
     *
     * @param source related IChartObject
     * @param longAttrType type of the attribute
     * @param oldLong old value
     * @param newLong new value
     * @param isAdjusting true if event is not about final action
     */
    public ChartObjectEvent(Object source, IChartObject.ATTR_LONG longAttrType, long oldLong, long newLong, boolean isAdjusting) {
        super(source);
        this.type = AttrType.TYPE_LONG;
        this.longAttrType = longAttrType;
        this.oldLong = oldLong;
        this.newLong = newLong;
        this.isAdjusting = isAdjusting;
    }

    /**
     * Constructor for attribute change event
     *
     * @param source related IChartObject
     * @param doubleAttrType type of the attribute
     * @param oldDouble old value
     * @param newDouble new value
     * @param isAdjusting true if event is not about final action
     */
    public ChartObjectEvent(Object source, IChartObject.ATTR_DOUBLE doubleAttrType, double oldDouble, double newDouble, boolean isAdjusting) {
        super(source);
        this.type = AttrType.TYPE_DOUBLE;
        this.doubleAttrType = doubleAttrType;
        this.oldDouble = oldDouble;
        this.newDouble = newDouble;
        this.isAdjusting = isAdjusting;
    }

    /**
     * Constructor for attribute change event
     *
     * @param source related IChartObject
     * @param intAttrType type of the attribute
     * @param oldInt old value
     * @param newInt new value
     * @param isAdjusting true if event is not about final action
     */
    public ChartObjectEvent(Object source, IChartObject.ATTR_INT intAttrType, int oldInt, int newInt, boolean isAdjusting) {
        super(source);
        this.type = AttrType.TYPE_INT;
        this.intAttrType = intAttrType;
        this.oldInt = oldInt;
        this.newInt = newInt;
        this.isAdjusting = isAdjusting;
    }

    /**
     * Constructor for attribute change event
     *
     * @param source related IChartObject
     * @param colorAttrType type of the attribute
     * @param oldColor old value
     * @param newColor new value
     * @param isAdjusting true if event is not about final action
     */
    public ChartObjectEvent(Object source, IChartObject.ATTR_COLOR colorAttrType, Color oldColor, Color newColor, boolean isAdjusting) {
        super(source);
        this.type = AttrType.TYPE_COLOR;
        this.colorAttrType = colorAttrType;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.isAdjusting = isAdjusting;
    }

    /**
     * Constructor for attribute change event
     *
     * @param source related IChartObject
     * @param booleanAttrType type of the attribute
     * @param oldBoolean old value
     * @param newBoolean new value
     * @param isAdjusting true if event is not about final action
     */
    public ChartObjectEvent(Object source, IChartObject.ATTR_BOOLEAN booleanAttrType, boolean oldBoolean, boolean newBoolean, boolean isAdjusting) {
        super(source);
        this.type = AttrType.TYPE_BOOLEAN;
        this.booleanAttrType = booleanAttrType;
        this.oldBoolean = oldBoolean;
        this.newBoolean = newBoolean;
        this.isAdjusting = isAdjusting;
    }

    /**
     * Returns true if event was canceled. New events always arrive with this parameter set to false
     *
     * @return true if event was canceled
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Allows to cancel changes made by this event. Not all events can be canceled
     */
    public void cancel() {
        canceled = true;
    }

    /**
     * Returns type of the attribute changed event
     *
     * @return type
     */
    public AttrType getType() {
        return type;
    }

    /**
     * Returns true if current event is for adjusting action. For example there will be many events with this attribute
     * set to true while user moves the drawing and only one final event with isAdjusting == false when user releases
     * drawing at the end. Changes in attribute or cancel methods are ignored when isAdjusting is true
     *
     * @return true if event is not about final position
     */
    public boolean isAdjusting() {
        return isAdjusting;
    }

    /**
     * Returns name of the attribute
     *
     * @return name of the attribute
     */
    public IChartObject.ATTR_LONG getLongAttrType() {
        return longAttrType;
    }

    /**
     * Returns old value
     *
     * @return old value
     */
    public long getOldLong() {
        return oldLong;
    }

    /**
     * Returns new value
     *
     * @return new value
     */
    public long getNewLong() {
        return newLong;
    }

    /**
     * Sets new attribute value
     *
     * @param newLong new attribute value
     */
    public void setNewLong(long newLong) {
        this.newLong = newLong;
    }

    /**
     * Returns name of the attribute
     *
     * @return name of the attribute
     */
    public IChartObject.ATTR_DOUBLE getDoubleAttrType() {
        return doubleAttrType;
    }

    /**
     * Returns old value
     *
     * @return old value
     */
    public double getOldDouble() {
        return oldDouble;
    }

    /**
     * Returns new value
     *
     * @return new value
     */
    public double getNewDouble() {
        return newDouble;
    }

    /**
     * Sets new attribute value
     *
     * @param newDouble new attribute value
     */
    public void setNewDouble(double newDouble) {
        this.newDouble = newDouble;
    }

    /**
     * Returns name of the attribute
     *
     * @return name of the attribute
     */
    public IChartObject.ATTR_INT getIntAttrType() {
        return intAttrType;
    }

    /**
     * Returns old value
     *
     * @return old value
     */
    public int getOldInt() {
        return oldInt;
    }

    /**
     * Returns new value
     *
     * @return new value
     */
    public int getNewInt() {
        return newInt;
    }

    /**
     * Sets new attribute value
     *
     * @param newInt new attribute value
     */
    public void setNewInt(int newInt) {
        this.newInt = newInt;
    }

    /**
     * Returns name of the attribute
     *
     * @return name of the attribute
     */
    public IChartObject.ATTR_COLOR getColorAttrType() {
        return colorAttrType;
    }

    /**
     * Returns old value
     *
     * @return old value
     */
    public Color getOldColor() {
        return oldColor;
    }

    /**
     * Returns new value
     *
     * @return new value
     */
    public Color getNewColor() {
        return newColor;
    }

    /**
     * Sets new attribute value
     *
     * @param newColor new attribute value
     */
    public void setNewColor(Color newColor) {
        this.newColor = newColor;
    }

    /**
     * Returns name of the attribute
     *
     * @return name of the attribute
     */
    public IChartObject.ATTR_BOOLEAN getBooleanAttrType() {
        return booleanAttrType;
    }

    /**
     * Returns old value
     *
     * @return old value
     */
    public boolean getOldBoolean() {
        return oldBoolean;
    }

    /**
     * Returns new value
     *
     * @return new value
     */
    public boolean getNewBoolean() {
        return newBoolean;
    }

    /**
     * Sets new attribute value
     *
     * @param newBoolean new attribute value
     */
    public void setNewBoolean(boolean newBoolean) {
        this.newBoolean = newBoolean;
    }
}
