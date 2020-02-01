/*
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.ITimedData;

/**
 * @author Kaspars Rinkevics
 * @deprecated use {@link IFeedListener}
 */
@Deprecated
public interface IFinancialFeedListener {
	
	/**
	 * The method is being called when next feed data arrives
	 * 
	 * @param feedInfo feed data descriptor
	 * @param feedData feed data
	 */
	void onFeedData(IFeedInfo feedInfo, ITimedData feedData);
	
}
