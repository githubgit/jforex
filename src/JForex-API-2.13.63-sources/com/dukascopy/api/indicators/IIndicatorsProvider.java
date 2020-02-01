/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import com.dukascopy.api.JFException;

import java.io.File;
import java.util.Collection;

/**
 * Interface to get indicator groups, indicator names, and indicators themselves
 * 
 * @author Dmitry Shohov
 */
public interface IIndicatorsProvider {

    /**
     * Returns list of indicator groups
     * 
     * @return list of indicator groups
     */
    Collection<String> getGroups();
    
    /**
     * Returns indicator names that belongs to specified group
     * 
     * @param groupName indicator group
     * @return indicator names
     */
    Collection<String> getNames(String groupName);
    
    /**
     * Returns list of all indicator names
     * 
     * @return list of all indicator names
     */
    Collection<String> getAllNames();
    
    /**
     * Returns indicator with specified name or null if no indicator was found
     * 
     * @param name name of the indicator
     * @param context instance of {@link IIndicatorContext IIndicatorContext}
     * 
     * @return indicator
     */
    IIndicator getIndicator(String name, IIndicatorContext context);
    
    /**
     * Returns indicator with specified name or null if no indicator was found
     * 
     * @param name name of the indicator
     * @return indicator
     */
    IIndicator getIndicator(String name);

    /**
     * Returns true if indicator should be available in add indicator dialog
     * 
     * @param indicatorName name of the indicator
     * @return true if indicator should be available in add indicator dialog or false otherwise
     */
    boolean isEnabledOnCharts(String indicatorName);

    /**
     * Returns indicator's title
     * 
     * @param name name of the indicator
     * @return indicator's title
     */
    String getTitle(String name);
    
    /** 
     * Attempts to open and register custom indicator in the system 
     * 
     * @param compiledCustomIndicatorFile file with the compiled indicator (the one with .jfx extension) 
     * @return the name of registered indicator
     * @throws JFException when indicator does not exist or can not be instantiated or does not pass the validation 
     */ 
    String registerUserIndicator(File compiledCustomIndicatorFile) throws JFException;

    /**
     * Registers listener to indicator deinitialization event
     *
     * @param indicator reference to indicator
     * @param stopListener listener to indicator deinitialization
     */
    void addIndicatorStopListener(IIndicator indicator, IStopListener stopListener);
}
