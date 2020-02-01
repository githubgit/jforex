/*
 * Copyright 2014 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.LineBreakLookback;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.CreationPoint;
import com.dukascopy.api.feed.DataInterpolationDescriptor;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.ILineBreak;
import com.dukascopy.api.feed.ITailoredFeedDescriptor;

/**
 * @author Janis Garsils
 * 
 * The class extends {@link FeedDescriptor}
 */
public class LineBreakFeedDescriptor extends FeedDescriptor implements ITailoredFeedDescriptor<ILineBreak> {

	/**
	 * Constructor, that uses minimal set of parameters. 
	 * 
	 * Default values are used for: 
	 * <ul>
	 * <li>time session, {@link Period#TICK}
	 * <li>creation point, {@link CreationPoint#CLOSE}
	 * <li>number of look-back lines, {@link LineBreakLookback#THREE_LINES}; 
	 * <li>base period, {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}.
	 * </ul>
	 * 
	 * @param instrument instrument
	 * @param offerSide offer side
	 */
	public LineBreakFeedDescriptor(
			Instrument instrument,
			OfferSide offerSide
	) {
		this(instrument, offerSide, null, null, null, null);
	}
	
	/**
	 * Constructor, that uses default values for: 
	 * <ul>
	 * <li>creation point, {@link CreationPoint#CLOSE}
	 * <li>number of look-back lines, {@link LineBreakLookback#THREE_LINES}; 
	 * <li>base period, {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}.
	 * </ul>
	 * 
	 * @param instrument instrument
	 * @param offerSide offer side
	 * @param timeSession indicates how often we take a price. Can be any valid {@link Period}. If null, then default {@link Period#TICK} is used. 
	 */
	public LineBreakFeedDescriptor(
			Instrument instrument,
			OfferSide offerSide,
			Period timeSession
	) {
		this(instrument, offerSide, timeSession, null, null, null);
	}
	
	/**
	 * Constructor, that uses default values for: 
	 * <ul>
	 * <li>number of look-back lines, {@link LineBreakLookback#THREE_LINES}; 
	 * <li>base period, {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}.
	 * </ul>
	 * 
	 * @param instrument instrument
	 * @param offerSide offer side
	 * @param timeSession indicates how often we take a price. Can be any valid {@link Period}. If null, then default {@link Period#TICK} is used. 
	 * @param creationPoint price point of the time session. If null, then default {@link CreationPoint#CLOSE} is used.  
	 */
	public LineBreakFeedDescriptor(
			Instrument instrument,
			OfferSide offerSide,
			Period timeSession,
			CreationPoint creationPoint
	) {
		this(instrument, offerSide, timeSession, creationPoint, null, null);
	}
	
	/**
	 * Constructor, that uses default values for: 
	 * <ul>
	 * <li>base period, {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}.
	 * </ul>
	 * 
	 * @param instrument instrument
	 * @param offerSide offer side
	 * @param timeSession indicates how often we take a price. Can be any valid {@link Period}. If null, then default {@link Period#TICK} is used. 
	 * @param creationPoint price point of the time session. If null, then default {@link CreationPoint#CLOSE} is used.  
	 * @param lookbackLines number of lines which will define turnaround price. If null, then default {@link LineBreakLookback#THREE_LINES} is used. 
	 */
	public LineBreakFeedDescriptor(
			Instrument instrument,
			OfferSide offerSide,
			Period timeSession,
			CreationPoint creationPoint,
			LineBreakLookback lookbackLines
	) {
		this(instrument, offerSide, timeSession, creationPoint, lookbackLines, null);
	}
	
	/**
	 * Constructor, that sets all required fields.
	 * 
	 * @param instrument instrument
	 * @param offerSide offer side
	 * @param timeSession indicates how often we take a price. Can be any valid {@link Period}. If null, then default {@link Period#TICK} is used. 
	 * @param creationPoint price point of the time session. If null, then default {@link CreationPoint#CLOSE} is used.  
	 * @param lookbackLines number of lines which will define turnaround price. If null, then default {@link LineBreakLookback#THREE_LINES} is used. 
	 * @param basePeriod the period, in which lines are calculated. If base period is given {@link Period#INFINITY}, then new lines are calculated from the beginning of the history. 
	 * Everything else at data loading time is considered as a default value ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null. 
	 */
	public LineBreakFeedDescriptor(
			Instrument instrument,
			OfferSide offerSide,
			Period timeSession,
			CreationPoint creationPoint,
			LineBreakLookback lookbackLines,
			Period basePeriod
	) {
		setDataType(DataType.LINE_BREAK);
		setInstrument(instrument);
		setOfferSide(offerSide);		
		super.setTimeSession(validateTimeSession(timeSession)); 
		super.setCreationPoint(validateCreationPoint(creationPoint));
		super.setLineBreakLookback(validateLineBreakLookback(lookbackLines));
		super.setPeriod(validateBasePeriod(basePeriod)); 
		setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
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
	public void setCreationPoint(CreationPoint creationPoint) {
		super.setCreationPoint(validateCreationPoint(creationPoint));
	}
	
	@Override
	public void setLineBreakLookback(LineBreakLookback lookbackLines){
		super.setLineBreakLookback(validateLineBreakLookback(lookbackLines));
	}
	
}
