/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * All notifications are called in strategy thread
 *
 * @author Dmitry Shohov
 */
public interface ChartObjectListener {
    /**
     * Called when user places mouse cursor over the drawing.
     * Cannot be canceled.
     *
     * @param e event
     */
    void highlighted(ChartObjectEvent e);

    /**
     * Called when user moves mouse cursor out of the drawing area
     * Cannot be canceled
     *
     * @param e event
     */
    void highlightingRemoved(ChartObjectEvent e);

    /**
     * Called when user selects the drawing
     *
     * @param e event
     */
    void selected(ChartObjectEvent e);

    /**
     * Called when user deselects the drawing
     * Cannot be canceled.
     *
     * @param e event
     */
    void deselected(ChartObjectEvent e);

    /**
     * Called when user moves the drawing. Movement of the drawing doesn't rise attrChanged events. Event contains old
     * and new coordinates of the first point of the drawing
     *
     * @param e event
     */
    void moved(ChartObjectEvent e);

    /**
     * Called when user deletes the drawing
     *
     * @param e event
     */
    void deleted(ChartObjectEvent e);

    /**
     * Called when user changes attribute of the drawing. Event contains type, name, old and new value of the attribute
     * @deprecated use <code>PropertyChangeListener</code> working with <code>ChartObject</code> 
     * @param e event
     */
    void attrChanged(ChartObjectEvent e);
}
