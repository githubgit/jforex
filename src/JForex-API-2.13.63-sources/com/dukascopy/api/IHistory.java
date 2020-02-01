/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import com.dukascopy.api.feed.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

import java.util.List;
import java.util.stream.Stream;

/**
 * The <code>IHistory</code> interface represents API for historical data access.
 */
public interface IHistory {

    /**
     * Returns time of last tick received for specified instrument. Returns -1 if no tick was received yet.
     * 
     * @param instrument instrument of the tick
     * @return time of last tick or -1 if no tick was received
     * @throws JFException when instrument is not active (not opened in platform)
     */
    long getTimeOfLastTick(Instrument instrument) throws JFException;

    /**
     * Returns last tick for specified instrument
     *
     * @param instrument instrument of the tick
     * @return tick
     * @throws JFException when instrument is not active (not opened in platform)
     */
    ITick getLastTick(Instrument instrument) throws JFException;

    /**
     * Returns starting time of the current bar (bar currently generated from ticks) for specified instrument and period.
     * If no tick was received for this instrument, then returns -1.
     * 
     * @param instrument instrument of the bar
     * @param period period of the bar
     * @return starting time of the current bar or -1 if no tick was received
     * @throws JFException when period is not supported or instrument is not active (not opened in platform)
     */
    long getStartTimeOfCurrentBar(Instrument instrument, Period period) throws JFException;

    /**
     * Returns bar for specified instrument, period and side, that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar (currently generated from ticks), 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
     * 
     * <p> consider getting the previous one hour bar
     * 
     * <pre>
     * IBar prevBar = history.getBar(Instrument.EURUSD, Period.ONE_HOUR, OfferSide.BID, 1);
     * console.getOut().println(prevBar);    
     * </pre>
     * 
     * @param instrument instrument of the bar
     * @param period period of the bar
     * @param side bid or ask side of the bar
     * @param shift number of candle back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
     * @return bar or null if no bar is loaded
     * @throws JFException when period is not supported or instrument is not active (not opened in platform)
     */
    IBar getBar(Instrument instrument, Period period, OfferSide side, int shift) throws JFException;

    /**
     * Reads ticks from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, ticks will be returned by calling methods in <code>tickListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error.
     * This method has two main purposes: one is to load a lot of ticks without keeping them all in memory, and second is asynchronous processing
     * 
     * <p> Consider analyzing the last days ticks - namely calculating the average ask price and the maximum bid
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * double maxBid = Double.MIN_VALUE, avgAsk = 0;
     * int tickCount =0;
     * private Instrument instrument = Instrument.EURUSD;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *     final long from = history.getLastTick(instrument).getTime() - Period.DAILY.getInterval();
     *     final long to = history.getLastTick(instrument).getTime();
     *
     *     history.readTicks(instrument, from, to,
     *
     *     new LoadingDataListener() {
     *         {@literal @}Override
     *         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
     *             tickCount++;
     *             maxBid = Math.max(bid, maxBid);
     *             avgAsk = tickCount == 1 ? ask : round(avgAsk + ((avgAsk - ask) / (double)tickCount));
     *         }
     *         
     *         private double round(double amount) { 
     *             return (new BigDecimal(amount)).setScale(instrument.getPipScale(), BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *
     *         {@literal @}Override
     *         public void newBar(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *             // no bars expected
     *         }
     *     },
     *     new LoadingProgressListener() {
     *
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s ticks %s - %s max Bid=%.5f average ask=%.5f", 
     *                     tickCount, DateUtils.format(from), DateUtils.format(to), maxBid, avgAsk).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     });
     * }
     * </pre>
     * 
     * @param instrument instrument of the ticks
     * @param from start of the time interval for which ticks should be loaded
     * @param to end time of the time interval for which ticks should be loaded. If there is tick with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @param tickListener receives data about requested ticks
     * @param loadingProgress used to control loading process
     * @throws JFException when some error occurs while creating internal request for data
     */
    void readTicks(Instrument instrument, long from, long to, LoadingDataListener tickListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads ticks from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, ticks will be returned by calling method in <code>tickListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of ticks without keeping them all in memory, and second is asynchronous processing
     * 
     * @param instrument instrument of the ticks
     * @param numberOfOneSecondIntervalsBefore how much one second interval of ticks to load before and including one second interval of ticks with time specified in <code>time</code> parameter
     * 
     * @param time time of the last one second tick interval in period specified in <code>numberOfOneSecondIntervalsBefore</code> parameter or/and
     *        time of the one second tick interval prior first one second tick interval in period specified with <code>numberOfOneSecondIntervalsAfter</code> parameter
     * @param numberOfOneSecondIntervalsAfter how much one second tick intervals to load after (not including) one second tick interval with time specified in <code>time</code> parameter
     * @param tickListener receives data about requested ticks
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     */
    void readTicks(Instrument instrument, int numberOfOneSecondIntervalsBefore, long time, int numberOfOneSecondIntervalsAfter, LoadingDataListener tickListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>barListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the last weeks 1 minute bars - namely calculating the average close price and the maximum bar size
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * double maxHeight = Double.MIN_VALUE, avgClose = 0;
     * int barCount, greenCount;
     * private Instrument instrument = Instrument.EURUSD;
     * private Period period = Period.ONE_MIN;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *     long lastTickTime = history.getLastTick(instrument).getTime();
     *     final long from = history.getBarStart(period, lastTickTime - Period.DAILY.getInterval() * 7);
     *     final long to = history.getTimeForNBarsBack(period, lastTickTime, 2);
     *
     *     history.readBars(instrument, period, OfferSide.BID, from, to,
     *
     *     new LoadingDataListener() {
     *         {@literal @}Override
     *         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {}
     *
     *         {@literal @}Override
     *         public void newBar(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *             barCount++;
     *             maxHeight = Math.max(Math.abs(open - close), maxHeight);
     *             avgClose = barCount == 1 ? close : round(avgClose + ((close - avgClose) / (double)barCount));
     *             if(close - open &gt; 0){
     *                 greenCount++;
     *             }
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     },
     *     new LoadingProgressListener() {
     *
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s %s candles (%s green, %s red) %s  - %s max bar height=%.1fpips average close=%.5f"
     *                     barCount, period, greenCount, barCount - greenCount, DateUtils.format(from), DateUtils.format(to), maxHeight/instrument.getPipValue(), avgClose).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     });
     * }
     * </pre>
     *
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for bar that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last bar to be loaded
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    void readBars(Instrument instrument, Period period, OfferSide side, long from, long to, LoadingDataListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background.
     * Method returns fast after creating request for data not waiting for any data to be read from local cache.
     * After internal request is sent, bars will be returned by calling method in <code>barListener</code>.
     * LoadingProgressListener is used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the last weeks 1 minute bars - namely calculating the average close price and the maximum bar size
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * double maxHeight = Double.MIN_VALUE, avgClose = 0;
     * int barCount, greenCount;
     * private Instrument instrument = Instrument.EURUSD;
     * private Period period = Period.ONE_MIN;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *     long lastTickTime = history.getLastTick(instrument).getTime();
     *     final long from = history.getBarStart(period, lastTickTime - Period.DAILY.getInterval() * 7);
     *     final long to = history.getTimeForNBarsBack(period, lastTickTime, 2);
     *
     *     history.readBars(instrument, period, OfferSide.BID, Filter.WEEKENDS, from, to,
     *
     *     new LoadingDataListener() {
     *         {@literal @}Override
     *         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {}
     *
     *         {@literal @}Override
     *         public void newBar(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *             barCount++;
     *             maxHeight = Math.max(Math.abs(open - close), maxHeight);
     *             avgClose = barCount == 1 ? close : round(avgClose + ((close - avgClose) / (double)barCount));
     *             if(close - open &gt; 0){
     *                 greenCount++;
     *             }
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     },
     *     new LoadingProgressListener() {
     *
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s %s candles (%s green, %s red) %s  - %s max bar height=%.1fpips average close=%.5f"
     *                     barCount, period, greenCount, barCount - greenCount, DateUtils.format(from), DateUtils.format(to), maxHeight/instrument.getPipValue(), avgClose).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     });
     * }
     * </pre>
     *
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter bars filtering method see {@link Filter}
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for bar that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last bar to be loaded
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    void readBars(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to, LoadingDataListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>barListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the 100000 1 minute bars - namely calculating the average close price and the maximum bar size
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * double maxHeight = Double.MIN_VALUE, avgClose = 0;
     * int barCount, greenCount;
     * private Instrument instrument = Instrument.EURUSD;
     * private Period period = Period.ONE_MIN;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *     long lastTickTime = history.getLastTick(instrument).getTime();
     *     final long to = history.getTimeForNBarsBack(period, lastTickTime, 2);
     *
     *     history.readBars(instrument, period, OfferSide.BID, Filter.WEEKENDS, 100000, to, 0,
     *
     *     new LoadingDataListener() {
     *         {@literal @}Override
     *         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {}
     *
     *         {@literal @}Override
     *         public void newBar(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *             barCount++;
     *             maxHeight = Math.max(Math.abs(open - close), maxHeight);
     *             avgClose = barCount == 1 ? close : round(avgClose + ((close - avgClose) / (double)barCount));
     *             if(close - open &gt; 0){
     *                 greenCount++;
     *             }
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     },
     *     new LoadingProgressListener() {
     *
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s %s candles (%s green, %s red) till %s max bar height=%.1fpips average close=%.5f",
     *                     barCount, period, greenCount, barCount - greenCount, DateUtils.format(to), maxHeight/instrument.getPipValue(), avgClose).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     });
     * }
     * </pre>
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter allows to filter candles
     * @param numberOfCandlesBefore how much candles to load before and including candle with time specified in <code>time</code> parameter
     * @param time time of the last candles in period specified in <code>numberOfCandlesBefore</code> parameter or/and
     *        time of the candle prior first candle in period specified with <code>numberOfCandlesAfter</code> parameter
     * @param numberOfCandlesAfter how much candles to load after (not including) candle with time specified in <code>time</code> parameter
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    void readBars(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter, LoadingDataListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns ticks for specified instrument and time interval. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * 
     * <pre>
     * ITick lastTick = history.getLastTick(Instrument.EURUSD);
     * List{@literal <ITick>} ticks = history.getTicks(Instrument.EURUSD, lastTick.getTime() - 10 * 1000, lastTick.getTime());
     * int last = ticks.size() - 1;
     * console.getOut().format(
     *     "Tick count=%s; Latest bid price=%.5f, time=%s; Oldest bid price=%.5f, time=%s", 
     *     ticks.size(), ticks.get(last).getBid(), DateUtils.format(ticks.get(last).getTime()), ticks.get(0).getBid(), DateUtils.format(ticks.get(last).getTime())
     * ).println();
     * </pre>
     * 
     * @param instrument instrument of the ticks
     * @param from start of the time interval for which ticks should be loaded
     * @param to end time of the time interval for which ticks should be loaded. If there is tick with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @return loaded ticks
     * @throws JFException when some error occurs when loading data
     */
    List<ITick> getTicks(Instrument instrument, long from, long to) throws JFException;

    /**
     * Returns ticks for specified instrument, time and count. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * 
     * @param instrument instrument of the ticks
     * @param numberOfOneSecondIntervalsBefore how much one second interval of ticks to load before and including one second interval of ticks with time specified in <code>time</code> parameter
     * @param time time of the last one second tick interval in period specified in <code>numberOfOneSecondIntervalsBefore</code> parameter or/and
     *        time of the one second tick interval prior first one second tick interval in period specified with <code>numberOfOneSecondIntervalsAfter</code> parameter
     * @param numberOfOneSecondIntervalsAfter how much one second tick intervals to load after (not including) one second tick interval with time specified in <code>time</code> parameter
     * @return loaded ticks
     * @throws JFException when some error occurs when loading data
     */
    List<ITick> getTicks(Instrument instrument, int numberOfOneSecondIntervalsBefore, long time, int numberOfOneSecondIntervalsAfter) throws JFException;

    /**
     * Returns bars for specified instrument, period and side. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * 
     * <p>Consider getting 5 bars over a time interval:
     * 
     * <pre>
     * long prevBarTime = history.getPreviousBarStart(Period.ONE_HOUR, history.getLastTick(Instrument.EURUSD).getTime());
     * long startTime =  history.getTimeForNBarsBack(Period.ONE_HOUR, prevBarTime, 5);  
     * List{@literal <IBar>} bars = history.getBars(Instrument.EURUSD, Period.ONE_HOUR, OfferSide.BID, startTime, prevBarTime);
     * int last = bars.size() - 1;
     * console.getOut().format(
     *          "Previous bar close price=%.5f; 4th to previous bar close price=%.5f", 
     *          bars.get(last).getClose(), bars.get(0).getClose()
     * ).println();
     * </pre>
     * Consider getting daily bars over several months:
     * <pre>
     * SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss"); 
     * dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); 
     * Date dateFrom, dateTo;
     * try { 
     *     dateFrom = dateFormat.parse("04/06/2013 00:00:00"); 
     *     dateTo = dateFormat.parse("04/10/2013 00:00:00");
     * } catch (ParseException e) {
     *     e.printStackTrace();
     *     return;
     * }    
     * List{@literal <IBar>} bars = history.getBars(Instrument.EURUSD, Period.DAILY, OfferSide.ASK, dateFrom.getTime(), dateTo.getTime());
     * console.getOut().format("bar FROM=%s; bar TO=%s", bars.get(0), bars.get(bars.size() - 1)).println();
     * </pre>
     * 
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    List<IBar> getBars(Instrument instrument, Period period, OfferSide side, long from, long to) throws JFException;

    /**
     * Returns bars for specified instrument, period, side and filter.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * 
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter bars filtering method see {@link Filter}
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException;

    /**
     * Returns bars for specified instrument, period and side. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>. If the requested period includes the bar that is not fully
     * formed yet (in-progress bar), then it is included even if it's flat
     *
     * <p>Consider getting 5 bars over a candle interval:
     * <pre>
     * long prevBarTime = history.getPreviousBarStart(Period.ONE_HOUR, history.getLastTick(Instrument.EURUSD).getTime());
     * List{@literal <IBar>} bars = history.getBars(Instrument.EURUSD, Period.ONE_HOUR, OfferSide.BID, Filter.NO_FILTER, 5, prevBarTime, 0);
     * int last = bars.size() - 1;
     * console.getOut().format(
     *         "Previous bar close price=%.5f; 4th to previous bar close price=%.5f", 
     *         bars.get(last).getClose(), bars.get(0).getClose()).println();
     * </pre>
     *
     * @param instrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter allows to filter candles
     * @param numberOfCandlesBefore how much candles to load before and including candle with time specified in <code>time</code> parameter
     * @param time time of the last candle in period specified in <code>numberOfCandlesBefore</code> parameter, or
     *        time of the first candle in period specified with <code>numberOfCandlesAfter</code> parameter if <code>numberOfCandlesBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfCandlesAfter</code> if <code>numberOfCandlesBefore</code> is &gt; 0
     * @param numberOfCandlesAfter how much candles to load after (not including) candle with time specified in <code>time</code> parameter
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    List<IBar> getBars(Instrument instrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException;

    /**
     * Returns starting time of the bar that includes time specified in <code>time</code> parameter
     * 
     * @param period period of the bar
     * @param time time that is included by the bar
     * @return starting time of the bar
     * @throws JFException when period is not supported
     */
    long getBarStart(Period period, long time) throws JFException;
    
    /**
     * Returns starting time of the bar next to the bar that includes time specified in <code>barTime</code> parameter
     * 
     * @param period period of the bar
     * @param barTime time that is included by the bar previous to the returned
     * @return starting time of the bar
     * @throws JFException when period is not supported
     */
    long getNextBarStart(Period period, long barTime) throws JFException;
    
    /**
     * Returns starting time of the bar previous to the bar that includes time specified in <code>barTime</code> parameter
     * 
     * @param period period to the bar
     * @param barTime time that is included by the bar next to the returned
     * @return staring time of the bar
     * @throws JFException when period is not supported
     */
    long getPreviousBarStart(Period period, long barTime) throws JFException;
    
    /**
     * Returns starting time of the bar that is <code>numberOfBars - 1</code> back in time to the bar that includes time specified in
     * <code>to</code> parameter. Method can be used to get time for the <code>from</code> parameter for {@link #getBars}
     * method when is known time of the last bar and number of candles that needs to be loaded
     * 
     * @param period period of the bars
     * @param to time of the last bar
     * @param numberOfBars number of bars that could be loaded when passing returned time and time specified in <code>to</code> parameter in
     *        {@link #getBars} method
     * @return starting time of the bar
     * @throws JFException when period is not supported
     */
    long getTimeForNBarsBack(Period period, long to, int numberOfBars) throws JFException;
    
    /**
     * Returns starting time of the bar that is + <code>numberOfBars - 1</code> in the future to the bar that includes time specified in
     * <code>from</code> parameter. Method can be used to get time for the <code>to</code> parameter for {@link #getBars}
     * method when is known time of the first bar and number of candles that needs to be loaded
     * 
     * @param period period of the bars
     * @param from time of the first bar
     * @param numberOfBars number of bars that could be loaded when passing returned time and time specified in <code>from</code> parameter in
     *        {@link #getBars} method
     * @return starting time of the last bar
     * @throws JFException when period is not supported
     */
    long getTimeForNBarsForward(Period period, long from, int numberOfBars) throws JFException;

    /**
     * Loads orders from the server in the background. Method returns fast after creating request for data not waiting for any data to be loaded
     * After internal request is sent, orders will be returned by calling method in <code>ordersListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method can be used for orders loading without blocking strategy execution
     *
     * NB! Only one order history request can be sent at a time. If there is another request sent method will throw JFException
     *
     * @param instrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @param ordersListener receives data about requested orders
     * @param loadingProgress used to control loading progress
     * @throws JFException in case of any system error
     * @see #getOrdersHistory(Instrument instrument, long from, long to)
     */
    void readOrdersHistory(Instrument instrument, long from, long to, LoadingOrdersListener ordersListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns orders for specified instrument and time interval. Method blocks until all data will be loaded from the server.
     *
     * NB! Only one order history request can be sent at a time. If there is another request sent method will throw JFException
     *
     * @param instrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @return loaded orders
     * @throws JFException in case of any system error
     */
    List<IOrder> getOrdersHistory(Instrument instrument, long from, long to) throws JFException;

    /**
     * Returns historical (closed) order by position id for the given user account.
     * @param id closed order's position id
     * @return historical (closed) order with specified position id for the given user account or <code>null</code> if there is no one.
     * @throws JFException in case of any system error
     */
    IOrder getHistoricalOrderById(String id) throws JFException;

    /**
     * Returns open orders for specified instrument and time interval.
     *
     * @param instrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @return open orders
     */
    List<IOrder> getOpenOrders(Instrument instrument, long from, long to);

    /**
     * Returns current equity calculated for every tick
     * 
     * @return actual equity
     */
    double getEquity();

	/**
	 * Returns tick for specified instrument, that is shifted back in time for number in ticks specified in <code>shift</code>
     * parameter, 0 - current tick, 1 - previous tick.
     * 
     * <pre>
     * ITick tick0 = history.getTick(Instrument.EURUSD, 0);
     * ITick tick1 = history.getTick(Instrument.EURUSD, 1);
     * console.getOut().format("last tick: %s; previous tick: %s", tick0, tick1).println();   
     * </pre>
     * 
     * @param instrument instrument of the tick
     * @param shift number of tick back in time staring from current tick. 1 - previous tick, 2 - current tick minus 2 ticks and so on
     * @return tick
     * @throws JFException when instrument is not active (not opened in platform) or other errors
	 */
	public ITick getTick(Instrument instrument, int shift) throws JFException;

	/**
     * Returns bar for specified feed descriptor, that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar (currently generated), 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
	 * 
	 * @param feedDescriptor holds parameters: data type, instrument, offer side, etc (depends on bar type), and describes what kind of bars must be loaded 
	 * @param shift number of bars back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
	 * @return bar or null if no bar is loaded, the type of returning bar depends on {@link FeedDescriptor#getDataType()}
	 * @throws JFException when some error occurs when loading data
	 */
	ITimedData getFeedData(IFeedDescriptor feedDescriptor, int shift) throws JFException;

	/**
	 * A type-safe feed data retrieval for the specified feed
	 * 
     * @param <T> type of data feed element
	 * @param feedDescriptor feed descriptor for corresponding data type
	 * @param shift number of bars back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
	 * @return bar or null if no bar is loaded
	 * @throws JFException when some error occurs when loading data
	 * @see #getFeedData(IFeedDescriptor, int)
	 */
	<T extends ITimedData> T getFeedData(ITailoredFeedDescriptor<T> feedDescriptor, int shift) throws JFException;

    /**
     * A type-safe feed data retrieval for the specified feed
     *
     * @param <T> type of data feed element
	 * @param feedInfo feed info for corresponding data type
	 * @param shift number of bars back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
	 * @return bar or null if no bar is loaded
	 * @throws JFException when some error occurs when loading data
     * @see #getFeedData(IFeedDescriptor, int)
     */
    <T extends ITimedData> T getFeedData(ITailoredFeedInfo<T> feedInfo, int shift) throws JFException;

    /**
     * Returns bars for specified feed descriptor. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
	 * 
	 * @param feedDescriptor holds parameters: data type, instrument, offer side, etc (depends on bar type), and describes what kind of bars must be loaded
     * @param from start of the time interval for which bars should be loaded
     * @param to end time of the time interval for which bars should be loaded. If there is bar with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
	 * @return loaded bars
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 */
	List<ITimedData> getFeedData(IFeedDescriptor feedDescriptor, long from, long to) throws JFException;

    /**
	 * A type-safe feed data retrieval for the specified feed
	 * 
     * @param <T> type of data feed element
	 * @param feedDescriptor feed descriptor for corresponding data type
     * @param from start of the time interval for which bars should be loaded
     * @param to end time of the time interval for which bars should be loaded. If there is bar with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
	 * @return loaded bars
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @see #getFeedData(IFeedDescriptor, long, long)
	 */
	<T extends ITimedData> List<T> getFeedData(ITailoredFeedDescriptor<T> feedDescriptor, long from, long to) throws JFException;

    /**
	 * Returns bars for specified feedDescriptor. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     *
	 * @param feedDescriptor holds parameters: data type, instrument, offer side, etc (depends on bar type), and describes what kind of bars must be loaded
	 * @param numberOfFeedBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time the time of the last bar in period specified in <code>numberOfFeedBarsBefore</code> parameter, or
     *        the time of the first bar in period specified with <code>numberOfFeedBarsAfter</code> parameter if <code>numberOfFeedBarsBefore</code> is 0, or
     *        the time of the bar prior to the first bar in period specified with <code>numberOfFeedBarsAfter</code> if <code>numberOfFeedBarsBefore</code> is &gt; 0
	 * @param numberOfFeedBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when some error occurs when loading data
	 */
	List<ITimedData> getFeedData(IFeedDescriptor feedDescriptor, int numberOfFeedBarsBefore, long time, int numberOfFeedBarsAfter) throws JFException;

	/**
	 * A type-safe feed data retrieval for the specified feed
	 * 
     * @param <T> type of data feed element
	 * @param feedDescriptor feed descriptor for corresponding data type
	 * @param numberOfFeedBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time the time of the last bar in period specified in <code>numberOfFeedBarsBefore</code> parameter, or
     *        the time of the first bar in period specified with <code>numberOfFeedBarsAfter</code> parameter if <code>numberOfFeedBarsBefore</code> is 0, or
     *        the time of the bar prior to the first bar in period specified with <code>numberOfFeedBarsAfter</code> if <code>numberOfFeedBarsBefore</code> is &gt; 0
	 * @param numberOfFeedBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when some error occurs when loading data
	 * @see #getFeedData(ITailoredFeedDescriptor, int, long, int)
	 */
	<T extends ITimedData> List<T> getFeedData(ITailoredFeedDescriptor<T> feedDescriptor, int numberOfFeedBarsBefore, long time, int numberOfFeedBarsAfter) throws JFException;

    /**
     * Reads feed data from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, feed data items will be returned by calling method in <code>feedListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * 
     * <p> Consider analyzing the last weeks renko bricks - namely calculating the average close price
     *
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * private Instrument instrument = Instrument.EURUSD;
     * private IFeedDescriptor feedDescriptor = new RenkoFeedDescriptor(instrument, PriceRange.TWO_PIPS, OfferSide.ASK);
     * private double avgPrice = 0;
     * private int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *
     *     final long to = history.getFeedData(feedDescriptor, 1).getTime();
     *     final long from = to - Period.DAILY.getInterval() * 7;
     *
     *     history.readFeedData(
     *         feedDescriptor,
     *         from,
     *         to,
     *         new IFeedListener() {
     *             {@literal @}Override
     *             public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {
     *                 itemCount++;
     *                 double price = feedDescriptor.getDataType() == DataType.TICKS
     *                         ? ((ITick)feedData).getBid()
     *                         : ((IBar)feedData).getClose();
     *                 avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double)itemCount));
     *             }
     *
     *             private double round(double amount) {
     *                 return (new BigDecimal(amount)).setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *             }
     *         },
     *         new LoadingProgressListener() {
     *             {@literal @}Override
     *             public void dataLoaded(long start, long end, long currentPosition, String information) {
     *             }
     *
     *             {@literal @}Override
     *             public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *                 console.getOut().format("Loaded %s %s %s  - %s average %s=%.5f    feed descriptor=%s",
     *                         itemCount,
     *                         feedDescriptor.getDataType(),
     *                         DateUtils.format(from),
     *                         DateUtils.format(to),
     *                         feedDescriptor.getDataType() == DataType.TICKS ? "bid" : "close",
     *                         avgPrice,
     *                         feedDescriptor
     *                 ).println();
     *                context.stop();
     *             }
     *
     *             {@literal @}Override
     *             public boolean stopJob() {
     *                 return false;
     *             }
     *         }
     *     );
     * }
     * </pre>
     *
     * @param feedDescriptor feed descriptor of the feed data
     * @param from start of the time interval for which feed data items should be loaded.
     * @param to end time of the time interval for which feed data items should be loaded.
     * @param feedListener receives data about requested feed data items
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     */
	void readFeedData(IFeedDescriptor feedDescriptor, long from, long to, IFeedListener feedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
	 * A type-safe feed data retrieval for the specified feed
	 *
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * Instrument instrument = Instrument.EURUSD;
     * ITailoredFeedDescriptor{@literal <IRenkoBar>} feedDescriptor = new RenkoFeedDescriptor(instrument, PriceRange.TWO_PIPS, OfferSide.ASK);
     * double avgPrice = 0;
     * int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *
     *     final long to = history.getFeedData(feedDescriptor, 1).getTime();
     *     final long from = to - Period.DAILY.getInterval() * 7;
     *
     *     history.readFeedData(
     *         feedDescriptor,
     *         from,
     *         to,
     *         new ITailoredFeedListener{@literal <IRenkoBar>}() {
     *             {@literal @}Override
     *             public void onFeedData(ITailoredFeedDescriptor{@literal <IRenkoBar>} feedDescriptor, IRenkoBar renko) {
     *                 itemCount++;
     *                 double price = renko.getClose();
     *                 avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double)itemCount));
     *             }
     *
     *             private double round(double amount) {
     *                 return (new BigDecimal(amount)).setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *             }
     *         },
     *         new LoadingProgressListener() {
     *             {@literal @}Override
     *             public void dataLoaded(long start, long end, long currentPosition, String information) {
     *             }
     *
     *             {@literal @}Override
     *             public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *                 console.getOut().format("Loaded %s Renkos %s  - %s average close=%.5f    feed descriptor=%s",
     *                         itemCount,
     *                         DateUtils.format(from),
     *                         DateUtils.format(to),
     *                         avgPrice,
     *                         feedDescriptor
     *                 ).println();
     *                 context.stop();
     *             }
     *
     *             {@literal @}Override
     *             public boolean stopJob() {
     *                 return false;
     *             }
     *         }
     *     );
     * }
     * </pre>
	 *
     * @param <T> type of data feed element
     * @param feedDescriptor feed descriptor of the feed data
     * @param from start of the time interval for which feed data items should be loaded
     * @param to end time of the time interval for which feed data items should be loaded
     * @param feedListener receives data about requested feed data items
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
	 * @see #readFeedData(IFeedDescriptor, long, long, IFeedListener, LoadingProgressListener)
	 */
	<T extends ITimedData> void readFeedData(ITailoredFeedDescriptor<T> feedDescriptor, long from, long to, ITailoredFeedListener<T> feedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads feed data from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, feed data will be returned by calling method in <code>feedListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of feed data without keeping them all in memory, and second is asynchronous processing
     * 
     * <p> Consider analyzing the last 1000 renko bricks - namely calculating the average close price
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * private Instrument instrument = Instrument.EURUSD;
     * private IFeedDescriptor feedDescriptor = new RenkoFeedDescriptor(Instrument.EURUSD, PriceRange.TWO_PIPS, OfferSide.ASK);
     * private double avgPrice = 0;
     * private int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *     final long to = history.getFeedData(feedDescriptor, 1).getTime();
     *
     *     history.readFeedData(
     *         feedDescriptor,
     *         1000,
     *         to,
     *         0,
     *         new IFeedListener() {
     *             {@literal @}Override
     *             public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {
     *                 itemCount++;
     *                 double price = feedDescriptor.getDataType() == DataType.TICKS
     *                       ? ((ITick)feedData).getBid()
     *                       : ((IBar)feedData).getClose();
     *                 avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double)itemCount));
     *             }
     *
     *             private double round(double amount) {
     *                 return (new BigDecimal(amount)).setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *             }
     *         },
     *         new LoadingProgressListener() {
     *             {@literal @}Override
     *             public void dataLoaded(long start, long end, long currentPosition, String information) {
     *             }
     *
     *             {@literal @}Override
     *             public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *                 console.getOut().format("Loaded %s %s till %s average %s=%.5f    feed descriptor=%s",
     *                       itemCount,
     *                       feedDescriptor.getDataType(),
     *                       DateUtils.format(to),
     *                       feedDescriptor.getDataType() == DataType.TICKS ? "bid" : "close",
     *                       avgPrice,
     *                       feedDescriptor
     *                 ).println();
     *                 context.stop();
     *             }
     *
     *             {@literal @}Override
     *             public boolean stopJob() {
     *                 return false;
     *             }
     *         }
     *     );
     * }
     * </pre>
     *   
     * @param feedDescriptor feed descriptor of the feed data
     * @param numberOfFeedDataBefore how much feed data items to load before and including feed data item with time specified in <code>time</code> parameter
     * @param time time of the last feed data item in period specified in <code>numberOfFeedDataBefore</code> parameter or/and
     *        time of the feed data item prior first feed data item in period specified with <code>numberOfFeedDataAfter</code> parameter
     * @param numberOfFeedDataAfter how much feed data items to load after (not including) feed data item with time specified in <code>time</code> parameter
     * @param feedListener receives feed data
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     */
	void readFeedData(IFeedDescriptor feedDescriptor, int numberOfFeedDataBefore, long time, int numberOfFeedDataAfter, IFeedListener feedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
	 * A type-safe feed data retrieval for the specified feed
	 * 
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * private Instrument instrument = Instrument.EURUSD;
     * private ITailoredFeedDescriptor{@literal <IRenkoBar>} feedDescriptor = new RenkoFeedDescriptor(Instrument.EURUSD, PriceRange.TWO_PIPS, OfferSide.ASK);
     * private double avgPrice = 0;
     * private int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *     final long to = history.getFeedData(feedDescriptor, 1).getTime();
     *
     *     history.readFeedData(
     *         feedDescriptor,
     *         1000,
     *         to,
     *         0,
     *         new ITailoredFeedListener{@literal <IRenkoBar>}() {
     *             {@literal @}Override
     *             public void onFeedData(ITailoredFeedDescriptor{@literal <IRenkoBar>} feedDescriptor, IRenkoBar renko) {
     *                 itemCount++;
     *                 double price = renko.getClose();
     *                 avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double)itemCount));
     *             }
     *             private double round(double amount) {
     *                  return (new BigDecimal(amount)).setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *             }
     *         },
     *         new LoadingProgressListener() {
     *             {@literal @}Override
     *             public void dataLoaded(long start, long end, long currentPosition, String information) {
     *             }
     *
     *             {@literal @}Override
     *             public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *                 console.getOut().format("Loaded %s Renkos till %s average close=%.5f feed descriptor=%s",
     *                         itemCount,
     *                         DateUtils.format(to),
     *                         avgPrice,
     *                         feedDescriptor
     *                 ).println();
     *                 context.stop();
     *             }
     *
     *             {@literal @}Override
     *             public boolean stopJob() {
     *                 return false;
     *             }
     *         }
     *     );
     * }
     * </pre>
	 * 
     * @param <T> type of data feed element
     * @param feedDescriptor feed descriptor of the feed data
     * @param numberOfFeedDataBefore how much feed data items to load before and including feed data item with time specified in <code>time</code> parameter
     * @param time time of the last feed data item in period specified in <code>numberOfFeedDataBefore</code> parameter or/and
     *        time of the feed data item prior first feed data item in period specified with <code>numberOfFeedDataAfter</code> parameter
     * @param numberOfFeedDataAfter how much feed data items to load after (not including) feed data item with time specified in <code>time</code> parameter
     * @param feedListener receives feed data
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
	 * @see #readFeedData(IFeedDescriptor, int, long, int, IFeedListener, LoadingProgressListener)
	 */
	<T extends ITimedData> void readFeedData(ITailoredFeedDescriptor<T> feedDescriptor, int numberOfFeedDataBefore, long time, int numberOfFeedDataAfter, ITailoredFeedListener<T> feedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * A type-safe feed data retrieval for the specified feed.
     *
     * <pre>{@code
     * private IHistory history;
     * private IConsole console;
     * private Instrument instrument = Instrument.EURUSD;
     *
     * public void onStart(IContext context) throws JFException {
     *     this.history = context.getHistory();
     *     this.console = context.getConsole();
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *
     *     RenkoFeedDescriptor renkoFeedDescriptor = new RenkoFeedDescriptor(
     *             instrument,
     *             PriceRange.TWO_PIPS,
     *             OfferSide.ASK);
     *
     *     ITimedData feedData = history.getFeedData(renkoFeedDescriptor, 1);
     *
     *     double average = history.readFeedData(renkoFeedDescriptor, 10, feedData.getTime(), 0)
     *             .mapToDouble(renkoBar -> renkoBar.getClose())
     *             .average()
     *             .getAsDouble();
     *
     *     console.getOut().format("Average renko close pirce is %s", average).println();
     *     context.stop();
     * }
     * }</pre>
     *
     * @param <T> type of data feed element
     * @param feedDescriptor feed descriptor of the feed data
     * @param numberOfFeedDataBefore how much feed data items to load before and including feed data item with time specified in <code>time</code> parameter
     * @param time time of the last feed data item in period specified in <code>numberOfFeedDataBefore</code> parameter or/and
     *        time of the feed data item prior first feed data item in period specified with <code>numberOfFeedDataAfter</code> parameter
     * @param numberOfFeedDataAfter how much feed data items to load after (not including) feed data item with time specified in <code>time</code> parameter
     * @param loadingProgressListener used to control loading progress
     * @return stream of loaded bars
     * @throws JFException when some errors occurs
     */
    <T extends ITimedData> Stream<T> readFeedData(
            ITailoredFeedDescriptor<T> feedDescriptor,
            int numberOfFeedDataBefore,
            long time,
            int numberOfFeedDataAfter,
            LoadingProgressListener loadingProgressListener) throws JFException;

    /**
     * A type-safe feed data retrieval for the specified feed.
     *
     * <pre>
     * private IHistory history;
     * private IConsole console;
     * private Instrument instrument = Instrument.EURUSD;
     *
     * public void onStart(IContext context) throws JFException {
     *     this.history = context.getHistory();
     *     this.console = context.getConsole();
     *     context.setSubscribedInstruments(Collections.singleton(instrument), true);
     *
     *     RenkoFeedDescriptor renkoFeedDescriptor = new RenkoFeedDescriptor(
     *             instrument,
     *             PriceRange.TWO_PIPS,
     *             OfferSide.ASK);
     *
     *     ITimedData feedData10 = history.getFeedData(renkoFeedDescriptor, 10);
     *     ITimedData feedData1 = history.getFeedData(renkoFeedDescriptor, 1);
     *
     *     LoadingProgressListener loadingProgressListener = new LoadingProgressListener() {
     *
     *        {@literal @Override}
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *        {@literal @Override}
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *         }
     *
     *        {@literal @Override}
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     };
     *
     *     double average = history.readFeedData(
     *             renkoFeedDescriptor, feedData10.getTime(), feedData1.getTime(), loadingProgressListener)
     *             .mapToDouble(renkoBar{@literal ->} renkoBar.getClose())
     *             .average()
     *             .getAsDouble();
     *
     *     console.getOut().format("Average renko close pirce is %s", average).println();
     *     context.stop();
     * }
     * </pre>
	 *
     * @param <T> type of data feed element
     * @param feedDescriptor feed descriptor of the feed data
     * @param from start of the time interval for which feed data items should be loaded
     * @param to end time of the time interval for which feed data items should be loaded
     * @param loadingProgressListener used to control loading progress
     * @return stream of loaded bars
     * @throws JFException when some errors occurs
     */
    <T extends ITimedData> Stream<T> readFeedData(
            ITailoredFeedDescriptor<T> feedDescriptor,
            long from,
            long to,
            LoadingProgressListener loadingProgressListener) throws JFException;

    /////////////////////////// deprecated methods

    /**
     * Reads ticks from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, ticks will be returned by calling method in <code>tickListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of ticks without keeping them all in memory, and second is asynchronous processing
     *
     * @param financialInstrument financial instrument of the ticks
     * @param numberOfOneSecondIntervalsBefore how much one second interval of ticks to load before and including one second interval of ticks with time specified in <code>time</code> parameter
     *
     * @param time time of the last one second tick interval in period specified in <code>numberOfOneSecondIntervalsBefore</code> parameter or/and
     *        time of the one second tick interval prior first one second tick interval in period specified with <code>numberOfOneSecondIntervalsAfter</code> parameter
     * @param numberOfOneSecondIntervalsAfter how much one second tick intervals to load after (not including) one second tick interval with time specified in <code>time</code> parameter
     * @param tickListener receives data about requested ticks
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     */
    @Deprecated
    void readTicks(IFinancialInstrument financialInstrument, int numberOfOneSecondIntervalsBefore, long time, int numberOfOneSecondIntervalsAfter, LoadingDataListener tickListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns bars for specified instrument, period, side and filter.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     *
     * @param financialInstrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter bars filtering method see {@link Filter}
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    @Deprecated
    List<IBar> getBars(IFinancialInstrument financialInstrument, Period period, OfferSide side, Filter filter, long from, long to) throws JFException;


    /**
     * Returns bars for specified feed info. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     *
     * @param feedInfo holds parameters: data type, financial instrument, offer side, etc (depends on bar type), and describes what kind of bars must be loaded
     * @param from start of the time interval for which bars should be loaded
     * @param to end time of the time interval for which bars should be loaded. If there is bar with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @return loaded bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     */
    @Deprecated
    List<ITimedData> getFeedData(IFeedInfo feedInfo, long from, long to) throws JFException;

    /**
     * A type-safe feed data retrieval for the specified feed
     *
     * @param <T> type of data feed element
     * @param feedInfo feed info for corresponding data type
     * @param from start of the time interval for which bars should be loaded
     * @param to end time of the time interval for which bars should be loaded. If there is bar with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @return loaded bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     * @see #getFeedData(IFeedInfo, long, long)
     */
    @Deprecated
    <T extends ITimedData> List<T> getFeedData(ITailoredFeedInfo<T> feedInfo, long from, long to) throws JFException;

    /**
     * Returns time of last tick received for specified instrument. Returns -1 if no tick was received yet.
     *
     * @param financialInstrument instrument of the tick
     * @return time of last tick or -1 if no tick was received
     * @throws JFException when instrument is not active (not opened in platform)
     */
    @Deprecated
    long getTimeOfLastTick(IFinancialInstrument financialInstrument) throws JFException;

    /**
     * Returns last tick for specified instrument
     *
     * @param financialInstrument instrument of the tick
     * @return tick
     * @throws JFException when instrument is not active (not opened in platform)
     */
    @Deprecated
    ITick getLastTick(IFinancialInstrument financialInstrument) throws JFException;

    /**
     * Returns starting time of the current bar (bar currently generated from ticks) for specified instrument and period.
     * If no tick was received for this instrument, then returns -1.
     *
     * @param financialInstrument instrument of the bar
     * @param period period of the bar
     * @return starting time of the current bar or -1 if no tick was received
     * @throws JFException when period is not supported or instrument is not active (not opened in platform)
     */
    @Deprecated
    long getStartTimeOfCurrentBar(IFinancialInstrument financialInstrument, Period period) throws JFException;

    /**
     * Returns bar for specified instrument, period and side, that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar (currently generated from ticks), 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
     *
     * <p> consider getting the previous one hour bar
     *
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     IBar prevBar = history.getBar(financialInstrument, Period.ONE_HOUR, OfferSide.BID, 1);
     *     console.getOut().println(prevBar);
     * }
     * </pre>
     *
     * @param financialInstrument instrument of the bar
     * @param period period of the bar
     * @param side bid or ask side of the bar
     * @param shift number of candle back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
     * @return bar or null if no bar is loaded
     * @throws JFException when period is not supported or instrument is not active (not opened in platform)
     */
    @Deprecated
    IBar getBar(IFinancialInstrument financialInstrument, Period period, OfferSide side, int shift) throws JFException;

    /**
     * Reads ticks from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, ticks will be returned by calling methods in <code>tickListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error.
     * This method has two main purposes: one is to load a lot of ticks without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the last days ticks - namely calculating the average ask price and the maximum bid
     * <pre>
     *     private IHistory history;
     *     private IConsole console;
     *
     *     double maxBid = Double.MIN_VALUE;
     *     double avgAsk = 0;
     *     int tickCount = 0;
     *
     *     {@literal @}Override
     *     public void onStart(final IContext context) throws JFException {
     *         history = context.getHistory();
     *         console = context.getConsole();
     *         IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *         final IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *
     *         context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *         final long from = history.getLastTick(financialInstrument).getTime() - Period.DAILY.getInterval();
     *         final long to = history.getLastTick(financialInstrument).getTime();
     *
     *         history.readTicks(financialInstrument, from, to,
     *             new LoadingFinancialDataListener() {
     *                 {@literal @}Override
     *                 public void newTick(IFinancialInstrument financialInstrument, long time, double ask, double bid, double askVol, double bidVol) {
     *                     tickCount++;
     *                     maxBid = Math.max(bid, maxBid);
     *                     avgAsk = tickCount == 1 ? ask : round(avgAsk + ((avgAsk - ask) / (double)tickCount));
     *                 }
     *
     *                 private double round(double amount) {
     *                     return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale(), BigDecimal.ROUND_HALF_UP).doubleValue();
     *                 }
     *
     *                 {@literal @}Override
     *                 public void newBar(IFinancialInstrument financialInstrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *                     // no bars expected
     *                 }
     *             },
     *
     *             new LoadingProgressListener() {
     *
     *                 {@literal @}Override
     *                 public void dataLoaded(long start, long end, long currentPosition, String information) {
     *
     *                 }
     *
     *                 {@literal @}Override
     *                 public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *                     console.getOut().format("Loaded %s ticks %s - %s max Bid=%.5f average ask=%.5f",
     *                     tickCount, DateUtils.format(from), DateUtils.format(to), maxBid, avgAsk).println();
     *                     context.stop();
     *                 }
     *
     *                 {@literal @}Override
     *                 public boolean stopJob() {
     *                     return false;
     *                 }
     *             });
     *         }
     * </pre>
     *
     * @param financialInstrument instrument of the ticks
     * @param from start of the time interval for which ticks should be loaded
     * @param to end time of the time interval for which ticks should be loaded. If there is tick with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @param tickListener receives data about requested ticks
     * @param loadingProgress used to control loading process
     * @throws JFException when some error occurs while creating internal request for data
     */
    @Deprecated
    void readTicks(IFinancialInstrument financialInstrument, long from, long to, DataLoadingListener tickListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>barListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the last weeks 1 minute bars - namely calculating the average close price and the maximum bar size
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * double maxHeight = Double.MIN_VALUE, avgClose = 0;
     * int barCount, greenCount;
     *
     * private Period period = Period.ONE_MIN;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *     final IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *     long lastTickTime = history.getLastTick(financialInstrument).getTime();
     *     final long from = history.getBarStart(period, lastTickTime - Period.DAILY.getInterval() * 7);
     *     final long to = history.getTimeForNBarsBack(period, lastTickTime, 2);
     *
     *     history.readBars(financialInstrument, period, OfferSide.BID, from, to,
     *
     *         new LoadingFinancialDataListener() {
     *             {@literal @}Override
     *             public void newTick(IFinancialInstrument financialInstrument, long time, double ask, double bid, double askVol, double bidVol) {}
     *
     *             {@literal @}Override
     *             public void newBar(IFinancialInstrument financialInstrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *                 barCount++;
     *                 maxHeight = Math.max(Math.abs(open - close), maxHeight);
     *                 avgClose = barCount == 1 ? close : round(avgClose + ((close - avgClose) / (double)barCount));
     *                 if(close - open &gt; 0){
     *                     greenCount++;
     *                 }
     *             }
     *
     *             private double round(double amount) {
     *                 return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *             }
     *         },
     *         new LoadingProgressListener() {
     *
     *             {@literal @}Override
     *             public void dataLoaded(long start, long end, long currentPosition, String information) {
     *             }
     *
     *             {@literal @}Override
     *             public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *                 console.getOut().format("Loaded %s %s candles (%s green, %s red) %s  - %s max bar height=%.1fpips average close=%.5f",
     *                         barCount, period, greenCount, barCount - greenCount, DateUtils.format(from), DateUtils.format(to), maxHeight/financialInstrument.getPipValue(), avgClose).println();
     *                 context.stop();
     *             }
     *
     *             {@literal @}Override
     *             public boolean stopJob() {
     *                 return false;
     *             }
     *         });
     * }
     * </pre>
     *
     * @param financialInstrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for bar that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last bar to be loaded
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    @Deprecated
    void readBars(IFinancialInstrument financialInstrument, Period period, OfferSide side, long from, long to, DataLoadingListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background.
     * Method returns fast after creating request for data not waiting for any data to be read from local cache.
     * After internal request is sent, bars will be returned by calling method in <code>barListener</code>.
     * LoadingProgressListener is used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the last weeks 1 minute bars - namely calculating the average close price and the maximum bar size
     * <pre>
     * int barCount = 0;
     * int greenCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *	 history = context.getHistory();
     *	 console = context.getConsole();
     *	 IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *	 final IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *
     *	 context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *	 long lastTickTime = history.getLastTick(financialInstrument).getTime();
     *	 final long from = history.getBarStart(period, lastTickTime - Period.DAILY.getInterval() * 7);
     *	 final long to = history.getTimeForNBarsBack(period, lastTickTime, 2);
     *
     *	 history.readBars(financialInstrument, period, OfferSide.BID, Filter.WEEKENDS, from, to,
     *
     *		 new LoadingFinancialDataListener() {
     *			 {@literal @}Override
     *			 public void newTick(IFinancialInstrument financialInstrument, long time, double ask, double bid, double askVol, double bidVol) {}
     *
     *			 {@literal @}Override
     *			 public void newBar(IFinancialInstrument financialInstrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *				 barCount++;
     *				 maxHeight = Math.max(Math.abs(open - close), maxHeight);
     *				 avgClose = barCount == 1 ? close : round(avgClose + ((close - avgClose) / (double)barCount));
     *				 if(close - open &gt; 0){
     *					greenCount++;
     *				}
     *			 }
     *
     *			 private double round(double amount) {
     *				return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *			 }
     *		 },
     *		 new LoadingProgressListener() {
     *
     *			 {@literal @}Override
     *			 public void dataLoaded(long start, long end, long currentPosition, String information) {
     *			 }
     *
     *			 {@literal @}Override
     *			 public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *				 console.getOut().format("Loaded %s %s candles (%s green, %s red) %s  - %s max bar height=%.1fpips average close=%.5f",
     *				 barCount, period, greenCount, barCount - greenCount, DateUtils.format(from), DateUtils.format(to), maxHeight/financialInstrument.getPipValue(), avgClose).println();
     *				 context.stop();
     *			 }
     *
     *			 {@literal @}Override
     *			 public boolean stopJob() {
     *				 return false;
     *			 }
     *		 }
     *	 );
     * }
     * </pre>
     *
     * @param financialInstrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter bars filtering method see {@link Filter}
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for bar that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last bar to be loaded
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    @Deprecated
    void readBars(IFinancialInstrument financialInstrument, Period period, OfferSide side, Filter filter, long from, long to, DataLoadingListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>barListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the 100000 1 minute bars - namely calculating the average close price and the maximum bar size
     * <pre>
     *
     * private IHistory history;
     * private IConsole console;
     *
     * double maxHeight = Double.MIN_VALUE, avgClose = 0;
     * int barCount, greenCount;
     * private Period period = Period.ONE_MIN;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *     final IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *     long lastTickTime = history.getLastTick(financialInstrument).getTime();
     *     final long to = history.getTimeForNBarsBack(period, lastTickTime, 2);
     *
     *     history.readBars(financialInstrument, period, OfferSide.BID, Filter.WEEKENDS, 100000, to, 0,
     *
     *     new LoadingFinancialDataListener() {
     *         {@literal @}Override
     *         public void newTick(IFinancialInstrument financialInstrument, long time, double ask, double bid, double askVol, double bidVol) {}
     *
     *         {@literal @}Override
     *         public void newBar(IFinancialInstrument financialInstrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol) {
     *             barCount++;
     *             maxHeight = Math.max(Math.abs(open - close), maxHeight);
     *             avgClose = barCount == 1 ? close : round(avgClose + ((close - avgClose) / (double)barCount));
     *             if(close - open &gt; 0){
     *                 greenCount++;
     *             }
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     },
     *     new LoadingProgressListener() {
     *
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s %s candles (%s green, %s red) till %s max bar height=%.1fpips average close=%.5f",
     *                     barCount, period, greenCount, barCount - greenCount, DateUtils.format(to), maxHeight/financialInstrument.getPipValue(), avgClose).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     });
     * }
     * </pre>
     * @param financialInstrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter allows to filter candles
     * @param numberOfCandlesBefore how much candles to load before and including candle with time specified in <code>time</code> parameter
     * @param time time of the last candles in period specified in <code>numberOfCandlesBefore</code> parameter or/and
     *        time of the candle prior first candle in period specified with <code>numberOfCandlesAfter</code> parameter
     * @param numberOfCandlesAfter how much candles to load after (not including) candle with time specified in <code>time</code> parameter
     * @param barListener receives data about requested bars
     * @param loadingProgress used to control loading progress
     * @throws JFException when period is not supported or time interval is not valid for specified period
     * @see #getBarStart(Period, long)
     */
    @Deprecated
    void readBars(IFinancialInstrument financialInstrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter, DataLoadingListener barListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns ticks for specified instrument and time interval. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     *
     * <pre>
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     this.history = context.getHistory();
     *     this.console = context.getConsole();
     *
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     ITick lastTick = history.getLastTick(financialInstrument);
     *     List{@literal <ITick>} = history.getTicks(financialInstrument, lastTick.getTime() - 10 * 1000, lastTick.getTime());
     *     int last = ticks.size() - 1;
     *     console.getOut().format("Tick count=%s; Latest bid price=%.5f, time=%s; Oldest bid price=%.5f, time=%s",
     *         ticks.size(), ticks.get(last).getBid(), DateUtils.format(ticks.get(last).getTime()), ticks.get(0).getBid(), DateUtils.format(ticks.get(last).getTime()) ).println();
     * }
     * </pre>
     *
     * @param financialInstrument instrument of the ticks
     * @param from start of the time interval for which ticks should be loaded
     * @param to end time of the time interval for which ticks should be loaded. If there is tick with time equals to the time in <code>to</code>
     *        parameter then it will be loaded as well
     * @return loaded ticks
     * @throws JFException when some error occurs when loading data
     */
    @Deprecated
    List<ITick> getTicks(IFinancialInstrument financialInstrument, long from, long to) throws JFException;

    /**
     * Returns ticks for specified instrument, time and count. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     *
     * @param financialInstrument instrument of the ticks
     * @param numberOfOneSecondIntervalsBefore how much one second interval of ticks to load before and including one second interval of ticks with time specified in <code>time</code> parameter
     * @param time time of the last one second tick interval in period specified in <code>numberOfOneSecondIntervalsBefore</code> parameter or/and
     *        time of the one second tick interval prior first one second tick interval in period specified with <code>numberOfOneSecondIntervalsAfter</code> parameter
     * @param numberOfOneSecondIntervalsAfter how much one second tick intervals to load after (not including) one second tick interval with time specified in <code>time</code> parameter
     * @return loaded ticks
     * @throws JFException when some error occurs when loading data
     */
    @Deprecated
    List<ITick> getTicks(IFinancialInstrument financialInstrument, int numberOfOneSecondIntervalsBefore, long time, int numberOfOneSecondIntervalsAfter) throws JFException;

    /**
     * Returns bars for specified instrument, period and side. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     *
     * <p>Consider getting 5 bars over a time interval:
     * <pre>
     *
     * private IHistory history;
     * private IConsole console;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *     final IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *
     *     long prevBarTime = history.getPreviousBarStart(Period.ONE_HOUR, history.getLastTick(financialInstrument).getTime());
     *     long startTime =  history.getTimeForNBarsBack(Period.ONE_HOUR, prevBarTime, 5);
     *     List{@literal <IBar>} bars = history.getBars(financialInstrument, Period.ONE_HOUR, OfferSide.BID, startTime, prevBarTime);
     *     int last = bars.size() - 1;
     *     console.getOut().format(
     *         "Previous bar close price=%.5f; 4th to previous bar close price=%.5f",
     *             bars.get(last).getClose(), bars.get(0).getClose()
     *     ).println();
     * }
     *
     * </pre>
     *
     * @param financialInstrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param from start of the time interval for which bars should be loaded. Should be the exact starting time of the bar for specified period.
     *        See {@link #getBarStart(Period, long)} description if you want to get bar starting time for candle that includes specific time
     * @param to end time of the time interval for which bars should be loaded. This is the starting time of the last candle to be loaded
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    @Deprecated
    List<IBar> getBars(IFinancialInstrument financialInstrument, Period period, OfferSide side, long from, long to) throws JFException;

    /**
     * Returns bars for specified instrument, period and side. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>. If the requested period includes the bar that is not fully
     * formed yet (in-progress bar), then it is included even if it's flat
     *
     * <p>Consider getting 5 bars over a candle interval:
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     long prevBarTime = history.getPreviousBarStart(Period.ONE_HOUR, history.getLastTick(financialInstrument).getTime());
     *     List{@literal <IBar>} bars = history.getBars(financialInstrument, Period.ONE_HOUR, OfferSide.BID, Filter.NO_FILTER, 5, prevBarTime, 0);
     *     int last = bars.size() - 1;
     *     console.getOut().format(
     *         "Previous bar close price=%.5f; 4th to previous bar close price=%.5f",
     *         bars.get(last).getClose(), bars.get(0).getClose()).println();
     * }
     * </pre>
     *
     * @param financialInstrument instrument of the bars
     * @param period period of the bars
     * @param side side of the bars
     * @param filter allows to filter candles
     * @param numberOfCandlesBefore how much candles to load before and including candle with time specified in <code>time</code> parameter
     * @param time time of the last candle in period specified in <code>numberOfCandlesBefore</code> parameter, or
     *        time of the first candle in period specified with <code>numberOfCandlesAfter</code> parameter if <code>numberOfCandlesBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfCandlesAfter</code> if <code>numberOfCandlesBefore</code> is &gt; 0
     * @param numberOfCandlesAfter how much candles to load after (not including) candle with time specified in <code>time</code> parameter
     * @return loaded bars
     * @throws JFException when period is not supported or some error occurs when loading data
     */
    @Deprecated
    List<IBar> getBars(IFinancialInstrument financialInstrument, Period period, OfferSide side, Filter filter, int numberOfCandlesBefore, long time, int numberOfCandlesAfter) throws JFException;

    /**
     * Loads orders from the server in the background. Method returns fast after creating request for data not waiting for any data to be loaded
     * After internal request is sent, orders will be returned by calling method in <code>ordersListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method can be used for orders loading without blocking strategy execution
     *
     * NB! Only one order history request can be sent at a time. If there is another request sent method will throw JFException
     *
     * @param financialInstrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @param ordersListener receives data about requested orders
     * @param loadingProgress used to control loading progress
     * @throws JFException in case of any system error
     * @see #getOrdersHistory(IFinancialInstrument financialInstrument, long from, long to)
     */
    @Deprecated
    void readOrdersHistory(IFinancialInstrument financialInstrument, long from, long to, OrdersLoadingListener ordersListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns orders for specified instrument and time interval. Method blocks until all data will be loaded from the server.
     *
     * NB! Only one order history request can be sent at a time. If there is another request sent method will throw JFException
     *
     * @param financialInstrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @return loaded orders
     * @throws JFException in case of any system error
     */
    @Deprecated
    List<IOrder> getOrdersHistory(IFinancialInstrument financialInstrument, long from, long to) throws JFException;

    /**
     * Returns open orders for specified instrument and time interval.
     *
     * @param financialInstrument instrument of the orders
     * @param from start of the time interval for which orders should be loaded
     * @param to end time of the time interval for which orders should be loaded
     * @return open orders
     */
    @Deprecated
    List<IOrder> getOpenOrders(IFinancialInstrument financialInstrument, long from, long to);

    /**
     * Returns tick for specified instrument, that is shifted back in time for number in ticks specified in <code>shift</code>
     * parameter, 0 - current tick, 1 - previous tick.
     *
     * <pre>
     * IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     * final IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     * ITick tick0 = history.getTick(financialInstrument, 0);
     * ITick tick1 = history.getTick(financialInstrument, 1);
     * console.getOut().format("last tick: %s; previous tick: %s", tick0, tick1).println();
     * </pre>
     *
     * @param financialInstrument instrument of the tick
     * @param shift number of tick back in time staring from current tick. 1 - previous tick, 2 - current tick minus 2 ticks and so on
     * @return tick
     * @throws JFException when instrument is not active (not opened in platform) or other errors
     */
    @Deprecated
    public ITick getTick(IFinancialInstrument financialInstrument, int shift) throws JFException;

    /**
     * Returns bar for specified feed info, that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar (currently generated), 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
     *
     * @param feedInfo holds parameters: data type, financial instrument, offer side, etc (depends on bar type), and describes what kind of bars must be loaded
     * @param shift number of bars back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
     * @return bar or null if no bar is loaded, the type of returning bar depends on {@link IFeedInfo#getDataType()}
     * @throws JFException when some error occurs when loading data
     */
    @Deprecated
    ITimedData getFeedData(IFeedInfo feedInfo, int shift) throws JFException;

    /**
     * Returns bars for specified feedInfo. Method blocks until all data will be loaded from the server into local cache
     * and then read and stored in collection. Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     *
     * @param feedInfo holds parameters: data type, financial instrument, offer side, etc (depends on bar type), and describes what kind of bars must be loaded
     * @param numberOfFeedBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
     * @param time the time of the last bar in period specified in <code>numberOfFeedBarsBefore</code> parameter, or
     *        the time of the first bar in period specified with <code>numberOfFeedBarsAfter</code> parameter if <code>numberOfFeedBarsBefore</code> is 0, or
     *        the time of the bar prior to the first bar in period specified with <code>numberOfFeedBarsAfter</code> if <code>numberOfFeedBarsBefore</code> is &gt; 0
     * @param numberOfFeedBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
     * @return loaded bars
     * @throws JFException when some error occurs when loading data
     */
    @Deprecated
    List<ITimedData> getFeedData(IFeedInfo feedInfo, int numberOfFeedBarsBefore, long time, int numberOfFeedBarsAfter) throws JFException;

    /**
     * A type-safe feed data retrieval for the specified feed
     *
     * @param <T> type of data feed element
     * @param feedInfo feed info for corresponding data type
     * @param numberOfFeedBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
     * @param time the time of the last bar in period specified in <code>numberOfFeedBarsBefore</code> parameter, or
     *        the time of the first bar in period specified with <code>numberOfFeedBarsAfter</code> parameter if <code>numberOfFeedBarsBefore</code> is 0, or
     *        the time of the bar prior to the first bar in period specified with <code>numberOfFeedBarsAfter</code> if <code>numberOfFeedBarsBefore</code> is &gt; 0
     * @param numberOfFeedBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
     * @return loaded bars
     * @throws JFException when some error occurs when loading data
     * @see #getFeedData(IFeedInfo, int, long, int)
     */
    @Deprecated
    <T extends ITimedData> List<T> getFeedData(ITailoredFeedInfo<T> feedInfo, int numberOfFeedBarsBefore, long time, int numberOfFeedBarsAfter) throws JFException;

    /**
     * Reads feed data from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, feed data items will be returned by calling method in <code>feedListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the last weeks renko bricks - namely calculating the average close price
     *
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * private double avgPrice = 0;
     * private int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     IFinancialInstrumentProvider financialInstrumentProvider = context.getFinancialInstrumentProvider();
     *     final IFinancialInstrument financialInstrument = financialInstrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *     final IFeedInfo feedInfo = new RenkoFeedInfo(financialInstrument, PriceRange.TWO_PIPS, OfferSide.ASK, Period.ONE_HOUR, CreationPoint.OPEN);
     *
     *     final long from = history.getFeedData(feedInfo, 10).getTime();
     *     final long to = history.getFeedData(feedInfo, 1).getTime();
     *
     *     IFinancialFeedListener tailoredFinancialFeedListener = new IFinancialFeedListener() {
     *         {@literal @}Override
     *         public void onFeedData(IFeedInfo feedInfo, ITimedData feedData) {
     *             itemCount++;
     *
     *             double price = ((IRenkoBar) feedData).getClose();
     *             avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double) itemCount));
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     };
     *
     *     LoadingProgressListener loadingProgressListener = new LoadingProgressListener() {
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s Renkos till %s average close=%.5f feed descriptor=%s",
     *                     itemCount,
     *                     DateUtils.format(to),
     *                     avgPrice,
     *                     feedInfo
     *             ).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     };
     *
     *     history.readFeedData(
     *             feedInfo,
     *             from,
     *             to,
     *             tailoredFinancialFeedListener,
     *             loadingProgressListener
     *     );
     * }
     *
     * </pre>
     *
     * @param feedInfo feed info of the feed data
     * @param from start of the time interval for which feed data items should be loaded.
     * @param to end time of the time interval for which feed data items should be loaded.
     * @param feedListener receives data about requested feed data items
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     */
    @Deprecated
    void readFeedData(IFeedInfo feedInfo, long from, long to, IFinancialFeedListener feedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * A type-safe feed data retrieval for the specified feed
     *
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * private double avgPrice = 0;
     * private int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     IFinancialInstrumentProvider financialInstrumentProvider = context.getFinancialInstrumentProvider();
     *     final IFinancialInstrument financialInstrument = financialInstrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *     final ITailoredFeedInfo{@literal <IRenkoBar>} tailoredFeedInfo = new RenkoFeedInfo(financialInstrument, PriceRange.TWO_PIPS, OfferSide.ASK, Period.ONE_HOUR, CreationPoint.OPEN);
     *
     *     final long from = history.getFeedData(tailoredFeedInfo, 10).getTime();
     *     final long to = history.getFeedData(tailoredFeedInfo, 1).getTime();
     *
     *     ITailoredFinancialFeedListener{@literal <IRenkoBar>} tailoredFinancialFeedListener = new ITailoredFinancialFeedListener{@literal <IRenkoBar>}() {
     *         {@literal @}Override
     *         public void onFeedData(ITailoredFeedInfo{@literal <IRenkoBar>} tailoredFeedInfo, IRenkoBar renko) {
     *             itemCount++;
     *
     *             double price = renko.getClose();
     *             avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double) itemCount));
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     };
     *
     *     LoadingProgressListener loadingProgressListener = new LoadingProgressListener() {
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s Renkos till %s average close=%.5f feed descriptor=%s",
     *                     itemCount,
     *                     DateUtils.format(to),
     *                     avgPrice,
     *                     tailoredFeedInfo
     *             ).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     };
     *
     *     history.readFeedData(
     *             tailoredFeedInfo,
     *             from,
     *             to,
     *             tailoredFinancialFeedListener,
     *             loadingProgressListener
     *     );
     * }
     * </pre>
     *
     * @param <T> type of data feed element
     * @param feedInfo feed info for corresponding data type
     * @param from start of the time interval for which feed data items should be loaded.
     * @param to end time of the time interval for which feed data items should be loaded.
     * @param tailoredFinancialFeedListener receives data about requested feed data items
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     * @see #readFeedData(IFeedDescriptor, long, long, IFeedListener, LoadingProgressListener)
     */
    @Deprecated
    <T extends ITimedData> void readFeedData(ITailoredFeedInfo<T> feedInfo, long from, long to, ITailoredFinancialFeedListener<T> tailoredFinancialFeedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Reads feed data from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, feed data will be returned by calling method in <code>feedListener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of feed data without keeping them all in memory, and second is asynchronous processing
     *
     * <p> Consider analyzing the last 1000 renko bricks - namely calculating the average close price
     * <pre>
     *
     * private IHistory history;
     * private IConsole console;
     *
     * private double avgPrice = 0;
     * private int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     IFinancialInstrumentProvider financialInstrumentProvider = context.getFinancialInstrumentProvider();
     *     final IFinancialInstrument financialInstrument = financialInstrumentProvider.getFinancialInstrument("EUR/USD");
     *     final IFeedInfo feedInfo = new RenkoFeedInfo(financialInstrument, PriceRange.TWO_PIPS, OfferSide.ASK);
     *
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     final long time = history.getFeedData(feedInfo, 1).getTime();
     *
     *     IFinancialFeedListener financialFeedListener = new IFinancialFeedListener() {
     *         {@literal @}Override
     *         public void onFeedData(IFeedInfo feedInfo, ITimedData feedData) {
     *             itemCount++;
     *             double price = ((IRenkoBar) feedData).getClose();
     *             avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double) itemCount));
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     };
     *
     *     LoadingProgressListener loadingProgressListener = new LoadingProgressListener() {
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s Renkos till %s average close=%.5f feed descriptor=%s",
     *                     itemCount,
     *                     DateUtils.format(time),
     *                     avgPrice,
     *                     feedInfo
     *             ).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     };
     *
     *     history.readFeedData(
     *             feedInfo,
     *             1000,
     *             time,
     *             0,
     *             financialFeedListener,
     *             loadingProgressListener
     *     );
     * }
     *
     * </pre>
     *
     * @param <T> type of data feed element
     * @param feedInfo feed info of the feed data
     * @param numberOfFeedDataBefore how much feed data items to load before and including feed data item with time specified in <code>time</code> parameter
     * @param time time of the last feed data item in period specified in <code>numberOfFeedDataBefore</code> parameter or/and
     *        time of the feed data item prior first feed data item in period specified with <code>numberOfFeedDataAfter</code> parameter
     * @param numberOfFeedDataAfter how much feed data items to load after (not including) feed data item with time specified in <code>time</code> parameter
     * @param financialFeedListener receives feed data
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     */
    @Deprecated
    <T extends ITimedData> void readFeedData(IFeedInfo feedInfo, int numberOfFeedDataBefore, long time, int numberOfFeedDataAfter, IFinancialFeedListener financialFeedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * A type-safe feed data retrieval for the specified feed
     *
     * <pre>
     * private IHistory history;
     * private IConsole console;
     *
     * private double avgPrice = 0;
     * private int itemCount = 0;
     *
     * {@literal @}Override
     * public void onStart(final IContext context) throws JFException {
     *     history = context.getHistory();
     *     console = context.getConsole();
     *
     *     IFinancialInstrumentProvider financialInstrumentProvider = context.getFinancialInstrumentProvider();
     *     final IFinancialInstrument financialInstrument = financialInstrumentProvider.getFinancialInstrument("EUR/USD");
     *     final ITailoredFeedInfo{@literal <IRenkoBar>} tailoredFeedInfo = new RenkoFeedInfo(financialInstrument, PriceRange.TWO_PIPS, OfferSide.ASK);
     *
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     final long time = history.getFeedData(tailoredFeedInfo, 1).getTime();
     *
     *     ITailoredFinancialFeedListener{@literal <IRenkoBar>} tailoredFinancialFeedListener = new ITailoredFinancialFeedListener{@literal <IRenkoBar>}() {
     *         {@literal @}Override
     *         public void onFeedData(ITailoredFeedInfo{@literal <IRenkoBar>} tailoredFeedInfo, IRenkoBar renko) {
     *             itemCount++;
     *             double price = renko.getClose();
     *             avgPrice = itemCount == 1 ? price : round(avgPrice + ((price - avgPrice) / (double) itemCount));
     *         }
     *
     *         private double round(double amount) {
     *             return (new BigDecimal(amount)).setScale(financialInstrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP).doubleValue();
     *         }
     *     };
     *
     *     LoadingProgressListener loadingProgressListener = new LoadingProgressListener() {
     *         {@literal @}Override
     *         public void dataLoaded(long start, long end, long currentPosition, String information) {
     *         }
     *
     *         {@literal @}Override
     *         public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition) {
     *             console.getOut().format("Loaded %s Renkos till %s average close=%.5f feed descriptor=%s",
     *                     itemCount,
     *                     DateUtils.format(time),
     *                     avgPrice,
     *                     tailoredFeedInfo
     *             ).println();
     *             context.stop();
     *         }
     *
     *         {@literal @}Override
     *         public boolean stopJob() {
     *             return false;
     *         }
     *     };
     *
     *     history.readFeedData(
     *             tailoredFeedInfo,
     *             1000,
     *             time,
     *             0,
     *             tailoredFinancialFeedListener,
     *             loadingProgressListener
     *     );
     * }
     *
     * </pre>
     *
     * @param <T> type of data feed element
     * @param feedInfo feed info for corresponding data type
     * @param numberOfFeedDataBefore how much feed data items to load before and including feed data item with time specified in <code>time</code> parameter
     * @param time time of the last feed data item in period specified in <code>numberOfFeedDataBefore</code> parameter or/and
     *        time of the feed data item prior first feed data item in period specified with <code>numberOfFeedDataAfter</code> parameter
     * @param numberOfFeedDataAfter how much feed data items to load after (not including) feed data item with time specified in <code>time</code> parameter
     * @param tailoredFinancialFeedListener receives feed data
     * @param loadingProgress used to control loading progress
     * @throws JFException when some errors occurs
     * @see #readFeedData(IFeedInfo, int, long, int, IFinancialFeedListener, LoadingProgressListener)
     */
    @Deprecated
    <T extends ITimedData> void readFeedData(ITailoredFeedInfo<T> feedInfo, int numberOfFeedDataBefore, long time, int numberOfFeedDataAfter, ITailoredFinancialFeedListener<T> tailoredFinancialFeedListener, LoadingProgressListener loadingProgress) throws JFException;

    /**
     * Returns Point and Figure for specified instrument, offer side, box size and reversal amount,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null. This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}). 
     * To indicate specific base period and/or data interpolation descriptor, use {@link #getFeedData(ITailoredFeedDescriptor, int)}.
	 * 
	 * @param instrument instrument of P&amp;F
	 * @param offerSide bid or ask side of P&amp;F
	 * @param boxSize box size of the P&amp;F
	 * @param reversalAmount reversal amount of the P&amp;F
	 * @param shift number of P&amp;F back in time staring from current P&amp;F. 1 - previous P&amp;F, 2 - current P&amp;F minus 2 bars and so on
	 * @return P&amp;F or null if no P&amp;F is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, int)}.
	 */
	@Deprecated
	IPointAndFigure getPointAndFigure(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int shift) throws JFException;
	
	/**
     * Returns Point and Figures for specified instrument, offer side, box size and reversal amount. This method uses default base period 
     * {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
     * To indicate specific base period and/or data interpolation descriptor, use {@link #getFeedData(ITailoredFeedDescriptor, long, long)}.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is included in result too
     * 
     * Subscribe to Point and Figure live notification first, before call this method.
     * 
     * @param instrument of P&amp;Fs
     * @param offerSide offer side of P&amp;Fs
     * @param boxSize box size of P&amp;Fs
     * @param reversalAmount reversal amount of P&amp;Fs
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Point And Figures
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, long, long)}.
     */
    @Deprecated
	List<IPointAndFigure> getPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, long from, long to) throws JFException;
	
    /**
     * Returns Point and Figures for specified instrument, offer side, box size and reversal amount. This method uses default 
     * base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}). 
     * To indicate specific base period and/or data interpolation descriptor, use {@link #getFeedData(ITailoredFeedDescriptor, int, long, int)}.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is included in result too
     * 
     * Subscribe to Point and Figure live notification first, before call this method.
     *
	 * @param instrument instrument of the P&amp;Fs
	 * @param offerSide offer side of the P&amp;Fs
	 * @param boxSize price range of the P&amp;Fs
     * @param reversalAmount reversal amount of P&amp;Fs
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is &gt; 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when desired data amount is negative, etc; or some error occurs when loading data
	 * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, int, long, int)}.
	 */
    @Deprecated
	List<IPointAndFigure> getPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int numberOfBarsBefore, long time, int numberOfBarsAfter) throws JFException;
	
    /**
     * Reads Point and Figures from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing.
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
     * <br> 
     * Subscribe to Point and Figure live notification first, before call this method.
     * 
     * @param instrument of P&amp;Fs
     * @param offerSide offer side of P&amp;Fs
     * @param boxSize box size of P&amp;Fs
     * @param reversalAmount reversal amount of P&amp;Fs
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, long, long, ITailoredFeedListener, LoadingProgressListener)}.
	 */
    @Deprecated
	void readPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, long from, long to, IPointAndFigureFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
    /**
     * Reads Point and Figures from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
     * <br>
     * Subscribe to Point and Figure live notification first, before call this method.
     * 
     * @param instrument of P&amp;Fs
     * @param offerSide offer side of P&amp;Fs
     * @param boxSize box size of P&amp;Fs
     * @param reversalAmount reversal amount of P&amp;Fs
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is &gt; 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, int, long, int, ITailoredFeedListener, LoadingProgressListener)}.
	 */
    @Deprecated
	void readPointAndFigures(Instrument instrument, OfferSide offerSide, PriceRange boxSize, ReversalAmount reversalAmount, int numberOfBarsBefore, long time, int numberOfBarsAfter, IPointAndFigureFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
    /**
     * Returns tick bar for specified instrument, offer side and tick bar size,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null. This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone. 
     * To indicate specific base period, use {@link #getFeedData(ITailoredFeedDescriptor, int)}.
	 * 
	 * @param instrument instrument of the tick bar
	 * @param offerSide bid or ask side of the tick bar
	 * @param tickBarSize tick bar size of the tick bar
	 * @param shift number of tick bar back in time staring from current tick bar. 1 - previous tick bar, 2 - current tick bar minus 2 bars and so on
	 * @return range bar or null if no bar is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, int)}.
	 */
	@Deprecated
	ITickBar getTickBar(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int shift) throws JFException;
    
    /**
     * Returns Tick Bars for specified instrument, offer side and tick bar size. This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is included in result too
     * 
     * Subscribe to Tick Bar live notification first, before call this method.
     * 
     * @param instrument of Tick Bars
     * @param offerSide offer side of Tick Bars
     * @param tickBarSize tick bar size of Tick Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Tick Bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, long, long)}.
     */
	@Deprecated
	List<ITickBar> getTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long from, long to) throws JFException;

	/**
     * Reads Tick Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone.
     * <br>
     * Subscribe to Tick Bar live notification first, before call this method.
     * 
     * @param instrument of Tick Bars
     * @param offerSide offer side of Tick Bars
     * @param tickBarSize tick bar size of Tick Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, long, long, ITailoredFeedListener, LoadingProgressListener)}.
	 */
	@Deprecated
	void readTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, long from, long to, ITickBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
	/**
     * Reads Tick Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone.
     * <br>
     * Subscribe to Tick Bar live notification first, before call this method.
     * 
     * @param instrument of Tick Bars
     * @param offerSide offer side of Tick Bars
     * @param tickBarSize tick bar size of Tick Bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is &gt; 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, int, long, int, ITailoredFeedListener, LoadingProgressListener)}.
	 */
	@Deprecated
	void readTickBars(Instrument instrument, OfferSide offerSide, TickBarSize tickBarSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, ITickBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
    /**
     * Returns Range Bars for specified instrument, offer side and price range. This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is included in result too
     * 
     * Subscribe to Range Bar live notification first, before call this method.
     * 
     * @param instrument of Range Bars
     * @param offerSide offer side of Range Bars
     * @param priceRange price range of Range Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Range Bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, long, long)}.
     */
	@Deprecated
	List<IRangeBar> getRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long from, long to) throws JFException;
	
	/**
     * Returns Range Bars for specified instrument, offer side and price range. This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is included in result too
     * 
     * Subscribe to Range Bar live notification first, before call this method.
     *
	 * @param instrument instrument of the Range bars
	 * @param offerSide offer side of the Range bars
	 * @param priceRange price range of the Range bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is &gt; 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when desired data amount is negative, etc; or some error occurs when loading data
	 * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, int, long, int)}.
	 */
	@Deprecated
	List<IRangeBar> getRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfBarsBefore, long time, int numberOfBarsAfter) throws JFException;
	
	/**
     * Reads Range Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
     * <br>
     * Subscribe to Range Bar live notification first, before call this method.
     * 
     * @param instrument of Range Bars
     * @param offerSide offer side of Range Bars
     * @param priceRange price range of Range Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, long, long, ITailoredFeedListener, LoadingProgressListener)}.
	 */
	@Deprecated
	void readRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, long from, long to, IRangeBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;

	/**
     * Reads Range Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
     * <br>
     * Subscribe to Range Bar live notification first, before call this method.
     * 
     * @param instrument of Range Bars
     * @param offerSide offer side of Range Bars
     * @param priceRange price range of Range Bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is &gt; 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, int, long, int, ITailoredFeedListener, LoadingProgressListener)}.
	 */
	@Deprecated
	void readRangeBars(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int numberOfBarsBefore, long time, int numberOfBarsAfter, IRangeBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
	
	/**
     * Returns range bar for specified instrument, offer side and price range,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null. This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone and default
     * data interpolation descriptor ({@link DataInterpolationDescriptor#DEFAULT}).
	 * 
	 * @param instrument instrument of the range bar
	 * @param offerSide bid or ask side of the range bar
	 * @param priceRange price range of the range bar
	 * @param shift number of range bar back in time staring from current range bar. 1 - previous range bar, 2 - current range bar minus 2 bars and so on
	 * @return range bar or null if no bar is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, int)}.
	 */
	@Deprecated
	IRangeBar getRangeBar(Instrument instrument, OfferSide offerSide, PriceRange priceRange, int shift) throws JFException;

	/**
	 * Returns renko bar for specified instrument, offer side and brick size,
     * that is shifted back in time for number in bars specified in <code>shift</code>
     * parameter, 0 - current bar, 1 - previous bar (last formed bar) If there is no bar loaded at that
     * position, then function returns null.
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone, default renko session ({@link Period#ONE_MIN}) and 
     * default price point ({@link RenkoCreationPoint#CLOSE}).
     * <br>
	 * 
	 * @param instrument instrument of the Renko bar
	 * @param offerSide bid or ask side of the Renko bar
	 * @param brickSize price range of the Renko bar
	 * @param shift number of bar back in time staring from current bar. 1 - previous bar, 2 - current bar minus 2 bars and so on
	 * @return Renko bar or null if no bar is loaded
	 * @throws JFException when shift value is negative or instrument is not active (not opened in platform)
	 * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, int)}.
	 */
	@Deprecated
	IRenkoBar getRenkoBar(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int shift) throws JFException;
	
	/**
     * Returns Renko Bars for specified instrument, offer side and brick size.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is included in result too
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone, default renko session ({@link Period#ONE_MIN}) and 
     * default price point ({@link RenkoCreationPoint#CLOSE}).
     * <br>
     * Subscribe to Renko Bar live notification first, before call this method.
     * 
     * @param instrument of Renko Bars
     * @param offerSide offer side of Renko Bars
     * @param brickSize price range of Renko Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
     * @return loaded Renko Bars
     * @throws JFException when incorrect time interval is passed or some error occurs when loading data
     * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, long, long)}.
     */
	@Deprecated
	List<IRenkoBar> getRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to) throws JFException;

	/**
     * Returns Renko Bars for specified instrument, offer side and brick size.
     * Method blocks until all data will be loaded from the server into local cache and then read and stored in collection.
     * Because all the data is loaded into the memory, this method should be used with caution to not
     * load too much data which can lead to <code>OutOfMemoryException</code>.
     * If the requested period includes the bar that is not fully formed yet (in-progress bar), then it is included in result too
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone, default renko session ({@link Period#ONE_MIN}) and 
     * default price point ({@link RenkoCreationPoint#CLOSE}).
     * <br>
     * Subscribe to Renko Bar live notification first, before call this method.
     *
	 * @param instrument instrument of the Renko bars
	 * @param offerSide offer side of the Renko bars
	 * @param brickSize price range of the Renko bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is &gt; 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @return loaded bars
	 * @throws JFException when desired data amount is negative, etc; or some error occurs when loading data
	 * @deprecated use {@link #getFeedData(ITailoredFeedDescriptor, int, long, int)}.
	 */
	@Deprecated
	List<IRenkoBar> getRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter) throws JFException;
	
	/**
     * Reads Renko Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone, default renko session ({@link Period#ONE_MIN}) and 
     * default price point ({@link RenkoCreationPoint#CLOSE}).
     * <br>
     * Subscribe to Renko Bar live notification first, before call this method.
     * 
     * @param instrument of Renko Bars
     * @param offerSide offer side of Renko Bars
     * @param brickSize price range of Renko Bars
     * @param from start time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the first loaded bar
     * @param to end time of the time interval for which bars should be loaded. This time is included in interval [start; end] of the last loaded bar
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, long, long, ITailoredFeedListener, LoadingProgressListener)}.
	 */
	@Deprecated
	void readRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, long from, long to, IRenkoBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;

	/**
     * Reads Renko Bars from the local cache in the background. Method returns fast after creating request for data not waiting for any data to be read
     * from local cache. After internal request is sent, bars will be returned by calling method in <code>listener</code>. LoadingProgressListener is
     * used to receive feedback about loading progress, to cancel loading and its method
     * {@link LoadingProgressListener#loadingFinished(boolean, long, long, long)} is called when loading is finished or as a result of error
     * This method has two main purposes: one is to load a lot of bars without keeping them all in memory, and second is asynchronous processing
     * This method uses default base period {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}, which has EET time zone, default renko session ({@link Period#ONE_MIN}) and 
     * default price point ({@link RenkoCreationPoint#CLOSE}).
     * <br>
     * Subscribe to Renko Bar live notification first, before call this method.
     * 
     * @param instrument of Renko Bars
     * @param offerSide offer side of Renko Bars
     * @param brickSize price range of Renko Bars
	 * @param numberOfBarsBefore how much bars to load before and including bar with time specified in <code>time</code> parameter
	 * @param time time of the last bar in period specified in <code>numberOfBarsBefore</code> parameter, or
     *        time of the first bar in period specified with <code>numberOfBarsAfter</code> parameter if <code>numberOfBarsBefore</code> is 0, or
     *        time of the candle prior to first candle in period specified with <code>numberOfBarsAfter</code> if <code>numberOfBarsBefore</code> is &gt; 0
	 * @param numberOfBarsAfter how much bars to load after (not including) bar with time specified in <code>time</code> parameter
	 * @param listener receives data about requested bars
	 * @param loadingProgress used to control loading progress
	 * @throws JFException when incorrect time interval is passed or some error occurs when loading data
	 * @deprecated use {@link #readFeedData(ITailoredFeedDescriptor, int, long, int, ITailoredFeedListener, LoadingProgressListener)}.
	 */
	@Deprecated
	void readRenkoBars(Instrument instrument, OfferSide offerSide, PriceRange brickSize, int numberOfBarsBefore, long time, int numberOfBarsAfter, IRenkoBarFeedListener listener, LoadingProgressListener loadingProgress) throws JFException;
}
