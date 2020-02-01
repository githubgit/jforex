package com.dukascopy.api.strategy.local;

import java.io.File;

import com.dukascopy.api.strategy.IStrategyDescriptor;

/**
 * Describes a local strategy
 */
public interface ILocalStrategyDescriptor extends IStrategyDescriptor{
	
	/**
	 * Returns a compiled strategy .jfx file if there exists any. 
	 * <p> In SDK, for instance, the strategies may get started without a file
	 * 
	 * @return a compiled strategy .jfx file if there exists any
	 */
	File getFile();
}