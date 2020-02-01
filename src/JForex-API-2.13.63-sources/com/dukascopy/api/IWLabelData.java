/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.awt.image.BufferedImage;

/**
 * Contains the information about <a href="http://www.dukascopy.com/swiss/english/ia/WL/white-label/">White Label service</a>
 * 
 * @see IDataService#getWhiteLabelData()
 * @author aburenin
 *
 */
public interface IWLabelData {
    
    /**
     * @return partner short name
     */
    String getShortName();
    
    /**
     * @return partner long name
     */
    String getLongName();
    
    /**
     * @return partner url
     */
    String getUrl();
    
    /**
     * @return partner skype id
     */
    String getSkype();
    
    /**
     * @return partner phone
     */
    String getPhone();
    
    /**
     * @return partner icon
     */
    BufferedImage getIcon();

    /**
     * @return partner logo
     */
    BufferedImage getLogo();
    
    /**
     * @return partner splash
     */
    BufferedImage getSplash();
}
