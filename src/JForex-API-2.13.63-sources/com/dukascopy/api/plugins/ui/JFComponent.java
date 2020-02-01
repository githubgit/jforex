package com.dukascopy.api.plugins.ui;

import java.awt.Component;
import java.awt.Dimension;

/**
 * Parent to all custom UI objects, that wrap a standard java swing component
 *
 * @param <T> type of wrapped java swing component
 */
public interface JFComponent<T extends Component> {
	
	/**
	 * Returns the wrapped standard java swing component
	 * 
	 * @return the wrapped standard java swing component
	 */
	T getComponent();

    /**
     * Sets preferred/minimum/maximum sizes of wrapped component.
     *
     * @param size component size
     */
    void setAllSizes(Dimension size);

}
