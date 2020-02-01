/*
 * Copyright 2015 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Listener to indicator deinitialization.
 * Indicator must register listener by {@link IIndicatorsProvider#addIndicatorStopListener(IIndicator, IStopListener)} method.<br>
 * There is no specific moment when it is known that some indicator instance is not used anymore,
 * so onStop() method will be called when this instance became unreachable through strong reference (or on application shutdown).<br>
 * Therefore, it is preferable to not implement IStopListener interface by indicator directly, but use for it static inner class.
 */
public interface IStopListener {
    /**
     * Called on indicator deinitialization.
     */
    void onStop();
}
