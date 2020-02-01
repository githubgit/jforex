/**
 * The file DescendingTrianglePatternIndicator.java was created on Mar 3, 2010 at 1:56:08 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class DescendingTrianglePatternIndicator extends AbstractPatternIndicator {

	/**
	 * 
	 *  0 
	 *   \ 
	 *    \     2
	 *     \    /\   4
	 *      \  /  \  /\
	 *       \/    \/  \
	 *       1     3    \
	 *                   \
	 *  				  5
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
				cleanPattern[2] > cleanPattern[4] &&

				aproxEqual(cleanPattern[1], cleanPattern[3]) && 
				
				willAsymptotesCrossBeforeBarsCount(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{1, 3}, average(cleanPattern[1], cleanPattern[3]), BARS_FORWARD_UNTIL_CROSS / 2) &&
				
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[5] < cleanPattern[1] &&
								cleanPattern[5] < cleanPattern[3]
						)
				)
				
		) {
			return true;
			
		}
		
		return false;
		
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("DESC_TRIANGLE", "Descending Triangle Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
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
		return constructHorizontalAsymptoteUntilCross(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {1, 3}, new int[] {2, 4});
	}

	@Override
	protected IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructDiagonalAsymptoteUntilCrossWithHorizontal(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {2, 4}, new int[] {1, 3});
	}

}
