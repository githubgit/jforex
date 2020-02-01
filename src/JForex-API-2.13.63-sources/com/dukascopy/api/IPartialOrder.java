/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Contains partial order data (either fill or close) when order was partially or fully filled or closed by server, respectively.<br>
 * Order's lifecycle contains from 0 to many elements of this data.
 * @see IFillOrder
 * @see ICloseOrder
 * @see IOrder#getFillHistory()
 * @see IOrder#getCloseHistory()
 * @author aburenin
 *
 */
public interface IPartialOrder extends Comparable<IPartialOrder>{
	/**
     * Returns time when order was fully or partially filled/closed by server.
     * 
     * @return time when order was filled/closed (partially or fully)
     */
    long getTime();
    
    /**
     * Returns price at which order was fully or partially filled/closed by server.
     * 
     * @return price at which order was filled/closed (partially or fully)
     */
    double getPrice();

    /**
     * Returns amount of the filled/closed part. 
     * 
     * @return filled/closed amount. 
     */
    double getAmount();
}
