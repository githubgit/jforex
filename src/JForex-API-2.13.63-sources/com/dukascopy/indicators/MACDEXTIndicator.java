package com.dukascopy.indicators;

import com.dukascopy.api.IIndicators;
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

/**
 * 
 * @author anatoly.pokusayev
 *
 */
public class MACDEXTIndicator implements IIndicator {

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;

    private final double[][] inputs = new double[1][];
    private final double[][] outputs = new double[3][];

    private int fastPeriod = 12;
    private int slowPeriod = 26;
    private int signalPeriod = 9;

    private int fastMaType = IIndicators.MaType.SMA.ordinal();
    private int slowMaType = IIndicators.MaType.SMA.ordinal();
    private int signalMaType = IIndicators.MaType.SMA.ordinal();

    private IIndicator fastMA;
    private IIndicator slowMA;
    private IIndicator signalMA;

    @Override
    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("MACDEXT", "MACD with controllable MA type", "Momentum Indicators", false, false, false, 1, 6, 3);

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
            new OptInputParameterInfo("Fast Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(fastPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Fast MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(fastMaType, maValues, maNames)),
            new OptInputParameterInfo("Slow Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(slowPeriod, 2, 2000, 1)),
            new OptInputParameterInfo("Slow MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(slowMaType, maValues, maNames)),
            new OptInputParameterInfo("Signal Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(signalPeriod, 1, 2000, 1)),
            new OptInputParameterInfo("Signal MAType", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(signalMaType, maValues, maNames))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("MACD", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("MACD Signal", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.DASH_LINE),
            new OutputParameterInfo("MACD Hist", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };

        IIndicatorsProvider indicatorsProvider = context.getIndicatorsProvider();
        fastMA = indicatorsProvider.getIndicator("MA");
        fastMA.setOptInputParameter(1, fastMaType);
        slowMA = indicatorsProvider.getIndicator("MA");
        slowMA.setOptInputParameter(1, slowMaType);
        signalMA = indicatorsProvider.getIndicator("MA");
        signalMA.setOptInputParameter(1, signalMaType);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        if( slowPeriod < fastPeriod ){
            int tempInteger = slowPeriod;
            slowPeriod = fastPeriod;
            fastPeriod = tempInteger;
            int tempMAType = slowMaType;
            slowMaType = fastMaType;
            fastMaType = tempMAType;
            
            fastMA.setOptInputParameter(0, fastPeriod);
            fastMA.setOptInputParameter(1, fastMaType);
            slowMA.setOptInputParameter(0, slowPeriod);
            slowMA.setOptInputParameter(1, slowMaType);
        }

        double[] fastMAOutput = new double[endIndex - startIndex + 1 + signalMA.getLookback()];
        fastMA.setInputParameter(0, inputs[0]);
        fastMA.setOutputParameter(0, fastMAOutput);
        double[] slowMAOutput = new double[endIndex  - startIndex + 1 + signalMA.getLookback()];
        slowMA.setInputParameter(0, inputs[0]);
        slowMA.setOutputParameter(0, slowMAOutput);

        fastMA.calculate(startIndex - signalMA.getLookback(), endIndex);
        IndicatorResult slowRes = slowMA.calculate(startIndex - signalMA.getLookback(), endIndex);

        double[] macd = new double[slowRes.getNumberOfElements()];
        int k;
        for (k = 0; k < slowRes.getNumberOfElements(); k++){
           macd[k] = fastMAOutput[k] - slowMAOutput[k];
        }

        double[] signalOutput = new double[endIndex - startIndex + 1];
        signalMA.setInputParameter(0, macd);
        signalMA.setOutputParameter(0, signalOutput);
        IndicatorResult signalRes = signalMA.calculate(0, slowRes.getNumberOfElements() - 1);

        System.arraycopy(macd, signalRes.getFirstValueIndex(), outputs[0], 0, signalRes.getNumberOfElements());

        for (k = 0; k < signalRes.getNumberOfElements(); k++){
            outputs[1][k] = signalOutput[k];
            outputs[2][k] = outputs[0][k] - signalOutput[k];
        }

        return new IndicatorResult(slowRes.getFirstValueIndex() + signalRes.getFirstValueIndex(), signalRes.getNumberOfElements());
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
            fastPeriod = (Integer) value;
            fastMA.setOptInputParameter(0, fastPeriod);
            break;
        case 1:
            fastMaType = (Integer) value;
            fastMA.setOptInputParameter(1, fastMaType);
            indicatorInfo.setUnstablePeriod(
                    fastMA.getIndicatorInfo().isUnstablePeriod() ||
                    slowMA.getIndicatorInfo().isUnstablePeriod() ||
                    signalMA.getIndicatorInfo().isUnstablePeriod());
            break;
        case 2:
             slowPeriod = (Integer) value;
             slowMA.setOptInputParameter(0, slowPeriod);
             break;
        case 3:
            slowMaType = (Integer) value;
            slowMA.setOptInputParameter(1, slowMaType);
            indicatorInfo.setUnstablePeriod(
                    fastMA.getIndicatorInfo().isUnstablePeriod() ||
                    slowMA.getIndicatorInfo().isUnstablePeriod() ||
                    signalMA.getIndicatorInfo().isUnstablePeriod());
            break;
        case 4:
             signalPeriod = (Integer) value;
             signalMA.setOptInputParameter(0, signalPeriod);
             break;
        case 5:
            signalMaType = (Integer) value;
            signalMA.setOptInputParameter(1, signalMaType);
            indicatorInfo.setUnstablePeriod(
                    fastMA.getIndicatorInfo().isUnstablePeriod() ||
                    slowMA.getIndicatorInfo().isUnstablePeriod() ||
                    signalMA.getIndicatorInfo().isUnstablePeriod());
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
        return Math.max(fastMA.getLookback(), slowMA.getLookback()) + signalMA.getLookback();
    }

    @Override
    public int getLookforward() {
        return 0;
    }
}
