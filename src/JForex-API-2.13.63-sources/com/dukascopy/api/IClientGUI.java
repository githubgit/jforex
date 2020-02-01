/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import javax.swing.JPanel;

/**
 * Provides access to specific {@link IChart} and its {@link IClientChartPresentationManager}.<br>
 * <b>NOTE:</b> Used only by <b>Standalone JForex API</b>.<br>
 * TODO: to be vetted.
 * @author aburenin
 */
public interface IClientGUI {
	
	/**
	 * @return {@link #getChart()}'s id
	 */
	int getChartId();
	
	/**
	 * @return specific {@link IChart}
	 */
	IChart getChart();
	
	/**
	 * @return {@link #getChart()}'s appropriate {@link JPanel}
	 */
	JPanel getChartPanel();
	
	/**
	 * @return  {@link #getChart()}'s appropriate {@link IClientChartPresentationManager}
	 */
	IClientChartPresentationManager getChartPresentationManager();
	
	/**
	 * @return {@link #getChart()}'s appropriate {@link IClientChartController}
	 */
	IClientChartController getClientChartController();
}
