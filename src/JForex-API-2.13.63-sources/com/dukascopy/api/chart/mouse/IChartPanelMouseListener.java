/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.chart.mouse;

/**
 * The listener interface for receiving chart panel mouse events.
 * 
 * @author Aleksandrs.Leiferovs
 * @see ChartPanelMouseAdapter
 * @see IChartPanelMouseEvent
 */
public interface IChartPanelMouseListener {

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a chart panel.
     *
     * @param e chart panel mouse event
     */
    void mouseClicked(IChartPanelMouseEvent e);

    /**
     * Invoked when a mouse button has been pressed on a chart panel.
     *
     * @param e chart panel mouse event
     */
    void mousePressed(IChartPanelMouseEvent e);

    /**
     * Invoked when a mouse button has been released on a chart panel.
     *
     * @param e chart panel mouse event
     */
    void mouseReleased(IChartPanelMouseEvent e);

    /**
     * Invoked when the mouse enters a chart panel.
     *
     * @param e chart panel mouse event
     */
    void mouseEntered(IChartPanelMouseEvent e);

    /**
     * Invoked when the mouse exits a chart panel.
     *
     * @param e chart panel mouse event
     */
    void mouseExited(IChartPanelMouseEvent e);
    
    /**
     * Invoked when a mouse button is pressed on a chart panel and then
     * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
     * delivered to the chart panel where the drag originated until the
     * mouse button is released (regardless of whether the mouse position
     * is within the bounds of the component).
     *
     * @param e chart panel mouse event
     */
    void mouseDragged(IChartPanelMouseEvent e);

    /**
     * Invoked when the mouse cursor has been moved onto a chart panel
     * but no buttons have been pushed.
     *
     * @param e chart panel mouse event
     */
    void mouseMoved(IChartPanelMouseEvent e);
}
