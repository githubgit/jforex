/*
 * Copyright 2014 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

/**
 * 
 * <p>This enum represents the data (e.g. LineBreak, Renko) creation point. This enum is necessary for data feeds, which uses time sessions.
 * <p>Example: if Line Break session is Period.ONE_MIN and creation point is {@link #CLOSE}, then next Line Break line is created between
 * the close price of finished Period.ONE_MIN candle and previous N-Line Break lines. 
 * 
 * @author Janis Garsils
 */
public enum CreationPoint {
	
	/**
	 * Indicates that Line Break line calculation will be according to the OPEN price of desired session length.
	 */
	OPEN,
	/**
	 * Indicates that Line Break line calculation will be according to the CLOSE price of desired session length.
	 */
	CLOSE,
	/**
	 * Indicates that Line Break line calculation will be according to the HIGH price of desired session length.
	 */
	HIGH,
	/**
	 * Indicates that Line Break line calculation will be according to the LOW price of desired session length.
	 */
	LOW,
	/**
	 * Indicates that Line Break line calculation will be according to the (HIGH+LOW)/2 price of desired session length.
	 */
	H_L_2,
	/**
	 * Indicates that Line Break line calculation will be according to the (OPEN+CLOSE)/2 price of desired session length.
	 */
	O_C_2;
	
	public static RenkoCreationPoint convertToRenkoCreationPoint(CreationPoint creationPoint){
		if (creationPoint == null){
			return null;
		}
		return RenkoCreationPoint.valueOf(creationPoint.name());
	}
}
	
	
