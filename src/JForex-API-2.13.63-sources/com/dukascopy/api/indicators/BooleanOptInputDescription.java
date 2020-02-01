/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Describes optional input as boolean value. On UI is represented as check box.
 * 
 * @author Mark Vilkel
 */
public class BooleanOptInputDescription implements OptInputDescription {
	
	private boolean defaultValue;
	
	public BooleanOptInputDescription() {
		this(false);
	}
	
	public BooleanOptInputDescription(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * The method returns default value, which will be shown on UI
	 * 
	 * @return default value
	 */
	public boolean getDefaultValue() {
		return defaultValue;
	}

	/**
	 * The method sets default value, which will be shown on UI
	 * 
	 * @param defaultValue value to set as default
	 */
	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/**
     * Returns parameter's default value
     * 
     * @return default value
     */
    @Override
    public Object getOptInputDefaultValue() {
        return Boolean.valueOf(defaultValue);
    }
}
