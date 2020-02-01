/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * Created by: S.Vishnyakov
 * Date: Mar 17, 2009
 * Time: 10:41:48 AM
 */
public class RelativeVigorIndicator  implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[2][];

    private int timePeriod = 10;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("RVI", "Relative Vigor Index", "Momentum Indicators", false, false, false, 1, 1, 2);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(timePeriod, 1, 100, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("RVI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Signal Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
		};

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] value1 = new double[endIndex - startIndex + 1 + getLookback() - 3];
        double[] value2 = new double[endIndex - startIndex + 1 + getLookback() - 3];

        //open, close, high, low, volume
        for (int i = startIndex - getLookback() + 3, j = 0; i <= endIndex; i++, j++) {
            // VALUE1 = ((CLOSE - OPEN) + 2 * (CLOSE (1)) - OPEN (1)) + 2*(CLOSE (2) - OPEN (2)) + (CLOSE (3) - OPEN (3))) / 6
             value1[j] = ((inputs[0][1][i - 3] - inputs[0][0][i - 3]) + 2 * (inputs[0][1][i - 2] - inputs[0][0][i - 2]) +
                     2 * (inputs[0][1][i - 1] - inputs[0][0][i - 1]) + (inputs[0][1][i] - inputs[0][0][i]))/6;
            //VALUE2 = ((HIGH - LOW) + 2 * (HIGH (1) - LOW (1)) + 2*(HIGH (2)- LOW (2)) + (HIGH (3) - LOW (3))) / 6
             value2[j] = ((inputs[0][2][i - 3] - inputs[0][3][i - 3]) + 2 * (inputs[0][2][i - 2] - inputs[0][3][i - 2]) +
                     2 * (inputs[0][2][i - 1] - inputs[0][3][i - 1]) + (inputs[0][2][i] - inputs[0][3][i]))/6;            
        }

        double[] rvi = new double[value1.length - timePeriod];
        int j = 0;
        for (int i = timePeriod; i < value1.length; i++, j++) {
            double num = 0;
            double denum = 0;
            // NUM = SUM (VALUE1, N)
             // DENUM = SUM (VALUE2, N)
            for (int x = j; x < (j +     timePeriod); x++) {
                num = value1[x] + num;
                denum = value2[x] + denum;
            }
            // RVI = NUM / DENUM
            rvi[j] = num/denum;
        }
        int z = 0;
        //RVISig = (RVI + 2 * RVI (1) + 2 * RVI (2) + RVI (3)) / 6
        for (int y = 3; y < rvi.length; y++, z++) {
            outputs[0][z] = rvi[y];
            outputs[1][z] = (rvi[y - 3] + 2*rvi[y - 2] + 2*rvi[y - 1] + rvi[y])/6;
        }

        return new IndicatorResult(startIndex, z);
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
        timePeriod = (Integer) value;
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return timePeriod + 6;
    }

    public int getLookforward() {
        return 0;
    }
}
