/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.awt.Color;
import java.io.Serializable;

import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * Contains information about Indicator's levels objects (JavaBean).<br>
 * Level value indicates either: 
 * <ul>
 * <li>the value on the vertical (price-axis) for sub-window's type indicators or
 * <li>price shift calculated by way of summing up the average indicator values and the specified level shift
 * (specified in current instrument pip-value) for on-chart types of indicators
 * </ul>
 * @author aburenin
 */
public class LevelInfo implements Comparable<LevelInfo>, Serializable, Cloneable {
    
    private static final long serialVersionUID = 364569280248232454L;
    private String label;
    private double value;
    private DrawingStyle drawingStyle = DrawingStyle.DASH_LINE;
    private Color color = Color.GRAY;
    private float opacityAlpha = 1f;
    private int lineWidth = 1;
    
    public LevelInfo() {
    }
    
    public LevelInfo(double value) {
        this.value = value;
    }
    
    /**
     * @param label level label 
     * @param value level value (indicates the value on the vertical (price-axis) for sub-window's type indicators or
     * price-axis shift calculated by way of summing up the average indicator values and the specified level
     * (specified in current instrument pip-value) for on-chart types of indicators
     * @param drawingStyle drawing style
     * @param color color
     * @param width width
     * @param alpha alpha
     */
    public LevelInfo(String label, double value, DrawingStyle drawingStyle, Color color, int width, float alpha) {
        super();
        this.label = label;
        this.value = value;
        this.drawingStyle = drawingStyle;
        this.color = color;
        this.lineWidth = width;
        this.opacityAlpha = alpha;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the drawingStyle
     */
    public DrawingStyle getDrawingStyle() {
        return drawingStyle;
    }

    /**
     * @param drawingStyle the drawingStyle to set
     */
    public void setDrawingStyle(DrawingStyle drawingStyle) {
        this.drawingStyle = drawingStyle;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        if (color == null){
            return Color.GRAY;
        }
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * @return the opacityAlpha
     */
    public float getOpacityAlpha() {
        return (opacityAlpha < 0.0f ? 0.0f : opacityAlpha > 1.0f ? 1.0f : opacityAlpha);
    }

    /**
     * @param opacityAlpha the opacityAlpha to set
     */
    public void setOpacityAlpha(float opacityAlpha) {
        this.opacityAlpha = opacityAlpha;
    }
    
    /**
     * @return the lineWidth
     */
    public int getLineWidth() {
        return lineWidth;
    }

    /**
     * @param lineWidth the lineWidth to set
     */
    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    @Override
    public int compareTo(LevelInfo other) {
    	if (other == null){
    		return 1;
    	}
    	return Double.compare(getValue(), other.getValue());
    }

    @Override
    public LevelInfo clone() {
        try {
            return (LevelInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
