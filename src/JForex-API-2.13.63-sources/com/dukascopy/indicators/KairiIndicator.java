package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * 
 * @author anatoly.pokusayev
 *
 */

public class KairiIndicator implements IIndicator{    
	private IndicatorInfo indicatorInfo;
	private InputParameterInfo[] inputParameterInfos;
	private OptInputParameterInfo[] optInputParameterInfos;
	private OutputParameterInfo[] outputParameterInfos;
	private double[][] inputs = new double[1][];
	private int timePeriod = 30;
	private double[][] outputs = new double[1][];	
	private IIndicator ma;
	private IIndicatorsProvider provider;
	private int maType;

	public void onStart(IIndicatorContext context) {
		provider = context.getIndicatorsProvider();
		ma = provider.getIndicator("SMA");
		indicatorInfo = new IndicatorInfo("KAIRI", "Kairi", "Momentum Indicators", false, false, true, 1, 2, 1);

		int[] maValues = new int[IIndicators.MaType.values().length];
		String[] maNames = new String[IIndicators.MaType.values().length];
		for (int i = 0; i < maValues.length; i++) {
			maValues[i] = i;
			maNames[i] = IIndicators.MaType.values()[i].name();
		}

		inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.DOUBLE)};
		optInputParameterInfos = new OptInputParameterInfo[] {
				new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(30, 2, 200, 1)),
				new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames))};
		outputParameterInfos = new OutputParameterInfo[] { new OutputParameterInfo("Output", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE) };		
	}

	public IndicatorResult calculate(int startIndex, int endIndex) {
		if (startIndex - getLookback() < 0) {
			startIndex = getLookback();
		}
		if (startIndex > endIndex) {
			return new IndicatorResult(0, 0);
		}

		double[] maOutput = new double[endIndex - startIndex + 1];
		ma.setInputParameter(0, inputs[0]);
		ma.setOutputParameter(0, maOutput);
		if (IIndicators.MaType.values()[maType] == IIndicators.MaType.MAMA) {
			double[] maDummy = new double[endIndex - startIndex + 1];
			ma.setOptInputParameter(0, 0.5);
			ma.setOptInputParameter(1, 0.05);
			ma.setOutputParameter(1, maDummy);
		} else {
			ma.setOptInputParameter(0, timePeriod);
		}
		ma.calculate(startIndex, endIndex);
		int i, j;
		
		for (i = startIndex, j = 0; i <= endIndex; i++, j++) {	   			
			outputs[0][j] = maOutput[j] == 0 ? 0 : (inputs[0][i] - maOutput[j]) / maOutput[j];
	    }		
		return new IndicatorResult(startIndex, j);
	}

	public IndicatorInfo getIndicatorInfo() {
		return indicatorInfo;
	}

	public InputParameterInfo getInputParameterInfo(int index) {
		if (index < inputParameterInfos.length) {
			return inputParameterInfos[index];
		}
		return null;
	}

	public int getLookback() {
		return ma.getLookback();
	}

	public int getLookforward() {
		return 0;
	}

	public OptInputParameterInfo getOptInputParameterInfo(int index) {
		if (index < optInputParameterInfos.length) {
			return optInputParameterInfos[index];
		}
		return null;
	}

	public OutputParameterInfo getOutputParameterInfo(int index) {
		if (index < outputParameterInfos.length) {
			return outputParameterInfos[index];
		}
		return null;
	}

	public void setInputParameter(int index, Object array) {
		switch (index) {
		case 0:
			inputs[0] = (double[]) array;
			break;		
		default:
			throw new ArrayIndexOutOfBoundsException(index);
		}
	}

	public void setOptInputParameter(int index, Object value) {
		if (index == 0) {
			timePeriod = (Integer) value;
		} else if (index == 1) {
			int maType = (Integer) value;
			ma = provider.getIndicator(IIndicators.MaType.values()[maType].name());

			this.maType = maType;
			if (!IIndicators.MaType.values()[maType].equals(IIndicators.MaType.MAMA)) {
				ma.setOptInputParameter(0, timePeriod);
			}
		}
	}

	public void setOutputParameter(int index, Object array) {
		outputs[index] = (double[]) array;
	}
}