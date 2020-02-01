/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.awt.Color;
import java.util.List;

import com.dukascopy.api.IIndicators.AppliedPrice;
import com.dukascopy.api.chart.mouse.IChartPanelMouseListener;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorAppearanceInfo;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Chart panel container for objects of corresponding type.
 * @author Aleksandrs.Leiferovs
 */
public interface IChartPanel {
    
    /**
     * Adds object of <code>IChartObject</code> type to current panel. 
     * Please note that only <b>one</b> instance of object can be added per chart.
     * 
     * @param chartObject - instance of <code>IChartObject</code> super type.
     * 
     * @throws IllegalArgumentException - if <code>object</code> is already present on chart.
     */
    void add(IChartObject chartObject);

    /**
     * Adds indicator to the current panel.
     * 
     * @param indicator as <code>IIndicator</code>
     * 
     * @return IChartPanel instance where indicator is put on.
     * 
     * @throws IllegalStateException trying to apply to main panel indicator designed for sub panel.
     */
    IChartPanel add(IIndicator indicator);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values
     * If optParams equals to null - default ones will be taken instead
     * 
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * 
     * @return IChartPanel instance where indicator is put on.
     * 
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values
     * If optParams equals to null - default ones will be taken instead
     * If basePeriod equals to null - no base period will be used
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param basePeriod base period for unstable period indicators
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod);
    
    /**
     * Adds indicator to the current panel by specifying initial optional parameters values
     * and indicator advanced settings
     * If optParams equals to null - default ones will be taken instead
     * If instrument, period or offerSide equals to null - values from chart will be taken instead
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param instrument as <code>Instrument</code>
     * @param period as <code>Period</code>
     * @param offerSide as <code>OfferSide</code>
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values
     * and indicator advanced settings
     * If optParams equals to null - default ones will be taken instead
     * If instrument, period or offerSide equals to null - values from chart will be taken instead
     * If basePeriod equals to null - no base period will be used
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param instrument as <code>Instrument</code>
     * @param period as <code>Period</code>
     * @param offerSide as <code>OfferSide</code>
     * @param basePeriod base period for unstable period indicators
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide, Period basePeriod);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values
     * and indicator advanced settings
     * If optParams equals to null - default ones will be taken instead
     * If instrument, period or offerSide equals to null - values from chart will be taken instead
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param financialInstrument as <code>IFinancialInstrument</code>
     * @param period as <code>Period</code>
     * @param offerSide as <code>OfferSide</code>
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    @Deprecated
    IChartPanel add(IIndicator indicator, Object[] optParams, IFinancialInstrument financialInstrument, Period period, OfferSide offerSide);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values
     * and data sides/types for each input
     * If optParams equals to null - default ones will be taken instead
     * If offerSides or appliedPrices equals to null - default ones will be taken instead
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param offerSides as <code>OfferSide[]</code>
     * @param appliedPrices as <code>AppliedPrice[]</code>
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, OfferSide[] offerSides, AppliedPrice[] appliedPrices);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values
     * and data sides/types for each input
     * If optParams equals to null - default ones will be taken instead
     * If basePeriod equals to null - no base period will be used
     * If offerSides or appliedPrices equals to null - default ones will be taken instead
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param basePeriod base period for unstable period indicators
     * @param offerSides as <code>OfferSide[]</code>
     * @param appliedPrices as <code>AppliedPrice[]</code>
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod, OfferSide[] offerSides, AppliedPrice[] appliedPrices);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values, curves colors, drawing styles and line widths
     * If optParams equals to null - default ones will be taken instead
     * If output params are nulls - default ones will be taken instead
     * 
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param outputColors as <code>Color[]</code>
     * @param outputDrawingStyles as <code>DrawingStyle[]</code>
     * @param outputWidths as <code>int[]</code>
     * 
     * @return IChartPanel instance where indicator is put on.
     * 
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values, curves colors, drawing styles and line widths
     * If optParams equals to null - default ones will be taken instead
     * If basePeriod equals to null - no base period will be used
     * If output params are nulls - default ones will be taken instead
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param basePeriod base period for unstable period indicators
     * @param outputColors as <code>Color[]</code>
     * @param outputDrawingStyles as <code>DrawingStyle[]</code>
     * @param outputWidths as <code>int[]</code>
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values,
     * indicator advanced settings, data sides/types for each input, output params
     * If optParams equals to null - default ones will be taken instead
     * If instrument, period or offerSide equals to null - values from chart will be taken instead
     * If offerSides or appliedPrices equals to null - default ones will be taken instead
     * If output params are nulls - default ones will be taken instead
     * 
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param instrument as <code>Instrument</code>
     * @param period as <code>Period</code>
     * @param offerSide as <code>OfferSide</code>
     * @param offerSides as <code>OfferSide[]</code>
     * @param appliedPrices as <code>AppliedPrice[]</code>
     * @param outputColors as <code>Color[]</code>
     * @param outputDrawingStyles as <code>DrawingStyle[]</code>
     * @param outputWidths as <code>int[]</code>
     * 
     * @return IChartPanel instance where indicator is put on.
     * 
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide,
            OfferSide[] offerSides, AppliedPrice[] appliedPrices, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values,
     * indicator advanced settings, data sides/types for each input, output params
     * If optParams equals to null - default ones will be taken instead
     * If instrument, period or offerSide equals to null - values from chart will be taken instead
     * If basePeriod equals to null - no base period will be used
     * If offerSides or appliedPrices equals to null - default ones will be taken instead
     * If output params are nulls - default ones will be taken instead
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param instrument as <code>Instrument</code>
     * @param period as <code>Period</code>
     * @param offerSide as <code>OfferSide</code>
     * @param basePeriod base period for unstable period indicators
     * @param offerSides as <code>OfferSide[]</code>
     * @param appliedPrices as <code>AppliedPrice[]</code>
     * @param outputColors as <code>Color[]</code>
     * @param outputDrawingStyles as <code>DrawingStyle[]</code>
     * @param outputWidths as <code>int[]</code>
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide, Period basePeriod,
            OfferSide[] offerSides, AppliedPrice[] appliedPrices, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * Adds indicator to the current panel by specifying initial optional parameters values,
     * indicator advanced settings, data sides/types for each input, output params
     * If optParams equals to null - default ones will be taken instead
     * If financialInstrument, period or offerSide equals to null - values from chart will be taken instead
     * If offerSides or appliedPrices equals to null - default ones will be taken instead
     * If output params are nulls - default ones will be taken instead
     *
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param financialInstrument as <code>IFinancialInstrument</code>
     * @param period as <code>Period</code>
     * @param offerSide as <code>OfferSide</code>
     * @param offerSides as <code>OfferSide[]</code>
     * @param appliedPrices as <code>AppliedPrice[]</code>
     * @param outputColors as <code>Color[]</code>
     * @param outputDrawingStyles as <code>DrawingStyle[]</code>
     * @param outputWidths as <code>int[]</code>
     *
     * @return IChartPanel instance where indicator is put on.
     *
     * @throws IllegalStateException trying to apply indicator designed for main panel to sub panel and vice versa.
     */
    @Deprecated
    IChartPanel add(IIndicator indicator, Object[] optParams, IFinancialInstrument financialInstrument, Period period, OfferSide offerSide,
                    OfferSide[] offerSides, AppliedPrice[] appliedPrices, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * Returns all indicators on the chart panel.
     * 
     * @return list of indicators
     */
    List<IIndicator> getIndicators();  
    
    /**
     * Returns indicator appearance information on the chart panel
     * 
     * @return indicator appearance information on the chart panel
     */
    List<IIndicatorAppearanceInfo> getIndicatorApperanceInfos();
    
    /**
     * Returns <code>IChartObject</code> instance by key if any exist on this panel.
     * 
     * @param chartObjectKey unique id
     * @return IChartObject or null if no object was found by specified key
     */
    IChartObject get(String chartObjectKey);
    
    /**
     * Returns all graphical objects on this panel.
     * 
     * @return list of all graphical objects on the chart
     */
    List<IChartObject> getAll();
    
    /**
     * Returns minimal value of price scale
     * 
     * @return minimal value of the vertical scale
     */
    double getMinPrice();
    
    /**
     * Returns maximal value of price scale
     * 
     * @return maximal value of the vertical scale
     */
    double getMaxPrice();
    
    /**
     * Remove specified indicator from this panel.
     * 
     * @param indicator instance of <code>IIndicator</code> interface
     */
    void removeIndicator(IIndicator indicator);
    
    /**
     * Deletes graphical object from this panel by key.
     * 
     * @param chartObjectKey key of object to remove
     * @return deleted chart object or null if specified key was not found
     */
    IChartObject remove(String chartObjectKey);
    
    /**
     * Deletes specified graphical object from this panel.
     * 
     * @param chartObject object to remove
     * @exception NullPointerException if chartObject is null
     */
    void remove(IChartObject chartObject);
    
    /**
     * Removes all graphical objects from this panel.
     * 
     */
    void removeAll();
    
    /**
     * Adds the specified mouse listener to receive mouse events from
     * this chart panel.
     * If <code>chartMouseListener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     * 
     * @param asynchronous if true listener will be triggered in AWT thread asynchronously with strategy thread, otherwise will be synchronized. Use false as default if not sure.
     * @param chartMouseListener the mouse listener
     * @see #removeMouseListener
     * @see #getMouseListeners
     */
    void addMouseListener(boolean asynchronous, IChartPanelMouseListener chartMouseListener);
    
    /**
     * Removes the specified mouse listener so that it no longer
     * receives mouse events from this component. This method performs
     * no function, nor does it throw an exception, if the listener
     * specified by the argument was not previously added to this component.
     * If <code>chartMouseListener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     * 
     * @param chartMouseListener the mouse listener
     * @see #addMouseListener
     * @see #getMouseListeners
     */
    void removeMouseListener(IChartPanelMouseListener chartMouseListener);
    
    /**
     * Returns an array of all the mouse listeners
     * registered on this component.
     *
     * @return all of this component's <code>MouseListener</code>s
     *         or an empty array if no mouse
     *         listeners are currently registered
     * 
     * @see #addMouseListener
     * @see #removeMouseListener
     */
    List<IChartPanelMouseListener> getMouseListeners();
}
