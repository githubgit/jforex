package com.dukascopy.api.plugins.widget;

/**
 * Listener for user actions on the widget, for more widget actions, 
 * add a listener to the IPluginWidget.getContentPanel.
 */
public abstract class PluginWidgetListener {

	/**
	 * executes on widget close
	 */
	public void onWidgetClose() {}
}
