package com.dukascopy.indicators;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import com.dukascopy.api.IBar;
import com.dukascopy.api.indicators.IDrawingIndicator;
import com.dukascopy.api.indicators.IIndicator;
import com.dukascopy.api.indicators.IIndicatorContext;
import com.dukascopy.api.indicators.IIndicatorDrawingSupport;
import com.dukascopy.api.indicators.IndicatorInfo;
import com.dukascopy.api.indicators.IndicatorResult;
import com.dukascopy.api.indicators.InputParameterInfo;
import com.dukascopy.api.indicators.OptInputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo;
import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
import com.dukascopy.api.indicators.OutputParameterInfo.Type;

/** 
 * 
 * @author anatoly.pokusayev
 *
 */

public class WSMTimeIndicator implements IIndicator, IDrawingIndicator {
    private IndicatorInfo indicatorInfo;
    private InputParameterInfo[] inputParameterInfos;    
    private OutputParameterInfo[] outputParameterInfos;
    private IBar[][] inputs = new IBar[1][];    
    private Object[][] outputs;    
    private GeneralPath generalPath = new GeneralPath();        
    public static enum Market{
	    AmericanStockExchange, AmmanStockExchange, AmsterdamStockExchange, AthensStockExchange, AustralianStockExchange, BMFBOVESPA, BarcelonaStockExchange, 
	    BeirutStockExchange, BerlinStockExchange, BermudaStockExchange, BilbaoStockExchange, BogotaStockExchange, BombayStockExchange, BourseExchange, 
	    BratislavaStockExchange, BucharestStockExchange, BudapestStockExchange, BuenosAiresStockExchange, BulgarianStockExchange, BursaMalaysia, CairoStockExchange, 
	    CaracasStockExchange, CasablancaStockExchange, CaymanIslandsStockExchange, ChicagoBoardofOptions, ChicagoBoardofTrade, ChicagoMercantileExchange, 
	    ChicagoStockExchange, ColomboStockExchange, CopenhagenStockExchange, CyprusStockExchange, DubaiGoldCommoditiesExchange, DusseldorfStockExchange, 
	    FrankfurtStockExchange, GhanaStockExchange, HamburgStockExchange, HelsinkiStockExchange, HoChiMinhCitySecuritiesTradingCenter, HongKongStockExchange, 
	    IcelandStockExchange, IrishStockExchange, IstanbulStockExchange, ItalianStockExchange, JakartaStockExchange, JamaicaStockExchange, 
	    JohannesburgStockExchange, KansasCityBoardofTrade, KarachiStockExchange, KoreaStockExchange, LimaStockExchange, LisbonStockExchange, Ljubljana, 
	    LondonMetalExchange, LondonStockExchange, MEFF, MadridFixedIncomeMarket, MadridStockExchange, MexicanStockExchange, MilanStockExchange, 
	    MinneapolisGrainExchange, MontrealStockExchange, MultiCommodityExchange, MunichStockExchange, NASDAQDubai, NASDAQStockExchange, NagoyaStockExchange, 
	    NationalCommoditiesandDerivativesExchange, NationalStockExchangeofIndia, NewYorkBoardofTrade, NewYorkMercantileExchange, NewYorkStockExchange, 
	    NewZealandStockExchange, OsakaSecuritiesExchange, OsloStockExchange, PalestineSecuritiesExchange, ParisStockExchange, PhiladelphiaStockExchange, 
	    PhilippinesStockExchange, PragueStockExchange, RussianTradingSystem, SantiagoStockExchange, SaoPaoloStockExchange, ShanghaiStockExchange, 
	    ShenzhenStockExchange, SingaporeExchangeLimited, SingaporeStockExchange, StockExchangeofMauritius, StockholmStockExchange, StuttgartStockExchange, 
	    SwissExchange, TSXVentureExchange, TaiwanStockExchange, TallinnStockExchange, TelAvivStockExchange, TheSouthAfricanFuturesExchange, 
	    TheStockExchangeofThailand, TokyoFinancialExchange, TokyoStockExchange, TorontoStockExchange, ViennaStockExchange, WarsawStockExchange, XETRATradingSystem, 
	    ZagrebStockExchange}    
    private static final String[] marketNames = new String[]{"American Stock Exchange (AMEX)", "Amman Stock Exchange", "Amsterdam Stock Exchange (Euronext)", 
    	"Athens Stock Exchange (ATHEX)", "Australian Stock Exchange (ASX)", "BM&F BOVESPA", "Barcelona Stock Exchange", "Beirut Stock Exchange", 
    	"Berlin Stock Exchange", "Bermuda Stock Exchange (BSX)", "Bilbao Stock Exchange", "Bogota Stock Exchange", "Bombay Stock Exchange", 
    	"Bourse Exchange (Brussels)", "Bratislava Stock Exchange", "Bucharest Stock Exchange", "Budapest Stock Exchange", 
    	"Buenos Aires Stock Exchange (BCBA)", "Bulgarian Stock Exchange (Sofia)", "Bursa Malaysia (Kuala Lumpur Stock Exchange)", 
    	"Cairo Stock Exchange", "Caracas Stock Exchange", "Casablanca Stock Exchange", "Cayman Islands Stock Exchange", "Chicago Board of Options", 
    	"Chicago Board of Trade (CBOT)", "Chicago Mercantile Exchange (CME)", "Chicago Stock Exchange", "Colombo Stock Exchange", 
    	"Copenhagen Stock Exchange (Nordic Exchange)", "Cyprus Stock Exchange", "Dubai Gold & Commodities Exchange", "Dusseldorf Stock Exchange",
    	"Frankfurt Stock Exchange", "Ghana Stock Exchange", "Hamburg Stock Exchange", "Helsinki Stock Exchange (Nordic Exchange)", 
    	"Ho Chi Minh City Securities Trading Center (HSTC)", "Hong Kong Stock Exchange", "Iceland Stock Exchange (NASDAQ OMX)", "Irish Stock Exchange", 
    	"Istanbul Stock Exchange", "Italian Stock Exchange (Borsa Italiana)", "Jakarta Stock Exchange (JSX)", "Jamaica Stock Exchange", 
    	"Johannesburg Stock Exchange", "Kansas City Board of Trade", "Karachi Stock Exchange", "Korea Stock Exchange", "Lima Stock Exchange", 
    	"Lisbon Stock Exchange (Euronext)", "Ljubljana (Slovenia) Stock Exchange", "London Metal Exchange", "London Stock Exchange (LSE)", 
    	"MEFF (Spanish Financial Futures & Options Exchange)", "Madrid Fixed Income Market", "Madrid Stock Exchange", "Mexican Stock Exchange", 
    	"Milan Stock Exchange (Borsa Italiana)", "Minneapolis Grain Exchange", "Montreal Stock Exchange", "Multi-Commodity Exchange(MCX)", 
    	"Munich Stock Exchange", "NASDAQ Dubai", "NASDAQ Stock Exchange", "Nagoya Stock Exchange", "National Commodities and Derivatives Exchange (NCDEX)", 
    	"National Stock Exchange of India", "New York Board of Trade (NYBOT)", "New York Mercantile Exchange (NYMEX)", "New York Stock Exchange (NYSE)", 
    	"New Zealand Stock Exchange", "Osaka Securities Exchange", "Oslo Stock Exchange", "Palestine Securities Exchange", "Paris Stock Exchange (Euronext)", 
    	"Philadelphia Stock Exchange", "Philippines Stock Exchange", "Prague Stock Exchange", "Russian Trading System (RTS)", "Santiago Stock Exchange", 
    	"Sao Paolo Stock Exchange (BOVESPA)", "Shanghai Stock Exchange (SSE)", "Shenzhen Stock Exchange", "Singapore Exchange Limited", 
    	"Singapore Stock Exchange", "Stock Exchange of Mauritius (SEMDEX)", "Stockholm Stock Exchange (Nordic Exchange)", "Stuttgart Stock Exchange", 
    	"Swiss Exchange (SWX)", "TSX Venture Exchange", "Taiwan Stock Exchange", "Tallinn Stock Exchange (NASDAQ OMX)", "Tel Aviv Stock Exchange", 
    	"The South African Futures Exchange (SAFEX)", "The Stock Exchange of Thailand", "Tokyo Financial Exchange (formerly TIFFE)", "Tokyo Stock Exchange", 
    	"Toronto Stock Exchange (TSX)", "Vienna Stock Exchange", "Warsaw Stock Exchange", "XETRA Trading System", "Zagreb Stock Exchange"};     
    private static final Map<Market, int[]> marketHours;
    static{
    	//market hours as follows: opening hour, closing hour, opening minutes, closing minutes
    	Map<Market, int[]> map = new HashMap<Market, int[]>();
    	map.put(Market.AmericanStockExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.AmmanStockExchange, new int[] {8, 10, 0, 25});
    	map.put(Market.AmsterdamStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.AthensStockExchange, new int[] {8, 14, 30, 30});
    	map.put(Market.AustralianStockExchange, new int[] {0, 6, 0, 0});
    	map.put(Market.BMFBOVESPA, new int[] {14, 21, 0, 0});
    	map.put(Market.BarcelonaStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.BeirutStockExchange, new int[] {7, 10, 30, 30});
    	map.put(Market.BerlinStockExchange, new int[] {8, 19, 0, 0});
    	map.put(Market.BermudaStockExchange, new int[] {13, 19, 0, 30});
    	map.put(Market.BilbaoStockExchange, new int[] {7, 15, 30, 30});
    	map.put(Market.BogotaStockExchange, new int[] {13, 22, 0, 45});
    	map.put(Market.BombayStockExchange, new int[] {3, 10, 30, 0});
    	map.put(Market.BourseExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.BratislavaStockExchange, new int[] {10, 13, 0, 0});
    	map.put(Market.BucharestStockExchange, new int[] {8, 0, 0, 30});
    	map.put(Market.BudapestStockExchange, new int[] {9, 15, 5, 30});
    	map.put(Market.BuenosAiresStockExchange, new int[] {14, 20, 0, 0});
    	map.put(Market.BulgarianStockExchange, new int[] {7, 14, 0, 0});
    	map.put(Market.BursaMalaysia, new int[] {1, 9, 0, 0});
    	map.put(Market.CairoStockExchange, new int[] {7, 13, 45, 15});
    	map.put(Market.CaracasStockExchange, new int[] {0, 16, 30, 30});
    	map.put(Market.CasablancaStockExchange, new int[] {8, 13, 0, 0});
    	map.put(Market.CaymanIslandsStockExchange, new int[] {13, 22, 30, 0});
    	map.put(Market.ChicagoBoardofOptions, new int[] {14, 21, 30, 15});
    	map.put(Market.ChicagoBoardofTrade, new int[] {14, 22, 0, 30});
    	map.put(Market.ChicagoMercantileExchange, new int[] {15, 23, 0, 0});
    	map.put(Market.ChicagoStockExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.ColomboStockExchange, new int[] {4, 9, 0, 0});
    	map.put(Market.CopenhagenStockExchange, new int[] {8, 16, 0, 0});
    	map.put(Market.CyprusStockExchange, new int[] {8, 11, 10, 0});
    	map.put(Market.DubaiGoldCommoditiesExchange, new int[] {4, 19, 30, 30});
    	map.put(Market.DusseldorfStockExchange, new int[] {8, 19, 0, 0});
    	map.put(Market.FrankfurtStockExchange, new int[] {8, 19, 0, 0});
    	map.put(Market.GhanaStockExchange, new int[] {10, 0, 0, 0});
    	map.put(Market.HamburgStockExchange, new int[] {8, 19, 0, 0});
    	map.put(Market.HelsinkiStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.HoChiMinhCitySecuritiesTradingCenter, new int[] {2, 4, 0, 0});
    	map.put(Market.HongKongStockExchange, new int[] {2, 8, 0, 0});
    	map.put(Market.IcelandStockExchange, new int[] {9, 16, 0, 0});
    	map.put(Market.IrishStockExchange, new int[] {8, 16, 0, 28});
    	map.put(Market.IstanbulStockExchange, new int[] {7, 14, 30, 30});
    	map.put(Market.ItalianStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.JakartaStockExchange, new int[] {2, 9, 30, 0});
    	map.put(Market.JamaicaStockExchange, new int[] {14, 18, 30, 30});
    	map.put(Market.JohannesburgStockExchange, new int[] {7, 15, 0, 0});
    	map.put(Market.KansasCityBoardofTrade, new int[] {14, 21, 30, 0});
    	map.put(Market.KarachiStockExchange, new int[] {4, 10, 30, 30});
    	map.put(Market.KoreaStockExchange, new int[] {0, 5, 0, 50});
    	map.put(Market.LimaStockExchange, new int[] {14, 18, 30, 30});
    	map.put(Market.LisbonStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.Ljubljana, new int[] {8, 0, 30, 0});
    	map.put(Market.LondonMetalExchange, new int[] {11, 17, 40, 0});
    	map.put(Market.LondonStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.MEFF, new int[] {8, 16, 0, 35});
    	map.put(Market.MadridFixedIncomeMarket, new int[] {8, 16, 0, 30});
    	map.put(Market.MadridStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.MexicanStockExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.MilanStockExchange, new int[] {8, 16, 5, 25});
    	map.put(Market.MinneapolisGrainExchange, new int[] {14, 19, 30, 20});
    	map.put(Market.MontrealStockExchange, new int[] {14, 21, 30, 15});
    	map.put(Market.MultiCommodityExchange, new int[] {4, 18, 30, 0});
    	map.put(Market.MunichStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.NASDAQDubai, new int[] {6, 13, 0, 0});
    	map.put(Market.NASDAQStockExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.NagoyaStockExchange, new int[] {0, 6, 0, 30});
    	map.put(Market.NationalCommoditiesandDerivativesExchange, new int[] {4, 18, 30, 0});
    	map.put(Market.NationalStockExchangeofIndia, new int[] {3, 10, 30, 0});
    	map.put(Market.NewYorkBoardofTrade, new int[] {0, 21, 55, 15});
    	map.put(Market.NewYorkMercantileExchange, new int[] {13, 22, 0, 15});
    	map.put(Market.NewYorkStockExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.NewZealandStockExchange, new int[] {22, 4, 0, 55});
    	map.put(Market.OsakaSecuritiesExchange, new int[] {0, 6, 0, 10});
    	map.put(Market.OsloStockExchange, new int[] {8, 16, 0, 20});
    	map.put(Market.PalestineSecuritiesExchange, new int[] {8, 11, 0, 0});
    	map.put(Market.ParisStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.PhiladelphiaStockExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.PhilippinesStockExchange, new int[] {1, 4, 0, 0});
    	map.put(Market.PragueStockExchange, new int[] {8, 14, 45, 45});
    	map.put(Market.RussianTradingSystem, new int[] {8, 15, 0, 0});
    	map.put(Market.SantiagoStockExchange, new int[] {13, 20, 30, 30});
    	map.put(Market.SaoPaoloStockExchange, new int[] {13, 19, 0, 55});
    	map.put(Market.ShanghaiStockExchange, new int[] {1, 7, 30, 0});
    	map.put(Market.ShenzhenStockExchange, new int[] {1, 7, 30, 0});
    	map.put(Market.SingaporeExchangeLimited, new int[] {1, 9, 0, 0});
    	map.put(Market.SingaporeStockExchange, new int[] {1, 9, 0, 0});
    	map.put(Market.StockExchangeofMauritius, new int[] {6, 7, 0, 30});
    	map.put(Market.StockholmStockExchange, new int[] {8, 16, 0, 30});
    	map.put(Market.StuttgartStockExchange, new int[] {8, 19, 0, 0});
    	map.put(Market.SwissExchange, new int[] {8, 16, 0, 20});
    	map.put(Market.TSXVentureExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.TaiwanStockExchange, new int[] {1, 5, 0, 25});
    	map.put(Market.TallinnStockExchange, new int[] {8, 14, 0, 0});
    	map.put(Market.TelAvivStockExchange, new int[] {7, 14, 45, 45});
    	map.put(Market.TheSouthAfricanFuturesExchange, new int[] {6, 15, 30, 30});
    	map.put(Market.TheStockExchangeofThailand, new int[] {3, 9, 0, 30});
    	map.put(Market.TokyoFinancialExchange, new int[] {0, 6, 0, 30});
    	map.put(Market.TokyoStockExchange, new int[] {0, 6, 0, 0});
    	map.put(Market.TorontoStockExchange, new int[] {14, 21, 30, 0});
    	map.put(Market.ViennaStockExchange, new int[] {8, 16, 15, 30});
    	map.put(Market.WarsawStockExchange, new int[] {9, 15, 0, 0});
    	map.put(Market.XETRATradingSystem, new int[] {8, 16, 0, 30});
    	map.put(Market.ZagrebStockExchange, new int[] {9, 15, 0, 0});    	
    	marketHours = Collections.unmodifiableMap(map);
    }    
    private static enum openClose{open, close}

    public static int[] getMarketHours(Market market){
    	return marketHours.get(market);
    }        
    
    public void onStart(IIndicatorContext context) {        	
    	this.outputs = new Object[Market.values().length][];    	        
    	indicatorInfo = new IndicatorInfo("WSMTIME", "World Stock Market Time", "", true, false, false, 1, 0, Market.values().length);
        inputParameterInfos = new InputParameterInfo[] {new InputParameterInfo("Input data", InputParameterInfo.Type.BAR)};                                     
        outputParameterInfos = new OutputParameterInfo[Market.values().length];
        for (int i = 0; i < Market.values().length; i++){
        	outputParameterInfos[i] = createOutputParameterInfo(marketNames[i], OutputParameterInfo.Type.OBJECT, OutputParameterInfo.DrawingStyle.LINE);
        }       
    }

    private OutputParameterInfo createOutputParameterInfo(String name, Type type, DrawingStyle drawingStyle) {
    	return new OutputParameterInfo(name, type, drawingStyle, false){{
    		setDrawnByIndicator(true);
    	}};
    }
    
    public IndicatorResult calculate(int startIndex, int endIndex) {
        if (startIndex - getLookback() < 0) {
            startIndex = getLookback();
        }                 
        int i, j;
        for (i = startIndex, j = 0; i <= endIndex; i++, j++) {        	
        	for (Market m : Market.values()){
        		outputs[m.ordinal()][j] = null;    			        	
        	}         	
        	HashMap<Market, openClose> marketValues = fetchMarketValues(inputs[0][i].getTime());
        	for (Market m : marketValues.keySet()){        		
        		outputs[m.ordinal()][j] = new Object[] {m,  marketValues.get(m).toString()};        		
        	}              	
        }
        return new IndicatorResult(startIndex, j);
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

    public int getLookback() {
        return 0;
    }

    public int getLookforward() {
        return 0;
    }

    public OptInputParameterInfo getOptInputParameterInfo(int index) {       
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
    }

    public void setOutputParameter(int index, Object array) {    	
        outputs[index] = (Object[])array;
    }      
       
    
    @Override
	public Point drawOutput(
			Graphics g,
			int outputIdx,
			Object values,
			Color color,
			Stroke stroke,
			IIndicatorDrawingSupport indicatorDrawingSupport,
			List<Shape> shapes,
			Map<Color, List<Point>> handles
	) {
		if (values != null) {			
			if (outputIdx == getFirstOutputIndex()) rectangles.clear();									
			
			Object[] output = (Object[]) values;
			
			Graphics2D g2 = (Graphics2D) g;
			generalPath.reset();												
			g2.setColor(color);
			g2.setStroke(stroke);						
									
			Font font = new Font("Dialog", Font.PLAIN, 9);		    
		    g2.setFont(font);
			
			drawLine(g2, output, indicatorDrawingSupport, generalPath);					
			g2.draw(generalPath);												
		}		
		return null;
	}
    
    private void drawLine(Graphics2D g2, Object[] output, IIndicatorDrawingSupport indicatorDrawingSupport, GeneralPath generalPath) {    	
		int maxWidth = indicatorDrawingSupport.getChartWidth();
		int maxHeight = indicatorDrawingSupport.getChartHeight();				
		
		for (int i = 0; i < output.length; i ++) {
			Object[] values = (Object[])output[i];

			if (values == null) continue;
			
			FontMetrics fontMetrics = g2.getFontMetrics();			
			String label = marketNames[((Market)values[0]).ordinal()] + ":  " + values[1].toString();
	        int width = fontMetrics.stringWidth(label);
	        int height = fontMetrics.getHeight();
	        	        	        
	        int x = (int)indicatorDrawingSupport.getMiddleOfCandle(i);	        
	        if (0 <= x && x <= maxWidth) {
				generalPath.moveTo(x, 0);
				generalPath.lineTo(x, maxHeight);											
	        
		        Rectangle rect = new Rectangle(x, 10, width, height);		        
		        int lowestY = rect.y;		        
		        for (Rectangle curRect : rectangles) {
		            if (rect != curRect && intersect(rect, curRect) && curRect.y >= lowestY) {
		                rect.y = curRect.y + curRect.height;
		                lowestY = curRect.y;
		            }
		        }
		        g2.drawString(label, rect.x, rect.y + rect.height);								 			
		        rectangles.add(rect);
	        }
		}				
	}   
        
	private HashMap<Market, openClose> fetchMarketValues(long barTime){						
		HashMap<Market, openClose> markets  = new HashMap<Market, openClose>();    	    	
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));        
    	cal.setTimeInMillis(barTime);        	    	    	
    	
    	if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 
    		return markets;    	    	
    	
    	int calHour = cal.get(Calendar.HOUR_OF_DAY), calMin = cal.get(Calendar.MINUTE), calSec = cal.get(Calendar.SECOND);
    	for (Market m : Market.values()){    		
    		if (calHour == marketHours.get(m)[0] && calMin == marketHours.get(m)[2] && calSec == 0){
    			markets.put(m, openClose.open);
    		}
    		else if (calHour == marketHours.get(m)[1] && calMin == marketHours.get(m)[3] && calSec == 0){
    			markets.put(m, openClose.close);
    		}
    	}    	          	    	
        return markets;
    }   
	
	private int getFirstOutputIndex(){
		for (int i = 0; i < outputParameterInfos.length - 1; i++){
			if (outputParameterInfos[i].isShowOutput()) return i; 
		}
		return -1;
	}
		
	private final SortedSet<Rectangle> rectangles = new TreeSet<Rectangle>(new Comparator<Rectangle>() {
		public int compare(Rectangle rec1, Rectangle rec2) {
			int xDif = rec1.x - rec2.x;
	       	if (xDif != 0) {
	       		return xDif;
	       	}
	       	int yDif = rec1.y - rec2.y;
	        return yDif;
	    }
	});
	 
	private boolean intersect(Rectangle rect, Rectangle curRect) {
		return !(rect.x + rect.width <= curRect.x || curRect.x + curRect.width <= rect.x);
	}	 
}
