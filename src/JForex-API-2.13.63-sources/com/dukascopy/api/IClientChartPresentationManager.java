/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.chart.IChartTheme;
import com.dukascopy.api.feed.IFeedDescriptor;

/**
 * Manages Chart presentation modes ({@link Period}, {@link DataType}, etc.).<br>
 * <b>NOTE: </b>Used only by <b>Standalone JForex API</b>.
 * @see IChart
 * @see IClientGUI
 * @author aburenin
 */
public interface IClientChartPresentationManager {
	
	/**
	 * Changes bundle of chart's feed data properties at once.<br>
	 * 
	 * The <b>Filter</b> value of the <b>IFeedDescriptor</b> is set globally and propagated to all open charts.  
	 * @see #setFilter(Filter)
     * @see #getFilter()
     * @see #setInstrument(Instrument)
     * @see #switchOfferSide(OfferSide)
	 * 
	 * @param feedDescriptor - see {@link com.dukascopy.api.feed.IFeedDescriptor}
	 */
	void setFeedDescriptor(IFeedDescriptor feedDescriptor);
	
	/**
     * Returns chart state described by bean {@link IFeedDescriptor}
     * @return chart state described by bean {@link IFeedDescriptor}
     */
    IFeedDescriptor getFeedDescriptor();

	/**
	 * Activates chart Auto Shift
	 * 
	 */
	void setChartAutoShift();
	
	/**
	 * returns true if chart Auto Shift is activated 
	 * @return true if chart Auto Shift is activated 
	 */
	boolean isChartAutoShift();
	
	/**
	 * Zooms in chart
	 * 
	 */
	void zoomIn();
	
	/**
	 * Zooms out chart
	 * 
	 */
	void zoomOut();
	
	/**
	 * Changes current {@link Instrument} to specified one
	 * @param instrument new active {@link Instrument} 
	 */
	void setInstrument(Instrument instrument);
	
	/**
	 * Applies DataPresentationType to current chart.
	 * Use {@link IChart#getDataType()}.getSupportedPresentationTypes() to get all allowed values.
	 * Use {@link IChart#getDataType()}.isPresentationTypeSupported(DataPresentationType presentationType)
	 * to check whether current DataType supports presentationType or not.
	 * 
	 * @param dataPresentationType one of DataPresentationType constants, supported by current DataType
	 * @see IChart#setDataPresentationType(com.dukascopy.api.DataType.DataPresentationType)
	 * @see IChart#getDataType()
	 * @throws IllegalArgumentException if DataPresentationType is not supported by current DataType
	 */
	void setDataPresentationType(DataPresentationType dataPresentationType);
	
	/**
	 * Returns charts' current {@link DataPresentationType}.
	 * Depends on current {@link DataType} value
     * @return data presentation type of chart
	 * @see IChart#getDataPresentationType()
	 * @see IChart#getDataType()
	 */
	DataPresentationType getDataPresentationType();
	
	/**
	 * Switches OfferSide
	 * 
	 * @param offerSide new offer side
	 */
	void switchOfferSide(final OfferSide offerSide);
	
    /**
     * Sets the {@link Filter} globally which will be propagated to all open charts. 
     * @param filter {@link Filter} to filter flats data
     */
    void setFilter(Filter filter);

    /**
     * @return the {@link Filter} which is shared by all {@link IChart}s
     */
    Filter getFilter();
    
    /**
     * Returns current chart theme. If the theme is among IChartTheme.Predefined, then a copy of the theme gets returned
     * 
     * @return the current chart theme
     */
    IChartTheme getTheme();
    
    /**
     * Sets chart theme
     * 
     * @param chartTheme theme to set
     */
    void setTheme(IChartTheme chartTheme);

    /**
     * Gets a copy of a predefined chart theme
     * 
     * @param predefinedTheme theme enumeration
     * @return a copy of a predefined chart theme
     */
	IChartTheme getPredefinedTheme(IChartTheme.Predefined predefinedTheme);
}
