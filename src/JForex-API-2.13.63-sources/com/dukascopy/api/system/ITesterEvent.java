package com.dukascopy.api.system;

import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * Contains an event that happened during the test execution of a strategy
 */
public interface ITesterEvent {

    enum EventType {
        ORDER_ENTRY, ORDER_CHANGED, ORDER_FILLED, ORDER_CLOSE, ORDER_CANCEL, MESSAGE, MARGIN_CALL, MARGIN_CUT,
        COMMISSIONS, OVERNIGHTS, ORDERS_MERGED, EXCEPTION, CANCELED_BY_USER
    }

    enum OpenTrigger {
        OPEN_BY_STRATEGY, OPEN_BY_MC
    }

    enum CloseTrigger {
        CLOSE_BY_STRATEGY, CLOSE_BY_STOP_LOSS, CLOSE_BY_TAKE_PROFIT, CLOSE_BY_MC, CLOSE_BY_MARGINCUT, CANCEL_BY_NO_MARGIN,
        CANCEL_BY_STRATEGY, CANCEL_BY_TIMEOUT, CANCEL_BY_MC, CANCEL_BY_VALIDATION, CANCEL_BY_NO_LIQUIDITY, MERGE_BY_MC, MERGE_BY_STRATEGY
    }

    double getAmount();

    double getCloseAmount();

    double getClosePrice();

    CloseTrigger getCloseTrigger();

    @Deprecated
    IFinancialInstrument getFinancialInstrument();

    Instrument getInstrument();

    String getLabel();

    double getOpenPrice();

    OpenTrigger getOpenTrigger();

    OrderCommand getOrderCommand();

    IOrder[] getOrdersMerged();

    String getText();

    long getTime();

    EventType getType();
}
