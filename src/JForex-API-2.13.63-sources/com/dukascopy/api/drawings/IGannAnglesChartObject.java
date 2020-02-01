/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface IGannAnglesChartObject extends IChartDependentChartObject {
	
	double getPipsPerBar();
	void setPipsPerBar(double pipsPerBar);

    int getWidthInBars();
    void setWidthInBars(int widthInBars);

    /**
     * Sets show width label option.
     *
     * System default value: <b>true</b>
     * @param showWidthLabel show width label option
     */
    void setShowWidthLabel(boolean showWidthLabel);

    /**
     * Returns show width label option.
     *
     * System default value: <b>true</b>
     * @return show width label option
     */
    boolean isWidthLabelShown();
}
