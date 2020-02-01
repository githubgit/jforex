package com.dukascopy.api.drawings;

public interface IFiboTimeZonesPercentsChartObject extends ILeveledChartObject {

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
