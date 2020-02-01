/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.dukascopy.api.feed.DataInterpolationDescriptor;
import com.dukascopy.api.feed.IFeedDescriptor;

/**
 * Provide with system state:
 * <ul>
 * <li>The information about high/low prices for corresponding instruments and periods. 
 * <li>The times of first ticks, bars, candles etc. for corresponding instruments.
 * <li>Forex Sentiment Indices.
 * <li>Offline Time Intervals
 * <li>...
 * </ul>
 * @author aburenin
 *
 */
public interface IDataService {

    /**
     * The method adds daily high/low listener.
     * After addition listener will be notified with the current best daily high/low (If in progress daily candle exists in the system).
     * If in progress candle doesn't exist in the system listener will be notified as soon as candle arrives.
     * This method equals to addHighLowListener(Period.DAILY, instrument, listener)
     *
     * @param instrument Instrument to listen for high/low
     * @param listener listener to add
     * @see #addHighLowListener(Period, Instrument, IHighLowListener)
     */
    void addDailyHighLowListener(
            Instrument instrument,
            IDailyHighLowListener listener
    );
    
    /**
     * Removes daily high/low listener
     * @param listener listener to remove
     * @see #removeHighLowListener(IHighLowListener)
     */
    void removeDailyHighLowListener(IDailyHighLowListener listener);
    
    /**
     * Returns all daily high/low listeners subscribed on the passed instrument.
     * This method equals to getHighLowListeners(Period.DAILY, instrument)
     * 
     * @param instrument Instrument to get listeners for
     * @return all daily high/low listeners subscribed on the passed instrument
     * @see #getHighLowListeners(Period, Instrument)
     */
    Collection<IDailyHighLowListener> getDailyHighLowListeners(Instrument instrument);
    
    /**
     * Returns all daily high/low listeners
     * This method equals to getHighLowListeners(Period.DAILY)
     * 
     * @return all daily high/low listeners
     * @see #getHighLowListeners(Period)
     */
    Map<Instrument, Collection<IDailyHighLowListener>> getDailyHighLowListeners();

    /**
     * Removes all high/low listeners
     * @see #removeAllHighLowListeners()
     */
    void removeAllDailyHighLowListeners();

    /**
     * The method adds high/low listener.<br>
     * After addition, listener will be notified with the current best period's high/low (If in progress candle exists in the system)
     * If in progress candle doesn't exist in the system listener will be notified as soon as candle arrives
     * @param period Candles' {@link Period} to listen to. {@link Period#TICK} isn't supported.
     * @param instrument Candles {@link Instrument} to listen to.
     * @param offerSide Candles {@link OfferSide} to listen to.
     * @param listener instance of {@link IHighLowListener}
     */
    void addHighLowListener(
            Period period,
            Instrument instrument,
            OfferSide offerSide,
            IHighLowListener listener
    );

    @Deprecated
    void addHighLowListener(
            Period period,
            Instrument instrument,
            IHighLowListener listener
    );
    
    /**
     * Removes high/low listener.<br>
     * <b>NOTE:</b> shared instance of listener will be unsubscribed from every period and/or instrument. 
     * @param listener instance of {@link IHighLowListener}
     */
    void removeHighLowListener(IHighLowListener listener);
    
    /**
     * Returns all high/low listeners subscribed to the passed period and instrument
     * 
     * @param period listener's {@link Period}
     * @param instrument listener's {@link Instrument}
     * @return all high/low listeners subscribed to the passed period and instrument
     */
    Collection<IHighLowListener> getHighLowListeners(Period period, Instrument instrument);
    
    /**
     * Returns all high/low listeners subscribed to the passed period and grouped by instrument
     * 
     * @param period listener's {@link Period}
     * @return a map of all high/low listeners subscribed to the passed period and grouped by instrument
     */
    Map<Instrument,Collection<IHighLowListener>> getHighLowListeners(Period period);
    
    /**
     * Returns all high/low listeners 
     * 
     * @return all high/low listeners grouped by period and instrument
     */
    Map<Period, Map<Instrument,Collection<IHighLowListener>>> getHighLowListeners();

    /**
     * Removes all high/low listeners
     * <b>NOTE:</b> shared instance of listeners will be unsubscribed from every period and/or instrument. 
     */
    void removeAllHighLowListeners();

    /**
     * Returns the time of first feed data specified in {@link IFeedDescriptor}.
     * @param feedDescriptor the {@link IFeedDescriptor} specifies the feed data the first time must be returned.<br>
     * {@link IFeedDescriptor#getDataType()}  determines the required properties of {@link IFeedDescriptor} must be set and<br>
     * the specifies the method will be invoked:
     * <ul>
     *  <li> DataType#TICKS - requires {@link Instrument}, equals to {@link #getTimeOfFirstTick(Instrument)} 
     *  <li> DataType#TICK_BAR - requires {@link Instrument}, equals to {@link #getTimeOfFirstTickBar(Instrument)} 
     *  <li> DataType#TIME_PERIOD_AGGREGATION - requires {@link Instrument} and {@link Period}, equals to {@link #getTimeOfFirstCandle(Instrument, Period)} 
     *  <li> DataType#PRICE_RANGE_AGGREGATION - requires {@link Instrument} and {@link PriceRange}, equals to {@link #getTimeOfFirstRangeBar(Instrument, PriceRange)} 
     *  <li> DataType#POINT_AND_FIGURE - requires {@link Instrument}, {@link PriceRange} and and {@link ReversalAmount}, equals to {@link #getTimeOfFirstPointAndFigure(Instrument, PriceRange, ReversalAmount)}
     *  <li> DataType#RENKO - requires {@link Instrument} and {@link PriceRange}, equals to {@link #getTimeOfFirstRenko(Instrument, PriceRange)}
     *  <li> DataType#LINE_BREAK - requires {@link Instrument} and {@link Period}, equals to {@link #getTimeOfFirstLineBreak(Instrument, Period)}
     * </ul>
     * @return Returns the time of first feed data specified in {@link IFeedDescriptor} or {@link Long#MAX_VALUE} if there is no one.
     */
    long getTimeOfFirstCandle(IFeedDescriptor feedDescriptor);

    /**
     * Returns the time of first {@link DataType#TICKS ticks} for specified {@link Instrument}
     * @param instrument the {@link Instrument} the first tick's time must be returned.
     * @return the time of first {@link DataType#TICKS ticks} for specified {@link Instrument} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#TICKS
     */
    long getTimeOfFirstTick(Instrument instrument);                                                                 
    
    /**
     * Returns the time of first {@link DataType#TICK_BAR tick bars} for specified {@link Instrument}
     * @param instrument the {@link Instrument} the first Tick Bars' time must be returned.
     * @return the time of first {@link DataType#TICK_BAR tick bars} for specified {@link Instrument} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#TICK_BAR
     */
    long getTimeOfFirstTickBar(Instrument instrument);                                                              
    
    /**
     * Returns the time of first {@link DataType#TIME_PERIOD_AGGREGATION candles} for specified {@link Instrument} and {@link Period}
     * @param instrument the {@link Instrument} the first {@link DataType#TIME_PERIOD_AGGREGATION candles}' time must be returned.
     * @param period the {@link Period} the first {@link DataType#TIME_PERIOD_AGGREGATION candles}' time must be returned.
     * @return the time of first {@link DataType#TIME_PERIOD_AGGREGATION candles} for specified {@link Instrument} and {@link Period}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#TIME_PERIOD_AGGREGATION
     */
    long getTimeOfFirstCandle(Instrument instrument, Period period);                                                
    
    /**
     * Returns the time of first {@link DataType#PRICE_RANGE_AGGREGATION price range bars} for specified {@link Instrument} and {@link PriceRange}
     * @param instrument the {@link Instrument} the first {@link DataType#PRICE_RANGE_AGGREGATION price range bars}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#PRICE_RANGE_AGGREGATION price range bars}' time must be returned.
     * @return the time of first {@link DataType#PRICE_RANGE_AGGREGATION price range bars} for specified {@link Instrument} and {@link PriceRange}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#PRICE_RANGE_AGGREGATION
     * @deprecated use {@link #getTimeOfFirstRangeBar(Instrument, PriceRange, com.dukascopy.api.feed.DataInterpolationDescriptor)}
     */
    @Deprecated
    long getTimeOfFirstRangeBar(Instrument instrument, PriceRange priceRange);                                      
    
    /**
     * Returns the time of first {@link DataType#PRICE_RANGE_AGGREGATION price range bars} for specified {@link Instrument}, {@link PriceRange} and {@link DataInterpolationDescriptor}
     * @param instrument the {@link Instrument} the first {@link DataType#PRICE_RANGE_AGGREGATION price range bars}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#PRICE_RANGE_AGGREGATION price range bars}' time must be returned.
     * @param dataInterpolationDescriptor the {@link DataInterpolationDescriptor} the first {@link DataType#PRICE_RANGE_AGGREGATION price range bars}' time must be returned.
     * @return the time of first {@link DataType#PRICE_RANGE_AGGREGATION price range bars} for specified {@link Instrument} and {@link PriceRange}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#PRICE_RANGE_AGGREGATION
     */
    long getTimeOfFirstRangeBar(Instrument instrument, PriceRange priceRange, DataInterpolationDescriptor dataInterpolationDescriptor);
    
    /**
     * Returns the time of first {@link DataType#POINT_AND_FIGURE point &amp; figures} for specified {@link Instrument}, {@link PriceRange} and {@link ReversalAmount}
     * @param instrument the {@link Instrument} the first {@link DataType#POINT_AND_FIGURE point &amp; figures}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#POINT_AND_FIGURE point &amp; figures}' time must be returned.
     * @param reversalAmount the {@link ReversalAmount} the first {@link DataType#POINT_AND_FIGURE point &amp; figures}' time must be returned.
     * @return the time of first {@link DataType#POINT_AND_FIGURE point &amp; figures} for specified {@link Instrument}, {@link PriceRange} and {@link ReversalAmount}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#POINT_AND_FIGURE
     * @deprecated use {@link #getTimeOfFirstPointAndFigure(Instrument, PriceRange, ReversalAmount, com.dukascopy.api.feed.DataInterpolationDescriptor)}
     */
    @Deprecated
    long getTimeOfFirstPointAndFigure(Instrument instrument, PriceRange priceRange, ReversalAmount reversalAmount); 
    
    /**
     * Returns the time of first {@link DataType#POINT_AND_FIGURE point &amp; figures} for specified {@link Instrument}, {@link PriceRange}, {@link ReversalAmount} and {@link DataInterpolationDescriptor}
     * @param instrument the {@link Instrument} the first {@link DataType#POINT_AND_FIGURE point &amp; figures}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#POINT_AND_FIGURE point &amp; figures}' time must be returned.
     * @param reversalAmount the {@link ReversalAmount} the first {@link DataType#POINT_AND_FIGURE point &amp; figures}' time must be returned.
     * @param dataInterpolationDescriptor the {@link DataInterpolationDescriptor} the first {@link DataType#POINT_AND_FIGURE point &amp; figures}' time must be returned.
     * @return the time of first {@link DataType#POINT_AND_FIGURE point &amp; figures} for specified {@link Instrument}, {@link PriceRange} and {@link ReversalAmount}
     * or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#POINT_AND_FIGURE
     */
    long getTimeOfFirstPointAndFigure(Instrument instrument, PriceRange priceRange, ReversalAmount reversalAmount, DataInterpolationDescriptor dataInterpolationDescriptor);
    
    /**
     * Returns the time of first {@link DataType#RENKO renko} for specified {@link Instrument} and {@link PriceRange}
     * @param instrument the {@link Instrument} the first {@link DataType#RENKO renko}' time must be returned.
     * @param priceRange the {@link PriceRange} the first {@link DataType#RENKO renko}' time must be returned.
     * @return the time of first {@link DataType#RENKO renko} for specified {@link Instrument} and {@link PriceRange} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#RENKO
     * @deprecated use {@link #getTimeOfFirstRenko(Instrument, Period)}
     */
    @Deprecated
    long getTimeOfFirstRenko(Instrument instrument, PriceRange priceRange);    
    
    /**
     * Returns the time of first {@link DataType#RENKO renko} for specified {@link Instrument} and {@link Period} (time session)
     * @param instrument the {@link Instrument} the first {@link DataType#RENKO renko}' time must be returned.
     * @param timeSession the {@link Period} (renko session) the first {@link DataType#RENKO renko}' time must be returned.
     * @return the time of first {@link DataType#RENKO renko} for specified {@link Instrument} and {@link Period time session} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#RENKO
     */
    long getTimeOfFirstRenko(Instrument instrument, Period timeSession);   
    
    /**
     * Returns the time of first {@link DataType#LINE_BREAK N-Line Break} for specified {@link Instrument} and {@link Period} (time session)
     * @param instrument the {@link Instrument} the first {@link DataType#LINE_BREAK N-Line Break}' time must be returned.
     * @param timeSession the {@link Period} (time session) the first {@link DataType#LINE_BREAK N-Line Break}' time must be returned.
     * @return the time of first {@link DataType#LINE_BREAK N-Line Break} for specified {@link Instrument} and {@link Period time session} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#LINE_BREAK
     */
    long getTimeOfFirstLineBreak(Instrument instrument, Period timeSession);
    
    /**
     * Returns the time of first {@link DataType#KAGI kagi} for specified {@link Instrument} and {@link Period} (time session)
     * @param instrument the {@link Instrument} the first {@link DataType#KAGI kagi}'s time must be returned.
     * @param timeSession the {@link Period} (time session) the first {@link DataType#KAGI kagi}' time must be returned.
     * @return the time of first {@link DataType#KAGI kagi} for specified {@link Instrument} and {@link Period time session} or {@link Long#MAX_VALUE} if there is no one.
     * @see DataType#KAGI
     */
    long getTimeOfFirstKagi(Instrument instrument, Period timeSession);
    
    /**
     * Returns <b>last updated</b> Forex Sentiment Index for specified {@link Instrument}.<br>
     * Equivalent to the getFXSentimentIndex(instrument, System.currentTimeMillis) for live data and
     * getFXSentimentIndex(instrument, pseudo_current_historical_tester_time) for historical data.
     * @param instrument {@link Instrument} of sentiment index
     * @return the last updated sentiment index or <code>null</code> if there is no such
     * @see #getFXSentimentIndex(Instrument, long)
     */
    IFXSentimentIndex getFXSentimentIndex(Instrument instrument);
    
    /**
     * Returns Forex Sentiment Index which was most up-to-date at a point of specified <code>time</code>
     * @param instrument {@link Instrument} of sentiment index
     * @param time the point of time in the past in milliseconds.<br>
     * To calculate the right time point one can use either
     * <ul>
     * <li> standard java utils {@link Calendar}/{@link Date}/{@link TimeUnit}/{@link DateFormat} or
     * <li> {@link JFUtils#getTimeForNPeriodsBack(Period, long, int)} / {@link JFUtils#getTimeForNPeriodsForward(Period, long, int)} or
     * <li> custom approaches
     * </ul>
     * @return the sentiment index which was most up-to-date at a point of specified <code>time</code> or <code>null</code> if there is no such.
     * @see JFUtils#getTimeForNPeriodsBack(Period, long, int)
     * @see JFUtils#getTimeForNPeriodsForward(Period, long, int)
     */
    IFXSentimentIndex getFXSentimentIndex(Instrument instrument, long time);

	/**
	 * Returns the list of sentiment indices which were most up-to-date at the specified time frame.
	 * @param instrument {@link Instrument} of sentiment index
	 * @param period period between the sentiment indices
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link IHistory#getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
	 * @return the list of sentiment indices which were most up-to-date at the specified time frame or <code>null</code> if there is no such.
	 * @throws JFException if <code>time</code> is not divisible by the interval of <code>period</code> or the period is not among
	 * Period.THIRTY_MINS, Period.ONE_HOUR, Period.FOUR_HOURS, Period.DAILY, Period.WEEKLY
	 */
    List<IFXSentimentIndexBar> getFXSentimentIndex(Instrument instrument, Period period, long from, long to) throws JFException;
    
    /**
     * Returns <b>last updated</b> Forex Sentiment Index for specified currency.<br>
     * @param currency currency of sentiment index
     * @return the last updated sentiment index or <code>null</code> if there is no such
     * @deprecated Use {@link IDataService#getFXSentimentIndex(ICurrency)} instead
     */
    @Deprecated
    IFXSentimentIndex getFXSentimentIndex(Currency currency);
    
    /**
     * Returns <b>last updated</b> Forex Sentiment Index for specified {@link ICurrency}.<br>
     * Equivalent to the getFXSentimentIndex(ICurrency, System.currentTimeMillis) for live data and
     * getFXSentimentIndex(ICurrency, pseudo_current_historical_tester_time) for historical data.
     * @param currency {@link ICurrency} of sentiment index.
     * @return the last updated sentiment index or <code>null</code> if there is no such
     * @see #getFXSentimentIndex(ICurrency, long)
     */
    IFXSentimentIndex getFXSentimentIndex(ICurrency currency);
    
    /**
     * Returns Forex Sentiment Index which was most up-to-date at a point of specified <code>time</code>.
     * @param currency currency of sentiment index
     * @param time the point of time in the past in milliseconds
     * @return the sentiment index which was most up-to-date at a point of specified <code>time</code> or <code>null</code> if there is no such
     * @deprecated Use {@link IDataService#getFXSentimentIndex(ICurrency, long)} instead
     */
    @Deprecated
    IFXSentimentIndex getFXSentimentIndex(Currency currency, long time);
    
    /**
     * Returns Forex Sentiment Index which was most up-to-date at a point of specified <code>time</code>
     * @param currency {@link ICurrency} of sentiment index.
     * @param time the point of time in the past in milliseconds.<br>
     * To calculate the right time point one can use either 
     * <ul>
     * <li> standard java utils {@link Calendar}/{@link Date}/{@link TimeUnit}/{@link DateFormat} or
     * <li> {@link JFUtils#getTimeForNPeriodsBack(Period, long, int)} / {@link JFUtils#getTimeForNPeriodsForward(Period, long, int)} or
     * <li> custom approaches
     * </ul>
     * @return the sentiment index which was most up-to-date at a point of specified <code>time</code> or <code>null</code> if there is no such.
     * @see JFUtils#getTimeForNPeriodsBack(Period, long, int)
     * @see JFUtils#getTimeForNPeriodsForward(Period, long, int)
     */
    IFXSentimentIndex getFXSentimentIndex(ICurrency currency, long time);
    
	/**
	 * Returns the list of sentiment indices which were most up-to-date at the specified time frame.
	 * @param currency currency of sentiment index
	 * @param period period between the sentiment indices
     * @param from start of the time interval for which bars should be loaded
     * @param to end time of the time interval for which bars should be loaded
	 * @return the list of sentiment indices which were most up-to-date at the specified time frame or <code>null</code> if there is no such
	 * @throws JFException if <code>time</code> is not divisible by the interval of <code>period</code> or the period is not among
	 * Period.THIRTY_MINS, Period.ONE_HOUR, Period.FOUR_HOURS, Period.DAILY, Period.WEEKLY
	 * @deprecated Use {@link IDataService#getFXSentimentIndex(ICurrency, Period, long, long)} instead
	 */
    @Deprecated
    List<IFXSentimentIndexBar> getFXSentimentIndex(Currency currency, Period period, long from, long to) throws JFException;
    
    /**
	 * Returns the list of sentiment indices which were most up-to-date at the specified time frame.
	 * @param currency {@link ICurrency} of sentiment index
	 * @param period period between the sentiment indices
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link IHistory#getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
	 * @return the list of sentiment indices which were most up-to-date at the specified time frame or <code>null</code> if there is no such.
	 * @throws JFException if <code>time</code> is not divisible by the interval of <code>period</code> or the period is not among
	 * Period.THIRTY_MINS, Period.ONE_HOUR, Period.FOUR_HOURS, Period.DAILY, Period.WEEKLY
	 */
    List<IFXSentimentIndexBar> getFXSentimentIndex(ICurrency currency, Period period, long from, long to) throws JFException;

    /**
     * Returns <code>true</code> if specified <code>time</code> is within the limits of offline (weekend) period. 
     * @param time time in milliseconds
     * @return <code>true</code> if specified <code>time</code> is within the limits of offline (weekend) period, <code>false</code> - otherwise
     */
    boolean isOfflineTime(long time);

    /**
     * Returns either present, current offline (weekend) {@link ITimeDomain time interval} or the next <b>approximate</b> upcoming one.<br>
     * The same as #getOfflineTimeDomain(shift) with shift == 0
     * @return either present, current offline (weekend) {@link ITimeDomain time interval} or the next <b>approximate</b> upcoming one. 
     * @throws JFException when some error occurs
     * @see #getOfflineTimeDomain(int)
     * 
     */
    ITimeDomain getOfflineTimeDomain() throws JFException;
    
    /**
     * Returns offline (weekend) {@link ITimeDomain time interval} that is shifted back or forward for number of offline intervals specified in <code>shift</code> parameter.
     * @param shift number of offline intervals back or forward in time staring from current one. E.g.:
     * <ul>
     * <li><b>0</b> - current or next <b>approximate</b> offline period, 
     * <li><b>-1</b> - previous offline (last finished one), 
     * <li><b>1</b> - next <b>approximate</b> after current one, etc.
     * </ul>
     * @return offline (weekend) {@link ITimeDomain time interval} that is shifted back or forward for number of offline intervals specified in <code>shift</code> parameter
     * @throws JFException when some error occurs
     */
    ITimeDomain getOfflineTimeDomain(int shift) throws JFException;
    
    /**
     * Returns the set of offline (weekend) {@link ITimeDomain time intervals} ascending ordered by time which are within the limits of <code>from</code> and <code>to</code> parameters.
     * @param from start of the time interval for which offline periods should be loaded. If <code>start</code> time is within the limits of offline period - this period will be returned as first element of resulting set. 
     * @param to end of the time interval for which offline periods should be loaded. If <code>end</code> time is within the limits of offline period - this period will be returned as last element of resulting set.
     * @return the set of offline (weekend) {@link ITimeDomain time intervals} ascending ordered by time which are within the limits of <code>from</code> and <code>to</code> parameters.
     * @throws JFException when some error occurs
     */
    Set<ITimeDomain> getOfflineTimeDomains(long from, long to) throws JFException;

    /**
     * Return <b>unmodifiable map</b> of server properties
     * @return <b>unmodifiable map</b> of server properties
     */
    Map<String, Object> getServerProperties();
    
    /**
     * @return {@link IWLabelData} information
     */
    IWLabelData getWhiteLabelData();
}
