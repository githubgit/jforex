package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class McClellanOscillator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    
    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private IIndicator ma1;
    private IIndicator ma2;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MCCLOSC", "McClellan Oscillator", "Momentum Indicators", false, false, true, 1, 3, 1);

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
            new OptInputParameterInfo("Short MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(19, 2, 2000, 1)),
            new OptInputParameterInfo("Long MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(39, 2, 2000, 1)),
            new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.EMA.ordinal(), maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Values", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ma1 = indicatorsProvider.getIndicator("MA");
        ma1.setOptInputParameter(1, IIndicators.MaType.EMA.ordinal());
        ma2 = indicatorsProvider.getIndicator("MA");
        ma2.setOptInputParameter(1, IIndicators.MaType.EMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] maOutputs = new double[2][endIndex - startIndex + 1];
        ma1.setInputParameter(0, inputs[0]);
        ma1.setOutputParameter(0, maOutputs[0]);
        ma1.calculate(startIndex, endIndex);

        ma2.setInputParameter(0, inputs[0]);
        ma2.setOutputParameter(0, maOutputs[1]);
        ma2.calculate(startIndex, endIndex);

        for (int i = 0; i < endIndex - startIndex + 1; i++) {
            outputs[0][i] = maOutputs[0][i] - maOutputs[1][i];
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1);
    }

    @Override
    public IndicatorInfo getIndicatorInfo() {
        return indicatorInfo;
    }

    @Override
    public InputParameterInfo getInputParameterInfo(int index) {
        if (index < inputParameterInfos.length) {
            return inputParameterInfos[index];
        }
        return null;
    }

    @Override
    public OptInputParameterInfo getOptInputParameterInfo(int index) {
        if (index < optInputParameterInfos.length) {
            return optInputParameterInfos[index];
        }
        return null;
    }

    @Override
    public OutputParameterInfo getOutputParameterInfo(int index) {
        if (index < outputParameterInfos.length) {
            return outputParameterInfos[index];
        }
        return null;
    }

    @Override
    public void setInputParameter(int index, Object array) {
        inputs[index] = (double[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            int maShortPeriod = (Integer) value;
            ma1.setOptInputParameter(0, maShortPeriod);
            break;
        case 1:
            int maLongPeriod = (Integer) value;
            ma2.setOptInputParameter(0, maLongPeriod);
            break;
        case 2:
            int maType = (Integer) value;
            ma1.setOptInputParameter(1, maType);
            ma2.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(ma1.getIndicatorInfo().isUnstablePeriod());
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return Math.max(ma1.getLookback(), ma2.getLookback());
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
