/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface ITimeMarkerChartObject extends IVerticalLineChartObject {

    /**
     * Sets show time label option.
     *
     * System default value: <b>true</b>
     * @param showTimeLabel show time label option
     */
    void setShowTimeLabel(boolean showTimeLabel);

    /**
     * Returns show time label option.
     *
     * System default value: <b>true</b>
     * @return show time label option
     */
    boolean isTimeLabelShown();
}
