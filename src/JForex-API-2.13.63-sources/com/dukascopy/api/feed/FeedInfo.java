/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Implementation of {@link com.dukascopy.api.feed.IFeedInfo}
 *
 * @author Mark Vilkel
 */
@Deprecated
public class FeedInfo implements IFeedInfo {

	private DataType dataType = null;
	private IFinancialInstrument financialInstrument = null;
	private OfferSide offerSide;
	private Period period;
	private PriceRange priceRange;
	private ReversalAmount reversalAmount;
	private TickBarSize tickBarSize;
	private Filter filter;
	private Period timeSession;
	private CreationPoint creationPoint;
	private DataInterpolationDescriptor interpolationDescriptor;
	private LineBreakLookback lookbackLines;

	/**
	 * Default constructor
	 */
	public FeedInfo() {

	}

	public FeedInfo(
            DataType dataType,
            IFinancialInstrument financialInstrument,
            Period period,
            OfferSide offerSide,
            PriceRange priceRange,
            ReversalAmount reversalAmount,
            TickBarSize tickBarSize,
            Filter filter
    ) {
		super();
		this.dataType = dataType;
		this.financialInstrument = financialInstrument;
		this.offerSide = offerSide;
		this.period = period;
		this.priceRange = priceRange;
		this.reversalAmount = reversalAmount;
		this.tickBarSize = tickBarSize;
		this.filter = filter;
	}

	/**
	 * Copy constructor
	 *
	 * @param feedInfo IFeedInfo
	 */
	public FeedInfo(IFeedInfo feedInfo) {
		this.dataType = feedInfo.getDataType();
		this.financialInstrument = feedInfo.getFinancialInstrument();
		this.offerSide = feedInfo.getOfferSide();
		this.priceRange = feedInfo.getPriceRange();
		this.reversalAmount = feedInfo.getReversalAmount();
		this.tickBarSize = feedInfo.getTickBarSize();
		this.filter = feedInfo.getFilter();
		this.creationPoint = feedInfo.getCreationPoint();
		this.timeSession = feedInfo.getTimeSession();
		this.lookbackLines = feedInfo.getLineBreakLookback();
		this.interpolationDescriptor = feedInfo.getDataInterpolationDescriptor();
		initPeriod(feedInfo.getPeriod());
	}
	
	private void initPeriod(Period period){
		if (period == null){
			this.period = DEFAULT_BASE_PERIOD;
		}
		else {
			this.period = period;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFinancialInstrument getFinancialInstrument() {
		return financialInstrument;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFinancialInstrument(IFinancialInstrument financialInstrument) {
		this.financialInstrument = financialInstrument;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OfferSide getOfferSide() {
		return offerSide;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOfferSide(OfferSide offerSide) {
		this.offerSide = offerSide;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataType getDataType() {
		return dataType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Period getPeriod() {
		return period;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PriceRange getPriceRange() {
		return priceRange;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPriceRange(PriceRange priceRange) {
		this.priceRange = priceRange;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReversalAmount getReversalAmount() {
		return reversalAmount;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReversalAmount(ReversalAmount reversalAmount) {
		this.reversalAmount = reversalAmount;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TickBarSize getTickBarSize() {
		return tickBarSize;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTickBarSize(TickBarSize tickBarSize) {
		this.tickBarSize = tickBarSize;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Filter getFilter() {
		return filter;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	@Override
	public void setTimeSession(Period timeSession) {
		this.timeSession = timeSession;		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Period getTimeSession() {
		return timeSession;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCreationPoint(CreationPoint creationPoint) {
		this.creationPoint = creationPoint;		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CreationPoint getCreationPoint() {
		return creationPoint;
	}	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLineBreakLookback(LineBreakLookback numOfLines) {
		this.lookbackLines = numOfLines;		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LineBreakLookback getLineBreakLookback() {
		return lookbackLines;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataInterpolationDescriptor(DataInterpolationDescriptor interpolationDescriptor) {
		if (interpolationDescriptor == null){
			interpolationDescriptor = DataInterpolationDescriptor.DEFAULT;
		}
		this.interpolationDescriptor = interpolationDescriptor;		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataInterpolationDescriptor getDataInterpolationDescriptor() {
		return interpolationDescriptor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((financialInstrument == null) ? 0 : financialInstrument.hashCode());
		result = prime * result + ((offerSide == null) ? 0 : offerSide.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((priceRange == null) ? 0 : priceRange.hashCode());
		result = prime * result + ((reversalAmount == null) ? 0 : reversalAmount.hashCode());
		result = prime * result + ((tickBarSize == null) ? 0 : tickBarSize.hashCode());
		result = prime * result + ((timeSession == null ? 0 : timeSession.hashCode()));
		result = prime * result + ((creationPoint == null ? 0 : creationPoint.hashCode()));
		result = prime * result + ((lookbackLines == null ? 0 : lookbackLines.hashCode()));
		result = prime * result + ((interpolationDescriptor == null ? 0 : interpolationDescriptor.hashCode()));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		FeedInfo other = (FeedInfo) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (financialInstrument == null) {
			if (other.financialInstrument != null)
				return false;
		} else if (!financialInstrument.equals(other.financialInstrument))
			return false;
		if (offerSide == null) {
			if (other.offerSide != null)
				return false;
		} else if (!offerSide.equals(other.offerSide))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (priceRange == null) {
			if (other.priceRange != null)
				return false;
		} else if (!priceRange.equals(other.priceRange))
			return false;
		if (reversalAmount == null) {
			if (other.reversalAmount != null)
				return false;
		} else if (!reversalAmount.equals(other.reversalAmount))
			return false;
		if (tickBarSize == null) {
			if (other.tickBarSize != null)
				return false;
		} else if (!tickBarSize.equals(other.tickBarSize))
			return false;
		if (timeSession == null) {
			if (other.timeSession != null)
				return false;
		} else if (!timeSession.equals(other.timeSession))
			return false;
		if (creationPoint == null) {
			if (other.creationPoint != null)
				return false;
		} else if (!creationPoint.equals(other.creationPoint))
			return false;
		if (lookbackLines == null) {
			if (other.lookbackLines != null)
				return false;
		} else if (!lookbackLines.equals(other.lookbackLines))
			return false;
		if (interpolationDescriptor == null) {
			if (other.interpolationDescriptor != null)
				return false;
		} else if (!interpolationDescriptor.equals(other.interpolationDescriptor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (dataType != null) {
			builder.append(", ").append("dataType=").append(dataType);
		}
		if (financialInstrument != null) {
			builder.append(", ").append("financialInstrument=").append(financialInstrument);
		}
		if (offerSide != null) {
			builder.append(", ").append("offerSide=").append(offerSide);
		}
		if (period != null) {
			builder.append(", ").append("period=").append(period);
		}
		if (priceRange != null) {
			builder.append(", ").append("priceRange=").append(priceRange);
		}
		if (reversalAmount != null) {
			builder.append(", ").append("reversalAmount=").append(reversalAmount);
		}
		if (tickBarSize != null) {
			builder.append(", ").append("tickBarSize=").append(tickBarSize);
		}
		if (filter != null) {
			builder.append(", ").append("filter=").append(filter);
		}
		if (timeSession != null){
			builder.append(", ").append("timeSession=").append(timeSession);
		}
		if (creationPoint != null){
			builder.append(", ").append("creationPoint=").append(creationPoint);
		}
		if (lookbackLines != null){
			builder.append(", ").append("lookbackLines=").append(lookbackLines);
		}
		if (interpolationDescriptor != null){
			builder.append(", ").append("interpolationDescriptor=").append(interpolationDescriptor);
		}
        if (builder.length() >= 2) {
            builder.delete(0, 2);
        }
		builder.insert(0, "FeedInfo [");
		builder.append("]");
		return builder.toString();
	}
    
    public static Period validateBasePeriod(Period basePeriod){
    	Period basePer;
		if (Period.isInfinity(basePeriod)){
			basePer = Period.INFINITY;
		} else {
            basePer = DEFAULT_BASE_PERIOD;
		}
		return basePer;
    }
    
    protected static Period validateRenkoSession(Period renkoSession){
    	return validateTimeSession(renkoSession);
    }
    
    protected static Period validateTimeSession(Period timeSession){
    	if (timeSession == null){
    		return Period.TICK;
    	}
    	return timeSession;
    }
    
    protected static RenkoCreationPoint validateRenkoCreationPoint(RenkoCreationPoint creationPoint){
   		return CreationPoint.convertToRenkoCreationPoint(validateCreationPoint(RenkoCreationPoint.convertToCreationPoint(creationPoint)));    		
    }
    
    protected static CreationPoint validateCreationPoint(CreationPoint creationPoint){
    	if (creationPoint == null){
    		return CreationPoint.CLOSE;
    	}
    	return creationPoint;
    }
    
    protected static LineBreakLookback validateLineBreakLookback(LineBreakLookback lookbackLines){
    	if (lookbackLines == null){
    		return LineBreakLookback.getDefault();
    	}
    	return lookbackLines;
    }

	protected static PriceRange validatePriceRange(PriceRange reversalAmount){
		if (reversalAmount == null){
			return PriceRange.THREE_PIPS;
		}
		return reversalAmount;
	}

	protected static PriceRange validateTurnaroundAmount(PriceRange reversalAmount){
		if (reversalAmount == null){
			return PriceRange.ONE_PIP;
		}
		return reversalAmount;
	}

}
