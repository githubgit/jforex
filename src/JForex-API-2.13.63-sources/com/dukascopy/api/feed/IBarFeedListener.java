/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;

/**
 * @author Mark Vilkel
 */
public interface IBarFeedListener {
	
	/**
	 * The method is being called when next Candle arrives
	 * 
	 * @param instrument instrument
	 * @param period period
	 * @param offerSide offer side
	 * @param bar bar
	 */
	public void onBar(
			Instrument instrument,
			Period period,
			OfferSide offerSide,
			IBar bar
	);
	
}
