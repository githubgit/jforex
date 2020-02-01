package com.dukascopy.indicators;

import java.util.Arrays;

import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.Period;
import com.dukascopy.api.IIndicators.MaType;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class WallabyIndicator implements IIndicator{
    private IIndicator stochastic;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final IBar[][] inputs = new IBar[4][];
    private final double[][] outputs = new double[4][];

    private final Period[] periods = new Period[10];

    public void onStart(IIndicatorContext context) {
        stochastic = context.getIndicatorsProvider().getIndicator("STOCH");
        stochastic.setOptInputParameter(2, MaType.SMA.ordinal());
        stochastic.setOptInputParameter(4, MaType.SMA.ordinal());

        indicatorInfo = new IndicatorInfo("WALLABY", "Wallaby", "", false, false, false, 4, 6, 4);
        indicatorInfo.setSparseIndicator(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Main", InputParameterInfo.Type.BAR),
            new InputParameterInfo("FirstPeriod", InputParameterInfo.Type.BAR),
            new InputParameterInfo("SecondPeriod", InputParameterInfo.Type.BAR),
            new InputParameterInfo("ThirdPeriod", InputParameterInfo.Type.BAR)
        };

        inputParameterInfos[1].setPeriod(Period.ONE_MIN);
        inputParameterInfos[2].setPeriod(Period.FIVE_MINS);
        inputParameterInfos[3].setPeriod(Period.FIFTEEN_MINS);

        int[] periodValues = new int[10];
        String[] periodNames = new String[10];
        periodValues[0] = 0;
        periodNames[0] = "1 Min";
        periods[0] = Period.ONE_MIN;
        periodValues[1] = 1;
        periodNames[1] = "5 Mins";
        periods[1] = Period.FIVE_MINS;
        periodValues[2] = 2;
        periodNames[2] = "10 Mins";
        periods[2] = Period.TEN_MINS;
        periodValues[3] = 3;
        periodNames[3] = "15 Mins";
        periods[3] = Period.FIFTEEN_MINS;
        periodValues[4] = 4;
        periodNames[4] = "30 Mins";
        periods[4] = Period.THIRTY_MINS;
        periodValues[5] = 5;
        periodNames[5] = "Hourly";
        periods[5] = Period.ONE_HOUR;
        periodValues[6] = 6;
        periodNames[6] = "4 Hours";
        periods[6] = Period.FOUR_HOURS;
        periodValues[7] = 7;
        periodNames[7] = "Daily";
        periods[7] = Period.DAILY;
        periodValues[8] = 8;
        periodNames[8] = "Weekly";
        periods[8] = Period.WEEKLY;
        periodValues[9] = 9;
        periodNames[9] = "Monthly";
        periods[9] = Period.MONTHLY;

        optInputParameterInfos = new OptInputParameterInfo[] {
            new OptInputParameterInfo("First period", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(0 , periodValues, periodNames)),
            new OptInputParameterInfo("Second period", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(1 , periodValues, periodNames)),
            new OptInputParameterInfo("Third period", OptInputParameterInfo.Type.OTHER, new IntegerListDescription(3 , periodValues, periodNames)),
            new OptInputParameterInfo("Fast %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(5, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %K Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 1, 2000, 1)),
            new OptInputParameterInfo("Slow %D Period", OptInputParameterInfo.Type.OTHER, new IntegerRangeDescription(3, 1, 2000, 1))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("First Stochastic", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Second Stochastic", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Third Stochastic", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE),
            new OutputParameterInfo("Average", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.GRAY);
        outputParameterInfos[1].setColor(DefaultColors.GRAY);
        outputParameterInfos[2].setColor(DefaultColors.GRAY);
        outputParameterInfos[3].setColor(DefaultColors.RED);
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0);
        }

        Arrays.fill(outputs[0], Double.NaN);
        Arrays.fill(outputs[1], Double.NaN);
        Arrays.fill(outputs[2], Double.NaN);
        Arrays.fill(outputs[3], Double.NaN);

        int i;
        for (i = 1; i < inputs.length; i++){
            calculateStochastic(i, endIndex - startIndex + 1, startIndex);
        }

        for (i = 0; i < outputs[0].length; i++){
            outputs[3][i] = (outputs[0][i] + outputs[1][i] + outputs[2][i]) / 3;
        }

        return new IndicatorResult(startIndex, outputs[0].length);
    }

    private void calculateStochastic(int inputIndex, int elements, int startIndex){
         double[][] stochasticInput = new double[5][inputs[inputIndex].length];

         for (int i = 0; i < inputs[inputIndex].length; i++) {
             IBar bar = inputs[inputIndex][i];
             stochasticInput[0][i] = bar.getOpen();
             stochasticInput[1][i] = bar.getClose();
             stochasticInput[2][i] = bar.getHigh();
             stochasticInput[3][i] = bar.getLow();
             stochasticInput[4][i] = bar.getVolume();
         }

         if (stochasticInput[0].length - stochastic.getLookback() > 0) {
             double[] stochasticOutput = new double[stochasticInput[0].length - stochastic.getLookback()];
             stochastic.setInputParameter(0, stochasticInput);
             stochastic.setOutputParameter(0, stochasticOutput);
             stochastic.setOutputParameter(1, new double[stochasticInput[0].length - stochastic.getLookback()]);

             IndicatorResult stochasticResult = stochastic.calculate(0, stochasticInput[0].length - 1);

             int j = 0;
             for (int i = 0; i < elements; i++) {
                 IBar bar = inputs[0][i + startIndex];
                 long barTime = bar.getTime();

                 while (j < stochasticResult.getFirstValueIndex() + stochasticResult.getNumberOfElements() &&
                         inputs[inputIndex][j].getTime() < barTime) {
                     j++;
                 }

                 if (j >= stochasticResult.getFirstValueIndex() + stochasticResult.getNumberOfElements() || 
                         inputs[inputIndex][j].getTime() != barTime || j < stochasticResult.getFirstValueIndex()) {
                     outputs[inputIndex - 1][i] = Double.NaN;
                 } else {
                     outputs[inputIndex - 1][i] = stochasticOutput[j - stochasticResult.getFirstValueIndex()];
                 }
             }

         } else {
             for (int i = 0; i < elements; i++) {
                 outputs[inputIndex - 1][i] = Double.NaN;
             }
         }
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
        inputs[index] = (IBar[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        switch (index) {
        case 0:
            inputParameterInfos[1].setPeriod(periods[(Integer) value]);
            break;
        case 1:
            inputParameterInfos[2].setPeriod(periods[(Integer) value]);
            break;
        case 2:
            inputParameterInfos[3].setPeriod(periods[(Integer) value]);
            break;
        case 3:
            stochastic.setOptInputParameter(0, value);
            break;
        case 4:
            stochastic.setOptInputParameter(1, value);
            break;
        case 5:
            stochastic.setOptInputParameter(3, value);
            break;
        default:
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public void setOutputParameter(int index, Object array) {
        outputs[index] = (double[]) array;
    }

    public int getLookback() {
        return stochastic.getLookback();
    }

    public int getLookforward() {
        return 0;
    }
}
