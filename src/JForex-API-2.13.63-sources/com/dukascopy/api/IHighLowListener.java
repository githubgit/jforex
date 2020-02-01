/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Listens to the maximum and minimum prices for corresponding {@link Instrument instrument}
 * @author Mark Vilkel
 */
public interface IHighLowListener {

	/**
	 * The method is called when another the best high price (maximal) 'arrives' in the system
	 * 
	 * @param instrument Instrument of updated price
	 * @param high new best high price
	 */
	void highUpdated(Instrument instrument,	double high);

	/**
	 * The method is called when another the best low price (minimal) 'arrives' in the system
	 * 
	 * @param instrument Instrument of updated price
	 * @param low new best low price
	 */
	void lowUpdated(Instrument instrument, double low);
}
