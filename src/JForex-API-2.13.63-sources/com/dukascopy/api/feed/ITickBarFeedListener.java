/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.TickBarSize;

/**
 * @deprecated use {@link com.dukascopy.api.feed.ITailoredFeedListener}
 * @author Mark Vilkel
 */
@Deprecated
public interface ITickBarFeedListener {
	
	/**
	 * The method is being called when next Tick Bar arrives
	 * 
	 * @param instrument instrument
	 * @param offerSide offer side
	 * @param tickBarSize tick bar size
	 * @param bar bar
	 */
	void onBar(
			Instrument instrument,
			OfferSide offerSide,
			TickBarSize tickBarSize,
			ITickBar bar
	);
}
