/**
 * The file AbstractHeadAndShouldersPatternIndicator.java was created on Mar 1, 2010 at 5:23:49 PM
 * by @author Marks Vilkelis
 */
package com.dukascopy.indicators.pattern;

import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

@Deprecated
public abstract class AbstractHeadAndShouldersPatternIndicator extends AbstractPatternIndicator {
	
	private double headHeightRatio = 0.3d;

	@Override
	protected IndexValue[] constructBottomAsymptote(
			double[] cleanPattern,
			double[] pivotPointsData, 
			int dirtyPatternStartIndex,
			int dirtyPatternSize
			
	) {
		return null;
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

	@Override
	protected OutputParameterInfo[] createOutputParameterInfo() {
		return new OutputParameterInfo[] {
				createPatterCurveInfo(),
	            new OutputParameterInfo("Asymptote", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE) {{
	    			setDrawnByIndicator(true);
	    		}}
	        };
	}

	@Override
	protected OptInputParameterInfo[] createOptInputParameterInfo() {
		OptInputParameterInfo[] superOptParamInfo = super.createOptInputParameterInfo();
		OptInputParameterInfo[] extraOptParamInfo = new OptInputParameterInfo[]{new OptInputParameterInfo("Head height ratio (times)", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(getHeadHeightRatio(), 0.01d, 100d, 0.01d, 2))};
		OptInputParameterInfo[] result = new OptInputParameterInfo[superOptParamInfo.length + extraOptParamInfo.length];
		
		System.arraycopy(superOptParamInfo, 0, result, 0, superOptParamInfo.length);
		System.arraycopy(extraOptParamInfo, 0, result, superOptParamInfo.length, extraOptParamInfo.length);

		return result;
	}
	
	@Override
	public void setOptInputParameter(int index, Object value) {
        if (index == 4) {
        	setHeadHeightRatio((Double) value);
        }
        else {
        	super.setOptInputParameter(index, value);
        }
	}

	protected double getHeadHeightRatio() {
		return headHeightRatio;
	}

	protected void setHeadHeightRatio(double headHeightRatio) {
		this.headHeightRatio = headHeightRatio;
	}
	
	@Override
	protected int getNumberOfOutputsForOneSubindicator() {
		return 2;
	}
}
