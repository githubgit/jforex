/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Represents client's stop loss level changing message
 * @author aburenin
 */
public interface IStopLossLevelChangedMessage extends IMessage {
    
    /**
     * @return client id
     */
    String getClientId();

    /**
     * @return previous value of Stop Loss Level in the client base currency
     */
    double previousStopLossLevel();
    
    /**
     * @return new (current) value of Stop Loss Level in the client base currency
     */
    double stopLossLevel();
}
