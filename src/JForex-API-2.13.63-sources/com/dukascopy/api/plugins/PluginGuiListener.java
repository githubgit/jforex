package com.dukascopy.api.plugins;

import com.dukascopy.api.plugins.menu.IPluginMenu;
import com.dukascopy.api.plugins.widget.IPluginWidget;
import com.dukascopy.api.plugins.widget.PluginWidgetListener;

/**
 * Listener for plugin actions to be used from JForex-SDK,
 * where the API user has to:
 * <ul>
 * 	<li>provide the widget and menu item container of his own,</li>
 * 	<li>handle listener adding/removal.</li>
 * </ul>
 */
public abstract class PluginGuiListener {

	/**
	 * Handle here widget adding
     * @param pluginWidget plugin widget
	 */
	public void onWidgetAdd(IPluginWidget pluginWidget) {}

	/**
	 * Handle here widget removal
     * @param pluginWidget plugin widget
	 */
	public void onWidgetRemove(IPluginWidget pluginWidget) {}
	
	/**
	 * Handle here Menu adding
     * @param pluginMenu plugin menu
	 */
	public void onMenuAdd(IPluginMenu pluginMenu) {}

	/**
	 * Handle here Menu removal
     * @param pluginMenu plugin menu
	 */
	public void onMenuRemove(IPluginMenu pluginMenu) {}
	
	/**
	 * Handle here widget listener adding
     * @param listener widget listener
	 */
	public void onWidgetListenerAdd(PluginWidgetListener listener) {}
	
	/**
	 * Handle here widget listener removal
     * @param listener widget listener
	 */
	public void onWidgetListenerRemove(PluginWidgetListener listener) {}
}
