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

public class ChandeDynamicMomentumIndex implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[2][];

    private int dmiPeriods = 14;
    private int dmiLowerLimit = 3;
    private int dmiUpperLimit = 30;

    private IIndicator stddev;
    private IIndicator ma;
    private IIndicator rsi;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("CDMI", "Chande's Dynamic Momentum Index", "Momentum Indicators", false, false, false, 1, 6, 2);

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
            new OptInputParameterInfo("Standard Deviation Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 2, 2000, 1)),
            new OptInputParameterInfo("MA Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(10, 2, 2000, 1)),
            new OptInputParameterInfo("MA Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Dynamic Momentum Index Periods", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(dmiPeriods, 2, 2000, 1)),
            new OptInputParameterInfo("Dynamic Periods Lower Bounds", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(dmiLowerLimit, 2, 2000, 1)),
            new OptInputParameterInfo("Dynamic Periods Upper Bounds", OptInputParameterInfo.Type.OTHER,
                    new IntegerRangeDescription(dmiUpperLimit, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("DMI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Periods Length", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[1].setShowOutput(false);

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 30, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 50, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 70, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        stddev = indicatorsProvider.getIndicator("STDDEV");
        ma = indicatorsProvider.getIndicator("MA");
        ma.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
        rsi = indicatorsProvider.getIndicator("RSI");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] stddevOutput = new double[endIndex - startIndex + 1 + ma.getLookback()];
        stddev.setInputParameter(0, inputs[0]);
        stddev.setOutputParameter(0, stddevOutput);
        stddev.calculate(startIndex - ma.getLookback(), endIndex);

        double[] maOutput = new double[endIndex - startIndex + 1];
        ma.setInputParameter(0, stddevOutput);
        ma.setOutputParameter(0, maOutput);
        ma.calculate(ma.getLookback(), stddevOutput.length - 1);

        for (int i = 0, j = ma.getLookback(); i < endIndex - startIndex + 1; i++, j++) {
            double vi = stddevOutput[j] / maOutput[i];
            int td = (int) (dmiPeriods / vi);
            outputs[1][i] = Math.max(Math.min(td, dmiUpperLimit), dmiLowerLimit);
        }

        for (int i = 0, j = startIndex; i < endIndex - startIndex + 1; i++, j++) {
            if (Double.isNaN(outputs[1][i])) {
                outputs[0][i] = Double.NaN;
                continue;
            }

            double[] rsiOutput = new double[1];
            rsi.setInputParameter(0, inputs[0]);
            rsi.setOptInputParameter(0, (int) outputs[1][i]);
            rsi.setOutputParameter(0, rsiOutput);
            rsi.calculate(j, j);

            outputs[0][i] = rsiOutput[0];
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
            int stddevPeriod = (Integer) value;
            stddev.setOptInputParameter(0, stddevPeriod);
            stddev.setOptInputParameter(1, 1.0);
            break;
        case 1:
            int maPeriod = (Integer) value;
            ma.setOptInputParameter(0, maPeriod);
            break;
        case 2:
            int maType = (Integer) value;
            ma.setOptInputParameter(1, maType);
            indicatorInfo.setUnstablePeriod(ma.getIndicatorInfo().isUnstablePeriod());
            break;
        case 3:
            dmiPeriods = (Integer) value;
            break;
        case 4:
            dmiLowerLimit = (Integer) value;
            break;
        case 5:
            dmiUpperLimit = (Integer) value;
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return Math.max(stddev.getLookback() + ma.getLookback(), dmiUpperLimit);
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
