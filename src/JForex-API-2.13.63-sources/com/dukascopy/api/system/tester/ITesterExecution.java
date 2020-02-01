/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system.tester;

/**
 * Provides control over a strategy testing process
 */
public interface ITesterExecution {

    /**
	 * Sets ITesterExecutionControl of strategy execution process
	 * 
	 * @param executionControl allows to control a strategy execution process(stop, continue, cancel)
	 */
	void setExecutionControl(ITesterExecutionControl executionControl);
}
