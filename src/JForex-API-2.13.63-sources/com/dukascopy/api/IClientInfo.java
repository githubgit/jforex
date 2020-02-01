/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Currency;

import com.dukascopy.api.IAccount.AccountState;

/**
 * Contains the account's clients information. 
 * @author aburenin
 */
public interface IClientInfo {
    
    /**
     * Returns client id.
     *
     * @return client id
     */
    String getClientId();

    /**
     * Returns client state.
     * @return client {@link AccountState state}
     */
    AccountState getClientState();
    
    /**
     * Returns currency.
     *
     * @return currency
     * @deprecated Use {@link #getAccountCurrency()} instead
     */
    @Deprecated
    Currency getCurrency();

    /**
     * Returns account currency.
     * 
     * @return account currency
     */
    ICurrency getAccountCurrency();
    
    /**
     * Returns client's equity stop loss level.
     * The equity stop loss level is in the client base currency and define 
     * an absolute level of equity from or under which the client will be stopped 
     * and their exposures closed.
     * @return client's equity stop loss level
     */
    double getStopLossLevel();
    
    /**
     * Returns current client's investment equity in the <b>trader base currency</b>.<br>
     * Value returned by this function is for information purposes and can be incorrect right after order changes,
     * as it is updated about every 5 seconds
     * 
     * @return equity
     */
    double getEquity();
    
    /**
     * Returns current client's <b>base</b> investment equity (No open Profit/Loss) in the <b>trader base currency</b>.<br>
     * Value returned by this function is for information purposes and
     * can be incorrect right after order changes, as it is updated about every 5 seconds.
     * 
     * @return <b>base</b> equity - equity without open positions' profit/loss
     */
    double getBaseEquity();
    
    /**
     * Returns client's investment balance in the <b>trader base currency</b>.<br>
     * The last client investment balance available is the balance for the previous end-of-day processing. 
     * 
     * @return balance
     */
    double getBalance();

    /**
     * Returns the client's market share - a double value with a positive sign, greater than or equal to 0.0 and less than or equal to 1.0.  
     * @return the client's market share <code>x</code> satisfying to <code>{@literal 0.0 <= x <= 1.0}</code> (multiplied by 100 shows the active client ratio in percentages)
     */
    double getRatio();
}
