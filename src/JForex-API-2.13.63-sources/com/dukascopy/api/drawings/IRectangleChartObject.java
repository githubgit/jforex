/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface IRectangleChartObject extends IFillableChartObject {

    /**
     * Allows to draw/remove size label part of label. True by default
     *
     * @param sizeLabelEnabled if true then draws size label in bottom right corner
     */
    void setSizeLabelEnabled(boolean sizeLabelEnabled);

    /**
     * If true then draws size label in bottom right corner
     *
     * @return true when size label is enabled
     */
    boolean isSizeLabelEnabled();
}
