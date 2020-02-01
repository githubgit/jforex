/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Contains index of first value in input, that has corresponding indicator value, and number of calculated elements.
 * For example, function has lookback == 5, user sets input with 20 elements and calls calculate function with indexes from 3 to 12.
 * In this case function has to calculate 10 elements, but it needs 5 elements for lookback, that means it can start only from index 5. In
 * result IndicatorResult will have firstValueIndex set to 5 and numberOfElements set to 12 - 5 + 1 = 8. If user executes calculate with indexes
 * from 7 to 15, then firstValueIndex will be 7 and numberOfElements - 9
 * 
 * @author Dmitry Shohov
 */
public class IndicatorResult {
    private int firstValueIndex;
    private int lastValueIndex = Integer.MIN_VALUE;
    private int numberOfElements;
    
    /**
     * Creates empty object. Use set... methods to set fields
     */
    public IndicatorResult() {
    }

    /**
     * Creates result object and sets fields
     * 
     * @param firstValueIndex index of the first element in input parameter(s), that has corresponding calculated value
     * @param numberOfElements number of elements in output
     */
    public IndicatorResult(int firstValueIndex, int numberOfElements) {
        this.firstValueIndex = firstValueIndex;
        this.numberOfElements = numberOfElements;
    }

    /**
     * Creates result object and sets fields
     * 
     * @param firstValueIndex index of the first element in input parameter(s), that has corresponding calculated value
     * @param numberOfElements number of elements in output
     * @param lastValueIndex index of the last element in input parameter(s), that has corresponding calculated value
     */
    public IndicatorResult(int firstValueIndex, int numberOfElements, int lastValueIndex) {
        this.firstValueIndex = firstValueIndex;
        this.numberOfElements = numberOfElements;
        this.lastValueIndex = lastValueIndex;
    }

    /**
     * Returns index of the first element in input parameter(s), that has corresponding calculated value
     * 
     * @return index of the first element in input parameter(s)
     */
    public int getFirstValueIndex() {
        return firstValueIndex;
    }

    /**
     * Sets index of the first element in input parameter(s), that has corresponding calculated value
     * 
     * @param firstValueIndex index of the first element in input parameter(s)
     */
    public void setFirstValueIndex(int firstValueIndex) {
        this.firstValueIndex = firstValueIndex;
    }

    /**
     * Returns index of the last element in input parameter(s), that has corresponding calculated value
     * 
     * @return index of the last element in input parameter(s)
     */
    public int getLastValueIndex() {
        return lastValueIndex;
    }

    /**
     * Sets index of the last element in input parameter(s), that has corresponding calculated value
     * 
     * @param lastValueIndex index of the last element in input parameter(s)
     */
    public void setLastValueIndex(int lastValueIndex) {
        this.lastValueIndex = lastValueIndex;
    }

    /**
     * Returns number of elements in output array
     * 
     * @return number of elements in output array
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * Sets number of elements in output array
     * 
     * @param numberOfElements number of elements in output array
     */
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
}
