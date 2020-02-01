/**
 * The file FallingWedgePatternIndicator.java was created on Mar 3, 2010 at 4:16:29 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class FallingWedgePatternIndicator extends AbstractPatternIndicator {
	
	/**
	 *  0
	 *   \                        
	 *    \     2                  
	 *     \    /\
	 *      \  /  \   4      6     
	 *       \/    \  /\    /
	 *       1      \/  \  /
	 *              3    \/ 
	 *                    5
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
				cleanPattern[4] > cleanPattern[5] &&
				
				cleanPattern[0] > cleanPattern[2] &&
				cleanPattern[0] > cleanPattern[4] &&
				
				cleanPattern[2] > cleanPattern[4] &&
				
				
				cleanPattern[5] < cleanPattern[3] &&
				cleanPattern[5] < cleanPattern[1] &&
				cleanPattern[3] < cleanPattern[1] &&
				
				!willAsymptotesCrossUntilPatternEnd(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{1, 3, 5}) &&
				
				(
						!isWholePatternCalculation() || 
						(
								cleanPattern[4] <= cleanPattern[6] &&
								cleanPattern[5] < cleanPattern[6]
						)
				)
				
		) {
			
			return true;
			
		}
		
		return false;
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("FALLING_WEDGE", "Falling Wedge Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	protected int getPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 7 : 6;
	}

	@Override
	protected IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3, 5});
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
