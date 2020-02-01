/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Forex Sentiment Index.<br>
 * Shows the percentage of longs in the overall amount of open trades in the most popular currencies and currency pairs consolidated by liquidity consumers.<br>
 * The sentiment ratio of Liquidity providers is opposite to Liquidity consumers (opposite to {@link #getIndexValue()}.<br>
 * The index reflects the distribution of the current market conditions and is updated every 30 minutes.<br>
 * @see <a href="http://www.dukascopy.com/swiss/english/marketwatch/sentiment/">SWFX Sentiment Index</a> 
 * @author aburenin
 * 
 */
public interface IFXSentimentIndex extends IFXTimedSentimentIndex  {
    
    /**
     * Returns a settlement time of current index. Sentiment index is updated by server every 30 minutes.
     * @return Returns a settlement time of current index.
     */
    long getIndexTime();
    
    /**
     * Index's value ranges from 0 to 100 percents.
     * <ul>
     * <li>If the number of traders selling a currency increases compared to those buying the currency (or instrument), the  index approaches 0.
     * <li>If there are more traders buying a currency than selling it, the  index approaches 100.
     * <li>Overbought/oversold zones are defined at the 70/30 level.
     * <li>If the  index reaches 30 or below, the currency (or instrument) has entered the oversold area and a reverse upward trend is very likely.
     * <li>Accordingly, the opposite response is likely if the  index reaches 70 or above (the overbought zone).
     * <li>If the  index is in the zone between 30 and 70, fluctuations are considered neutral.
     * </ul>
     * In other words, the index value is percentage of <b>LONG</b> positions in the overall amount of open trades.<br>
     * To calculate the percentage of <b>SHORT</b> positions in overall amount of trades use <code>(100 - getIndexValue())</code>
     * @return the percentage of <b>LONG</b> positions in the overall amount of open trades.
     */
    double getIndexValue();
    
    /**
     * Returns the current index trend (percentage of <b>LONG</b> positions minus percentage of <b>SHORT</b> positions in overall amount of trades)
     * @return Returns the current index trend
     */
    double getIndexTendency();
}
