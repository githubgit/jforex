package com.dukascopy.api.drawings;

import com.dukascopy.api.IChartObject;

/**
 * Label for displaying user's info which attached to 1 of 4 corners on chart.
 * 
 * @author Aleksandrs.Leiferovs
 */
public interface IScreenLabelChartObject extends IChartObject {
    
    /**
     * String constant to be used in PropertyChangeListener.
     */
    static final String PROPERTY_CORNER = "corner";
    /**
     * String constant to be used in PropertyChangeListener.
     */
    static final String PROPERTY_X_DISTANCE = "x.distance";
    /**
     * String constant to be used in PropertyChangeListener.
     */
    static final String PROPERTY_Y_DISTANCE = "y.distance";
    
    
    public enum Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    /**
     * Returns corner which label is attached to.
     * @return current corner
     */
    Corner getCorner();

    /**
     * Attaches label to new corner.
     * @param corner new corner
     */
    void setCorner(Corner corner);
    
    /**
     * Returns gap between specified chart corner and label by X axis.
     * @return xDistance in pixels
     */
    int getxDistance();

    /**
     * Sets gap between specified chart corner and label by X axis.
     * @param xDistance in pixels
     */
    void setxDistance(int xDistance);
    
    /**
     * Returns gap between specified chart corner and label by Y axis.
     * @return yDistance in pixels
     */
    int getyDistance();

    /**
     * Sets gap between specified chart corner and label by X axis.
     * @param yDistance in pixels
     */
    void setyDistance(int yDistance);
}
