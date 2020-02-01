/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Listens to the new loaded {@link IReportPosition}
 * @see IReportService
 * @author andrej.burenin
 */
public interface ILoadingReportPositionListener {
	
	/**
	 * Called on new loaded {@link IReportPosition}
	 * @param reportPosition loaded {@link IReportPosition}
	 */
	void newReportPosition(IReportPosition reportPosition);
}
