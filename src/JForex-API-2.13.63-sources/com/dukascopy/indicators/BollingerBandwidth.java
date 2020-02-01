package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.DoubleRangeDescription;
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

public class BollingerBandwidth implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private IIndicator bbands;
    private IIndicator max;
    private IIndicator min;

    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("BBANDWIDTH", "Bollinger Bandwidth", "Overlap Studies", false, false, false, 1, 5, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bollinger Bands Price", InputParameterInfo.Type.DOUBLE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Bollinger Bands Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1)),
            new OptInputParameterInfo("Bollinger Bands Standard Deviations", OptInputParameterInfo.Type.OTHER,
                    new DoubleRangeDescription(2, -10000, 10000, 0.01, 3)),
            new OptInputParameterInfo("Bollinger Bands MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Bandwidth Bulge Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(150, 2, 2000, 1)),
            new OptInputParameterInfo("Bandwidth Squeeze Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(150, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Bandwidth", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("The Bulge", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("The Squeeze", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.DEEP_MAGENTA);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[2].setColor(DefaultColors.RED);

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        bbands = indicatorsProvider.getIndicator("BBANDS");
        max = indicatorsProvider.getIndicator("MAX");
        min = indicatorsProvider.getIndicator("MIN");
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] bbandsOutput = new double[4][];
        bbands.setInputParameter(0, inputs[0]);
        for (int i = 0; i < bbandsOutput.length; i++) {
            bbandsOutput[i] = new double[endIndex - startIndex + 1 + Math.max(max.getLookback(), min.getLookback())];
            if (i < 3) {
                bbands.setOutputParameter(i, bbandsOutput[i]);
            }
        }
        bbands.calculate(startIndex - Math.max(max.getLookback(), min.getLookback()), endIndex);

        for (int i = 0; i < bbandsOutput[3].length; i++) {
            bbandsOutput[3][i] = (bbandsOutput[0][i] - bbandsOutput[2][i]) / bbandsOutput[1][i];
        }

        double[] maxOutput = new double[endIndex - startIndex + 1];
        max.setInputParameter(0, bbandsOutput[3]);
        max.setOutputParameter(0, maxOutput);
        max.calculate(Math.max(max.getLookback(), min.getLookback()), bbandsOutput[3].length - 1);

        double[] minOutput = new double[endIndex - startIndex + 1];
        min.setInputParameter(0, bbandsOutput[3]);
        min.setOutputParameter(0, minOutput);
        min.calculate(Math.max(max.getLookback(), min.getLookback()), bbandsOutput[3].length - 1);

        for (int i = 0, j = Math.max(max.getLookback(), min.getLookback()); i < endIndex - startIndex + 1; i++, j++){
           outputs[0][i] = bbandsOutput[3][j];
           outputs[1][i] = maxOutput[i];
           outputs[2][i] = minOutput[i];
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
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
                int timePeriod = (Integer) value;
                bbands.setOptInputParameter(0, timePeriod);
                break;
            case 1:
                double deviation = (Double) value;
                bbands.setOptInputParameter(1, deviation);
                bbands.setOptInputParameter(2, deviation);
                break;
            case 2:
                int maType = (Integer) value;
                bbands.setOptInputParameter(3, IIndicators.MaType.values()[maType].ordinal());
                indicatorInfo.setUnstablePeriod(bbands.getIndicatorInfo().isUnstablePeriod());
                break;
            case 3:
                int bulgePeriod = (Integer) value;
                max.setOptInputParameter(0, bulgePeriod);
                break;
            case 4:
                int squeezePeriod = (Integer) value;
                min.setOptInputParameter(0, squeezePeriod);
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return bbands.getLookback() + Math.max(max.getLookback(), min.getLookback());
    }

    public int getLookforward() {
        return 0;
    }
}
