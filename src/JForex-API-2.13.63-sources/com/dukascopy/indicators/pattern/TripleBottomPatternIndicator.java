/**
 * The file TripleBottomPatternIndicator.java was created on Mar 2, 2010 at 11:35:44 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class TripleBottomPatternIndicator extends AbstractPatternIndicator {

	/**
	 * 
	 *  0              6
	 *   \   2   4    /
	 *    \  /\  /\  /
	 *     \/  \/  \/
	 *     1   3    5
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
				cleanPattern[1] < cleanPattern[4] &&
				
				cleanPattern[3] < cleanPattern[0] &&
				
				cleanPattern[5] < cleanPattern[2] &&
				cleanPattern[5] < cleanPattern[0] &&
				
				cleanPattern[0] > cleanPattern[2] &&
				
				aproxEqualTriple(cleanPattern[1], cleanPattern[3], cleanPattern[5]) &&
				aproxEqual(cleanPattern[2], cleanPattern[4]) &&
				
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[5] < cleanPattern[6] &&
								cleanPattern[1] < cleanPattern[6] &&
								cleanPattern[3] < cleanPattern[6] &&
								cleanPattern[6] > cleanPattern[4]
						)
				)
				
		) {
			
			return true;
			
		}

		return false;
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("TRIPLE_BOTTOM", "Triple Bottom Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
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
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {1, 3, 5});
	}

	@Override
	protected IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {2, 4});
	}
	
}
