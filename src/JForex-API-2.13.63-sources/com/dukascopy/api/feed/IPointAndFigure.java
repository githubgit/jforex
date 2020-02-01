/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

/**
 * @author Mark Vilkel
 */
public interface IPointAndFigure extends IPriceAggregationBar {

	/**
	 * Returns whether bar is rising or falling.
	 * 
	 * @return {@code true} if bar is rising or X, {@code false} if bar is falling or O
	 */
	Boolean isRising();

}
