/*
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.ITimedData;

/**
 * Feed listener which works only with a certain feed type
 *
 * @param <T> type of feed data element
 */
public interface ITailoredFinancialFeedListener<T extends ITimedData> {
	
	/**
	 * The method is being called when next feed data arrives
	 * 
	 * @param feedInfo feed data descriptor
	 * @param feedData data of specified type
	 */
	void onFeedData(ITailoredFeedInfo<T> feedInfo, T feedData);
}
