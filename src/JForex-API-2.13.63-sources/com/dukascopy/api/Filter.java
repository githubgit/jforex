/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Different methods of flats filtering
 *
 * @author Dmitry Shohov
 */
public enum Filter {
    /**
     * Don't filter anything
     */
    NO_FILTER,

    /**
     * Filter flats at the weekends (21:00 or 22:00 Friday - 21:00 or 22:00 Sunday)
     */
    WEEKENDS,

    /**
     * Filter all flats
     */
    ALL_FLATS
}
