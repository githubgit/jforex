/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Notification message about instrument's tradability changes
 * 
 * @author viktor.sjubajev
 */
public interface IInstrumentStatusMessage extends IMessage {

	/**
	 * Returns instrument which tradability was changed
	 * 
	 * @return instrument as {@link Instrument}
	 */
	Instrument getInstrument();

	/**
	 * Returns instrument's tradability state
	 * 
	 * @return true if instrument is tradable
	 */
	boolean isTradable();
}