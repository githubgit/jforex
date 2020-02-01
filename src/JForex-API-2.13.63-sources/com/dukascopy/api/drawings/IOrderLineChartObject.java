/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;

/**
 * Use only for order preview.
 */
public interface IOrderLineChartObject extends IHorizontalLineChartObject {

    /**
     * Sets background color of chart to automatically select appropriate highlighted color.
     *
     * @param backgroundColor background color of chart
     */
    void setBackgroundColor(Color backgroundColor);
    
    /**
     * Sets foreground color.
     *
     * @param foregroundColor foreground color
     */
    void setForegroundColor(Color foregroundColor);

    /**
     * Sets highlighted color of order line.
     *
     * @param highlightedColor highlighted color
     */
    void setHighlightedColor(Color highlightedColor);

    /**
     * Sets additional info to display in order line handler.
     *
     * @param info additional info
     */
	void setInfo(String... info);

}
