/*
 * Copyright 2017 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Listener to chart instruments changes.
 */
@Deprecated
public interface IChartFinancialInstrumentsListener {
    /**
     * Called when chart instruments are changed.
     *
     * @param chartInstruments array with new chart instruments
     */
    void onInstrumentsChanged(IFinancialInstrument[] chartInstruments);
}
