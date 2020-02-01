/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Describes optional input as list of values
 * 
 * @author Dmitry Shohov
 */
public class DoubleListDescription implements OptInputDescription {
    private double defaultValue;
    private double[] values;
    private String[] valueNames;
    
    /**
     * Creates object without setting any field
     */
    public DoubleListDescription() {
    }

    /**
     * Creates object and sets all the fields
     * 
     * @param defaultValue default value, that is used if user doesn't set optional input
     * @param values array of the values as doubles
     * @param valueNames name of every value
     */
    public DoubleListDescription(double defaultValue, double[] values, String[] valueNames) {
        this.defaultValue = defaultValue;
        this.valueNames = valueNames;
        this.values = values;
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
     * Returns array of the values, that user can set
     * 
     * @return array of the values, that user can set
     */
    public double[] getValues() {
        return values;
    }
    
    /**
     * Sets array of the values, that user can set
     * 
     * @param values array of the values, that user can set
     */
    public void setValues(double[] values) {
        this.values = values;
    }
    
    /**
     * Returns names of the every value in array
     * 
     * @return names of the every value in array
     */
    public String[] getValueNames() {
        return valueNames;
    }
    
    /**
     * Sets value names
     * 
     * @param valueNames value names
     */
    public void setValueNames(String[] valueNames) {
        this.valueNames = valueNames;
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
