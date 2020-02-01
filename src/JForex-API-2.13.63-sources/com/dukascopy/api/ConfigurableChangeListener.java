package com.dukascopy.api;

import java.util.Map;

public interface ConfigurableChangeListener {

	/**
	 * The map contains the user-selected parameter values.
	 * In order to reject the parameter changes a <code>JFException</code> must be thrown.
	 * 
	 * @param newValueMap contains the user-selected parameter values
     * @throws JFException if parameter changes should be rejected
	 */
	void onConfigurableChange(Map<Configurable, Object> newValueMap) throws JFException;
}
