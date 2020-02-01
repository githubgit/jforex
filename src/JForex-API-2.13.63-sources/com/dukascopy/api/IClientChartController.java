/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

/**
 * Allows control over the chart objects and mouse listeners
 * <b>NOTE: </b>Used only by <b>Standalone JForex API</b>.
 * @see IChart
 * @see IClientGUI 
 * @see IClientChartPresentationManager
 * @author aburenin
 *
 */
public interface IClientChartController {
	
	/**
	 * Displays the "Add Indicator" dialog.  
	 * 
	 */
	void addIndicators();
	
	/**
	 * The Price Marker activation allows to manually draw price marker lines on the chart.      
	 * 
	 */
	void activatePriceMarker();
	
	/**
	 * The Time Marker activation allows to manually draw Time Marker line on the chart.
	 * 
	 */
	void activateTimeMarker();
	
	/**
	 * The Percent Lines activation allows to manually draw Percent Lines on the chart.
	 * 
	 */
	void activatePercentLines();
	
	/**
	 * The Channel Lines activation allows to manually draw Channel Lines on the chart.
	 * 
	 */
	void activateChannelLines();
	
	/**
	 * The Poly Line activation allows to manually draw Poly Line on the chart.
	 * 
	 */
	void activatePolyLine();
	
	/**
     * The Short Line activation allows to manually draw Short Line on the chart.
     * 
     */
    void activateShortLine();
    
    /**
     * The Long Line activation allows to manually draw Long Line on the chart.
     * 
     */
    void activateLongLine();
    
    /**
     * The Ray Line activation allows to manually draw Ray Line on the chart.
     * 
     */
    void activateRayLine();
	
    /**
     * The Horizontal Line activation allows to manually draw Horizontal Line on the chart.
     * 
     */
    void activateHorizontalLine();
    
    /**
     * The Vertical Line activation allows to manually draw Vertical Line on the chart.
     * 
     */
    void activateVerticalLine();
    
    /**
     * The Text mode activation allows to add Text on the chart.
     * 
     */
    void activateTextMode();
    
	/**
	 * Adds OHLC Informer on the chart
	 * 
	 */
	void addOHLCInformer();
	
	/**
	 * Set the cruciform cursor pointer visibility on the chart
	 * @param show <code>true</code> cursor pointer visible, <code>false</code> otherwise
	 */
	void setCursorPointer(boolean show);
	
	/**
	 * Adds the specified mouse listener to receive mouse events from
     * this chart.
	 * @param ml the mouse listener
	 */
	void addMouseListener(MouseListener ml);
	
	/**
	 * Adds the specified mouse motion listener to receive mouse motion
     * events from chart.
	 * @param mml the mouse motion listener
	 */
	void addMouseMotionListener(MouseMotionListener mml);
	
	/**
	 * Adds the specified mouse wheel listener to receive mouse wheel events
     * from chart.
	 * @param mwl the mouse wheel listener
	 */
	void addMouseWheelListener(MouseWheelListener mwl);
	
	/**
	 * Removes the specified mouse listener so that it no longer
     * receives mouse events from this chart
	 * @param ml the mouse listener
	 */
	void removeMouseListener(MouseListener ml);
	
	/**
	 * Removes the specified mouse motion listener so that it no longer
     * receives mouse motion events from chart.
	 * @param mml the mouse motion listener
	 */
	void removeMouseMotionListener(MouseMotionListener mml);
	
	/**
	 * Removes the specified mouse wheel listener so that it no longer
     * receives mouse wheel events from chart.
	 * @param mwl the mouse wheel listener.
	 */
	void removeMouseWheelListener(MouseWheelListener mwl);
}
