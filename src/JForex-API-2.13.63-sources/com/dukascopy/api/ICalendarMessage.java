/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.List;

/**
 * Calendar message definition - recieved as result of subscription using {@link INewsFilter} with source : {@link INewsFilter.NewsSource#DJ_LIVE_CALENDAR}
 * 
 * @author Denis Larka
 */
public interface ICalendarMessage extends INewsMessage {

	/**
	 * Calendar message's detail
	 */
    interface Detail {
    	/**
    	 * Returns detail's actual value
    	 * 
    	 * @return actual value as {@link String}
    	 */
        String getActual();

        /**
         * Returns detail's previous value
         * 
         * @return previous value as {@link String}
         */
        String getPrevious();

        /**
         * Returns detail's expected value
         * 
         * @return expected value as {@link String}
         */
        String getExpected();

        /**
         * Returns delta between actual and previous values
         * 
         * @return delta as {@link String}
         */
        String getDelta();

        /**
         * Returns detail's description
         * 
         * @return description as {@link String} 
         */
        String getDescription();

        /**
         * Returns detail's ID
         * @return ID as {@link String}
         */
        String getId();
    }

    /**
     * Returns company URL
     * 
     * @return url as {@link String}
     */
    String getCompanyURL();

    /**
     * Returns event's country
     * 
     * @return country as {@link String}
     */
    String getCountry();

    /**
     * Returns event's details
     * 
     * @return details as {@link List} of {@link Detail}
     */
    List<Detail> getDetails();

    /**
     * Return event's code
     * 
     * @return code as {@link String}
     */
    String getEventCode();
    
    /**
     * Return event's category
     * 
     * @return category as {@link String}
     */
    String getEventCategory();

    /**
     * Returns event's date
     * 
     * @return date as long
     */
    long getEventDate();

    /**
     * Returns event's URL
     * 
     * @return url as {@link String} 
     */
    String getEventURL();

    /**
     * Returns event's ISIN
     * 
     * @return ISIN as {@link String}
     */
    String getISIN();

    /**
     * Returns event's organisation
     * 
     * @return organisation as {@link String}
     */
    String getOrganisation();

    /**
     * Returns event's period
     * 
     * @return period as {@link String}
     */
    String getPeriod();

    /**
     * Return event's ticker
     * 
     * @return ticker as {@link String}
     */
    String getTicker();

    /**
     * Returns event's venue
     * 
     * @return venue as {@link String}
     */
    String getVenue();

    /**
     * Returns event's confirmation state
     * 
     * @return true if event is confirmed
     */
    boolean isConfirmed();
}