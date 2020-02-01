/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Contains partial order data when order was partially or fully filled by server, respectively.<br>
 * Order's lifecycle contains from 0 to many elements of this data.
 * @author aburenin
 *
 */
public interface IFillOrder extends IPartialOrder{
	/**
     * Returns time when order was fully or partially filled by server.
     * 
     * @return time when order was filled (partially or fully)
     */
    long getTime();
    
    /**
     * Returns price at which order was fully or partially filled by server.
     * 
     * @return price at which order was filled (partially or fully)
     */
    double getPrice();

    /**
     * Returns amount of the filled part. 
     * 
     * @return filled amount. 
     */
    double getAmount();
}
