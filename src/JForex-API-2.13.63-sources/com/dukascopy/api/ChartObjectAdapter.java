/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Implementation of listener with empty methods
 *
 * @author Dmitry Shohov
 */
public class ChartObjectAdapter implements ChartObjectListener{
    @Override
    public void highlighted(ChartObjectEvent e) {
    }

    @Override
    public void highlightingRemoved(ChartObjectEvent e) {
    }

    @Override
    public void selected(ChartObjectEvent e) {
    }

    @Override
    public void deselected(ChartObjectEvent e) {
    }

    @Override
    public void moved(ChartObjectEvent e) {
    }

    @Override
    public void deleted(ChartObjectEvent e) {
    }

    @Override
    public void attrChanged(ChartObjectEvent e) {
    }
}
