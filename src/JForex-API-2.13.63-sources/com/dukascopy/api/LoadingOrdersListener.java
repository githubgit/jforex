/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * @author Dmitry Shohov
 */
public interface LoadingOrdersListener {
    void newOrder(Instrument instrument, IOrder orderData);
}
