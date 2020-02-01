/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface IPriceMarkerChartObject  extends IHorizontalLineChartObject{

    /**
     * Sets show price label option.
     *
     * System default value: <b>true</b>
     * @param showPriceLabel show price label option
     */
    void setShowPriceLabel(boolean showPriceLabel);

    /**
     * Returns show price label option.
     *
     * System default value: <b>true</b>
     * @return show price label option
     */
    boolean isPriceLabelShown();
}
