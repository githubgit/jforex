/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.dukascopy.api.DataType;

/**
 * Describes indicator
 * 
 * @author Dmitry Shohov
 */
public class IndicatorInfo {
    private String name;
    private String title;    
	private String groupName;    
    private boolean overChart;
    private boolean overVolumes;
    private boolean unstablePeriod;
    private int numberOfInputs;
    private int numberOfOptionalInputs;
    private int numberOfOutputs;
    @Deprecated
    private boolean showOnTicks = true;
    private boolean recalculateAll;
    private boolean sparseIndicator;
    private Set<DataType> supportedDataTypes = EnumSet.allOf(DataType.class);
    private boolean recalculateOnNewCandleOnly;

    private List<LevelInfo> defaultLevelsInfo;
	
	/**
     * Creates IndicatorInfo without filling any fields
     */
    public IndicatorInfo() {
    }

    /**
     * Creates IndicatorInfo and fills all fields
     * 
     * @param name name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * @param title title of the indicator
     * @param groupName name of the indicator group
     * @param overChart true if indicator should be drawn over candles/ticks
     * @param overVolumes true if indicator should be drawn over volume information
     * @param unstablePeriod true if indicator has unstable period (like EMA or SAR). This will add more candles in every call to stabilize function
     * @param candlesticks true if indicator is Pattern Recognition function and should be shown over bars
     * @param numberOfInputs number of inputs that user should provide
     * @param numberOfOptionalInputs number of optional inputs
     * @param numberOfOutputs number of outputs, that function returns
     * @deprecated all "candlesticks" indicators are just "overChart" indicators with specific output type
     */
    public IndicatorInfo(String name, String title, String groupName, boolean overChart, boolean overVolumes, boolean unstablePeriod, boolean candlesticks,
            int numberOfInputs, int numberOfOptionalInputs, int numberOfOutputs) {
        this.groupName = groupName;
        this.name = name;
        this.title = title;
        this.numberOfInputs = numberOfInputs;
        this.numberOfOptionalInputs = numberOfOptionalInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.overChart = overChart || candlesticks;
        this.overVolumes = overVolumes;
        this.unstablePeriod = unstablePeriod;
    }

    /**
     * Creates IndicatorInfo and fills all fields
     *
     * @param name name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * @param title title of the indicator
     * @param groupName name of the indicator group
     * @param overChart true if indicator should be drawn over candles/ticks
     * @param overVolumes true if indicator should be drawn over volume information
     * @param unstablePeriod true if indicator has unstable period (like EMA or SAR). This will add more candles in every call to stabilize function
     * @param numberOfInputs number of inputs that user should provide
     * @param numberOfOptionalInputs number of optional inputs
     * @param numberOfOutputs number of outputs, that function returns
     */
    public IndicatorInfo(String name, String title, String groupName, boolean overChart, boolean overVolumes, boolean unstablePeriod,
            int numberOfInputs, int numberOfOptionalInputs, int numberOfOutputs) {
        this.groupName = groupName;
        this.name = name;
        this.title = title;
        this.numberOfInputs = numberOfInputs;
        this.numberOfOptionalInputs = numberOfOptionalInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.overChart = overChart;
        this.overVolumes = overVolumes;
        this.unstablePeriod = unstablePeriod;
    }

    /**
     * Returns name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * 
     * @return name of the indicator
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets name of the indicator. Should be something simple like SMA for Simple Moving Average or BBANDS for Bollinger Bands
     * 
     * @param name name of the indicator
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns title of the indicator
     *
     * @return title of the indicator
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title of the indicator
     *
     * @param title title of the indicator
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns name of the group
     * 
     * @return name of the group
     */
    public String getGroupName() {
        return groupName;
    }
    
    /**
     * Sets name of the group
     * 
     * @param groupName name of the group
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    /**
     * Returns true if indicator should be drawn over chart
     * 
     * @return true if indicator should be drawn over chart
     */
    public boolean isOverChart() {
        return overChart;
    }
    
    /**
     * Sets flag that defines where indicator should be drawn
     * 
     * @param overChart true if indicator should be drawn over chart
     */
    public void setOverChart(boolean overChart) {
        this.overChart = overChart;
    }
    
    /**
     * Returns true if indicator should be shown over volumes
     * 
     * @return true if indicator should be shown over volumes
     */
    public boolean isOverVolumes() {
        return overVolumes;
    }
    
    /**
     * Sets flag that defines where indicator should be drawn
     * 
     * @param overVolumes true if indicator should be shown over volumes
     */
    public void setOverVolumes(boolean overVolumes) {
        this.overVolumes = overVolumes;
    }
    
    /**
     * Returns true if indicator has unstable period (like EMA or SAR). This will add more candles in every call to stabilize function
     * 
     * @return true if indicator has unstable period
     */
    public boolean isUnstablePeriod() {
        return unstablePeriod;
    }
    
    /**
     * Sets flag that defines if indicator has unstable period
     * 
     * @param unstablePeriod true if indicator has unstable period
     */
    public void setUnstablePeriod(boolean unstablePeriod) {
        this.unstablePeriod = unstablePeriod;
    }
    
    /**
     * @return {@code false}
     * @deprecated always returns false
     */
    @Deprecated
    public boolean isCandlesticks() {
        return false;
    }
    
    /**
     * @param candlesticks true if indicator should be drawn over chart
     * @deprecated if true, then makes indicator "overChart"
     */
    @Deprecated
    public void setCandlesticks(boolean candlesticks) {
        this.overChart = overChart || candlesticks;
    }
    
    /**
     * Returns number of inputs, that should be provided before calling function
     * 
     * @return number of inputs
     */
    public int getNumberOfInputs() {
        return numberOfInputs;
    }
    
    /**
     * Sets number of inputs
     * 
     * @param numberOfInputs number of inputs
     */
    public void setNumberOfInputs(int numberOfInputs) {
        this.numberOfInputs = numberOfInputs;
    }
    
    /**
     * Returns number of optional inputs, that can be set to customize function
     * 
     * @return number of optional inputs
     */
    public int getNumberOfOptionalInputs() {
        return numberOfOptionalInputs;
    }
    
    /**
     * Sets number of optional inputs
     * 
     * @param numberOfOptionalInputs number of optional inputs
     */
    public void setNumberOfOptionalInputs(int numberOfOptionalInputs) {
        this.numberOfOptionalInputs = numberOfOptionalInputs;
    }
    
    /**
     * Returns number of indicator outputs. Usually every output represents one line
     * 
     * @return number of outputs
     */
    public int getNumberOfOutputs() {
        return numberOfOutputs;
    }
    
    /**
     * Sets number of outputs, that indicator will return
     * 
     * @param numberOfOutputs number of outputs
     */
    public void setNumberOfOutputs(int numberOfOutputs) {
        this.numberOfOutputs = numberOfOutputs;
    }

    /**
     * @return true if indicator should be shown on TICKS chart
     * @deprecated replaced by {@link #isDataTypeSupported(DataType)} 
     */
    @Deprecated
    public boolean isShowOnTicks() {
        return showOnTicks;
    }

    /**
     * @param showOnTicks true if indicator should be shown on TICKS chart
     * @deprecated replaced by {@link #setSupportedDataTypes(DataType... dataTypes)} 
     */
    @Deprecated
    public void setShowOnTicks(boolean showOnTicks) {
        this.showOnTicks = showOnTicks;
    }

    /**
     * Returns true if indicator should be recalculated for all chart data
     * 
     * @return true if indicator should be recalculated for all chart data
     */
    public boolean isRecalculateAll() {
        return recalculateAll;
    }

    /**
     * If set to true, then indicator will be recalculated for all chart data, instead of calculating it only for new arriving candle
     * 
     * @param recalculateAll true if complete recalculation
     */
    public void setRecalculateAll(boolean recalculateAll) {
        this.recalculateAll = recalculateAll;
    }

    /**
     * @return true if the indicator is a "sparse indicator" which doesn't generate values for every candle, false otherwise
     * @deprecated As of API ver. 2.6.45 replaced by {@link #isSparseIndicator()}
     */
    @Deprecated
    public boolean isSparceIndicator() {
        return sparseIndicator;
    }
    
    /**     
     * @param sparceIndicator draw indicator on more data than is visible on the screen
     * @deprecated As of API ver. 2.6.45 replaced by {@link #setSparseIndicator(boolean)}
     */
    @Deprecated
    public void setSparceIndicator(boolean sparceIndicator) {
        this.sparseIndicator = sparceIndicator;
    }
    
    /**
     * Returns true if the indicator is a "sparse indicator" which doesn't generate values for every candle, false otherwise
     *
     * @return true if the indicator is a "sparse indicator" which doesn't generate values for every candle, false otherwise
     */
    public boolean isSparseIndicator(){
    	return sparseIndicator;
    }
    
    /**
     * Set this flag to true if indicator doesn't draw values for every candle, e.g. the ZigZag indicator. This will force
     * drawing on more data than is visible on the screen making it slower but with correct lines that go beyond the
     * chart edge.
     *
     * @param sparseIndicator draw indicator on more data than is visible on the screen
     */
    public void setSparseIndicator(boolean sparseIndicator){
    	this.sparseIndicator = sparseIndicator;
    }
    
    /**
     * Returns true if indicator gets calculated only once for every newly arriving candle as opposed to every arriving tick
     * 
     * @return true if indicator gets calculated only once for every newly arriving candle as opposed to every arriving tick
     */
    public boolean isRecalculateOnNewCandleOnly() {
		return recalculateOnNewCandleOnly;
	}

    /**
     * Set to true if indicator should get calculated only once for every newly arriving candle 
     *
     * @param recalculateOnNewCandleOnly calculate indicator only once for every newly arriving candle 
     */
	public void setRecalculateOnNewCandleOnly(boolean recalculateOnNewCandleOnly) {
		this.recalculateOnNewCandleOnly = recalculateOnNewCandleOnly;
	}
    
    /**
     * Checks whether the data type is supported by the indicator
     * 
     * @param dataType data type to check
     * @return true if data type is supported by the indicator, false otherwise
     */
    public boolean isDataTypeSupported(DataType dataType){
    	return supportedDataTypes.contains(dataType);
    }

    /**
     * Sets the data types supported by the indicator
     *
     * @param dataTypes data types supported by the indicator
     */
    public void setSupportedDataTypes(DataType... dataTypes){
    	this.supportedDataTypes.clear();
        Collections.addAll(supportedDataTypes, dataTypes);
    }

    /**
     * Returns default list of levels for indicator.
     *
     * @return default list of levels for indicator
     */
    public List<LevelInfo> getDefaultLevelsInfo() {
        if (defaultLevelsInfo == null) {
            return null;
        }
        List<LevelInfo> levelsInfo = new ArrayList<>();
        for (LevelInfo levelInfo : defaultLevelsInfo) {
            levelsInfo.add(levelInfo.clone());
        }
        return levelsInfo;
    }

    /**
     * Sets default list of levels for indicator.
     *
     * @param levelsInfo default list of levels for indicator
     */
    public void setDefaultLevelsInfo(List<LevelInfo> levelsInfo) {
        if (levelsInfo == null) {
            defaultLevelsInfo = null;
            return;
        }
        defaultLevelsInfo = new ArrayList<>();
        for (LevelInfo levelInfo : levelsInfo) {
            defaultLevelsInfo.add(levelInfo.clone());
        }
    }

    /**
     * Sets default list of levels for indicator.
     *
     * @param levelsInfo default list of levels for indicator
     */
    public void setDefaultLevelsInfo(LevelInfo[] levelsInfo) {
        setDefaultLevelsInfo(levelsInfo != null ? Arrays.asList(levelsInfo) : null);
    }
}
