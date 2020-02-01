/*
 * Copyright Nov 5, 2015 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.instrument;

import java.util.Set;

import com.dukascopy.api.Instrument;

/**
 * <h3><b>NOTE:</b> Under development</h3>
 * @author Dukascopy Bank SA
 */
public interface IFinancialInstrumentProvider {

	Set<IFinancialInstrument> 	getFinancialInstruments();
	Set<Instrument>             getExoticInstruments();
	
	IInstrumentGroup 			getGroup(String name);
	Set<IInstrumentGroup> 	  	getGroups();
	
	/**
     * @param instrumentAsString string in "CUR1/CUR2" format
     * @return corresponding Instrument or null if no Instrument was found for specified string
	 * @deprecated use {@link com.dukascopy.api.Instrument#fromString(String instrumentAsString)}
	 */
	@Deprecated
	Instrument 	   	            getInstrument(String instrumentAsString);
	@Deprecated
	IFinancialInstrument 		getFinancialInstrument(String instrumentAsString);
	@Deprecated
	IFinancialInstrument 		getFinancialInstrument(Instrument instrument);
	@Deprecated
	Instrument 		            getInstrument(IFinancialInstrument financialInstrument);
}
