package com.dukascopy.api;

import java.util.Currency;

public interface ICurrency {
	
    /**
     * Gets the currency code of this currency.
     *
     * @return the currency code of this currency.
     */
	String getCurrencyCode();
	
    /**
     * Gets the symbol of this currency if there is one.
     * 
     * @return the symbol of this currency if there is one
     */
	String getSymbol();
	
	/**
     * Gets the {@link Currency java currency} instance
     *
     * @return the {@link Currency java currency} instance. Can be null if this currency is not ISO 4217 one
     */
	Currency getJavaCurrency();
}
