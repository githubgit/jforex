/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.Set;

/**
 * News filter definition - used for {@link INewsMessage} subscription.<br>
 * Default implementations : {@link NewsFilter} and {@link CalendarFilter}
 * 
 * @author viktor.sjubajev
 */
public interface INewsFilter {

	/**
	 * News sources 
	 */
	enum NewsSource {
		
		/**
	     * @deprecated
	     */
		@Deprecated
	    DJ_NEWSWIRES("Dow Jones Newswires", "DJ"),
	    
	    DJ_LIVE_CALENDAR("Dow Jones Live Calendar", "DJ"),
	    ACM_MARKET_NEWS("Acquire Media Market News", "ACM"),
		FXSPIDER_NEWS("FX Spider News", "FX Spider"),
		DUKASCOPY_WEBINAR("Dukascopy Webinar", "WB");

	    public final String name;
	    public final String provider;

	    NewsSource(String name, String provider) {
	        this.name = name;
	        this.provider = provider;
	    }
	}	
	
	/**
	 * Geo regions - used for {@link Country} grouping
	 */
	enum Region {
		Combined		("Combined"),
		Europe			("Europe"),
		NorthAmerica	("North America"),
		CentralAmerica	("Central America"),
		SouthAmerica	("South America"),
		Asia			("Asia"),
		Africa			("Africa"),
		Pacific			("Pacific");
		
		public final String name;
		private Region(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}	
	
	/**
	 * Countries
	 */
	enum Country {
	    // Africa
		AFRICA("Africa"		, Region.Africa),
	    DZ("Algeria"		, Region.Africa),
	    EG("Egypt"			, Region.Africa),
	    CI("Ivory Coast"	, Region.Africa),
	    KE("Kenya"			, Region.Africa),
	    NA("Namibia"		, Region.Africa),
	    NG("Nigeria"		, Region.Africa),
	    SL("Sierra Leone"	, Region.Africa),
	    ZA("South Africa"	, Region.Africa),
	    TN("Tunisia"		, Region.Africa),
	    ZM("Zambia"			, Region.Africa),
	    ZW("Zimbabwe"		, Region.Africa),

	    // Asia
	    AZ("Azerbaijan"		, Region.Asia),
	    BH("Bahrain"		, Region.Asia),
	    BD("Bangladesh"		, Region.Asia),
	    KH("Cambodia"		, Region.Asia),
	    CN("China"			, Region.Asia),
	    CY("Cyprus"			, Region.Asia),
	    HK("Hong Kong"		, Region.Asia),
	    IN("India"			, Region.Asia),
	    ID("Indonesia"		, Region.Asia),
	    IR("Iran"			, Region.Asia),
	    IQ("Iraq"			, Region.Asia),
	    IL("Israel"			, Region.Asia),
	    JP("Japan"			, Region.Asia),
	    KW("Kuwait"			, Region.Asia),
	    MY("Malaysia"		, Region.Asia),
	    NP("Nepal"			, Region.Asia),
	    KP("North Korea"	, Region.Asia),
	    PK("Pakistan"		, Region.Asia),
	    PH("Philippines"	, Region.Asia),
	    QA("Qatar"			, Region.Asia),
	    SA("Saudi Arabia"	, Region.Asia),
	    SG("Singapore"		, Region.Asia),
	    KR("South Korea"	, Region.Asia),
	    LK("Sri Lanka"		, Region.Asia),
	    TW("Taiwan"			, Region.Asia),
	    TH("Thailand"		, Region.Asia),
	    AE("United Arab Emirates"	, Region.Asia),
	    VN("Vietnam"				, Region.Asia),

	    // North America
	    NORTH_AMERICA("North America", Region.NorthAmerica),
	    US("United States"	, Region.NorthAmerica),
	    CA("Canada"			, Region.NorthAmerica),
	    MX("Mexico"			, Region.NorthAmerica),

	    // Central America
	    CENTRAL_AMERICA("Central America", Region.CentralAmerica),
	    PA("Panama"			, Region.CentralAmerica),

	    // South America
	    SOUTH_AMERICA("South America"	, Region.SouthAmerica),
	    AR("Argentina"		, Region.SouthAmerica),
	    BR("Brazil"			, Region.SouthAmerica),
	    KY("Cayman Islands"	, Region.SouthAmerica),
	    CL("Chile"			, Region.SouthAmerica),
	    CO("Colombia"		, Region.SouthAmerica),
	    EC("Ecuador"		, Region.SouthAmerica),
	    PE("Peru"			, Region.SouthAmerica),
	    VE("Venezuela"		, Region.SouthAmerica),

	    // Europe
	    EUROPE("Europe"					, Region.Europe),
	    WESTERN_EUROPE("Western Europe"	, Region.Europe),
	    EASTERN_EUROPE("Eastern Europe"	, Region.Europe),
	    AD("Andorra"		, Region.Europe),
	    AT("Austria"		, Region.Europe),
	    BE("Belgium"		, Region.Europe),
	    BG("Bulgaria"		, Region.Europe),
	    CZ("Czech Republic"	, Region.Europe),
	    DK("Denmark"		, Region.Europe),
	    EE("Estonia"		, Region.Europe),
	    FI("Finland"		, Region.Europe),
	    FR("France"			, Region.Europe),
	    DE("Germany"		, Region.Europe),
	    GR("Greece"			, Region.Europe),
	    HU("Hungary"		, Region.Europe),
	    IS("Iceland"		, Region.Europe),
	    IE("Ireland"		, Region.Europe),
	    IT("Italy"			, Region.Europe),
	    LV("Latvia"			, Region.Europe),
	    LT("Lithuania"		, Region.Europe),
	    LU("Luxembourg"		, Region.Europe),
	    MT("Malta"			, Region.Europe),
	    MC("Monaco"			, Region.Europe),
	    NL("Netherlands"	, Region.Europe),
	    NO("Norway"			, Region.Europe),
	    PL("Poland"			, Region.Europe),
	    PT("Portugal"		, Region.Europe),
	    RO("Romania"		, Region.Europe),
	    RU("Russia"			, Region.Europe),
	    SK("Slovakia"		, Region.Europe),
	    SI("Slovenia"		, Region.Europe),
	    ES("Spain"			, Region.Europe),
	    SE("Sweden"			, Region.Europe),
	    CH("Switzerland"	, Region.Europe),
	    TR("Turkey"			, Region.Europe),
	    UK("United Kingdom"	, Region.Europe),
	    UA("Ukraine"		, Region.Europe),

	    // Pacific
	    PACIFIC("Pacific"	, Region.Pacific),
	    AU("Australia"		, Region.Pacific),
	    NZ("New Zealand"	, Region.Pacific),

		//Combined
	    /**
	     * Combination of : AZ, BH, BD, KH, CN, CY, HK, IN, ID, IR, IQ, IL, JP, KW, MY, NP, KP, PK, PH, QA, SA, SG, KR, LK, TW, TH, AE, VN
	     */
		ASIA("Asia - All", Region.Combined,
				AZ, BH, BD, KH, CN, CY, HK, IN, ID, IR, IQ, IL, JP, KW,
				MY, NP, KP, PK, PH, QA, SA, SG, KR, LK, TW, TH, AE, VN),
		/**
		 * Combination of : AU, NZ 
		 */
		AUSTRALIA_NZ("Australia/NZ", Region.Combined,
				AU, NZ),
		/**
		 * Combination of : EUROPE, AT, BE, BG, CZ, DK, EE, FI, FR, DE, GR, HU, IE, IT, LV, LT, LU, MT, NL, PL, PT, RO, SK, SI, ES, SE, UK, US
		 */
		EU_US("EU/USA", Region.Combined,
				EUROPE, AT, BE, BG, CZ, DK, EE, FI, FR, DE, GR, HU, IE, IT, LV,
				LT, LU, MT, NL, PL, PT, RO, SK, SI, ES, SE, UK, US),
		/**
		 * Combination of : AT, BE, FI, FR, DE, GR, IE, IT, LU, MT, NL, PT, SK, SI, ES
		 */
		EURO_ZONE("Euro Zone", Region.Combined,
				AT, BE, FI, FR, DE, GR, IE, IT, LU, MT, NL, PT, SK, SI, ES),
		/**
		 * Combination of : CA, FR, DE, IT, JP, UK, US
		 */
	    G7("G7", Region.Combined,
	    		CA, FR, DE, IT, JP, UK, US),
	    /**
	     * Combination of : BE, CA, FR, DE, IT, JP, NL, SE, CH, UK, US
	     */
	    G10("G10", Region.Combined,
	    		BE, CA, FR, DE, IT, JP, NL, SE, CH, UK, US);	    

	    /**
	     * Human readable name
	     */
	    public final String name;
	    /**
	     * Country's geo region
	     */
	    public final Region region;
	    /**
	     * Included countries - in case when region is {@link Region#Combined}
	     */
	    public final Country[] countries;

	    Country(String name, Region region, Country ... countries) {
	        this.name = name;
	        this.region = region;
	        this.countries = countries;
	    }

	    @Override
	    public String toString() {
	    	return name;
	    }
	}
	
	/**
	 * Stock indices regions - used for {@link StockIndex} grouping
	 */
	enum IndexRegion {
		Asia			("Asia"			),
		Europe			("Europe"		),
		Global			("Global"		),
		NorthAmerica	("North America");
		
		public final String name;

		private IndexRegion(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	/**
	 * Stock indices
	 */
	enum StockIndex {
		
		//Asia ---------------------------------------------------------------------------------------------------------

		/**
		 * Dow Jones Asian Titans Index Components @ {@link IndexRegion#Asia}
		 */
		DJAT		("Dow Jones Asian Titans Index Components"	, IndexRegion.Asia),
		/**
		 * Hang Seng Components @ {@link IndexRegion#Asia}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		HSC			("Hang Seng Components"						, IndexRegion.Asia),
		/**
		 * Nikkei 225 Components @ {@link IndexRegion#Asia}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		NIKKEI		("Nikkei 225 Components"					, IndexRegion.Asia),

		//Europe -------------------------------------------------------------------------------------------------------

		/**
		 * AEX Index Components @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		AEX			("AEX Index Components"				, IndexRegion.Europe),
		/**
		 * ATX Components @ {@link IndexRegion#Europe}
		 */		
		ATX			("ATX Components"					, IndexRegion.Europe),
		/**
		 * AEX Index Components @ {@link IndexRegion#Europe}
		 */		
		BEL20		("Bel20 Components"					, IndexRegion.Europe),
		/**
		 * CAC 40 Components @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		CAC			("CAC 40 Components"				, IndexRegion.Europe),
		/**
		 * AEX Index Components @ {@link IndexRegion#Europe}
		 */		
		DAXMC		("DAX MidCap Index"					, IndexRegion.Europe),
		/**
		 * DAX SmallCap Index @ {@link IndexRegion#Europe}
		 */		
		DAXSC		("DAX SmallCap Index"				, IndexRegion.Europe),
		/**
		 * Dow Jones Stoxx 50 Components @ {@link IndexRegion#Europe}
		 */		
		DJS			("Dow Jones Stoxx 50 Components"	, IndexRegion.Europe),
		/**
		 * Dow Jones Stoxx 50 Components @ {@link IndexRegion#Europe}
		 */		
		EURONEXT	("Euronext 100"						, IndexRegion.Europe),
		/**
		 * EuroSTOXX50 @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		EUROSTOXX50	("EuroSTOXX50"						, IndexRegion.Europe),
		/**
		 * FTSE 100 Components @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		FTSE		("FTSE 100 Components"				, IndexRegion.Europe),
		/**
		 * FTSE 100 Components @ {@link IndexRegion#Europe}
		 */		
		HEX			("HEX Components"					, IndexRegion.Europe),
		/**
		 * IBEX-35 Components @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		IBEX35		("IBEX-35 Components"				, IndexRegion.Europe),
		/**
		 * KFX Components @ {@link IndexRegion#Europe}
		 */		
		KFX			("KFX Components"					, IndexRegion.Europe),
		/**
		 * MIB 40 Components @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		MIB			("MIB 40 Components"				, IndexRegion.Europe),
		/**
		 * OBX Total Components @ {@link IndexRegion#Europe}
		 */		
		OBX			("OBX Total Components"				, IndexRegion.Europe),
		/**
		 * PSI-20 Components @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		PSI20		("PSI-20 Components"				, IndexRegion.Europe),
		/**
		 * SMI Components @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		SMI			("SMI Components"					, IndexRegion.Europe),
		/**
		 * SX All Share Component @ {@link IndexRegion#Europe}
		 */		
		SX			("SX All Share Components"			, IndexRegion.Europe),
		/**
		 * TecDAX Components @ {@link IndexRegion#Europe}
		 */		
		TECDAX		("TecDAX Components"				, IndexRegion.Europe),
		/**
		 * XETRA DAX Component @ {@link IndexRegion#Europe}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		XETRADAX	("XETRA DAX Components"				, IndexRegion.Europe),
		
		//Global -------------------------------------------------------------------------------------------------------

		/**
		 * Dow Jones Global Index Components @ {@link IndexRegion#Global}
		 */		
		DJG			("Dow Jones Global Index Components"					, IndexRegion.Global),
		/**
		 * Dow Jones Global Titans Index Components @ {@link IndexRegion#Global}
		 */		
		DJGT		("Dow Jones Global Titans Index Components"				, IndexRegion.Global),
		/**
		 * Dow Jones Industrial Average Components @ {@link IndexRegion#Global}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		DJIA		("Dow Jones Industrial Average Components"				, IndexRegion.Global),
		/**
		 * Dow Jones Islamic Index Components @ {@link IndexRegion#Global}
		 */		
		DJI			("Dow Jones Islamic Index Components"					, IndexRegion.Global),
		/**
		 * Dow Jones Sector Titan Index - Financial Components @ {@link IndexRegion#Global}
		 */		
		DJSTFIN		("Dow Jones Sector Titan Index - Financial Components"	, IndexRegion.Global),
		/**
		 * Dow Jones Sector Titan Index - Technology Components @ {@link IndexRegion#Global}
		 */		
		DJSTTECH	("Dow Jones Sector Titan Index - Technology Components"	, IndexRegion.Global),
		/**
		 * Dow Jones Sector Titan Index - Tel Co @ {@link IndexRegion#Global}
		 */		
		DJSTTEL		("Dow Jones Sector Titan Index - Tel Co"				, IndexRegion.Global),
		/**
		 * S&amp;P 500 Index component @ {@link IndexRegion#Global}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		SP			("S&P 500 Index component"								, IndexRegion.Global),

		//North America ------------------------------------------------------------------------------------------------

		/**
		 * Fortune 500 Index @ {@link IndexRegion#Global}
		 */		
		FORTUNE		("Fortune 500 Index"				, IndexRegion.NorthAmerica),
		/**
		 * NASDAQ 100 Components @ {@link IndexRegion#Global}. Accepted by Market News filter.
		 */
		@AcceptByMarketNewsFilter
		NASDAQ		("NASDAQ 100 Components"			, IndexRegion.NorthAmerica),
		/**
		 * NYSE Composite Index Components @ {@link IndexRegion#Global}
		 */		
		NYSE		("NYSE Composite Index Components"	, IndexRegion.NorthAmerica),
		/**
		 * Russell 3000 component @ {@link IndexRegion#Global}
		 */		
		RUSSELL		("Russell 3000 component"			, IndexRegion.NorthAmerica),
		/**
		 * US Small Cap Index @ {@link IndexRegion#Global}
		 */		
		US			("US Small Cap Index"				, IndexRegion.NorthAmerica);

		public final String name;
		public final IndexRegion region;

		private StockIndex(String name, IndexRegion region) {
			this.name = name;
			this.region = region;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	/**
	 * Market sectors
	 */
	enum MarketSector {
		/**
		 * Consumer Goods
		 */
	    NCY("Consumer Goods"),
	    /**
	     * Technology
	     */
	    TEC("Technology"),
	    /**
	     * Utilities
	     */
	    UTI("Utilities"),
	    /**
	     * Basic Materials
	     */
	    BSC("Basic Materials"),
	    /**
	     * Consumer Services
	     */
	    CYC("Consumer Services"),
	    /**
	     * Oil &amp; Gas
	     */
	    ENE("Oil & Gas"),
	    /**
	     * Financial
	     */
	    FCL("Financial"),
	    /**
	     * Industrials
	     */
	    IDU("Industrials"),
	    /**
	     * Health Care
	     */
	    HCR("Health Care"),
	    /**
	     * Telecommunications
	     */
	    TEL("Telecommunications");

	    public final String name;

	    private MarketSector(String name) {
	        this.name = name;
	    }

	    @Override
	    public String toString() {
	    	return this.name;
	    }
	}
	
	/**
	 * Currencies
	 */
	enum Currency {
		
	    ARS("Argentine Peso"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    AUD("Australian Dollar"),
	    BGN("Bulgarian Lev"),
	    BRL("Brazilian Real"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    CAD("Canadian Dollar"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    CHF("Swiss Franc"),
	    CNY("Yuan Renminbi"),
	    CZK("Czech Koruna"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    DKK("Danish Krone"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    EUR("Euro"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    GBP("Pound Sterling"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    HKD("Hong Kong Dollar"),
	    HUF("Forint"),
	    INR("Indian Rupee"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    JPY("Yen"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    MXN("Mexican Nuevo Peso"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    NOK("Norwegian Krone"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    NZD("New Zealand Dollar"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    PLN("Zloty"),
	    RON("Romanian New Leu"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    RUB("Russian Ruble"),
	    SAR("Saudi Riyal"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    SEK("Swedish Krone"),
	    @AcceptByMarketNewsFilter
	    SGD("Singapore Dollar"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    TRY("New Turkish Lira"),
	    TWD("New Taiwan Dollar"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    USD("U.S. Dollar"),
	    
	    /**
	     * Accepted by Market News filter.
	     */
	    @AcceptByMarketNewsFilter
	    ZAR("Rand");

	    public final String name;
	    
	    private Currency(String name) {
	        this.name = name;
	    }

		@Override
	    public String toString() {
	    	return name() + " - " + this.name;
	    }
	}
	
	/**
	 * News types
	 */
	enum Type {
		/**
		 * Corporate
		 */
        ICC("Corporate"),
        /**
         * Economic
         */
        IEP("Economic"),
        /**
         * Debt Issuance
         */
        IDC("Debt Issuance");

        public final String name;

        private Type(String name) {
            this.name = name;
        }

        @Override
	    public String toString() {
	    	return this.name;
	    }
	}

	/**
	 * Event Category contains an ID of the event category so that historical events can be linked<br>
     */
    enum EventCategory {
        ADP_NATIONAL_EMPLYMENT_REPORT                   ("ADP National Employment Report"                   , 1),
        ALL_INDUSTRY_ACTIVITY_INDEX                     ("All Industry Activity Index"                      , 2),
        AUTO_EXPORTS                                    ("Auto Exports"                                     , 3),
        AUTO_PRODUCTION                                 ("Auto Production"                                  , 4),
        AUTO_SALES                                      ("Auto Sales"                                       , 5),
        BALANCE_OF_PAYMENTS                             ("Balance of Payments"                              , 6),
        BANK_LENDING                                    ("Bank Lending"                                     , 8),
        BANK_OF_CANADA_MONTHLY_STATS                    ("Bank of Canada Monthly Stats"                     , 10),
        
        BANK_OF_CANADA_WEEKLY_STATS                     ("Bank of Canada Weekly Stats"                      , 11),
        INTEREST_RATE_ANNOUNCEMENTS                     ("Interest Rate Announcements"                      , 12),
        BUSINESS_AND_CONSUMER_CONFIDENCE                ("Business and Consumer Confidence"                 , 14),
        BUSINESS_CONFIDENCE                             ("Business Confidence"                              , 15),
        BUSINESS_INVENTORIES                            ("Business Inventories"                             , 16),
        BUSINESS_INVESTMENT                             ("Business Investment"                              , 17),
        BUSINESS_SENTIMENT                              ("Business Sentiment"                               , 18),
        CHICAGO_PMI                                     ("Chicago PMI"                                      , 19),
        CONFERENCE_BOARD_CONSUMER_CONFIDENCE_SURVEY     ("Conference Board Consumer Confidence Survey"      , 20),
        
        CONSTRUCTION_SPENDING                           ("Construction Spending"                            , 21),
        CONSUMER_CONFIDENCE                             ("Consumer Confidence"                              , 22),
        CONSUMER_LENDING                                ("Consumer Lending"                                 , 24),
        CONSUMER_PRICE_INDEX                            ("Consumer Price Index"                             , 25),
        CONSUMER_PRICE_INDEX_FINAL                      ("Consumer Price Index - Final"                     , 26),
        CONSUMER_PRICE_INDEX_FLASH_EST                  ("Consumer Price Index - Flash Est."                , 27),
        CONSUMER_PRICE_INDEX_PRELIMINARY                ("Consumer Price Index - Preliminary"               , 28),
        CONSUMER_SPENDING                               ("Consumer Spending"                                , 29),
        CORPORATE_GOODS_PRICE_INDEX                     ("Corporate Goods Price Index"                      , 30),
        
        CURRENT_ACCOUNT                                 ("Current Account"                                  , 31),
        DALLAS_FED_MANUFACTURING_OUTLOOK_SURVEY         ("Dallas Fed Manufacturing Outlook Survey"          , 32),
        DURABLE_GOODS                                   ("Durable Goods"                                    , 33),
        EMPLOYMENT                                      ("Employment"                                       , 34),
        EMPLOYMENT_COST_INDEXES                         ("Employment Cost Indexes"                          , 35),
        EXISTING_HOME_SALES                             ("Existing Home Sales"                              , 38),
        FACTORY_ORDERS                                  ("Factory Orders"                                   , 39),
        
        FOREIGN_RESERVES                                ("Foreign Reserves"                                 , 41),
        GDP                                             ("GDP"                                              , 42),
        GDP_ADVANCE                                     ("GDP - Advance"                                    , 43),
        GDP_FINAL                                       ("GDP - Final"                                      , 44),
        GDP_FLASH_EST                                   ("GDP - Flash Est."                                 , 45),
        GDP_PRELIMINARY                                 ("GDP - Preliminary"                                , 46),
        GDP_REVISED                                     ("GDP - Revised"                                    , 47),
        HOUSING_INDEX_HALIFAX_PENDING_SALES             ("Housing Index (Halifax/Pending Sales)"            , 48),
        HOUSING_STARTS                                  ("Housing Starts"                                   , 49),
        IFO_BUSINESS_SURVEY                             ("IFO Business Survey"                              , 50),
        
        IMPORT_PRICES                                   ("Import Prices"                                    , 51),
        INDUSTRIAL_PRODUCTION                           ("Industrial Production"                            , 54),
        INDUSTRIAL_PRODUCTION_PRELIMINARY               ("Industrial Production - Preliminary"              , 55),
        INDUSTRIAL_PRODUCTION_REVISED                   ("Industrial Production - Revised"                  , 56),
        INDUSTRIAL_PRODUCTION_AND_CAPACITY_UTILIZATION  ("Industrial Production and Capacity Utilization"   , 57),
        INTERNATIONAL_SECURITIES_TRANSACTIONS           ("International Securities Transactions"            , 59),
        ISM_MANUFACTURING_SURVEY                        ("ISM Manufacturing Survey"                         , 60),
        
        ISM_NON_MANUFACTURING_SURVEY                    ("ISM Non-Manufacturing Survey"                     , 61),
        JOBLESS_CLAIMS                                  ("Jobless Claims"                                   , 62),
        LEADING_ECONOMIC_INDICATORS                     ("Leading Economic Indicators"                      , 65),
        MANUFACTURING_ORDERS                            ("Manufacturing Orders"                             , 68),
        MANUFACTURING_SURVEY                            ("Manufacturing Survey"                             , 69),
        
        MONETARY_BASE                                   ("Monetary Base"                                    , 71),
        MONETARY_RESERVES                               ("Monetary Reserves"                                , 72),
        MONEY_SUPPLY                                    ("Money Supply"                                     , 73),
        NAHB_HOUSING_INDEX                              ("NAHB Housing Index"                               , 74),
        NATIONWIDE_HOUSE_PRICES                         ("Nationwide House Prices"                          , 75),
        NEW_HOME_SALES                                  ("New Home Sales"                                   , 76),
        NEW_ORDERS                                      ("New Orders"                                       , 77),
        NY_FED_EMPIRE_STATE_SURVEY                      ("NY Fed Empire State Survey"                       , 78),
        PERSONAL_INCOME                                 ("Personal Income"                                  , 79),
        PHILADELPHIA_FED_BUSINESS_OUTLOOK_SURVEY        ("Philadelphia Fed Business Outlook Survey"         , 80),
        
        PMI_MANUFACTURING                               ("PMI - Manufacturing"                              , 81),
        PMI_SERVICES                                    ("PMI - Services"                                   , 82),
        PRELIMINARY_PORTFOLIO_DATA                      ("Preliminary Portfolio Data"                       , 83),
        PRODUCER_PRICE_INDEX                            ("Producer Price Index"                             , 84),
        PRODUCTIVITY_PRELIMINARY                        ("Productivity - Preliminary"                       , 87),
        PRODUCTIVITY_REVISED                            ("Productivity - Revised"                           , 88),
        PUBLIC_FINANCES                                 ("Public Finances"                                  , 89),
        RETAIL_SALES                                    ("Retail Sales"                                     , 90),
        
        RICHMOND_FED_BUSINESS_ACTIVITY_SURVEY           ("Richmond Fed Business Activity Survey"            , 91),
        RICS_HOUSE_PRICES                               ("RICS House Prices"                                , 92),
        TANKAN                                          ("Tankan"                                           , 93),
        TERTIARY_INDUSTRY_ACTIVITY_INDEX                ("Tertiary Industry Activity Index"                 , 94),
        TRADE_BALANCE                                   ("Trade Balance"                                    , 95),
        REUTERS_UNIVERSITY_OF_MICHIGAN_CONSUMER_SENTIMENT_SURVEY_FINAL   ("Reuters/University of Michigan Consumer Sentiment Survey (Final)"    , 96),
        WAGE_EARNERS_HOUSEHOLD_SPENDING                 ("Wage-Earners Household Spending"                  , 97),
        WHOLESALE_TRADE                                 ("Wholesale Trade"                                  , 98),
        ZEW_BUSINESS_CONFIDENCE                         ("ZEW Business Confidence"                          , 99),
        INDUSTRIAL_AND_RAW_MATERIAL_PRICES              ("Industrial And Raw Material Prices"               , 100),
        
        BAVARIA_CPI                                     ("Bavaria CPI"                                      , 101),
        BRANDENBURG_CPI                                 ("Brandenburg CPI"                                  , 102),
        BADEN_WUERTTEMBERG_CPI                          ("Baden-Wuerttemberg CPI"                           , 103),
        HESSE_CPI                                       ("Hesse CPI"                                        , 104),
        NORTH_RHINE_WESTPHALIA_CPI                      ("North Rhine Westphalia CPI"                       , 105),
        SAXONY_CPI                                      ("Saxony CPI"                                       , 106),
        RBA_CASH_RATE                                   ("RBA Cash Rate"                                    , 107),
        FOREX_TRANSACTION_SPOT_MARKET                   ("Forex Transaction Spot Market"                    , 108),
        IVEY_PMI                                        ("Ivey PMI"                                         , 109),
        PMI_FLASH_ESTIMATES                             ("PMI - Flash Estimates"                            , 110),
        
        RBNZ_CASH_RATE                                  ("RBNZ Cash Rate"                                   , 111),
        TRADE_INDEX                                     ("Trade Index"                                      , 112),
        BOE_MPC_MINUTES                                 ("BOE MPC Minutes"                                  , 113),
        CBI_DISTRIBUTITIVE_TRADES_SURVEY                ("CBI Distributitive Trades Survey"                 , 114),
        FEDERAL_FUNDS_RATE                              ("Federal Funds Rate"                               , 115),
        TIC_REPORT                                      ("TIC Report"                                       , 116),
        REPO_RATE                                       ("Repo Rate"                                        , 118),
        LIBOR_RATE_BANDS                                ("Libor Rate bands"                                 , 119),
        CONSUMER_SENTIMENT_INDEX                        ("Consumer Sentiment Index"                         , 120),
        
        KOF_ECONOMIC_BAROMETER                          ("KOF Economic Barometer"                           , 122),
        SP_CASE_SHILLER_HOME_PX_INDEX                   ("SP Case Shiller Home Px Index"                    , 124),
        NORGES_BANK_RATE                                ("Norges Bank Rate"                                 , 125),
        EMPLOYMENT_TRENDS                               ("Employment Trends"                                , 126),
        BUILDING_APPROVALS                              ("Building Approvals"                               , 127),
        CPI_NATION                                      ("CPI (Nation)"                                     , 128),
        CPI_TOKYO                                       ("CPI (Tokyo)"                                      , 129),
        UNEMPLOYMENT                                    ("Unemployment"                                     , 130),
        
        CHICAGO_FED_MIDWEST_MANUFACTURING_INDEX         ("Chicago Fed Midwest Manufacturing Index"          , 131),
        CHICAGO_FED_NATIONAL_ACTIVITY_INDEX             ("Chicago Fed National Activity Index"              , 132),
        FEDERAL_DISCOUNT_WINDOW                         ("Federal Discount Window"                          , 133),
        FOREIGN_CUSTODY_HOLDING                         ("Foreign Custody Holding"                          , 134),
        MBA_MORTGAGE_APPLICATION_SURVEY                 ("MBA Mortgage Application Survey"                  , 135),
        CBI_INDUSTRIAL_TRENDS_SURVEY                    ("CBI Industrial Trends Survey"                     , 136),
        HOUSING_FINANCE                                 ("Housing Finance"                                  , 137),
        LABOUR_PRICE_INDEX                              ("Labour Price Index"                               , 138),
        INDUSTRIAL_INDEX                                ("Industrial Index"                                 , 139),
        BRC_RETAIL_SALES                                ("BRC Retail Sales"                                 , 140),
        
        REDBOOK_RETAIL_SALES                            ("Redbook Retail Sales"                             , 141),
        MEDIAN_EXISTING_HOME_PRICE                      ("Median Existing Home Price"                       , 142),
        US_ICSC_GOLDMAN_SACHS_CHAIN_STORE_SALES         ("US ICSC-Goldman Sachs Chain Store Sales"          , 143),
        ECONOMIC_SENTIMENT                              ("Economic Sentiment"                               , 144),
        ABC_NEWS_CONSUMER_CONFIDENCE                    ("ABC News Consumer Confidence"                     , 145),
        DJ_BTMU_BUSINESS_BAROMETE                       ("DJ-BTMU Business Baromete"                        , 146),
        SMALL_BUSINESS_OPTIMISM_INDEX                   ("Small Business Optimism Index"                    , 147),
        LABOR_COST_INDEX                                ("Labor Cost Index"                                 , 148),
        KANSAS_CITY_FED_MFG_INDEX                       ("Kansas City Fed Mfg Index"                        , 150),
        
        NAPM_NY_BUSINESS_INDEX                          ("NAPM-NY Business Index"                           , 151),
        INDUSTRIAL_CAPACITY_UTILIZATION_RATE            ("Industrial Capacity Utilization Rate"             , 152),
        REUTERS_UNIVERSITY_OF_MICHIGAN_CONSUMER_SENTIMENT_SURVEY_PRELIM     ("Reuters/University of Michigan Consumer Sentiment Survey (Prelim)"   , 153),
        BUSINESS_INDICATORS                             ("Business Indicators"                              , 154),
        BUILDING_PERMITS                                ("Building Permits"                                 , 155),
        NEW_HOUSING_PRICE_INDEX                         ("New Housing Price Index"                          , 156),
        ISM_NY_BUSINESS_INDEX                           ("ISM NY Business Index"                            , 157),
        EIA_DOE_WEEKLY_PETROLIUM_STATUS_REPORT          ("EIA/DOE Weekly Petrolium Status Report"           , 300),
        EIA_DOE_Weekly_Natural_Gas_Storage_Report       ("EIA/DOE Weekly Natural Gas Storage Report"        , 301),
        API_WEEKLY_STATISTICAL_BULLETIN                 ("API Weekly Statistical Bulletin"                  , 302);

        public final String description;
        public final int code;

        private EventCategory(String description, int code) {
            this.description = description;
            this.code = code;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
    
    

	/**
	 * Returns subscription's news sources
	 * 
	 * @return news sources as {@link NewsSource}
	 */
	Set<NewsSource> getNewsSources();

	/**
	 * Returns subscription's the first news source
	 * 
	 * @return news source as {@link NewsSource}
	 * @deprecated use {@link INewsFilter#getNewsSources()}}
	 */
	@Deprecated
	NewsSource getNewsSource();
	
	/**
	 * Returns subscription to "hot" news state
	 * 
	 * @return true if subscription is only for "hot" news
	 */
	boolean isOnlyHot();

	/**
	 * Returns subscription's countries
	 * 
	 * @return countries as {@link Set} of {@link Country}
	 */
	Set<Country> getCountries();

	/**
	 * Returns subscription's stock indicies
	 * 
	 * @return stock indices as {@link Set} of {@link StockIndex}
	 */
	Set<StockIndex> getStockIndicies();

	/**
	 * Returns subscription's market sectors
	 * 
	 * @return market sectors as {@link Set} of {@link MarketSector}
	 */
	Set<MarketSector> getMarketSectors();

	/**
	 * Returns subscription's currencies
	 * 
	 * @return currencies as {@link Set} of {@link Currency}
	 */
	Set<Currency> getCurrencies();

	/**
	 * Returns subscription's event categories
	 * 
	 * @return event categories as {@link Set} of {@link EventCategory}
	 */
	Set<EventCategory> getEventCategories();

	/**
	 * Returns subscription's period's start
	 * 
	 * @return period's start as {@link Date}
	 */
	Date getFrom();

	/**
	 * Returns subscription's period's end
	 * 
	 * @return period's end as {@link Date}
	 */
	Date getTo();

	/**
	 * Returns subscription's keywords
	 * 
	 * @return keywords as {@link Set} of {@link String}
	 */
	Set<String> getKeywords();

	/**
	 * Returns subscription's news type
	 * 
	 * @return news type as {@link Type}
	 */
	Type getType();
	
	/**
	 * Marker annotation for news filter elements accepted by Market News filter.
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface AcceptByMarketNewsFilter {}
}