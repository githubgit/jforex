/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.dukascopy.api.IAccount;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.ICurrencyConverter;
import com.dukascopy.api.IDataService;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IFeedInfo;
import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Provides access to system services
 *
 * @author Dmitry Shohov
 */
public interface IIndicatorContext {

    /**
     * Returns interface that allows to write messages into the Messages table
     *
     * @return interface for messages sending
     */
    IConsole getConsole();

    /**
     * Returns interface that can be used to get indicators registered in the system
     *
     * @return interface to work with indicators
     */
    IIndicatorsProvider getIndicatorsProvider();

    /**
     * Returns instrument of the primary input when called from {@link IIndicator#calculate} or {@link IIndicator#setInputParameter} methods
     *
     * @return instrument of the primary input
     *
     * @deprecated Use {@link IIndicatorContext#getFeedDescriptor()}.getInstrument() instead
     */
    @Deprecated
    Instrument getInstrument();

    /**
     * Returns period of the primary input when called from {@link IIndicator#calculate} or {@link IIndicator#setInputParameter} methods
     *
     * @return period of the primary input
     *
     * @deprecated Use {@link IIndicatorContext#getFeedDescriptor()}.getPeriod() instead
     */
    @Deprecated
    Period getPeriod();

    /**
     * Returns offer side of the primary input when called from {@link IIndicator#calculate} or {@link IIndicator#setInputParameter} methods
     *
     * @return offer side of the primary input
     *
     * @deprecated Use {@link IIndicatorContext#getFeedDescriptor()}.getOfferSide() instead
     */
    @Deprecated
    OfferSide getOfferSide();

    /**
     * Provides access to history from indicators
     *
     * @return history interface
     */
    IHistory getHistory();

    /**
     * Provides access to various account information
     *
     * @return {@link IAccount} interface
     */
    IAccount getAccount();

    /**
     * @return chart state described by bean {@link IFeedDescriptor}
     */
    IFeedDescriptor getFeedDescriptor();

    /**
     * @return chart state described by bean {@link IFeedInfo}
     */
    @Deprecated
    IFeedInfo getFeedInfo();

    /**
     * Returns all chart instruments
     * 
     * @return array with chart instruments
     */
    Instrument[] getChartInstruments();

    /**
     * Returns all chart financial instruments
     *
     * @return array with chart financial instruments
     */
    @Deprecated
    IFinancialInstrument[] getChartFinancialInstruments();

    /**
     * Registers listener to chart instruments changes.
     * 
     * @param listener listener to chart instruments changes
     */
    void addChartInstrumentsListener(IChartInstrumentsListener listener);

    /**
     * Registers listener to chart instruments changes.
     *
     * @param listener listener to chart instruments changes
     */
    @Deprecated
    void addChartInstrumentsListener(IChartFinancialInstrumentsListener listener);

    /**
     * Provides access to {@link IDataService}
     *
     * @return IDataService interface
     */
    IDataService getDataService();

    /**
     * Provides access to {@link IIndicatorChartPanel}
     *
     * @return IIndicatorChartPanel interface
     */
    IIndicatorChartPanel getIndicatorChartPanel();

    /**
     * Provides access to {@link ICurrencyConverter}
     *
     * @return {@link ICurrencyConverter} implementation
     */
    ICurrencyConverter getCurrencyConverter();

    /**
     * Returns true when indicator is used on Live account, false otherwise
     *
     * @return true when indicator is used on Live account, false otherwise
     */
    boolean isLive();

	/**
	 * Returns directory where reading and writing is allowed. Usually ~/My Documents/My Strategies/files
	 *
	 * @return directory with free read/write access
	 */
	File getFilesDir();

    /**
     * Executes task asynchronously.
     *
     * @param <V> Type of returned value
     * @param task Task for execution
     * @return Future representing pending completion of the task
     */
    <V> Future<V> calculateAsynchronously(Callable<V> task);

    /**
     * Updates progress status of pending task execution.
     *
     * @param percent Percent of completed work
     */
    void updateCalculationProgress(int percent);
}
