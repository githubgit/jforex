/**
 * Copyright 2012 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Currency;

/**
 * Straightforward money bean (double value along with {@link ICurrency}).<br>
 * Intended to be used in expressing monetary amounts and prices.
 * @author andrej.burenin
 */
public final class Money {
	private double amount;
	private ICurrency currency;

	/**
	 * @param amount the amount
	 * @param currency the currency
	 * @deprecated Use {@link #Money(double, ICurrency)} instead
	 */
    @Deprecated
	public Money(double amount, Currency currency){
		this(amount, JFCurrency.getInstance(currency.getCurrencyCode()));
	}
	
	/**
	 * @param amount the amount
	 * @param currency the currency
	 */
	public Money(double amount, ICurrency currency){
		this.amount = amount;
		this.currency = currency;
	}
	
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the currency
	 * @deprecated Use {@link #getJFCurrency()} instead
	 */
    @Deprecated
	public Currency getCurrency() {
		return currency.getJavaCurrency();
	}
	
	/**
	 * @param currency the currency to set
	 * @deprecated Use {@link #setJFCurrency(ICurrency)} instead
	 */
    @Deprecated
	public void setCurrency(Currency currency) {
		this.currency = JFCurrency.getInstance(currency.getCurrencyCode());
	}

	/**
	 * @return the currency
	 */
	public ICurrency getJFCurrency() {
		return currency;
	}
	
	/**
	 * @param currency the currency to set
	 */
	public void setJFCurrency(ICurrency currency) {
		this.currency = currency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((currency == null) ? 0 : currency.getCurrencyCode().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (!(obj instanceof Money)) {
            return false;
        }
		Money other = (Money) obj;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
		if (currency == null) {
			if (other.currency != null) {
                return false;
            }
		} else if (!currency.getCurrencyCode().equals(other.currency.getCurrencyCode())) {
            return false;
        }
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("%.2f %s", getAmount(), getJFCurrency()); 
	}
}
