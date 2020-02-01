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
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Created by: S.Vishnyakov
 * Date: Aug 26, 2009
 * Time: 2:55:39 PM
 */
public class IchimokuIndicatorVF implements IIndicator {

    public static final int SENKOU_A = 0;
    public static final int SENKOU_B = 1;

    private IIndicator tenkanMaxIndicator;
    private IIndicator tenkanMinIndicator;
    private IIndicator kijunMaxIndicator;
    private IIndicator kijunMinIndicator;
    private IIndicator senkouBMaxIndicator;
    private IIndicator senkouBMinIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[5][];

    private int tenkan = 9;
    private int kijun = 26;
    private int senkou = 52;

    public void onStart(IIndicatorContext context) {
        tenkanMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        tenkanMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");
        kijunMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        kijunMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");
        senkouBMaxIndicator = context.getIndicatorsProvider().getIndicator("MAX");
        senkouBMinIndicator = context.getIndicatorsProvider().getIndicator("MIN");

        indicatorInfo = new IndicatorInfo("ICHIMOKUVF", "Ichimoku VF", "Overlap Studies", true, false, false, 1, 3, 5);

        inputParameterInfos = new InputParameterInfo[] {
    		new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
    		new OptInputParameterInfo("Tenkan", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(tenkan, 1, 400, 1)),
    		new OptInputParameterInfo("Kijun", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(kijun, 2, 400, 1)),
    		new OptInputParameterInfo("Senkou", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(senkou, 2, 400, 1))
		};

        outputParameterInfos = new OutputParameterInfo[] {
    		new OutputParameterInfo("Tenkan Sen", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Kijun Sen", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
    		new OutputParameterInfo("Chikou Span", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
			new OutputParameterInfo("Senkou A", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
			new OutputParameterInfo("Senkou B", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.DARK_ORANGE);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[2].setColor(DefaultColors.DEEP_MAGENTA);
        outputParameterInfos[2].setShift(-kijun);
        outputParameterInfos[3].setColor(DefaultColors.RED);
        outputParameterInfos[3].setShift(kijun);
        outputParameterInfos[4].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[4].setShift(kijun);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (endIndex + getLookforward() >= inputs[0][0].length) {
            endIndex = inputs[0][0].length - 1 - getLookforward();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] tenkanMax = new double[endIndex - startIndex + 1];
        double[] tenkanMin = new double[endIndex - startIndex + 1];
        double[] kijunMax = new double[endIndex - startIndex + 1];
        double[] kijunMin = new double[endIndex - startIndex + 1];
        double[] senkouBMax = new double[endIndex - startIndex + 1];
        double[] senkouBMin = new double[endIndex - startIndex + 1];

        // high value for max
        tenkanMaxIndicator.setInputParameter(0, inputs[0][2]);
        kijunMaxIndicator.setInputParameter(0, inputs[0][2]);
        senkouBMaxIndicator.setInputParameter(0, inputs[0][2]);
        // low value for min
        tenkanMinIndicator.setInputParameter(0, inputs[0][3]);
        kijunMinIndicator.setInputParameter(0, inputs[0][3]);
        senkouBMinIndicator.setInputParameter(0, inputs[0][3]);

        tenkanMaxIndicator.setOutputParameter(0, tenkanMax);
        tenkanMinIndicator.setOutputParameter(0, tenkanMin);
        kijunMaxIndicator.setOutputParameter(0, kijunMax);
        kijunMinIndicator.setOutputParameter(0, kijunMin);
        senkouBMaxIndicator.setOutputParameter(0, senkouBMax);
        senkouBMinIndicator.setOutputParameter(0, senkouBMin);

        IndicatorResult dtenkanMaxResult = tenkanMaxIndicator.calculate(startIndex, endIndex);
        IndicatorResult dtenkanMinResult = tenkanMinIndicator.calculate(startIndex, endIndex);
        IndicatorResult dkijunMaxResult = kijunMaxIndicator.calculate(startIndex, endIndex);
        IndicatorResult dkijunMinResult = kijunMinIndicator.calculate(startIndex, endIndex);
        IndicatorResult dsenkouBMaxResult = senkouBMaxIndicator.calculate(startIndex, endIndex);
        IndicatorResult dsenkouBMinResult = senkouBMinIndicator.calculate(startIndex, endIndex);

        int i, k;
        for (i = 0, k = dtenkanMaxResult.getNumberOfElements(); i < k; i++) {
            outputs[0][i] = (tenkanMax[i] + tenkanMin[i]) / 2;
            outputs[1][i] = (kijunMax[i] + kijunMin[i]) / 2;

            // senkou A
            outputs[3][i] = (outputs[1][i] + outputs[0][i]) / 2;
            outputs[4][i] = (senkouBMax[i] + senkouBMin[i]) / 2;
        }

        int resIndex = 0;
        // chikou = close with shift by tenkan param
        for (int z = startIndex; z <= endIndex; z++, resIndex++) {
            //Inputs: 0 open, 1 close, 2 high, 3 low, 4 volume
            outputs[2][resIndex] = inputs[0][1][z];
        }

        return new IndicatorResult(startIndex, i);
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
        switch (index) {
            case 0:
                tenkan = (Integer) value;
                break;
            case 1:
                kijun = (Integer) value;
                break;
            case 2:
                senkou = (Integer) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }

        outputParameterInfos[2].setShift(-kijun);
        outputParameterInfos[3].setShift(kijun);
        outputParameterInfos[4].setShift(kijun);

        tenkanMaxIndicator.setOptInputParameter(0, tenkan);
        tenkanMinIndicator.setOptInputParameter(0, tenkan);
        kijunMaxIndicator.setOptInputParameter(0, kijun);
        kijunMinIndicator.setOptInputParameter(0, kijun);
        senkouBMaxIndicator.setOptInputParameter(0, senkou);
        senkouBMinIndicator.setOptInputParameter(0, senkou);
    }

    public void setOutputParameter(int index, Object array) {
        if (index < 5) {
            outputs[index] = (double[]) array;
        }
    }

    public int getLookback() {
        return Math.max(tenkan, Math.max(kijun, senkou)) - 1;
    }

    public int getLookforward() {
        return 0;
    }
}
