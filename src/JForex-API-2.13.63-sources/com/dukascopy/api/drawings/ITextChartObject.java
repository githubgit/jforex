/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;

public interface ITextChartObject extends IChartDependentChartObject {
	
	/**
	 * Returns current text color.
	 * @return current color
	 */
	Color getFontColor();
	
	/**
	 * Sets text color.
	 * @param color new color
	 */
	void setFontColor(Color color);
	
	/**
	 * Returns text rotation angle in radians (0.0 by default).
	 * @return current angle
	 */
	double getTextAngle();
	
	/**
	 * Sets text rotation angle in radians.
	 * @param angle new angle
	 */
	void setTextAngle(double angle);
	
}
