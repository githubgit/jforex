package com.dukascopy.api.system;

/**
 * Contains performance data concerning the execution of a strategy function.
 */
public interface IPerfStatData {

    /**
     * Returns the name and info about the function that this object describes.
     * 
     * @return info
     */
    String getFunctionInfo();
    
    /**
     * Returns the number of times that the function has been called.
     * 
     * @return percentage
     */
    int getCallCount();

    /**
     * Returns time elapsed to execute the function.
     * Time is expressed as percentage of all time elapsed in the strategy.
     * 
     * @return percentage of all time elapsed in strategy
     */
    double getDurationPercent();

    /**
     * Returns time elapsed to execute the function.
     * 
     * @return time in nanoseconds
     */
    long getDuration();
}
