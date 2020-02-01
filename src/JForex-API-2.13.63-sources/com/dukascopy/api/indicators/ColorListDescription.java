package com.dukascopy.api.indicators;

import java.awt.Color;

/**
 * Describes optional input as a list of values
 */
public class ColorListDescription implements OptInputDescription {
    private Color defaultValue;
    private Color[] values;
    private String[] valueNames;
    
    /**
     * Creates an object without setting any field
     */
    public ColorListDescription() {
    }

    /**
     * Creates an object and sets all the fields
     * 
     * @param defaultValue default value that is used when optional input is not set
     * @param values an array of values of type Color 
     * @param valueNames name of every value
     */
    public ColorListDescription(Color defaultValue, Color[] values, String[] valueNames) {
        this.defaultValue = defaultValue;
        this.values = values;
        this.valueNames = valueNames;
    }
    
    /**
     * Returns the default value
     * 
     * @return default value
     */
    public Color getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Sets the default value
     * 
     * @param defaultValue default value
     */
    public void setDefaultValue(Color defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * Returns an array of values
     * 
     * @return array of values
     */
    public Color[] getValues() {
        return values;
    }
    
    /**
     * Sets an array of values
     * 
     * @param values array of values
     */
    public void setValues(Color[] values) {
        this.values = values;
    }
    
    /**
     * Returns an array of value names
     * 
     * @return array of value names
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
        return defaultValue;
    }
}