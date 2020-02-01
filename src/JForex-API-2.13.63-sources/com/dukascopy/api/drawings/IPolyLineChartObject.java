/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface IPolyLineChartObject extends IDecoratedChartObject {

    /**
     * maximum number of points allowed
     */
    int MAX_POINTS_COUNT = 500;
    
    /**
     * Appends new point to end of the line.
     * 
     * @param time time value
     * @param price price value
     * @return true if point appended successfully, otherwise false if reached max number of points
     * @see #MAX_POINTS_COUNT
     */
    boolean addNewPoint(long time, double price);

    /**
     * Removes point at specified position.
     * @param index point index
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<code>index &lt; 0 || index &gt;= size()</code>)
     */
    void removePoint(int index);
}
