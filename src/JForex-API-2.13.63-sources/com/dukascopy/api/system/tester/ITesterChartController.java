package com.dukascopy.api.system.tester;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IClientChartController;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;

/**
 * Allows control over the chart object.
 */
public interface ITesterChartController extends IClientChartController {
	
	/**
	 * Changes chart time period. Use for the {@link com.dukascopy.api.DataType#TIME_PERIOD_AGGREGATION}
	 * 
	 * @param dataType - see {@link com.dukascopy.api.DataType}.
	 * @param period - see {@link com.dukascopy.api.Period}.
	 * 
	 * @deprecated
	 * Use {@link com.dukascopy.api.system.tester.ITesterChartController#setFeedDescriptor(IFeedDescriptor)}.
	 */
    @Deprecated
	void changePeriod(DataType dataType, Period period);
	
	/**
	 * Changes chart type.<br>
	 * 
	 * The <b>Instrument</b> and <b>OfferSide</b> of the <b>IFeedDescriptor</b> are ignored.<br>
	 * To change <b>OfferSide</b> use {@link com.dukascopy.api.system.tester.ITesterChartController#switchOfferSide(OfferSide)}.<br>
	 * The <b>Filter</b> value of the <b>IFeedDescriptor</b> is set globally and propagated to all open charts.  
	 * @see #setFilter(Filter)
     * @see #getFilter()
	 * 
	 * @param feedDescriptor - see {@link com.dukascopy.api.feed.IFeedDescriptor}
	 */
	void setFeedDescriptor(IFeedDescriptor feedDescriptor);
	
	/**
	 * Activates chart Auto Shift
	 * 
	 */
	void setChartAutoShift();
	
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
	 * Switches OfferSide
	 * 
	 * @param offerSide new OfferSide
	 */
	void switchOfferSide(final OfferSide offerSide);
	
	/**
	 * Makes the indicator visible or invisible.
	 *  
	 * @param show  true to make the indicator visible; false to make it invisible
	 */
	void showEquityIndicator(boolean show);

    /**
     * Makes the indicator visible or invisible.
     *  
     * @param show  true to make the indicator visible; false to make it invisible
     */
	void showProfitLossIndicator(boolean show);
	
    /**
     * Makes the indicator visible or invisible.
     *  
     * @param show  true to make the indicator visible; false to make it invisible
     */
	void showBalanceIndicator(boolean show);
	
    /**
     * @return the {@link Filter} which is shared by all {@link IChart}s
     */
    Filter getFilter();
    
    /**
     * Sets the {@link Filter} globally which will be propagated to all open charts. 
     * @param filter {@link Filter} to filter flats data
     */
    void setFilter(Filter filter);
}
