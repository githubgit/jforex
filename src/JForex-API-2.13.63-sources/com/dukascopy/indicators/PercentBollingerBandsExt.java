package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.DoubleRangeDescription;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class PercentBollingerBandsExt implements IIndicator {
    private IIndicator bbandsIndicator;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    public void onStart(IIndicatorContext context) {
        bbandsIndicator = context.getIndicatorsProvider().getIndicator("BBANDS");

        indicatorInfo = new IndicatorInfo("PERSBBANDS_EXT", "Percent Bollinger Bands (with extended parameters)",
                "Momentum Indicators", false, false, true, 1, 4, 1);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1)),
            new OptInputParameterInfo("Nb Dev Up", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(2, -10000, 10000, 0.01, 3)),
            new OptInputParameterInfo("Nb Dev Dn", OptInputParameterInfo.Type.OTHER, new DoubleRangeDescription(2, -10000, 10000, 0.01, 3)),
            new OptInputParameterInfo("MA type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.EMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("BBands%", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 50, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 100, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int totalElements = endIndex - startIndex + 1;
        double[] bbandsHighOutput = new double[totalElements];
        double[] bbandsMiddleOutput = new double[totalElements];
        double[] bbandsLowOutput = new double[totalElements];

        bbandsIndicator.setInputParameter(0, inputs[0]); 
        bbandsIndicator.setOutputParameter(0, bbandsHighOutput);
        bbandsIndicator.setOutputParameter(1, bbandsMiddleOutput);
        bbandsIndicator.setOutputParameter(2, bbandsLowOutput);
        IndicatorResult bbandsRes = bbandsIndicator.calculate(startIndex, endIndex);

        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            if (bbandsHighOutput[j] - bbandsLowOutput[j] == 0) {
                outputs[0][j] = 0;
            } else {
                outputs[0][j] = ((inputs[0][i] - bbandsLowOutput[j]) / (bbandsHighOutput[j] - bbandsLowOutput[j])) * 100;
            }
        }

        return new IndicatorResult(bbandsRes.getFirstValueIndex(), bbandsRes.getNumberOfElements());
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
        bbandsIndicator.setOptInputParameter(index, value);

        if (index == 3) {
            indicatorInfo.setUnstablePeriod(bbandsIndicator.getIndicatorInfo().isUnstablePeriod());
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return bbandsIndicator.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
