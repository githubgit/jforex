package com.dukascopy.api.plugins;

import com.dukascopy.api.IJFRunnable;
import com.dukascopy.api.JFException;

/**
 * Class that all plugins have to extend
 */
public abstract class Plugin implements IJFRunnable<IPluginContext> {
	
    /**
     * Called on plugin start
     * 
     * @param context allows access to all system functionality
     * @throws JFException when strategy author ignores exceptions 
     */
    @Override
    public void onStart(IPluginContext context) throws JFException {}
    
    /**
     * Called before plugin is stopped
     */
    @Override
    public void onStop() throws JFException {}
}
