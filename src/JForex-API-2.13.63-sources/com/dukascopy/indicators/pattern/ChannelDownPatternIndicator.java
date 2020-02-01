/**
 * The file ChannelDownPatternIndicator.java was created on Mar 4, 2010 at 10:48:11 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class ChannelDownPatternIndicator extends AbstractChannelPatternIndicator {
	
	/*
	 * Is needed for not to confuse bottom and upper asymptotes
	 */
	private boolean isTypeOne = false;

	
	/**
	 * Type 1.
	 *         
	 *        0			 
	 *         \   2    
	 *          \  /\  
	 *           \/  \ 
	 *           1    \  
	 *                 3
	 *                 
	 * Type 2.
	 * 
	 *        1 
	 *        /\
	 *       /  \    3
	 *      0    \  /
	 *            \/
	 *            2   
	 *            
	 *   Lines (0; 2) and (1; 3) has to be parallel
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
				
				cleanPattern[0] > cleanPattern[2] &&
				
				cleanPattern[1] > cleanPattern[3] &&
				
				areParallel(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2}, new int[]{1, 3})
				
		) {
			isTypeOne = true;
			return true;
			
		}
		
		else if (
				
				cleanPattern[0] < cleanPattern[1] &&
				cleanPattern[1] > cleanPattern[2] &&
				cleanPattern[2] < cleanPattern[3] &&
				
				cleanPattern[0] > cleanPattern[2] &&
				
				cleanPattern[1] > cleanPattern[3] &&
				
				areParallel(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{0, 2}, new int[]{1, 3})
		) {
			isTypeOne = false;
			return true;
			
		}

		return false;
		
	}

	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("CHANNEL_DOWN", "Channel Down Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
	}

	@Override
	protected int getPatternPivotPointNumber() {
		return 4;
	}

	@Override
	protected IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		int[] asymptote = null;
		
		if (isTypeOne) {
			asymptote = new int[]{1, 3};
		}
		else {
			asymptote = new int[]{0, 2};
		}

		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
	}

	@Override
	protected IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		int[] asymptote = null;
		
		if (isTypeOne) {
			asymptote = new int[]{0, 2};
		}
		else {
			asymptote = new int[]{1, 3};
		}
		
		return constructDiagonalAsymptote(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, asymptote);
	}

}
