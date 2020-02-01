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
public class DoubleRangeDescription implements OptInputDescription {
    private double defaultValue;
    private double min;
    private double max;
    private double suggestedIncrement;
    private int precision;
    
    /**
     * Creates object without setting any field
     */
    public DoubleRangeDescription() {
    }

    /**
     * Creates object and sets all the fields
     * 
     * @param defaultValue default value, that is user if used doesn't set optional input
     * @param min minimal value
     * @param max maximal value
     * @param suggestedIncrement suggested step to change this input
     * @param precision number of digits after decimal separator
     */
    public DoubleRangeDescription(double defaultValue, double min, double max, double suggestedIncrement, int precision) {
        this.defaultValue = defaultValue;
        this.max = max;
        this.min = min;
        this.suggestedIncrement = suggestedIncrement;
        this.precision = precision;
    }

    /**
     * Returns default value, that is used if user doesn't set optional input
     * 
     * @return default value
     */
    public double getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Sets default value, that is used if user doesn't set optional input
     * 
     * @param defaultValue default value
     */
    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Returns minimal value
     * 
     * @return minimal value
     */
    public double getMin() {
        return min;
    }

    /**
     * Sets minimal value
     * 
     * @param min minimal value
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * Returns maximal value
     * 
     * @return maximal value
     */
    public double getMax() {
        return max;
    }

    /**
     * Sets maximal value
     * 
     * @param max maximal value
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * Returns suggested step to change this input
     * 
     * @return suggested step to change this input
     */
    public double getSuggestedIncrement() {
        return suggestedIncrement;
    }

    /**
     * Sets suggested step to change this input
     * 
     * @param suggestedIncrement suggested step to change this input
     */
    public void setSuggestedIncrement(double suggestedIncrement) {
        this.suggestedIncrement = suggestedIncrement;
    }

    /**
     * Returns number of digits after decimal separator, that should be used
     * 
     * @return number of digits after decimal separator, that should be used
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Sets number of digits after decimal separator, that should be used
     * 
     * @param precision number of digits after decimal separator, that should be used
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }
    
    /**
     * Returns parameter's default value
     * 
     * @return default value
     */
    @Override
    public Object getOptInputDefaultValue() {
        return Double.valueOf(defaultValue);
    }
}
