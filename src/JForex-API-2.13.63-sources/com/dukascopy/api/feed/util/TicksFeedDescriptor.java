/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.FeedDescriptor;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor}
 */
public class TicksFeedDescriptor extends FeedDescriptor {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument instrument
	 */
	public TicksFeedDescriptor(Instrument instrument) {
		setDataType(DataType.TICKS);
		setPeriod(Period.TICK);
		setInstrument(instrument);
	}

}
