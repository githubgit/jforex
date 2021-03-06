/**
 * The file RisingWedgePatternIndicator.java was created on Mar 3, 2010 at 5:15:13 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class RisingWedgePatternIndicator extends AbstractPatternIndicator {
	
	/**
	 *  0
	 *   \ 
	 *    \                 4 
	 *     \         2      /\
	 *      \        /\    /  \
	 *       \      /  \  /    \
	 *        \    /    \/      \
	 *         \  /     3        \
	 *          \/                5
	 *          1                  
	 *                              
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
				cleanPattern[1] < cleanPattern[2] &&
				cleanPattern[2] > cleanPattern[3] &&
				cleanPattern[3] < cleanPattern[4] &&
				
				cleanPattern[0] > cleanPattern[2] &&
				cleanPattern[0] > cleanPattern[4] &&
				cleanPattern[2] < cleanPattern[4] &&
				
				cleanPattern[1] < cleanPattern[3] &&
				
				!willAsymptotesCrossUntilPatternEnd(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3}, new int[]{2, 4}) &&
				(
						!isWholePatternCalculation() || 
						(
								cleanPattern[5] < cleanPattern[3] && 
								cleanPattern[4] > cleanPattern[5]
						)
				)
				
		) {
			if (isWholePatternCalculation() && cleanPattern[5] == 0) {
				System.out.println(cleanPattern[5]);
			}
			return true;
			
		}
		
		return false;
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("RISING_WEDGE", "Rising Wedge Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
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
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3});
	}

	@Override
	protected IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4});
	}
}
