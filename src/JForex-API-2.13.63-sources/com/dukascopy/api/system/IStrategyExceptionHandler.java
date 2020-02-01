/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

/**
 * Exception handler that is called when one of the strategy methods throws exception
 *
 * @author Dmitry Shohov
 */
public interface IStrategyExceptionHandler {
    enum Source {
        ON_START("onStart"),
        ON_STOP("onStop"),
        ON_TICK("onTick"),
        ON_BAR("onBar"),
        ON_MESSAGE("onMessage"),
        ON_ACCOUNT_INFO("onAccount");

        private String methodName;
        private Source(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public String toString() {
            return methodName;
        }
    }

    /**
     * Called when one of the strategy methods throws exception
     *
     * @param strategyId id of the strategy
     * @param source method that threw the exception
     * @param t exception
     */
    void onException(long strategyId, Source source, Throwable t);
}
