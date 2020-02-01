package com.dukascopy.api.plugins.widget;

import javax.swing.JPanel;

/**
 * Represents a plugin widget
 */
public interface IPluginWidget {
	
	JPanel getContentPanel();
	
	void addPluginWidgetListener(PluginWidgetListener listener);
	
	void removePluginWidgetListener(PluginWidgetListener listener);
}