/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

public interface IChannelChartObject extends ILeveledChartObject {
	
    boolean isFixedSides();
    void setFixedSides(boolean fixed);
}
