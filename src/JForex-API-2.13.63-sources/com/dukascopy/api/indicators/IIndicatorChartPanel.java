package com.dukascopy.api.indicators;

import java.util.List;

import com.dukascopy.api.IChartObject;
import com.dukascopy.api.drawings.IChartObjectFactory;

/**
 * A container for graphical objects created by the indicator.
 * 
 * @author anatoly.pokusayev
 */
public interface IIndicatorChartPanel {
	
	/**
     * Adds the object of type <code>IChartObject</code> to the current panel. 
     * The object is locked by default. 
     * Note that only <b>one</b> object instance can be added per chart.
     * 
     * @param chartObject - instance of <code>IChartObject</code> super type.
     * 
     * @throws IllegalArgumentException - if <code>object</code> is already present on chart.
     */
	void add(IChartObject chartObject);
	
	/**
     * Removes the specified graphical object from this panel.
     * 
     * @param chartObject - graphical object to remove.
     */
	void remove(IChartObject chartObject);
	
	/**
	 * Removes all graphical objects created by the indicator.
	 * 
	 * @return the list of graphical objects to be removed.
	 */
	List<IChartObject> removeAll();
	
	 /**
     * Returns the instance of <code>IChartObjectFactory</code>. 
     * 
     * @return instance of <code>IChartObjectFactory</code>.
     */
	IChartObjectFactory getChartObjectFactory();
	
	 /**
     * Returns the instance of <code>IChartObject</code> created by the indicator by key.
     * 
     * @param chartObjectKey the unique chart object key.
     * 
     * @return instance of <code>IChartObject</code> or null if the object with the specified key was not found or the object was not created by the indicator.
     */
	IChartObject get(String chartObjectKey);
	
	 /**
     * Returns all graphical objects created by the indicator.
     * 
     * @return list of all graphical objects created by the indicator.
     */
	List<IChartObject> getAll();
	
	/**
	 * Returns the minimum time scale value.
	 * 
	 * @return minimum time scale value
	 * @deprecated
	 */
	@Deprecated
	long getMinTime();
	
	/**
	 * Returns the maximum time scale value.
	 * 
	 * @return maximum time scale value
	 * @deprecated
	 */
	@Deprecated
	long getMaxTime();
	
	/**
     * Returns the minimum value of the price scale.
     * 
     * @return minimum value of the price scale.
	 * @deprecated
	 */
	@Deprecated
	double getMinPrice();
	
	/**
     * Returns the maximum value of the price scale.
     * 
     * @return maximum value of the price scale.
	 * @deprecated
	 */
	@Deprecated
	double getMaxPrice();
}