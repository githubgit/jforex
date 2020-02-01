/**
 * The file RectanglePatternIndicator.java was created on Feb 26, 2010 at 1:22:11 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class HeadAndShouldersPatternIndicator extends AbstractHeadAndShouldersPatternIndicator {

	/**
	 *   	  3
	 * 	      /\
	 *	  1  /  \  5
	 *	  /\/	 \/\	
	 *	 /  2	 4  \
	 *  0			 6
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
				cleanPattern[3] > cleanPattern[4] &&
				cleanPattern[4] < cleanPattern[5] && 
				
				cleanPattern[3] >= cleanPattern[5] + (cleanPattern[5] - cleanPattern[4]) * getHeadHeightRatio() && // head has to be taller than shoulders
				cleanPattern[3] >= cleanPattern[1] + (cleanPattern[1] - cleanPattern[2]) * getHeadHeightRatio() && // head has to be taller than shoulders

				cleanPattern[2] >= cleanPattern[0] &&
				
				aproxEqual(cleanPattern[1], cleanPattern[5]) &&
				aproxEqual(cleanPattern[2], cleanPattern[4]) &&
				(
						!isWholePatternCalculation() ||
						(
								cleanPattern[5] > cleanPattern[6] &&
								cleanPattern[4] >= cleanPattern[6]
						)
				)
				
		) {

			/*
			 * First line must be lower than asymptote
			 */
			double[] asymptoteLinePriceValues = indicesToValues(cleanPattern, new int[]{2, 4});
			double avgPoint = average(asymptoteLinePriceValues);
			if (cleanPattern[0] < avgPoint) {
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("HEAD&SHLDRS", "Head and Shoulders Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}
	
	@Override
	protected int getPatternPivotPointNumber() {
		return isWholePatternCalculation() ? 7 : 6;
	}

}
