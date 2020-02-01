/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Describes optional input
 * 
 * @author Dmitry Shohov
 */
public class OptInputParameterInfo {
    /**
     * Type of the optional input
     * 
     * @author Dmitry Shohov
     */
    public enum Type {
        /**
         * Optional input is percent
         */
        PERCENT,
        /**
         * Optional input is degree
         */
        DEGREE,
        /**
         * Optional input is currency
         */
        CURRENCY,
        /**
         * Optional input is something other
         */
        OTHER
    }
    
    private String name;
    private Type type;
    private OptInputDescription description;
    
    /**
     * Creates descriptor without setting any field
     */
    public OptInputParameterInfo() {
    }

    /**
     * Creates descriptor and sets all the fields
     * 
     * @param name name of the optional input
     * @param type type of the optional input
     * @param description object that describes possible values of the optional input
     * @see IntegerListDescription
     * @see IntegerRangeDescription
     * @see DoubleListDescription
     * @see DoubleRangeDescription
     */
    public OptInputParameterInfo(String name, Type type, OptInputDescription description) {
        this.description = description;
        this.name = name;
        this.type = type;
    }

    /**
     * Returns name of the optional input
     * 
     * @return name of the optional input
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the optional input
     * 
     * @param name name of the optional input
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns type of the optional input
     * 
     * @return type of the optional input
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets type of the optional input
     * 
     * @param type type of the optional input
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns object that describes possible values of the optional input
     * 
     * @return object that describes possible values of the optional input
     */
    public OptInputDescription getDescription() {
        return description;
    }

    /**
     * Sets object that describes possible values of the optional input
     * 
     * @param description object that describes possible values of the optional input
     */
    public void setDescription(OptInputDescription description) {
        this.description = description;
    }
}
