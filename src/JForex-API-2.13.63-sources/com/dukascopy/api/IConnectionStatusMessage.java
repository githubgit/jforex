/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Notification message about connection status changes 
 * 
 * @author viktor.sjubajev
 */
public interface IConnectionStatusMessage extends IMessage {

	/**
	 * Returns connection state
	 * 
	 * @return true if connection is established
	 */
	boolean isConnected();
}