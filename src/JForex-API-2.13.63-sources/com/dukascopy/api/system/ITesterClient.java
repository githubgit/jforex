/*
 * Copyright 1998-2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Currency;
import java.util.concurrent.Future;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IFeedInfo;
import com.dukascopy.api.system.tester.*;

/**
 * Contains methods to control testing process
 *
 * @author Dmitry Shohov
 */
public interface ITesterClient extends IClient {
    /**
     * Different techniques of how to convert bars into ticks
     */ 
    enum InterpolationMethod {
        /**
         * One tick for every bar with open price of the bar
         */
        OPEN_TICK,
        /**
         * One tick for every bar with close price of the bar
         */
        CLOSE_TICK,
        /**
         * Four tick for every bar with open/high/low/close prices of the bar
         */
        FOUR_TICKS,
        /**
         * Cubic spline interpolation of the prices including open/high/low/close prices
         */
        CUBIC_SPLINE;

        public static InterpolationMethod fromString(String stringValue) {
            InterpolationMethod interpolationMethod = null;
            try {
                interpolationMethod = valueOf(stringValue);
            } catch(NullPointerException e) {
            }
            return interpolationMethod;
        }
    }

    /**
     * Different ticks loading methods
     * Depending on selected method different amount of ticks will be passed to the IStrategy.onTick() method 
     */
    enum DataLoadingMethod {
    	/**
    	 * All ticks will be loaded
    	 */
    	ALL_TICKS,
    	/**
    	 * The current Tick will be skipped in case if it has the same ask and bid prices as previous Tick
    	 */
    	DIFFERENT_PRICE_TICKS,
    	/**
    	 * Pivot or extremum Ticks will be loaded
    	 */
    	PIVOT_TICKS,
    	/**
    	 * Ticks with price difference greater or equal to the specified number of pips will be loaded
    	 * 
    	 * @see #setPriceDifferenceInPips(int priceDifferenceInPips)
    	 */
    	TICKS_WITH_PRICE_DIFFERENCE_IN_PIPS,
        /**
         * Ticks with time difference greater or equal to the specified time interval will be loaded 
         * 
         * @see #setTimeIntervalBetweenTicks(long timeIntervalBetweenTicks)
         */
        TICKS_WITH_TIME_INTERVAL;
    	
        private int priceDifferenceInPips = 0;
        private long timeIntervalBetweenTicks = 0;

        /**
         * Sets the price difference in pips.
         * 
         * @param priceDifferenceInPips price difference in pips
         * 
         * @see #TICKS_WITH_PRICE_DIFFERENCE_IN_PIPS
         */
        public void setPriceDifferenceInPips(int priceDifferenceInPips){
            this.priceDifferenceInPips = priceDifferenceInPips;
        }

        /**
         * Returns price difference in pips
         * 
         * @return price difference in pips
         * 
         * @see #TICKS_WITH_PRICE_DIFFERENCE_IN_PIPS
         */
        public int getPriceDifferenceInPips(){
            return this.priceDifferenceInPips;
        }
        
        /**
         * Sets the time interval in milliseconds.
         * 
         * @param timeIntervalBetweenTicks time interval in milliseconds
         * 
         * @see #TICKS_WITH_TIME_INTERVAL
         */
        public void setTimeIntervalBetweenTicks(long timeIntervalBetweenTicks) {
            this.timeIntervalBetweenTicks = timeIntervalBetweenTicks;
        }
        
        /**
         * Returns time interval in milliseconds.
         * 
         * @return time interval in milliseconds
         * 
         * @see #TICKS_WITH_TIME_INTERVAL
         */
        public long getTimeIntervalBetweenTicks() {
            return timeIntervalBetweenTicks;
        }

        public static DataLoadingMethod fromString(String stringValue) {
            DataLoadingMethod dataLoadingMethod = null;
            try {
                dataLoadingMethod = valueOf(stringValue);
            } catch(NullPointerException e) {
            }
            return dataLoadingMethod;
        }
    }

    /**
     * Sets the period to use for ticks generation and time interval when to start and end testing process.
     * Changing this value will take effect only if called before testing process is started
     *
     * @param period defines period that will be used for ticks generation. Default value Period.TICK will be used if this parameter is null
     * @param offerSide offer side
     * @param interpolationMethod interpolation method
     * @param from when to start testing process. Default value will be previous day start
     * @param to when to stop testing process. Default value will be previous day end
     */ 
    void setDataInterval(Period period, OfferSide offerSide, InterpolationMethod interpolationMethod, long from, long to);

    /**
     * Sets the DataLoadingMethod to use for ticks generation and time interval when to start and end testing process.
     *
     * @param dataLoadingMethod data loading method
     * @param from when to start testing process. Default value will be previous day start
     * @param to when to stop testing process. Default value will be previous day end
     */
    void setDataInterval(DataLoadingMethod dataLoadingMethod, long from, long to);

    /**
     * returns ITesterDataInterval that is used in the testing process
     * 
     * @return ITesterDataInterval that is used in the testing process
     */
    ITesterDataInterval getDataInterval();

    /**
     * Downloads data into the local cache. It's not necessary to download data before starting your testing process,
     * If there is no data in local cache tester will download data while testing, this can slow down testing process significantly.
     * Method runs asynchronously returning before the data is actually loaded. You can use returned future to wait for
     * the loading process to complete
     *
     * @param loadingProgressListener listener to control downloading process
     * @return future to wait for the loading process to complete
     */
    Future<?> downloadData(LoadingProgressListener loadingProgressListener);

    /**
     * Starts the strategy
     *
     * @param strategy strategy to run
     * @param testerProgressListener provides progress information, allows to cancel strategy
     * @return returns process id assigned to the strategy
     * @throws IllegalStateException if not connected
     * @throws IllegalArgumentException if one of the parameters is null
     */
    long startStrategy(
    		IStrategy strategy, 
    		LoadingProgressListener testerProgressListener
    ) throws IllegalStateException, IllegalArgumentException;

    /**
     * Starts the strategy
     *
     * @param strategy strategy to run
     * @param exceptionHandler if not null then passed exception handler will be called when strategy throws exception
     * @param testerProgressListener provides progress information, allows to cancel strategy
     * @return returns process id assigned to the strategy
     * @throws IllegalStateException if not connected
     * @throws IllegalArgumentException if one of the parameters is null
     */
    long startStrategy(
    		IStrategy strategy, 
    		IStrategyExceptionHandler exceptionHandler, 
    		LoadingProgressListener testerProgressListener
    ) throws IllegalStateException, IllegalArgumentException;

    /**
     * Starts the strategy
     * 
     * @param strategy strategy to run
     * @param testerProgressListener provides progress information, allows to cancel strategy
     * @param testerExecution allows to control strategy execution process(stop, continue, cancel)
     * @param testerUserInterface provides access to various strategy testing GUI parts
     * @return returns process id assigned to the strategy
     * @throws IllegalStateException  if not connected
     * @throws IllegalArgumentException if one of the parameters is null
     */
    long startStrategy(
    		IStrategy strategy, 
    		LoadingProgressListener testerProgressListener, 
    		ITesterExecution testerExecution, 
    		ITesterUserInterface testerUserInterface
    ) throws IllegalStateException, IllegalArgumentException;
    
    /**
     * Starts the strategy
     * 
     * @param strategy strategy to run
     * @param testerProgressListener provides progress information, allows to cancel strategy
     * @param testerVisualModeParameters visual mode optional parameters
     * @param testerExecution allows to control strategy execution process(stop, continue, cancel)
     * @param testerUserInterface provides access to various strategy testing GUI parts
     * @return returns process id assigned to the strategy
     * @throws IllegalStateException  if not connected
     * @throws IllegalArgumentException if one of the parameters is null
     */
    long startStrategy(
    		IStrategy strategy, 
    		LoadingProgressListener testerProgressListener, 
    		ITesterVisualModeParameters testerVisualModeParameters, 
    		ITesterExecution testerExecution, 
    		ITesterUserInterface testerUserInterface
    ) throws IllegalStateException, IllegalArgumentException;

    /**
     * Starts the strategy
     *
     * @param strategy strategy to run
     * @param testerProgressListener provides progress information, allows to cancel strategy
     * @param testerParametersForVisualMode visual mode optional parameters
     * @param testerExecution allows to control strategy execution process(stop, continue, cancel)
     * @param testerUserInterface provides access to various strategy testing GUI parts
     * @return returns process id assigned to the strategy
     * @throws IllegalStateException  if not connected
     * @throws IllegalArgumentException if one of the parameters is null
     */
    long startStrategy(
            IStrategy strategy,
            LoadingProgressListener testerProgressListener,
            ITesterParametersForVisualMode testerParametersForVisualMode,
            ITesterExecution testerExecution,
            ITesterUserInterface testerUserInterface
    ) throws IllegalStateException, IllegalArgumentException;

    /**
     * Starts the strategy. If no GUI mode is used, then all of testerVisualModeParameters, testerExecution and testerUserInterface
     * parameters should be null
     * 
     * @param strategy strategy to run
     * @param testerProgressListener provides progress information, allows to cancel strategy
     * @param testerVisualModeParameters visual mode optional parameters
     * @param testerExecution allows to control strategy execution process(stop, continue, cancel)
     * @param testerUserInterface provides access to various strategy testing GUI parts
     * @param writeHTDataToFiles if the flag is false, then history loading and indicator calculation with big look back will be based not on interpolated data which was fed to strategy but with real data from real cache
     * @return returns process id assigned to the strategy
     * @throws IllegalStateException  if not connected
     * @throws IllegalArgumentException if one of the parameters is null
     */
    long startStrategy(
    		IStrategy strategy, 
    		LoadingProgressListener testerProgressListener, 
    		ITesterVisualModeParameters testerVisualModeParameters, 
    		ITesterExecution testerExecution, 
    		ITesterUserInterface testerUserInterface,
    		boolean writeHTDataToFiles
    ) throws IllegalStateException, IllegalArgumentException;

    /**
     * Starts the strategy. If no GUI mode is used, then all of testerVisualModeParameters, testerExecution and testerUserInterface
     * parameters should be null
     *
     * @param strategy strategy to run
     * @param testerProgressListener provides progress information, allows to cancel strategy
     * @param testerParametersForVisualMode visual mode optional parameters
     * @param testerExecution allows to control strategy execution process(stop, continue, cancel)
     * @param testerUserInterface provides access to various strategy testing GUI parts
     * @param writeHTDataToFiles if the flag is false, then history loading and indicator calculation with big look back will be based not on interpolated data which was fed to strategy but with real data from real cache
     * @return returns process id assigned to the strategy
     * @throws IllegalStateException  if not connected
     * @throws IllegalArgumentException if one of the parameters is null
     */
    long startStrategy(
            IStrategy strategy,
            LoadingProgressListener testerProgressListener,
            ITesterParametersForVisualMode testerParametersForVisualMode,
            ITesterExecution testerExecution,
            ITesterUserInterface testerUserInterface,
            boolean writeHTDataToFiles
    ) throws IllegalStateException, IllegalArgumentException;

    /**
     * @param gatherReportData gather report data
     * @deprecated use {@link #createReport(long, File)} instead
     * @see #createReport(long, File)
     */
    @Deprecated
    void setGatherReportData(boolean gatherReportData);

    /**
     * @return gather report data
     * @deprecated use {@link #createReport(long, File)} instead
     * @see #createReport(long, File)
     */
    @Deprecated
    boolean getGatherReportData();

    /**
     * If true then event log information will be collected when strategy is run.
     * Event log eats some memory and should be disabled if not needed. Default value is false.
     * Changing this value will take effect only if called before testing process is started
     *
     * @param eventLogEnabled true to enable event log
     */
    void setEventLogEnabled(boolean eventLogEnabled);

    /**
     * Returns true if event log enabled
     *
     * @return true if event log enabled
     */
    boolean getEventLogEnabled();

    /**
     * If true then the tester will collect statistics about processing time in different subsystems.
     * Collecting information slows down the testing process and should be enabled only when needed. Default value is false
     * Changing this value will take effect only if called before testing process is started
     *
     * @param processingStats if true then processing statistics will be collected by the tester
     */
    void setProcessingStatsEnabled(boolean processingStats);

    /**
     * Returns true if processing stats enabled
     *
     * @return true if processing stats enabled
     */
    boolean getProcessingStats();

    /**
     * @param file report file
     * @throws IOException if file cannot be written
     * @throws IllegalStateException when there is not report data available
     * @deprecated use {@link #createReport(long, File)} instead
     * @see #createReport(long, File)
     */
    @Deprecated
    void createReport(File file) throws IOException, IllegalStateException;

    /**
     * Will create and write report into the given file. This method should be called after testing process is complete, otherwise it will throw an exception
     *
     * @param processId process id assigned to the strategy
     * @param file file where to save report
     *
     * @throws IOException if file cannot be written
     * @throws IllegalStateException when there is not report data available
     */
    void createReport(long processId, File file) throws IOException, IllegalStateException;
    
    /**
     * Returns object containing results of the execution of a strategy. This method should be called after testing process is complete, otherwise it will throw an exception
     *
     * @param processId process id assigned to the strategy
     * 
     * @return ITesterReportData report data for the strategy
     *
     * @throws IllegalStateException when there is not report data available
     */
    ITesterReportData getReportData(long processId) throws IllegalStateException;

    /**
     * @param currency deposit currency
     * @param deposit deposit amount
     * @deprecated Use {@link #setInitialDeposit(ICurrency, double)} instead
     */
    @Deprecated
    void setInitialDeposit(Currency currency, double deposit) throws IllegalArgumentException;

    /**
     * @return deposit currency
     * @deprecated Use {@link #getInitialDepositJFCurrency()} instead
     */
    @Deprecated
    Currency getInitialDepositCurrency();

    /**
     * Sets initial deposit amount and currency. The last parameter will affect commission calculation.
     * Changing this values will take effect only if called before testing process is started
     *
     * @param currency deposit currency. Must be one of the accepted currencies for deposit. Default value USD
     * @param deposit deposit amount. Default 50,000
     *
     * @throws IllegalArgumentException if currency is not one of accepted currencies for deposits
     */
    void setInitialDeposit(ICurrency currency, double deposit) throws IllegalArgumentException;

    /**
     * Returns initial deposit
     *
     * @return initial deposit
     */
    double getInitialDeposit();
    
    /**
     * Return deposit currency
     * 
     * @return deposit currency
     */
    ICurrency getInitialDepositJFCurrency();

    /**
     * Sets leverage. Default value is 100.
     * Changing this value will take effect only if called before testing process is started
     *
     * @param leverage leverage
     */
    void setLeverage(int leverage);

    /**
     * Returns leverage
     *
     * @return leverage
     */
    int getLeverage();

    /**
     * Sets the commissions information. If left untouched or <code>new Commissions(false)</code> object is passed,
     * the commission will be calculated as described in
     * <a href="http://www.dukascopy.com/swiss/english/forex/forex_trading_accounts/commission-policy">Commission Policy</a>
     * document using "Deposit with Dukascopy" column values.
     * Changing this value will take effect only if called before testing process is started
     *
     * @param commissions commission in usd per 1mil
     */
    void setCommissions(Commissions commissions);

    /**
     * Returns Commissions object filled with limits and corresponding commissions for deposit, equity and turnover amounts
     *
     * @return Commissions object
     */
    Commissions getCommissions();

    /**
     * Sets the overnight vales. If left untouched or <code>new Overnights(false)</code> object is passed,
     * the default overnights will be used
     * Changing this value will take effect only if called before testing process is started
     *
     * @param overnights commission in usd per 1mil
     */
    void setOvernights(Overnights overnights);

    /**
     * Returns Overnights object
     *
     * @return Overnights object
     */
    Overnights getOvernights();

    /**
     * Sets level in percents, exceeding which the system will start margin cut procedure reducing use of leverage.
     * Default value is 200.
     * Changing this value will take effect only if called before testing process is started
     *
     * @param mcLevel margin cut level
     */
    void setMarginCutLevel(int mcLevel);

    /**
     * Returns margin cut level in percents
     *
     * @return margin cut level
     */
    int getMarginCutLevel();

    /**
     * Sets minimum equity amount. If actual equity becomes less than minimum equity set by this method,
     * then all positions will be merged, closed and all pending orders will be canceled.
     * Default value is 0.
     * Changing this value will take effect only if called before testing process is started
     *
     * @param mcEquity minimum equity amount
     */
    void setMCEquity(double mcEquity);

    /**
     * Returns minimum equity amount
     *
     * @return minimum equity amount
     */
    double getMCEquity();

    /**
     * Set charts that will be opened when the tester starts running.
     *
     * @param feedDescriptors feed descriptors for charts
     */
    void setCharts(Collection<IFeedDescriptor> feedDescriptors);

    /**
     * Set charts that will be opened when the tester starts running.
     *
     * @param feedInfos feed infos for charts
     */
    @Deprecated
    void setFinancialCharts(Collection<IFeedInfo> feedInfos);

    /**
     * Set filter for all charts opened from by ITesterClient.
     *
     * @param filter filter for charts
     */
    void setDefaultFilter(Filter filter);


}
