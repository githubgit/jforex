/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Represents a bar (candle).
 * 
 * @author Denis Larka, Dmitry Shohov
 */
public interface IBar extends ITimedData {

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

    /**
     * Returns volume of the bar. This is the sum of volumes of best prices for each tick in this bar
     * 
     * @return volume of the bar
     */
    double getVolume();
}
