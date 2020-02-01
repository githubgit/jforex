/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Interface for object that describes possible values of the optional input. Don't implement it in your classes,
 * use {@link IntegerListDescription}, {@link IntegerRangeDescription}, {@link DoubleListDescription} or {@link DoubleRangeDescription} instead
 * 
 * @author Dmitry Shohov
 * @see IntegerListDescription
 * @see IntegerRangeDescription
 * @see DoubleListDescription
 * @see DoubleRangeDescription
 */
public interface OptInputDescription {

    /**
	 * Returns default value for optional input
	 * 
	 * @return default value for optional input
	 */
	Object getOptInputDefaultValue();
}
