package com.dukascopy.api.drawings;

public interface IFiboChannelChartObject extends ILeveledChartObject {
	
    boolean isFixedSides();
    void setFixedSides(boolean fixed);
}
