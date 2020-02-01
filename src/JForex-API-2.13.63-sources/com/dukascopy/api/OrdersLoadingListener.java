package com.dukascopy.api;

import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public interface OrdersLoadingListener {
    void newOrder(IFinancialInstrument financialInstrument, IOrder orderData);
}
