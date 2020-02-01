/*
 * Copyright 2011 Dukascopy® Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * @author Dmitry Shohov
 */
public interface IStrategyListener {
    /**
     * Called on new strategy start
     *
     * @param strategyId id of the started strategy
     */
    void onStart(long strategyId);

    /**
     * Called on the strategy stop
     *
     * @param strategyId id of the strategy stopped
     */
    void onStop(long strategyId);
}
