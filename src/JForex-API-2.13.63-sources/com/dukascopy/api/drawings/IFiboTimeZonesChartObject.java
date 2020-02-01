/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;

public interface IFiboTimeZonesChartObject extends ILeveledChartObject {

    /**
     * Returns level value by specified index.
     * 
     * @param index
     *            - level index.
     * @exception IllegalArgumentException
     *                - if level index is out of bounds.
     * @return level value.
     */
    @Override
    Double getLevelValue(int index);
    
    /**
     * Sets value to level, specified by index.
     * 
     * @param index
     *            - level index.
     * @param value
     *            - level value.
     * @exception IllegalArgumentException
     *                - if level index is out of bounds.
     */
    @Override
    void setLevelValue(int index, Double value);
    
    /**
     * Adds new level to object levels.
     * 
     * @param label
     *            - level label text.
     * @param color
     *            - level color.
     * @param value
     *            - level value.
     */
    @Override
    void addLevel(String label, Double value, Color color);

    /**
     * Sets show distance option.
     *
     * System default value: <b>true</b>
     * @param showDistance show distance option
     */
    void setShowDistance(boolean showDistance);

    /**
     * Returns show distance option.
     *
     * System default value: <b>true</b>
     * @return show distance option
     */
    boolean isDistanceShown();

}
