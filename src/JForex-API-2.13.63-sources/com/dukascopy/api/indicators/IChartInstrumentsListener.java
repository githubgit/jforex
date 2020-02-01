/*
 * Copyright 2015 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import com.dukascopy.api.Instrument;

/**
 * Listener to chart instruments changes.
 */
public interface IChartInstrumentsListener {
    /**
     * Called when chart instruments are changed.
     *
     * @param chartInstruments array with new chart instruments
     */
    void onInstrumentsChanged(Instrument[] chartInstruments);
}
