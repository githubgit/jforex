/*
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;

/**
 * @author Mark Vilkel
 */
public interface ITickFeedListener {
	
	/**
	 * The method is being called when next Tick arrives
	 * 
	 * @param instrument instrument
	 * @param tick tick
	 */
	void onTick(Instrument instrument, ITick tick);

}
