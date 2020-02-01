/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Represents client's withdrawal action
 * @author aburenin
 */
public interface IWithdrawalMessage extends IMessage {
    
    /**
     * @return client id
     */
    String getClientId();

    /**
     * @return withdrawal amount
     */
    double amount();
    
    /**
     * @return <code>true</code> if withdrawal is scheduled at the following settlement, <code>false</code> - real withdrawal has been done.
     */
    boolean isScheduled();
}
