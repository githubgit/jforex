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

/**
 * <b>NOTE: </b> The calculate logic of this indicator is implemented in JavaScript.
 * Please, update the corresponding JS code in case of updating of this class.
 * 
 * @author anatoly.pokusayev
 */
public class StochasticIndicator implements IIndicator {

    private static final int OPEN = 0;
    private static final int CLOSE = 1;
    private static final int HIGH = 2;
    private static final int LOW = 3;
    private static final int VOLUME = 4;

    private static final int PRICE_TYPE_LOWHIGH = 0;
    private static final int PRICE_TYPE_CLOSE = 1;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[2][];

    private int fastKPeriod = 5;
    private int slowKPeriod = 3;
    private int slowDPeriod = 3;
    private int priceType = PRICE_TYPE_LOWHIGH;

    private IIndicator slowKMa;
    private IIndicator slowDMa;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("STOCH", "Stochastic", "Momentum Indicators", false, false, false, 1, 6, 2);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        int[] maValues = new int[IIndicators.MaType.values().length];
        String[] maNames = new String[IIndicators.MaType.values().length];
        for (int i = 0; i < maValues.length; i++) {
            maValues[i] = i;
            maNames[i] = IIndicators.MaType.values()[i].name();
        }

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Fast %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastKPeriod, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowKPeriod, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %K MAType", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Slow %D Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowDPeriod, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %D MAType", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(IIndicators.MaType.SMA.ordinal(), maValues, maNames)),
            new OptInputParameterInfo("Price Type", OptInputParameterInfo.Type.OTHER,
                    new IntegerListDescription(priceType, new int[] {PRICE_TYPE_LOWHIGH, PRICE_TYPE_CLOSE}, new String[] {"Low/High", "Close/Close"}))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Slow %K", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("Slow %D", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE)
        };

        indicatorInfo.setDefaultLevelsInfo(new LevelInfo[] {
            new LevelInfo("", 20, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1),
            new LevelInfo("", 80, OutputParameterInfo.DrawingStyle.DASH_LINE, DefaultColors.GRAY, 1, 1)
        });

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        slowKMa = indicatorsProvider.getIndicator("MA");
        slowKMa.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
        slowDMa = indicatorsProvider.getIndicator("MA");
        slowDMa.setOptInputParameter(1, IIndicators.MaType.SMA.ordinal());
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        int maLookback = slowKMa.getLookback() + slowDMa.getLookback();
        double[] kInputs = new double[endIndex - startIndex + 1 + maLookback];
        double[] dmaOutput = new double[endIndex - startIndex + 1 + slowDMa.getLookback()];

        for (int i = startIndex - maLookback, j = 0; i <= endIndex; i++, j++) {
            double highestPrice = Double.NEGATIVE_INFINITY;
            double lowestPrice = Double.POSITIVE_INFINITY;

            for (int k = fastKPeriod - 1; k >= 0; k--) {
                double highPrice = (priceType == PRICE_TYPE_LOWHIGH ? inputs[0][HIGH][i - k] : inputs[0][CLOSE][i - k]);
                double lowPrice = (priceType == PRICE_TYPE_LOWHIGH ? inputs[0][LOW][i - k] : inputs[0][CLOSE][i - k]);

                highestPrice = (highPrice > highestPrice ? highPrice : highestPrice);
                lowestPrice = (lowPrice < lowestPrice ? lowPrice : lowestPrice);
            }

            if (highestPrice - lowestPrice == 0) {
                kInputs[j] = 0;
            } else {
                kInputs[j] = 100 * ((inputs[0][CLOSE][i] - lowestPrice) / (highestPrice - lowestPrice));
            }
        }

        slowKMa.setInputParameter(0, kInputs);
        slowKMa.setOutputParameter(0, dmaOutput);
        IndicatorResult kResult = slowKMa.calculate(0, kInputs.length - 1);

        if (kResult.getNumberOfElements() == 0) {
            return new IndicatorResult(0, 0);
        }

        slowDMa.setInputParameter(0, dmaOutput);
        slowDMa.setOutputParameter(0, outputs[1]);
        IndicatorResult dResult = slowDMa.calculate(0, kResult.getNumberOfElements() - 1);

        if (dResult.getNumberOfElements() == 0) {
            return new IndicatorResult(0, 0);
        }

        System.arraycopy(dmaOutput, dResult.getFirstValueIndex(), outputs[0], 0, dResult.getNumberOfElements());

        return new IndicatorResult(kResult.getFirstValueIndex() + dResult.getFirstValueIndex() + (fastKPeriod - 1), dResult.getNumberOfElements());
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
            fastKPeriod = (Integer) value;
            break;
        case 1:
            slowKPeriod = (Integer) value;
            slowKMa.setOptInputParameter(0, slowKPeriod);
            break;
        case 2:
            int slowKMaType = (Integer) value;
            slowKMa.setOptInputParameter(1, slowKMaType);
            indicatorInfo.setUnstablePeriod(
                    slowKMa.getIndicatorInfo().isUnstablePeriod() ||
                    slowDMa.getIndicatorInfo().isUnstablePeriod());
            break;
        case 3:
             slowDPeriod = (Integer) value;
             slowDMa.setOptInputParameter(0, slowDPeriod);
             break;
        case 4:
            int slowDMaType = (Integer) value;
            slowDMa.setOptInputParameter(1, slowDMaType);
            indicatorInfo.setUnstablePeriod(
                    slowKMa.getIndicatorInfo().isUnstablePeriod() ||
                    slowDMa.getIndicatorInfo().isUnstablePeriod());
            break;
        case 5:
            priceType = (Integer) value;
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
        return slowDMa.getLookback() + slowKMa.getLookback() + (fastKPeriod - 1);
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
