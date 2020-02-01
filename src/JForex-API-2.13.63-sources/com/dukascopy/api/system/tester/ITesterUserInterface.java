/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system.tester;

import java.util.Map;
import com.dukascopy.api.IChart;

/**
 * Provides access to various strategy testing GUI parts
 */
public interface ITesterUserInterface {

    /**
	 * Maps the IChart interface to a corresponding chart panel
	 *  
	 * @param chartPanels - Map that contains IChart and the corresponding chart panel    
	 */
	void setChartPanels(Map<IChart, ITesterGui> chartPanels);
}
