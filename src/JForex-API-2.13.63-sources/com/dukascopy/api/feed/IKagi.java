package com.dukascopy.api.feed;

import com.dukascopy.api.PriceRange;

public interface IKagi extends IPriceAggregationBar{

	
	/**
	 * According to change a trend for a next line, the current price must exceed the turnaround price. Turnaround price depends 
	 * on the chosen {@link PriceRange} value. E.g. if we use {@link PriceRange#THREE_PIPS}, then the trend of the line is changed
	 * if next session price exceeds 3 pips of previous line's minimum or maximum. 
	 * 
	 * @return turnaround price
	 */
	double getTurnaroundPrice();

	/**
	 * Provides an information about the trend of the line. 
	 * 
	 * @return true (bullish) or false (bearish)
	 */
	
	Boolean isRising();
	
	/**
     * Returns the number of time sessions which formed the current line
     * 
     * @return elements count
     */
	@Override
    long getFormedElementsCount();
	
	/**
	 * Returns the 'yang' (the thick) line's price range interval. Because one Kagi line can contain both - yang (thick) and yin (thin) line, the returned interval can be with including or excluding margins. 
	 * Use {@link #isYang(double)} or {@link #isYin(double)} method to detect whether the yin/yang interaction point belongs to yang or yin line type.
	 * E.g. one kagi line (minimum = 10, maximum = 20) with rising trend line consists of - yin (thin) line [10,15], and yang line (15,20], the method <i>isYang(15)</i> will return <i>false</i> while 
	 * <i>isYin(15)</i> will return <i>true</i>. <br>
	 * The method returns double[] array with two elements - array[0] = minimum, array[1] = maximum. Method returns null if this Kagi line contains only yin line type.  
	 *   
	 * @return the double[] array or null. 
	 */
	double[] getYangSpan();
	
	/**
	 * Returns the 'yin' (the thin) line's price range interval. Because one Kagi line can contain both - yang (thick) and yin (thin) line, the returned interval can be with including or excluding margins. 
	 * Use {@link #isYang(double)} or {@link #isYin(double)} method to detect whether the yin/yang interaction point belongs to yang or yin line type.
	 * E.g. one kagi line (minimum = 10, maximum = 20) with rising trend line consists of - yin (thin) line [10,15], and yang line (15,20], the method <i>isYang(15)</i> will return <i>false</i> while 
	 * <i>isYin(15)</i> will return <i>true</i>. <br>
	 * The method returns double[] array with two elements - array[0] = minimum, array[1] = maximum. Method returns null if this Kagi line contains only yang line type.  
	 *   
	 * @return the double[] array or null. 
	 */
	double[] getYinSpan();
	
	/**
	 * Indicates whether or not this price belongs to Kagi line's yin (thin) part.
	 * Returns <i>false</i> if this Kagi line doesn't contain yin line or if the given price is outside the price interval of yin line.
	 * @param price price value
	 * @return boolean value. Returns <i>true</i> if price belongs to yin line. Otherwise returns <i>false</i> 
	 */
	boolean isYin(double price);
	
	/**
	 * Indicates whether or not this price belongs to Kagi line's yang (thick) part.
	 * Returns <i>false</i> if this Kagi line doesn't contain yang line or if the given price is outside the price interval of yang line.
	 * @param price price value
	 * @return boolean value. Returns <i>true</i> if price belongs to yang line. Otherwise returns <i>false</i>. 
	 */
	boolean isYang(double price);
	
}
