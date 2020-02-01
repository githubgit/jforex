/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Contains order data and allows order manipulation
 * 
 * @author Denis Larka
 */
public interface IOrder {

    /**
     * Indicates state of the order
     */
    enum State {
        /**
         * Set right after order submission and before order acceptance by the server
         */
        CREATED,
        
        /**
         * Set after order submission for conditional orders. Simple BUY or SELL orders can have this state before filling or can get FILLED
         * state right after CREATED state
         */
        OPENED,
        
        /**
         * Set after order was fully or partially filled. Partially filled orders have different values returned from getRequestedAmount
         * and getAmount methods
         */
        FILLED,
        
        /**
         * Set after order was closed
         */
        CLOSED,
        
        /**
         * Set after order was canceled
         */
        CANCELED
    }

    /**
     * Returns instrument of the order
     * 
     * @return instrument
     */
    Instrument getInstrument();

    /**
     * Returns label
     * 
     * @return label
     */
    String getLabel();
    
    /**
     * Returns position or entry ID
     * 
     * @return id
     */
    String getId();

    /**
     * Returns creation time. This is the time when server accepted the order, not the time when it was submitted
     * For global accounts it is always 0.
     * 
     * @return creation time
     */
    long getCreationTime();
    
    /**
     * Returns time when server closed the order
     * 
     * @return time when order was closed
     */
    long getCloseTime();
    
    /**
     * Returns {@link IEngine.OrderCommand} of this message. 
     * After the order fill returns {@link IEngine.OrderCommand#BUY} if the order is LONG,
     * {@link IEngine.OrderCommand#SELL} otherwise.
     * 
     * @return order command of this message
     */
    IEngine.OrderCommand getOrderCommand();
    
    /**
     * Returns true if order is LONG. Equals to <code>getOrderCommand().isLong()</code>
     * 
     * @return true if order is LONG
     */
    boolean isLong();

    /**
     * Returns time of the fill
     * 
     * @return time of the order fill
     */
    long getFillTime();
    
    /**
     * Returns original constant amount of the order. The value is set on order submit and cannot be changed later.
     * @see IEngine#submitOrder(String, Instrument, com.dukascopy.api.IEngine.OrderCommand, double)
     * 
     * @return original amount of the order
     */
    double getOriginalAmount();

    /**
     * Returns amount of the order. For orders in {@link State#OPENED} state returns amount requested. For orders in {@link State#FILLED}
     * state will return filled amount. Filled amount can be different from requested amount (partial fill).
     * 
     * @return amount of the order
     */
    double getAmount();
    
    /**
     * Returns requested amount
     * 
     * @return amount requested
     */
    double getRequestedAmount();

    /**
     * Returns entry level price for conditional orders in {@link State#CREATED} and {@link State#OPENED} state or price at which order was
     * filled for orders in {@link State#FILLED} or {@link State#CLOSED} states
     * 
     * @return entry level for conditional orders or open price for positions
     */
    double getOpenPrice();

    /**
     * Returns price at which order was closed or 0 if order or order part wasn't closed. This is the price of latest close operation in case
     * of partial close
     * 
     * @return close price for closed orders, return 0 for any other type of order
     */
    double getClosePrice();

    /**
     * Returns price of stop loss condition or 0 if stop loss condition is not set. Orders submitted with stop loss condition, will have this
     * price set only after server accepts order
     * 
     * @return stop loss price or 0
     */
    double getStopLossPrice();

    /**
     * Returns price of take profit condition or 0 if take profit condition is not set. Orders submitted with take profit condition,
     * will have this price set only after server accepts order
     * 
     * @return take profit price or 0
     */
    double getTakeProfitPrice();

    /**
     * Sets stop loss price. If price is 0 or negative, then stop loss condition will be removed. Default stop loss side is BID for long orders and ASK
     * for short. This method will send command to the server, {@link #getStopLossPrice()} method will still return old value until server
     * will accept this changes.
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * Not applicable for Global accounts.
     * 
     * <pre>{@code
     *  if (order.getState() == IOrder.State.FILLED
     *          || (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional())
     *  ) {
     *      order.setStopLossPrice(order.getOpenPrice() - instrument.getPipValue() * 20);
     *  }
     * }</pre>
     * 
     * @param price price to set
     * @throws JFException when method fails for some reason
     */
    void setStopLossPrice(double price) throws JFException;

    /**
     * Sets stop loss price. If price is 0 or negative, then stop loss condition will be removed. This method will send command to the server,
     * {@link #getStopLossPrice()} method will still return old value until server will accept this changes
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * Not applicable for Global accounts.
     * 
     * <pre>{@code
     *  if (order.getState() == IOrder.State.FILLED
     *          || (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional())
     *  ) {
     *      order.setStopLossPrice(order.getOpenPrice() - instrument.getPipValue() * 20, OfferSide.BID);
     *  }
     * }</pre>
     * 
     * @param price price to set
     * @param side side that will be used to check stop loss condition
     * @throws JFException when method fails for some reason
     */
    void setStopLossPrice(double price, OfferSide side) throws JFException;
    
    /**
     * Sets stop loss price. If price is 0 or negative, then stop loss condition will be removed. If trailingStep is bigger than 10, then trailing step
     * logic will be applied for stop loss price. This method will send command to the server, {@link #getStopLossPrice()} method will still
     * return old value until server will accept this changes
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * Not applicable for Global accounts.
     * 
     * <pre>{@code
     *  if (order.getState() == IOrder.State.FILLED
     *          || (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional())
     *  ) {
     *      order.setStopLossPrice(order.getOpenPrice() - instrument.getPipValue() * 20, OfferSide.BID, 10);
     *  }
     * }</pre>
     * 
     * @param price price to set
     * @param side side that will be used to check stop loss condition
     * @param trailingStep if == 0 then adds stop loss order without trailing step. Should be 0 or &ge; 10
     * @throws JFException trailingStep is &gt; 0 and &lt; 10 or when method fails for some reason
     */
    void setStopLossPrice(double price, OfferSide side, double trailingStep) throws JFException;

    /**
     * Sets label for entry orders.
     * 
     * <p>By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * 
     * <pre>{@code
     *  if (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional()) {
     *      order.setLabel("new_"+order.getLabel());
     *  }
     * }</pre>
     * 
     * @param label new label
     * @throws JFException if order is not in {@link State#OPENED} state or new label is null or empty
     */
    void setLabel(String label) throws JFException;

    /**
     * Returns side that is used to check stop loss condition
     * 
     * @return stop loss side
     */
    OfferSide getStopLossSide();

    /**
     * Returns current trailing step or 0 if no trailing step is set
     * 
     * @return trailing step value
     */
    double getTrailingStep();
    
    /**
     * Sets take profit price. If price is 0 or negative, then take profit condition will be removed. This method will send command to the server,
     * {@link #getTakeProfitPrice()} method will still return old value until server will accept this changes
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * Not applicable for Global accounts.
     * 
     * <pre>{@code
     *  if (order.getState() == IOrder.State.FILLED
     *          || (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional())
     *  ) {
     *      order.setTakeProfitPrice(order.getOpenPrice() + instrument.getPipValue() * 20);
     *  }
     * }</pre>
     * 
     * @param price price to set
     * @throws JFException when method fails for some reason
     */
    void setTakeProfitPrice(double price) throws JFException;
    
    /**
     * Returns comment that was set when order was submitted
     * 
     * @return comment
     */
    String getComment();

    /**
     * Sets comment for pending orders.
     *
     * <p>By default, may not be called on the same order more often than once per second
     * (exact value of max order update per second is sent by server).
     *
     * <pre>{@code
     *  if (order.getState() == IOrder.State.OPENED && order.getOrderCommand() != BUY && order.getOrderCommand() != SELL) {
     *      order.setComment("new_"+order.getComment());
     *  }
     * }</pre>
     *
     * @param comment comment text
     * @throws JFException if order is not in {@link State#OPENED} state
     */
    void setComment(String comment) throws JFException;

    /**
     * Sets amount of order in {@link State#OPENED} state or cancels pending part of partially
     * filled order when amount equals to zero is set.
     * 
     * <p>By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * 
     * <pre>{@code
     *  if (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional()) {
     *      order.setRequestedAmount(0); //cancel conditional order
     *  }
     *  
     *  if (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional()) {
     *      order.setRequestedAmount(order.getAmount() * 2); //double the order amount
     *  }
     * }</pre>
     * 
     * @param amount new amount
     * @throws JFException when:
     * <ul>
	 * <li>Order is neither {@link State#OPENED} nor {@link State#FILLED}</li>
	 * <li>Order is {@link State#OPENED} and amount is less than the minimum allowed and not 0</li>
	 * <li>Order is fully filled, i.e., in {@link State#FILLED} and with {@link IOrder#getRequestedAmount()}=={@link IOrder#getAmount()}</li>
	 * <li>Order is partially filled, i.e., in {@link State#FILLED} and the amount is not 0</li>
	 * </ul>
     */
    void setRequestedAmount(double amount) throws JFException;

    /**
     * Sets open price for order in {@link State#OPENED} state or pending part of partially filled order
     * 
     * <p>By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * 
     * <pre>{@code
     * if (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional()) {
     *     //raise open price by two pips
     *     order.setOpenPrice(order.getOpenPrice() + order.getInstrument().getPipValue() * 2);
     * }
     *  
     * if (order.getState() == IOrder.State.FILLED && order.getOrderCommand().isConditional()
     *         && order.getAmount() < order.getRequestedAmount()
     * ) {
     *     //change open price for the UNFILLED part
     *     order.setOpenPrice(order.getOpenPrice() + order.getInstrument().getPipValue() * 2);
     * }
     * }</pre>
     * 
     * @param price price of the opening condition
     * @throws JFException when price change fails
     */
    void setOpenPrice(double price) throws JFException;

    /**
     * Sets open price for order in {@link State#OPENED} state or pending part of partially filled order
     *
     * <p>By default, may not be called on the same order more often than once per second
     * (exact value of max order update per second is sent by server).
     *
     * <pre>{@code
     * if (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional()) {
     *     //raise open price by two pips
     *     order.setOpenPrice(order.getOpenPrice() + order.getInstrument().getPipValue() * 2, 1);
     * }
     *
     * if (order.getState() == IOrder.State.FILLED && order.getOrderCommand().isConditional()
     *         && order.getAmount() < order.getRequestedAmount()
     * ) {
     *     //change open price for the UNFILLED part
     *     order.setOpenPrice(order.getOpenPrice() + order.getInstrument().getPipValue() * 2, 1);
     * }
     * }</pre>
     *
     * @param price price of the opening condition
     * @param slippage required price slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @throws JFException when price change fails
     */
    void setOpenPrice(double price, double slippage) throws JFException;

    /**
     * Sends a request to close the position with specified amount, price and slippage. 
     * If order has both pending and filled parts and amount is greater than 0, only filled part will be closed.
     * If amount is 0 then both filled and pending parts will be closed/canceled.
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. Full position close is not allowed for Global accounts. 
     *   
     * <pre>
     *  if (order.getState() == IOrder.State.FILLED) {
     *       // conditional partial close by price, with default slippage of 1 pip
     *      double lastBid = history.getLastTick(instrument).getBid();
     *      order.close(0.003, lastBid - 1 * instrument.getPipValue(), 1);
     *  }
     * </pre>
     * 
     * @param amount closing amount. Can be less than opened amount, in this case partial close will take place. If 0 is provided then all
     *              amount will be closed
     * @param price required close price. Close will be rejected if no liquidity at this price. This parameter doesn't affect 
     *              entry (conditional) orders.
     * @param slippage required price slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @throws JFException when called for order not in {@link State#OPENED} or {@link State#FILLED} state. 
     *  And if order is {@link State#OPENED}, but <code>amount</code> != 0 or <code>price</code> != 0 or <code>slippage</code> &ge; 0
     */
    void close(double amount, double price, double slippage) throws JFException;

    /**
     * Sends a request to close the position with specified amount, price and default slippage.
     * If order has both pending and filled parts and amount is greater than 0, only filled part will be closed.
     * If amount is 0 then both filled and pending parts will be closed/canceled.
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. Full position close is not allowed for Global accounts.  
     * 
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>close(...)</code> methods.
     *   
     * <pre>
     *  if (order.getState() == IOrder.State.FILLED) {
     *      // conditional partial close by price, with default slippage of 5 pips
     *      double lastBid = history.getLastTick(instrument).getBid();
     *      order.close(0.003, lastBid - 1 * instrument.getPipValue());
     *  }
     * </pre>
     * 
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>close(...)</code> methods.
     * 
     * @param amount closing amount. Can be less than opened amount, in this case partial close will take place. If 0 is provided then all
     *              amount will be closed
     * @param price required close price. Close will be rejected if no liquidity at this price. This parameter doesn't affect 
     *              entry (conditional) orders.
     * @throws JFException when called for order not in {@link State#OPENED} or {@link State#FILLED} state. 
     *  And if order is {@link State#OPENED}, but <code>amount</code> != 0 or <code>price</code> != 0
     */
    void close(double amount, double price)throws JFException;

    /**
     * Sends a request to close the position with specified amount, by market price and default slippage.
     * If order has both pending and filled parts and amount is greater than 0, only filled part will be closed.
     * If amount is 0 then both filled and pending parts will be closed/canceled.
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. Full position close is not allowed for Global accounts.  
     *   
     * <pre>
     *  if (order.getState() == IOrder.State.FILLED) {
     *      // unconditional partial close
     *      order.close(order.getAmount() / 2);
     *  }
     * </pre>
     * 
     * @param amount closing amount. Can be less than opened amount, in this case partial close will take place. If 0 is provided then all
     *              amount will be closed
     * @throws JFException when called for order not in {@link State#OPENED} or {@link State#FILLED} state. 
     *  And if order is {@link State#OPENED}, but <code>amount</code> != 0
     */
    void close(double amount) throws JFException;

    /**
     * Sends a request to fully close position by market price or cancel entry order.
     * If order has both pending and filled parts, both will be closed/canceled.
     * 
     * <p>Applicable to orders of state {@link State#OPENED} and {@link State#FILLED}. Not allowed for Global accounts.
     *   
     * <pre>{@code
     *  if (order.getState() == IOrder.State.FILLED) {
     *      // unconditional close
     *      order.close();
     *  }
     *  
     *  if (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional()) {
     *      // cancel conditional order
     *      order.close();
     *  }
     * }</pre>
     *
     * @throws JFException if order is not in {@link State#OPENED} or {@link State#FILLED} state
     */
    void close() throws JFException;

    /**
     * Returns current {@link State} of the order
     * 
     * @return state
     */
    State getState();

    /**
     * Sets "good till time" for BIDs, OFFERs and LIMIT orders.
     * 
     * <p>Applicable to orders of state {@link State#OPENED}. By default, may not be called on the same order more often than once per second 
     * (exact value of max order update per second is sent by server).
     * 
     * <pre>{@code
     * //order "expires" 1 minute after its creation
     * if (order.getState() == IOrder.State.OPENED && order.getOrderCommand().isConditional()) {
     *     order.setGoodTillTime(order.getCreationTime() + TimeUnit.MINUTES.toMillis(1));
     * }
     * }</pre>
     * @param goodTillTime time when BID, OFFER or LIMIT orders should be canceled
     * @throws JFException when order is simple market order
     */
    void setGoodTillTime(long goodTillTime) throws JFException;
    
    /**
     * Returns time when order will be cancelled or 0 if order is "good till cancel"
     * 
     * @return cancel time or 0
     */
    long getGoodTillTime();
    
    /**
     * Blocks strategy thread (with exception of {@link IStrategy#onMessage(IMessage)}) until the order changes it's state or any order value gets updated.
     * All the ticks, bars and other feed elements that platform receives while waiting get dropped.
     * 
     * <p>The method should be used for imminent order updates (e.g., set stop loss, close order). Consequently it should not be used for non-imminent order updates
     * like order close on stop loss, conditional order fill, etc.
     * The method <u>must not</u> be called from the {@link IStrategy#onMessage(IMessage)} method.
     * 
     * <p>See usage example at {@link #waitForUpdate(long, TimeUnit, State...)}
     *
     * @param timeoutMills timeout to wait for order state change
     */
    void waitForUpdate(long timeoutMills);

    /**
     * Blocks strategy thread (with exception of {@link IStrategy#onMessage(IMessage)}) until the order changes it's state or any order value gets updated.
     * All the ticks, bars and other feed elements that platform receives while waiting get dropped.
     * 
     * <p>The method should be used for imminent order updates (e.g., set stop loss, close order). Consequently it should not be used for non-imminent order updates
     * like order close on stop loss, conditional order fill, etc.
     * The method <u>must not</u> be called from the {@link IStrategy#onMessage(IMessage)} method.
     * 
     * <p>See usage example at {@link #waitForUpdate(long, TimeUnit, State...)}
     *
     * @param timeout how long to wait before giving up, in units of
     *        <code>unit</code>
     * @param unit a <code>TimeUnit</code> determining how to interpret the
     *        <code>timeout</code> parameter
     * @return message generated as the result of order update or null if method exited by timeout
     */
    IMessage waitForUpdate(long timeout, TimeUnit unit);

    /**
     * Blocks strategy thread (with exception of {@link IStrategy#onMessage(IMessage)}) until the order changes it's state value to one of the expected <code>states</code>. 
     * If <code>states</code> is empty, any order update will cause the thread unblocking.
     * All the ticks, bars and other feed elements that platform receives while waiting get dropped.
     * 
     * <p>The method should be used for imminent order updates (e.g., set stop loss, close order). Consequently it should not be used for non-imminent order updates
     * like order close on stop loss, conditional order fill, etc.
     * The method <u>must not</u> be called from the {@link IStrategy#onMessage(IMessage)} method.
     * 
     * <p>See usage example at {@link #waitForUpdate(long, TimeUnit, State...)}
     *
     * @param states a set of expected states. If null or empty - blocks until order changes it's state to any value.
     * 
     * @throws JFException when order is in state that cannot be changed to one of expected states
     * @return message generated as the result of order update. Can be null if method exited by timeout
     */
    IMessage waitForUpdate(State ...states) throws JFException;
    
    /**
     * Blocks strategy thread (with exception of {@link IStrategy#onMessage(IMessage)}) until the order changes it's state value to one of the expected <code>states</code>. 
     * If <code>states</code> is empty, any order update will cause the thread unblocking.
     * All the ticks, bars and other feed elements that platform receives while waiting get dropped.
     * 
     * <p>The method should be used for imminent order updates (e.g., set stop loss, close order). Consequently it should not be used for non-imminent order updates
     * like order close on stop loss, conditional order fill, etc.
     * The method <u>must not</u> be called from the {@link IStrategy#onMessage(IMessage)} method.
     * 
     * <p>See usage example at {@link #waitForUpdate(long, TimeUnit, State...)}
     *
     * @param timeoutMills timeout to wait for order state change
     * @param states a set of expected states. If null or empty - blocks until order changes it's state to any value. 
     * 
     * @throws JFException when order is in state that cannot be changed to one of expected states
     * @return message generated as the result of order update. Can be null if method exited by timeout
     */
    IMessage waitForUpdate(long timeoutMills, State ...states) throws JFException;
    
    /**
     * Blocks strategy thread (with exception of {@link IStrategy#onMessage(IMessage)}) until the order changes it's state value to one of the expected <code>states</code>. 
     * If <code>states</code> is empty, any order update will cause the thread unblocking.
     * All the ticks, bars and other feed elements that platform receives while waiting get dropped.
     * 
     * <p>The method should be used for imminent order updates (e.g., set stop loss, close order). Consequently it should not be used for non-imminent order updates
     * like order close on stop loss, conditional order fill, etc.
     * The method <u>must not</u> be called from the {@link IStrategy#onMessage(IMessage)} method.
     * 
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IConsole console = context.getConsole();
     *     context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
     *          
     *     //submit order and await its fill data
     *     IOrder o = engine.submitOrder("order", Instrument.EURUSD, OrderCommand.BUY, 0.001);
     *     o.waitForUpdate(2, TimeUnit.SECONDS, IOrder.State.FILLED);   
     *     console.getOut().format("After fill: open price=%.5f, fill time=%s", o.getOpenPrice(), DateUtils.format(o.getFillTime())).println();
     *     
     *     //adjust stop loss to be in 5 pip distance from open price and await the assigned SL price
     *     o.setStopLossPrice(o.getOpenPrice() - 0.0005);
     *     //wait max 2 sec for SL price to get updated; note that we don't expect IOrder.State change here 
     *     o.waitForUpdate(2, TimeUnit.SECONDS); 
     *     console.getOut().format("After SL update: stop loss=%.5f", o.getStopLossPrice()).println();         
     *     }
     * </pre>
     *
     * @param timeout how long to wait before giving up, in units of
     *        <code>unit</code>
     * @param unit a <code>TimeUnit</code> determining how to interpret the
     *        <code>timeout</code> parameter.
     * @param states a set of expected states. If null or empty - blocks until order changes it's state to any value. 
     * @throws JFException when order is in state that cannot be changed to one of expected states        
     * @return message generated as the result of order update. Can be null if method exited by timeout
     */
    IMessage waitForUpdate(long timeout, TimeUnit unit, State ...states) throws JFException;

    /**
     * Returns profit/loss in pips. The value is rounded to 0.1 pips.
     *
     * @return profit/loss in pips
     */
    double getProfitLossInPips();

    /**
     * Returns profit/loss in USD
     *
     * @return profit/loss in USD
     */
    double getProfitLossInUSD();

    /**
     * Returns profit/loss in account currency
     *
     * @return profit/loss in account currency
     */
    double getProfitLossInAccountCurrency();
    
    /**
     * Return position's commission in account currency.
     * 
     * <p>For global accounts (i.e., {@link IAccount#isGlobal()}) returns the accumulated commission
     * of the instrument's position that has been accumulated over the current trading session.
     * Which means that the value gets reset with every platform reconnect.
     * 
     * @return position's commission in account currency
     */
    double getCommission();
    
    /**
     * Return position's commission in USD
	 *
     * <p>For global accounts (i.e., {@link IAccount#isGlobal()}) returns the accumulated commission
     * of the instrument's position that has been accumulated over the current trading session.
     * Which means that the value gets reset with every platform reconnect.
     * 
     * @return position's commission in USD
     */
    double getCommissionInUSD();
    
    /**
     * Returns ordered history of order filling. History is sorted by fill time in ascending order.
     * 
     * @return ascending ordered history of order filling
     */
    List<IFillOrder> getFillHistory();
    
    /**
     * Returns ordered history of order closing. History is sorted by close time in ascending order.
     * 
     * @return ascending ordered history of order closing
     */
    List<ICloseOrder> getCloseHistory();
    
    /**
     * Returns whether this order is One Cancels the Other order.
     *
     * @return true if this order is One Cancels the Other order.
     */
    boolean isOCO();

    /**
     * Adds this order with specified second order to One Cancels the Other group.
     *
     * @param second second order in OCO group
     * @throws JFException if orders can't be added to OCO group
     */
    void groupToOco(IOrder second) throws JFException;

    /**
     * Removes this order from One Cancels the Other group.
     *
     * @throws JFException if order can't be removed from OCO group
     */
    void ungroupOco() throws JFException;

    /**
     * Compares order to current one
     * 
     * @deprecated use {@link java.lang.Object#equals(Object)}
     * 
     * @param order to compare with current
     * @return true if all fields are equal
     */
    @Deprecated
    boolean compare(IOrder order);

    /**
     * Returns instrument of the order
     * @return financialInstrument
     * @deprecated use {@link #getInstrument()}
     */
    @Deprecated
    IFinancialInstrument getFinancialInstrument();

}
