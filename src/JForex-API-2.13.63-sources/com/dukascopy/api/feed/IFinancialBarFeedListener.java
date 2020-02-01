/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.IBar;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public interface IFinancialBarFeedListener {
	
	/**
	 * The method is being called when next Candle arrives
	 * 
	 * @param financialInstrument instrument
	 * @param period period
	 * @param offerSide offer side
	 * @param bar bar
	 */
	void onBar(
			IFinancialInstrument financialInstrument,
			Period period,
			OfferSide offerSide,
			IBar bar
	);
	
}
