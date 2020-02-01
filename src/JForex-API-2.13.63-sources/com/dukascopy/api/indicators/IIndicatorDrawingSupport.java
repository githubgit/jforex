/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.awt.Color;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFTimeZone;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Various helper methods to be able to draw indicators
 *
 * @author Dmitry Shohov
 */
public interface IIndicatorDrawingSupport {

    /**
     * Returns true if Graphics passed to drawOutput method belong to main chart (where candles are shown) or false
     * if it belongs to separate panel created for this indicator (when IndicatorInfo.isOverChart returns false).
     *
     * @return true for main chart and false for indicator panel under chart
     */
    boolean isChartPanel();

    /**
     * Returns number of candles currently visible on the screen. This values is usually less than size of the array with values and array with candles
     *
     * @return number of candles currently visible on the screen
     */
    int getNumberOfCandlesOnScreen();

    /**
     * Index in the array of values and candles. Points to the first candle that is visible on the screen.
     * Depending on type of the output (simple line for example) it may be a good idea to draw only values starting
     * from the candle before first visible candle and to the candle after last visible candle
     *
     * @return index of the first visible candle in the array of values and candles
     */
    int getIndexOfFirstCandleOnScreen();

    /**
     * Candle width in pixels
     *
     * @return candle width in pixels
     */
    float getCandleWidthInPixels();

    /**
     * Returns number of pixels between two candles
     *
     * @return number of pixels between two candles
     */
    float getSpaceBetweenCandlesInPixels();

    /**
     * X coordinate of the candle with index
     *
     * @param index index of the candles or/and value
     * @return x coordinate of the candle
     */
    float getMiddleOfCandle(int index);

    /**
     * Index in the array of values and candles, shifted from start index on specified number of candles.
     *
     * @param index start index
     * @param shift number of candles for shift
     * @return shifted index
     */
    int getShiftedIndex(int index, int shift);

    /**
     * Index in the array of values and candles, shifted from start index on specified number of candles.
     *
     * @param index start index
     * @param shift number of candles for shift
     * @param displayMode display mode for indicators with custom period
     * @return shifted index
     */
    int getShiftedIndex(int index, int shift, DisplayMode displayMode);

    /**
     * Time, shifted from start time on specified number of candles.
     *
     * @param time start time
     * @param shift number of candles for shift
     * @return shifted time
     */
    long getShiftedTime(long time, int shift);

    /**
     * Returns Y coordinate for the specified value. Coordinate is calculated depending on minimum and maximum values for indicator
     *
     * @param value indicator value
     * @return y coordinate for the specified value
     */
    float getYForValue(double value);
    
    /**
     * Return X coordinate for the specified time. Coordinate is calculated depending on data sequence currently displayed on chart 
     * 
     * @param time time to get x coordinate for
     * @return x coordinate for the specified time
     */
    int getXForTime(long time);

    /**
     * Return X coordinate for the specified time. Coordinate is calculated depending on data sequence currently displayed on chart
     *
     * @param time time to get x coordinate for
     * @param stickToCandleBarTime stick to candle/bar time
     * @return x coordinate for the specified time
     */
    int getXForTime(long time, boolean stickToCandleBarTime);

    /**
     * Returns Y coordinate for the specified value. Coordinate is calculated depending on minimum and maximum values for indicator
     *
     * @param value indicator value
     * @return y coordinate for the specified value
     */
    float getYForValue(int value);

    /**
     * Returns array of candles. Each one of the candle corresponds to the value with the same index in the array of values for the current output
     *
     * @return array of candles with the same size as the size of the output values array
     */
    IBar[] getCandles();

    /**
     * Returns time data for currently drawn indicator or <code>null</code> if it is calculated on chart period.
     *
     * @return time data for currently drawn indicator
     */
    IFormulaTimeData getFormulaTimeData();

    /**
     * Returns true if the last candle is not fully formed yet
     *
     * @return true if last candle is in-progress candle
     */
    boolean isLastCandleInProgress();

    /**
     * Returns width of the panel in pixels where drawing should happen
     *
     * @return width of the chart
     */
    int getChartWidth();

    /**
     * Returns height of the panel in pixels where drawing should happen
     *
     * @return height of the chart
     */
    int getChartHeight();

    /**
     * Returns instrument of the chart
     *
     * @return instrument of the chart
     */
    Instrument getInstrument();

    /**
     * Returns instrument of the chart
     *
     * @return instrument of the chart
     */
    @Deprecated
    IFinancialInstrument getFinancialInstrument();

    /**
     * Returns period of the chart.
     * Note: returns base period for price aggregated periods.
     *
     * @return period of the chart
     */
    Period getPeriod();

    /**
     * Returns time zone of the chart.
     *
     * @return time zone of the chart
     */
    JFTimeZone getJFTimeZone();

    /**
     * Returns period of currently drawn indicator.
     *
     * @return period of the indicator
     */
    Period getFormulaPeriod();

    /**
     * Returns display mode of currently drawn indicator with custom period.
     *
     * @return display mode for indicators with custom period
     */
    DisplayMode getDisplayMode();

    /**
     * Return offer side of the chart or null if this is ticks chart
     *
     * @return offer side of the chart or null if this is ticks chart
     */
    OfferSide getOfferSide();
    
    /**
     * Returns downtrend color for this indicator
     * 
     * @return downtrend color for this indicator
     */
    Color getDowntrendColor();
    
    /**
     * Returns the minimum value of the price scale.
     * 
     * @return minimum value of the price scale.
     */
    double getMinPrice();
    
    /**
     * Returns the maximum value of the price scale.
     * 
     * @return maximum value of the price scale.
     */
    double getMaxPrice();
    
    /**
	 * Returns the minimum time scale value.
	 * 
	 * @return minimum time scale value
	 */
    long getMinTime();
    
    /**
	 * Returns the maximum time scale value.
	 * 
	 * @return maximum time scale value
	 */
    long getMaxTime();
    
    /**
     * Returns current period is whether time aggregated or not.
     * 
     * @return current period is whether time aggregated or not.
     */
    boolean isTimeAggregatedPeriod();
}
