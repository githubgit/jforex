/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Describes optional input as range of values
 * 
 * @author Dmitry Shohov
 */
public class IntegerRangeDescription implements OptInputDescription {
    private int defaultValue;
    private int min;
    private int max;
    private int suggestedIncrement;

    /**
     * Creates object without setting any field
     */
    public IntegerRangeDescription() {
    }

    /**
     * Creates object and sets all the fields
     * 
     * @param defaultValue default value, that is user if used doesn't set optional input
     * @param min minimal value
     * @param max maximal value
     * @param suggestedIncrement suggested step to change this input
     */
    public IntegerRangeDescription(int defaultValue, int min, int max, int suggestedIncrement) {
        this.defaultValue = defaultValue;
        this.max = max;
        this.min = min;
        this.suggestedIncrement = suggestedIncrement;
    }
    
    /**
     * Returns parameter's default value
     * 
     * @return default value
     */
    @Override
    public Object getOptInputDefaultValue() {
        return Integer.valueOf(defaultValue);
    }
    
    /**
     * Returns default value, that is used if user doesn't set optional input
     * 
     * @return default value
     */
    public int getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Sets default value, that is used if user doesn't set optional input
     * 
     * @param defaultValue default value
     */
    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * Returns minimal value
     * 
     * @return minimal value
     */
    public int getMin() {
        return min;
    }
    
    /**
     * Sets minimal value
     * 
     * @param min minimal value
     */
    public void setMin(int min) {
        this.min = min;
    }
    
    /**
     * Returns maximal value
     * 
     * @return maximal value
     */
    public int getMax() {
        return max;
    }
    
    /**
     * Sets maximal value
     * 
     * @param max maximal value
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Returns suggested step to change this input
     * 
     * @return suggested step to change this input
     */
    public int getSuggestedIncrement() {
        return suggestedIncrement;
    }

    /**
     * Sets suggested step to change this input
     * 
     * @param suggestedIncrement suggested step to change this input
     */
    public void setSuggestedIncrement(int suggestedIncrement) {
        this.suggestedIncrement = suggestedIncrement;
    }
 }
