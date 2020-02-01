/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.ITailoredFeedDescriptor;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor} used for CandleSticks
 */
public class TimePeriodAggregationFeedDescriptor extends FeedDescriptor implements ITailoredFeedDescriptor<IBar> {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument instrument
	 * @param period period
	 * @param offerSide offer side
	 * @param filter filter
	 */
	public TimePeriodAggregationFeedDescriptor(
			Instrument instrument,
			Period period,
			OfferSide offerSide,
			Filter filter
	) {
		setDataType(DataType.TIME_PERIOD_AGGREGATION);
		setInstrument(instrument);
		setPeriod(period);
		setOfferSide(offerSide);
		setFilter(filter);
	}

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument instrument
	 * @param period period
	 * @param offerSide offer side
	 */
	public TimePeriodAggregationFeedDescriptor(
			Instrument instrument,
			Period period,
			OfferSide offerSide
	) {
		this(
				instrument,
				period,
				offerSide,
				Filter.NO_FILTER
		);
	}
	
}
