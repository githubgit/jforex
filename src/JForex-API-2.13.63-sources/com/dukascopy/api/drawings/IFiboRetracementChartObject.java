/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface IFiboRetracementChartObject extends ILeveledChartObject {

    void setFixedLevelsRightSide(boolean fixedLevelsRightSide);

    /**
     * Sets show connecting line option.
     *
     * System default value: <b>true</b>
     * @param showConnectingLine show connecting line option
     */
    void setShowConnectingLine(boolean showConnectingLine);

    /**
     * Returns show connecting line option.
     *
     * System default value: <b>true</b>
     * @return show connecting line option
     */
    boolean isConnectingLineShown();

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
