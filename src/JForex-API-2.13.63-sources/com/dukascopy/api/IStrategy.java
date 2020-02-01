/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Interface that all strategies should implement
 */
public interface IStrategy extends IJFRunnable<IContext>{

    /**
     * Called on strategy start. Here one would normally initialize variables held by IContext, 
     * subscribe to instruments and feeds (e.g. custom period, range bars, renko bricks, etc.) 
     * and do other strategy setup operations.
     * 
     * <p>It is imperative to subscribe with {@link IContext#setSubscribedInstruments(java.util.Set, boolean)} 
     * to the instruments that the strategy is going to work with, one can do it in the following way:
     * <pre>{@code
     * context.setSubscribedInstruments(new HashSet<Instrument>(Arrays.asList(Instrument.EURUSD, Instrument.AUDCAD), true));
     * }</pre>
     * 
     * @param context allows access to all system functionality
     * @throws JFException when strategy author ignores exceptions 
     */
    @Override
	void onStart(IContext context) throws JFException;

    /**
     * Called on every tick of every instrument that application is subscribed on. 
     * <p>In order to only work with ticks of a particular instrument consider tick filtering:
     * <pre>
     * public void onTick(Instrument instrument, ITick tick) throws JFException {
     *     if (!instrument.equals(Instrument.EURUSD) || !instrument.equals(Instrument.AUDCAD)){ 
     *         return; 
     *     }
     *     //the strategy from here on only works with either EURUSD or AUDCAD ticks
     * } 
     * </pre>
     * 
     * @param instrument instrument of the tick
     * @param tick tick data
     * @throws JFException when strategy author ignores exceptions
     */
    void onTick(Instrument instrument, ITick tick) throws JFException;

    /**
     * Called on every bar for every basic period and instrument that application is subscribed on.
     * <p>In order to only work with bars of a particular instrument and period consider bar filtering:
     * <pre>
     * public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
     *     if (!instrument.equals(Instrument.EURUSD) || !period.equals(Period.ONE_HOUR)){ 
     *         return; 
     *     }
     *     //the strategy from here on only works with EURUSD ONE_HOUR bars
     * } 
     * </pre>
     * 
     * @param instrument instrument of the bar
     * @param period period of the bar
     * @param askBar bar created of ask side of the ticks
     * @param bidBar bar created of bid side of the ticks
     * @throws JFException when strategy author ignores exceptions
     */
    void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException;

    /**
     * Called when new message is received.
     * Most prominently the method receives an <code>IMessage</code> whenever the order state of any order changes,
     * this way allowing to manage order state.
     * 
     * @param message message
     * @throws JFException when strategy author ignores exceptions
     */
    void onMessage(IMessage message) throws JFException;

    /**
     * Called when account information update is received
     * 
     * @param account updated account information
     * @throws JFException when strategy author ignores exceptions
     */
    void onAccount(IAccount account) throws JFException;
    
    /**
     * Called before strategy is stopped.
     * Here, depending on strategy logic, one would consider closing all active orders, 
     * removing created chart objects, disposing any custom GUI objects, etc.
     */
    @Override
	void onStop() throws JFException;
}
