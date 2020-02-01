/*
 * Copyright 1998-2011 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.instrument;

import java.util.Currency;

import com.dukascopy.api.ICurrency;

/**
 * The IFinancialInstrument instrument holds information about 
 * a particular financial instrument.
 */
public interface IFinancialInstrument {

    enum Type {
        FOREX, CFD, METAL
    }

    /**
     * Instrument's type
     *
     * @return instrument's type
     */
    Type getType();

	/**
	 * Returns the instrument's group
	 * 
	 * @return the instrument's group or null for predefined instrument which is not available on current trader's account.
	 */
    IInstrumentGroup getGroup();

	/**
	 * Returns the description of the instrument
	 * 
	 * @return the description of the instrument or null for predefined instrument which is not available on current trader's account.
	 */
    String getDescription();

	/**
	 * Returns the name of the instrument
	 * 
	 * @return the name of the instrument
	 */
    String getName();

    /**
     * Returns primary currency of the instrument
     * 
     * @return primary currency of the instrument
     */
    ICurrency getPrimaryJFCurrency();

    /**
     * Returns secondary currency of the instrument
     * 
     * @return secondary currency of the instrument
     */
    ICurrency getSecondaryJFCurrency();

    /**
     * Returns value of one pip of the instrument
     * 
     * @return value of one pip of the instrument
     */
    double getPipValue();

    /**
     * Returns decimal place count of one pip of the instrument
     * 
     * @return decimal place count of one pip of the instrument
     */
    int getPipScale();

    /**
     * Returns decimal place count of instrument's tick size
     *
     * @return decimal place count of instrument's tick size
     */
    int getTickScale();


    /**
     * Returns minimal trade amount in contracts.
     *
     * @return minimal trade amount in contracts
     */
    double getMinTradeAmount();
    double getTradeAmountIncrement();
    double getAmountPerContract();

    double getLeverageUse();

	String getCountry();
	
	boolean isExotic();

	
	/**
	 * Temporary added for backward compatibility, will be removed in future releases
     * @return primary currency
	 * @deprecated use {@link #getPrimaryJFCurrency()}
	 */
	@Deprecated
	Currency getPrimaryCurrency();
	/** 
	 * Temporary added for backward compatibility, will be removed in future releases
     * @return secondary currency
	 * @deprecated use {@link #getSecondaryJFCurrency()}
	 */
	@Deprecated
	Currency getSecondaryCurrency();
}
