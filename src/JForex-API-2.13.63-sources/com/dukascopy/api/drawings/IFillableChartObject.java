/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;

public interface IFillableChartObject extends IChartDependentChartObject {
	
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_BACKGROUND_COLOR = "fill.color";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_BACKGROUND_OPACITY = "fill.alpha";

	/**
	 * Returns fill color of this object.
	 * 
	 * @return object's fill color.
	 */
	Color getFillColor();

	/**
	 * Sets fill color of this object.
	 * 
	 * @param fillColor - object's fill color.
	 */
	void setFillColor(Color fillColor);

	/**
	 * Returns fill opacity of this object.
	 * 
	 * @return object's fill opacity.
	 */
	float getFillOpacity();

	/**
	 * Sets fill opacity of this object.
	 * 
	 * @param fillAlpha - object's fill opacity.
	 */
	void setFillOpacity(float fillAlpha);
}
