package com.dukascopy.api.drawings;

import javax.swing.JPanel;

public interface ICustomWidgetChartObject extends IWidgetChartObject {
	
	/**
	 * Returns the content panel of the chart widget. 
	 * 
	 * @return the content panel of the chart widget
	 */
	JPanel getContentPanel();
	
    /**
     * Sets the header of the widget
     * 
     * @param text header of the widget
     * @deprecated
     */
	@Deprecated
    void setText(String text);
}
