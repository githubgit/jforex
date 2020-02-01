/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFTimeZone;
import com.dukascopy.api.LineBreakLookback;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.api.Unit;

/**
 * Interface, which describes feed data.
 * <p>
 * There might be several data types supported by JForex, see {@link DataType}
 * <p>
 * Ticks are defined by<br>
 * 		{@link DataType#TICKS} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)})
 * <p>
 * Candles/Bars are defined by<br>
 *    	{@link DataType#TIME_PERIOD_AGGREGATION} setter - ({@link #setDataType(DataType)}),<br>
 *    	{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 *    	{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)}),<br>
 *    	{@link Filter} (setter - {@link #setFilter(Filter)}),<br>
 *    	{@link Period} (setter - {@link #setPeriod(Period)})
 * <p>
 * RangeBars are defined by<br>
 * 		{@link DataType#PRICE_RANGE_AGGREGATION} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link PriceRange} (setter - {@link #setPriceRange(PriceRange)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)}),<br>
 * 		{@link Period} ((base period) setter - {@link #setPeriod(Period)})<br>
 * <p>
 * Point and Figures are defined by<br>
 * 		{@link DataType#POINT_AND_FIGURE} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link PriceRange} called box size (setter - {@link #setPriceRange(PriceRange)}),<br>
 * 		{@link ReversalAmount} (setter - {@link #setReversalAmount(ReversalAmount)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)}),<br>
 * 		{@link Period} ((session length) setter - {@link #setTimeSession(Period)}),<br>
 * 		{@link CalculationMethod} (setter - {@link #setCalculationMethod(CalculationMethod)})
 * 		{@link Period} ((base period) setter - {@link #setPeriod(Period)}),<br>
 * <p>
 * Tick Bars are defined by<br>
 * 		{@link DataType#TICK_BAR} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link TickBarSize} (setter - {@link #setTickBarSize(TickBarSize)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)}),<br>
 * 		{@link Period} ((base period) setter - {@link #setPeriod(Period)})
 * <p> 
 * Renkos are defined by<br>
 * 		{@link DataType#RENKO} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link PriceRange} called brick size (setter - {@link #setPriceRange(PriceRange)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)},<br>
 * 		{@link Period} ((session length) setter - {@link #setTimeSession(Period)}),<br>
 * 		{@link CreationPoint} (setter - {@link #setCreationPoint(CreationPoint)}),<br>
 * 		{@link Period} ((base period) setter - {@link #setPeriod(Period)}),<br>
 * 		{@link RenkoType} (setter - {@link #setRenkoType(RenkoType)})
 * <p>
 * Line Break lines are defined by<br>
 * 		{@link DataType#LINE_BREAK} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)}),<br>
 * 		{@link Period} ((session length) setter - {@link #setTimeSession(Period)}),<br>
 *		{@link CreationPoint} (setter - {@link #setCreationPoint(CreationPoint)}),<br> 
 *		{@link LineBreakLookback} (setter - {@link #setLineBreakLookback(LineBreakLookback)}),<br>
 * 		{@link Period} ((base period) setter - {@link #setPeriod(Period)})<br>
 * <p>
 * Kagi bars are defined by<br>
 * 		{@link DataType#KAGI} (setter - {@link #setDataType(DataType)}),<br>
 * 		{@link Instrument} (setter - {@link #setInstrument(Instrument)}),<br>
 * 		{@link PriceRange} called turnaround amount (setter - {@link #setPriceRange(PriceRange)}),<br>
 * 		{@link OfferSide} (setter - {@link #setOfferSide(OfferSide)}),<br>
 * 		{@link Period} ((session length) setter - {@link #setTimeSession(Period)}),<br>
 *		{@link CreationPoint} (setter - {@link #setCreationPoint(CreationPoint)}),<br>
 * 		{@link Period} ((base period) setter - {@link #setPeriod(Period)})<br>
 * <p>  
 * 
 * @author Mark Vilkel, Janis Garsils
 */
public interface IFeedDescriptor {

	/**
	 * Base period works as a starting point from which to calculate price aggregation bars. E.g. DEFAULT_BASE_PERIOD starts calculation 
	 * at every Sunday 21:00 or 22:00 UTC (depends on summer/winter time). 
	 * In theory the calculation of price aggregated bars starts with the first tick of the history. 
	 * Practically for JForex this means to load all history and calculate bars from the first available tick. This would be very time and resource consuming process. 
	 * To speed it up, platform uses base period. When the base period starts, price aggr. bars are calculated 
	 * as if this would be the beginning of the history. So, as a result we don't load all history, but just e.g. 7 days (in case of DEFAULT_BASE_PERIOD).
	 * 
	 */	
	public static final Period DEFAULT_BASE_PERIOD = Period.createCustomPeriod(Unit.Day, 7, JFTimeZone.EET);
	public static final Period INFINITE_BASE_PERIOD = Period.INFINITY;
	
	/**
	 * Getter for instrument
	 * 
	 * @return instrument
	 */
	Instrument getInstrument();

	/**
	 * Setter for instrument
	 * 
	 * @param instrument instrument
	 */
	void setInstrument(Instrument instrument);
	

	/**
	 * Getter for offer side
	 * 
	 * @return offer side
	 */
	OfferSide getOfferSide();

	/**
	 * Setter for offer side
	 * 
	 * @param offerSide offer side
	 */
	void setOfferSide(OfferSide offerSide);
	

	/**
	 * Getter for data type
	 * 
	 * @return data type
	 */
	DataType getDataType();

	/**
	 * Setter for data type. 
	 * 
	 * @param dataType data type
	 */
	void setDataType(DataType dataType);
	

	/**
	 * Getter for period
	 * 
	 * @return period
	 */
	Period getPeriod();

	/**
	 * Setter for period. 
	 * 
	 * Setter for period. For price aggregation and tick bars ({@link DataType#TICK_BAR}, {@link DataType#PRICE_RANGE_AGGREGATION}, {@link DataType#RENKO}, {@link DataType#POINT_AND_FIGURE}, {@link DataType#LINE_BREAK}, {@link DataType#KAGI}) 
	 * period works as a base period (more information about base period - {@link #DEFAULT_BASE_PERIOD}).
	 * Only two base periods are possible - {@link Period#INFINITY} and one week ({@link #DEFAULT_BASE_PERIOD}). If one provides anything else (including null) than {@link Period#INFINITY}, then at data
	 * loading time it is considered to be as a default base period ({@link #DEFAULT_BASE_PERIOD}).
	 * <br>{@link Period#INFINITY} is not allowed on remote run.
	 * 
	 * @param period period
	 */
	void setPeriod(Period period);
	

	/**
	 * Getter for price range
	 * 
	 * @return price range
	 */
	PriceRange getPriceRange();

	/**
	 * Setter for price range
	 * 
	 * @param priceRange price range
	 */
	void setPriceRange(PriceRange priceRange);
	

	/**
	 * Getter for reversal amount
	 * 
	 * @return reversal amount
	 */
	ReversalAmount getReversalAmount();

	/**
	 * Setter for reversal amount
	 * 
	 * @param reversalAmount reversal amount
	 */
	void setReversalAmount(ReversalAmount reversalAmount);
	

	/**
	 * Getter for tick bar size
	 * 
	 * @return tick bar size
	 */
	TickBarSize getTickBarSize();

	/**
	 * Setter for tick bar size
	 * 
	 * @param tickBarSize tick bar size
	 */
	void setTickBarSize(TickBarSize tickBarSize);


	/**
	 * Getter for filter
	 * 
	 * @return filter
	 */
	Filter getFilter();

	/**
	 * Setter for filter
	 * 
	 * @param filter filter
	 */
	void setFilter(Filter filter);


	/**
	 * Setter for DataType.RENKO session period
	 * 
	 * @param renkoSession Renko session period
	 * @deprecated - use setTimeSession(Period timeSession)
	 */
	@Deprecated
	void setRenkoSession(Period renkoSession);
	
	/**
	 * Getter for DataType.RENKO session period
	 * 
	 * @return Renko session period
	 * @deprecated - use getTimeSession()
	 */
	@Deprecated 
	Period getRenkoSession();
	
	/**
	 * Setter for DataType.LINE_BREAK, DataType.RENKO, DataType.KAGI, DataType.POINT_AND_FIGURE session period
	 * 
	 * @param timeSession session period
	 */
	void setTimeSession(Period timeSession);
	
	/**
	 * Getter for DataType.LINE_BREAK, DataType.RENKO, DataType.KAGI, DataType.POINT_AND_FIGURE session period
	 * 
	 * @return session period
	 */
	Period getTimeSession();


	/**
	 * Setter for DataType.RENKO data creation point
     *
	 * @param renkoCreationPoint RenkoCreationPoint
	 * @deprecated - use setCreationPoint(CreationPoint creationPoint)
	 */
	@Deprecated 
	void setRenkoCreationPoint(RenkoCreationPoint renkoCreationPoint);
	
	/**
	 * Getter for DataType.RENKO data creation point
	 * 
	 * @return RenkoCreationPoint
	 * @deprecated - use getCreationPoint()
	 */
	@Deprecated 
	RenkoCreationPoint getRenkoCreationPoint();
	
	/**
	 * Setter for DataType.LINE_BREAK, DataType.RENKO, DataType.KAGI data creation point
     *
	 * @param creationPoint CreationPoint
	 */
	void setCreationPoint(CreationPoint creationPoint);
	
	/**
	 * Getter for DataType.LINE_BREAK, DataType.RENKO, DataType.KAGI data creation point
     *
	 * @return CreationPoint
	 */
	CreationPoint getCreationPoint();
	

	/**
	 * Setter for DataType.PRICE_RANGE_AGGREGATION DataInterpolationDescriptor.
	 * 
	 * @param interpolationDescriptor DataInterpolationDescriptor
	 */
	void setDataInterpolationDescriptor(DataInterpolationDescriptor interpolationDescriptor);
	
	/**
	 * Getter for DataType.PRICE_RANGE_AGGREGATION DataInterpolationDescriptor.
	 * 
	 * @return DataInterpolationDescriptor
	 */
	DataInterpolationDescriptor getDataInterpolationDescriptor();


	/**
	 * Setter for DataType.LINE_BREAK number of look-back lines.
     *
	 * @param numOfLines LineBreakLookback
	 */
	void setLineBreakLookback(LineBreakLookback numOfLines);
	
	/**
	 * Getter for DataType.LINE_BREAK number of look-back lines.
     *
	 * @return LineBreakLookback
	 */
	LineBreakLookback getLineBreakLookback();


	/**
	 * Setter for DataType.RENKO bricks type.
     *
	 * @param renkoType RenkoType
	 */
	void setRenkoType(RenkoType renkoType);

	/**
	 * Getter for DataType.RENKO bricks type.
     *
	 * @return RenkoType
	 */
	RenkoType getRenkoType();


	/**
	 * Setter for DataType.POINT_AND_FIGURE calculation method.
     *
	 * @param calculationMethod CalculationMethod
	 */
	void setCalculationMethod(CalculationMethod calculationMethod);

	/**
	 * Getter for DataType.POINT_AND_FIGURE calculation method.
     *
	 * @return CalculationMethod
	 */
	CalculationMethod getCalculationMethod();
	
}
