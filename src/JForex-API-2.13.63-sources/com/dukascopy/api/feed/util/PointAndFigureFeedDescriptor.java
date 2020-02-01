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
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.feed.CalculationMethod;
import com.dukascopy.api.feed.DataInterpolationDescriptor;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.IPointAndFigure;
import com.dukascopy.api.feed.ITailoredFeedDescriptor;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor}
 */
public class PointAndFigureFeedDescriptor extends FeedDescriptor implements ITailoredFeedDescriptor<IPointAndFigure>{

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * Base period is set to default (one week).
	 * 
	 * @param instrument instrument
	 * @param boxSize box size
	 * @param reversalAmount reversal amount
	 * @param offerSide offer side
	 */
	public PointAndFigureFeedDescriptor(
			Instrument instrument,
			PriceRange boxSize,
			ReversalAmount reversalAmount,
			OfferSide offerSide
	) {
		this(instrument, boxSize, reversalAmount, offerSide, null, null, null);
	}
	
	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation.
	 * The period represents the base period.
	 * 
	 * @param instrument instrument
	 * @param boxSize box size
	 * @param reversalAmount reversal amount
	 * @param offerSide offer side
	 * @param basePeriod the period, in which the point and figure bars are calculated. If base period is given {@link Period#INFINITY}, then point and figure bars are calculated from the beginning of the history. 
	 * Everything else at data loading time is considered as a default value (one week), including null. 
	 */
	public PointAndFigureFeedDescriptor(
			Instrument instrument,
			PriceRange boxSize,
			ReversalAmount reversalAmount,
			OfferSide offerSide,
			Period basePeriod
	) {
		this(instrument, boxSize, reversalAmount, offerSide, null, null, basePeriod);
	}
	
	/**
	 * Constructor, that sets all required fields.
	 * 
	 * @param instrument instrument
	 * @param boxSize box size
	 * @param reversalAmount reversal amount
	 * @param offerSide offer side
	 * @param basePeriod the period, in which the point and figure bars are calculated. If base period is given {@link Period#INFINITY}, then point and figure bars are calculated from the beginning of the history. 
	 * Everything else at data loading time is considered as a default value (one week), including null. 
	 * @param interpolationDescriptor tick interpolation descriptor. If null, the DataInterpolationDescriptor.DEFAULT interpolation is used. 
	 * To get most suitable interpolation descriptor, use {@link DataInterpolationDescriptor#getSuitableDataInterpolationDescriptor(PriceRange priceRange, ReversalAmount reversalAmount)} method. 
	 * To find more about tick interpolation from candles and it's purpose, see {@link DataInterpolationDescriptor}.
     * @deprecated Interpolation descriptor is not used for P&amp;F calculation any more.
     * See {@link #PointAndFigureFeedDescriptor(com.dukascopy.api.Instrument, com.dukascopy.api.PriceRange, com.dukascopy.api.ReversalAmount, com.dukascopy.api.OfferSide, com.dukascopy.api.Period, com.dukascopy.api.feed.CalculationMethod, com.dukascopy.api.Period)}
	 */
	public PointAndFigureFeedDescriptor(
			Instrument instrument,
			PriceRange boxSize,
			ReversalAmount reversalAmount,
			OfferSide offerSide,
			Period basePeriod,
			DataInterpolationDescriptor interpolationDescriptor
	) {
        this(instrument, boxSize, reversalAmount, offerSide, null, null, basePeriod);
	}

	/**
	 * Constructor, that sets all required fields.
	 *
	 * @param instrument instrument
	 * @param boxSize box size
	 * @param reversalAmount reversal amount
	 * @param offerSide offer side
     * @param timeSession time session
     * @param calculationMethod calculation method
	 * @param basePeriod the period, in which the point and figure bars are calculated. If base period is given {@link Period#INFINITY}, then point and figure bars are calculated from the beginning of the history.
	 * Everything else at data loading time is considered as a default value (one week), including null.
	 */
	public PointAndFigureFeedDescriptor(
			Instrument instrument,
			PriceRange boxSize,
			ReversalAmount reversalAmount,
			OfferSide offerSide,
            Period timeSession,
            CalculationMethod calculationMethod,
			Period basePeriod
	) {
		setDataType(DataType.POINT_AND_FIGURE);
		setInstrument(instrument);
		setPriceRange(boxSize);
		setReversalAmount(reversalAmount);
		setOfferSide(offerSide);
		setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
        setTimeSession(timeSession);
        setCalculationMethod(calculationMethod);
		setPeriod(basePeriod);
	}
	
	@Override
	public void setPeriod(Period period) {
		super.setPeriod(validateBasePeriod(period));
	}

	@Override
	public void setTimeSession(Period timeSession) {
		super.setTimeSession(validateTimeSession(timeSession));
	}

	@Override
	public void setCalculationMethod(CalculationMethod calculationMethod) {
		super.setCalculationMethod(validateCalculationMethod(calculationMethod));
	}
	
}
