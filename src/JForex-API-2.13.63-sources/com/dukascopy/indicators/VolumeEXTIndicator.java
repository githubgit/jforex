package com.dukascopy.indicators;

import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * 
 * @author anatoly.pokusayev
 *
 */
public class VolumeEXTIndicator implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;  
    private double[][][] inputs = new double[1][][];
    private double[][] outputs = new double[1][];
    
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("VOLUMEEXT", "Volume indicator with customizable drawing style", "Volume Indicators", false, false, false, 1, 1, 1);
        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
		};                
        optInputParameterInfos = new OptInputParameterInfo[] {
            	new OptInputParameterInfo("Drawing Style", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(0 , new int[]{0, 1}, new String[]{"HISTOGRAM", "LINE"} ))
        };        
        outputParameterInfos = new OutputParameterInfo[] {
        		new OutputParameterInfo("Volumes", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
    	};
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {
            outputs[0][j] = inputs[0][4][i];
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
        return 0;
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
        inputs[index] = (double[][]) array;
    }

    public void setOptInputParameter(int index, Object value) {
    	DrawingStyle drawingStyle = (Integer) value == 0 ? DrawingStyle.HISTOGRAM : DrawingStyle.LINE;   
    	this.outputParameterInfos[0].setDrawingStyle(drawingStyle);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }
}
