/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Listener to receive ticks and candles
 * 
 * @author Dmitry Shohov
 */
public interface LoadingDataListener {
    /**
     * Called to pass tick data
     * 
     * @param instrument instrument of the tick
     * @param time time of the tick
     * @param ask best ask price
     * @param bid best bid price
     * @param askVol volume for the best ask price
     * @param bidVol volume for the best bid price
     */
    void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol);
    
    /**
     * Called to pass bar data
     * 
     * @param instrument instrument of the bar
     * @param period period of the bar
     * @param side side of the bar (bid or ask)
     * @param time time of the bar
     * @param open open price
     * @param close close price
     * @param low lowest price
     * @param high highest price
     * @param vol volume of the bar (sum of volumes for best prices of all ticks)
     */
    void newBar(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol);
}
