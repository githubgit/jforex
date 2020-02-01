package com.dukascopy.indicators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dukascopy.api.DataType;
import com.dukascopy.api.IBar;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.OfferSide;
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

public class TicksVWAP implements IIndicator {
    private IIndicatorContext context;

    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private final IBar[][] inputs = new IBar[1][];
    private final double[][] outputs = new double[1][];

    private final IBar[][] prevInputs = new IBar[1][];
    private final double[][] prevOutputs = new double[1][];

    private IFeedDescriptor prevFeedDescriptor;
    private int prevInputStart;
    private int prevInputEnd;

    private static final long MAX_LOADING_PERIOD = 100_000_000; // ms

    @Override
    public void onStart(IIndicatorContext context) {
        this.context = context;

        indicatorInfo = new IndicatorInfo("TicksVWAP", "Volume Weighted Average Price", "", true, false, false, 1, 0, 1);
        indicatorInfo.setRecalculateAll(true);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Bars", InputParameterInfo.Type.BAR)
        };

        optInputParameterInfos = new OptInputParameterInfo[0];

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("VWAP", OutputParameterInfo.Type.DOUBLE, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setGapAtNaN(true);
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

        Arrays.fill(outputs[0], Double.NaN);

        IFeedDescriptor feedDescriptor = context.getFeedDescriptor();
        Instrument instrument = feedDescriptor.getInstrument();
        Period period = feedDescriptor.getPeriod();
        OfferSide offerSide = feedDescriptor.getOfferSide();
        DataType dataType = feedDescriptor.getDataType();

        List<int[]> gaps = new ArrayList<>();
        gaps.add(new int[] {startIndex, endIndex});
        boolean usePrevData = prevInputs[0] != null && prevOutputs[0] != null && feedDescriptor.equals(prevFeedDescriptor);

        // try to use previously calculated values
        for (int inputIndex = (usePrevData ? startIndex : endIndex), outputIndex = 0, prevInputIndex = prevInputStart, prevOutputIndex = 0;
                inputIndex <= endIndex; inputIndex++, outputIndex++) {

            boolean found = false;
            if (usePrevData) {
                while (prevInputIndex <= prevInputEnd &&
                        prevInputs[0][prevInputIndex].getTime() < inputs[0][inputIndex].getTime()) {
                    prevInputIndex++;
                    prevOutputIndex++;
                }
                if (prevInputIndex > prevInputEnd) {
                    usePrevData = false;
                } else if (prevInputs[0][prevInputIndex].getTime() == inputs[0][inputIndex].getTime()) {
                    found = true;
                    outputs[0][outputIndex] = prevOutputs[0][prevOutputIndex];
                }
            }

            boolean isGap = !found || Double.isNaN(outputs[0][outputIndex]);

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

        boolean callFromChart = (context.getIndicatorChartPanel() != null);

        try {
            int gapNum = gaps.size() - 1;

            while (gapNum >= 0) {
                int fromIndex = gaps.get(gapNum)[0];
                int toIndex = gaps.get(gapNum)[1];
                if (fromIndex > toIndex) {
                    gapNum--;
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
                    while (inputs[0][fromIndex].getTime() < fromTime && fromIndex < toIndex) {
                        fromIndex++;
                    }
                    fromTime = inputs[0][fromIndex].getTime();
                }
                if (fromTime > toTime) {
                    gapNum--;
                    continue;
                }

//                java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
//                System.out.println("loading data: " + dateFormat.format(new java.util.Date(fromTime)) +
//                        " - " + dateFormat.format(new java.util.Date(toTime)));
                
                List<ITick> ticks = context.getHistory().getTicks(instrument, fromTime, toTime);
                
//                System.out.println(ticks.size() + " : " + ticks);

                for (int i = fromIndex, j = fromIndex - startIndex, k = 0; i <= toIndex; i++, j++) {
                    while (k < ticks.size() && ticks.get(k).getTime() < inputs[0][i].getTime()) {
                        k++;
                    }

                    double sumPrice = 0.0, sumVolume = 0.0;
                    while (k < ticks.size() && (i + 1 >= inputs[0].length ||
                            ticks.get(k).getTime() < inputs[0][i + 1].getTime())) {
                        double price = (offerSide == OfferSide.ASK ? ticks.get(k).getAsk() : ticks.get(k).getBid());
                        double volume = (offerSide == OfferSide.ASK ? ticks.get(k).getAskVolume() : ticks.get(k).getBidVolume());
                        sumPrice += price * volume;
                        sumVolume += volume;
                        k++;
                    }

                    if (sumVolume == 0) {
                        outputs[0][j] = (j > 0 ? outputs[0][j - 1] : 0);
                    } else {
                        outputs[0][j] = sumPrice / sumVolume;
                    }
                }

                if (fromIndex > gaps.get(gapNum)[0]) {
                    if (callFromChart) {
                        break;
                    } else {
                        gaps.get(gapNum)[1] = fromIndex - 1;
                        continue;
                    }
                }

                gapNum--;
            }

        } catch (JFException ex) {
            context.getConsole().getErr().println(ex);
        }

        return new IndicatorResult(startIndex, endIndex - startIndex + 1, endIndex);
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
