/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;


public interface IVerticalRetracementChartObject extends IChartDependentChartObject {

    /**
     * Sets show time step label option.
     *
     * System default value: <b>true</b>
     * @param showTimeStepLabel show time step label option
     */
    void setShowTimeStepLabel(boolean showTimeStepLabel);

    /**
     * Returns show time step label option.
     *
     * System default value: <b>true</b>
     * @return show time step label option
     */
    boolean isTimeStepLabelShown();

    /**
     * Sets show period labels option.
     *
     * System default value: <b>true</b>
     * @param showPeriodLabels show period labels option
     */
    void setShowPeriodLabels(boolean showPeriodLabels);

    /**
     * Returns show period labels option.
     *
     * System default value: <b>true</b>
     * @return show period labels option
     */
    boolean arePeriodLabelsShown();
}
