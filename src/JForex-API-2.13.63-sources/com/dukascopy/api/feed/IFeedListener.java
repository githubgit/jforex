/*
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.ITimedData;

/**
 * @author Mark Vilkel
 */
public interface IFeedListener {
	
	/**
	 * The method is being called when next feed data arrives
	 * 
	 * @param feedDescriptor feed data descriptor
	 * @param feedData feed data
	 */
	void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData);
	
}
