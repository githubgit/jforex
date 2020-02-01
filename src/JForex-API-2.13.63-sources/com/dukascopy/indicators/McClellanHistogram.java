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

public class McClellanHistogram implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[1][];

    private IIndicator osc;
    private IIndicator ma;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MCCLHIST", "McClellan Histogram", "Momentum Indicators", false, false, true, 1, 4, 1);

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
            new OptInputParameterInfo("Signal MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(9, 2, 2000, 1)),
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
        osc = indicatorsProvider.getIndicator("MCCLOSC");
        ma = indicatorsProvider.getIndicator("MA");
        ma.setOptInputParameter(1, IIndicators.MaType.EMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] oscOutput = new double[endIndex - startIndex + 1 + ma.getLookback()];
        osc.setInputParameter(0, inputs[0]);
        osc.setOutputParameter(0, oscOutput);
        osc.calculate(startIndex - ma.getLookback(), endIndex);

        double[] maOutput = new double[endIndex - startIndex + 1];
        ma.setInputParameter(0, oscOutput);
        ma.setOutputParameter(0, maOutput);
        ma.calculate(ma.getLookback(), oscOutput.length - 1);

        for (int i = ma.getLookback(), j = 0; i < oscOutput.length; i++, j++) {
            outputs[0][j] = oscOutput[i] - maOutput[j];
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
            osc.setOptInputParameter(0, maShortPeriod);
            break;
        case 1:
            int maLongPeriod = (Integer) value;
            osc.setOptInputParameter(1, maLongPeriod);
            break;
        case 2:
            int maSignalPeriod = (Integer) value;
            ma.setOptInputParameter(0, maSignalPeriod);
            break;
        case 3:
            int maType = (Integer) value;
            osc.setOptInputParameter(2, maType);
            ma.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(ma.getIndicatorInfo().isUnstablePeriod());
            break;
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return osc.getLookback() + ma.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
