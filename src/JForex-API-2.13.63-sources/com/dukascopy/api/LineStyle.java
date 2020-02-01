/*
 * Copyright 2010 Dukascopy® Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Set of Line Style Patterns
 */
public interface LineStyle {
	/**
	 * Solid pen
	 */
	public static final int SOLID				= 0;
	/**
	 * Dashed pen
	 */
	public static final int DASH				= 1;
	/**
	 * Doted pen
	 */
	public static final int DOT					= 2;
	/**
	 * Sequence of dash and dots
	 */
	public static final int DASH_DOT			= 3;
	/**
	 * Sequence of dash and double dots
	 */
	public static final int DASH_DOT_DOT		= 4;
	/**
	 * Fine dashed pen
	 */
	public static final int FINE_DASHED 		= 5;
	/**
	 * Sequence of long and short dashes
	 */
	public static final int LONG_AND_SHORT_DASH = 6;
}
