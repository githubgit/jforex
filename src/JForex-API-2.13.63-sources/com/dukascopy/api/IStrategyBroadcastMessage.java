/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Represents message broadcasted by strategy
 * 
 * @author viktor.sjubajev
 */
public interface IStrategyBroadcastMessage extends IMessage {

	/**
	 * @return topic of broadcast message
	 */
	String getTopic();
}