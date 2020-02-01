package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dukascopy.api.DataType;
import com.dukascopy.api.DefaultColors;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.ICurrency;
import com.dukascopy.api.IDataService;
import com.dukascopy.api.IFXSentimentIndexBar;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.JFTimeZone;
import com.dukascopy.api.Period;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IMinMax;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.IntegerListDescription;
import com.dukascopy.api.indicators.IntegerRangeDescription;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;

public class SentimentIndexBarIndicator implements IIndicator, IDrawingIndicator, IMinMax {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;
    private OptInputParameterInfo[] optInputParameterInfos;
    private OutputParameterInfo[] outputParameterInfos;

    private IBar[] inputBars = new IBar[1];
    private final Object[][] sentimentOutputs = new Object[1][];

    private IIndicatorContext context;
    private IDataService dataService;
    private IHistory history;
    private IConsole console;

    private SentimentMode sentimentMode;
    private DataSource dataSource;
    private int maxBars;
    private Period sentimentPeriod;

    private final GeneralPath path = new GeneralPath();

    private static final int OPEN = 0;  
    private static final int HIGH = 1;
    private static final int LOW = 2;
    private static final int CLOSE = 3;

    private static final Period MIN_PERIOD = Period.THIRTY_MINS;

    public static int MAX_BARS_DEFAULT = 500; //to be accessed from strategies


    public enum SentimentMode {
        CHART_PERIOD(null), 
        THIRTY_MINS(Period.THIRTY_MINS), 
        ONE_HOUR(Period.ONE_HOUR), 
        FOUR_HOURS(Period.FOUR_HOURS), 
        DAILY(Period.DAILY), 
        WEEKLY(Period.WEEKLY);

        private final Period period;
        private static final List<Period> SENTIMENT_INDEX_PERIODS = new ArrayList<Period>(
                Arrays.asList(new Period[] {Period.THIRTY_MINS, Period.ONE_HOUR, Period.FOUR_HOURS, Period.DAILY, Period.WEEKLY}));
        
        private SentimentMode(Period period) {
            this.period = period;
        }

        public Period getPeriod(IIndicatorContext context) {
            Period chartPeriod = context.getFeedDescriptor().getPeriod();
            if (chartPeriod.getJFTimeZone() != JFTimeZone.UTC) {
                chartPeriod = Period.createCustomPeriod(chartPeriod.getUnit(), chartPeriod.getNumOfUnits(), JFTimeZone.UTC);
            }
            // use chart period for sentiment index if specified in opt inputs
            if (this == CHART_PERIOD) {
                return chartPeriod.compareTo(MIN_PERIOD) >= 0 && SENTIMENT_INDEX_PERIODS.contains(chartPeriod)
                        ? chartPeriod 
                        : MIN_PERIOD;
            }
            return chartPeriod.compareTo(period) > 0 && SENTIMENT_INDEX_PERIODS.contains(chartPeriod)
                    ? chartPeriod
                    : period;
        } 
    }
    
    public enum DataSource {
        INSTRUMENT, 
        PRIMARY_CURRENCY, 
        SECONDARY_CURRENCY
    }


    public void onStart(IIndicatorContext context) {
        indicatorInfo = new IndicatorInfo("SentimentBar", "Sentiment Index bar", "", false, false, false, 1, 3, 1);
        indicatorInfo.setRecalculateAll(true);
        indicatorInfo.setRecalculateOnNewCandleOnly(true);
        indicatorInfo.setSupportedDataTypes(DataType.TIME_PERIOD_AGGREGATION);

        inputParameterInfos = new InputParameterInfo[] {
            new InputParameterInfo("Price", InputParameterInfo.Type.BAR)
        };

        optInputParameterInfos = new OptInputParameterInfo[] { 
            new OptInputParameterInfo("Mode", OptInputParameterInfo.Type.OTHER,
                new IntegerListDescription(SentimentMode.CHART_PERIOD.ordinal(), getEnumValues(SentimentMode.class), getEnumNames(SentimentMode.class))),
            new OptInputParameterInfo("Source", OptInputParameterInfo.Type.OTHER,
                new IntegerListDescription(DataSource.INSTRUMENT.ordinal(), getEnumValues(DataSource.class), getEnumNames(DataSource.class))),
            new OptInputParameterInfo("Max bars", OptInputParameterInfo.Type.OTHER,
                new IntegerRangeDescription(MAX_BARS_DEFAULT, 100, 4000, 100))
        };

        outputParameterInfos = new OutputParameterInfo[] {
            new OutputParameterInfo("Sentiment Index", OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE)
        };

        outputParameterInfos[0].setColor(DefaultColors.OLIVE_DRAB);
        outputParameterInfos[0].setColor2(DefaultColors.RED);
        outputParameterInfos[0].setDrawnByIndicator(true);

        this.context = context;
        dataService = context.getDataService();
        history = context.getHistory();
        console = context.getConsole();
    }

    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }
        if (startIndex > endIndex || context.getFeedDescriptor().getDataType() != DataType.TIME_PERIOD_AGGREGATION) {
            return new IndicatorResult(startIndex, sentimentOutputs[0].length);
        }

        //calculate only the last 500 bars for performance reasons
        int startIndex2 = Math.max(0, inputBars.length - 1 - maxBars);
        
        long startTime = inputBars[startIndex2].getTime();
        long endTime = inputBars[inputBars.length - 1].getTime();
        List<IFXSentimentIndexBar> sentimentIndices;
        Instrument instrument = context.getFeedDescriptor().getInstrument();
        sentimentPeriod = sentimentMode.getPeriod(context);

        try {
            if (dataSource == DataSource.INSTRUMENT) {
                sentimentIndices = dataService.getFXSentimentIndex(instrument, sentimentPeriod,
                        history.getBarStart(sentimentPeriod, startTime), history.getBarStart(sentimentPeriod, endTime));
            } else {
                ICurrency currency = dataSource == DataSource.PRIMARY_CURRENCY 
                        ? instrument.getPrimaryJFCurrency() 
                        : instrument.getSecondaryJFCurrency();
                sentimentIndices = dataService.getFXSentimentIndex(currency, sentimentPeriod,
                     history.getBarStart(sentimentPeriod, startTime), history.getBarStart(sentimentPeriod, endTime));
            }

            if (sentimentIndices == null) {
                console.getErr().println("No sentiment indices fetched");
                return new IndicatorResult(startIndex, sentimentOutputs[0].length);
            }

            for (int in = startIndex2, out = startIndex2, si = 0; in <= endIndex && si < sentimentIndices.size(); in++, out++) {
                IFXSentimentIndexBar siBar = sentimentIndices.get(si);
                IBar inputBar = inputBars[in];

                // to sentiment indices filters are not applicable, so we skip the indices of which corresponding candles have been filtered
                long sentimentBarStart = history.getBarStart(sentimentPeriod, inputBar.getTime());
                while (siBar != null && siBar.getTime() < sentimentBarStart) {
                    si++;
                    siBar = (si < sentimentIndices.size() ? sentimentIndices.get(si) : null);
                }

                // assign the same bar basically if sentiment interval > chart interval
                sentimentOutputs[0][out] = siBar;
            }

        } catch (JFException e) {
            console.getErr().println(e);
        }

        return new IndicatorResult(startIndex, sentimentOutputs[0].length);
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
        inputBars = (IBar[]) array;
    }

    public void setOptInputParameter(int index, Object value) {
        if(index == 0){
            sentimentMode = SentimentMode.values()[(Integer) value];
        } else if(index == 1){
            dataSource = DataSource.values()[(Integer) value];
        } else if(index == 2){
            maxBars = (Integer) value;
        }
    }

    public void setOutputParameter(int index, Object array) {
        sentimentOutputs[index] = (Object[]) array;
    }

    public int getLookback() {
        return 0;
    }

    public int getLookforward() {
        return 0;
    }

    @Override
    public Point drawOutput(Graphics g, int outputIdx, Object values2, Color color, Stroke stroke, IIndicatorDrawingSupport support,
            List<Shape> shapes, Map<Color, List<Point>> handles) {

        Integer lastX = null, lastY = null;
        Color lastColor = color;
        Object[] values = (Object[]) values2;

        path.reset();

        int halfOfCandle = (int) (support.getCandleWidthInPixels() / 2);

        int handlesStep = 1;
        if (sentimentPeriod == MIN_PERIOD) {
            Period chartPeriod = support.getPeriod();
            double koef = Math.min((double) chartPeriod.getInterval() / sentimentPeriod.getInterval(), 1);
            handlesStep = Math.max((int) (support.getNumberOfCandlesOnScreen() * koef / 4), 1);
        }

        int firstCandle = support.getIndexOfFirstCandleOnScreen();
        int lastCandle = firstCandle + support.getNumberOfCandlesOnScreen() - 1;

        for (int i = 0, j = 0; i <= lastCandle; i++) {
            IFXSentimentIndexBar sentimentBar = (IFXSentimentIndexBar) values[i];
            if (sentimentBar == null) {
                continue;
            }

            int startIndex = i;
            //skip drawing on candles which have the same sentimentBar
            while(i + 1 < values.length && sentimentBar == values[i + 1]){
                i++;
            }
            if (i < firstCandle) {
                continue;
            }

            int[] candle = new int[] {
                    (int) support.getYForValue(sentimentBar.getOpen()),
                    (int) support.getYForValue(sentimentBar.getHigh()),
                    (int) support.getYForValue(sentimentBar.getLow()),
                    (int) support.getYForValue(sentimentBar.getClose())
            };

            int x1 = (int) support.getMiddleOfCandle(startIndex) - halfOfCandle;
            int x2 = (int) support.getMiddleOfCandle(i) + halfOfCandle;
            int middle = (x1 + x2) / 2;

            //draw line for 30-min sentiment index bars which are always flat
            if(sentimentPeriod == MIN_PERIOD){
                if(lastX != null && lastY != null){
                    int trend = Double.compare(lastY, candle[CLOSE]);
                    g.setColor(trend == -1 ? support.getDowntrendColor() : trend == 1 ? color : lastColor);
                    g.drawLine(lastX, lastY, middle, candle[CLOSE]);
                } else {
                    g.setColor(color);
                    g.drawLine(x1, candle[CLOSE], middle, candle[CLOSE]);
                    path.moveTo(x1, candle[CLOSE]);
                }
                lastX = middle;
                lastY = candle[CLOSE];
                lastColor = g.getColor();
                path.lineTo(lastX, lastY);

                if (j % handlesStep == handlesStep / 2) {
                    List<Point> points = handles.get(lastColor);
                    if (points == null) {
                        points = new ArrayList<>();
                        handles.put(lastColor, points);
                    }
                    points.add(new Point(lastX, lastY));
                }
                j++;

                if (i >= lastCandle || i + 1 < values.length && values[i + 1] == null) {
                    g.drawLine(lastX, lastY, x2, candle[CLOSE]);
                    lastX = x2;
                    lastY = candle[CLOSE];
                    path.lineTo(lastX, lastY);
                }

            //draw bars for other aggregations of more than one sentiment index value
            } else {
                g.setColor(candle[OPEN] < candle[CLOSE] ? support.getDowntrendColor() : color);
                g.fillRect(x1, Math.min(candle[OPEN], candle[CLOSE]), x2 - x1 + 1, Math.abs(candle[OPEN] - candle[CLOSE]) + 1);
                if (candle[HIGH] != candle[LOW]) {
                    g.drawLine(middle, candle[HIGH], middle, candle[LOW]);
                }

                path.moveTo(x1, candle[OPEN]);
                path.lineTo(x1, candle[CLOSE]);
                path.lineTo(x2, candle[CLOSE]);
                path.lineTo(x2, candle[OPEN]);
                path.closePath();
            }
        }

        shapes.add((Shape) path.clone());

        return null;
    }

    @Override
    public double[] getMinMax(int outputIdx, Object values2, int firstVisibleValueIndex, int lastVisibleValueIndex) {
        Object[] values = (Object[]) values2;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (Object val : values) {
            IFXSentimentIndexBar sentimentBar = (IFXSentimentIndexBar) val;
            if (sentimentBar == null) {
                continue;
            }
            if(sentimentBar.getHigh() > max){
               max = sentimentBar.getHigh();
            }
            if(sentimentBar.getLow() < min){
                min = sentimentBar.getLow();
            }
        }
        if(Double.compare(min, Double.MAX_VALUE) == 0 || Double.compare(max, Double.MIN_VALUE)== 0){
            return new double[] {0, 100}; // no values on chart
        }
        double buffer = (max - min) / 10;
        return new double[] { min - buffer , max + buffer };
    }

    private static String[] getEnumNames(Class<?> enumClass){
        List<String> names = new ArrayList<>();
        for(Field f : enumClass.getDeclaredFields()){
            if(f.isEnumConstant()){
                try {
                    names.add((f.get(null).toString()));
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return names.toArray(new String[names.size()]);
    }

    private static int[] getEnumValues(Class<?> enumClass){
        String[] names = getEnumNames(enumClass);
        int[] values = new int[names.length];
        for(int i=0; i<values.length; i++){
            values[i] = i;
        }
        return values;
    }
}
