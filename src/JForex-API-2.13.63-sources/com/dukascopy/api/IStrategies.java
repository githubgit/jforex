/*
 * Copyright 2011 Dukascopy® Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.File;
import java.util.Map;

/**
 * @deprecated please use {@link com.dukascopy.api.system.IClient#startStrategy(IStrategy)} instead.
 */
@Deprecated
public interface IStrategies {

	/**
     * Start strategy.
     *
     * @param jfxFile strategy binary file
     * @param listener strategy listener
     * @param configurables parameters values
     * @param fullAccess require full access flag
     * @return strategy id
     * @throws JFException if exception has been thrown on starting of strategy
	 * @deprecated please use {@link com.dukascopy.api.system.IClient#startStrategy(IStrategy)} instead.
	 */
    @Deprecated
    long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException;

	/**
     * Start strategy.
     *
     * @param strategy strategy
     * @param listener strategy listener
     * @param fullAccess require full access flag
     * @return strategy id
     * @throws JFException if exception has been thrown on starting of strategy
	 * @deprecated please use {@link com.dukascopy.api.system.IClient#startStrategy(IStrategy)} instead.
	 */
    @Deprecated
    long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess) throws JFException;

	/**
     * Stop strategy.
     *
     * @param strategyId strategy id
     * @throws JFException if exception has been thrown on stopping of strategy
	 * @deprecated please use {@link com.dukascopy.api.system.IClient#stopStrategy(long)} instead.
	 */
    @Deprecated
    void stopStrategy(long strategyId) throws JFException;
    
    /**
     * Stop all nested strategies started by either {@link #startStrategy(IStrategy, IStrategyListener, boolean)}
     * or {@link #startStrategy(File, IStrategyListener, Map, boolean)}.<br>
     * <b>NOTE:</b> The strategy that uses this method has not been stopped and must have <b>full access</b> rights granted.
     *
     * @throws JFException if main strategy has no <b>full access</b>, if exception has been thrown on stopping of any executed strategy.
	 * @deprecated please use {@link com.dukascopy.api.system.IClient#stopStrategy(long)} instead.
     */
    @Deprecated
    void stopAll() throws JFException;
}
