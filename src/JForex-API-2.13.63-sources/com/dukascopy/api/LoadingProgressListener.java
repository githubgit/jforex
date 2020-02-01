/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Listener to follow and control (cancel) loading progress
 * 
 * @author Dmitry Shohov
 */
public interface LoadingProgressListener {
    /**
     * Called on some random periods when new data arrives from curves server
     * 
     * @param start start point used for progress tracking
     * @param end end point used for progress tracking
     * @param currentPosition current position used for progress tracking
     * @param information information which can be used as details for data loading. Can vary from simple "Loading..." to something more
     * informative like "Loading data for period from 12.02.2007 to 13.02.2007..."
     */
    void dataLoaded(long start, long end, long currentPosition, String information);
    
    /**
     * Called when data loading finished for some reason, it can be because all requested data loaded or because of some failure
     * 
     * @param allDataLoaded true if all requested data loaded, false if loading finished as a result of failure
     * @param start start point used for progress tracking
     * @param end end point used for progress tracking
     * @param currentPosition current position used for progress tracking
     */
    void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition);
    
    /**
     * This method will be called to check if the job we are doing is still actual for the caller. If method returns true than job will be stopped
     * 
     * @return true if the job must be stopped
     */
    boolean stopJob();
}
