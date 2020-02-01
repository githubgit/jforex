package com.dukascopy.api;

import java.util.Currency;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JFCurrency implements ICurrency {

	private final Currency javaCurrency;
	private final String currencyCode;
	
	private static Map<String, ICurrency> nonJavaCurrencies = new ConcurrentHashMap<>();
	private static Map<Currency, ICurrency> javaCurrencies = new ConcurrentHashMap<>();
	
	public static ICurrency getInstance(String currencyCode) {
		if (nonJavaCurrencies.keySet().contains(currencyCode)) {
			return nonJavaCurrencies.get(currencyCode);
		}
		
		try {
			Currency currency = Currency.getInstance(currencyCode);
			if (javaCurrencies.keySet().contains(currency)) {
				return javaCurrencies.get(currency);
			}
			ICurrency jfCurrency = new JFCurrency(currency);
			javaCurrencies.put(currency, jfCurrency);
			return jfCurrency;
		} catch (IllegalArgumentException e) {
			// specified currency code is not ISO 4217 one
		}
		ICurrency jfCurrency = new JFCurrency(currencyCode);
		nonJavaCurrencies.put(currencyCode, jfCurrency);
		return jfCurrency;
	}

	private JFCurrency(String currencyCode) {
		this.currencyCode = currencyCode;	
		this.javaCurrency = null;		
	}

	private JFCurrency(Currency currency) {
		this.currencyCode = currency.getCurrencyCode();
		this.javaCurrency = currency;		
	}

	@Override
	public String getCurrencyCode() {
		if (javaCurrency != null) {
			return javaCurrency.getCurrencyCode();
		}
		return currencyCode;
	}
	
	@Override
	public String getSymbol(){
		if (javaCurrency != null) {
			return javaCurrency.getSymbol();
		}
		return "";
	}
	
	@Override
	public Currency getJavaCurrency() {
		return javaCurrency;
	}

	@Override
	public String toString() {
		if (javaCurrency != null) {
			return javaCurrency.toString();
		}
		return currencyCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
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
		if (getClass() != obj.getClass()) {
            return false;
        }
		JFCurrency other = (JFCurrency) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null) {
                return false;
            }
		} else if (!currencyCode.equals(other.currencyCode)) {
            return false;
        }
		return true;
	}
}
