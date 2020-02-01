package com.dukascopy.api.feed;

/**
 * Method of P&amp;F calculation.
 */
public enum CalculationMethod {
    /**
     * Only Close price of time session bar is used.
     */
    CLOSE,
    /**
     * High or Low price of time session bar is used.
     */
    HIGH_LOW
}
