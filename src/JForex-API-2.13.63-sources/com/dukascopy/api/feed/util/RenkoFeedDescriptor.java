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
import com.dukascopy.api.feed.CreationPoint;
import com.dukascopy.api.feed.DataInterpolationDescriptor;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IRenkoBar;
import com.dukascopy.api.feed.ITailoredFeedDescriptor;
import com.dukascopy.api.feed.RenkoCreationPoint;
import com.dukascopy.api.feed.RenkoType;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor}
 */
public class RenkoFeedDescriptor extends FeedDescriptor implements ITailoredFeedDescriptor<IRenkoBar> {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation.
	 * This constructor sets base period to default ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}),
     * renko session to Period.TICK, renko creation point to {@link RenkoCreationPoint#CLOSE} and renko type to {@link RenkoType#REGULAR}.
	 * 
	 * @param instrument instrument
	 * @param brickSize brick size
	 * @param offerSide offer side
	 */
	public RenkoFeedDescriptor(
			Instrument instrument,
			PriceRange brickSize,
			OfferSide offerSide
	) {
		this(instrument, brickSize, offerSide, null);
	}
	
	/**
	 * This constructor sets renko session to Period.TICK, renko creation point to {@link RenkoCreationPoint#CLOSE} and renko type to {@link RenkoType#REGULAR}.
	 * 
	 * @param instrument instrument
	 * @param brickSize brick size
	 * @param offerSide offer side
	 * @param basePeriod the period, in which the renko bars are calculated. If base period is given {@link Period#INFINITY}, then renko bars are calculated from the beginning of the history. 
	 * Everything else at data loading time is considered as a default value (o{@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null. 
	 */
	public RenkoFeedDescriptor(
			Instrument instrument,
			PriceRange brickSize,
			OfferSide offerSide,
			Period basePeriod
	) {
		this(instrument, brickSize, offerSide, null, validateCreationPoint(null), basePeriod);
	}
	
	/**
	 * This constructor sets base period to default ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}) and renko type to {@link RenkoType#REGULAR}.
	 * 
	 * @param instrument instrument
	 * @param brickSize brick size
	 * @param offerSide offer side
	 * @param timeSession time session
	 * @param creationPoint creation point
	 */
	public RenkoFeedDescriptor(
			Instrument instrument,
			PriceRange brickSize,
			OfferSide offerSide,
			Period timeSession,
			CreationPoint creationPoint
	) {
		this(instrument, brickSize, offerSide, timeSession, creationPoint, null);
	}
	
	/**
	 * This constructor sets base period to default ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}) and renko type to {@link RenkoType#REGULAR}.
	 * 
	 * @param instrument instrument
	 * @param brickSize brick size
	 * @param offerSide offer side
	 * @param renkoSessionPeriod time session
	 * @param renkoCreationPoint creation point
	 * @deprecated - use analog constructor, which takes CreationPoint instead of RenkoCreationPoint
	 */
	@Deprecated 
	public RenkoFeedDescriptor(
			Instrument instrument,
			PriceRange brickSize,
			OfferSide offerSide,
			Period renkoSessionPeriod,
			RenkoCreationPoint renkoCreationPoint
	) {
		this(instrument, brickSize, offerSide, renkoSessionPeriod, renkoCreationPoint, null);
	}
	
	
	/**
	 * This constructor sets renko type to {@link RenkoType#REGULAR}.
	 * 
	 * @param instrument instrument
	 * @param brickSize brick size
	 * @param offerSide offer side
	 * @param renkoSessionPeriod time session
	 * @param renkoCreationPoint creation point
	 * @param basePeriod the period, in which the renko bars are calculated. If base period is given {@link Period#INFINITY}, then renko bars are calculated from the beginning of the history. 
	 * Everything else at data loading time is considered as a default value ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
	 * @deprecated - use analog constructor, which takes CreationPoint instead of RenkoCreationPoint
	 */
	@Deprecated 
	public RenkoFeedDescriptor(
			Instrument instrument,
			PriceRange brickSize,
			OfferSide offerSide,
			Period renkoSessionPeriod,
			RenkoCreationPoint renkoCreationPoint,
			Period basePeriod
	) {
		this(instrument, brickSize, offerSide, renkoSessionPeriod, RenkoCreationPoint.convertToCreationPoint(renkoCreationPoint), basePeriod);
	}
	
	/**
	 * This constructor sets renko type to {@link RenkoType#REGULAR}.
	 * 
	 * @param instrument instrument
	 * @param brickSize brick size
	 * @param offerSide offer side
	 * @param timeSession time session
	 * @param creationPoint creation point
	 * @param basePeriod the period, in which the renko bars are calculated. If base period is given {@link Period#INFINITY}, then renko bars are calculated from the beginning of the history. 
	 * Everything else at data loading time is considered as a default value ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null. 
	 */
	public RenkoFeedDescriptor(
			Instrument instrument,
			PriceRange brickSize,
			OfferSide offerSide,
			Period timeSession,
			CreationPoint creationPoint,
			Period basePeriod
	) {
        this(instrument, brickSize, offerSide, timeSession, creationPoint, basePeriod, null);
	}

	/**
	 * Constructor, that sets all required fields.
	 *
	 * @param instrument instrument
	 * @param brickSize brick size
	 * @param offerSide offer side
	 * @param timeSession time session
	 * @param creationPoint creation point
	 * @param basePeriod the period, in which the renko bars are calculated. If base period is given {@link Period#INFINITY}, then renko bars are calculated from the beginning of the history.
	 * Everything else at data loading time is considered as a default value ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
     * @param renkoType renko type
	 */
	public RenkoFeedDescriptor(
			Instrument instrument,
			PriceRange brickSize,
			OfferSide offerSide,
			Period timeSession,
			CreationPoint creationPoint,
			Period basePeriod,
            RenkoType renkoType
	) {
		setDataType(DataType.RENKO);
		setInstrument(instrument);
		setPriceRange(brickSize);
		setOfferSide(offerSide);
		super.setTimeSession(validateTimeSession(timeSession));
		super.setCreationPoint(validateCreationPoint(creationPoint));
		super.setPeriod(validateBasePeriod(basePeriod));
		setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
        super.setRenkoType(validateRenkoType(renkoType));
	}
	
	@Override
	public void setPeriod(Period period) {
		super.setPeriod(validateBasePeriod(period));
	}
	
	@Override 
	public void setRenkoSession(Period timeSession) {
		setTimeSession(timeSession);		
	}
	
	@Override
	public void setTimeSession(Period timeSession) {
		super.setTimeSession(validateTimeSession(timeSession));		
	}
	
	@Override 
	public void setRenkoCreationPoint( RenkoCreationPoint renkoCreationPoint) {
		setCreationPoint(RenkoCreationPoint.convertToCreationPoint(renkoCreationPoint));
	}
	
	@Override
	public void setCreationPoint(CreationPoint creationPoint) {
		super.setCreationPoint(validateCreationPoint(creationPoint));
	}

    @Override
    public void setRenkoType(RenkoType renkoType) {
        super.setRenkoType(validateRenkoType(renkoType));
    }
	
}
