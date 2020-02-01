/*
 * Copyright 2010 Dukascopy® (Suisse) Bank. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The class indicates the amount of price movement required to shift a chart to the right.
 * This condition is used on charts that only take into consideration price movement instead of both price and time.
 * Is used for Point and Figures charts for example. 
 * 
 * @author marks.vilkelis
 */
public final class ReversalAmount implements Comparable<ReversalAmount>, Serializable {
	
	private final int amount;

	public static final ReversalAmount ONE;
	public static final ReversalAmount TWO;
	public static final ReversalAmount THREE;
	public static final ReversalAmount FOUR;
	public static final ReversalAmount FIVE;

	public static int MAXIMAL_REVERSAL_AMOUNT = 50;
	
	private static final List<ReversalAmount> JFOREX_REVERSAL_AMOUNTS;
	
	static {
		ONE = new ReversalAmount(1);
		TWO = new ReversalAmount(2);
		THREE = new ReversalAmount(3);
		FOUR = new ReversalAmount(4);
		FIVE = new ReversalAmount(5);
		
		JFOREX_REVERSAL_AMOUNTS = Collections.unmodifiableList(createJForexPriceRanges());
	}
	
	private ReversalAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * The method returns appropriate ReversalAmount for the passed String
	 * 
	 * @param str String representation
	 * @return Reversal Amount
	 */
	public static ReversalAmount valueOf(String str) {
		if (str == null) {
			return null;
		}
		
		try {
			int amount = Integer.parseInt(str);
			return valueOf(amount);
 		} catch (Throwable e) {
 			return null;
 		}
	}
	
	/**
	 * The method returns appropriate Reversal Amount for the passed amount
	 * 
	 * @param amount int to parse to ReversalAmount
	 * @return Reversal Amount
	 */
	public static ReversalAmount valueOf(int amount) {
		for (ReversalAmount ra : JFOREX_REVERSAL_AMOUNTS) {
			if (ra.getAmount() == amount) {
				return ra;
			}
		}
		return null;
	}

	/**
	 * Method creates and returns all available to JForex Reversal Amounts 
	 * 
	 * @return the list of Reversal Amounts
	 */
	public static List<ReversalAmount> createJForexPriceRanges() {
		List<ReversalAmount> result = new ArrayList<>();
		
		result.add(ONE);
		result.add(TWO);
		result.add(THREE);
		result.add(FOUR);
		result.add(FIVE);
		
		for (int i = 6; i <= MAXIMAL_REVERSAL_AMOUNT; i ++) {
			result.add(new ReversalAmount(i));
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return String.valueOf(getAmount());
	}

	public int getAmount() {
		return amount;
	}
	
	/**
	 * The method compares passed ReversalAmount with the current one
     * <p>
	 * if (ReversalAmount == null) or (ReversalAmount.getAmount() &lt; this.getAmount()) Returns 1<br>
	 * if ReversalAmount.getAmount() &gt; this.getAmount() Returns -1<br>
	 * Otherwise Returns 0
	 * 
	 * @param reversalAmount ReversalAmount object to compare
	 * @return comparison result
	 */
	@Override
	public int compareTo(ReversalAmount reversalAmount) {
		if (reversalAmount == null) {
			return 1;
		} else if (getAmount() < reversalAmount.getAmount()) {
			return -1;
		} else if (getAmount() > reversalAmount.getAmount()) {
			return 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
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
		ReversalAmount other = (ReversalAmount) obj;
		if (amount != other.amount) {
			return false;
		}
		return true;
	}
}
