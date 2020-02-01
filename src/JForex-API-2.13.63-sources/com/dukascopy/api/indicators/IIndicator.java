/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

/**
 * Interface that should be implemented by custom(user) indicators. Interface is more like ta-lib library abstract call interface
 * <P>
 * It is possible to define some of the outputs as drawnByIndicator by calling setDrawnByIndicator method in OutputParameterInfo. In this case indicator should implement IDrawingIndicator interface and method
 * <code>public void drawOutput(Graphics g, int outputIdx, Object values, Color color, IIndicatorDrawingSupport indicatorDrawingSupport, List&lt;Shape&gt; shapes, Map&lt;Color, List&lt;Point&gt;&gt; handles)</code>
 * This method will be called every time chart surface should be redrawn
 * </P>
 * <P>
 * Also it's possible to implement IMinMax interface and define method that will define scale of the indicator if it's drawn on separate windown under chart.
 * <code>public double[] getMinMax(int outputIdx, Object values, int firstVisibleValueIndex, int lastVisibleValueIndex)</code>
 * </P>
 * 
 * @author Dmitry Shohov
 */
public interface IIndicator {
    /**
     * Called on indicator initialization
     * 
     * @param context allows access to system functionality
     */
    void onStart(IIndicatorContext context);

    /**
     * Returns object that describes indicator, how many inputs, outputs it has, where it should be shown etc
     * 
     * @return object that describes indicator
     */
    IndicatorInfo getIndicatorInfo();
    
    /**
     * Returns object that describes one of the inputs
     * 
     * @param index index of the input
     * @return object that describes input
     */
    InputParameterInfo getInputParameterInfo(int index);

    /**
     * Returns object that describes optional input
     * 
     * @param index index of the optional input
     * @return object that describes optional input
     */
    OptInputParameterInfo getOptInputParameterInfo(int index);
    
    /**
     * Returns object that describes output
     * 
     * @param index index of the output
     * @return object that describes output
     */
    OutputParameterInfo getOutputParameterInfo(int index);

    /**
     * Sets input parameter. Array parameter is an array of the doubles, ints or prices represented as double[][]. Prices is in following order:
     * open, close, high, low, volume
     * 
     * @param index index of the parameter
     * @param array array of the doubles, ints or prices represented as double[][]
     */
    void setInputParameter(int index, Object array);
    
    /**
     * Sets optional input parameter. If one of the parameters not set, then default value should be used
     * 
     * @param index index of the parameter
     * @param value int or double value
     */
    void setOptInputParameter(int index, Object value);
    
    /**
     * Sets output parameter. Size of the array should be enough to hold calculated values requested with {@link #calculate} call
     * 
     * @param index index of the parameter
     * @param array array of doubles or ints enough to hold values from startIndex to endIndex
     */
    void setOutputParameter(int index, Object array);
    
    /**
     * Returns number of elements needed to calculate value of the first element. Usually depends on optional parameters
     * 
     * @return number of elements needed to calculate value of the first element
     */
    int getLookback();

    /**
     * Returns number of elements after last element needed to calculate value of the last element. Usually depends on optional parameters
     * 
     * @return number of elements needed to calculate value of the last element
     */
    int getLookforward();
    
    /**
     * Calculates values of the indicator from startIndex to endIndex of input parameter and places them in output parameters
     * 
     * @param startIndex index of the first element in input parameters that needs corresponding indicator value. That doesn't mean that values
     * before startIndex will not be read, they will be if lookback is more than 0. That also doesn't mean that value for startIndex will be
     * calculated, it will be not if startIndex &lt; lookback. {@link IndicatorResult#getFirstValueIndex()} returns index of the first element that
     * has corresponding calculated value in output array(s)
     * @param endIndex index of the last element in input parameters that needs corresponding indicator value
     * @return object with first index in input that has calculated value and number of values calculated
     */
    IndicatorResult calculate(int startIndex, int endIndex);
}
