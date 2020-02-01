package com.dukascopy.indicators;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorsProvider;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.LevelInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class TrueStrengthIndex implements IIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private IIndicator ema1;
    private IIndicator ema2;
    private IIndicator ema3;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("TSI", "True Strength Index", "Momentum Indicators", false, false, true, 1, 3, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.DOUBLE),
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Smoothing Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(20, 2, 2000, 1)),
            new OptInputParameterInfo("Double Smoothing Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(13, 2, 2000, 1)),
            new OptInputParameterInfo("Signal Line Periods", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(7, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("TSI", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Signal Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Histogram", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };

        outputParameterInfos[0].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[1].setColor(DefaultColors.RED);
        outputParameterInfos[2].setColor(DefaultColors.OLIVE_DRAB);

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 25, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.RED, 1, 1),
            new LevelInfo("", 0, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", -25, OutputParameterInfo.DrawingStyle.DOT_LINE, DefaultColors.RED, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        ema1 = indicatorsProvider.getIndicator("EMA");
        ema2 = indicatorsProvider.getIndicator("EMA");
        ema3 = indicatorsProvider.getIndicator("EMA");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[][] emaInputs = new double[3][];
        double[][] emaOutputs = new double[4][];
        emaInputs[0] = new double[endIndex - startIndex + 1 + ema1.getLookback() + ema2.getLookback() + ema3.getLookback()];
        emaInputs[1] = new double[emaInputs[0].length];
        for (int i = startIndex - ema1.getLookback() - ema2.getLookback() - ema3.getLookback(), j = 0; i <= endIndex; i++, j++) {
            emaInputs[0][j] = inputs[0][i] - inputs[0][i - 1];
            emaInputs[1][j] = Math.abs(emaInputs[0][j]);
        }

        for (int i = 0; i < 2; i++) {
            emaOutputs[i] = new double[endIndex - startIndex + 1 + ema2.getLookback() + ema3.getLookback()];
            ema1.setInputParameter(0, emaInputs[i]);
            ema1.setOutputParameter(0, emaOutputs[i]);
            ema1.calculate(ema1.getLookback(), emaInputs[i].length - 1);

            emaOutputs[i + 2] = new double[endIndex - startIndex + 1 + ema3.getLookback()];
            ema2.setInputParameter(0, emaOutputs[i]);
            ema2.setOutputParameter(0, emaOutputs[i + 2]);
            ema2.calculate(ema2.getLookback(), emaOutputs[i].length - 1);
        }

        emaInputs[2] = new double[endIndex - startIndex + 1 + ema3.getLookback()];
        for (int i = 0; i < emaInputs[2].length; i++) {
            emaInputs[2][i] = (emaOutputs[3][i] != 0 ? 100 * emaOutputs[2][i] / emaOutputs[3][i] : 0);
        }

        ema3.setInputParameter(0, emaInputs[2]);
        ema3.setOutputParameter(0, outputs[1]);
        ema3.calculate(ema3.getLookback(), emaInputs[2].length - 1);

        for (int i = ema3.getLookback(), j = 0; i < emaInputs[2].length; i++, j++) {
            outputs[0][j] = emaInputs[2][i];
            outputs[2][j] = outputs[0][j] - outputs[1][j];
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
            int period1 = (Integer) value;
            ema1.setOptInputParameter(0, period1);
            break;
        case 1:
            int period2 = (Integer) value;
            ema2.setOptInputParameter(0, period2);
            break;
        case 2:
            int period3 = (Integer) value;
            ema3.setOptInputParameter(0, period3);
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
        return ema1.getLookback() + ema2.getLookback() + ema3.getLookback() + 1;
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
