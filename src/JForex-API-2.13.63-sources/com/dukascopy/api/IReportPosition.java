/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Provides information about report position
 * @author andrej.burenin
 */
public interface IReportPosition {
	
	/**
	 * Indicates type of Position 
	 */
	enum PositionType {
		/** 
		 * usual position 
		 */
		REGULAR,
		
		/** 
		 * result of merge positions 
		 */
		MERGED
	}
	
	/**
	 * @return {@link PositionType}
	 */
	PositionType getPositionType();
	
	/**
	 * @return position's id
	 */
	String getPositionId();
	
	/**
	 * @return <code>true</code> if position is LONG, <code>false</code> otherwise
	 */
	boolean isLong();
	
	/**
	 * @return position's {@link Instrument}
	 */
	Instrument getInstrument();
	
	/**
     * Returns amount of the position. For open positions (<code>isClosed</code> == false) returns amount requested.
     * For filled positions will return filled amount (full or partial)
     * 
     * @return amount of the position
     */
	double getAmount();
	
	/**
     * Returns price at which position was filled
     *  
     * @return open price for positions
     */
	double getOpenPrice();
	
	/**
	 * @return current volatile price for position's instrument. 
	 */
	double getCurrentPrice();
	
	/**
     * Returns price at which position was closed or 0 if order or position part wasn't closed.
     * 
     * @return close price for closed orders, return 0 for any other type of order
     */
	double getClosePrice();

	/**
	 * @return Profit/Loss expressed in position's instrument second currency ({@link Instrument#getSecondaryJFCurrency()})
	 */
	Money getProfitLoss();
	
	/**
	 * @return Swap points
	 */
	Money getSwaps();
	
	/**
	 * Returns Gross Profit/Loss ({@link #getProfitLoss() + #getSwaps()} expressed in
	 * position's instrument second currency ({@link Instrument#getSecondaryJFCurrency()})
	 * 
	 * @return Gross Profit/Loss
	 */
	Money getGrossProfitLoss();
	
	/**
	 * Returns commission expressed in <b>account's currency</b>
	 * @return Position's commission
	 */
	Money getCommission();
	
	/**
     * Returns last time when server filled the position
     * 
     * @return last time when server filled the position
     */
	long getOpenTime();
	
	/**
     * Returns time when server closed the position
     * 
     * @return time when position was closed
     */
	long getCloseTime();
	
	/**
	 * @return <code>true</code> when ({@link #getClosePrice()} &gt; 0 and {@link #getCloseTime()} &gt; 0), <code>false</code> otherwise
	 */
	boolean isClosed();
}
