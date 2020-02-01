/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;

public interface ILeveledChartObject extends IFillableChartObject {

	/**
	 * Returns level label text by specified index.
	 * 
	 * @param index level index
	 * @return level label text
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	String getLevelLabel(int index);

	/**
	 * Sets label text to level, specified by index.
	 * 
	 * @param index level index
	 * @param label text to set
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	void setLevelLabel(int index, String label);

	/**
	 * Returns level value by specified index.
	 * 
	 * @param index level index
	 * @return level value coefficient. Coefficient 1.0 corresponds to the 100% level.
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	Double getLevelValue(int index);

	/**
	 * Sets value to level, specified by index.
	 * 
	 * @param index level index
	 * @param value level value coefficient. Coefficient 1.0 corresponds to the 100% level.
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	void setLevelValue(int index, Double value);

	/**
	 * Returns level color by specified index.
	 * 
	 * @param index level index
	 * @return level color
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	Color getLevelColor(int index);

	/**
	 * Sets color to level, specified by index.
	 * 
	 * @param index level index
	 * @param color color to set
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	void setLevelColor(int index, Color color);

	/**
	 * Returns level fill color by specified index.
	 *
	 * @param index level index
	 * @return level fill color
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	Color getLevelFillColor(int index);

	/**
	 * Sets fill color to level, specified by index.
	 *
	 * @param index level index
	 * @param fillColor fill color to set
	 * @exception IllegalArgumentException if level index is out of bounds
	 */
	void setLevelFillColor(int index, Color fillColor);

//    /**
//     * Returns level line style by specified index.
//     *
//	 * @param index level index
//	 * @return level line style (one of {@link com.dukascopy.api.LineStyle} constants)
//	 * @exception IllegalArgumentException if level index is out of bounds
//     */
//    int getLevelLineStyle(int index);
//
//    /**
//     * Sets line style to level, specified by index.
//     *
//	 * @param index level index
//	 * @param lineStyle line style to set (one of {@link com.dukascopy.api.LineStyle} constants)
//	 * @exception IllegalArgumentException if level index is out of bounds
//     */
//    void setLevelLineStyle(int index, int lineStyle);
//
//    /**
//     * Returns level line width by specified index.
//     *
//	 * @param index level index
//	 * @return level line width
//	 * @exception IllegalArgumentException if level index is out of bounds
//     */
//    float getLevelLineWidth(int index);
//
//    /**
//     * Sets line width to level, specified by index.
//     *
//	 * @param index level index
//	 * @param lineWidth line width to set
//	 * @exception IllegalArgumentException if level index is out of bounds
//     */
//    void setLevelLineWidth(int index, float lineWidth);

	/**
	 * Adds new level to object levels.
	 * 
	 * @param label level label text
	 * @param value level value coefficient. Coefficient 1.0 corresponds to the 100% level.
	 * @param color level color
	 */
	void addLevel(String label, Double value, Color color);

	/**
	 * Adds new level to object levels.
	 *
	 * @param label level label text
	 * @param value level value coefficient. Coefficient 1.0 corresponds to the 100% level.
	 * @param color level color
	 * @param fillColor level fill color
	 */
	void addLevel(String label, Double value, Color color, Color fillColor);

//	/**
//	 * Adds new level to object levels.
//	 *
//	 * @param label level label text
//	 * @param value level value coefficient. Coefficient 1.0 corresponds to the 100% level.
//	 * @param color level color
//	 * @param fillColor level fill color
//	 * @param lineStyle level line style (one of {@link com.dukascopy.api.LineStyle} constants)
//	 * @param lineWidth level line width
//	 */
//	void addLevel(String label, Double value, Color color, Color fillColor, int lineStyle, float lineWidth);

	/**
	 * Removes level by specified index.
	 * 
	 * @param index index of level to remove
	 * @exception IllegalArgumentException if index is out of bounds
	 */
	void removeLevel(int index);

	/**
	 * Returns number of levels.
	 * 
	 * @return number of levels
	 */
	int getLevelsCount();

    /**
     * Sets show levels option.
     *
     * System default value: <b>true</b>
     * @param showLevels show levels option
     */
    void setShowLevels(boolean showLevels);

    /**
     * Returns show levels option.
     *
     * System default value: <b>true</b>
     * @return show levels option
     */
    boolean areLevelsShown();

    /**
     * Sets levels display option.
     *
     * System default value: <b>true</b>
     * @param option levels display option
     * @param show option value
     */
    void setLevelsDisplayOption(LevelsDisplayOption option, boolean show);

    /**
     * Returns levels display option.
     *
     * System default value: <b>true</b>
     * @param option levels display option
     * @return option value
     */
    boolean getLevelsDisplayOption(LevelsDisplayOption option);

    /**
     * Returns levels display option support status.
     *
     * @param option levels display option
     * @return option support status
     */
    boolean isLevelsDisplayOptionSupported(LevelsDisplayOption option);

    enum LevelsDisplayOption {SHOW_LABELS, SHOW_VALUES, SHOW_PRICES, SHOW_VALUES_DIFF, SHOW_PRICES_DIFF}
}
