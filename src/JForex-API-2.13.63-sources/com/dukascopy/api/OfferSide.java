/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

/**
 * Defines offer side
 * 
 * @author Dmitry Shohov
 */
public enum OfferSide {
    /**
     * BID side
     */
    BID("Bid"),
    /**
     * ASK side
     */
    ASK("Ask");
    
    private String text;
    
    private OfferSide(String text) {
        this.text = text;
    }
    
    @Override
    public String toString() {
        return text;
    }

    public static OfferSide fromString(String stringValue) {
        OfferSide offerSide = null;
        try {
            offerSide = valueOf(stringValue);
        } catch(NullPointerException e) {
        }
        return offerSide;
    }

}
