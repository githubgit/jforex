package com.dukascopy.api.strategy;

import java.util.List;
import java.util.UUID;

/**
 * Describes a strategy
 */
public interface IStrategyDescriptor {
		
	/**
	 * Returns strategy class name
	 * 
	 * @return strategy class name
	 */
	String getName();
	
	/**
	 * Returns strategy start time on the remote server
	 * 
	 * @return strategy start time on the remote server
	 */
	long getStartTime();
	
	/**
	 * Returns strategy parameters
	 * 
	 * @return strategy parameters
	 */
	List<IStrategyParameter> getParameters();

	/**
	 * Returns remote strategy id
	 * 
	 * @return remote strategy id
	 */
	UUID getId();
	
	/**
	 * Returns the message (if any) about the last strategy state change
	 * 
	 * @return the message about the last strategy state change
	 */
	String getChangeReason();
}
