/**
 * Copyright 2014 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

/**
 * <p>This enum represents the renko creation point. Renko bars are created from sessions.
 * <p>Example: if renko session is Period.ONE_MIN and creation point is {@link RenkoCreationPoint#CLOSE}, then renko bars are created between
 * the close prices of two sequential Period.ONE_MIN candles. 
 * 
 * 
 * @author janis.garsils
 *
 */
public enum RenkoCreationPoint {

	/**
	 * Indicates that renko calculation will be according to the OPEN price of desired renko session.
	 */
	OPEN,
	/**
	 * Indicates that renko calculation will be according to the CLOSE price of desired renko session.
	 */
	CLOSE,
	/**
	 * Indicates that renko calculation will be according to the HIGH price of desired renko session.
	 */
	HIGH,
	/**
	 * Indicates that renko calculation will be according to the LOW price of desired renko session.
	 */
	LOW,
	/**
	 * Indicates that renko calculation will be according to the (HIGH+LOW)/2 price of desired renko session.
	 */
	H_L_2,
	/**
	 * Indicates that renko calculation will be according to the (OPEN+CLOSE)/2 price of desired renko session.
	 */
	O_C_2;
	
	public static CreationPoint convertToCreationPoint(RenkoCreationPoint renkoCreationPoint){
		if (renkoCreationPoint == null){
			return null;
		}
		return CreationPoint.valueOf(renkoCreationPoint.name());
	}
}
