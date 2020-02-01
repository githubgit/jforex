/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

public interface IFXTimedSentimentIndex  {
    
    /**
     * Returns a settlement time of current index. Sentiment index is updated by server every 30 minutes.
     * @return Returns a settlement time of current index.
     */
    long getIndexTime();
}
