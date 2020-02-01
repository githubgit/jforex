package com.dukascopy.api.strategy;

/**
 * Represents a strategy parameter
 */
public interface IStrategyParameter {
	
	/**
	 * Returns parameter name
	 * 
	 * @return parameter name
	 */
	String getName();
	
	/**
	 * Returns parameter value
	 * 
	 * @return parameter value
	 */
	Object getValue();
	
	/**
	 * Returns parameter class
	 * 
	 * @return parameter class
	 */
	Class<?> getType();
}
