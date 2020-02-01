/**
 * The file InverseRectanglePatternIndicator.java was created on Mar 2, 2010 at 11:01:46 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class InverseRectanglePatternIndicator extends AbstractPatternIndicator {
	
	/**
	 * 
	 *               5 
	 *     1   3    /
	 *     /\  /\  /
	 *    /  \/  \/
	 *   /	  2   4 
	 *  0             
	 *  
	 */
	@Override
	protected boolean checkPattern(
			double[] cleanPattern, 
			double[] dirtyPattern, 
			int dirtyPatternStartIndex, 
			int dirtyPatternSize
			
	) {
		if (
				
				cleanPattern[0] > cleanPattern[1] &&
				cleanPattern[1] > cleanPattern[2] &&
				cleanPattern[2] < cleanPattern[3] &&
				cleanPattern[3] > cleanPattern[4] &&
				
				cleanPattern[0] < cleanPattern[2] &&
				cleanPattern[0] < cleanPattern[4] &&
				
				cleanPattern[1] > cleanPattern[4] &&
				
				aproxEqual(cleanPattern[1], cleanPattern[3]) &&
				aproxEqual(cleanPattern[2], cleanPattern[4]) &&
				
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[4] < cleanPattern[5] &&
								cleanPattern[5] > cleanPattern[3] &&
								cleanPattern[5] > cleanPattern[1]
						)
				)
				
		) {
			
			return true;
			
		}
		
		return false;
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("INVERSE_RECT", "Inverse Rectangle Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	protected int getPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 6 : 5;
	}

	@Override
	protected IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {2, 4});
	}

	@Override
	protected IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {1, 3});
	}

}
