/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Contains partial order data when order was partially or fully closed by server, respectively.<br>
 * Order's lifecycle contains from 0 to many elements of this data.
 * @author aburenin
 */
public interface ICloseOrder extends IPartialOrder {
	
	/**
     * Returns time when order was fully or partially closed by server.
     * 
     * @return time when order was closed (partially or fully)
     */
    long getTime();
    
    /**
     * Returns price at which order was fully or partially closed by server.
     * 
     * @return price at which order was closed (partially or fully)
     */
    double getPrice();

    /**
     * Returns amount of the closed part. 
     * 
     * @return closed amount. 
     */
    double getAmount();
}
