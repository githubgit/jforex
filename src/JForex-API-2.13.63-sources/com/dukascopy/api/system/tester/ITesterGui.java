/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system.tester;

import javax.swing.JPanel;

/**
 * Provides access to various GUI parts
 */
public interface ITesterGui {

    /**
	 * @return the corresponding chart panel
	 */
	JPanel getChartPanel();
	
	/**
	 * @return the corresponding chart controller
	 */
	ITesterChartController getTesterChartController();
}
