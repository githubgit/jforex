/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import com.dukascopy.api.system.IClient;

/**
 * Interface for listeners which are "interested" in open / close charts from strategy   
 * <b>NOTE: </b>Used only by <b>JForex SDK</b>.
 * @see IContext#openChart(com.dukascopy.api.feed.IFeedDescriptor)
 * @see IContext#closeChart(IChart)
 * @see IClient#addClientGUIListener(IClientGUIListener)
 * @see IClient#removeClientGUIListener(IClientGUIListener)
 * 
 * @author aburenin
 */
public interface IClientGUIListener {
	
	/**
	 * Triggered on {@link IContext#openChart(com.dukascopy.api.feed.IFeedDescriptor)}
	 * @param clientGUI {@link IClientGUI} data
	 */
	void onOpenChart(IClientGUI clientGUI);
	
	/**
	 * Triggered on {@link IContext#closeChart(IChart)}
	 * @param chart {@link IChart} 
	 */
	void onCloseChart(IChart chart);
}
