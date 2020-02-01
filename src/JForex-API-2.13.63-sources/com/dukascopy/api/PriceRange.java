/**
 * The file PriceRange.java was created on Mar 26, 2010 at 3:51:10 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The class is used for Range Bar and Point&amp;Figures  price interval in pips identification
 * 
 * @author Mark Vilkel, Janis Garsils
 */
public final class PriceRange implements Comparable<PriceRange>, Serializable {
	
	private String name;
	private int pipCount;
	
	public static final PriceRange ONE_PIP;
	public static final PriceRange TWO_PIPS;
	public static final PriceRange THREE_PIPS;
	public static final PriceRange FOUR_PIPS;
	public static final PriceRange FIVE_PIPS;
	public static final PriceRange SIX_PIPS;
	
	public static int MAXIMAL_PIP_COUNT = 10000;
	
	static {
		
		ONE_PIP = createPriceRange(1, "ONE_PIP");
		TWO_PIPS = createPriceRange(2, "TWO_PIPS");
		THREE_PIPS = createPriceRange(3, "THREE_PIPS");
		FOUR_PIPS = createPriceRange(4, "FOUR_PIPS");
		FIVE_PIPS = createPriceRange(5, "FIVE_PIPS");
		SIX_PIPS = createPriceRange(6, "SIX_PIPS");
	}
	
	private static PriceRange createPriceRange(int pipValue){
		return createPriceRange(pipValue, pipValue + "_PIPS");
	}
	
	private static PriceRange createPriceRange(int pipValue, String name){

		if (pipValue > MAXIMAL_PIP_COUNT || pipValue < 1){
			return null;
		}
		
		PriceRange priceRange = new PriceRange(name, pipValue);
		return priceRange;
	}
	
	private PriceRange(String name, int pipCount) {
		this.name = name;
		this.pipCount = pipCount;
	}

	/**
	 * Method returns the price range name
	 * 
	 * @return the name of price range
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method returns pip count of price range
	 * 
	 * @return pip count
	 */
	public int getPipCount() {
		return pipCount;
	}
	
	/**
	 * The method returns appropriate PriceRange for the passed String
	 * 
	 * @param str String representation
	 * @return price range
	 */
	public static PriceRange valueOf(String str) {
		if (str == null) {
			return null;
		}
		
		if (ONE_PIP.getName().toLowerCase().startsWith(str.toLowerCase())){
			return ONE_PIP;
		}
		else if (TWO_PIPS.getName().toLowerCase().startsWith(str.toLowerCase())){
			return TWO_PIPS;
		}
		else if (THREE_PIPS.getName().toLowerCase().startsWith(str.toLowerCase())){
			return THREE_PIPS;
		}
		else if (FOUR_PIPS.getName().toLowerCase().startsWith(str.toLowerCase())){
			return FOUR_PIPS;
		}
		else if (FIVE_PIPS.getName().toLowerCase().startsWith(str.toLowerCase())){
			return FIVE_PIPS;
		}
		else if (SIX_PIPS.getName().toLowerCase().startsWith(str.toLowerCase())){
			return SIX_PIPS;
		}
		
		String[] split = str.split("_");
		
		if (split.length > 0){
			try {
				int pipCount = Integer.parseInt(split[0]);				
				PriceRange range = valueOf(pipCount);
				if (range != null && range.getName().toLowerCase().startsWith(str.toLowerCase())){
					return range;
				}
				else if (pipCount <= 6){
					int nextPips = pipCount * 10;
					range = valueOf(nextPips);
					if (range != null && range.getName().toLowerCase().startsWith(str.toLowerCase())){
						return range;
					}
				}
			} catch (Throwable t){
				return null;
			}
		}

		return null;
	}
	
	/**
	 * The method returns appropriate PriceRange for the passed pip count
	 * 
	 * @param pipCount pip count value
	 * @return price range
	 */
	public static PriceRange valueOf(int pipCount) {
		
		if (pipCount <= 6){
			if (pipCount == 1){
				return PriceRange.ONE_PIP;
			}
			else if (pipCount == 2){
				return PriceRange.TWO_PIPS;
			}
			else if (pipCount == 3){
				return PriceRange.THREE_PIPS;
			}
			else if (pipCount == 4){
				return PriceRange.FOUR_PIPS;
			}
			else if (pipCount == 5){
				return PriceRange.FIVE_PIPS;
			}
			else if (pipCount == 6){
				return PriceRange.SIX_PIPS;
			}
		}
		
		return createPriceRange(pipCount);
	}

	/**
	 * Method creates and returns first 1000 Price Ranges. 
	 * 
	 * @return the list of price ranges
	 */
	public static List<PriceRange> createJForexPriceRanges() {
		List<PriceRange> result = new ArrayList<>();
		
		result.add(ONE_PIP);
		result.add(TWO_PIPS);
		result.add(THREE_PIPS);
		result.add(FOUR_PIPS);
		result.add(FIVE_PIPS);
		result.add(SIX_PIPS);
		
		for (int i = 7; i < 1000 + 1; i++) {
			PriceRange pr = new PriceRange(i + "_PIPS", i);
			result.add(pr);
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * The method compares passed PriceRange with the current one
     * <p>
	 * if (PriceRange == null) or (PriceRange.getPipCount() &lt; this.getPipCount()) Returns 1<br>
	 * if PriceRange.getPipCount() &gt; this.getPipCount() Returns -1<br>
	 * Otherwise Returns 0
	 * 
	 * @param priceRange PriceRange object to compare
	 * @return comparison result
	 */
	@Override
	public int compareTo(PriceRange priceRange) {
		if (priceRange == null) {
			return 1;
		}
		if (getPipCount() < priceRange.getPipCount()) {
			return -1;
		} else if (getPipCount() > priceRange.getPipCount()) {
			return 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pipCount;
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
		PriceRange other = (PriceRange) obj;
		if (getPipCount() != other.getPipCount()) {
			return false;
		}
		return true;
	}
}
