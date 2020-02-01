/*
 * Copyright 2014 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.LineBreakLookback;

/**
 * @author Janis Garsils
 */
public interface ILineBreak extends IPriceAggregationBar {

	/**
	 * The Wick or Tail gives additional information of the market movement during the drawing of any individual line.
	 * Line Break charts use session length (time aggregated data, e.g. Period.ONE_MIN). After the current 
	 * session (e.g. Period.ONE_MIN) is finished, we take one price from that session (which price depends on {@link CreationPoint})
	 * and create (if possible) the next Line Break line. If the price of the current session didn't create a new line, then
	 * it makes a wick for next line. Bearish lines can have wicks only on top of them and bullish lines only on bottom.  
	 *   
	 * @return wick price
	 */
	Double getWickPrice();
	
	/**
	 * According to change a trend for a next line, the current price must exceed the turnaround price. Turnaround price depends 
	 * on the chosen {@link LineBreakLookback}} value. E.g. if we use {@link LineBreakLookback#THREE_LINES}, then the turnaround
	 * price is determined according to the last three lines. 
	 * 
	 * @return turnaround price
	 */
	Double getTurnaroundPrice();

	/**
	 * Provides an information about the trend of the line. 
	 * 
	 * @return true (bullish) or false (bearish)
	 */
	
	Boolean isRising();
	
	/**
     * 
     * Returns the number of time sessions which formed the current line
     * 
     * @return elements count
     */
	@Override
    long getFormedElementsCount();

}
