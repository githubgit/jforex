/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Set;

/**
 * News message definition - received as result of subscription using {@link INewsFilter}
 * 
 * @author Denis Larka
 */
public interface INewsMessage extends IMessage {
    
	/**
	 * Actions
	 */
    enum Action{
    	/**
    	 * Means that news need to be added to existing news list
    	 */
    	INSERT,
    	/**
    	 * Means that news need to be deleted from news list using ID - all others fields is empty
    	 */
    	DELETE
	}
    
    /**
     * Returns action
     * 
     * @return action as {@link Action}
     */
    Action getAction();

    /**
     * Returns copyright
     * 
     * @return copyright as {@link String}
     */
    String getCopyright();

    /**
     * Returns header
     * 
     * @return header as {@link String}
     */
    String getHeader();

    /**
     * Returns ID
     * 
     * @return ID as {@link String}
     */    
    String getId();
    
    /**
     * Returns publish date
     * 
     * @return publish date as long
     */    
    long getPublishDate();
    
    /**
     * @return true if this news is end of story
     */    
    boolean isEndOfStory();
    
    /**
     * @return true if this news is "hot"
     */    
    boolean isHot();

    /**
     * Returns ISO 4217 currency codes
     *
     * @return currencies as {@link Set} of {@link String} - can be interpreted using {@link INewsFilter.Currency}'s valueOf()
     * method (only limited amount of currencies) or {@link com.dukascopy.api.JFCurrency#getInstance(String)} (preferred)
     */
    Set<String> getCurrencies();

    /**
     * Returns geo regions
     * 
     * @return geo regions as {@link Set} of {@link String} - can be interpreted using {@link INewsFilter.Country}'s valueOf() method
     */
    Set<String> getGeoRegions();
    
    /**
     * Returns market sectors
     * 
     * @return market sectors as {@link Set} of {@link String} - can be interpreted using {@link INewsFilter.MarketSector}'s valueOf() method
     */
    Set<String> getMarketSectors();
    
    /**
     * Returns stock indices
     * 
     * @return stock indices as {@link Set} of {@link String} - can be interpreted using {@link INewsFilter.StockIndex}'s valueOf() method
     */
    Set<String> getStockIndicies();

    /**
     * Returns time when message was published
     *
     * @return time when message was created
     */
    @Override
    long getCreationTime();
}