package com.dukascopy.api.indicators;

import java.awt.Color;
import java.util.List;

import com.dukascopy.api.IChartObject;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

public interface IIndicatorAppearanceInfo {
	
	/**
	 * Returns indicator name
	 * 
	 * @return indicator name
	 */
    String getName();
    
	/**
	 * Returns indicator name
	 * 
	 * @return indicator name
	 */
    IIndicator getIndicator();

	/**
	 * Returns output colors, one for each output
	 * 
	 * @return output colors, one for each output
	 */
	Color[] getOutputColors();

	/**
	 * Returns output drawing styles, one for each output
	 * 
	 * @return output drawing styles, one for each output
	 */
	DrawingStyle[] getDrawingStyles();

	/**
	 * Returns output line widths, one for each output
	 * 
	 * @return output line widths, one for each output
	 */
	int[] getLineWidths();

	/**
	 * Returns chart objects drawn by the indicator
	 * 
	 * @return output chart objects drawn by the indicator
	 */
	List<IChartObject> getChartObjects();
	
	/**
	 * Returns optional input parameter array
	 * 
	 * @return optional input parameter array
	 */
    Object[] getOptParams();
    
    /**
     * Returns offer sides - one for each output - that get used
     * when indicator gets plotted on the tick chart
     * 
     * @return offer sides - one for each output - that get used
     * when indicator gets plotted on the tick chart
     */
    OfferSide[] getOfferSidesForTicks();
    
    /**
     * Returns applied prices - one for each output - that get used
     * for indicator outputs that use single price input
     * 
     * @return applied prices - one for each output - that get used
     * for indicator outputs that use single price input
     */
    IIndicators.AppliedPrice[] getAppliedPricesForCandles();
}