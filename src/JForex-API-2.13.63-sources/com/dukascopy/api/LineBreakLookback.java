/*
 * Copyright 2014 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is used for Line Break data look-back identification. E.g. 3 Line Break, 4 Line Break, etc. 
 * 
 * @author Janis Garsils
 */
public class LineBreakLookback implements Comparable<LineBreakLookback>, Serializable {

	private String name;
	private int numOfLines;
	
	public static final LineBreakLookback TWO_LINES;
	public static final LineBreakLookback THREE_LINES;
	public static final LineBreakLookback FOUR_LINES;
	public static final LineBreakLookback FIVE_LINES;
	
	public static int MIN_NUM_OF_LINES = 2;
	public static int MAX_NUM_OF_LINES = 10;
	
	private static final List<LineBreakLookback> LOOKBACKS;
	
	static {
		TWO_LINES = new LineBreakLookback("TWO_LINES", 2);
		THREE_LINES = new LineBreakLookback("THREE_LINES", 3);
		FOUR_LINES = new LineBreakLookback("FOUR_LINES", 4);
		FIVE_LINES = new LineBreakLookback("FIVE_LINES", 5);
		
		LOOKBACKS = Collections.unmodifiableList(Arrays.asList(createAllPossibleNLineBreakLookbackValues()));
	}
	
	private LineBreakLookback(String name, int numOfLines) {
		this.name = name;
		this.numOfLines = numOfLines;
	}

	/**
	 * Method returns the name of look-back value
	 * 
	 * @return the name of look-back value
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method returns number of look-back lines
	 * 
	 * @return number of look-back lines
	 */
	public int getNumOfLines() {
		return numOfLines;
	}

	/**
	 * The method returns appropriate LineBreakLookback for the passed String
	 * Note -  maximal allowed number of look-back lines is 10, minimal - 2.
	 * 
	 * @param str String representation
	 * @return LineBreakLookback
	 */
	public static LineBreakLookback valueOf(String str) {
		if (str == null) {
			return null;
		}
		
		for (LineBreakLookback pr : LOOKBACKS) {
			if (pr.getName().toLowerCase().startsWith(str.toLowerCase())) {
				return pr;
			}
		}
		
		try {
			int nrOfLines = Integer.parseInt(str);
			return valueOf(nrOfLines);
		} catch (Throwable t) {
			return null;
		}
	}
	
	/**
	 * The method returns appropriate LineBreakLookback for the passed number of lines.
	 * Note -  maximal allowed number of look-back lines is 10, minimal - 2.
	 * 
	 * @param lineNum number of look-back lines
	 * @return LineBreakLookback or null, if the lineNum exceeds allowed values. 
	 */
	public static LineBreakLookback valueOf(int lineNum) {
		for (LineBreakLookback pr : LOOKBACKS) {
			if (pr.getNumOfLines() == lineNum) {
				return pr;
			}
		}
		return null;
	}
	
	public static LineBreakLookback getDefault() {
	    return THREE_LINES;
	}

	/**
	 * Method creates and returns all available LineBreakLookback's
	 * 
	 * @return the list of LineBreakLookback
	 */
	public static LineBreakLookback[] createAllPossibleNLineBreakLookbackValues() {
		LineBreakLookback[] result = new LineBreakLookback[MAX_NUM_OF_LINES - 1];
						
		for (int lines = 2; lines < MAX_NUM_OF_LINES + 1; lines++) {
			LineBreakLookback pr = new LineBreakLookback(lines + "_lines", lines);
			result[lines - 2] = pr;			
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * The method compares passed LineBreakLookback with the current one
     * <p>
	 * if (lookback == null) or (lookback.getNumOfLines() &lt; this.getNumOfLines()) Returns 1<br>
	 * if lookback.getNumOfLines() &gt; this.getNumOfLines() Returns -1<br>
	 * Otherwise Returns 0 
	 *
	 * @param lookback LineBreakLookback object to compare
	 * @return comparison result
	 */
	@Override
	public int compareTo(LineBreakLookback lookback) {
		if (lookback == null) {
			return 1;
		}
		if (getNumOfLines() < lookback.getNumOfLines()) {
			return -1;
		} else if (getNumOfLines() > lookback.getNumOfLines()) {
			return 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numOfLines;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LineBreakLookback other = (LineBreakLookback) obj;
		if (numOfLines != other.numOfLines)
			return false;
		return true;
	}

}
