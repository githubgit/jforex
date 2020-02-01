/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Feb 11, 2009
 * Time: 12:56:41 PM
 */
public class AlligatorIndicator implements IIndicator {

    private IIndicator jawSmmaIndicator;
    private IIndicator teethSmmaIndicator;
    private IIndicator lipsSmmaIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private int jawPeriod = 13;
    private int teethPeriod = 8;
    private int lipsPeriod = 5;

    private final int jawShift = 8;
    private final int teethShift = 5;
    private final int lipsShift = 3;

    public void onStart(IIndicatorContext context) {
        jawSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");
        teethSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");
        lipsSmmaIndicator = context.getIndicatorsProvider().getIndicator("SMMA");

        indicatorInfo = new IndicatorInfo("Alligator", "Alligator", "Bill Williams", true, false, true, 1, 3, 3);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
		};

        inputParameterInfos[0].setAppliedPrice(IIndicators.AppliedPrice.MEDIAN_PRICE);

        optInputParameterInfos = new OptInputParameterInfo[] {
			new OptInputParameterInfo("Jaw Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(jawPeriod, 2, 200, 1)),
			new OptInputParameterInfo("Teeth Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(teethPeriod, 2, 200, 1)),
			new OptInputParameterInfo("Lips Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(lipsPeriod, 2, 200, 1))
		};

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Alligator Jaw", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Alligator Teeth", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Alligator Lips", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
		};

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[0].setShift(jawShift);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[1].setShift(teethShift);
        outputParameterInfos[2].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[2].setShift(lipsShift);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }      

        jawSmmaIndicator.setInputParameter(0, inputs[0]);
        teethSmmaIndicator.setInputParameter(0, inputs[0]);
        lipsSmmaIndicator.setInputParameter(0, inputs[0]);

        jawSmmaIndicator.setOutputParameter(0, outputs[0]);
        teethSmmaIndicator.setOutputParameter(0, outputs[1]);
        lipsSmmaIndicator.setOutputParameter(0, outputs[2]);

        jawSmmaIndicator.calculate(startIndex, endIndex);
        teethSmmaIndicator.calculate(startIndex, endIndex);
        return lipsSmmaIndicator.calculate(startIndex, endIndex);       
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
        inputs[index] = (double[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            jawPeriod = (Integer) value;
            jawSmmaIndicator.setOptInputParameter(0, jawPeriod);
            break;
        case 1:
            teethPeriod = (Integer) value;
            teethSmmaIndicator.setOptInputParameter(0, teethPeriod);
            break;
        case 2:
            lipsPeriod = (Integer) value;
            lipsSmmaIndicator.setOptInputParameter(0, lipsPeriod);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return Math.max(teethPeriod + teethShift, Math.max(jawPeriod + jawShift, lipsPeriod + lipsShift)) + 1;
    }

    public int getLookforward() {
        return 0;
    }
}
