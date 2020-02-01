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

public class DrakeDelayStochasticIndicator implements IIndicator {

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[2][];

    private IIndicator max;
    private IIndicator min;
    private IIndicator ema;
    private IIndicator signalEma;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("DDS", "Drake Delay Stochastic", "", false, false, true, 1, 3, 2);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Time Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(13, 2, 2000, 1)),
            new OptInputParameterInfo("EMA Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(8, 2, 2000, 1)),
            new OptInputParameterInfo("Signal EMA Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(9, 2, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("DDS Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Signal Line", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.YELLOW_GREEN);
        outputParameterInfos[1].setColor(DefaultColors.RED);

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 20, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 80, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        max = indicatorsProvider.getIndicator("MAX");
        min = indicatorsProvider.getIndicator("MIN");
        ema = indicatorsProvider.getIndicator("EMA");
        signalEma = indicatorsProvider.getIndicator("EMA");
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex < getLookback()) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] highestHigh = new double[getLookback() - max.getLookback() + endIndex - startIndex + 1];
        max.setInputParameter(0, inputs[0][HIGH]);
        max.setOutputParameter(0, highestHigh);
        max.calculate(startIndex - getLookback() + max.getLookback(), endIndex);

        double[] lowestLow = new double[getLookback() - min.getLookback() + endIndex - startIndex + 1];
        min.setInputParameter(0, inputs[0][LOW]);
        min.setOutputParameter(0, lowestLow);
        min.calculate(startIndex - getLookback() + min.getLookback(), endIndex);

        double[] emaInput = new double[getLookback() - max.getLookback() + endIndex - startIndex + 1];
        for (int i = startIndex - getLookback() + max.getLookback(), j = 0; i <= endIndex; i++, j++) {
            if (highestHigh[j] == lowestLow[j]) {
                emaInput[j] = 0;
            } else {
                emaInput[j] = 100 * (inputs[0][CLOSE][i] - lowestLow[j]) / (highestHigh[j] - lowestLow[j]);
            }
        }

        double[] emaOutput = new double[max.getLookback() + ema.getLookback() + signalEma.getLookback() + endIndex - startIndex + 1];
        ema.setInputParameter(0, emaInput);
        ema.setOutputParameter(0, emaOutput);
        ema.calculate(ema.getLookback(), emaInput.length - 1);

        double[] maxEma = new double[ema.getLookback() + signalEma.getLookback() + endIndex - startIndex + 1];
        max.setInputParameter(0, emaOutput);
        max.setOutputParameter(0, maxEma);
        max.calculate(max.getLookback(), emaOutput.length - 1);

        double[] minEma = new double[ema.getLookback() + signalEma.getLookback() + endIndex - startIndex + 1];
        min.setInputParameter(0, emaOutput);
        min.setOutputParameter(0, minEma);
        min.calculate(min.getLookback(), emaOutput.length - 1);

        emaInput = new double[ema.getLookback() + signalEma.getLookback() + endIndex - startIndex + 1];
        for (int i = max.getLookback(), j = 0; i <= emaOutput.length - 1; i++, j++) {
            if (maxEma[j] == minEma[j]) {
                emaInput[j] = 0;
            } else {
                emaInput[j] = 100 * (emaOutput[i] - minEma[j]) / (maxEma[j] - minEma[j]);
            }
        }

        emaOutput = new double[signalEma.getLookback() + endIndex - startIndex + 1];
        ema.setInputParameter(0, emaInput);
        ema.setOutputParameter(0, emaOutput);
        ema.calculate(ema.getLookback(), emaInput.length - 1);

        System.arraycopy(emaOutput, signalEma.getLookback(), outputs[0], 0, endIndex - startIndex + 1);

        signalEma.setInputParameter(0, emaOutput);
        signalEma.setOutputParameter(0, outputs[1]);
        signalEma.calculate(signalEma.getLookback(), emaOutput.length - 1);

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
        inputs[index] = (double[][]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            max.setOptInputParameter(0, (Integer) value);
            min.setOptInputParameter(0, (Integer) value);
            break;
        case 1:
            ema.setOptInputParameter(0, (Integer) value);
            break;
        case 2:
            signalEma.setOptInputParameter(0, (Integer) value);
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
        return max.getLookback() + ema.getLookback() + max.getLookback() + ema.getLookback() + signalEma.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
