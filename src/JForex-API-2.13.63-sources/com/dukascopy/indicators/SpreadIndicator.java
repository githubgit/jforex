package com.dukascopy.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dukascopy.api.DataType;
import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.IFeedDescriptor;
import com.dukascopy.api.feed.IPriceAggregationBar;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

/**
 * Bar Spread Analyzer
 */
public class SpreadIndicator implements IIndicator {
    private IIndicatorContext context;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final double[][] outputs = new double[NUMBER_OF_OUTPUTS][];

    private final IBar[][] prevInputs = new IBar[1][];
    private final double[][] prevOutputs = new double[NUMBER_OF_OUTPUTS][];

    private IFeedDescriptor prevFeedDescriptor;
    private int prevInputStart;
    private int prevInputEnd;

    private static final long MAX_LOADING_PERIOD = 100_000_000; // ms
    private static final int NUMBER_OF_OUTPUTS = 3;

    @Override
    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("SPREAD", "Bar Spread Analyzer", "", false, false, false, 1, 0, NUMBER_OF_OUTPUTS);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bars", InputParameterInfo.Type.BAR)
        };

        optInputParameterInfos = new OptInputParameterInfo[0];

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Max", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Avg", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM),
            new OutputParameterInfo("Min", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.HISTOGRAM)
        };

        outputParameterInfos[0].setColor(DefaultColors.DARK_RED);
        outputParameterInfos[1].setColor(DefaultColors.DARK_ORANGE);
        outputParameterInfos[2].setColor(DefaultColors.FOREST_GREEN);
    }

    @Override
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (endIndex + getLookforward() > inputs[0].length - 1) {
            endIndex = inputs[0].length - 1 - getLookforward();
        }
        if (startIndex > endIndex) {
            return new IndicatorResult(0, 0, 0);
        }

        for (int i = 0; i < outputs.length; i++) {
            Arrays.fill(outputs[i], Double.NaN);
        }

        IFeedDescriptor feedDescriptor = context.getFeedDescriptor();
        Instrument instrument = feedDescriptor.getInstrument();
        Period period = feedDescriptor.getPeriod();
        DataType dataType = feedDescriptor.getDataType();

        List<int[]> gaps = new ArrayList<>();
        gaps.add(new int[] {startIndex, endIndex});
        boolean usePrevData = (prevInputs != null) && (prevOutputs != null) && feedDescriptor.equals(prevFeedDescriptor);

        // try to use previously calculated values
        for (int inputIndex = startIndex, outputIndex = 0, prevInputIndex = prevInputStart, prevOutputIndex = 0;
                inputIndex <= endIndex; inputIndex++, outputIndex++) {

            boolean found = false;
            if (usePrevData) {
                while ((prevInputIndex <= prevInputEnd) &&
                        (prevInputs[0][prevInputIndex].getTime() < inputs[0][inputIndex].getTime())) {
                    prevInputIndex++;
                    prevOutputIndex++;
                }
                if (prevInputIndex > prevInputEnd) {
                    usePrevData = false;
                } else if (prevInputs[0][prevInputIndex].getTime() == inputs[0][inputIndex].getTime()) {
                    found = true;
                    for (int outputSeries = 0; outputSeries < outputs.length; outputSeries++) {
                        outputs[outputSeries][outputIndex] = prevOutputs[outputSeries][prevOutputIndex];
                    }
                }
            }

            boolean isGap = !found;
            if (!isGap) {
                for (int outputSeries = 0; outputSeries < outputs.length; outputSeries++) {
                    if (Double.isNaN(outputs[outputSeries][outputIndex])) {
                        isGap = true;
                        break;
//                    } else if ((outputs[outputSeries][outputIndex] == 0.0) && (inputs[0][inputIndex].getVolume() != 0.0)) {
//                        // just in case, should not happen
//                        isGap = true;
//                        break;
                    }
                }
            }

            int[] lastGap = gaps.get(gaps.size() - 1);
            if (isGap) {
                if (inputIndex > lastGap[1]) {
                    if (lastGap[0] <= lastGap[1]) {
                        lastGap = new int[2];
                        gaps.add(lastGap);
                    }
                    lastGap[0] = inputIndex;
                    lastGap[1] = endIndex;
                } else if (inputIndex == lastGap[1] && inputIndex == endIndex && lastGap[0] < lastGap[1] &&
                        (dataType.equals(DataType.TIME_PERIOD_AGGREGATION) && period.getInterval() > MAX_LOADING_PERIOD / 2 ||
                        !dataType.equals(DataType.TIME_PERIOD_AGGREGATION) && !dataType.equals(DataType.TICKS))) {
                    // for long periods, select last bar into separate interval
                    // to allow progressive loading of previous bars
                    lastGap[1] = inputIndex - 1;
                    lastGap = new int[2];
                    gaps.add(lastGap);
                    lastGap[0] = inputIndex;
                    lastGap[1] = inputIndex;
                }
            } else {
                if (inputIndex <= lastGap[1]) {
                    lastGap[1] = inputIndex - 1;
                }
            }
        }

        prevFeedDescriptor = feedDescriptor;
        prevInputStart = startIndex;
        prevInputEnd = endIndex;

        try {
            for (int gapNum = gaps.size() - 1; gapNum >= 0; gapNum--) {
                int fromIndex = gaps.get(gapNum)[0];
                int toIndex = gaps.get(gapNum)[1];
                if (fromIndex > toIndex) {
                    continue;
                }

                long fromTime = inputs[0][fromIndex].getTime();
                long toTime;
                if (toIndex + 1 < inputs[0].length) {
                    toTime = inputs[0][toIndex + 1].getTime() - 1;
                } else {
                    if (dataType.equals(DataType.TIME_PERIOD_AGGREGATION) || dataType.equals(DataType.TICKS)) {
                        if (!period.isTickBasedPeriod()) {
                            toTime = context.getHistory().getNextBarStart(period, inputs[0][toIndex].getTime()) - 1;
                        } else {
                            toTime = inputs[0][toIndex].getTime() + Period.ONE_SEC.getInterval() - 1;
                        }
                        long lastTime = context.getHistory().getTimeOfLastTick(instrument);
                        if (toTime > lastTime) {
                            toTime = lastTime;
                            prevInputEnd--;
                        }
                    } else {
                        toTime = ((IPriceAggregationBar) inputs[0][toIndex]).getEndTime();
                        prevInputEnd--;
                    }
                }
                if (toTime - fromTime > MAX_LOADING_PERIOD) {
                    fromTime = toTime - MAX_LOADING_PERIOD;
                    if (dataType.equals(DataType.TIME_PERIOD_AGGREGATION) || dataType.equals(DataType.TICKS)) {
                        long interval = (!period.isTickBasedPeriod() ? period.getInterval() : 1000);
                        fromIndex = Math.max(toIndex - (int) Math.ceil((double) MAX_LOADING_PERIOD / interval) + 1, fromIndex);
                    }
                    while ((inputs[0][fromIndex].getTime() < fromTime) && (fromIndex < toIndex)) {
                        fromIndex++;
                    }
                    fromTime = inputs[0][fromIndex].getTime();
                }
                if (fromTime > toTime) {
                    continue;
                }

//                java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
//                System.out.println("loading data: " + dateFormat.format(new java.util.Date(fromTime)) +
//                        " - " + dateFormat.format(new java.util.Date(toTime)));

                List<ITick> ticks = context.getHistory().getTicks(instrument, fromTime, toTime);

//                System.out.println(ticks.size() + " : " + ticks);

                for (int i = fromIndex, j = fromIndex - startIndex, k = 0; i <= toIndex; i++, j++) {
                    while ((k < ticks.size()) && (ticks.get(k).getTime() < inputs[0][i].getTime())) {
                        k++;
                    }

                    double max = Double.NaN, min = Double.NaN, sum = 0.0;
                    int count = 0;
                    while ((k < ticks.size()) && ((i + 1 >= inputs[0].length) ||
                            (ticks.get(k).getTime() < inputs[0][i + 1].getTime()))) {
                        double spread = ticks.get(k).getAsk() - ticks.get(k).getBid();
                        if (Double.isNaN(max) || (max < spread)) {
                            max = spread;
                        }
                        if (Double.isNaN(min) || (min > spread)) {
                            min = spread;
                        }
                        sum += spread;
                        count++;
                        k++;
                    }
                    double avg = (count > 0 ? sum / count : Double.NaN);

                    outputs[0][j] = convertToPips(max, instrument);
                    outputs[1][j] = convertToPips(avg, instrument);
                    outputs[2][j] = convertToPips(min, instrument);
                }

//                for (int outputSeries = 0; outputSeries < outputs.length; outputSeries++) {
//                    System.out.println(Arrays.toString(Arrays.copyOfRange(outputs[outputSeries], fromIndex - startIndex, toIndex - startIndex + 1)));
//                }

                if (fromIndex > gaps.get(gapNum)[0]) {
                    break;
                }
            }

        } catch (JFException ex) {
            context.getConsole().getErr().println(ex);
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
    }

    private double convertToPips(double spread, Instrument instrument) {
        if (Double.isNaN(spread)) {
            return 0.0;
        }
        return Math.round(spread / instrument.getPipValue() * 10.0) / 10.0;
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
        prevInputs[index] = inputs[index];
        inputs[index] = (IBar[]) array;
    }

    @Override
    public void setOptInputParameter(int index, Object value) {
    }

    @Override
    public void setOutputParameter(int index, Object array) {
        prevOutputs[index] = outputs[index];
        outputs[index] = (double[]) array;
    }

    @Override
    public int getLookback() {
        return 0;
    }

    @Override
    public int getLookforward() {
        return (indicatorInfo.isRecalculateOnNewCandleOnly() ? 1 : 0);
    }
}
