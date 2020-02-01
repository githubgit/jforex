/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Represents message sent from server to client application
 * 
 * @author Denis Larka, Dmitry Shohov
 */
public interface IMessage {
    /**
     * Type of the message
     */
    enum Type {

        /**
         * Sent when order was rejected
         */
        ORDER_SUBMIT_REJECTED,

        /**
         * Sent after order submission was accepted by the server
         */
        ORDER_SUBMIT_OK,

        /**
         * Sent if server rejected order fill execution. One of the possible reasons is not enough margin
         */
        ORDER_FILL_REJECTED,

        /**
         * Sent if order close request was rejected
         */
        ORDER_CLOSE_REJECTED,

        /**
         * Sent after successful order closing
         */
        ORDER_CLOSE_OK,

        /**
         * Sent after successful order filling
         */
        ORDER_FILL_OK,

        /**
         * Sent after successful orders merge
         */
        ORDERS_MERGE_OK,

        /**
         * Sent if orders merge was rejected by the server
         */
        ORDERS_MERGE_REJECTED,

        /**
         * Sent after successful orders change
         */
        ORDER_CHANGED_OK,

        /**
         * Sent if orders change was rejected by the server
         */
        ORDER_CHANGED_REJECTED,
        /**
         * Message sent from broker
         */
        MAIL,

        /**
         * Market news
         */
        NEWS,
        /**
         * Market calendar
         */
        CALENDAR,
        /**
         * Notifications from server or events in the system like disconnect
         */
        NOTIFICATION,

        /**
         * Sent if system changes instrument status
         */
        INSTRUMENT_STATUS,

        /**
         * Sent when connection status changes
         */
        CONNECTION_STATUS,

        /**
         * Sent by strategy 
         */
        STRATEGY_BROADCAST,
        
        /**
         * Notifies that an order is to be sent to the server
         * 
         * @deprecated The message type is not to be used in the strategies. 
         */
        @Deprecated
        SENDING_ORDER,

        /**
         * Client's stop loss level changed message. Used for PAMM accounts
         * 
         */
        STOP_LOSS_LEVEL_CHANGED,
        
        /**
         * Client's withdrawal message
         */
        WITHDRAWAL
    }

    /**
     * Reason of message. 
     * Specific to {@link IMessage.Type}, except {@link Reason#UNDEFINED}.
     * @see IMessage#getReasons()
     */
    enum Reason {
        
        /**
         * Order closed by Stop Loss trigger.
         * Specific to {@link Type#ORDER_CLOSE_OK} message types. 
         */
        ORDER_CLOSED_BY_SL(Type.ORDER_CLOSE_OK),
        
        /**
         * Order closed by Take Profit trigger.
         * Specific to {@link Type#ORDER_CLOSE_OK} message types. 
         */
        ORDER_CLOSED_BY_TP(Type.ORDER_CLOSE_OK), 
        
        /**
         * Order get merged.
         * Specific to {@link Type#ORDER_CLOSE_OK} message types. 
         */
        ORDER_CLOSED_BY_MERGE(Type.ORDER_CLOSE_OK),
        
        /**
         * Stop Loss trigger was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_SL(Type.ORDER_CHANGED_OK), 
        
        /**
         * Take Profit trigger was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_TP(Type.ORDER_CHANGED_OK), 
        
        /**
         * Order amount was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_AMOUNT(Type.ORDER_CHANGED_OK),
        
        /**
         * Order price was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_PRICE(Type.ORDER_CHANGED_OK), 
        
        /**
         * Execution timeout (Good Till Time) was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_GTT(Type.ORDER_CHANGED_OK),
        
        /**
         * Order {@link IEngine.OrderCommand command} was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_TYPE(Type.ORDER_CHANGED_OK), 
        
        /**
         * Order is Fully Filled.
         * Specific to {@link Type#ORDER_CHANGED_OK} and {@link Type#ORDER_FILL_OK} message types.
         */
        ORDER_FULLY_FILLED(Type.ORDER_CHANGED_OK, Type.ORDER_FILL_OK),

        /**
         * Order label was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_LABEL(Type.ORDER_CHANGED_OK),

        /**
         * Order label was changed.
         * Specific to {@link Type#ORDER_CHANGED_OK} message types.
         */
        ORDER_CHANGED_COMMENT(Type.ORDER_CHANGED_OK),


        UNDEFINED;
        
        private final Set<Type> messageTypes;
        
        private Reason(Type... messageTypes){
            this.messageTypes = EnumSet.noneOf(Type.class);
            Collections.addAll(this.messageTypes, messageTypes);
        }
        
        /**
         * @param messageType {@link Type}
         * @return <code>true</code> if specified {@link Type} can has this {@link Reason} in {@link IMessage#getReasons()} set, <code>false</code> - otherwise
         */
        public boolean isMessageTypeSupported(Type messageType) {
            return messageTypes.contains(messageType);
        }
    }

    /**
     * Returns type of the message
     * 
     * @return type of the message
     */
    Type getType();
    
    /**
     * Returns a set of message's reasons. Never <code>null</code>. 
     * @return either set of reasons or empty set if there are no specific ones.
     */
    Set<Reason> getReasons();

    /**
     * Returns textual content
     * 
     * @return string content of given message
     */
    String getContent();

    /**
     * Returns {@link IOrder} linked with this message or null if there is no related order
     * 
     * @return {@link IOrder} linked with this message. Can be null
     */
    IOrder getOrder();

    /**
     * Returns time when message was created. If it was created on server then returns exact time when it was created on server
     *
     * @return time when message was created
     */
    long getCreationTime();
}
