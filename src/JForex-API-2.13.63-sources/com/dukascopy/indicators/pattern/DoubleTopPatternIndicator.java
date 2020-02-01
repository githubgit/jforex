/**
 * The file DoubleTopPatternIndicator.java was created on Mar 2, 2010 at 9:21:52 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class DoubleTopPatternIndicator extends AbstractPatternIndicator {

	/**
	 * 
	 *    1    3
	 *    /\  /\
	 *   /  \/  \
	 *  /   2    \
	 * 0          4
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
				cleanPattern[0] < cleanPattern[1] &&
				cleanPattern[1] > cleanPattern[2] &&
				cleanPattern[2] < cleanPattern[3] &&
				cleanPattern[2] > cleanPattern[0] &&
				
				aproxEqual(cleanPattern[1], cleanPattern[3]) &&
				
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[2] > cleanPattern[4] &&
								cleanPattern[3] > cleanPattern[4]
						)
				)
				
		) {
			return true;
		}
		
		return false;
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("DOUBLE_TOP", "Double Top Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	protected int getPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 5 : 4;
	}

	@Override
	protected IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructHorizontalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[] {2});
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
