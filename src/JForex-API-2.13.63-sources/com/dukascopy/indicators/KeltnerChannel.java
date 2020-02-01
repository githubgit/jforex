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
 * Date: Feb 26, 2010
 * Time: 10:29:12 AM
 */
public class KeltnerChannel implements IIndicator {

    private IIndicator ma;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final double[][][] inputs = new double[1][][];
    private final double[][] outputs = new double[3][];

    private int period = 10;

    public void onStart(IIndicatorContext context) {
        ma = context.getIndicatorsProvider().getIndicator("SMA");

        indicatorInfo = new IndicatorInfo("KELTNER", "Keltner Channel", "Overlap Studies", true, false, false, 1, 1, 3);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.PRICE)
        };

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(period, 2, 400, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Keltner Channel Up", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Keltner Channel Middle", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Keltner Channel Low", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.RED);
        outputParameterInfos[1].setColor(DefaultColors.ROYAL_BLUE);
        outputParameterInfos[2].setColor(DefaultColors.RED);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        double[] sma = new double[endIndex - startIndex + 2 + getLookback()];
        ma.setInputParameter(0, inputs[0][1]);
        ma.setOptInputParameter(0, period);
        ma.setOutputParameter(0, sma);
        IndicatorResult dSmaResult = ma.calculate(startIndex - 1, endIndex);

        int i, k;
        for (i = 1, k = dSmaResult.getNumberOfElements(); i < k; i++) {
            // middle
            outputs[1][i - 1] = sma[i];

            double avg = findAvg(i);
            outputs[0][i - 1] = sma[i] + avg;
            outputs[2][i - 1] = sma[i] - avg;
        }

        return new IndicatorResult(startIndex, i - 1);
    }

    private double findAvg(int shift){
        double sum = 0;
        for (int x = shift; x < (shift + period); x++) {
            sum += inputs[0][2][x] - inputs[0][3][x];
        }
        return (sum/period);
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
                period = (Integer) value;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
        ma.setOptInputParameter(0, period);
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return period + 1;
    }

    public int getLookforward() {
        return 0;
    }
}
