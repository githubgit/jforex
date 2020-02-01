/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

/**
 * Class for Renko bar representation 
 * 
 * @author Mark Vilkel
 */
public interface IRenkoBar extends IPriceAggregationBar {

	/**	
	 * This method always returns null. In theory, renko bars doesn't have any in-progress bars.
	 * 
	 * @deprecated
	 * @return null
	 */
	@Deprecated
	IRenkoBar getInProgressBar();
	
	/**
	 * The Wick or Tail gives additional information of the market movement during the drawing of any individual bar.
	 * Example using a 5-pip Renko Bar and where market moves from 10 to 25:
	 * Assuming that price movement was not simple one-way, but market moved from {@literal 10 -> 17 -> 13 -> 20 -> 17 -> 25},
	 * the same RB with wicks will show the following 3 bars:
	 * 
	 * {@literal 10 -> 15} no wick (null),
	 * {@literal 15 -> 20} with wick down to 13,
	 * {@literal 20 -> 25} with wick down to 17.
	 * 
	 * In case of rising Renko Bars: wicks can only be underneath of the bar-body.
	 * In case of falling Renko Bars: wicks can only be above of the bar-body.
	 * 
	 * @return wick price
	 */
	Double getWickPrice();

}
