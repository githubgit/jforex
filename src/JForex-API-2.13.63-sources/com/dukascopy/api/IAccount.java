/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Currency;
import java.util.Set;

/**
 * Methods of this interface allows to retrieve account information such as current equity
 * 
 * @author Denis Larka
 */
public interface IAccount {

    /**
     * Account state: OK, MARGIN_CLOSING, MARGIN_CALL, OK_NO_MARGIN_CALL, DISABLED, BLOCKED.
     * @author Alexander Temerev, temerev@dukascopy.com
     */
    enum AccountState {

        /**
         * Permanent status 1  - Account OK, trading is allowed.
         */
        OK("OK"),

        /**
         * Temporary status - Margin call triggered, closing positions by force. Entering new orders is prohibited.
         */
        MARGIN_CLOSING("MARGIN_CLOSING"),

        /**
         * Temporary status - Margin call status. Entering new orders is prohibited.
         */
        MARGIN_CALL("MARGIN_CALL"),

        /**
         * Permanent status 3  - Account acting as usually, but margin call doesn't close positions.
         */
        OK_NO_MARGIN_CALL("OK_NO_MARGIN_CALL"),

        /**
         * Permanent status 2 - Margin call. Entering new orders is prohibited.
         */
        DISABLED("DISABLED"),

        /**
         * Permanent status 4 - Account is blocked. Entering new orders is prohibited.
         */
        BLOCKED("BLOCKED");


        private String value;

        AccountState(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Returns currency.
     *
     * @return currency
     * @deprecated Use {@link #getAccountCurrency()} instead
     */
    Currency getCurrency();
    
    /**
     * Returns account currency.
     * 
     * @return account currency
     */
    ICurrency getAccountCurrency();

    /**
     * Returns current equity. Value returned by this function is for information purposes and can be incorrect right after order changes,
     * as it is updated about every 5 seconds
     * 
     * @return equity
     */
    double getEquity();
    
    /**
     * Returns current <b>base</b> equity (No open Profit/Loss). <br>
     * Value returned by this function is for information purposes and
     * can be incorrect right after order changes, as it is updated about every 5 seconds.
     * 
     * @return <b>base</b> equity - equity without open positions' profit/loss
     */
    double getBaseEquity();
    
    /**
     * Returns account balance.
     * The last account balance available is the balance for the previous end-of-day processing.
     * 
     * @return balance
     */
    double getBalance();
    
    /**
     * Returns current leverage. This is fixed value that is set when you sign contract with Dukascopy. Usually has value of 100
     * 
     * @return leverage
     */
    double getLeverage();

    /**
     * Returns current use of leverage. Value returned by this function is for information purposes and can be incorrect right after order changes,
     * as it is updated about every 5 seconds
     * 
     * @return leverage
     */
    double getUseOfLeverage();

    /**
     * Returns current available credit. Value returned by this function is for information purposes and can be incorrect right after order changes,
     * as it is updated about every 5 seconds
     * 
     * @return available credit
     */
    double getCreditLine();

    /**
     * Returns maximum use of leverage exceeding which will result in margin cut
     *
     * @return maximum use of leverage before margin cuts
     */
    int getMarginCutLevel();

    /**
     * Returns maximum use of leverage exceeding which will result in margin cut. Value effective before weekends
     *
     * @return maximum use of leverage before margin cuts at weekends
     */
    int getOverWeekEndLeverage();

    /**
     * Returns true if account is global account
     *
     * @return true if account is global account
     */
    boolean isGlobal();

    /**
     * Returns account id
     *
     * @return account id
     */
    String getAccountId();

    /**
     * In case of manager account, returns managed accounts client ids.
     * In case of self trader account, returns own client id.
     *
     * @return set of client ids
     * @deprecated use {@link #getClients()}
     */
    @Deprecated
    Set<String> getClientIds();

    /**
     * In case of manager account, returns client info objects for managed accounts.
     * In case of self trader account, returns client info object for own account.
     *
     * @return a set of {@link IClientInfo clients}
     */
    Set<IClientInfo> getClients();
    
    /**
     * Returns account state
     * @return account {@link AccountState state}
     */
    AccountState getAccountState();

    /**
     * Returns account's equity stop loss level.
     * The equity stop loss level is in the account base currency and define 
     * an absolute level of equity from or under which the account will be stopped 
     * and their exposures closed.
     * @return account equity stop loss level
     */
    double getStopLossLevel();

    /**
     * Returns true if account is connected to server
     *
     * @return <code>true</code> if account is connected to server, <code>false</code> - otherwise
     */
    boolean isConnected();
    
    /**
     * Returns user name
     *
     * @return user name
     */
    String getUserName();

    /**
     * Returns used margin in account currency
     *
     * @return used margin
     */
    double getUsedMargin();
}
