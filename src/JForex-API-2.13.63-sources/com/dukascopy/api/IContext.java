/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.dukascopy.api.feed.*;
import com.dukascopy.api.instrument.IFinancialInstrument;
import com.dukascopy.api.instrument.IFinancialInstrumentProvider;

/**
 * Gives access to the various parts of the system
 * 
 * @author Denis Larka, Dmitry Shohov
 */
public interface IContext {
    /**
     * Returns interface of the main engine (order submitting, merging etc)
     * 
     * @return main engine
     */
    IEngine getEngine();
    
    /**
     * Returns first chart for specified instrument. If there is more than one chart for this instrument, then method returns one of them,
     * but always the same.
     * 
     * @param instrument currency pair
     * @return chart or null if there is no chart opened for the instrument
     */
    IChart getChart(Instrument instrument);

    /**
     * Returns set of charts for specified instrument.
     * 
     * @param instrument currency pair
     * @return <code>Set&lt;IChart&gt;</code>
     */
    Set<IChart> getCharts(Instrument instrument);

    /**
     * Returns all open charts.
     * 
     * @return <code>Set&lt;IChart&gt;</code>
     */
	Set<IChart> getCharts();
    
    /**
     * @return corresponding {@link IChart} object of the last active chart, i.e., the chart which last had focus
     */
    IChart getLastActiveChart();
    
    /**
     * Opens new chart with specified in {@link IFeedDescriptor} parameters.
     * 
     * @param feedDescriptor new chart's feed descriptor
     * @throws IllegalArgumentException when feedDescriptor not formed well
     * @return IChart newly created chart
     */
    IChart openChart(IFeedDescriptor feedDescriptor);
    
    /**
     * Close specified chart.
     * 
     * @param chart chart to close
     */
    void closeChart(IChart chart);
    
    /**
     * Returns an interface which provides control
     * to custom strategy tabs in the main and
     * bottom panels
     * @return singleton instance of the IUserInterface
     */
    IUserInterface getUserInterface();
    
    /**
     * Returns interface that allows access to history data
     * 
     * @return interface for history access
     */
    IHistory getHistory();
    
    /**
     * Returns interface that allows to write messages into the Messages table
     * 
     * @return interface for messages sending
     */
    IConsole getConsole();
    
    /**
     * Returns interface that allows to calculate indicator values
     * 
     * @return interface for indicator calculations
     */
    IIndicators getIndicators();

    /**
     * Returns last known state of the account info. This state is updated once in 5 seconds and can be inaccurate
     * if significant price changes happen on the market
     *
     * @return account
     */
    IAccount getAccount();

    /**
     * Returns interface that allows to handle downloadable strategies
     * @return interface for downloadable strategies handling
     */
    IDownloadableStrategies getDownloadableStrategies();
    
    /**
     * Returns interface with JForex utility methods, e.g., currency converter
     * @return {@link JFUtils} object
     */
    JFUtils getUtils();
    
    /**
     * Returns interface that allows access to system data. 
     * @return interface for system data
     */
    IDataService getDataService();

    /**
     * Returns interface that give access to reporting system
     * @return interface for consolidated reports' data
     */
    IReportService getReportService();

    /**
     * Checks that the instruments are subscribed and subscribes to the instrument if it's not.
     * Method returns fast after invoking and doesn't  wait for subscription will be done.
     * Ticks passed in onTick method will have full depth for this instruments, while other instruments are not guaranteed to have full depth.<br>
     * Equivalent to {@link #setSubscribedInstruments(Set, boolean)} with lock set as <code>false</code>
     * 
     * <pre>{@code
     * context.setSubscribedInstruments(new HashSet<Instrument>(Arrays.asList(Instrument.EURUSD, Instrument.AUDCAD)));
     * }</pre>
     * 
     * @param instruments set of instruments, that strategy needs for it's work
     */
    void setSubscribedInstruments(Set<Instrument> instruments);

    /**
     * Checks that the instruments are subscribed and subscribes to the instrument if it's not.
     * Ticks passed in onTick method will have full depth for this instruments, while other instruments are not guaranteed to have full depth.
     * 
     * <pre>{@code
     * context.setSubscribedInstruments(new HashSet<Instrument>(Arrays.asList(Instrument.EURUSD, Instrument.AUDCAD), true));
     * }</pre>
     *
     * @param instruments set of instruments, that strategy needs for it's work
     * @param lock <code>false</code> - method returns fast after invoking and doesn't wait for subscription will be done. <code>true</code> - otherwise. 
     */
    void setSubscribedInstruments(Set<Instrument> instruments, boolean lock);

    void unsubscribeInstruments(final Set<Instrument> instruments);

    /**
     * Checks that the instruments are subscribed and subscribes to the instrument if it's not.
     * Ticks passed in onTick method will have full depth for this instruments, while other instruments are not guaranteed to have full depth.
     *
     * <pre>{@code
     * IFinancialInstrumentProvider provider = context.getFinancialInstrumentProvider();
     * IFinancialInstrument eurusdInstr = provider.getFinancialInstrument("EURUSD");
     * IFinancialInstrument audcadInstr = provider.getFinancialInstrument("AUDCAD");
     *
     * context.setSubscribedInstruments(new HashSet<IFinancialInstrument>(Arrays.asList(eurusdInstr, audcadInstr)));
     * }</pre>
     *
     * @param financialInstruments set of instruments, that strategy needs for it's work
     * @param lock <code>false</code> - method returns fast after invoking and doesn't wait for subscription will be done. <code>true</code> - otherwise.
     */
    void setSubscribedFinancialInstruments(Set<IFinancialInstrument> financialInstruments, boolean lock);

    /**
     * Returns set of the currently subscribed instruments
     * 
     * @return set of the subscribed instruments
     */
    Set<Instrument> getSubscribedInstruments();

    /**
     * Stops strategy execution. 
     * Current task will be completed.
     */
    void stop();
    
    /**
     * Returns true if user requested to stop the strategy.
     * Can be used to check if strategy was stopped (for example from non strategy threads running in parallel)
     *
     * @return true if strategy stop was requested 
     */
    boolean isStopped();
    
    /**
     * Returns true if strategy is granted full access. 
     * 
     * @return true of false depending on access level
     */
    boolean isFullAccessGranted();
   
    /**
     * Every strategy executes in it's own thread. This will ensure single threaded model: Any handle method of {@link IStrategy}
     * will be executed in order.
     * Submission of orders can be called only from this thread.
     * If some critical action like submitting order needs to be called from another thread, you need to use this method to access
     * strategy thread. For instance:
     *
     * <pre>
     * Thread thread = new Thread(new Runnable() {
     *      public void run() {
     *          try {
     *              context.executeTask(task);
     *          } catch (Exception e) {
     *              console.getErr().println(Thread.currentThread().getName() + " " + e);
     *          }
     *      }
     *  });
     * thread.start();
     * </pre>
     *
     * @param <T> type of the return value
     * @param callable task to execute
     * @return <code>Future&lt;T&gt;</code> that can be used to get result of execution 
     */
    <T> Future<T> executeTask(Callable<T> callable);

    /**
	 * Returns directory where reading and writing is allowed. Usually ~/My Documents/My Strategies/files
     * 
     * @return directory with free read/write access
	 */
    File getFilesDir();

    /**
     * Pauses historical testing, doesn't have any effect if not in historical tester
     */
    void pause();

    /**
     * A convenience type-safe method to receive a particular feed data. Example:
     *
     * <pre>
     * // create type-safe descriptor
     * ITailoredFeedDescriptor{@literal <IPointAndFigure>} descriptor = new PointAndFigureFeedDescriptor(Instrument.EURUSD, PriceRange.THREE_PIPS, ReversalAmount.THREE, OfferSide.ASK);
     *
     * // create type-safe listener
     * ITailoredFeedListener{@literal <IPointAndFigure>} listener = new ITailoredFeedListener{@literal <IPointAndFigure>}() {
     *     {@literal @}Override
     *     public void onFeedData(ITailoredFeedDescriptor{@literal <IPointAndFigure>} feedDescriptor, IPointAndFigure feedData) {
     *         // Print IPointAndFigure specific data without casting
     *         console.getOut().println(feedData.isRising());
     *     }
     * };
     *
     * // subscribe
     * context.subscribeToFeed(descriptor, listener);
     * </pre>
     *
     * @param <T> type of feed data element
     * @param feedDescriptor {@link ITailoredFeedDescriptor}
     * @param feedListener {@link ITailoredFeedListener}
     * @see #subscribeToFeed(IFeedDescriptor, IFeedListener)
     */
    <T extends ITimedData> void subscribeToFeed(ITailoredFeedDescriptor<T> feedDescriptor, ITailoredFeedListener<T> feedListener);
    
    /**
     * Unsubscribes passed listener from feed notification
     * 
     * @param <T> type of feed data element
     * @param feedListener {@link ITailoredFeedListener}
     */
    <T extends ITimedData> void unsubscribeFromFeed(ITailoredFeedListener<T> feedListener);
    
    /**
     * Unsubscribes passed listener from feed notification according to the given feed descriptor
     * 
     * @param <T> type of feed data element
     * @param feedListener {@link ITailoredFeedListener}
     * @param feedDescriptor {@link ITailoredFeedDescriptor}
     */
    <T extends ITimedData> void unsubscribeFromFeed(ITailoredFeedListener<T> feedListener, ITailoredFeedDescriptor<T> feedDescriptor);

    /**
     * Subscribes passed listener on feed notification by passed feed descriptor
     * 
     * @param feedDescriptor {@link IFeedDescriptor}
     * @param feedListener {@link IFeedListener}
     */
    void subscribeToFeed(IFeedDescriptor feedDescriptor, IFeedListener feedListener);

    /**
     * Unsubscribes passed listener from feed notification
     * 
     * @param feedListener {@link IFeedListener}
     */
    void unsubscribeFromFeed(IFeedListener feedListener);

    /**
     * Unsubscribes passed listener from feed notification according to the given feed descriptor
     * 
     * @param feedListener {@link IFeedListener}
     * @param feedDescriptor {@link IFeedDescriptor}
     */
    void unsubscribeFromFeed(IFeedListener feedListener, IFeedDescriptor feedDescriptor);

    /**
     * Subscribes passed listener on ticks feed notification by passed instrument.
     * 
     * @param instrument {@link Instrument} of ticks to listen
     * @param listener {@link ITickFeedListener} listener
     */
    void subscribeToTicksFeed(Instrument instrument, ITickFeedListener listener);

    /**
     * Unsubscribes passed listener from ticks feed notification
     *
     * @param listener listener to unsubscribe
     */
    void unsubscribeFromTicksFeed(IFinancialTickFeedListener listener);

    /**
     * Unsubscribes passed listener from ticks feed notification
     * 
     * @param listener listener to unsubscribe
     */
    void unsubscribeFromTicksFeed(ITickFeedListener listener);

     /**
     * Subscribes passed listener on bars feed notification by passed instrument, period and offer side.
     *
     * @param instrument {@link Instrument} of bars to listen
     * @param period {@link Period} period of bars to listen (Tick period is not supported).
     * @param offerSide {@link OfferSide} of bars to listen
     * @param listener {@link IBarFeedListener} listener
     */
    void subscribeToBarsFeed(
			Instrument instrument,
			Period period,
			OfferSide offerSide,
			IBarFeedListener listener
    );

    /**
     * Unsubscribes passed listener from bars feed notification
     *
     * @param listener listener to unsubscribe
     */
    void unsubscribeFromBarsFeed(IFinancialBarFeedListener listener);

    /**
     * Unsubscribes passed listener from bars feed notification
     *
     * @param listener listener to unsubscribe
     */
    void unsubscribeFromBarsFeed(IBarFeedListener listener);

    /**
     * Register a listener for a strategy configuration parameter which is preceded by a {@link Configurable} annotation.
     * Listener will listen changes of specified property for running strategy.
     * @param parameter either the name of strategy field which is preceded by a {@link Configurable} annotation or
     *                  {@link Configurable#value()} - the assigned configurable parameter name that appears besides
     *                  its value in GUI.
     * @param listener Listener which will get {@link PropertyChangeEvent}s
     */
    void addConfigurationChangeListener(String parameter, PropertyChangeListener listener);

    /**
     * Unregister a listener for a certain configuration parameter which is preceded by a {@link Configurable} annotation.
     * @param parameter either the name of strategy field which is preceded by a {@link Configurable} annotation or
     *                  {@link Configurable#value()} - the assigned configurable parameter name that appears besides
     *                  its value in GUI.
     * @param listener Listener which will get {@link PropertyChangeEvent}s
     */
    void removeConfigurationChangeListener(String parameter, PropertyChangeListener listener);

    /**
     * Subscribes to configurable changes during the strategy run,
     * overwrites previously set listener.
     * 
     * @param listener listener to subscribe
     */
    void setConfigurableChangeListener(ConfigurableChangeListener listener);

    /**
     * Returns current server time adjusted to network latency.
     * If connection is lost, returns local system time.
     *
     * @return current server time
     */
    long getTime();

    // *******************************
    // ****** Deprecated methods *****
    // *******************************

    /**
     * Subscribes passed listener on feed notification by passed feed descriptor
     *
     * @param feedInfo {@link IFeedInfo}
     * @param financialfeedListener {@link IFinancialFeedListener}
     */
    @Deprecated
    void subscribeToFeed(IFeedInfo feedInfo, IFinancialFeedListener financialfeedListener);

    /**
     * Checks that the instruments are subscribed and subscribes to the instrument if it's not.
     * Method returns fast after invoking and doesn't  wait for subscription will be done.
     * Ticks passed in onTick method will have full depth for this instruments, while other instruments are not guaranteed to have full depth.<br>
     * Equivalent to {@link #setSubscribedInstruments(Set, boolean)} with lock set as <code>false</code>
     *
     * <pre>{@code
     * IFinancialInstrumentProvider provider = context.getFinancialInstrumentProvider();
     * IFinancialInstrument eurusdInstr = provider.getFinancialInstrument("EUR/USD");
     * IFinancialInstrument audcadInstr = provider.getFinancialInstrument("AUD/CAD");
     *
     * context.setSubscribedInstruments(new HashSet<IFinancialInstrument>(Arrays.asList(eurusdInstr, audcadInstr)));
     * }</pre>
     *
     * @param financialInstruments set of instruments, that strategy needs for it's work
     */
    @Deprecated
    void setSubscribedFinancialInstruments(Set<IFinancialInstrument> financialInstruments);

    /**
     * Opens new chart with specified in {@link IFeedInfo} parameters.
     *
     * @param feedInfo new chart's feed descriptor
     * @throws IllegalArgumentException when feedDescriptor not formed well
     * @return IChart newly created chart
     */
    @Deprecated
    IChart openChart(IFeedInfo feedInfo);

    /**
     * Returns first chart for specified instrument. If there is more than one chart for this instrument, then method returns one of them,
     * but always the same.
     *
     * @param financialInstrument {@link IFinancialInstrument} currency pair
     * @return chart or null if there is no chart opened for the instrument
     */
    @Deprecated
    IChart getChart(IFinancialInstrument financialInstrument);

    /**
     * Returns set of charts for specified instrument.
     *
     * @param financialInstrument {@link IFinancialInstrument} currency pair
     * @return <code>Set&lt;IChart&gt;</code>
     */
    @Deprecated
    Set<IChart> getCharts(IFinancialInstrument financialInstrument);

    /**
     * <h3><b>NOTE:</b> Under development</h3>
     * @return interface for {@link IFinancialInstrument} data
     */
    @Deprecated
    IFinancialInstrumentProvider getFinancialInstrumentProvider();

    /**
     * <h3><b>NOTE:</b> Under development</h3>
     * @return interface for {@link IFinancialInstrument} data
     */
    @Deprecated
    IFeedInfoProvider getFeedInfoProvider();

    /**
     * Returns set of the currently subscribed instruments
     *
     * @return set of the subscribed instruments
     */
    @Deprecated
    Set<IFinancialInstrument> getSubscribedFinancialInstruments();

    /**
     * Subscribes passed listener on ticks feed notification by passed instrument.
     *
     * @param financialInstrument {@link IFinancialInstrument} of ticks to listen
     * @param listener {@link IFinancialTickFeedListener} listener
     */
    @Deprecated
    void subscribeToTicksFeed(IFinancialInstrument financialInstrument, IFinancialTickFeedListener listener);

    /**
     * Subscribes passed listener on bars feed notification by passed instrument, period and offer side.
     *
     * @param financialInstrument {@link IFinancialInstrument} of bars to listen
     * @param period {@link Period} period of bars to listen (Tick period is not supported).
     * @param offerSide {@link OfferSide} of bars to listen
     * @param listener {@link IBarFeedListener} listener
     */
    @Deprecated
    void subscribeToBarsFeed(
            IFinancialInstrument financialInstrument,
            Period period,
            OfferSide offerSide,
            IFinancialBarFeedListener listener
    );

    /**
     * Subscribes passed listener on range bars feed notification by passed instrument, offer side and price range
     *
     * @param instrument {@link Instrument} of bars to listen
     * @param offerSide {@link OfferSide} of bars to listen
     * @param priceRange {@link PriceRange} of bars to listen
     * @param listener {@link IRangeBarFeedListener} listener
     * @deprecated replaced by {@link #subscribeToFeed(com.dukascopy.api.feed.ITailoredFeedDescriptor, com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void subscribeToRangeBarFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		PriceRange priceRange,
    		IRangeBarFeedListener listener
    );

    /**
     * Unsubscribes passed listener from range bars feed notification
     *
     * @param listener listener to unsubscribe
     * @deprecated replaced by {@link #unsubscribeFromFeed(com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void unsubscribeFromRangeBarFeed(IRangeBarFeedListener listener);

    /**
     * Subscribes passed listener on point and figure feed notification by passed instrument, offer side, price range and reversal amount
     *
     * @param instrument {@link Instrument} of P&amp;Fs to listen
     * @param offerSide {@link OfferSide} of P&amp;Fs to listen
     * @param priceRange {@link PriceRange} of P&amp;Fs to listen
     * @param reversalAmount {@link ReversalAmount} of P&amp;Fs to listen
     * @param listener {@link IPointAndFigureFeedListener} listener
     * @deprecated replaced by {@link #subscribeToFeed(com.dukascopy.api.feed.ITailoredFeedDescriptor, com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void subscribeToPointAndFigureFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		PriceRange priceRange,
    		ReversalAmount reversalAmount,
    		IPointAndFigureFeedListener listener
    );

    /**
     * Unsubscribes passed listener from point and figure feed notification
     *
     * @param listener listener to unsubscribe
     * @deprecated replaced by {@link #unsubscribeFromFeed(com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void unsubscribeFromPointAndFigureFeed(IPointAndFigureFeedListener listener);

    /**
     * Subscribes passed listener on tick bar feed notification by passed instrument, offer side and tick bar size
     *
     * @param instrument {@link Instrument} of tick bars to listen
     * @param offerSide {@link OfferSide} of tick bars to listen
     * @param tickBarSize {@link TickBarSize} of tick bars to listen
     * @param listener {@link ITickBarFeedListener} listener
     * @deprecated replaced by {@link #subscribeToFeed(com.dukascopy.api.feed.ITailoredFeedDescriptor, com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void subscribeToTickBarFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		TickBarSize tickBarSize,
    		ITickBarFeedListener listener
    );

    /**
     * Unsubscribes passed listener from tick bar feed notification
     *
     * @param listener listener to unsubscribe
     * @deprecated replaced by {@link #unsubscribeFromFeed(com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void unsubscribeFromTickBarFeed(ITickBarFeedListener listener);

    /**
     * Subscribes passed listener on renko bars feed notification by passed instrument, offer side and brick size
     *
     * @param instrument {@link Instrument} of renko bars to listen
     * @param offerSide {@link OfferSide} of renko bars to listen
     * @param brickSize {@link PriceRange brick size} of renko bars to listen
     * @param listener {@link IRenkoBarFeedListener} listener
     * @deprecated replaced by {@link #subscribeToFeed(com.dukascopy.api.feed.ITailoredFeedDescriptor, com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void subscribeToRenkoBarFeed(
    		Instrument instrument,
    		OfferSide offerSide,
    		PriceRange brickSize,
    		IRenkoBarFeedListener listener
    );

    /**
     * Unsubscribes passed listener from renko bars feed notification
     *
     * @param listener listener to unsubscribe
     * @deprecated replaced by {@link #unsubscribeFromFeed(com.dukascopy.api.feed.ITailoredFeedListener)}
     */
    @Deprecated
    void unsubscribeFromRenkoBarFeed(IRenkoBarFeedListener listener);

    /**
     * Returns interface that allows to start/stop and control other strategies
     *
     * @return {@link IStrategies} implementation
     * @deprecated use {@link com.dukascopy.api.system.IClient#startStrategy(IStrategy)} instead
     */
    @Deprecated
    IStrategies getStrategies();
}
