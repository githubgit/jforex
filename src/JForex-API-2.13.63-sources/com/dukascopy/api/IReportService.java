/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.List;

/**
 * Provide access to consolidated trading activity reports:
 * <ul>
 * <li><a href="http://www.dukascopy.com/wiki/#Position_Report">The position report consolidates the trading activity reporting by grouping open and close orders</a>
 * <li>...
 * </ul>
 * @author andrej.burenin
 */
public interface IReportService {

	/**
	 * Returns a list of all open positions
	 * @return a list of all open positions
	 * @throws JFException in case of any system error
	 */
	List<IReportPosition> getOpenPositions() throws JFException;
	
	/**
	 * Loads all open positions from the server in the background. Method returns fast after creating request for data not waiting
	 * for any data to be loaded. After internal request is sent, open positions will be returned by calling method 
	 * in {@link ILoadingReportPositionListener}. {@link LoadingProgressListener} is used to receive feedback about loading progress,
	 * to cancel loading and its method {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading
	 * is finished or as a result of error. This method can be used for open positions loading without blocking strategy execution
     * @param reportPositionListener receives data about requested open positions
     * @param loadingProgress used to control loading progress
     * @throws JFException in case of any system error
	 */
	void readOpenPositions(ILoadingReportPositionListener reportPositionListener, LoadingProgressListener loadingProgress) throws JFException;

	/**
     * Returns a list of closed positions for specified:
     * @param from start of the time interval for which closed positions should be loaded
     * @param to end time of the time interval for which closed positions should be loaded
     * @return a list of closed positions (never null).<br>
     * @throws JFException in case of any system error
	 */
	List<IReportPosition> getClosedPositions(long from, long to) throws JFException;
	
	/**
     * Loads closed positions from the server in the background. Method returns fast after creating request for data not waiting for any data
     * to be loaded. After internal request is sent, closed positions will be returned by calling method in {@link ILoadingReportPositionListener}.
     * {@link LoadingProgressListener} is used to receive feedback about loading progress, to cancel loading and its method.
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error.
     * This method can be used for positions loading without blocking strategy execution
     * @param from start of the time interval for which closed positions should be loaded
     * @param to end time of the time interval for which closed positions should be loaded
     * @param reportPositionListener receives data about requested closed positions
     * @param loadingProgress used to control loading progress
     * @throws JFException in case of any system error
	 */
	void readClosedPositions(long from, long to, ILoadingReportPositionListener reportPositionListener, LoadingProgressListener loadingProgress) throws JFException;
}
