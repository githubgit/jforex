/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.indicators;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Point;
import java.awt.Stroke;
import java.util.List;
import java.util.Map;

/**
 * Defines method that will be called for outputs with drawnByIndicator attribute set to true
 *
 * @author Dmitry Shohov
 */
public interface IDrawingIndicator {
    /**
     * This method will be called once for every output marked as drawnByIndicator every time the chart surface needs to be redrawn
     *
     * @param g graphical context
     * @param outputIdx index of the output parameter
     * @param values array of values (int[], double[] or Object[] depending of the type of the output). Array may be empty or null if data is not yet available.
     * @param color color selected when the indicator was added
     * @param stroke stroke selected when the indicator was added
     * @param indicatorDrawingSupport provides information about candle positions, indexes, etc
     * @param shapes graphical shapes can be added to this list. This makes it possible to show correct popup when user right-clicks one of the shapes in this list on chart
     * @param handles coordinate points with their corresponding colors can be added to this list
     * @return X-Y coordinates of last point drawn. This point will be used when drawing last value marker on chart. The marker will not be
     * drawn if <code>null</code> is returned.
     */
    Point drawOutput(Graphics g, int outputIdx, Object values, Color color, Stroke stroke,
                           IIndicatorDrawingSupport indicatorDrawingSupport, java.util.List<Shape> shapes,
                           Map<Color, List<Point>> handles);
}
