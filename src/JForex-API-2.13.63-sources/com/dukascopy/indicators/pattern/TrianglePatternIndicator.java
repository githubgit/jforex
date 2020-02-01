/**
 * The file TrianglePatternIndicator.java was created on Mar 3, 2010 at 2:08:22 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.dukascopy.api.indicators.IndicatorInfo;

@Deprecated
public class TrianglePatternIndicator extends AbstractPatternIndicator {
	
	/**
	 * 
	 *   
	 *            2
	 *            /\       4
	 *           /  \      /\
	 *          /    \    /  5
	 *         /      \  /
	 *     0  /        \/
	 *      \/         3 
	 *      1
	 *                       
	 *                       
	 *             OR
	 *             
	 *      1       
	 *      /\         3  
	 *     0  \        /\
	 *         \      /  \      
	 *          \    /    \  5 
	 *           \  /      \/
	 *            \/       4
	 *            2      
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
				(
						cleanPattern[0] > cleanPattern[1] &&
						cleanPattern[1] < cleanPattern[2] &&
						cleanPattern[2] > cleanPattern[3] &&
						cleanPattern[3] < cleanPattern[4] &&
				
						cleanPattern[2] > cleanPattern[4] &&
						cleanPattern[3] > cleanPattern[1] &&
						(
								!isWholePatternCalculation() ||
								cleanPattern[4] > cleanPattern[5]
						)
				) 
				
				||
				
				(
						cleanPattern[0] < cleanPattern[1] &&
						cleanPattern[1] > cleanPattern[2] &&
						cleanPattern[2] < cleanPattern[3] &&
						cleanPattern[3] > cleanPattern[4] &&
				
						cleanPattern[2] < cleanPattern[4] &&
						cleanPattern[3] < cleanPattern[1] &&
						(
								!isWholePatternCalculation() ||
								cleanPattern[4] < cleanPattern[5]
						)
				)
		) {
			boolean willCross = willAsymptotesCrossForUnitsCount(
					cleanPattern,
					dirtyPattern,
					dirtyPatternStartIndex,
					dirtyPatternSize,
					new int[]{2, 4},
					new int[]{1, 3},
					BARS_FORWARD_UNTIL_CROSS
			);
			
			if (!willCross) {
				return false;
			}

			/*
			 * Asymptotes cross point must be later than the last line cross with one of two asymptote
			 */
			int asymptotesCrossIndex = (int)calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{1, 3});
			int firstAsymptoteLastLineCrossIndex = (int)calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{3, 4});
			int secondAsymptoteLastLineCrossIndex = (int)calculateCrossIndex(cleanPattern, dirtyPattern, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3}, new int[]{3, 4});
			
			if (
					asymptotesCrossIndex > firstAsymptoteLastLineCrossIndex &&
					asymptotesCrossIndex > secondAsymptoteLastLineCrossIndex
			) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected IndicatorInfo createIndicatorInfo() {
		return new IndicatorInfo("TRIANGLE", "Triangle Pattern", getDefaultPatternRecognitionGroup(), true, false, false, 1, getOptInputParameterInfos().length, getNumberOfOutputs());
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
		return constructDiagonalAsymptoteUntilCross(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{1, 3}, new int[]{2, 4});
	}

	@Override
	protected IndexValue[] constructUppperAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return constructDiagonalAsymptoteUntilCross(cleanPattern, pivotPointsData, dirtyPatternStartIndex, dirtyPatternSize, new int[]{2, 4}, new int[]{1, 3}	);
	}
	
	@Override
	protected void drawPatternLine(
			IndexValue[] indexValues,
			int currentIndex,
			Graphics2D g,
			int x1,
			int y1,
			int x2,
			int y2
	) {
		if (indexValues.length > 2) {
			if (currentIndex <= 1) {
				Point2D point = continueLine(x1, y1, x2, y2, 15);
				g.drawLine((int)point.getX(), (int)point.getY(), x1, y1);
			}
			else if (isWholePatternCalculation() && currentIndex > indexValues.length - 2) {
				Point2D point = continueLine(x2, y2, x1, y1, 15);
				g.drawLine((int)point.getX(), (int)point.getY(), x2, y2);
			}
			else {
				super.drawPatternLine(indexValues, currentIndex, g, x1, y1, x2, y2);
			}
		}
		else {
			super.drawPatternLine(indexValues, currentIndex, g, x1, y1, x2, y2);
		}
	}

	private Point2D continueLine(int x1, int y1, int x2, int y2, int segmentLength) {
		Line2D line1 = new Line2D.Double(x1, y1, x2, y2);
		Line2D line2 = new Line2D.Double(x2, y2, 2 * x2, y2);

		double angle = angleBetween2LinesRadians(line1, line2);
		
		if (x1 < 0 || x2 < 0) {
			angle = Math.PI + angle;
		}
		
		double dx = Math.cos(angle) * segmentLength;
		double dy = Math.sin(angle) * segmentLength;
		Point2D point = new Point2D.Double(x1 + dx, y1 + dy);
		
		return point;
	}
	
}
