package com.dukascopy.api.chart.mouse;

import java.awt.event.MouseEvent;

public interface IChartPanelMouseEvent {

    /**
     * Returns chart time where mouse event occurred.
     *
     * @return time
     */
    long getTime();
    
    /**
     * Returns bar(candle) time where mouse event occurred.
     * On tick chart <code>getBarTime() == getTime()</code>
     *
     * @return time
     */
    long getBarTime();
    
    /**
     * Returns chart price where mouse event occurred.
     *
     * @return price
     */
    double getPrice();
    
    /**
     * Returns the source event
     *
     * @return source event
     */
    MouseEvent getSourceEvent();
}
