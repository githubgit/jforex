/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.api.feed.DataInterpolationDescriptor;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.ITailoredFeedDescriptor;
import com.dukascopy.api.feed.ITickBar;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor}
 */
public class TickBarFeedDescriptor extends FeedDescriptor implements ITailoredFeedDescriptor<ITickBar> {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation. 
	 * Base period is set to default (one week).
	 * 
	 * @param instrument instrument
	 * @param tickBarSize tick bar size
	 * @param offerSide offer side
	 */
	public TickBarFeedDescriptor(
			Instrument instrument,
			TickBarSize tickBarSize,
			OfferSide offerSide
	) {
		this(instrument, tickBarSize, offerSide, null);
	}
	
	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation. 
	 * The period represents the base period.
	 * 
	 * @param instrument instrument
	 * @param tickBarSize tick bar size
	 * @param offerSide offer side
	 * @param basePeriod the period, in which the tick bars are calculated. If base period is given {@link Period#INFINITY}, then tick bars are calculated from the beginning of the history. 
	 * Everything else at data loading time is considered as a default value (one week), including null. 
	 */
	public TickBarFeedDescriptor(
			Instrument instrument,
			TickBarSize tickBarSize,
			OfferSide offerSide,
			Period basePeriod
	) {
		setDataType(DataType.TICK_BAR);
		setInstrument(instrument);
		setTickBarSize(tickBarSize);
		setOfferSide(offerSide);
		super.setPeriod(validateBasePeriod(basePeriod));
		setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
	}
	
	@Override
	public void setPeriod(Period period) {
		super.setPeriod(validateBasePeriod(period));
	}
	
	@Override
	public void setDataInterpolationDescriptor(DataInterpolationDescriptor interpolationDescriptor){
		super.setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
	}

}
