package com.dukascopy.api.system;

import com.dukascopy.api.IOrder;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.instrument.IFinancialInstrument;

import java.util.List;

/**
 * Contains the results of the test execution of a strategy.
 */
public interface ITesterReportData {

    /**
     * Gets the name of strategy this report data describes.
     * @return The name of the strategy.
     */
    String getStrategyName();

    /**
     * Returns list of orders that were open when the strategy stopped.
     * 
     * @return list of orders
     * @see #getOpenOrders(Instrument)
     */
    List<IOrder> getOpenOrders();

    /**
     * Returns list of orders that were open when the strategy stopped. Method returns only orders of the specified instrument.
     *
     * @param financialInstrument The instrument.
     * @return list of orders
     * @see #getOpenOrders()
     */
    @Deprecated
    List<IOrder> getOpenOrders(IFinancialInstrument financialInstrument);

    /**
     * Returns list of orders that were open when the strategy stopped. Method returns only orders of the specified instrument.
     * 
     * @param instr The instrument.
     * @return list of orders
     * @see #getOpenOrders()
     */
    List<IOrder> getOpenOrders(Instrument instr);

    /**
     * Returns list of orders that were closed when the strategy stopped.
     * 
     * @return list of orders
     * @see #getClosedOrders(Instrument)
     */
    List<IOrder> getClosedOrders();

    /**
     * Returns list of orders that were closed when the strategy stopped. Method returns only orders of the specified instrument.
     *
     * @param financialInstrument The instrument.
     * @return list of orders
     * @see #getClosedOrders()
     */
    @Deprecated
    List<IOrder> getClosedOrders(IFinancialInstrument financialInstrument);

    /**
     * Returns list of orders that were closed when the strategy stopped. Method returns only orders of the specified instrument.
     * 
     * @param instr The instrument.
     * @return list of orders
     * @see #getClosedOrders()
     */
    List<IOrder> getClosedOrders(Instrument instr);

    /**
     * Returns list of strategy parameters.
     * Each parameter is represented as String array in which element with index 0 is parameter name but element with index 1 is parameter value.
     * 
     * @return The parameters.
     */
    List<String[]> getParameterValues();

    /**
     * Returns all events from event log.
     * 
     * @return Non-null list of events.
     */
    List<ITesterEvent> getEvents();

    /**
     * Returns time in milliseconds when strategy has started
     *
     * @return time in milliseconds when strategy has started
     */
    long getFrom();

    /**
     * Returns time in milliseconds when strategy has stopped
     *
     * @return time in milliseconds when strategy has stopped
     */
    long getTo();

    /**
     * Returns the total commission in USD
     *
     * @return the total commission in USD
     */
    double getCommission();

    /**
     * Returns the total turnover in USD
     *
     * @return the total turnover in USD
     */
    double getTurnover();

    /**
     * Returns initial deposit
     *
     * @return initial deposit
     */
    double getInitialDeposit();

    /**
     * Returns finish deposit
     *
     * @return finish deposit
     */
    double getFinishDeposit();

    /**
     * Returns a list that holds performance statistics for each function of the strategy
     *
     * @return performance statistics
     */
    List<IPerfStatData> getPerfStats();
}
