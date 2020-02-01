/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.PriceRange;

/**
 * @deprecated use {@link com.dukascopy.api.feed.ITailoredFeedListener}
 * @author Mark Vilkel
 */
@Deprecated
public interface IRangeBarFeedListener {
	
	/**
	 * The method is being called when next Range Bar arrives
	 * 
	 * @param instrument instrument
	 * @param offerSide offer side
	 * @param priceRange price range
	 * @param bar bar
	 */
	void onBar(
			Instrument instrument,
			OfferSide offerSide,
			PriceRange priceRange,
			IRangeBar bar
	);
}
