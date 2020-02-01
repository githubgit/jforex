package com.dukascopy.api.plugins;

import java.util.Set;

import com.dukascopy.api.IContext;
import com.dukascopy.api.plugins.menu.IPluginMenu;
import com.dukascopy.api.plugins.ui.UIFactory;
import com.dukascopy.api.plugins.widget.IPluginWidget;
import com.dukascopy.api.plugins.widget.WidgetProperties;
import com.dukascopy.api.strategy.local.ILocalStrategyManager;
import com.dukascopy.api.strategy.remote.IRemoteStrategyManager;

/**
 * Provides the access to system interfaces
 */
public interface IPluginContext extends IContext {
	
	/**
	 * Add a widget
	 * 
	 * @param name widget tab name
	 * @return the widget object
	 */
	IPluginWidget addWidget(String name);
	
	/**
	 * Add a widget with certain visual properties. 
	 * Available with JForex-API 3.0.
	 * 
	 * @param name widget tab name
	 * @param widgetProperties visual properties
	 * @return the widget object
	 */
	IPluginWidget addWidget(String name, WidgetProperties widgetProperties);
	
	/**
	 * Removes a widget
	 * 
	 * @param pluginWidget widget to remove
	 */
	void removeWidget(IPluginWidget pluginWidget);
	
	/**
	 * Returns all widgets
	 * 
	 * @return all widgets
	 */
	Set<IPluginWidget> getWidgets();
	
	/**
	 * Add a Menu
	 * 
	 * @param name Menu tab name
	 * @return the Menu object
	 */
	IPluginMenu addMenu(String name);	
	
	/**
	 * Removes a Menu
	 * 
	 * @param menu menu to remove
	 */
	void removeMenu(IPluginMenu menu);
	
	/**
	 * Returns all Menus
	 * 
	 * @return all Menus
	 */
	Set<IPluginMenu> getMenus();
	
	/**
	 * Returns true if the plugin is allowed to make trading operations (i.e. submit, modify, merge, close orders)
	 * 
	 * @return true if the plugin is allowed to make trading operations (i.e. submit, modify, merge, close orders)
	 */
	boolean isAutoTradingAllowed();
	
	/**
	 * Subscribes to order-change and other platform messages
	 * 
	 * @param messageListener the message listener
	 */
	void subscribeToMessages(IMessageListener messageListener);
	
	/**
	 * Returns the remote strategy manager
	 * 
	 * @return the remote strategy manager
	 */
	IRemoteStrategyManager getRemoteStrategyManager();
	
	/**
	 * Returns the local strategy manager
	 * 
	 * @return the local strategy manager
	 */
	ILocalStrategyManager getLocalStrategyManager();

    /**
     * Returns the factory for widget UI objects
     *
     * @return the factory for widget UI objects
     */
	UIFactory getUIFactory();

}
