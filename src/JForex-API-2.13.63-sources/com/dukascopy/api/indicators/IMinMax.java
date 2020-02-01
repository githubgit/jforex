/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Defines getMinMax method
 *
 * @author Dmitry Shohov
 */
public interface IMinMax {

    /**
     * This method will be called once for every output.
     * Method must return array of doubles with two elements - minimal and maximal value. This values will be used to
     * define scale for the indicator if it's drawn on separate panel under main chart. If one of the elements is
     * Double.NaN, then it will be ignored
     *
     * @param outputIdx index of the output parameter
     * @param values array of the values (int[], double[] or Object[] depending of the type of the output)
     * @param firstVisibleValueIndex index of the first value that corresponds to the candle that is visible on the screen. Will be 0 even if the values array is with the length of 0
     * @param lastVisibleValueIndex index of the last value that corresponds to the candle that is visible on the screen. Will be -1 if the values array is with the length of 0
     * @return returns array of doubles with minimal and maximal values. First element is minimal, second is maximal value
     */
    double[] getMinMax(int outputIdx, Object values, int firstVisibleValueIndex, int lastVisibleValueIndex);
}
