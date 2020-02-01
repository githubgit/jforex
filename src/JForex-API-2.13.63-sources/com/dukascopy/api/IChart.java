/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DataType.DataPresentationType;
import com.dukascopy.api.chart.IChartTheme;
import com.dukascopy.api.chart.mouse.IChartPanelMouseListener;
import com.dukascopy.api.drawings.IChartObjectFactory;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorAppearanceInfo;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * Allows to draw graphical objects on the chart
 *
 * @author Denis Larka
 */
public interface IChart extends Iterable<IChartObject>, IChartPanel {

    /**
     * Type of the graphical object
     */
    enum Type {
        /**
         * Vertical line
         */
        VLINE,
        /**
         * Horizontal line
         */
        HLINE,

        /**
         * @deprecated use PRICEMARKER instead
         */
        @Deprecated
        ORDER_LINE,

        /**
         * Entry order line
         */
        ENTRY_ORDER,
        /**
         * Stop loss line
         */
        STOP_LOSS,
        /**
         * Take profit line
         */
        TAKE_PROFIT,

        /**
         * Vertical line with time text
         */
        TIMEMARKER,
        
        /**
         * Horizontally resizable rectangle between two time points
         */
        TIMERANGE,
        
        /**
         * Horizontal line with price text
         */
        PRICEMARKER,

        /**
         * Multiple line between many points
         */
        POLY_LINE,

        /**
         * Arbitrary set of shapes
         */
        PENCIL,

        /**
         * Short line between two points
         */
        SHORT_LINE,
        /**
         * Long line drawn through two points
         */
        LONG_LINE,
        /**
         * Ray line drawn through two points. Finite in one direction and infinite in the other.
         */
        RAY_LINE,

        /**
         * @deprecated isn't supported
         */
        @Deprecated
        TREND,
        /**
         * Trend line by angle
         */
        TRENDBYANGLE,

        /**
         * Fibonacci channel lines
         */
        FIBOCHANNEL,
        /**
         * Linear regression
         */
        REGRESSION,
        /**
         * Equidistant channel lines
         */
        CHANNEL,
        
        /**
         * Standard deviation
         */
        STDDEVCHANNEL,

        /**
         * Gann line
         */
        GANNLINE,
        /**
         * Gann fan
         */
        GANNFAN,
        /**
         * Gann grid
         */
        GANNGRID,

        /**
         * Retracement
         */
        FIBO,
        /**
         * Time zones
         */
        FIBOTIMES,
        /**
         * Time zones percents
         */
        FIBOTIMES_PERCENTS,
        /**
         * Fan
         */
        FIBOFAN,
        /**
         * Arcs
         */
        FIBOARC,
        /**
         * Circles
         */
        FIBOCIRCLES,
        /**
         * Expansion
         */
        EXPANSION,

        /**
         * Rectangle
         */
        RECTANGLE,
        /**
         * Triangle
         */
        TRIANGLE,
        /**
         * Ellipse
         */
        ELLIPSE,

        /**
         * Andrew's pitchfork
         */
        PITCHFORK,
        /**
         * Cycle lines (periods)
         */
        CYCLES,
        /**
         * Percent (horizontal)
         */
        PERCENT,

        /**
         * Text
         */
        TEXT,
        /**
         * Text label
         */
        LABEL,
        /**
         * Label attached to the corner of chart
         */
        SCREEN_LABEL,
        
        /**
         * Arrow up
         */
        SIGNAL_UP,
        /**
         * Arrow down
         */
        SIGNAL_DOWN,
        
        /**
         * Candlestick - custom candle
         */
        CANDLESTICK,

        /**
         * Volume Profile - most traded prices for a particular time period
         */
        VOLUME_PROFILE,

        /**
         * Widget displaying OHLC data
         */
        OHLC_INFORMER,
        
        /**
         * Widget for searching and displaying patterns on chart
         */
        PATTERN_WIDGET,
        
        /**
         * Widget displaying user data
         */
        CUSTOM_CHART_WIDGED,
    }

    /**
     * Draws graphical object that requires up to 3 points
     * 
     * @deprecated - this method is deprecated. please use <code>IChartObjectFactory</code> <b>create()</b> method() instead.
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @param time3 time of third point
     * @param price3 price of third point
     * @return graphical object 
     */
    @Deprecated
    IChartObject draw(String key, Type type, long time1, double price1, long time2, double price2, long time3, double price3);

    /**
     * Draws graphical object that requires up to 2 points
     * 
     * @deprecated - this method is deprecated. please use <code>IChartObjectFactory</code> <b>create()</b> method() instead.
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @return graphical object 
     */
    @Deprecated
    IChartObject draw(String key, Type type, long time1, double price1, long time2, double price2);

    /**
     * Draws graphical object that requires only 1 point

     * @deprecated - this method is deprecated. please use <code>IChartObjectFactory</code> <b>create()</b> method() instead.
     * 
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of the point
     * @param price1 price of the point
     * @return graphical object
     */
    @Deprecated
    IChartObject draw(String key, Type type, long time1, double price1);

    /**
     * @deprecated - this method is deprecated. please use <code>addToMainChartUnlocked()</code> method instead.
     * Draws graphical object that requires up to 3 points. Object can be selected, moved and changed by the user
     *
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @param time3 time of third point
     * @param price3 price of third point
     * @return graphical object
     */
    @Deprecated
    IChartObject drawUnlocked(String key, Type type, long time1, double price1, long time2, double price2, long time3, double price3);

    /**
     * @deprecated - this method is deprecated. please use <code>addToMainChartUnlocked()</code> method instead.
     * Draws graphical object that requires up to 2 points. Object can be selected, moved and changed by the user
     *
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of first point
     * @param price1 price of first point
     * @param time2 time of second point
     * @param price2 price of second point
     * @return graphical object
     */
    @Deprecated
    IChartObject drawUnlocked(String key, Type type, long time1, double price1, long time2, double price2);

    /**
     * Draws graphical object that requires only 1 point. Object can be selected, moved and changed by the user
     * @deprecated - this method is deprecated. please use <code>addToMainChartUnlocked()</code> method instead.
     * @param key unique id of the object
     * @param type type of the object
     * @param time1 time of the point
     * @param price1 price of the point
     * @return graphical object
     */
    @Deprecated
    IChartObject drawUnlocked(String key, Type type, long time1, double price1);

    /**
     * Moves graphical object to new coordinates.
     * Do not initiate chart repaint immediately, use IChart.repaint() method if necessary.
     *
     * @param objectToMove chart object to move
     * @param newTime new time coordinate
     * @param newPrice new price coordinate
     */
    void move(IChartObject objectToMove, long newTime, double newPrice);

    /**
     * Moves graphical object to new coordinates.
     * Do not initiate chart repaint immediately, use IChart.repaint() method if necessary.
     *
     * @param chartObjectKey key of the chart object to move
     * @param newTime new time coordinate
     * @param newPrice new price coordinate
     */
    void move(String chartObjectKey, long newTime, double newPrice);

    /**
     * Writes a comment in the upper left corner. Line is
     * split by new line characters
     *
     * @param comment string to display
     */
    void comment(String comment);

    /**
     * Sets the horizontal position of the comment's text.
     *
     * @param position  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code>
     * @exception IllegalArgumentException if position has incorrect value
     *
     * @see #getCommentHorizontalPosition()
     * @see javax.swing.SwingConstants
     */
    void setCommentHorizontalPosition(int position);

    /**
     * Returns the horizontal position of the comment's text.
     *
     * @return   One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>, 
     *           <code>RIGHT</code>.
     * 
     * @see #setCommentHorizontalPosition(int)
     */
    int getCommentHorizontalPosition();

    /**
     * Sets the vertical position of the comment's text.
     *
     * @param position  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code>,
     *           <code>BOTTOM</code>
     * @exception IllegalArgumentException if position has incorrect value
     *
     * @see #getCommentVerticalPosition()
     * @see javax.swing.SwingConstants
     */    
    void setCommentVerticalPosition(int position);

    /**
     * Returns the vertical position of the comment's text.
     *
     * @return   One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code>, 
     *           <code>BOTTOM</code>.
     *
     * @see #setCommentVerticalPosition(int)
     */    
    int getCommentVerticalPosition();

    /**
     * Sets the comment's font.
     *
     * @param font the desired <code>Font</code> for comment
     *
     * @see java.awt.Component#getFont()
     */
    void setCommentFont(Font font);

    /**
     * Gets the comment's font.
     * @return comment's font; if a font has not been set for comment,
     * then the font defined in chart's theme for drawings is returned
     *
     * @see #setCommentFont(Font)
     */
    Font getCommentFont();

    /**
     * Sets the foreground color of comment.
     * @param color the color to become comment's foreground color
     * if this parameter is <code>null</code>, then used default text font defined in chart's theme
     *
     * @see #getCommentColor()
     */
    void setCommentColor(Color color);

    /**
     * Gets the foreground color of comment.
     * @return comments's foreground color
     * if comment does not have a foreground color, then used default text font defined in chart's theme
     *
     * @see #setCommentColor(Color) 
     */
    Color getCommentColor();
    
    /**
     * {@inheritDoc}
     */
    @Override
    IChartObject get(String key);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartObject remove(String key);
    
    /**
     * {@inheritDoc}
     */
    @Override
    void remove(IChartObject chartObject);

    /**
     * Deletes graphical objects
     * 
     * @param chartObjects list of graphical objects to remove
     * @return list of all graphical objects that were actually removed from chart
     */
    List<IChartObject> remove(List<IChartObject> chartObjects);
    
    /**
     * {@inheritDoc}
     */
    @Override
    List<IChartObject> getAll();

    /**
     * {@inheritDoc}
     */
    @Override
    void removeAll();
    
    /**
     * Returns number of the graphical objects on the chart
     * 
     * @return number of the graphical objects on the chart
     */
    int size();

    /**
     * Returns minimal value of the vertical scale.
     * 
     * @param index of the (sub-)window
     * @return minimal value of the vertical scale
     * @deprecated use {@link IChartPanel#getMinPrice()}
     */
    @Deprecated
    double priceMin(int index);

    /**
     * Returns maximal value of the vertical scale.
     * 
     * @param index of the (sub-)window
     * @return maximal value of the vertical scale
     * @deprecated use {@link IChartPanel#getMaxPrice()}
     */
    @Deprecated
    double priceMax(int index);
    
    /**
     * Sets minimal and maximal value of the vertical scale of
     * the current chart when it is maximally expanded.
     * Note: automatically switches off chart price range autoscaling.
     * @param minPriceValue minimal value of the vertical scale
     * @param maxPriceValue maximal value of the vertical scale
     */
    void setVerticalAxisScale(double minPriceValue, double maxPriceValue);

    /**
     * Switches on/off chart vertical autoscale mode.
     * When autoscale switched off chart is draggable vertically
     * @param autoscale vertical autoscale
     */
    void setVerticalAutoscale(boolean autoscale);
    
    /**
     * Returns amount of bars visible on the screen
     * @return amount of bars visible on the screen
     */
    int getBarsCount();

    /**
     * Returns count of indicator windows on the chart (including main chart).
     * @return total count of chart windows (main window + indicator subwindows)
     */
    int windowsTotal();

    /**
     * Returns instrument of the chart
     * 
     * @return instrument of the chart
     * @see #getFeedDescriptor()
     */
    Instrument getInstrument();

    /**
     * Sets chart's instrument
     * @param instrument instrument to set
     * @deprecated 
     */
    @Deprecated
    void setInstrument(Instrument instrument);

    /**
     * Returns selected period
     * 
     * @return selected period
     * @see #getFeedDescriptor()
     */
    Period getSelectedPeriod();

    /**
     * Returns selected offer side
     * 
     * @return selected offer side
     * @see #getFeedDescriptor()
     */
    OfferSide getSelectedOfferSide();
    
    /**
     * Add indicator to the chart
     * 
     * @param indicator as <code>IIndicator</code>
     * @deprecated please use {@link #add(IIndicator)} method
     */
    @Deprecated
    void addIndicator(IIndicator indicator);

    /**
     * Add indicator to the chart by specifying initial optional parameters values
     * If optParams equals to null - default ones will be taken instead
     * 
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @deprecated Please use {@link #add(IIndicator, Object[])} method
     */
    @Deprecated
    void addIndicator(IIndicator indicator, Object[] optParams);
    
    /**
     * Add indicator to the chart by specifying initial optional parameters values, curves colors, drawing styles and line widths
     * If output params are nulls - default ones will be taken instead
     * 
     * @param indicator as <code>IIndicator</code>
     * @param optParams as <code>Object[]</code>
     * @param outputColors as <code>Color[]</code>
     * @param outputDrawingStyles as <code>DrawingStyle[]</code>
     * @param outputWidths as <code>int[]</code>
     * @deprecated Please use {@link com.dukascopy.api.IChartPanel#add(com.dukascopy.api.indicators.IIndicator, Object[], java.awt.Color[], com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle[], int[])} method
     */
    @Deprecated
    void addIndicator(
            IIndicator indicator,
            Object[] optParams,
            Color[] outputColors,
            DrawingStyle[] outputDrawingStyles,
            int[] outputWidths
    );

    /**
     * Add sub indicator
     * 
     * @param subChartId id of sub chart
     * @param indicator as <code>IIndicator</code>
     * @deprecated use {@link #getIndicatorPanels()} and {@link IChartPanel#add(IIndicator)} methods.
     */
    @Deprecated
    void addSubIndicator(Integer subChartId, IIndicator indicator);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide, Period basePeriod);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod, OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Period basePeriod,
            Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide,
            OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     */
    @Override
    IChartPanel add(IIndicator indicator, Object[] optParams, Instrument instrument, Period period, OfferSide offerSide, Period basePeriod,
            OfferSide[] offerSides, IIndicators.AppliedPrice[] appliedPrices, Color[] outputColors, DrawingStyle[] outputDrawingStyles, int[] outputWidths);

    /**
     * {@inheritDoc}
     */
    @Override
    void removeIndicator(IIndicator indicator);

    /**
     * {@inheritDoc}
     * <br>Including the ones on sub-panels
     */
    @Override
    List<IIndicator> getIndicators();
    
    /**
     * {@inheritDoc}
     * <br>Including the ones on sub-panels
     */
    @Override
    List<IIndicatorAppearanceInfo> getIndicatorApperanceInfos();
    
    /**
     * Returns selected Data Type
     * 
     * @return selected Data Type
     * @see #getFeedDescriptor()
     */
    DataType getDataType();
    
    /**
     * The method returns currently selected Price Range on chart
     * 
     * @return selected Price Range
     * @see #getFeedDescriptor()
     */
    PriceRange getPriceRange();
    
    /**
     * The method returns currently selected LineBreakLoockback on chart
     * 
     * @return selected LineBreakLookback
     * @see #getFeedDescriptor()
     */
    LineBreakLookback getLineBreakLookback();
    
    /**
     * The method returns currently selected Reversal Amount on the Point And Figure chart.
     * For non P&amp;F charts null will be returned
     * 
     * @return selected Reversal Amount
     * @see #getFeedDescriptor()
     */
    ReversalAmount getReversalAmount();
    
    /**
     * Returns current filter
     * 
     * @return filter
     * @see #getFeedDescriptor()
     */
    Filter getFilter();
    
    /**
     * Returns chart state described by bean {@link IFeedDescriptor}
     * @return chart state described by bean {@link IFeedDescriptor}
     */
    IFeedDescriptor getFeedDescriptor();
    
    /**
     * Refresh and repaint chart
     */
    void repaint();
   
    /**
     * Returns <code>IChartObjectFactory</code> instance. This factory provides convenience methods to create
     * various graphics objects.
     * @return <code>IChartObjectFactory</code> instance.
     */
    IChartObjectFactory getChartObjectFactory();

    /**
     * {@inheritDoc}
     */
    @Override
    void add(IChartObject chartObject);
    
	/**
	 * Adds object of <code>IChartObject</code> super type to main chart window. 
	 * Please note that only <b>one</b> instance of object can be added per main chart window.
	 * 
	 * @param object - instance of <code>IChartObject</code> super type.
	 * 
	 * @exception IllegalArgumentException - if <code>object</code> is already present on any of main charts.
	 * @deprecated Please use {@link #add(IChartObject)} method
	 */
    @Deprecated
	void addToMainChart(IChartObject object);
	
	/**
	 * Adds object of <code>IChartObject</code> super type to sub chart window with specified <code>subChartId</code> and <code>indicatorId</code>. 
	 * Please note that only <b>one</b> instance of object can be added per sub chart window. 
	 * 
	 * 
	 * @param subChartId - sub chart id.
	 * @param indicatorId - indicator function id.
	 * @param object - instance of <code>IChartObject</code> super type.
	 * 
	 * @exception IllegalArgumentException - if <code>object</code> is already present on any of sub charts.
	 * @deprecated use {@link #getIndicatorPanels()} and {@link IIndicatorPanel#add(IChartObject)} methods
	 */
	@Deprecated
	void addToSubChart(Integer subChartId, int indicatorId, IChartObject object);
	
	/**
	 * Adds object of <code>IChartObject</code> super type to unlocked objects pool of main chart window. 
	 * Please note that only <b>one</b> instance of object can be added to unlocked objects pool per main chart window. 
	 * 
	 * @param object - instance of <code>IChartObject</code> super type.
	 * @exception IllegalArgumentException - if <code>object</code> is already present on any of main charts unlocked objects pool.
	 * 
	 * @deprecated Please, use method @link {@link IChart#addToMainChart(IChartObject)} There is no objects division onto locked and unlocked anymore
	 */
	@Deprecated
	void addToMainChartUnlocked(IChartObject object);

	/**
	 * Checks whether <code>chartObject</code> is unlocked or not. Returns <code>null</code> if chart does not contain specified <code>chartObject</code>
	 * 
	 * @param chartObject object to check
	 * @return <code>true</code> - <code>chartObject</code> is unlocked, <code>false</code> - if not, <code>null</code> - <code>chartObject</code> does not belong to <code>this</code> chart.
	 * 
	 * @deprecated There is no objects division onto locked and unlocked anymore, so this method will always return false
	 */
	@Deprecated
	Boolean isChartObjectUnlocked(IChartObject chartObject);
	
    /**
     * The method returns currently selected Trade Bar Size on Tick Bar chart
     * For non Tick Bar charts null might be returned
     * 
     * @return selected Tick Bar Size
     * @see #getFeedDescriptor()
     */
	TickBarSize getTickBarSize();

	/**
	 * The method returns List of chart objects created by current strategy
	 * Empty list is returned if no objects were created
	 * Null is never returned
	 * 
	 * @return chart objects that were create by current strategy
     * @deprecated
	 */
	@Deprecated
	List<IChartObject> getStrategyChartObjects();
	
	/**
	 * Creates a {@link BufferedImage} snapshot of this chart at the given moment.
	 * @return {@link BufferedImage} snapshot of current chart at the method invocation moment.   
	 */
	BufferedImage getImage();
	
	/**
	 * Applies DataPresentationType to current chart.
	 * Use {@link #getDataType()}.getSupportedPresentationTypes() to get all allowed values.
	 * Use {@link #getDataType()}.isPresentationTypeSupported(DataPresentationType presentationType)
	 *  to check whether current DataType supports presentationType or not.
	 * 
	 * @param presentationType one of DataPresentationType constants, supported by current DataType
	 * @see #getDataType()
	 * @throws IllegalArgumentException if DataPresentationType is not supported by current DataType
	 */
	void setDataPresentationType(DataPresentationType presentationType);
	
	/**
	 * Returns charts' current {@link DataPresentationType}.
	 * Depends on current {@link DataType} value
     * @return data type of chart
	 * @see #getDataType()
	 */
	DataPresentationType getDataPresentationType();
	
	/**
	 * Select specified drawing by key.
	 * Note: locked drawings cannot be selected.
	 * @see IChartObject#isLocked()
	 * @see IChartObject#setLocked(boolean)
	 * @param key IChartObject's key
	 */
	void selectDrawing(String key);
	
	/**
	 * Select specified drawing.
     * Note: locked drawings cannot be selected.
     * @see IChartObject#isLocked()
     * @see IChartObject#setLocked(boolean)
	 * @param chartObject object to select
	 */
	void selectDrawing(IChartObject chartObject);
	
	/**
	 * Navigates to and selects specified drawing by key.
	 * Note: locked drawings cannot be selected.
     * @see IChartObject#isLocked()
     * @see IChartObject#setLocked(boolean)
	 * @param key IChartObject's key
	 */
	void navigateAndSelectDrawing(String key);
	
	/**
	 * Navigates to and selects specified drawing.
     * Note: locked drawings cannot be selected.
     * @see IChartObject#isLocked()
     * @see IChartObject#setLocked(boolean)
	 * @param chartObject object to select
	 */
	void navigateAndSelectDrawing(IChartObject chartObject);
	
	/**
	 * Returns current chart active sub panels.
	 * 
	 * @return list of panels
	 */
	List<IIndicatorPanel> getIndicatorPanels();
	
	/**
     * Creates new sub panel and adds indicator onto it.
     * 
     * @param indicator indicator to add
     * @deprecated use {@link #add(IIndicator)}
     */
	@Deprecated
    void addSubIndicator(IIndicator indicator);
	
	/**
	 * Adds indicator to chart sub panel with specified index (not ID).
	 * 
	 * @param subPanelIndex panel's index
	 * @param indicator indicator to add
	 * @throws IllegalStateException in case of incorrect subPanelIndex or indicator is not designed for sub panel.
	 * @deprecated use {@link #getIndicatorPanels()} and {@link #add(IIndicator)} methods
	 */
	@Deprecated
	void addSubIndicatorByPanelIndex(Integer subPanelIndex, IIndicator indicator);
	
	/**
	 * Adds object of <code>IChartObject</code> type to sub chart panel with specified <code>index</code>.
     * Please note that only <b>one</b> instance of object can be added on chart.
	 * 
	 * @param subPanelIndex panel's index
	 * @param chartObject object to add
	 * @throws IllegalArgumentException in case of incorrect index
	 * @deprecated use {@link #getIndicatorPanels()} and {@link IIndicatorPanel#add(IChartObject)} methods
	 */
	@Deprecated
	void addToSubChart(int subPanelIndex, IChartObject chartObject);
	
	/**
	 * Returns data feed objects that are plotted on the chart at the given moment.
	 * The 0th element stands for the first visible data feed object on the chart, 
	 * and the last element - for the last visible data feed object on the chart.
	 * {@link Object} type depends on {@link #getDataType()}
	 * 
	 * @return data feed objects that are plotted on the chart at the given moment.
	 */
	ITimedData[] getLastRequestedData();
	
	/**
	 * Returns outputs of the indicators that are plotted on the chart at the given moment.
	 * Each {@link Object} in Object[] stands for an output array of an indicator.
	 * In each output array that is represented by an Object (of Object[]),
	 * the 0th element stands for the first visible data feed object on the chart, 
	 * and the last element - for the data feed object element on the chart.
	 * 
	 * @return outputs of the indicators that are plotted on the chart at the given moment.
	 */
	Map<IIndicator, Object[]> getLastCalculatedIndicatorOutputs();
	
	/**
	 * Returns the minimal time axis value.
	 * 
	 * @return minimal time axis value
	 */
	long getMinTime();
	
	/**
	 * Returns the maximal time axis value. Note that for charts of {@link DataType} 
	 * other than {@link DataType#TICKS} or {@link DataType#TIME_PERIOD_AGGREGATION},
	 * the maximal time value is the one of the last feed data element.
	 * 
	 * @return maximal time axis value
	 */
	long getMaxTime();

    /**
     * {@inheritDoc}
     */
    @Override
    double getMinPrice();

    /**
     * {@inheritDoc}
     */
    @Override
    double getMaxPrice();
	
	/**
	 * Returns true if the chart is opened by Historical Tester or from a strategy while back-testing
	 * 
	 * @return true if the chart is opened by Historical Tester or from a strategy while back-testing
	 */
	boolean isHistoricalTesterChart();

    /**
     * Returns current chart theme. If the theme is among IChartTheme.Predefined, then a copy of the theme gets returned
     *
     * @return the current chart theme
     */
    IChartTheme getTheme();

    /**
     * Sets current chart feed.
     *
     * @param feedDescriptor descriptor of chart data feed
     */
    void setFeedDescriptor(IFeedDescriptor feedDescriptor);

    /**
     * Focuses JForex platform to this chart
     */
    void setFocus();
    
    /**
     * Returns current chart state.
     * @return false if chart is closed, true if chart is active.
     */
    boolean isAlive();

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
     * Starts/stops picking price from chart.
     *
     * @param isPickingPrice if set to true - turn on picking price mode, false - turn it off
     * @param listener listener to receive picked price value
     */
    void switchPickingPriceMode(boolean isPickingPrice, PickingPriceListener listener);

    /**
     * Listener to receive picked price value in picking price mode.
     */
    public interface PickingPriceListener {
        /**
         * Receives picked price value.
         *
         * @param price price value selected on chart
         * @param instrument corresponding instrument from chart
         */
        void priceValuePicked(double price, Instrument instrument);
    }
}
