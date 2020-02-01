/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import com.dukascopy.api.Filter;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFTimeZone;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;

/**
 * Describes indicator input
 * 
 * @author Dmitry Shohov
 */
public class InputParameterInfo {
    /**
     * Type of the input
     * 
     * @author Dmitry Shohov
     */
    public enum Type {
        /**
         * Indicates that this input is price. Price includes open, close, high, low, volume
         */
        PRICE,
        /**
         * Indicates that this input is any double data.
         */
        DOUBLE,
        /**
         * Indicates that this input is IBar array
         */
        BAR
    }

    private String name;
    private Type type;
    private IIndicators.AppliedPrice appliedPrice;
    private OfferSide side;
    private Period period;
    private boolean autoAdjustTimeZone = true;
	private Instrument instrument;
    private Filter filter;    
    private boolean allowIndependentPeriod;                     	

	/**
     * Creates input parameter descriptor without setting any field
     */
    public InputParameterInfo() {
    }

    /**
     * Creates input parameter descriptor and sets all the fields
     * 
     * @param name name of the input
     * @param type type of the input
     */
    public InputParameterInfo(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns name of the input
     * 
     * @return name of the input
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the input
     * 
     * @param name of the input
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns type of the input
     * 
     * @return type of the input
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets type of the input
     * 
     * @param type type of the input
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns default applied price to use
     * 
     * @return default applied price to use
     */
    public IIndicators.AppliedPrice getAppliedPrice() {
        return appliedPrice;
    }

    /**
     * Sets default applied price to use
     * 
     * @param appliedPrice default applied price to use
     */
    public void setAppliedPrice(IIndicators.AppliedPrice appliedPrice) {
        this.appliedPrice = appliedPrice;
    }

    /**
     * Returns side of this input, or null if it was not set
     *
     * @return side of the input
     */
    public OfferSide getOfferSide() {
        return side;
    }

    /**
     * Sets side of this input. This allows indicator to receive prices of the another side in the same time period
     *
     * @param side side of the input
     */
    public void setOfferSide(OfferSide side) {
        this.side = side;
    }

    /**
     * Returns period of this input if it was set, or null if period of the chart should be used
     *
     * @return period of the input
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Returns period of this input (adjusted to given time zone) if it was set, or null if period of the chart should be used
     *
     * @param timeZone time zone
     * @return period of the input
     */
    public Period getPeriod(JFTimeZone timeZone) {
        Period period = this.period;
        if (period != null && autoAdjustTimeZone && Period.isTimeZoneValid(period.getUnit(), period.getNumOfUnits(), timeZone)) {
            period = Period.createCustomPeriod(period.getUnit(), period.getNumOfUnits(), timeZone);
        }
        return period;
    }

    /**
     * Sets period of this input. This allows indicator to receive specific period no matter which period is selected on the chart
     *
     * @param period period of the input
     */
    public void setPeriod(Period period) {
        this.period = period;
    }

    /**
     * Returns instrument of this input, or null if no instrument was set
     *
     * @return instrument of the input
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * Sets instrument of this input. This allows indicator to receive prices of the another instrument in the same time period
     *
     * @param instrument instrument of the input
     */
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Returns filter of this input or null if no filter was set
     *
     * @return filter of the input
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Sets filter of this input. This allows indicator to get filtered prices for this input even if it's added on chart with another filter
     *
     * @param filter filter of the input
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }
    
    /**
     * Returns true if setting of independent periods is allowed for the input
     * 
     * @return true when the setting of independent periods is allowed for the input
     */    
    public boolean isAllowIndependentPeriod() {
		return allowIndependentPeriod;
	}

    /**
     * Allows setting periods for this particular input that differ from main input's period.
     * The type of input must be InputParameterInfo.Type.BAR.
     * Indicator is still responsible for handling all synchronization between inputs.
     * 
     * @param allowIndependentPeriod allow setting independent periods for this particular input
     */    
    public void setAllowIndependentPeriod(boolean allowIndependentPeriod) {
		this.allowIndependentPeriod = allowIndependentPeriod;
	}
    
    /**
     * Returns true if input's time zone gets automatically adjusted to chart's time zone.
     * Set to true by default
     *
     * @return true if input's time zone is automatically adjusted, false otherwise
     */
    public boolean isAutoAdjustTimeZone() {
		return autoAdjustTimeZone;
	}

    /**
     * Enables or disables the automatic adjustment of input period's time zone to chart's time zone (if indicator is on chart).
     * When enabled, input period's time zone is ignored and indicator receives data for the same time zone as chart's time zone
     *
     * @param autoAdjustTimeZone enables/disables the automatic adjustment of input period's time zone
     */
	public void setAutoAdjustTimeZone(boolean autoAdjustTimeZone) {
		this.autoAdjustTimeZone = autoAdjustTimeZone;
	}
}
