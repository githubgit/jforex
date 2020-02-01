/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.feed.DataInterpolationDescriptor;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.IRangeBar;
import com.dukascopy.api.feed.ITailoredFeedDescriptor;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor}
 */
public class RangeBarFeedDescriptor extends FeedDescriptor implements ITailoredFeedDescriptor<IRangeBar> {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument instrument
	 * @param priceRange price range
	 * @param offerSide offer side
	 */
	public RangeBarFeedDescriptor(
			Instrument instrument,
			PriceRange priceRange,
			OfferSide offerSide
	) {
		this(instrument, priceRange, offerSide, null);
	}
	
	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation.
	 * The period represents the base period.
	 * 
	 * @param instrument instrument
	 * @param priceRange price range
	 * @param offerSide offer side
	 * @param basePeriod the period, in which the range bars are calculated. If base period is given {@link Period#INFINITY}, then range bars are calculated from the beginning of the history.
	 * Everything else at data loading time is considered as a default value (one week), including null.
	 */
	public RangeBarFeedDescriptor(
			Instrument instrument,
			PriceRange priceRange,
			OfferSide offerSide,
			Period basePeriod
	) {
		this(instrument, priceRange, offerSide, basePeriod, null);
	}

	/**
	 * Constructor, that sets all required fields.
	 * 
	 * @param instrument instrument
	 * @param priceRange price range
	 * @param offerSide offer side
	 * @param basePeriod the period, in which the range bars are calculated. If base period is given {@link Period#INFINITY}, then range bars are calculated from the beginning of the history.
	 * Everything else at data loading time is considered as a default value (one week), including null. 
	 * @param interpolationDescriptor - tick interpolation descriptor. If null, the DataInterpolationDescriptor.DEFAULT interpolation is used. 
	 * To get most suitable interpolation descriptor, use {@link DataInterpolationDescriptor#getSuitableDataInterpolationDescriptor(PriceRange priceRange)} method. 
	 * To find more about tick interpolation from candles and it's purpose, see {@link DataInterpolationDescriptor}. 
	 */
	public RangeBarFeedDescriptor(
			Instrument instrument,
			PriceRange priceRange,
			OfferSide offerSide,
			Period basePeriod,
			DataInterpolationDescriptor interpolationDescriptor
	) {
		setDataType(DataType.PRICE_RANGE_AGGREGATION);
		setInstrument(instrument);
		setPriceRange(priceRange);
		setOfferSide(offerSide);
		super.setPeriod(validateBasePeriod(basePeriod));
		setDataInterpolationDescriptor(interpolationDescriptor);
	}
	
	@Override
	public void setPeriod(Period period) {
		super.setPeriod(validateBasePeriod(period));
	}
}
