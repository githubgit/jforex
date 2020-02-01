/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.util.Set;



public interface IPatternWidgetChartObject extends IWidgetChartObject {
    
    /**
     * Constant for PropertyChangeListener
     */
    public static final String PROPERTY_SCAN_ALL_INSTUMENTS = "scannAllInstruments";
    
    /**
     * Constants for sorting found patterns.
     * @see IPatternWidgetChartObject#setSortPatternsByCriteria(PatternsSortCriteria)
     * @see IPatternWidgetChartObject#getSortPatternsByCriteria()
     */
    public enum PatternsSortCriteria {
        QUALITY,
        MAGNITUDE,
        STARTING_TIME,
        SIZE,
        INSTRUMENT,
        PERIOD
    }
    
    /**
     * Constants to specify which prices will be used as pivot points.
     * @see IPatternWidgetChartObject#setPivotPointsPrice(PivotPointsPrice)
     * @see IPatternWidgetChartObject#getPivotPointsPrice()
     */
    public enum PivotPointsPrice {
        CLOSE,
        HIGH_LOW
    }

    
    public enum Pattern {
        ASCENDING_TRIANGLE,
        DESCENDING_TRIANGLE,
        CHANNEL_DOWN,
        CHANNEL_UP,
        DOUBLE_BOTTOM,
        DOUBLE_TOP,
        HEAD_AND_SHOULDERS,
        INVERSE_HEAD_AND_SHOULDERS,
        RECTANGLE,
        @Deprecated
        INVERSE_RECTANGLE,
        FLAG,
        FALLING_WEDGE,
        RISING_WEDGE,
        TRIANGLE,
        PENNANT,
        TRIPLE_BOTTOM,
        TRIPLE_TOP,
        BROADENING_RISING_WEDGE,
        BROADENING_FALLING_WEDGE
    }
    
    /**
     * Adds pattern to analyze. Has no effect if pattern already contains in {@link #getPatternsToAnalyze()}
     * @param pattern pattern to add
     */
    void addPattern(Pattern pattern);

    /**
     * Removes pattern from list to analyze.
     * @see #getPatternsToAnalyze()
     * @param pattern pattern to remove
     */
    void removePattern(Pattern pattern);

    /**
     * Returns current set of patterns to search.
     * @return set of patterns
     */
    Set<Pattern> getPatternsToAnalyze();

    /**
     * Returns current minimal pattern quality applied to search criteria.
     * @return minimal pattern quality
     */
    int getDesiredMinQuality();

    /**
     * Sets minimal pattern quality to search criteria.
     * @param desiredMinQuality minimal pattern quality
     */
    void setDesiredMinQuality(int desiredMinQuality);

    /**
     * Returns current minimal pattern magnitude applied to search criteria.
     * @return minimal pattern magnitude
     */
    int getDesiredMinMagnitude();

    /**
     * Sets minimal pattern magnitude to search criteria.
     * @param desiredMinMagnitude minimal pattern magnitude
     */
    void setDesiredMinMagnitude(int desiredMinMagnitude);

    PivotPointsPrice getPivotPointsPrice();
    void setPivotPointsPrice(PivotPointsPrice pivotPointsPrice);

    PatternsSortCriteria getSortPatternsByCriteria();
    void setSortPatternsByCriteria(PatternsSortCriteria criteria);

    @Deprecated
    boolean isShowAll();
    @Deprecated
    void setShowAll(boolean showAll);
    
    boolean isOnlyEmerging();
    void setOnlyEmerging(boolean onlyEmerging);
    
    boolean isScanAllInstruments();
    void setScanAllInstruments(boolean scanAllInstruments);
}
