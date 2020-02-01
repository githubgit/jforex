/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import com.dukascopy.api.instrument.IFinancialInstrument;

import java.util.Collection;
import java.util.List;

/**
 * Interface to the main engine, that allows orders submission, merging etc
 * 
 * @author Denis Larka
 */
public interface IEngine {

    /**
     * Type of the engine
     */
    enum Type {
        LIVE, DEMO, TEST
    }

    /**    
     * Stands for strategy run mode - if it is ran locally or on remote server
     */
    enum RunMode {
    	REMOTE, LOCAL
    }
    
    /**
     * Specifies type of the order
     */
    enum OrderCommand {
        /**
         *  Buy by current market price. You can specify price and slippage, if current market price at execution moment
         *  (when order reaches server) is worse than specified price, and slippage is not big enough to execute order by current market price,
         *  then order will be rejected
         */
        BUY,
        /**
         *  Sell by current market price. You can specify price and slippage, if current market price at execution moment
         *  (when order reaches server) is worse than specified price, and slippage is not big enough to execute order by current market price,
         *  then order will be rejected
         */
        SELL,
        /**
         * Buy when ask price is &le; specified price
         */
        BUYLIMIT,
        /**
         * Sell when bid price is &ge; specified price
         */
        SELLLIMIT,
        /**
         * Buy when ask price is &ge; specified price
         */
        BUYSTOP,
        /**
         * Sell when bid price is &le; specified price
         */
        SELLSTOP,
        /**
         * Buy when bid price is &le; specified price
         */
        BUYLIMIT_BYBID,
        /**
         * Sell when ask price is &ge; specified price
         */
        SELLLIMIT_BYASK,
        /**
         * Buy when bid price is &ge; specified price
         */
        BUYSTOP_BYBID,
        /**
         * Sell when ask price is &le; specified price
         */
        SELLSTOP_BYASK,
        /**
         * Place bid at specified price
         */
        PLACE_BID,
        /**
         * Place offer at specified price
         */
        PLACE_OFFER;

        /**
         * Returns true if order is LONG and false if order is SHORT
         * @return true if order is LONG and false if order is SHORT
         */
        public boolean isLong() {
            return this == BUY || this == BUYLIMIT || this == BUYSTOP || this == BUYLIMIT_BYBID || this == BUYSTOP_BYBID || this == PLACE_BID;
        }

        /**
         * Returns true if order is SHORT and false if order is LONG
         * @return true if order is SHORT and false if order is LONG
         */
        public boolean isShort() {
            return !isLong();
        }
        
        /**
         * Returns true if order is one of STOP or LIMIT orders
         * 
         * @return true if STOP or LIMIT order, false otherwise
         */
        public boolean isConditional() {
            return this != BUY && this != SELL && this != PLACE_BID && this != PLACE_OFFER; 
        }

        public static OrderCommand getValue(long mtValue) {
        	Long index = mtValue;
        	OrderCommand returnValue = OrderCommand.values()[index.intValue()];
        	return returnValue;
        }
    }

    /**
     * Specifies strategy running mode.
     * 
     */
    enum StrategyMode {
        
        /**
         * Strategy works as usual - it can create, close, modify etc orders on it's own 
         */
        INDEPENDENT("independent"),
        
        /**
         * Strategy does not create, close or modify orders.
         * It can only signalize about intention to make any activity.  
         */
        SIGNALS("signals");
        
        private String modeString;

        public static StrategyMode fromString(String modeString) {
            for (StrategyMode mode : StrategyMode.values()) {
                if (mode.modeString.equalsIgnoreCase(modeString)) {
                    return mode;
                }
            }
            
            return null;
        } 
        
        private StrategyMode(String modeString) {
            this.modeString = modeString;
        }

        public String getModeString() {
            return modeString;
        }
    }

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     * 
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *     
     *     ITick lastTick = history.getLastTick(instrument);
     *     double price = lastTick.getAsk() + instrument.getPipValue() * 5;
     *     double sl = lastTick.getAsk() - instrument.getPipValue() * 20;
     *     double tp = lastTick.getAsk() + instrument.getPipValue() * 10;
     *     long SECOND = 1000;
     *     long gtt = lastTick.getTime() + 30 * SECOND; //withdraw after 30 secs
     *     IOrder order = engine.submitOrder("BuyStopOrder", instrument, OrderCommand.BUYSTOP, 0.1, price, 20, sl, tp, gtt, "My comment"); 
     * }
     * </pre>
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @param goodTillTime how long order should live if not executed. Only if &gt; 0, then orderCommand should <b>NOT</b> be
     *          neither {@link IEngine.OrderCommand#BUY} nor {@link IEngine.OrderCommand#SELL} market order.
     * @param comment comment that will be saved in order
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime &gt; 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime, String comment) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation
     * 
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *     
     *     ITick lastTick = history.getLastTick(instrument);
     *     double price = lastTick.getAsk() + instrument.getPipValue() * 5;
     *     double sl = lastTick.getAsk() - instrument.getPipValue() * 20;
     *     double tp = lastTick.getAsk() + instrument.getPipValue() * 10;
     *     long SECOND = 1000;
     *     long gtt = lastTick.getTime() + 30 * SECOND; //withdraw after 30 secs
     *     IOrder order = engine.submitOrder("BuyStopOrder", instrument, OrderCommand.BUYSTOP, 0.1, price, 20, sl, tp, gtt); 
     * }
     * </pre>
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @param goodTillTime how long order should live if not executed. Only if &gt; 0, then orderCommand should <b>NOT</b> be
     *          neither {@link IEngine.OrderCommand#BUY} nor {@link IEngine.OrderCommand#SELL} market order.
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime &gt; 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation
     * 
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *     
     *     ITick lastTick = history.getLastTick(instrument);
     *     double price = lastTick.getAsk() + instrument.getPipValue() * 5;
     *     double sl = lastTick.getAsk() - instrument.getPipValue() * 20;
     *     double tp = lastTick.getAsk() + instrument.getPipValue() * 10;
     *     IOrder order = engine.submitOrder("BuyStopOrder", instrument, OrderCommand.BUYSTOP, 0.1, price, 20, sl, tp); 
     * }
     * </pre>
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation
     * 
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *     
     *     ITick lastTick = history.getLastTick(instrument);
     *     double price = lastTick.getAsk() + instrument.getPipValue() * 5;
     *     IOrder order = engine.submitOrder("BuyStopOrder", instrument, OrderCommand.BUYSTOP, 0.1, price, 20); 
     * }
     * </pre>
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price, double slippage) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     *
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *     
     *     ITick lastTick = history.getLastTick(instrument);
     *     double price = lastTick.getAsk() + instrument.getPipValue() * 5;
     *     IOrder order = engine.submitOrder("BuyStopOrder", instrument, OrderCommand.BUYSTOP, 0.1, price); 
     * }
     * </pre> 
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>submitOrder(...)</code> methods.
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long, String)
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders. 
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount, double price) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     * 
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     context.setSubscribedInstruments(java.util.Collections.singleton(Instrument.EURUSD), true);
     *     
     *     IOrder order = engine.submitOrder("MarketOrder", Instrument.EURUSD, OrderCommand.BUY, 0.1);
     * }
     * </pre> 
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>submitOrder(...)</code> methods.
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long)
     * @see #submitOrder(String, Instrument, OrderCommand, double, double, double, double, double, long, String)
     * 
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param instrument instrument 
     * @param orderCommand type of submitted order. Only {@link IEngine.OrderCommand#BUY} and {@link IEngine.OrderCommand#SELL} allowed in this method
     * @param amount amount in millions for the order
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null or if orderCommand is not BUY or SELL
     */
    IOrder submitOrder(String label, Instrument instrument, OrderCommand orderCommand, double amount) throws JFException;

    /**
     * Returns order by label, or null if no order was found
     * 
     * @param label order's label
     * @return order in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state or null
     * @throws JFException if an error occurred
     */
    IOrder getOrder(String label) throws JFException;
    
    /**
     * Returns order in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state by id, or null if no order was found 
     * 
     * @param orderId order's id
     * @return order or null
     */
    IOrder getOrderById(String orderId);

    /**
     * Returns list of orders in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state for
     * specified instrument
     * 
     * @param instrument instrument
     * @return list of orders
     * @throws JFException if an error occurred
     */
    List<IOrder> getOrders(Instrument instrument) throws JFException;

    /**
     * Returns list of orders in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state
     * 
     * @return list of orders
     * @throws JFException if an error occurred
     */
    List<IOrder> getOrders() throws JFException;

    /**
     * Merges positions. On successful merge the passed positions get closed (this entails sending the {@link IMessage.Type#ORDER_CLOSE_OK}
     * message for each of the positions) and a new - resulting position gets created and the {@link IMessage.Type#ORDERS_MERGE_OK} message gets sent. 
     * There is no resulting position if the amount sum is 0 (short order amounts get negated).
     * 
     * <p>For full merge workflow see <a href = http://www.dukascopy.com/wiki/files/Order_Merge_States_Diagram.pdf>Merge states diagram</a>
     * 
     * <p>There is no merging for global accounts as there is never more than one position per instrument.
     * <pre>{@code
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IConsole console = context.getConsole();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *
     *     //0.01/BUY + 0.01/BUY + 0.01/BUY -> FILLED 0.03/BUY
     *     IOrder buy1 = engine.submitOrder("buy1", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy2 = engine.submitOrder("buy2", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy3 = engine.submitOrder("buy3", instrument, OrderCommand.BUY, 0.01);
     *     buy1.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy2.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy3.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder buyMerge = engine.mergeOrders("mergedBuyPosition", buy1, buy2, buy3);
     *     buyMerge.waitForUpdate(2000, IOrder.State.FILLED);
     *     
     *     //0.03/BUY + 0.03/SELL -> Closed position with amount=0
     *     IOrder sell = engine.submitOrder("sell", instrument, OrderCommand.SELL, 0.03);
     *     sell.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder mergedToZeroPos = engine.mergeOrders("mergedToZeroPosition", buyMerge, sell);
     *     mergedToZeroPos.waitForUpdate(2000, IOrder.State.CLOSED);     
     *     
     *     for (IOrder o : new IOrder[] {buy1, buy2, buy3, buyMerge, sell, mergedToZeroPos}) {
     *         console.getOut().format(
     *                 "%s cmd=%s created=%s closed=%s open price=%.5f close price=%.5f",
     *                 o,
     *                 o.getOrderCommand(),
     *                 DateUtils.format(o.getCreationTime()),
     *                 DateUtils.format(o.getCloseTime()),
     *                 o.getOpenPrice(),
     *                 o.getClosePrice()).println();
     *     }
     * }	
     * }</pre>
     * @param label user defined identifier for the resulting order. Label must be unique for the given user account among the current orders
     * @param orders orders to merge
     * @return resulting order in CREATED state
     * @throws JFException if there are less than 2 positions passed; if any position:
     * 		<ul>
     *          <li>is not in {@link IOrder.State#FILLED} state
     *          <li>has an attached stop loss or take profit order
     *          <li>does not belong to the same instrument
     *      </ul> 
     */
    IOrder mergeOrders(String label, IOrder... orders) throws JFException;

    /**
     * Merges positions. On successful merge the passed positions get closed (this entails sending the {@link IMessage.Type#ORDER_CLOSE_OK}
     * message for each of the positions) and a new - resulting position gets created and the {@link IMessage.Type#ORDERS_MERGE_OK} message gets sent. 
     * There is no resulting position if the amount sum is 0 (short order amounts get negated).
     * 
     * <p>For full merge workflow see <a href = http://www.dukascopy.com/wiki/files/Order_Merge_States_Diagram.pdf>Merge states diagram</a>
     * 
     * <p>There is no merging for global accounts as there is never more than one position per instrument.
     * <pre>{@code
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IConsole console = context.getConsole();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *
     *     //0.01/BUY + 0.01/BUY + 0.01/BUY -> FILLED 0.03/BUY
     *     IOrder buy1 = engine.submitOrder("buy1", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy2 = engine.submitOrder("buy2", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy3 = engine.submitOrder("buy3", instrument, OrderCommand.BUY, 0.01);
     *     buy1.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy2.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy3.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder buyMerge = engine.mergeOrders("mergedBuyPosition", buy1, buy2, buy3);
     *     buyMerge.waitForUpdate(2000, IOrder.State.FILLED);
     *     
     *     //0.03/BUY + 0.03/SELL -> Closed position with amount=0
     *     IOrder sell = engine.submitOrder("sell", instrument, OrderCommand.SELL, 0.03);
     *     sell.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder mergedToZeroPos = engine.mergeOrders("mergedToZeroPosition", buyMerge, sell);
     *     mergedToZeroPos.waitForUpdate(2000, IOrder.State.CLOSED);     
     *     
     *     for (IOrder o : new IOrder[] {buy1, buy2, buy3, buyMerge, sell, mergedToZeroPos}) {
     *         console.getOut().format(
     *                 "%s cmd=%s created=%s closed=%s open price=%.5f close price=%.5f",
     *                 o,
     *                 o.getOrderCommand(),
     *                 DateUtils.format(o.getCreationTime()),
     *                 DateUtils.format(o.getCloseTime()),
     *                 o.getOpenPrice(),
     *                 o.getClosePrice()).println();
     *     }
     * }	
     * }</pre>
     * @param label user defined identifier for the resulting order. Label must be unique for the given user account among the current orders
     * @param comment comment that will be saved in merged order
     * @param orders orders to merge
     * @return resulting order in CREATED state
     * @throws JFException if there are less than 2 positions passed; if any position:
     * 		<ul>
     *          <li>is not in {@link IOrder.State#FILLED} state
     *          <li>has an attached stop loss or take profit order
     *          <li>does not belong to the same instrument
     *      </ul> 
     */
    IOrder mergeOrders(String label, String comment, IOrder... orders) throws JFException;
    
    /**
     * Merges positions. On successful merge the passed positions get closed (this entails sending the {@link IMessage.Type#ORDER_CLOSE_OK}
     * message for each of the positions) and a new - resulting position gets created and the {@link IMessage.Type#ORDERS_MERGE_OK} message gets sent. 
     * There is no resulting position if the amount sum is 0 (short order amounts get negated).
     * 
     * <p>For full merge workflow see <a href = http://www.dukascopy.com/wiki/files/Order_Merge_States_Diagram.pdf>Merge states diagram</a>
     * 
     * <p>There is no merging for global accounts as there is never more than one position per instrument.
     * <pre>{@code
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IConsole console = context.getConsole();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *
     *     //0.01/BUY + 0.01/BUY + 0.01/BUY -> FILLED 0.03/BUY
     *     IOrder buy1 = engine.submitOrder("buy1", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy2 = engine.submitOrder("buy2", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy3 = engine.submitOrder("buy3", instrument, OrderCommand.BUY, 0.01);
     *     buy1.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy2.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy3.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder buyMerge = engine.mergeOrders("mergedBuyPosition", buy1, buy2, buy3);
     *     buyMerge.waitForUpdate(2000, IOrder.State.FILLED);
     *     
     *     //0.03/BUY + 0.03/SELL -> Closed position with amount=0
     *     IOrder sell = engine.submitOrder("sell", instrument, OrderCommand.SELL, 0.03);
     *     sell.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder mergedToZeroPos = engine.mergeOrders("mergedToZeroPosition", buyMerge, sell);
     *     mergedToZeroPos.waitForUpdate(2000, IOrder.State.CLOSED);     
     *     
     *     for (IOrder o : new IOrder[] {buy1, buy2, buy3, buyMerge, sell, mergedToZeroPos}) {
     *         console.getOut().format(
     *                 "%s cmd=%s created=%s closed=%s open price=%.5f close price=%.5f",
     *                 o,
     *                 o.getOrderCommand(),
     *                 DateUtils.format(o.getCreationTime()),
     *                 DateUtils.format(o.getCloseTime()),
     *                 o.getOpenPrice(),
     *                 o.getClosePrice()).println();
     *     }
     * }	
     * }</pre>
     * @param label user defined identifier for the resulting order. Label must be unique for the given user account among the current orders
     * @param orders orders to merge
     * @return resulting order in CREATED state
     * @throws JFException if there are less than 2 positions passed; if any position:
     * 		<ul>
     *          <li>is not in {@link IOrder.State#FILLED} state
     *          <li>has an attached stop loss or take profit order
     *          <li>does not belong to the same instrument
     *      </ul> 
     */
    IOrder mergeOrders(String label, Collection<IOrder> orders) throws JFException;

    /**
     * Merges positions. On successful merge the passed positions get closed (this entails sending the {@link IMessage.Type#ORDER_CLOSE_OK}
     * message for each of the positions) and a new - resulting position gets created and the {@link IMessage.Type#ORDERS_MERGE_OK} message gets sent. 
     * There is no resulting position if the amount sum is 0 (short order amounts get negated).
     * 
     * <p>For full merge workflow see <a href = http://www.dukascopy.com/wiki/files/Order_Merge_States_Diagram.pdf>Merge states diagram</a>
     * 
     * <p>There is no merging for global accounts as there is never more than one position per instrument.
     * <pre>{@code
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IConsole console = context.getConsole();
     *     Instrument instrument = Instrument.EURUSD;
     *     context.setSubscribedInstruments(java.util.Collections.singleton(instrument), true);
     *
     *     //0.01/BUY + 0.01/BUY + 0.01/BUY -> FILLED 0.03/BUY
     *     IOrder buy1 = engine.submitOrder("buy1", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy2 = engine.submitOrder("buy2", instrument, OrderCommand.BUY, 0.01);
     *     IOrder buy3 = engine.submitOrder("buy3", instrument, OrderCommand.BUY, 0.01);
     *     buy1.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy2.waitForUpdate(2000, IOrder.State.FILLED);
     *     buy3.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder buyMerge = engine.mergeOrders("mergedBuyPosition", "my comment", buy1, buy2, buy3);
     *     buyMerge.waitForUpdate(2000, IOrder.State.FILLED);
     *     
     *     //0.03/BUY + 0.03/SELL -> Closed position with amount=0
     *     IOrder sell = engine.submitOrder("sell", instrument, OrderCommand.SELL, 0.03);
     *     sell.waitForUpdate(2000, IOrder.State.FILLED);
     *     IOrder mergedToZeroPos = engine.mergeOrders("mergedToZeroPosition", "my comment", buyMerge, sell);
     *     mergedToZeroPos.waitForUpdate(2000, IOrder.State.CLOSED);     
     *     
     *     for (IOrder o : new IOrder[] {buy1, buy2, buy3, buyMerge, sell, mergedToZeroPos}) {
     *         console.getOut().format(
     *                 "%s cmd=%s created=%s closed=%s open price=%.5f close price=%.5f",
     *                 o,
     *                 o.getOrderCommand(),
     *                 DateUtils.format(o.getCreationTime()),
     *                 DateUtils.format(o.getCloseTime()),
     *                 o.getOpenPrice(),
     *                 o.getClosePrice()).println();
     *     }
     * }	
     * }</pre>
     * @param label user defined identifier for the resulting order. Label must be unique for the given user account among the current orders
     * @param comment comment that will be saved in merged order
     * @param orders orders to merge
     * @return resulting order in CREATED state
     * @throws JFException if there are less than 2 positions passed; if any position:
     * 		<ul>
     *          <li>is not in {@link IOrder.State#FILLED} state
     *          <li>has an attached stop loss or take profit order
     *          <li>does not belong to the same instrument
     *      </ul> 
     */
    IOrder mergeOrders(String label, String comment, Collection<IOrder> orders) throws JFException;

    /**
     * Mass close. Closes all orders passed in parameter(s)
     * <p>Not applicable for global accounts
     * @param orders orders to close
     * @throws JFException if orders not in {@link IOrder.State#FILLED} state.
     */
    void closeOrders(IOrder... orders) throws JFException;
    
    /**
     * Mass close. Closes all orders passed in parameter(s)
     * <p>Not applicable for global accounts
     * @param orders orders to close
     * @throws JFException if orders not in {@link IOrder.State#FILLED} state.
     */
    void closeOrders(Collection<IOrder> orders) throws JFException;
    
    /**
     * Returns type of the engine, one of the {@link IEngine.Type#LIVE}, {@link IEngine.Type#DEMO} or {@link IEngine.Type#TEST} for tester.
     * 
     * @return type of the engine
     */
    Type getType();

    /**
     * Broadcast message.
     *
     * @param topic topic of message
     * @param message text of message
     * @throws JFException if broadcast schedule was not successful
     */
    void broadcast(String topic, String message) throws JFException;

    /**
     * Returns strategy running mode, one of the {@link IEngine.StrategyMode#INDEPENDENT} or {@link IEngine.StrategyMode#SIGNALS}.
     * 
     * @return strategy running mode
     */
	StrategyMode getStrategyMode();
	
	/**
	 * Returns strategy run mode - if it is ran locally or on remote server
	 * 
	 * @return strategy run mode - if it is ran locally or on remote server
	 */
	RunMode getRunMode();

    /**
     * Checks whether instrument is currently tradable.
     * If the instrument is not subscribed the method will always return false.
     * You can use {@link Instrument#isTradable()} instead.
     *
     * @param instrument instrument to check
     * @return true if instrument is currently tradable, false otherwise
     */
    boolean isTradable(Instrument instrument);

    /*
     * Deprecated methods
     */

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     *
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     ITick lastTick = history.getLastTick(financialInstrument);
     *     double price = lastTick.getAsk() + financialInstrument.getPipValue() * 5;
     *     double sl = lastTick.getAsk() - financialInstrument.getPipValue() * 20;
     *     double tp = lastTick.getAsk() + financialInstrument.getPipValue() * 10;
     *     long SECOND = 1000;
     *     long gtt = lastTick.getTime() + 30 * SECOND; //withdraw after 30 secs
     *     IOrder order = engine.submitOrder("BuyStopOrder", financialInstrument, IEngine.OrderCommand.BUYSTOP, 0.1, price, 20, sl, tp, gtt, "My comment");
     * }
     * </pre>
     *
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param financialInstrument financial instrument
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @param goodTillTime how long order should live if not executed. Only if &gt; 0, then orderCommand should <b>NOT</b> be
     *          neither {@link IEngine.OrderCommand#BUY} nor {@link IEngine.OrderCommand#SELL} market order.
     * @param comment comment that will be saved in order
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime &gt; 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    @Deprecated
    IOrder submitOrder(String label, IFinancialInstrument financialInstrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime, String comment) throws JFException;


    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     *
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     ITick lastTick = history.getLastTick(financialInstrument);
     *     double price = lastTick.getAsk() + financialInstrument.getPipValue() * 5;
     *     double sl = lastTick.getAsk() - financialInstrument.getPipValue() * 20;
     *     double tp = lastTick.getAsk() + financialInstrument.getPipValue() * 10;
     *     long SECOND = 1000;
     *     long gtt = lastTick.getTime() + 30 * SECOND; //withdraw after 30 secs
     *     IOrder order = engine.submitOrder("BuyStopOrder", financialInstrument, IEngine.OrderCommand.BUYSTOP, 0.1, price, 20, sl, tp, gtt);
     * }
     * </pre>
     *
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param financialInstrument financial instrument
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @param goodTillTime how long order should live if not executed. Only if &gt; 0, then orderCommand should <b>NOT</b> be
     *          neither {@link IEngine.OrderCommand#BUY} nor {@link IEngine.OrderCommand#SELL} market order
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime &gt; 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    @Deprecated
    IOrder submitOrder(String label, IFinancialInstrument financialInstrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice, long goodTillTime) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     *
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     ITick lastTick = history.getLastTick(financialInstrument);
     *     double price = lastTick.getAsk() + financialInstrument.getPipValue() * 5;
     *     double sl = lastTick.getAsk() - financialInstrument.getPipValue() * 20;
     *     double tp = lastTick.getAsk() + financialInstrument.getPipValue() * 10;
     *     IOrder order = engine.submitOrder("BuyStopOrder", financialInstrument, IEngine.OrderCommand.BUYSTOP, 0.1, price, 20, sl, tp);
     * }
     * </pre>
     *
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param financialInstrument financial instrument
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @param stopLossPrice price of the stop loss. Price should be divisible by 0.1 pips or order will be rejected
     * @param takeProfitPrice price of the take profit. Price should be divisible by 0.1 pips or order will be rejected
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime &gt; 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    @Deprecated
    IOrder submitOrder(String label, IFinancialInstrument financialInstrument, OrderCommand orderCommand, double amount, double price, double slippage, double stopLossPrice, double takeProfitPrice) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     *
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     ITick lastTick = history.getLastTick(financialInstrument);
     *     double price = lastTick.getAsk() + financialInstrument.getPipValue() * 5;
     *     IOrder order = engine.submitOrder("BuyStopOrder", financialInstrument, IEngine.OrderCommand.BUYSTOP, 0.1, price, 20);
     * }
     * </pre>
     *
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param financialInstrument financial instrument
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @param slippage slippage. The value of slippage means following:
     *      <ul>
     *          <li>if negative then default value of 5 pips is used
     *          <li>if <code>Double.isNaN(slippage) == true</code> then no slippage is used
     *          <li>otherwise, slippage is set in pips, you should pass 1, not 0.0001
     *      </ul>
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if goodTillTime &gt; 0 and orderCommand is not BID/OFFER, if amount is less
     *          than minimum allowed, if some of the required parameters is null
     */
    @Deprecated
    IOrder submitOrder(String label, IFinancialInstrument financialInstrument, OrderCommand orderCommand, double amount, double price, double slippage) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     *
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     ITick lastTick = history.getLastTick(financialInstrument);
     *     double price = lastTick.getAsk() + financialInstrument.getPipValue() * 5;
     *     IOrder order = engine.submitOrder("BuyStopOrder", financialInstrument, IEngine.OrderCommand.BUYSTOP, 0.1, price);
     * }
     * </pre>
     *
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>submitOrder(...)</code> methods.
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double)
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double, double, double)
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double, double, double, long)
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double, double, double, long, String)
     *
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param financialInstrument financial instrument
     * @param orderCommand type of submitted order
     * @param amount amount in millions for the order
     * @param price preferred price for order. If zero, then last market price visible on the JForex will be used.
     *          Price should be divisible by 0.1 pips or order will be rejected.
     *          In case of market orders, incorrect price (worse than current market) will be changed to current price and slippage
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null
     */
    @Deprecated
    IOrder submitOrder(String label, IFinancialInstrument financialInstrument, OrderCommand orderCommand, double amount, double price) throws JFException;

    /**
     * Submits new order. Returned order is in {@link IOrder.State#CREATED} status and will be updated to {@link IOrder.State#OPENED} status
     * after server confirmation.
     *
     * <pre>
     * public void onStart(IContext context) throws JFException {
     *     IEngine engine = context.getEngine();
     *     IHistory history = context.getHistory();
     *     IFinancialInstrumentProvider instrumentProvider = context.getFinancialInstrumentProvider();
     *
     *     IFinancialInstrument financialInstrument = instrumentProvider.getFinancialInstrument("EUR/USD");
     *     context.setSubscribedFinancialInstruments(Collections.singleton(financialInstrument), true);
     *
     *     IOrder order = engine.submitOrder("BuyStopOrder", financialInstrument, IEngine.OrderCommand.BUYSTOP, 0.1);
     * }
     * </pre>
     *
     * <b>Note:</b> default value of 5 pips slippage is used. To specify custom slippage, or disable slippage at all, please use extended <code>submitOrder(...)</code> methods.
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double)
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double, double, double)
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double, double, double, long)
     * @see #submitOrder(String, IFinancialInstrument, OrderCommand, double, double, double, double, double, long, String)
     *
     * @param label user defined identifier for the order. Label must be unique for the given user account among the current orders.
     * 			Allowed characters: letters, numbers and "_". Label must have at most 256 characters.
     * @param financialInstrument financialInstrument
     * @param orderCommand type of submitted order. Only {@link IEngine.OrderCommand#BUY} and {@link IEngine.OrderCommand#SELL} allowed in this method
     * @param amount amount in millions for the order
     * @return new order instance in {@link IOrder.State#CREATED} state
     * @throws JFException if label is not valid or already exists, if amount is less than minimum allowed, if some of the required
     *          parameters is null or if orderCommand is not BUY or SELL
     */
    @Deprecated
    IOrder submitOrder(String label, IFinancialInstrument financialInstrument, OrderCommand orderCommand, double amount) throws JFException;

    /**
     * Returns list of orders in {@link IOrder.State#CREATED}, {@link IOrder.State#OPENED} and {@link IOrder.State#FILLED} state for
     * specified instrument
     *
     * @param  financialInstrument financial instrument
     * @return list of orders
     * @throws JFException if an error occurred
     */
    @Deprecated
    List<IOrder> getOrders(IFinancialInstrument financialInstrument) throws JFException;

    /**
     * Merges positions. On successful merge the passed positions get closed (this entails sending the {@link IMessage.Type#ORDER_CLOSE_OK}
     * message for each of the positions) and a new - resulting position gets created and the {@link IMessage.Type#ORDERS_MERGE_OK} message gets sent.
     * There is no resulting position if the amount sum is 0 (short order amounts get negated).
     *
     * <p>For full merge workflow see <a href = http://www.dukascopy.com/wiki/files/Order_Merge_States_Diagram.pdf>Merge states diagram</a>
     *
     * <p>There is no merging for global accounts as there is never more than one position per instrument.
     *
     * @param orders positions to merge
     * @throws JFException if there are less than 2 positions passed; if any position:
     * 		<ul>
     *          <li>is not in {@link IOrder.State#FILLED} state
     *          <li>has an attached stop loss or take profit order
     *          <li>does not belong to the same instrument
     *      </ul>
     * @deprecated use {@link #mergeOrders(String label, IOrder... orders)}
     */
    @Deprecated
    void mergeOrders(IOrder... orders) throws JFException;

    /**
     *
     * Returns account name
     *
     * @deprecated use {@link IAccount#getUserName()}
     *
     * @return account name
     */
    @Deprecated
    String getAccount();
}