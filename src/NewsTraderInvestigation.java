import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IChart;
import com.dukascopy.api.IConsole;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IEngine;
import com.dukascopy.api.IHistory;
import com.dukascopy.api.IIndicators;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IOrder;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.IUserInterface;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Library;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.drawings.IChartObjectFactory;
import com.dukascopy.api.drawings.IVerticalLineChartObject;

public class NewsTraderInvestigation implements IStrategy {

    public static final double MEANINGFUL = 30;
    private CopyOnWriteArrayList<TradeEventAction> tradeEventActions = new CopyOnWriteArrayList<>();
    private static final String DATE_FORMAT_NOW = "yyyyMMdd_HHmmss";
    private IEngine engine;
    private IConsole console;
    private IHistory history;
    private IContext context;
    private IIndicators indicators;
    private IUserInterface userInterface;

    @Configurable("Instruments")
    public String Instruments = "EUR/USD,AUD/USD,GBP/USD,USD/CAD,USD/CHF,NZD/USD,USD/JPY";
    private String[] instruments = { "USD/CAD", "AUD/USD", "GBP/USD", "EUR/USD", "USD/CHF", "NZD/USD", "USD/JPY" };
    private int RETRIES = 7;

    @Configurable("defaultInstrument")
    public Instrument defaultInstrument = Instrument.EURUSD;
    @Configurable("defaultSlippage")
    public int defaultSlippage = 0;
    @Configurable("defaultPeriod")
    public Period defaultPeriod = Period.ONE_MIN;
    @Configurable("News Test filename")
    public String filename = "/Users/vlad/Downloads/1970-01-01-2020-01-31.txt";// "/Users/vlad/Downloads/2008_2018_tillJune05_Investing.com.html";
    @Configurable("CSV filename:")
    public String destFileName = "/Users/vlad/Downloads/2005-01-01-2020-01-31/";
    @Configurable("showNewsOnChart")
    public boolean showNewsOnChart = false;
    @Configurable("Trade (true) or Investigate (false)")
    public boolean TradeOrInvestigate = true;
    @Configurable("SPREAD to trade, pips") // for market orders only
    public double SPREAD = 2;
    @Configurable("Seconds before news to enter")
    public long secondsBeforeNews = 20;
    @Configurable("Cancel after seconds")
    public long cancellAfterSeconds = 140;
    @Configurable("Market Orders")
    public boolean marketOrders = false;
    @Configurable("Pending Orders")
    public boolean usePendingOrders = false;
    @Configurable("Bid/Offer Orders")
    public boolean useBidOfferOrders = true;
    @Configurable("Number of stop orders")
    public int numberOfOrders = 23;
    @Configurable("Number of Candles")
    public int numberOfCandles = 1;
    @Configurable("Initial Gap, Points")
    public int GAP = 90;
    @Configurable("Step between orders, Points")
    public int STEP = 30;
    @Configurable("Take Profit, Points")
    public int TP = 150;
    @Configurable("Stop Loss, Points")
    public int SL = 20;
    @Configurable("Move To Breakeven")
    public boolean moveToBreakeven = true;
    @Configurable("Move to breakeven level")
    public int lockLevel = 90;
    @Configurable("Points to lock:")
    public int lockPoints = 60;
    @Configurable("Risk in every trade, % ")
    public double RISK = 0.5;
    @Configurable("Close Opposite:")
    public boolean CloseOpposite = true;
    @Configurable("Close on New Event:")
    public boolean closeOnNewEvent = true;
    @Configurable("debug:")
    public boolean debug = false;

    private String AccountId = "";
    private double Equity;

    private String AccountCurrency = "";
    private int OverWeekendEndLeverage;
    private double Leverage;
    private List<IOrder> PendingPositions = null;
    private List<IOrder> ActiveOrders = null;
    private double UseofLeverage;
    private IMessage LastTradeEvent = null;
    private boolean GlobalAccount;
    private Candle LastAskCandle = null;
    private HashMap<String, Candle> LastAskCandleMap = new HashMap<>();
    private Candle LastBidCandle = null;
    private HashMap<String, Candle> LastBidCandleMap = new HashMap<>();
    private Tick LastTick = null;
    private HashMap<String, Tick> LastTickMap = new HashMap<>();

    private int MarginCutLevel;
    private List<IOrder> AllPositions = null;

    private IChart chart;
    private HashMap<String, IChart> IChartMap = new HashMap<>();

    private AllInOne main;
    private long ONE_HOUR = 1000 * 60 * 60;// milliseconds in 1 hour

    @Override
    public void onStart(IContext context) throws JFException {
        instruments = Instruments.split(",");
        this.engine = context.getEngine();
        this.console = context.getConsole();
        this.history = context.getHistory();
        this.context = context;
        this.indicators = context.getIndicators();
        this.userInterface = context.getUserInterface();

        for (String sInstrument : instruments) {
            Instrument instrument = Instrument.fromString(sInstrument);
            subscriptionInstrumentCheck(instrument);

            ITick lastITick = context.getHistory().getLastTick(instrument);

            LastTick = new Tick(lastITick, instrument);
            LastTickMap.put(sInstrument, LastTick);

            IBar bidBar = context.getHistory().getBar(instrument, defaultPeriod, OfferSide.BID, 1);
            IBar askBar = context.getHistory().getBar(instrument, defaultPeriod, OfferSide.ASK, 1);
            LastAskCandle = new Candle(askBar, defaultPeriod, instrument, OfferSide.ASK);
            LastAskCandleMap.put(sInstrument, LastAskCandle);
            LastBidCandle = new Candle(bidBar, defaultPeriod, instrument, OfferSide.BID);
            LastBidCandleMap.put(sInstrument, LastBidCandle);

            chart = context.getChart(instrument);

            /*
             * if(chart==null){ IFeedDescriptor feedDescriptor = new
             * TimePeriodAggregationFeedDescriptor(instrument, defaultPeriod,
             * OfferSide.BID); chart = context.openChart(feedDescriptor); }
             */

            if (chart != null) {
                IChartMap.put(sInstrument, chart);
            }
        }

        main = new AllInOne();
        try {
            main.readnPlotNews();
        } catch (Throwable e) {
            throw new JFException(e.getMessage(), e);
        }
    }

    @Override
    public void onStop() throws JFException {
        if (!TradeOrInvestigate) {
            main.writeInvestigationsToCSV();
        }
        /*
         * for(IChart chart: IChartMap.values()){ context.closeChart(chart); }
         */
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
        AccountCurrency = account.getCurrency().toString();
        Leverage = account.getLeverage();
        AccountId = account.getAccountId();
        Equity = account.getEquity();
        UseofLeverage = account.getUseOfLeverage();
        OverWeekendEndLeverage = account.getOverWeekEndLeverage();
        MarginCutLevel = account.getMarginCutLevel();
        GlobalAccount = account.isGlobal();
    }

    private void updateVariables(Instrument instrument) {
        try {
            AllPositions = engine.getOrders();
            List<IOrder> listPending = new ArrayList<>();
            List<IOrder> listActive = new ArrayList<>();
            for (IOrder order : AllPositions) {
                if (order.getState().equals(IOrder.State.OPENED)) {
                    listPending.add(order);
                } else if (order.getState().equals(IOrder.State.FILLED)) {
                    listActive.add(order);
                }

            }

            PendingPositions = listPending;
            ActiveOrders = listActive;

        } catch (JFException e) {
            e.printStackTrace();
        }
    }

    public void closeOppositePendingOrders(IMessage message, IOrder order) throws JFException {
        if (message.getType() == IMessage.Type.ORDER_FILL_OK) {

            boolean closeLong = !order.isLong();

            for (IOrder pendingOrder : PendingPositions) {

                if (order.isLong() && !pendingOrder.isLong()) {
                    pendingOrder.close();
                } else if (!order.isLong() && pendingOrder.isLong()) {
                    pendingOrder.close();
                }
            }
        }
    }

    private void closeOpenOrders() throws JFException {

        for (int i = 0; i < ActiveOrders.size(); i++) {
            IOrder ord = ActiveOrders.get(i);
            ord.close();
        }
        ActiveOrders.clear();
    }

    private IOrder mergeOrders(IOrder... orders) throws JFException {
        IOrder mergedOrder = engine.mergeOrders("mergedOrder", orders);
        IMessage message = mergedOrder.waitForUpdate(2, java.util.concurrent.TimeUnit.SECONDS);
        // we have received either MESSAGE_MERGE_OK or MESSAGE_MERGE_REJECTED
        console.getOut().println("Message after merge: " + message.getType() + " - " + message);
        // order is FILLED/CLOSED on successful merge with amount > 0

        return mergedOrder;
    }

    private void removeTakeProfitStopLoss(IOrder... orders) throws JFException {

        // remove sl and tp attached orders if any
        for (IOrder o : orders) {
            if (Double.compare(o.getStopLossPrice(), 0) != 0) {
                o.setStopLossPrice(0);
                console.getOut().println(o.getLabel() + " remove stop loss.");
                o.waitForUpdate(1000);
            }
            if (Double.compare(o.getTakeProfitPrice(), 0) != 0) {
                o.setTakeProfitPrice(0);
                console.getOut().print(o.getLabel() + " remove take profit.");
                o.waitForUpdate(1000);
            }
        }
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        if (message.getOrder() != null) {
            updateVariables(message.getOrder().getInstrument());

            IOrder order = message.getOrder();
            // if(debug)
            // console.getOut().println("onMessage messageType=" +
            // message.getType() );

            if (message.getType() == IMessage.Type.ORDER_FILL_OK) {
                // if tp, ls for opened order is zero
                setOpenedOrderTP_SL(order);
            }

            if (CloseOpposite)
                closeOppositePendingOrders(message, order);

            LastTradeEvent = message;
            for (TradeEventAction event : tradeEventActions) {
                if (order != null && event != null && message.getType().equals(event.getMessageType())
                        && order.getLabel().equals(event.getPositionLabel())) {
                    Method method;
                    try {
                        method = this.getClass().getDeclaredMethod(event.getNextBlockId(), Integer.class);
                        method.invoke(this, new Integer[] { event.getFlowId() });
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    tradeEventActions.remove(event);
                }
            }
        }
    }

    public void setOpenedOrderTP_SL(IOrder order) throws JFException {
        double sl = 0, tp = 0;

        if (order.getTakeProfitPrice() == 0) {

            if (order.isLong()) {
                sl = round(order.getOpenPrice() - order.getInstrument().getPipValue() * SL / 10, order.getInstrument());
                tp = round(order.getOpenPrice() + order.getInstrument().getPipValue() * TP / 10, order.getInstrument());
            } else {
                sl = round(order.getOpenPrice() + order.getInstrument().getPipValue() * SL / 10, order.getInstrument());
                tp = round(order.getOpenPrice() - order.getInstrument().getPipValue() * TP / 10, order.getInstrument());
            }

            order.setTakeProfitPrice(tp);

            if (debug)
                console.getOut().println("Order setup: " + order.getId() + " TP=" + tp);
        }

        if (order.getStopLossPrice() == 0) {
            if (order.isLong()) {
                sl = round(order.getOpenPrice() - order.getInstrument().getPipValue() * SL / 10, order.getInstrument());
            } else {
                sl = round(order.getOpenPrice() + order.getInstrument().getPipValue() * SL / 10, order.getInstrument());
            }

            order.setStopLossPrice(sl);// order.setStopLossPrice(price, side,
                                        // trailingStep);

            if (debug)
                console.getOut().println("Order setup: " + order.getId() + " SL=" + sl);
        }
    }

    private static boolean investigation = false;

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        LastTick = new Tick(tick, instrument);

        LastTickMap.put(instrument.getName(), LastTick);

        updateVariables(instrument);

        // here it is - On Tick start point
        if (!TradeOrInvestigate) {
            main.investigate();

        } else {
            main.trading();
            main.breakEven();
        }
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        if (!period.equals(defaultPeriod)) {
            return;
        }
        LastAskCandle = new Candle(askBar, period, instrument, OfferSide.ASK);
        LastBidCandle = new Candle(bidBar, period, instrument, OfferSide.BID);

        LastAskCandleMap.put(instrument.getName(), LastAskCandle);
        LastBidCandleMap.put(instrument.getName(), LastBidCandle);

        updateVariables(instrument);

        long lastTickTime = history.getLastTick(instrument).getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(lastTickTime);

        if (calendar.get(Calendar.HOUR_OF_DAY) == 23 && calendar.get(Calendar.MINUTE) == 57)
            closeOpenOrders();

        if (marketOrders)
            checkAndLockProfit(askBar, bidBar);

        if (!TradeOrInvestigate && !investigation) {
            main.investigateNewApproach();
            investigation = true;
        }
    }

    private void checkAndLockProfit(IBar askBar, IBar bidBar) throws JFException {
        for (IOrder ord : ActiveOrders) {

            Instrument instrument = ord.getInstrument();
            Tick lastTick = this.LastTickMap.get(instrument.getName());

            if (ord.getProfitLossInPips() > 0) {

                if (ord.isLong()) {

                    if (bidBar.getClose() - bidBar.getOpen() < 0) {
                        ord.close();
                    }
                } else {

                    if (askBar.getClose() - askBar.getOpen() > 0) {
                        ord.close();
                    }
                }
            }
        }
    }

    public void subscriptionInstrumentCheck(Instrument instrument) {
        try {
            if (!context.getSubscribedInstruments().contains(instrument)) {
                Set<Instrument> instruments = new HashSet<>();
                instruments.add(instrument);
                context.setSubscribedInstruments(instruments, true);
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public double round(double price, Instrument instrument) {
        BigDecimal big = new BigDecimal("" + price);
        big = big.setScale(instrument.getPipScale() + 1, BigDecimal.ROUND_HALF_UP);
        return big.doubleValue();
    }

    public ITick getLastTick(Instrument instrument) {
        try {
            return (context.getHistory().getTick(instrument, 0));
        } catch (JFException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isMeaningful(List<NewsEvent> events) {
        boolean result = false;

        for (NewsEvent event : events) {
            if (isMeaningful(event)) {
                for (NewsEvent e : events) {
                    if (event != e && e.getChangePips() == null && event.getPair().contentEquals(e.getPair())) {
                        e.setPreviousCandleChangePips(event.getPreviousCandleChangePips());
                        e.setChangePips(event.getChangePips());
                    }
                }
                result = true;
                break;
            }
        }

        return result;
    }

    public static boolean isMeaningful(NewsEvent event) {
        return event.getChangePips() != null && (event.getChangePips() >= MEANINGFUL || event.getChangePips() <= -1 * MEANINGFUL);
    }

    public static String toString(NewsEvent mainEvent, List<NewsEvent> list) {
        // TODO replace all commas to be a dot: 1,12345 -> 1.12345
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.#");

        java.text.DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        java.text.DecimalFormat pf = new java.text.DecimalFormat("#0.0");
        pf.setDecimalFormatSymbols(dfs);

        String sr = ", ";
        String line = sdf.format(mainEvent.dateTime) + sr;
        line += mainEvent.currency + sr;
        line += mainEvent.importance.substring(0, 1) + sr;

        line = eventLine(df, line, mainEvent);

        for (NewsEvent event : list) {
            if (event != mainEvent) {
                line = eventLine(df, line, event);
            }
        }

        line += sr;
        line += mainEvent.pair + sr;
        line += (mainEvent.changePercentage != null ? df.format(mainEvent.changePercentage) + "%" : "-") + sr;
        line += (mainEvent.previousCandleChangePips != null ? df.format(mainEvent.previousCandleChangePips) : "-") + sr;
        line += (mainEvent.changePips != null ? df.format(mainEvent.changePips) : "-") + sr;

        return line;
    }

    private static String eventLine(java.text.DecimalFormat df, String line, NewsEvent event) {
        line += "(" + event.getCurrency() + " " + event.title + " "
                + (event.changePercentage != null ? df.format(event.changePercentage) + "%" : "-") + ")";
        return line;
    }

    public IOrder OpenOrder(IEngine.OrderCommand command, Instrument instrument, double lot, double dPrice, double dStopLoss,
            double dTakeProfit, long goodTillTime, String comment) {
        // ITick tick = getLastTick(instrument);

        double stopLoss = round(dStopLoss, instrument);
        double takeProfit = round(dTakeProfit, instrument);
        double price = round(dPrice, instrument);
        String label = getLabel();
        int retries = 0;
        IOrder order = null;

        try {
            // console.getOut().println("Trying to open order...");
            order = context.getEngine().submitOrder(label, instrument, command, lot, price, defaultSlippage, stopLoss, takeProfit,
                    goodTillTime, comment);
            // wait max 100 milliseconds for OPENED
            IMessage message = order.waitForUpdate(200, TimeUnit.MILLISECONDS);
            // console.getOut().println("message.getType=" + message.getType());

            // resubmit order on rejection
            while (message.getType() == IMessage.Type.ORDER_SUBMIT_REJECTED && retries < RETRIES) {
                retries++;
                // console.getOut().println("Trying to open order... retry " +
                // retries);
                order = context.getEngine().submitOrder(label, instrument, command, lot, price, defaultSlippage, stopLoss, takeProfit,
                        goodTillTime, comment);
                // wait max 100 milliseconds for OPENED
                message = order.waitForUpdate(200, TimeUnit.MILLISECONDS);
                // console.getOut().println("Submit Order message.getType()=" +
                // message.getType());
            }

            if (debug) {
                if (message.getType() == IMessage.Type.ORDER_SUBMIT_OK) {
                    console.getOut().println("Submit Order message.getType()=" + command.toString());
                } else {
                    console.getOut().println("Order Rejected " + command.toString() + " price:" + price + " ");
                }
            }

        } catch (JFException e) {
            console.getErr().println(e.getMessage());
        }

        return order;
    }

    class Candle {

        IBar bar;
        Period period;
        Instrument instrument;
        OfferSide offerSide;

        public Candle(IBar bar, Period period, Instrument instrument, OfferSide offerSide) {
            this.bar = bar;
            this.period = period;
            this.instrument = instrument;
            this.offerSide = offerSide;
        }

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }

        public Instrument getInstrument() {
            return instrument;
        }

        public void setInstrument(Instrument instrument) {
            this.instrument = instrument;
        }

        public OfferSide getOfferSide() {
            return offerSide;
        }

        public void setOfferSide(OfferSide offerSide) {
            this.offerSide = offerSide;
        }

        public IBar getBar() {
            return bar;
        }

        public void setBar(IBar bar) {
            this.bar = bar;
        }

        public long getTime() {
            return bar.getTime();
        }

        public double getOpen() {
            return bar.getOpen();
        }

        public double getClose() {
            return bar.getClose();
        }

        public double getLow() {
            return bar.getLow();
        }

        public double getHigh() {
            return bar.getHigh();
        }

        public double getVolume() {
            return bar.getVolume();
        }
    }

    class Tick {

        private ITick tick;
        private Instrument instrument;

        public Tick(ITick tick, Instrument instrument) {
            this.instrument = instrument;
            this.tick = tick;
        }

        public Instrument getInstrument() {
            return instrument;
        }

        public double getAsk() {
            return tick.getAsk();
        }

        public double getBid() {
            return tick.getBid();
        }

        public double getAskVolume() {
            return tick.getAskVolume();
        }

        public double getBidVolume() {
            return tick.getBidVolume();
        }

        public long getTime() {
            return tick.getTime();
        }

        public ITick getTick() {
            return tick;
        }
    }

    protected String getLabel() {
        String label;
        label = "IVF" + getCurrentTime(LastTick.getTime()) + generateRandom(10000) + generateRandom(10000);
        return label;
    }

    private String getCurrentTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(time);
    }

    private static String generateRandom(int n) {
        int randomNumber = (int) (Math.random() * n);
        String answer = "" + randomNumber;
        if (answer.length() > 3) {
            answer = answer.substring(0, 4);
        }
        return answer;
    }

    class TradeEventAction {
        private IMessage.Type messageType;
        private String nextBlockId = "";
        private String positionLabel = "";
        private int flowId = 0;

        public IMessage.Type getMessageType() {
            return messageType;
        }

        public void setMessageType(IMessage.Type messageType) {
            this.messageType = messageType;
        }

        public String getNextBlockId() {
            return nextBlockId;
        }

        public void setNextBlockId(String nextBlockId) {
            this.nextBlockId = nextBlockId;
        }

        public String getPositionLabel() {
            return positionLabel;
        }

        public void setPositionLabel(String positionLabel) {
            this.positionLabel = positionLabel;
        }

        public int getFlowId() {
            return flowId;
        }

        public void setFlowId(int flowId) {
            this.flowId = flowId;
        }
    }

    class AllInOne {
        private String[] symbols = { "USD", "EUR", "GBP", "CAD", "CHF", "NZD", "JPY", "AUD" };
        private String[] pairs = { "USD/CAD", "EUR/USD", "GBP/USD", "USD/CAD", "USD/CHF", "NZD/USD", "USD/JPY", "AUD/USD" };
        private HashMap<String, String> pairsMap = new HashMap<>();

        private List<NewsEvent> newsEvents;
        private Map<String, List<NewsEvent>> byTitleMap = new LinkedHashMap<>();
        private Map<String, List<NewsEvent>> byPairMap = new LinkedHashMap<>();
        private Map<Long, List<NewsEvent>> byTimeMap = new LinkedHashMap<>();

        private List<NewsEvent> newsSortedByTime = null;
        private final long ONE_MINUTE_IN_MILLIS = 60000;
        private List<NewsEvent> workingEvents = new LinkedList<>();

        private void initPairsMap() {
            for (int i = 0; i < symbols.length; i++) {
                pairsMap.put(symbols[i], pairs[i]);
            }
        }

        private boolean isSummerDST(Date newsTime) {
            boolean isSummer = false;

            int month = newsTime.getMonth() + 1;
            int day = newsTime.getDate();
            int dayOfWeek = newsTime.getDay();

            if (dayOfWeek == 0)
                dayOfWeek = 7;

            if (month > 3 && month < 10)
                isSummer = true;
            else if (month > 10 || month < 3)
                isSummer = false;
            else if (month == 3 && 31 - day + dayOfWeek >= 7) {
                if (31 - day + dayOfWeek >= 7)
                    isSummer = false;
                else
                    isSummer = true;
            } else if (month == 10) {
                if (31 - day + dayOfWeek < 7)
                    isSummer = false;
                else
                    isSummer = true;
            }

            return isSummer;
        }

        private void correctDaylightSaving(NewsEvent event) {

            long newsTime = event.getDateTime().getTime();

            if (isSummerDST(event.getDateTime())) {
                // adding daylight saving +1 summer hour
                newsTime += 3 * ONE_HOUR;
                event.setDateTime(new Date(newsTime));
            } else {
                newsTime += 2 * ONE_HOUR; // UTC+2 EET
                event.setDateTime(new Date(newsTime));
            }
        }

        public void readnPlotNews() throws java.text.ParseException, JFException {

            initPairsMap();

            if (debug)
                console.getOut().println("Initialization of Pairs Map - OK");

            ParseInvestingHTML newsParser = new ParseInvestingHTML();

            List<NewsEvent> list = newsParser.parse(filename);

            console.getOut().println(" Total NewsEvents: " + list.size());
            ArrayList<NewsEvent> allNewsEvents = new ArrayList<>();

            for (NewsEvent event : list) {

                String pair = pairsMap.get(event.getCurrency());

                if (pair == null)
                    continue;// it might be more currencies than expected

                event.setPair(pair);
                // use only news for the pair from strategy instruments
                // Filtering by instrument
                boolean shouldAdd = false;
                for (String sInstrument : instruments) {

                    if (pair.equals(sInstrument)) {
                        // if (debug)
                        // console.getOut().println("Event added for " + pair +
                        // " Event:" + event.toString());

                        shouldAdd = true;
                        break;
                    }
                }

                if (!shouldAdd)
                    continue;

                // filtering by time - avoid old news
                correctDaylightSaving(event);

                if (TradeOrInvestigate) {// while trading avoid old news
                    if (!(LastTick.getTime() < event.getDateTime().getTime()))
                        continue;
                }

                String key = event.getCurrency() + " " + event.getTitle();
                List<NewsEvent> byTitleList = byTitleMap.get(key);

                if (byTitleList == null) {
                    byTitleList = new ArrayList<>();
                }

                byTitleList.add(event);
                byTitleMap.put(key, byTitleList);

                long timeKey = event.getDateTime().getTime();

                List<NewsEvent> byTimeList = byTimeMap.get(timeKey);
                if (byTimeList == null) {
                    byTimeList = new ArrayList<>();
                }
                byTimeList.add(event);
                byTimeMap.put(timeKey, byTimeList);

                List<NewsEvent> byPairList = byPairMap.get(pair);
                if (byPairList == null) {
                    byPairList = new ArrayList<>();
                }

                byPairList.add(event);
                allNewsEvents.add(event);

                // put news event to the charts
                if (showNewsOnChart) {
                    IChart chart = IChartMap.get(pair);

                    if (chart != null) {
                        Date dateTime = event.getDateTime();
                        IChartObjectFactory factory = chart.getChartObjectFactory();
                        IVerticalLineChartObject newsLine = factory.createVerticalLine(event.toString(), dateTime.getTime());
                        newsLine.setColor(java.awt.Color.RED);
                        newsLine.setMenuEnabled(false);
                        newsLine.setShowLabel(true);
                        newsLine.setText(event.toString());
                        chart.add(newsLine);
                    }
                }
            }

            if (!TradeOrInvestigate)
                console.getOut().println(
                        "News events was successfully processed and separated by currency+title for investigations: " + list.size());

            String instrumentsNamesStr = "";
            for (String instrumentName : instruments) {
                instrumentsNamesStr += instrumentName + " ";
            }

            console.getOut().println("Added " + allNewsEvents.size() + " (events for " + instrumentsNamesStr + " after "
                    + new Date(LastTick.getTime()) + ")");
            this.newsEvents = allNewsEvents;
            System.out.println();

        }

        private void sortNewsByTime() {
            if (newsSortedByTime == null) {
                newsSortedByTime = new LinkedList<>();
                newsSortedByTime.addAll(newsEvents);
                Collections.sort(newsSortedByTime);
                if (debug)
                    console.getOut().println("sortNewsByTime() - OK ");
            }
        }

        LinkedHashMap<String, List<NewsEvent>> workingNewsMap = null;

        private boolean collectWorkingEventsForTrading() throws JFException {
            if (workingEvents.size() == 0) {
                // looking for working news events ( or several events on the
                // same time)
                for (NewsEvent event : newsSortedByTime) {
                    Tick lastTick = LastTickMap.get(event.getPair());

                    if (event.getDateTime().getTime() > lastTick.getTime()
                            && event.getDateTime().getTime() - lastTick.getTime() <= secondsBeforeNews * 1000
                    /*
                     * event.getDateTime().getTime() < lastTick.getTime() &&
                     * lastTick.getTime() - event.getDateTime().getTime() <=
                     * secondsBeforeNews * 1000
                     */) {
                        // collecting upcoming news events secondsBeforeNews
                        // seconds before news release
                        workingEvents.add(event);
                        if (debug)
                            if (debug)
                                console.getOut().println("Trading news release: " + event.toString());
                    } else if (lastTick.getTime() > event.getDateTime().getTime()) {
                        // stop iterating when next news event in the past
                        // already
                        // to speed up processing
                        break;
                    }
                }
            }

            workingNewsMap = new LinkedHashMap<>();

            for (NewsEvent event : workingEvents) {
                List<NewsEvent> newsByPair = workingNewsMap.get(event.getPair());
                if (newsByPair == null) {
                    newsByPair = new ArrayList<>();
                }
                newsByPair.add(event);
                workingNewsMap.put(event.getPair(), newsByPair);
            }

            if (closeOnNewEvent && workingNewsMap.size() > 0) {
                closeOpenOrders();
            }

            return workingEvents.size() > 0;
        }

        public void trading() throws JFException {
            // TODO
            // 1. Sort by news release time (several news could be on the same
            // time, but for different instruments or even the same instrument)
            // 2. Wait onTick data beforeSeconds before news release for
            // upcoming news event/events
            // 3. On news release time minus beforeSeconds open stop orders
            // 4. On session end or another news event close open orders

            sortNewsByTime();
            // when market orders - waiting for breakout to open opders and
            // clear working news/working news map

            if (collectWorkingEventsForTrading()) {

                if (debug)
                    console.getOut().println("Working events found: " + workingEvents.size() + "  - news release time: "
                            + workingEvents.get(0).getDateTime());

                for (Iterator<Map.Entry<String, List<NewsEvent>>> it = workingNewsMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, List<NewsEvent>> entry = it.next();
                    String pair = entry.getKey();
                    Tick lastTick = LastTickMap.get(pair);

                    long prevBarTime = history.getPreviousBarStart(Period.ONE_MIN, lastTick.getTime());

                    Instrument instrument = Instrument.fromString(pair);
                    int INVESTIGATE_BARS = numberOfCandles;
                    // getting 1 min bars from history
                    List<IBar> barsBid = history.getBars(instrument, Period.ONE_MIN, OfferSide.BID,
                            history.getTimeForNBarsBack(Period.ONE_MIN, prevBarTime, INVESTIGATE_BARS), prevBarTime);
                    List<IBar> barsAsk = history.getBars(instrument, Period.ONE_MIN, OfferSide.BID,
                            history.getTimeForNBarsBack(Period.ONE_MIN, prevBarTime, INVESTIGATE_BARS), prevBarTime);

                    double priceMin = Double.MAX_VALUE, priceMax = Double.MIN_VALUE;

                    if (barsAsk.size() > 0) {
                        // looking min/max price 5 min after news release time

                        for (IBar bar : barsAsk) {
                            if (bar.getHigh() > priceMax)
                                priceMax = bar.getHigh();
                        }
                    }

                    if (barsBid.size() > 0) {
                        // looking min/max price 5 min after news release time
                        for (IBar bar : barsBid) {
                            if (bar.getLow() < priceMin)
                                priceMin = bar.getLow();
                        }
                    }

                    if (debug)
                        console.getOut().println("Preparing orders for news event on: " + pair);

                    if (marketOrders) {

                        // in case of market orders break-out might be absent -
                        // so remove working news on secondsAfterNews time
                        List<NewsEvent> events = entry.getValue();
                        NewsEvent event = events.get(0);
                        long cancelBleakoutWaitingTime = event.getDateTime().getTime() + 1000 * cancellAfterSeconds;

                        if (lastTick.getTime() < cancelBleakoutWaitingTime) {

                            // open market orders on break-out
                            if (isSpreadOK(instrument, lastTick)) {
                                boolean breakoutHappened = checkBreakoutAndOpenOrders(instrument, priceMin, priceMax);

                                if (breakoutHappened) {
                                    newsSortedByTime.removeAll(events);
                                    workingEvents.removeAll(events);
                                    it.remove();
                                }
                            }

                        }
                    } else {

                        if (useBidOfferOrders) {
                            placeBidOfferOrders(instrument, priceMin, priceMax);
                        } else if (usePendingOrders) {
                            openPendingOrders(instrument, priceMin, priceMax);
                        }

                        List<NewsEvent> events = workingNewsMap.get(pair);
                        newsSortedByTime.removeAll(events);
                        workingEvents.removeAll(events);
                    }
                }

                // removing processed news events from workingEvents list &
                // newsSortedByTime to speed up processing
                if (!marketOrders) {
                    workingNewsMap.clear();
                    workingEvents.clear();
                }
            }
        }

        public boolean isSpreadOK(Instrument instrument, Tick lastTick) {
            double spreadPip = (lastTick.getAsk() - lastTick.getBid()) / instrument.getPipValue();
            boolean result = spreadPip <= SPREAD;

            if (!result && debug)
                console.getOut().println("Spread is too high");

            return result;
        }

        public void breakEven() throws JFException {
            if (moveToBreakeven)
                if (ActiveOrders.size() > 0) {

                    for (IOrder order : ActiveOrders) {
                        double lockLevelPips = lockLevel / 10;

                        if (order.getProfitLossInPips() >= lockLevelPips) {

                            double open = order.getOpenPrice();
                            double breakEven;

                            if (order.isLong()) {
                                breakEven = open + lockPoints / 10 * order.getInstrument().getPipValue();
                            } else {
                                breakEven = open - lockPoints / 10 * order.getInstrument().getPipValue();
                            }

                            breakEven = round(breakEven, order.getInstrument());

                            if (order.getStopLossPrice() != breakEven)
                                order.setStopLossPrice(breakEven);
                        }
                    }
                }
        }

        public void placeBidOfferOrders(Instrument instrument, double priceMin, double priceMax) throws JFException {
            Tick lastTick = LastTickMap.get(instrument.getName());

            double gapInPrice = round(GAP / 10 * instrument.getPipValue(), instrument);
            double stepInPrice = round(STEP / 10 * instrument.getPipValue(), instrument);
            double initialPriceLevelForSell = priceMin - gapInPrice;
            double initialPriceLevelForBuy = priceMax + gapInPrice;

            if (debug)
                console.getOut().println("priceMin=" + priceMin + " priceMax=" + priceMax + " initialPriceSell=" + initialPriceLevelForSell
                        + " initialPriceBuy=" + initialPriceLevelForBuy);

            double lot, stopLossPrice, takeProfitPrice;

            IEngine.OrderCommand command;

            long cancelTime = lastTick.getTime() + 1000 * cancellAfterSeconds;

            for (int i = 0; i < numberOfOrders; i++) {
                double priceSell = initialPriceLevelForSell - (i * stepInPrice);
                stopLossPrice = priceSell + (SL / 10 * lastTick.getInstrument().getPipValue());
                takeProfitPrice = priceSell - (TP / 10 * lastTick.getInstrument().getPipValue());
                command = IEngine.OrderCommand.PLACE_OFFER;
                lot = getPositionSize(instrument, priceSell, stopLossPrice, command);

                if (debug)
                    console.getOut().println("Sell lot=" + lot + " priceSell=" + round(priceSell, instrument));

                OpenOrder(command, instrument, lot, priceSell, 0, 0, cancelTime, "OFFER_" + (i + 1));

                double priceBuy = initialPriceLevelForBuy + (i * stepInPrice);
                stopLossPrice = priceBuy - (SL / 10 * lastTick.getInstrument().getPipValue());
                takeProfitPrice = priceBuy + (TP / 10 * lastTick.getInstrument().getPipValue());
                command = IEngine.OrderCommand.PLACE_BID;
                lot = getPositionSize(instrument, priceBuy, stopLossPrice, command);

                if (debug)
                    console.getOut().println("Buy lot=" + lot + " priceBuy=" + round(priceBuy, instrument));

                OpenOrder(command, instrument, lot, priceBuy, 0, 0, cancelTime, "BID_" + (i + 1));
            }

        }

        public boolean checkBreakoutAndOpenOrders(Instrument instrument, double priceMin, double priceMax) throws JFException {
            // open Sell/Buy STOP orders
            Tick lastTick = LastTickMap.get(instrument.getName());

            double gapInPrice = round(GAP / 10 * instrument.getPipValue(), instrument);
            double stepInPrice = round(STEP / 10 * instrument.getPipValue(), instrument);
            double initialPriceLevelForSell = priceMin - gapInPrice;
            double initialPriceLevelForBuy = priceMax + gapInPrice;

            double lot, stopLossPrice, takeProfitPrice;

            IEngine.OrderCommand command;

            boolean breakout = false;
            long cancelTime = lastTick.getTime() + 1000 * cancellAfterSeconds;

            if (lastTick.getAsk() >= initialPriceLevelForBuy) {
                // Up breakout - open buy
                breakout = true;
                stopLossPrice = initialPriceLevelForSell;
                command = IEngine.OrderCommand.BUY;
                lot = getPositionSize(instrument, lastTick.getAsk(), stopLossPrice, command);

                // if(debug)
                console.getOut().println(instrument.getName() + " Up breakout - open Buy lot=" + lot + " open price=" + lastTick.getAsk());

                OpenOrder(command, instrument, lot, lastTick.getAsk(), 0, 0, 0, "UpBreakoutBUY");

            } else if (lastTick.getBid() <= initialPriceLevelForSell) {
                // Down breakout - open sell
                breakout = true;
                stopLossPrice = initialPriceLevelForBuy;
                command = IEngine.OrderCommand.SELL;
                lot = getPositionSize(instrument, lastTick.getBid(), stopLossPrice, command);

                // if(debug)
                console.getOut()
                        .println(instrument.getName() + " Down breakout - open Sell lot=" + lot + " open price=" + lastTick.getBid());

                OpenOrder(command, instrument, lot, lastTick.getBid(), 0, 0, 0, "DownBreakoutSell");
            }

            return breakout;
        }

        public void openPendingOrders(Instrument instrument, double priceMin, double priceMax) throws JFException {
            // open Sell & Buy STOP orders
            Tick lastTick = LastTickMap.get(instrument.getName());

            double gapInPrice = round(GAP / 10 * instrument.getPipValue(), instrument);
            double stepInPrice = round(STEP / 10 * instrument.getPipValue(), instrument);
            double initialPriceLevelForSell = priceMin - gapInPrice;
            double initialPriceLevelForBuy = priceMax + gapInPrice;

            double lot, stopLossPrice, takeProfitPrice;

            IEngine.OrderCommand command;

            long cancelTime = lastTick.getTime() + 1000 * cancellAfterSeconds;

            // open buy stop orders
            command = IEngine.OrderCommand.BUYSTOP;

            for (int i = 0; i < numberOfOrders; i++) {
                double priceBuy = initialPriceLevelForBuy + (i * stepInPrice);
                stopLossPrice = priceBuy - (SL / 10 * lastTick.getInstrument().getPipValue());
                takeProfitPrice = priceBuy + (TP / 10 * lastTick.getInstrument().getPipValue());

                lot = getPositionSize(instrument, priceBuy, stopLossPrice, command);

                if (debug)
                    console.getOut().println("Buy lot=" + lot + " priceBuy=" + priceBuy);

                OpenOrder(command, instrument, lot, priceBuy, stopLossPrice, takeProfitPrice, cancelTime,
                        "BUYSTOP_" + (i + 1) + " requested price:" + priceBuy);
            }

            // open sell stop orders
            command = IEngine.OrderCommand.SELLSTOP;

            for (int i = 0; i < numberOfOrders; i++) {
                double priceSell = initialPriceLevelForSell - (i * stepInPrice);
                stopLossPrice = priceSell + (SL / 10 * lastTick.getInstrument().getPipValue());
                takeProfitPrice = priceSell - (TP / 10 * lastTick.getInstrument().getPipValue());

                lot = getPositionSize(instrument, priceSell, stopLossPrice, command);

                if (debug)
                    console.getOut().println("Sell lot=" + lot + " priceSell=" + priceSell);

                OpenOrder(command, instrument, lot, priceSell, stopLossPrice, takeProfitPrice, cancelTime,
                        "SELLSTOP_" + (i + 1) + " requested price:" + priceSell);
            }

        }

        private boolean isLongOrder(IEngine.OrderCommand orderCommand) {
            return orderCommand == IEngine.OrderCommand.BUYSTOP || orderCommand == IEngine.OrderCommand.BUYSTOP_BYBID
                    || orderCommand == IEngine.OrderCommand.BUYLIMIT || orderCommand == IEngine.OrderCommand.BUYLIMIT_BYBID
                    || orderCommand == IEngine.OrderCommand.BUY || orderCommand == IEngine.OrderCommand.PLACE_BID;
        }

        private double getPositionSize(Instrument instrument, double entryPrice, double stopLossPrice, IEngine.OrderCommand orderCmd)
                throws JFException {

            String accountCurrency = context.getAccount().getCurrency().getCurrencyCode();
            String primaryCurrency = instrument.getPrimaryCurrency().getCurrencyCode();
            String secondaryCurrency = instrument.getSecondaryCurrency().getCurrencyCode();
            double maxLossInAccountCurrency = RISK / 100 * Equity * Leverage / 1000;

            // get exchange rate of traded pair in relation to account currency
            double accountCurrencyExchangeRate;
            String apCurrency = accountCurrency + "/" + primaryCurrency;
            Instrument i;

            if (primaryCurrency.equals(accountCurrency)) {
                i = instrument;
            } else {
                i = Instrument.fromString(apCurrency);
            }

            if (i == null) { // currency not found, try inverted pair
                i = Instrument.fromInvertedString(apCurrency);
                if (isLongOrder(orderCmd))
                    accountCurrencyExchangeRate = 1 / history.getLastTick(i).getAsk();
                else
                    accountCurrencyExchangeRate = 1 / history.getLastTick(i).getBid();
            } else {
                if (isLongOrder(orderCmd))
                    accountCurrencyExchangeRate = history.getLastTick(i).getAsk();
                else
                    accountCurrencyExchangeRate = history.getLastTick(i).getBid();
            }

            // calc currency/pip value
            double pairExchangeRate;

            if (isLongOrder(orderCmd))
                pairExchangeRate = history.getLastTick(instrument).getAsk();
            else
                pairExchangeRate = history.getLastTick(instrument).getBid();
            double accountCurrencyPerPip = instrument.getPipValue() / pairExchangeRate * 100000;

            if (!primaryCurrency.equals(accountCurrency))
                // convert to account pip value
                accountCurrencyPerPip /= accountCurrencyExchangeRate;

            // calc stop loss pips
            double stopLossPips;
            if (isLongOrder(orderCmd)) {
                stopLossPips = Math.abs(stopLossPrice - entryPrice) * Math.pow(10, instrument.getPipScale());
            } else {
                stopLossPips = Math.abs(stopLossPrice - entryPrice) * Math.pow(10, instrument.getPipScale());
            }

            // position size
            double units = maxLossInAccountCurrency / stopLossPips * 100000 / accountCurrencyPerPip;

            // converting to standard lots
            double lots = units / 1000000;

            if (lots < 0.001) {
                // 1000 USD is minimum lot in Ducascopy
                lots = 0.001;
            } else if (lots > Equity * Leverage / 1000000) {
                lots = Equity * Leverage / 1000000;
            }

            return lots;
        }

        // this method should be called on every tick
        public void investigate() throws JFException {
            // FIXME idea - investigate speed fix
            // start investigate at the end of the investigation period
            // walk by news events one by one
            // take the time of news release directly from news
            // +1 min from that time and get two ONE_MIN candles back in time

            // 1. Sort by news release time (several news could be on the same
            // time, but for different instruments or even the same instrument)
            // FIXME speed up idea // 2. Wait onTick data 2 min after news
            // release for upcoming news
            // event/events
            // 3. On news release +1 min take 2 bars back for investigation
            // 4. Save news change, start price , min/max price after news
            // release to NewsEvent object
            // 5. onStop - save all news infos by it's currency/title to the CSV
            // file

            sortNewsByTime();

            // old approach
            /*
             * if (workingEvents.size() == 0) { // looking for working news
             * events ( or several events on the // same time) for (NewsEvent
             * event : newsSortedByTime) { Tick lastTick =
             * LastTickMap.get(event.getPair());
             * 
             * // 5 second tolerance long tolerance = 5000; long
             * minutesAfterNewsRelease = 5; long prevBarTime =
             * history.getPreviousBarStart(Period.ONE_MIN, lastTick.getTime());
             * // collecting working news events minutesAfterNewsRelease if
             * (prevBarTime > event.getDateTime().getTime() && (prevBarTime -
             * event.getDateTime().getTime()) <= (minutesAfterNewsRelease *
             * ONE_MINUTE_IN_MILLIS) && (prevBarTime -
             * event.getDateTime().getTime()) >= (minutesAfterNewsRelease *
             * ONE_MINUTE_IN_MILLIS) - tolerance) { workingEvents.add(event); }
             * else if (event.getDateTime().getTime() > lastTick.getTime()) { //
             * stop iterating when next news event more that 1 min // ahead //
             * to speed up processing break; } } }
             * 
             * if (workingEvents.size() > 0) { if (debug)
             * console.getOut().println("Working events found: " +
             * workingEvents.size() + "  - news release time: " +
             * workingEvents.get(0).getDateTime());
             * 
             * for (NewsEvent event : workingEvents) { if (event.actual != null
             * && event.previous != null) { double change = (event.actual -
             * event.previous) / event.previous ((event.previous < 0) ? -1 : 1);
             * event.setChange(change); // calculating change in %
             * event.setChangePercentage(change * 100); }
             * 
             * Tick lastTick = LastTickMap.get(event.getPair());
             * 
             * long prevBarTime = history.getPreviousBarStart(Period.ONE_MIN,
             * lastTick.getTime());
             * 
             * Instrument instrument = Instrument.fromString(event.getPair());
             * int INVESTIGATE_BARS = 2; // getting 2 ONE_MIN bars from history
             * List<IBar> bars = history.getBars(instrument, Period.ONE_MIN,
             * OfferSide.BID, history.getTimeForNBarsBack(Period.ONE_MIN,
             * prevBarTime, INVESTIGATE_BARS), prevBarTime);
             * 
             * double priceMin = Double.MAX_VALUE, priceMax = Double.MIN_VALUE;
             * double previousCandleChangePips = 0; if (bars.size() > 0) { //
             * looking start price and price change 1 min after news Pips //
             * release time closed bar
             * event.setStartPrice(bars.get(0).getClose());
             * previousCandleChangePips = (bars.get(0).getClose() -
             * bars.get(0).getOpen()) * Math.pow(10, instrument.getPipScale());
             * bars.remove(0); double changePips = 0;
             * 
             * for (IBar bar : bars) { if (bar.getLow() < priceMin) priceMin =
             * bar.getLow(); if (bar.getHigh() > priceMax) priceMax =
             * bar.getHigh(); changePips = (bar.getClose() -
             * event.getStartPrice()) * Math.pow(10, instrument.getPipScale());
             * } event.setChangePips(changePips);
             * event.setPreviousCandleChangePips(previousCandleChangePips); }
             * 
             * if (debug) console.getOut().println("Working news event found: "
             * + event); } // removing processed news events from workingEvents
             * list & // newsSortedByTime to speed up processing
             * newsSortedByTime.removeAll(workingEvents); workingEvents.clear();
             * }
             */

        }

        public void investigateNewApproach() throws JFException {
            main.sortNewsByTime();
            // new approach
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date startDate;
            try {
                // start date for instruments could be from 01/01/2005
                startDate = sdf.parse("01/01/2004 00:00:00");
            } catch (ParseException e) {
                throw new JFException(e);
            }

            for (NewsEvent event : newsSortedByTime) {
                // avoid very old news
                if (event.getDateTime().getTime() < startDate.getTime())
                    continue;

                if (event.getPrevious() != null && event.getPrevious() != 0 && event.getActual() != null && event.getActual() != 0) {

                    // Analyze only meaningful news

                    double change = (event.actual - event.previous) / event.previous * ((event.previous < 0) ? -1 : 1);
                    event.setChange(change);
                    // calculating change in %
                    event.setChangePercentage(change * 100);

                    // bar on news time
                    int minutesAfterEvent = 2;
                    long endBarTime = history.getPreviousBarStart(Period.ONE_MIN,
                            event.getDateTime().getTime() + minutesAfterEvent * this.ONE_MINUTE_IN_MILLIS);

                    Instrument instrument = Instrument.fromString(event.getPair());
                    int INVESTIGATE_BARS = minutesAfterEvent + 1; // +1 bar
                                                                    // before
                                                                    // news to
                                                                    // analyze
                                                                    // start
                                                                    // price

                    long startBarTime = history.getTimeForNBarsBack(Period.ONE_MIN, endBarTime, INVESTIGATE_BARS);
                    List<IBar> bars = history.getBars(instrument, Period.ONE_MIN, OfferSide.BID, startBarTime, endBarTime);

                    // if (debug)
                    // console.getOut().println("event time:" +
                    // sdf.format(event.getDateTime())+ " start bar time: " +
                    // sdf.format(new Date(startBarTime))+" end bar time:
                    // "+sdf.format(new Date(endBarTime)));

                    double priceMin = Double.MAX_VALUE, priceMax = Double.MIN_VALUE;
                    double previousCandleChangePips = 0;

                    if (bars.size() > 0) {
                        // looking start price and price change
                        // minutesAfterEvent min after news, Pips
                        // release time closed bar
                        event.setStartPrice(bars.get(0).getClose());
                        previousCandleChangePips = (bars.get(0).getClose() - bars.get(0).getOpen())
                                * Math.pow(10, instrument.getPipScale());
                        bars.remove(0);
                        Double changePips = null;

                        for (IBar bar : bars) {
                            if (bar.getLow() < priceMin)
                                priceMin = bar.getLow();
                            if (bar.getHigh() > priceMax)
                                priceMax = bar.getHigh();
                        }

                        if (Math.abs(priceMax - event.getStartPrice()) > Math.abs(event.getStartPrice() - priceMin)) {
                            // gone up
                            changePips = (priceMax - event.getStartPrice()) * Math.pow(10, instrument.getPipScale());

                        } else {
                            // gone down
                            changePips = (priceMin - event.getStartPrice()) * Math.pow(10, instrument.getPipScale());
                        }

                        event.setChangePips(changePips);
                        event.setPreviousCandleChangePips(previousCandleChangePips);

                    } else {
                        console.getOut().println("ERROR: Can't get corrects bars, date: " + event.getDateTime());
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        public void writeInvestigationsToCSV() {

            for (String key : byTitleMap.keySet()) {

                List<NewsEvent> events = byTitleMap.get(key);
                /// NewsEvent eve = events.get(0);
                // investigating only USD/CAD for now
                // if (!eve.getCurrency().equals("USD") &&
                // !eve.getCurrency().equals("CAD"))
                // continue;

                // sorting by change in %
                Collections.sort(events, new Comparator<NewsEvent>() {
                    @Override
                    public int compare(NewsEvent event1, NewsEvent event2) {
                        if (event1 != null && event2 != null) {

                            if (event1.getChangePips() != null && event2.getChangePips() != null) {
                                Double eve1Pips = Math.abs(event1.getChangePips());
                                Double eve2Pips = Math.abs(event2.getChangePips());
                                return eve2Pips.compareTo(eve1Pips);
                            }
                            else
                                return 1;
                        } else
                            return 1;
                    }
                });

                String fullName = destFileName + key + ".csv";
                Path path = Paths.get(fullName);

                try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
                    for (NewsEvent event : events) {
                        List<NewsEvent> sameTimeList = byTimeMap.get(event.getDateTime().getTime());

                        if (sameTimeList.size() == 1) {
                            if (NewsTraderInvestigation.isMeaningful(event)) {
                                writer.write(event.toString() + "\n");
                            }
                        } else {
                            // several news events at the same time
                            //if (NewsTraderInvestigation.isMeaningful(sameTimeList)) {
                            //    writer.write(NewsTraderInvestigation.toString(event, sameTimeList) + "\n");
                            //}
                            if (NewsTraderInvestigation.isMeaningful(sameTimeList)) {
                                // main news event goes first
                                writer.write(event.toString() + "\n");

                                for (NewsEvent sameTimeEvent : sameTimeList) {
                                    if (sameTimeEvent == event)
                                        continue;
                                    else
                                        writer.write(sameTimeEvent.toString() + "\n");
                                }
                            }
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                try {
                    FileChannel file = FileChannel.open(path);

                    if (file.size() == 0) {
                        path.toFile().delete();
                    }

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    @Library("jsoup-1.11.2.jar")
    class ParseInvestingHTML {

        public List<NewsEvent> parse(String filename) throws JFException {
            List<NewsEvent> list = new ArrayList<>();
            File input = new File(filename);

            try {

                Document doc = Jsoup.parse(input, "UTF-8", "");

                Element tbody = doc.body().tagName("tbody");
                Element timeZone = tbody.selectFirst("#timeZoneGmtOffsetFormatted");
                console.getOut().println("timeZone = " + timeZone.text());
                int timeZoneOffsetHours = 0;

                if (timeZone != null) {
                    String sTimeZoneOffsetHours = timeZone.text().replace("GMT", "").replace("(", "").replace(")", "").replace(" ", "")
                            .replace(":00", "");

                    if (sTimeZoneOffsetHours.length() > 0)
                        timeZoneOffsetHours = Integer.parseInt(sTimeZoneOffsetHours);
                    if (debug)
                        console.getOut().println("Parser: news source timeZoneOffset=" + timeZoneOffsetHours);
                }

                Elements events = tbody.select("tr.js-event-item");
                if (debug)
                    console.getOut().println("Starting News events creation...");

                for (Element event : events) {
                    String newsDateTime = event.attr("data-event-datetime");
                    int dateSpace = newsDateTime.indexOf(Constants.SPACE);
                    String date = newsDateTime.substring(0, dateSpace);
                    String time = newsDateTime.substring(dateSpace + 1, newsDateTime.lastIndexOf(":"));
                    String currency = event.selectFirst("td.flagCur").text().trim();
                    String importance = event.selectFirst("td.sentiment").attr("title");
                    String title = event.selectFirst("td.event").text();
                    String previous = event.selectFirst("td[id^=eventPrevious]").text();
                    String forecast = event.selectFirst("td[id^=eventForecast]").text();
                    String actual = event.selectFirst("td[id^=eventActual]").text();

                    NewsEvent newsEvent = new NewsEvent(title, timeZoneOffsetHours, date + Constants.SPACE + time, currency, importance,
                            previous, forecast, actual);

                    // if (debug)
                    // console.getOut().println("newsEvent: " + newsEvent);

                    // if (newsEvent.getActual() != null) {
                    list.add(newsEvent);
                    // }
                }

                if (debug)
                    console.getOut().println("End of News events creation");

            } catch (Throwable th) {
                console.getErr().println(th.getMessage());
                throw new JFException(th.getMessage(), th);
            }

            if (debug)
                console.getOut().print("File " + filename + " been successfully parsed!\n Number of news releases: " + list.size());

            return list;
        }

    }

    class Constants {

        public static final String SPACE = " ";
        public static final String PERCENT = "%";
        public static final String K = "K";
        public static final String M = "M";
        public static final String B = "B";
    }

    class NewsEvent implements Comparable<NewsEvent> {

        private final java.text.SimpleDateFormat FORMATTER;
        private String title;
        private Boolean positive; // true/false the better value of the
                                    // estimated measure the better it is for
                                    // the currency
        private Date dateTime;
        private String currency;
        private String pair;// instrument name
        private String importance;// 3,2,1 H,M,L respectively
        private Double previous;
        private Double forecast;
        private Double actual;
        private Double changePercentage;
        private Double change;
        private String line;
        private String units;
        private Double startPrice;// last 1 min close price before news release/
                                    // open price on news release
        private Double previousCandleChangePips;
        private Double changePips;

        /**
         * @param title
         * @param dateTime
         * @param currency
         * @param importance
         * @param previous
         * @param forecast
         * @param actual
         */
        public NewsEvent(String title, int offsetHours, String dateTime, String currency, String importance, String previous,
                String forecast, String actual) throws java.text.ParseException {

            this.title = purify(title);

            FORMATTER = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm");
            setDateTime(dateTime, offsetHours);

            this.currency = currency;
            this.importance = importance.substring(0, importance.indexOf(" "));
            this.previous = parseDouble(previous);
            this.forecast = parseDouble(forecast);
            this.actual = parseDouble(actual);

            this.setLine(
                    createLine(this.title, FORMATTER.format(this.dateTime), this.currency, this.importance, previous, forecast, actual));
        }

        public String getPair() {
            return pair;
        }

        public void setPair(String pair) {
            this.pair = pair;
        }

        public void setStartPrice(double startPrice) {
            this.startPrice = startPrice;
        }

        public double getStartPrice() {
            return startPrice;
        }

        public final Double parseDouble(String param) {
            Double result = null;

            param = param.replace(",", "");

            if (param != null && param.length() > 0)

                if (param.contains(Constants.PERCENT)) {
                    param = param.replace(Constants.PERCENT, "");
                    result = Double.parseDouble(param);
                    result = result * 0.01;
                    units = Constants.PERCENT;

                } else if (param.contains(Constants.K)) {
                    param = param.replace(Constants.K, "");
                    result = Double.parseDouble(param);
                    result = result * 1000;
                    units = Constants.K;

                } else if (param.contains(Constants.M)) {
                    param = param.replace(Constants.M, "");
                    result = Double.parseDouble(param);
                    result = result * 1000000;
                    units = Constants.M;

                } else if (param.contains(Constants.B)) {

                    param = param.replace(Constants.B, "");
                    result = Double.parseDouble(param);
                    result = result * 1000000000;
                    units = Constants.B;
                }

            return result;
        }

        private String purify(String s) {

            String result = s;
            if (result.contains("(")) {

                for (String filter : filterList) {
                    if (result.contains(filter)) {
                        result = result.replace(filter, "");
                    }
                }
            }

            return result;
        }

        public String createLine(String title, String dateTime, String currency, String importance, String previous, String forecast,
                String actual) {
            String line = dateTime + "; ";
            line += currency + "; ";
            line += importance + "; ";
            line += title;
            line += "; " + actual + "; " + forecast + "; " + previous + ";";
            return line;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Date getDateTime() {
            return dateTime;
        }

        public void setDateTime(Date dateTime) {
            this.dateTime = dateTime;
        }

        public void setDateTime(String sDateTime, int offsetHours) throws java.text.ParseException {

            dateTime = FORMATTER.parse(sDateTime);
            // adjusting time to GMT
            long eventTimeMillis = dateTime.getTime();
            long ONE_HOUR = 1000 * 60 * 60;
            long convertedTimeMillis = eventTimeMillis - ONE_HOUR * offsetHours;
            dateTime.setTime(convertedTimeMillis);
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getImportance() {
            return importance;
        }

        public void setImportance(String importance) {
            this.importance = importance;
        }

        public Double getPrevious() {
            return previous;
        }

        public void setPrevious(Double previous) {
            this.previous = previous;
        }

        public Double getForecast() {
            return forecast;
        }

        public void setForecast(Double forecast) {
            this.forecast = forecast;
        }

        public Double getActual() {
            return actual;
        }

        public void setActual(Double actual) {
            this.actual = actual;
        }

        @Override
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof NewsEvent) {
                NewsEvent event = (NewsEvent) o;
                result = hashCode() == event.hashCode();
            }

            return result;
        }

        @Override
        public int hashCode() {
            return this.title.hashCode() * this.currency.hashCode();
        }

        @Override
        public int compareTo(NewsEvent o) {
            return this.getDateTime().compareTo(o.getDateTime());
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            java.text.DecimalFormat df = new java.text.DecimalFormat("#0.#");

            java.text.DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);
            java.text.DecimalFormat pf = new java.text.DecimalFormat("#0.0");
            pf.setDecimalFormatSymbols(dfs);

            String sr = ", ";
            String line = sdf.format(dateTime) + sr;
            line += currency + sr;
            line += importance.substring(0, 1) + sr;
            line += title + sr;
            line += pair + sr;
            line += (changePercentage != null ? df.format(changePercentage) + "%" : "-") + sr;
            line += (previousCandleChangePips != null ? df.format(previousCandleChangePips) + "" : "-") + sr;
            line += (changePips != null ? df.format(changePips) + "" : "-") + sr;

            return line;
            // date time, currency, importance, title, instrument,
            // changePercentage, previousCandleChangePips, changePips
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public Double getChange() {
            return change;
        }

        public void setChange(Double change) {
            this.change = change;
        }

        public Double getChangePercentage() {
            return changePercentage;
        }

        public void setChangePercentage(Double changePercentage) {
            this.changePercentage = changePercentage;
        }

        public Boolean getPositive() {
            return positive;
        }

        public void setPositive(Boolean positive) {
            this.positive = positive;
        }

        public double getPreviousCandleChangePips() {
            return previousCandleChangePips;
        }

        public void setPreviousCandleChangePips(Double previousCandleChangePips) {
            this.previousCandleChangePips = previousCandleChangePips;
        }

        public Double getChangePips() {
            return changePips;
        }

        public void setChangePips(Double changePips) {
            this.changePips = changePips;
        }

        private String[] filterList = new String[] { " (Jan)", " (Feb)", " (Mar)", " (Apr)", " (May)", " (Jun)", " (Jul)", " (Aug)",
                " (Sep)", " (Oct)", " (Nov)", " (Dec)", " (Q1)", " (Q2)", " (Q3)", " (Q4)", " (QoQ)", " (YoY)", " (MoM)" };
    }

}