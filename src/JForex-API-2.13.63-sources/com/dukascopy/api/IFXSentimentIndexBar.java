/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Forex Sentiment Index.<br>
 * Aggregation over Forex Sentiment Indices' long values
 * which shows the percentage of longs in the overall amount of open trades in the most popular currencies and currency pairs consolidated by liquidity consumers.<br>
 * The index reflects the distribution of the current market conditions and is updated every 30 minutes.<br>
 * @see <a href="http://www.dukascopy.com/swiss/english/marketwatch/sentiment/">SWFX Sentiment Index</a> 
 * 
 */
public interface IFXSentimentIndexBar extends IFXTimedSentimentIndex  {
    
	  /**
     * The time of the oldest index in the aggregation. If there are no indices in the database over the given timespan
     * then the value is less than {@link #getTime()}
     * @return Returns a settlement time of current index.
     */
    long getIndexTime();
	
    /**
     * Returns the start time of the sentiment index aggregation bar. If the bar is aggregation over 0 indices 
     * (i.e. there were no indices over the bar's timespan in the database), then it is larger than {@link #getIndexTime()}
     * 
     * @return the start time of the sentiment index aggregation bar.
     */
    long getTime();
    
    /**
     * Returns opening price of the bar
     * 
     * @return opening price
     */
    double getOpen();

    /**
     * Returns closing price of the bar
     * 
     * @return closing price
     */
    double getClose();

    /**
     * Returns the lowest price of the bar
     * 
     * @return the lowest price
     */
    double getLow();

    /**
     * Returns the highest price of the bar
     * 
     * @return the highest price
     */
    double getHigh();
}
