/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.LineBreakLookback;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;

/**
 * Implementation of {@link IFeedDescriptor}
 * 
 * @author Mark Vilkel
 */
public class FeedDescriptor implements IFeedDescriptor {
    
	private DataType dataType;
	private Instrument instrument;
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
    private RenkoType renkoType;
    private CalculationMethod calculationMethod;
	
	/**
	 * Default constructor
	 */
	public FeedDescriptor() {
		
	}
	
	public FeedDescriptor(
			DataType dataType,
			Instrument instrument,
			Period period,
			OfferSide offerSide
	) {
		this(dataType, instrument, period, offerSide, null, null, null, null);
	}

	public FeedDescriptor(
			DataType dataType,
			Instrument instrument,
			Period period,
			OfferSide offerSide,
			PriceRange priceRange,
			ReversalAmount reversalAmount,
			TickBarSize tickBarSize,
			Filter filter
	) {
		super();
		this.dataType = dataType;
		this.instrument = instrument;
		this.period = period;
		this.offerSide = offerSide;
		this.priceRange = priceRange;
		this.reversalAmount = reversalAmount;
		this.tickBarSize = tickBarSize;
		this.filter = filter;
	}

	/**
	 * Copy constructor
	 * 
	 * @param feedDescriptor IFeedDescriptor
	 */
	public FeedDescriptor(IFeedDescriptor feedDescriptor) {
		this.dataType = feedDescriptor.getDataType();
		this.instrument = feedDescriptor.getInstrument();
		this.offerSide = feedDescriptor.getOfferSide();
		this.priceRange = feedDescriptor.getPriceRange();
		this.reversalAmount = feedDescriptor.getReversalAmount();
		this.tickBarSize = feedDescriptor.getTickBarSize();
		this.filter = feedDescriptor.getFilter();
		this.creationPoint = feedDescriptor.getCreationPoint();
		this.timeSession = feedDescriptor.getTimeSession();
		this.lookbackLines = feedDescriptor.getLineBreakLookback();
		this.interpolationDescriptor = feedDescriptor.getDataInterpolationDescriptor();
		this.renkoType = feedDescriptor.getRenkoType();
		this.calculationMethod = feedDescriptor.getCalculationMethod();
		initPeriod(feedDescriptor.getPeriod());
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
	public Instrument getInstrument() {
		return instrument;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRenkoSession(Period nissonRenkoSessionPeriod) {
		setTimeSession(nissonRenkoSessionPeriod);
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public Period getRenkoSession() {
		return getTimeSession();
	}
	/**
	 * {@inheritDoc}
	 */
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
	public RenkoCreationPoint getRenkoCreationPoint() {
		if (creationPoint == null){
			return null;
		}
		return CreationPoint.convertToRenkoCreationPoint(creationPoint);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRenkoCreationPoint( RenkoCreationPoint renkoCreationPoint) {
		if (renkoCreationPoint == null){
			this.creationPoint = null;
			return;
		}
		this.creationPoint = RenkoCreationPoint.convertToCreationPoint(renkoCreationPoint);
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
	public void setRenkoType(RenkoType renkoType) {
		this.renkoType = renkoType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RenkoType getRenkoType() {
		return renkoType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCalculationMethod(CalculationMethod calculationMethod) {
		this.calculationMethod = calculationMethod;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CalculationMethod getCalculationMethod() {
		return calculationMethod;
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
		result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
		result = prime * result + ((offerSide == null) ? 0 : offerSide.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((priceRange == null) ? 0 : priceRange.hashCode());
		result = prime * result + ((reversalAmount == null) ? 0 : reversalAmount.hashCode());
		result = prime * result + ((tickBarSize == null) ? 0 : tickBarSize.hashCode());
		result = prime * result + ((timeSession == null ? 0 : timeSession.hashCode()));
		result = prime * result + ((creationPoint == null ? 0 : creationPoint.hashCode()));
		result = prime * result + ((lookbackLines == null ? 0 : lookbackLines.hashCode()));
		result = prime * result + ((interpolationDescriptor == null ? 0 : interpolationDescriptor.hashCode()));
		result = prime * result + ((renkoType == null ? 0 : renkoType.hashCode()));
		result = prime * result + ((calculationMethod == null ? 0 : calculationMethod.hashCode()));
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
		FeedDescriptor other = (FeedDescriptor) obj;
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
		if (instrument == null) {
			if (other.instrument != null)
				return false;
		} else if (!instrument.equals(other.instrument))
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
		if (renkoType == null) {
			if (other.renkoType != null)
				return false;
		} else if (!renkoType.equals(other.renkoType))
			return false;
		if (calculationMethod == null) {
			if (other.calculationMethod != null)
				return false;
		} else if (!calculationMethod.equals(other.calculationMethod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (dataType != null) {
			builder.append(", ").append("dataType=").append(dataType);
		}
		if (instrument != null) {
			builder.append(", ").append("instrument=").append(instrument);
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
		if (renkoType != null){
			builder.append(", ").append("renkoType=").append(renkoType);
		}
		if (calculationMethod != null){
			builder.append(", ").append("calculationMethod=").append(calculationMethod);
		}
        if (builder.length() >= 2) {
            builder.delete(0, 2);
        }
		builder.insert(0, "FeedDescriptor [");
		builder.append("]");
		return builder.toString();
	}

	public static FeedDescriptor valueOf(String paramValue) {
		FeedDescriptor fd = new FeedDescriptor();

		//int searchStart = paramValue.indexOf("[") + 1;
		int searchEnd = paramValue.indexOf("]");
		int start, end;

		start = paramValue.indexOf("period=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 7, end);
			fd.setPeriod(Period.valueOfToString(value));
		}

		start = paramValue.indexOf("dataType=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 9, end);
			fd.setDataType(DataType.valueOf(value));
		}

		start = paramValue.indexOf("instrument=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			Instrument instrument = null;
			String value = paramValue.substring(start + 11, end);
			if (value.contains(Instrument.getPairsSeparator())) {
				instrument = Instrument.fromString(value);
            }else{
            	try {
            		instrument = Instrument.valueOf(value);
                } catch (IllegalArgumentException e) {}
            }
			fd.setInstrument(instrument);
		}

		start = paramValue.indexOf("offerSide=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 10, end);
			fd.setOfferSide(OfferSide.valueOf(value.toUpperCase()));
		}

		start = paramValue.indexOf("priceRange=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 11, end);
			fd.setPriceRange(PriceRange.valueOf(value));
		}

		start = paramValue.indexOf("reversalAmount=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 15, end);
			fd.setReversalAmount(ReversalAmount.valueOf(value));
		}

		start = paramValue.indexOf("tickBarSize=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 12, end);
			fd.setTickBarSize(TickBarSize.valueOf(value));
		}

		start = paramValue.indexOf("filter=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 7, end);
			fd.setFilter(Filter.valueOf(value));
		}

		start = paramValue.indexOf("timeSession=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 12, end);
			fd.setTimeSession(Period.valueOfToString(value));
		}

		//for compatibility with previous versions
		start = paramValue.indexOf("renkoSession=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 13, end);
			fd.setTimeSession(Period.valueOfToString(value));
		}
		
		start = paramValue.indexOf("creationPoint=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 14, end);
			fd.setCreationPoint(CreationPoint.valueOf(value));
		}

		//for compatibility with previous versions:
		start = paramValue.indexOf("renkoCreationPoint=");
		if(start > 0) {
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 19, end);
			fd.setCreationPoint(CreationPoint.valueOf(value));
		}
		
		start = paramValue.indexOf("interpolationDescriptor=");
		if (start > 0){
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 24, end);
			fd.setDataInterpolationDescriptor(DataInterpolationDescriptor.valueOf(value));
		}

		start = paramValue.indexOf("renkoType=");
		if (start > 0){
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 10, end);
			fd.setRenkoType(RenkoType.valueOf(value));
		}

		start = paramValue.indexOf("calculationMethod=");
		if (start > 0){
			end = paramValue.indexOf(",", start);
			if(end < 0) {
				end = searchEnd;
			}
			String value = paramValue.substring(start + 18, end);
			fd.setCalculationMethod(CalculationMethod.valueOf(value));
		}
		
		return fd;
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

    protected static RenkoType validateRenkoType(RenkoType renkoType){
    	if (renkoType == null){
    		return RenkoType.REGULAR;
    	}
    	return renkoType;
    }

    protected static CalculationMethod validateCalculationMethod(CalculationMethod calculationMethod){
    	if (calculationMethod == null){
    		return CalculationMethod.CLOSE;
    	}
    	return calculationMethod;
    }
    
}
