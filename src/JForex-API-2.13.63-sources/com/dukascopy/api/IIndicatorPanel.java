/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.awt.Color;
import java.util.List;

import com.dukascopy.api.chart.mouse.IChartPanelMouseListener;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorAppearanceInfo;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * @author Aleksandrs.Leiferovs
 */
public interface IIndicatorPanel extends IChartPanel {

    /**
     * {@inheritDoc}
     * <br>Note: global chart objects (e.g. Time Marker) are not allowed on IIndicatorPanel and will cause {@link IllegalArgumentException}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    void add(IChartObject chartObject);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    void remove(IChartObject chartObject);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartObject remove(String chartObjectKey);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    void removeAll();

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartObject get(String chartObjectKey);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    List<IChartObject> getAll();

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide, Period basePeriod);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod, OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod,
            Color[] outputColors,DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide,
            OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide, Period basePeriod,
            OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    void removeIndicator(IIndicator indicator);

    /**
     * {@inheritDoc}
     * <br>Returns only indicators applied to current panel.
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    List<IIndicator> getIndicators();

    /**
     * {@inheritDoc}
     * <br>Returns only indicators applied to current panel.
     */
    @Override
    List<IIndicatorAppearanceInfo> getIndicatorApperanceInfos();

    /**
     * Returns minimal value of base indicator placed on this panel
     *
     * @return minimal value of the vertical scale
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    double getMinPrice();

    /**
     * Returns maximal value of base indicator placed on this panel
     *
     * @return maximal value of the vertical scale
     * @throws IllegalStateException if this panel is not visible {@link #isActive()}
     */
    @Override
    double getMaxPrice();

    /**
     * {@inheritDoc}
     */
    @Override
    void addMouseListener(boolean asynchronous, IChartPanelMouseListener chartMouseListener);

    /**
     * {@inheritDoc}
     */
    @Override
    void removeMouseListener(IChartPanelMouseListener chartMouseListener);

    /**
     * {@inheritDoc}
     */
    @Override
    List<IChartPanelMouseListener> getMouseListeners();
    
    /**
     * Indicates this panel visibility on chart.
     * <p>Note:
     * <br>- working with removed/invisible panel is not allowed.
     * <br>- panel could be automatically removed with last indicator on it. 
     * 
     * @return true if visible, false if not
     */
    boolean isActive();
}
