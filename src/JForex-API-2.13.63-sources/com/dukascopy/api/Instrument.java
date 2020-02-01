/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.dukascopy.api.instrument.IFinancialInstrument;
import com.dukascopy.api.instrument.IInstrumentGroup;
import com.dukascopy.api.instrument.IMarketInfo;

/**
 * Defines currency pairs traded by Dukascopy.
 * Not all pairs predefined as static. To get others use {@link #fromString(String instrumentStr)} method.
 */
public class Instrument implements IFinancialInstrument, Comparable<Instrument> {

    private static int num = 0;

    private static final Map<String, Instrument> INSTRUMENTS_BY_NAME = new LinkedHashMap<>();
    private static final Map<String, Instrument> INSTRUMENTS_BY_STRING = new HashMap<>();

    public static final Comparator<Instrument> COMPARATOR = new InstrumentComparator();

    public static final Instrument AUDJPY = createForexInstrument("AUD", "JPY", 0.01,   2);
    public static final Instrument AUDCAD = createForexInstrument("AUD", "CAD", 0.0001, 4);
    public static final Instrument AUDCHF = createForexInstrument("AUD", "CHF", 0.0001, 4);
    public static final Instrument AUDNZD = createForexInstrument("AUD", "NZD", 0.0001, 4);
    public static final Instrument AUDSGD = createForexInstrument("AUD", "SGD", 0.0001, 4);
    public static final Instrument AUDUSD = createForexInstrument("AUD", "USD", 0.0001, 4);
    public static final Instrument CADCHF = createForexInstrument("CAD", "CHF", 0.0001, 4);
    public static final Instrument CADHKD = createForexInstrument("CAD", "HKD", 0.0001, 4);
    public static final Instrument CADJPY = createForexInstrument("CAD", "JPY", 0.01,   2);
    public static final Instrument CHFJPY = createForexInstrument("CHF", "JPY", 0.01,   2);
    public static final Instrument CHFPLN = createForexInstrument("CHF", "PLN", 0.0001, 4);
    public static final Instrument CHFSGD = createForexInstrument("CHF", "SGD", 0.0001, 4);
    public static final Instrument EURAUD = createForexInstrument("EUR", "AUD", 0.0001, 4);
    public static final Instrument EURBRL = createForexInstrument("EUR", "BRL", 0.0001, 4);
    public static final Instrument EURCAD = createForexInstrument("EUR", "CAD", 0.0001, 4);
    public static final Instrument EURCHF = createForexInstrument("EUR", "CHF", 0.0001, 4);
    public static final Instrument EURCZK = createForexInstrument("EUR", "CZK", 0.001,  3);
    public static final Instrument EURDKK = createForexInstrument("EUR", "DKK", 0.0001, 4);
    public static final Instrument EURGBP = createForexInstrument("EUR", "GBP", 0.0001, 4);
    public static final Instrument EURHKD = createForexInstrument("EUR", "HKD", 0.0001, 4);
    public static final Instrument EURHUF = createForexInstrument("EUR", "HUF", 0.01,   2);
    public static final Instrument EURJPY = createForexInstrument("EUR", "JPY", 0.01,   2);
    public static final Instrument EURMXN = createForexInstrument("EUR", "MXN", 0.0001, 4);
    public static final Instrument EURNOK = createForexInstrument("EUR", "NOK", 0.0001, 4);
    public static final Instrument EURNZD = createForexInstrument("EUR", "NZD", 0.0001, 4);
    public static final Instrument EURPLN = createForexInstrument("EUR", "PLN", 0.0001, 4);
    public static final Instrument EURRUB = createForexInstrument("EUR", "RUB", 0.0001, 4);
    public static final Instrument EURSEK = createForexInstrument("EUR", "SEK", 0.0001, 4);
    public static final Instrument EURSGD = createForexInstrument("EUR", "SGD", 0.0001, 4);
    public static final Instrument EURTHB = createForexInstrument("EUR", "THB", 0.001,  3);
    public static final Instrument EURTRY = createForexInstrument("EUR", "TRY", 0.0001, 4);
    public static final Instrument EURUSD = createForexInstrument("EUR", "USD", 0.0001, 4);
    public static final Instrument EURZAR = createForexInstrument("EUR", "ZAR", 0.0001, 4);
    public static final Instrument GBPAUD = createForexInstrument("GBP", "AUD", 0.0001, 4);
    public static final Instrument GBPCAD = createForexInstrument("GBP", "CAD", 0.0001, 4);
    public static final Instrument GBPCHF = createForexInstrument("GBP", "CHF", 0.0001, 4);
    public static final Instrument GBPJPY = createForexInstrument("GBP", "JPY", 0.01,   2);
    public static final Instrument GBPNZD = createForexInstrument("GBP", "NZD", 0.0001, 4);
    public static final Instrument GBPUSD = createForexInstrument("GBP", "USD", 0.0001, 4);
    public static final Instrument HKDJPY = createForexInstrument("HKD", "JPY", 0.0001, 4);
    public static final Instrument HUFJPY = createForexInstrument("HUF", "JPY", 0.0001, 4);
    public static final Instrument MXNJPY = createForexInstrument("MXN", "JPY", 0.0001, 4);
    public static final Instrument NZDCAD = createForexInstrument("NZD", "CAD", 0.0001, 4);
    public static final Instrument NZDCHF = createForexInstrument("NZD", "CHF", 0.0001, 4);
    public static final Instrument NZDJPY = createForexInstrument("NZD", "JPY", 0.01,   2);
    public static final Instrument NZDSGD = createForexInstrument("NZD", "SGD", 0.0001, 4);
    public static final Instrument NZDUSD = createForexInstrument("NZD", "USD", 0.0001, 4);
    public static final Instrument SGDJPY = createForexInstrument("SGD", "JPY", 0.01,   2);
    public static final Instrument USDBRL = createForexInstrument("USD", "BRL", 0.0001, 4);
    public static final Instrument USDCAD = createForexInstrument("USD", "CAD", 0.0001, 4);
    public static final Instrument USDCHF = createForexInstrument("USD", "CHF", 0.0001, 4);
    public static final Instrument USDCNH = createForexInstrument("USD", "CNH", 0.0001, 4);
    public static final Instrument USDCZK = createForexInstrument("USD", "CZK", 0.001,  3);
    public static final Instrument USDDKK = createForexInstrument("USD", "DKK", 0.0001, 4);
    public static final Instrument USDHKD = createForexInstrument("USD", "HKD", 0.0001, 4);
    public static final Instrument USDHUF = createForexInstrument("USD", "HUF", 0.01,   2);
    public static final Instrument USDILS = createForexInstrument("USD", "ILS", 0.0001, 4);
    public static final Instrument USDJPY = createForexInstrument("USD", "JPY", 0.01,   2);
    public static final Instrument USDMXN = createForexInstrument("USD", "MXN", 0.0001, 4);
    public static final Instrument USDNOK = createForexInstrument("USD", "NOK", 0.0001, 4);
    public static final Instrument USDPLN = createForexInstrument("USD", "PLN", 0.0001, 4);
    public static final Instrument USDRON = createForexInstrument("USD", "RON", 0.0001, 4);
    public static final Instrument USDRUB = createForexInstrument("USD", "RUB", 0.0001, 4);
    public static final Instrument USDSEK = createForexInstrument("USD", "SEK", 0.0001, 4);
    public static final Instrument USDSGD = createForexInstrument("USD", "SGD", 0.0001, 4);
    public static final Instrument USDTHB = createForexInstrument("USD", "THB", 0.001,  3);
    public static final Instrument USDTRY = createForexInstrument("USD", "TRY", 0.0001, 4);
    public static final Instrument USDZAR = createForexInstrument("USD", "ZAR", 0.0001, 4);
    public static final Instrument ZARJPY = createForexInstrument("ZAR", "JPY", 0.01,   2);
    public static final Instrument TRYJPY = createForexInstrument("TRY", "JPY", 0.01,   2);
    
    public static final Instrument XAGUSD = createPredefinedInstrument(Type.METAL, "XAG", "USD", 0.01, 2);
    public static final Instrument XAUUSD = createPredefinedInstrument(Type.METAL, "XAU", "USD", 0.01, 2);

    // CFD instruments
    public static final Instrument BRENTCMDUSD =   createCfdInstrument("BRENT.CMD", "USD");
    public static final Instrument LIGHTCMDUSD =   createCfdInstrument("LIGHT.CMD", "USD"); // WTI Light Crude Oil
    public static final Instrument DIESELCMDUSD =  createCfdInstrument("DIESEL.CMD",  "USD");
    public static final Instrument COTTONCMDUSD =  createCfdInstrument("COTTON.CMD",  "USD");
    public static final Instrument SUGARCMDUSD =   createCfdInstrument("SUGAR.CMD",   "USD");
    public static final Instrument OJUICECMDUSD =  createCfdInstrument("OJUICE.CMD",  "USD");
    public static final Instrument COFFEECMDUSD =  createCfdInstrument("COFFEE.CMD",  "USD");
    public static final Instrument COCOACMDUSD =   createCfdInstrument("COCOA.CMD",   "USD");
    public static final Instrument DEUIDXEUR =     createCfdInstrument("DEU.IDX", "EUR");
    public static final Instrument FRAIDXEUR =     createCfdInstrument("FRA.IDX", "EUR");
    public static final Instrument CHEIDXCHF =     createCfdInstrument("CHE.IDX", "CHF");
    public static final Instrument GBRIDXGBP =     createCfdInstrument("GBR.IDX", "GBP");
    public static final Instrument JPNIDXJPY =     createCfdInstrument("JPN.IDX", "JPY");
    public static final Instrument USA30IDXUSD =   createCfdInstrument("USA30.IDX",   "USD");
    public static final Instrument USATECHIDXUSD = createCfdInstrument("USATECH.IDX", "USD");
    public static final Instrument USA500IDXUSD =  createCfdInstrument("USA500.IDX",  "USD");


    public static final Instrument AUSIDXAUD =     createCfdInstrument("AUS.IDX", "AUD");  // Australia 200 Index
    public static final Instrument EUSIDXEUR =     createCfdInstrument("EUS.IDX", "EUR");  // Europe 50 Index
    public static final Instrument NLDIDXEUR =     createCfdInstrument("NLD.IDX", "EUR");  // Netherlands 25 Index
    public static final Instrument ITAIDXEUR =     createCfdInstrument("ITA.IDX", "EUR");  // Italy 40 Index
    public static final Instrument ESPIDXEUR =     createCfdInstrument("ESP.IDX", "EUR");  // Spain 30 Index
    public static final Instrument HKGIDXHKD =     createCfdInstrument("HKG.IDX", "HKD");  // Hong Kong Index
    public static final Instrument PRTIDXEUR =     createPredefinedInstrument(Type.CFD, "PRT.IDX",    "EUR", 0.0001, 4);  // Portugal Index
    public static final Instrument COPPERCMDUSD =  createPredefinedInstrument(Type.CFD, "COPPER.CMD", "USD", 0.001, 3);   // High Grade Copper
    public static final Instrument GASCMDUSD =     createPredefinedInstrument(Type.CFD, "GAS.CMD",    "USD", 0.001, 3);   // Natural Gas
    
    // Crypto
    public static final Instrument BTCUSD =       createForexInstrument("BTC", "USD", 1, 0); // Bitcoin
    public static final Instrument ETHUSD =       createForexInstrument("ETH", "USD", 1, 0); // Ethereum


    /**
     * @deprecated Predefined stocks could be removed in further releases. Use {@link #fromString(String instrumentStr)} instead.<br>
     *  Example: <code>fromString("MMM.US/USD")</code>
     */
    @Deprecated public static final Instrument MMMUSUSD =     createCfdInstrument("MMM.US",     "USD");        // 3M Co
    @Deprecated public static final Instrument AXPUSUSD =     createCfdInstrument("AXP.US",     "USD");        // American Express Co
    @Deprecated public static final Instrument TUSUSD =       createCfdInstrument("T.US",       "USD");        // AT&T Inc
    @Deprecated public static final Instrument BAUSUSD =      createCfdInstrument("BA.US",      "USD");        // Boeing Co/The
    @Deprecated public static final Instrument CATUSUSD =     createCfdInstrument("CAT.US",     "USD");        // Caterpillar Inc
    @Deprecated public static final Instrument CVXUSUSD =     createCfdInstrument("CVX.US",     "USD");        // Chevron Corp
    @Deprecated public static final Instrument CSCOUSUSD =    createCfdInstrument("CSCO.US",    "USD");        // Cisco Systems Inc
    @Deprecated public static final Instrument KOUSUSD =      createCfdInstrument("KO.US",      "USD");        // Coca-Cola Co/The
    @Deprecated public static final Instrument DDUSUSD =      createCfdInstrument("DD.US",      "USD");        // EI du Pont de Nemours & Co
    @Deprecated public static final Instrument XOMUSUSD =     createCfdInstrument("XOM.US",     "USD");        // Exxon Mobil Corp
    @Deprecated public static final Instrument GEUSUSD =      createCfdInstrument("GE.US",      "USD");        // General Electric Co
    @Deprecated public static final Instrument GSUSUSD =      createCfdInstrument("GS.US",      "USD");        // Goldman Sachs Group Inc/The
    @Deprecated public static final Instrument HDUSUSD =      createCfdInstrument("HD.US",      "USD");        // Home Depot Inc/The
    @Deprecated public static final Instrument INTCUSUSD =    createCfdInstrument("INTC.US",    "USD");        // Intel Corp
    @Deprecated public static final Instrument IBMUSUSD =     createCfdInstrument("IBM.US",     "USD");        // International Business Machines Corp
    @Deprecated public static final Instrument JNJUSUSD =     createCfdInstrument("JNJ.US",     "USD");        // Johnson & Johnson
    @Deprecated public static final Instrument JPMUSUSD =     createCfdInstrument("JPM.US",     "USD");        // JPMorgan Chase & Co
    @Deprecated public static final Instrument MCDUSUSD =     createCfdInstrument("MCD.US",     "USD");        // McDonald's Corp
    @Deprecated public static final Instrument MRKUSUSD =     createCfdInstrument("MRK.US",     "USD");        // Merck & Co Inc
    @Deprecated public static final Instrument MSFTUSUSD =    createCfdInstrument("MSFT.US",    "USD");        // Microsoft Corp
    @Deprecated public static final Instrument NKEUSUSD =     createCfdInstrument("NKE.US",     "USD");        // NIKE Inc
    @Deprecated public static final Instrument PFEUSUSD =     createCfdInstrument("PFE.US",     "USD");        // Pfizer Inc
    @Deprecated public static final Instrument PGUSUSD =      createCfdInstrument("PG.US",      "USD");        // Procter & Gamble Co/The
    @Deprecated public static final Instrument TRVUSUSD =     createCfdInstrument("TRV.US",     "USD");        // Travelers Cos Inc/The
    @Deprecated public static final Instrument UTXUSUSD =     createCfdInstrument("UTX.US",     "USD");        // United Technologies Corp
    @Deprecated public static final Instrument UNHUSUSD =     createCfdInstrument("UNH.US",     "USD");        // UnitedHealth Group Inc
    @Deprecated public static final Instrument VZUSUSD =      createCfdInstrument("VZ.US",      "USD");        // Verizon Communications Inc
    @Deprecated public static final Instrument VUSUSD =       createCfdInstrument("V.US",       "USD");        // Visa Inc
    @Deprecated public static final Instrument WMTUSUSD =     createCfdInstrument("WMT.US",     "USD");        // Wal-Mart Stores Inc
    @Deprecated public static final Instrument DISUSUSD =     createCfdInstrument("DIS.US",     "USD");        // Walt Disney Co/The
    @Deprecated public static final Instrument TSLAUSUSD =    createCfdInstrument("TSLA.US",    "USD");        //
    @Deprecated public static final Instrument AMZNUSUSD =    createCfdInstrument("AMZN.US",    "USD");        //
    @Deprecated public static final Instrument FBUSUSD =      createCfdInstrument("FB.US",      "USD");        //
    @Deprecated public static final Instrument ADBEUSUSD =    createCfdInstrument("ADBE.US",    "USD");        //
    @Deprecated public static final Instrument AAPLUSUSD =    createCfdInstrument("AAPL.US",    "USD");        //
    @Deprecated public static final Instrument EBAYUSUSD =    createCfdInstrument("EBAY.US",    "USD");        //
    @Deprecated public static final Instrument ALXNUSUSD =    createCfdInstrument("ALXN.US",    "USD");        //
    @Deprecated public static final Instrument YHOOUSUSD =    createCfdInstrument("YHOO.US",    "USD");        //
    @Deprecated public static final Instrument GOOGUSUSD =    createCfdInstrument("GOOG.US",    "USD");        //
    @Deprecated public static final Instrument SBUXUSUSD =    createCfdInstrument("SBUX.US",    "USD");        //
    @Deprecated public static final Instrument WINUSUSD =     createCfdInstrument("WIN.US",     "USD");        //
    @Deprecated public static final Instrument AVGOUSUSD =    createCfdInstrument("AVGO.US",    "USD");        //
    @Deprecated public static final Instrument LBTYAUSUSD =   createCfdInstrument("LBTYA.US",   "USD");        //
    @Deprecated public static final Instrument DISHUSUSD =    createCfdInstrument("DISH.US",    "USD");        //
    @Deprecated public static final Instrument CERNUSUSD =    createCfdInstrument("CERN.US",    "USD");        //
    @Deprecated public static final Instrument NXPIUSUSD =    createCfdInstrument("NXPI.US",    "USD");        //
    @Deprecated public static final Instrument XLNXUSUSD =    createCfdInstrument("XLNX.US",    "USD");        //
    @Deprecated public static final Instrument VRSKUSUSD =    createCfdInstrument("VRSK.US",    "USD");        //
    @Deprecated public static final Instrument ISRGUSUSD =    createCfdInstrument("ISRG.US",    "USD");        //
    @Deprecated public static final Instrument NDAQUSUSD =    createCfdInstrument("NDAQ.US",    "USD");        //
    @Deprecated public static final Instrument CAUSUSD =      createCfdInstrument("CA.US",      "USD");        //
    @Deprecated public static final Instrument PAYXUSUSD =    createCfdInstrument("PAYX.US",    "USD");        //
    @Deprecated public static final Instrument ZIONUSUSD =    createCfdInstrument("ZION.US",    "USD");        //
    @Deprecated public static final Instrument SIRIUSUSD =    createCfdInstrument("SIRI.US",    "USD");        //
    @Deprecated public static final Instrument AKAMUSUSD =    createCfdInstrument("AKAM.US",    "USD");        //
    @Deprecated public static final Instrument FFIVUSUSD =    createCfdInstrument("FFIV.US",    "USD");        //
    @Deprecated public static final Instrument PCLNUSUSD =    createCfdInstrument("PCLN.US",    "USD");        //
    @Deprecated public static final Instrument EXPDUSUSD =    createCfdInstrument("EXPD.US",    "USD");        //
    @Deprecated public static final Instrument HASUSUSD =     createCfdInstrument("HAS.US",     "USD");        //
    @Deprecated public static final Instrument NTRSUSUSD =    createCfdInstrument("NTRS.US",    "USD");        //
    @Deprecated public static final Instrument PCARUSUSD =    createCfdInstrument("PCAR.US",    "USD");        //
    @Deprecated public static final Instrument GOOGLUSUSD =   createCfdInstrument("GOOGL.US",   "USD");        //
    @Deprecated public static final Instrument ETFCUSUSD =    createCfdInstrument("ETFC.US",    "USD");        //
    @Deprecated public static final Instrument AALUSUSD =     createCfdInstrument("AAL.US",     "USD");        //
    @Deprecated public static final Instrument CTRXUSUSD =    createCfdInstrument("CTRX.US",    "USD");        //
    @Deprecated public static final Instrument AMATUSUSD =    createCfdInstrument("AMAT.US",    "USD");        //
    @Deprecated public static final Instrument ALTRUSUSD =    createCfdInstrument("ALTR.US",    "USD");        //
    @Deprecated public static final Instrument LMCKUSUSD =    createCfdInstrument("LMCK.US",    "USD");        //
    @Deprecated public static final Instrument QVCAUSUSD =    createCfdInstrument("QVCA.US",    "USD");        //
    @Deprecated public static final Instrument HSICUSUSD =    createCfdInstrument("HSIC.US",    "USD");        //
    @Deprecated public static final Instrument ADIUSUSD =     createCfdInstrument("ADI.US",     "USD");        //
    @Deprecated public static final Instrument ADSKUSUSD =    createCfdInstrument("ADSK.US",    "USD");        //
    @Deprecated public static final Instrument MDLZUSUSD =    createCfdInstrument("MDLZ.US",    "USD");        //
    @Deprecated public static final Instrument MYLUSUSD =     createCfdInstrument("MYL.US",     "USD");        //
    @Deprecated public static final Instrument TXNUSUSD =     createCfdInstrument("TXN.US",     "USD");        //
    @Deprecated public static final Instrument TRIPUSUSD =    createCfdInstrument("TRIP.US",    "USD");        //
    @Deprecated public static final Instrument GRMNUSUSD =    createCfdInstrument("GRMN.US",    "USD");        //
    @Deprecated public static final Instrument CTXSUSUSD =    createCfdInstrument("CTXS.US",    "USD");        //
    @Deprecated public static final Instrument NVDAUSUSD =    createCfdInstrument("NVDA.US",    "USD");        //
    @Deprecated public static final Instrument CTASUSUSD =    createCfdInstrument("CTAS.US",    "USD");        //
    @Deprecated public static final Instrument WYNNUSUSD =    createCfdInstrument("WYNN.US",    "USD");        //
    @Deprecated public static final Instrument DISCAUSUSD =   createCfdInstrument("DISCA.US",   "USD");        //
    @Deprecated public static final Instrument FASTUSUSD =    createCfdInstrument("FAST.US",    "USD");        //
    @Deprecated public static final Instrument MUUSUSD =      createCfdInstrument("MU.US",      "USD");        //
    @Deprecated public static final Instrument BRCMUSUSD =    createCfdInstrument("BRCM.US",    "USD");        //
    @Deprecated public static final Instrument INTUUSUSD =    createCfdInstrument("INTU.US",    "USD");        //
    @Deprecated public static final Instrument VIPUSUSD =     createCfdInstrument("VIP.US",     "USD");        //
    @Deprecated public static final Instrument NAVIUSUSD =    createCfdInstrument("NAVI.US",    "USD");        //
    @Deprecated public static final Instrument QCOMUSUSD =    createCfdInstrument("QCOM.US",    "USD");        //
    @Deprecated public static final Instrument TROWUSUSD =    createCfdInstrument("TROW.US",    "USD");        //
    @Deprecated public static final Instrument EQIXUSUSD =    createCfdInstrument("EQIX.US",    "USD");        //
    @Deprecated public static final Instrument LVNTAUSUSD =   createCfdInstrument("LVNTA.US",   "USD");        //
    @Deprecated public static final Instrument CMEUSUSD =     createCfdInstrument("CME.US",     "USD");        //
    @Deprecated public static final Instrument BBBYUSUSD =    createCfdInstrument("BBBY.US",    "USD");        //
    @Deprecated public static final Instrument ATVIUSUSD =    createCfdInstrument("ATVI.US",    "USD");        //
    @Deprecated public static final Instrument FOSLUSUSD =    createCfdInstrument("FOSL.US",    "USD");        //
    @Deprecated public static final Instrument MNSTUSUSD =    createCfdInstrument("MNST.US",    "USD");        //
    @Deprecated public static final Instrument CHRWUSUSD =    createCfdInstrument("CHRW.US",    "USD");        //
    @Deprecated public static final Instrument SPLSUSUSD =    createCfdInstrument("SPLS.US",    "USD");        //
    @Deprecated public static final Instrument STXUSUSD =     createCfdInstrument("STX.US",     "USD");        //
    @Deprecated public static final Instrument FISVUSUSD =    createCfdInstrument("FISV.US",    "USD");        //
    @Deprecated public static final Instrument KLACUSUSD =    createCfdInstrument("KLAC.US",    "USD");        //
    @Deprecated public static final Instrument PDCOUSUSD =    createCfdInstrument("PDCO.US",    "USD");        //
    @Deprecated public static final Instrument ADPUSUSD =     createCfdInstrument("ADP.US",     "USD");        //
    @Deprecated public static final Instrument TSCOUSUSD =    createCfdInstrument("TSCO.US",    "USD");        //
    @Deprecated public static final Instrument NFLXUSUSD =    createCfdInstrument("NFLX.US",    "USD");        //
    @Deprecated public static final Instrument ILMNUSUSD =    createCfdInstrument("ILMN.US",    "USD");        //
    @Deprecated public static final Instrument BIDUUSUSD =    createCfdInstrument("BIDU.US",    "USD");        //
    @Deprecated public static final Instrument XRAYUSUSD =    createCfdInstrument("XRAY.US",    "USD");        //
    @Deprecated public static final Instrument CHTRUSUSD =    createCfdInstrument("CHTR.US",    "USD");        //
    @Deprecated public static final Instrument SBACUSUSD =    createCfdInstrument("SBAC.US",    "USD");        //
    @Deprecated public static final Instrument DTVUSUSD =     createCfdInstrument("DTV.US",     "USD");        //
    @Deprecated public static final Instrument FOXUSUSD =     createCfdInstrument("FOX.US",     "USD");        //
    @Deprecated public static final Instrument REGNUSUSD =    createCfdInstrument("REGN.US",    "USD");        //
    @Deprecated public static final Instrument LBTYKUSUSD =   createCfdInstrument("LBTYK.US",   "USD");        //
    @Deprecated public static final Instrument SYMCUSUSD =    createCfdInstrument("SYMC.US",    "USD");        //
    @Deprecated public static final Instrument GTUSUSD =      createCfdInstrument("GT.US",      "USD");        //
    @Deprecated public static final Instrument SNDKUSUSD =    createCfdInstrument("SNDK.US",    "USD");        //
    @Deprecated public static final Instrument FOXAUSUSD =    createCfdInstrument("FOXA.US",    "USD");        //
    @Deprecated public static final Instrument CINFUSUSD =    createCfdInstrument("CINF.US",    "USD");        //
    @Deprecated public static final Instrument MARUSUSD =     createCfdInstrument("MAR.US",     "USD");        //
    @Deprecated public static final Instrument SRCLUSUSD =    createCfdInstrument("SRCL.US",    "USD");        //
    @Deprecated public static final Instrument FITBUSUSD =    createCfdInstrument("FITB.US",    "USD");        //
    @Deprecated public static final Instrument PBCTUSUSD =    createCfdInstrument("PBCT.US",    "USD");        //
    @Deprecated public static final Instrument CHKPUSUSD =    createCfdInstrument("CHKP.US",    "USD");        //
    @Deprecated public static final Instrument VODUSUSD =     createCfdInstrument("VOD.US",     "USD");        //
    @Deprecated public static final Instrument EAUSUSD =      createCfdInstrument("EA.US",      "USD");        //
    @Deprecated public static final Instrument ORLYUSUSD =    createCfdInstrument("ORLY.US",    "USD");        //
    @Deprecated public static final Instrument VIABUSUSD =    createCfdInstrument("VIAB.US",    "USD");        //
    @Deprecated public static final Instrument HBANUSUSD =    createCfdInstrument("HBAN.US",    "USD");        //
    @Deprecated public static final Instrument FLIRUSUSD =    createCfdInstrument("FLIR.US",    "USD");        //
    @Deprecated public static final Instrument FTRUSUSD =     createCfdInstrument("FTR.US",     "USD");        //
    @Deprecated public static final Instrument CMCSAUSUSD =   createCfdInstrument("CMCSA.US",   "USD");        //
    @Deprecated public static final Instrument ESRXUSUSD =    createCfdInstrument("ESRX.US",    "USD");        //
    @Deprecated public static final Instrument MCHPUSUSD =    createCfdInstrument("MCHP.US",    "USD");        //
    @Deprecated public static final Instrument BIIBUSUSD =    createCfdInstrument("BIIB.US",    "USD");        //
    @Deprecated public static final Instrument GMCRUSUSD =    createCfdInstrument("GMCR.US",    "USD");        //
    @Deprecated public static final Instrument VRSNUSUSD =    createCfdInstrument("VRSN.US",    "USD");        //
    @Deprecated public static final Instrument ROSTUSUSD =    createCfdInstrument("ROST.US",    "USD");        //
    @Deprecated public static final Instrument COSTUSUSD =    createCfdInstrument("COST.US",    "USD");        //
    @Deprecated public static final Instrument HCBKUSUSD =    createCfdInstrument("HCBK.US",    "USD");        //
    @Deprecated public static final Instrument DISCKUSUSD =   createCfdInstrument("DISCK.US",   "USD");        //
    @Deprecated public static final Instrument EXPEUSUSD =    createCfdInstrument("EXPE.US",    "USD");        //
    @Deprecated public static final Instrument CTSHUSUSD =    createCfdInstrument("CTSH.US",    "USD");        //
    @Deprecated public static final Instrument CELGUSUSD =    createCfdInstrument("CELG.US",    "USD");        //
    @Deprecated public static final Instrument AMGNUSUSD =    createCfdInstrument("AMGN.US",    "USD");        //
    @Deprecated public static final Instrument SIALUSUSD =    createCfdInstrument("SIAL.US",    "USD");        //
    @Deprecated public static final Instrument DLTRUSUSD =    createCfdInstrument("DLTR.US",    "USD");        //
    @Deprecated public static final Instrument CMCSKUSUSD =   createCfdInstrument("CMCSK.US",   "USD");        //
    @Deprecated public static final Instrument WDCUSUSD =     createCfdInstrument("WDC.US",     "USD");        //
    @Deprecated public static final Instrument FSLRUSUSD =    createCfdInstrument("FSLR.US",    "USD");        //
    @Deprecated public static final Instrument LLTCUSUSD =    createCfdInstrument("LLTC.US",    "USD");        //
    @Deprecated public static final Instrument WFMUSUSD =     createCfdInstrument("WFM.US",     "USD");        //
    @Deprecated public static final Instrument NWSAUSUSD =    createCfdInstrument("NWSA.US",    "USD");        //
    @Deprecated public static final Instrument LMCAUSUSD =    createCfdInstrument("LMCA.US",    "USD");        //
    @Deprecated public static final Instrument LRCXUSUSD =    createCfdInstrument("LRCX.US",    "USD");        //
    @Deprecated public static final Instrument MATUSUSD =     createCfdInstrument("MAT.US",     "USD");        //
    @Deprecated public static final Instrument URBNUSUSD =    createCfdInstrument("URBN.US",    "USD");        //
    @Deprecated public static final Instrument NTAPUSUSD =    createCfdInstrument("NTAP.US",    "USD");        //
    @Deprecated public static final Instrument GILDUSUSD =    createCfdInstrument("GILD.US",    "USD");        //
    @Deprecated public static final Instrument VRTXUSUSD =    createCfdInstrument("VRTX.US",    "USD");        //
    @Deprecated public static final Instrument PETMUSUSD =    createCfdInstrument("PETM.US",    "USD");        //
    @Deprecated public static final Instrument AEPUSUSD =     createCfdInstrument("AEP.US",     "USD");        // American Electric Power
    @Deprecated public static final Instrument SPYUSUSD =     createCfdInstrument("SPY.US",     "USD");

    @Deprecated public static final Instrument BABAUSUSD =    createCfdInstrument("BABA.US",    "USD");
    @Deprecated public static final Instrument BACUSUSD =     createCfdInstrument("BAC.US",     "USD");
    @Deprecated public static final Instrument CUSUSD =       createCfdInstrument("C.US",       "USD");
    @Deprecated public static final Instrument EEMUSUSD =     createCfdInstrument("EEM.US",     "USD");
    @Deprecated public static final Instrument EFAUSUSD =     createCfdInstrument("EFA.US",     "USD");
    @Deprecated public static final Instrument EWJUSUSD =     createCfdInstrument("EWJ.US",     "USD");
    @Deprecated public static final Instrument GDXUSUSD =     createCfdInstrument("GDX.US",     "USD");
    @Deprecated public static final Instrument GLDUSUSD =     createCfdInstrument("GLD.US",     "USD");
    @Deprecated public static final Instrument IWMUSUSD =     createCfdInstrument("IWM.US",     "USD");
    @Deprecated public static final Instrument IYRUSUSD =     createCfdInstrument("IYR.US",     "USD");
    @Deprecated public static final Instrument JNKUSUSD =     createCfdInstrument("JNK.US",     "USD");
    @Deprecated public static final Instrument QQQUSUSD =     createCfdInstrument("QQQ.US",     "USD");
    @Deprecated public static final Instrument TLTUSUSD =     createCfdInstrument("TLT.US",     "USD");
    @Deprecated public static final Instrument WFCUSUSD =     createCfdInstrument("WFC.US",     "USD");
    @Deprecated public static final Instrument XLFUSUSD =     createCfdInstrument("XLF.US",     "USD");
    @Deprecated public static final Instrument XLIUSUSD =     createCfdInstrument("XLI.US",     "USD");
    @Deprecated public static final Instrument XLPUSUSD =     createCfdInstrument("XLP.US",     "USD");
    @Deprecated public static final Instrument XIVUSUSD =     createCfdInstrument("XIV.US",     "USD");
    @Deprecated public static final Instrument VXXUSUSD =     createCfdInstrument("VXX.US",     "USD");
    @Deprecated public static final Instrument BBDUSUSD =     createCfdInstrument("BBD.US",     "USD");
    @Deprecated public static final Instrument ABEVUSUSD =    createCfdInstrument("ABEV.US",    "USD");
    @Deprecated public static final Instrument VALEUSUSD =    createCfdInstrument("VALE.US",    "USD");
    @Deprecated public static final Instrument FXIUSUSD =     createCfdInstrument("FXI.US",     "USD");
    @Deprecated public static final Instrument ITUBUSUSD =    createCfdInstrument("ITUB.US",    "USD");
    @Deprecated public static final Instrument PBRUSUSD =     createCfdInstrument("PBR.US",     "USD");
    @Deprecated public static final Instrument EWZUSUSD =     createCfdInstrument("EWZ.US",     "USD");
    @Deprecated public static final Instrument NOKUSUSD =     createCfdInstrument("NOK.US",     "USD");
    @Deprecated public static final Instrument AZNUSUSD =     createCfdInstrument("AZN.US",     "USD");
    @Deprecated public static final Instrument CSUSUSD =      createCfdInstrument("CS.US",      "USD");
    @Deprecated public static final Instrument LVSUSUSD =     createCfdInstrument("LVS.US",     "USD");
    @Deprecated public static final Instrument USOUSUSD =     createCfdInstrument("USO.US",     "USD");
    @Deprecated public static final Instrument XOPUSUSD =     createCfdInstrument("XOP.US",     "USD");
    @Deprecated public static final Instrument GDXJUSUSD =    createCfdInstrument("GDXJ.US",    "USD");
    @Deprecated public static final Instrument AUSUSD =       createCfdInstrument("A.US",       "USD");
    @Deprecated public static final Instrument ABTUSUSD =     createCfdInstrument("ABT.US",     "USD");
    @Deprecated public static final Instrument AETUSUSD =     createCfdInstrument("AET.US",     "USD");
    @Deprecated public static final Instrument AIGUSUSD =     createCfdInstrument("AIG.US",     "USD");
    @Deprecated public static final Instrument BBYUSUSD =     createCfdInstrument("BBY.US",     "USD");
    @Deprecated public static final Instrument BKUSUSD =      createCfdInstrument("BK.US",      "USD");
    @Deprecated public static final Instrument BMYUSUSD =     createCfdInstrument("BMY.US",     "USD");
    @Deprecated public static final Instrument BRKBUSUSD =    createCfdInstrument("BRKB.US",    "USD");
    @Deprecated public static final Instrument BSXUSUSD =     createCfdInstrument("BSX.US",     "USD");
    @Deprecated public static final Instrument CIUSUSD =      createCfdInstrument("CI.US",      "USD");
    @Deprecated public static final Instrument CMGUSUSD =     createCfdInstrument("CMG.US",     "USD");
    @Deprecated public static final Instrument CRMUSUSD =     createCfdInstrument("CRM.US",     "USD");
    @Deprecated public static final Instrument DEUSUSD =      createCfdInstrument("DE.US",      "USD");
    @Deprecated public static final Instrument DGUSUSD =      createCfdInstrument("DG.US",      "USD");
    @Deprecated public static final Instrument DIAUSUSD =     createCfdInstrument("DIA.US",     "USD");
    @Deprecated public static final Instrument DUKUSUSD =     createCfdInstrument("DUK.US",     "USD");
    @Deprecated public static final Instrument DVYUSUSD =     createCfdInstrument("DVY.US",     "USD");
    @Deprecated public static final Instrument EMBUSUSD =     createCfdInstrument("EMB.US",     "USD");
    @Deprecated public static final Instrument EWHUSUSD =     createCfdInstrument("EWH.US",     "USD");
    @Deprecated public static final Instrument EWWUSUSD =     createCfdInstrument("EWW.US",     "USD");
    @Deprecated public static final Instrument GLWUSUSD =     createCfdInstrument("GLW.US",     "USD");
    @Deprecated public static final Instrument IBBUSUSD =     createCfdInstrument("IBB.US",     "USD");
    @Deprecated public static final Instrument ICEUSUSD =     createCfdInstrument("ICE.US",     "USD");
    @Deprecated public static final Instrument IJRUSUSD =     createCfdInstrument("IJR.US",     "USD");
    @Deprecated public static final Instrument ITWUSUSD =     createCfdInstrument("ITW.US",     "USD");
    @Deprecated public static final Instrument IVEUSUSD =     createCfdInstrument("IVE.US",     "USD");
    @Deprecated public static final Instrument IVWUSUSD =     createCfdInstrument("IVW.US",     "USD");
    @Deprecated public static final Instrument JWNUSUSD =     createCfdInstrument("JWN.US",     "USD");
    @Deprecated public static final Instrument KUSUSD =       createCfdInstrument("K.US",       "USD");
    @Deprecated public static final Instrument KHCUSUSD =     createCfdInstrument("KHC.US",     "USD");
    @Deprecated public static final Instrument KMBUSUSD =     createCfdInstrument("KMB.US",     "USD");
    @Deprecated public static final Instrument LUSUSD =       createCfdInstrument("L.US",       "USD");
    @Deprecated public static final Instrument LENUSUSD =     createCfdInstrument("LEN.US",     "USD");
    @Deprecated public static final Instrument LLYUSUSD =     createCfdInstrument("LLY.US",     "USD");
    @Deprecated public static final Instrument LMTUSUSD =     createCfdInstrument("LMT.US",     "USD");
    @Deprecated public static final Instrument MAUSUSD =      createCfdInstrument("MA.US",      "USD");
    @Deprecated public static final Instrument NOCUSUSD =     createCfdInstrument("NOC.US",     "USD");
    @Deprecated public static final Instrument OXYUSUSD =     createCfdInstrument("OXY.US",     "USD");
    @Deprecated public static final Instrument NEMUSUSD =     createCfdInstrument("NEM.US",     "USD");
    @Deprecated public static final Instrument NEWUSUSD =     createCfdInstrument("NEW.US",     "USD");


    @Deprecated public static final Instrument _1299HKHKD =   createCfdInstrument("1299.HK",    "HKD");    // AIA Group Ltd
    @Deprecated public static final Instrument _3988HKHKD =   createCfdInstrument("3988.HK",    "HKD");    // Bank of China Ltd
    @Deprecated public static final Instrument _3328HKHKD =   createCfdInstrument("3328.HK",    "HKD");    // Bank of Communications Co Ltd
    @Deprecated public static final Instrument _0023HKHKD =   createCfdInstrument("0023.HK",    "HKD");    // Bank of East Asia Ltd
    @Deprecated public static final Instrument _1880HKHKD =   createCfdInstrument("1880.HK",    "HKD");    // Belle International Holdings Ltd
    @Deprecated public static final Instrument _2388HKHKD =   createCfdInstrument("2388.HK",    "HKD");    // BOC Hong Kong Holdings Ltd
    @Deprecated public static final Instrument _0293HKHKD =   createCfdInstrument("0293.HK",    "HKD");    // Cathay Pacific Airways Ltd
    @Deprecated public static final Instrument _0001HKHKD =   createCfdInstrument("0001.HK",    "HKD");    // Cheung Kong Holdings Ltd
    @Deprecated public static final Instrument _0939HKHKD =   createCfdInstrument("0939.HK",    "HKD");    // China Construction Bank Corp
    @Deprecated public static final Instrument _2628HKHKD =   createCfdInstrument("2628.HK",    "HKD");    // China Life Insurance Co Ltd
    @Deprecated public static final Instrument _2319HKHKD =   createCfdInstrument("2319.HK",    "HKD");    // China Mengniu Dairy Co Ltd
    @Deprecated public static final Instrument _0144HKHKD =   createCfdInstrument("0144.HK",    "HKD");    // China Merchants Holdings International C
    @Deprecated public static final Instrument _0941HKHKD =   createCfdInstrument("0941.HK",    "HKD");    // China Mobile Ltd
    @Deprecated public static final Instrument _0688HKHKD =   createCfdInstrument("0688.HK",    "HKD");    // China Overseas Land & Investment Ltd
    @Deprecated public static final Instrument _0386HKHKD =   createCfdInstrument("0386.HK",    "HKD");    // China Petroleum & Chemical Corp
    @Deprecated public static final Instrument _0291HKHKD =   createCfdInstrument("0291.HK",    "HKD");    // China Resources Enterprise Ltd
    @Deprecated public static final Instrument _1109HKHKD =   createCfdInstrument("1109.HK",    "HKD");    // China Resources Land Ltd
    @Deprecated public static final Instrument _0836HKHKD =   createCfdInstrument("0836.HK",    "HKD");    // China Resources Power Holdings Co Ltd
    @Deprecated public static final Instrument _1088HKHKD =   createCfdInstrument("1088.HK",    "HKD");    // China Shenhua Energy Co Ltd
    @Deprecated public static final Instrument _0762HKHKD =   createCfdInstrument("0762.HK",    "HKD");    // China Unicom Hong Kong Ltd
    @Deprecated public static final Instrument _0267HKHKD =   createCfdInstrument("0267.HK",    "HKD");    // CITIC Ltd
    @Deprecated public static final Instrument _0002HKHKD =   createCfdInstrument("0002.HK",    "HKD");    // CLP Holdings Ltd
    @Deprecated public static final Instrument _0883HKHKD =   createCfdInstrument("0883.HK",    "HKD");    // CNOOC Ltd
    @Deprecated public static final Instrument _0027HKHKD =   createCfdInstrument("0027.HK",    "HKD");    // Galaxy Entertainment Group Ltd
    @Deprecated public static final Instrument _0101HKHKD =   createCfdInstrument("0101.HK",    "HKD");    // Hang Lung Properties Ltd
    @Deprecated public static final Instrument _0011HKHKD =   createCfdInstrument("0011.HK",    "HKD");    // Hang Seng Bank Ltd
    @Deprecated public static final Instrument _0012HKHKD =   createCfdInstrument("0012.HK",    "HKD");    // Henderson Land Development Co Ltd
    @Deprecated public static final Instrument _1044HKHKD =   createCfdInstrument("1044.HK",    "HKD");    // Hengan International Group Co Ltd
    @Deprecated public static final Instrument _0003HKHKD =   createCfdInstrument("0003.HK",    "HKD");    // Hong Kong & China Gas Co Ltd
    @Deprecated public static final Instrument _0388HKHKD =   createCfdInstrument("0388.HK",    "HKD");    // Hong Kong Exchanges and Clearing Ltd
    @Deprecated public static final Instrument _0005HKHKD =   createCfdInstrument("0005.HK",    "HKD");    // HSBC Holdings PLC
    @Deprecated public static final Instrument _0013HKHKD =   createCfdInstrument("0013.HK",    "HKD");    // Hutchison Whampoa Ltd
    @Deprecated public static final Instrument _1398HKHKD =   createCfdInstrument("1398.HK",    "HKD");    // ICBC
    @Deprecated public static final Instrument _0135HKHKD =   createCfdInstrument("0135.HK",    "HKD");    // Kunlun Energy Co Ltd
    @Deprecated public static final Instrument _0992HKHKD =   createCfdInstrument("0992.HK",    "HKD");    // Lenovo Group Ltd
    @Deprecated public static final Instrument _0494HKHKD =   createCfdInstrument("0494.HK",    "HKD");    // Li & Fung Ltd
    @Deprecated public static final Instrument _0823HKHKD =   createCfdInstrument("0823.HK",    "HKD");    // Link REIT/The
    @Deprecated public static final Instrument _0066HKHKD =   createCfdInstrument("0066.HK",    "HKD");    // MTR Corp Ltd
    @Deprecated public static final Instrument _0017HKHKD =   createCfdInstrument("0017.HK",    "HKD");    // New World Development Co Ltd
    @Deprecated public static final Instrument _0857HKHKD =   createCfdInstrument("0857.HK",    "HKD");    // PetroChina Co Ltd
    @Deprecated public static final Instrument _2318HKHKD =   createCfdInstrument("2318.HK",    "HKD");    // Ping An Insurance Group Co of China Ltd
    @Deprecated public static final Instrument _0006HKHKD =   createCfdInstrument("0006.HK",    "HKD");    // Power Assets Holdings Ltd
    @Deprecated public static final Instrument _1928HKHKD =   createCfdInstrument("1928.HK",    "HKD");    // Sands China Ltd
    @Deprecated public static final Instrument _0083HKHKD =   createCfdInstrument("0083.HK",    "HKD");    // Sino Land Co Ltd
    @Deprecated public static final Instrument _0016HKHKD =   createCfdInstrument("0016.HK",    "HKD");    // Sun Hung Kai Properties Ltd
    @Deprecated public static final Instrument _0019HKHKD =   createCfdInstrument("0019.HK",    "HKD");    // Swire Pacific Ltd
    @Deprecated public static final Instrument _0700HKHKD =   createCfdInstrument("0700.HK",    "HKD");    // Tencent Holdings Ltd
    @Deprecated public static final Instrument _0322HKHKD =   createCfdInstrument("0322.HK",    "HKD");    // Tingyi Cayman Islands Holding Corp
    @Deprecated public static final Instrument _0151HKHKD =   createCfdInstrument("0151.HK",    "HKD");    // Want Want China Holdings Ltd
    @Deprecated public static final Instrument _0004HKHKD =   createCfdInstrument("0004.HK",    "HKD");    // Wharf Holdings Ltd/The

    @Deprecated public static final Instrument WNCACAD =      createCfdInstrument("WN.CA",      "CAD");        // George Weston Ltd
    @Deprecated public static final Instrument ELDCACAD =     createCfdInstrument("ELD.CA",     "CAD");        // Eldorado Gold Corp
    @Deprecated public static final Instrument CNRCACAD =     createCfdInstrument("CNR.CA",     "CAD");        // Canadian National Railway Co
    @Deprecated public static final Instrument COSCACAD =     createCfdInstrument("COS.CA",     "CAD");        // Canadian Oil Sands Ltd
    @Deprecated public static final Instrument ARXCACAD =     createCfdInstrument("ARX.CA",     "CAD");        // ARC Resources Ltd
    @Deprecated public static final Instrument SLFCACAD =     createCfdInstrument("SLF.CA",     "CAD");        // Sun Life Financial Inc
    @Deprecated public static final Instrument CCOCACAD =     createCfdInstrument("CCO.CA",     "CAD");        // Cameco Corp
    @Deprecated public static final Instrument BAMACACAD =    createCfdInstrument("BAM-A.CA",   "CAD");        // Brookfield Asset Management Inc
    @Deprecated public static final Instrument SAPCACAD =     createCfdInstrument("SAP.CA",     "CAD");        // Saputo Inc
    @Deprecated public static final Instrument PPLCACAD =     createCfdInstrument("PPL.CA",     "CAD");        // Pembina Pipeline Corp
    @Deprecated public static final Instrument FMCACAD =      createCfdInstrument("FM.CA",      "CAD");        // First Quantum Minerals Ltd
    @Deprecated public static final Instrument GILCACAD =     createCfdInstrument("GIL.CA",     "CAD");        // Gildan Activewear Inc
    @Deprecated public static final Instrument MFCCACAD =     createCfdInstrument("MFC.CA",     "CAD");        // Manulife Financial Corp
    @Deprecated public static final Instrument LCACAD =       createCfdInstrument("L.CA",       "CAD");        // Loblaw Cos Ltd
    @Deprecated public static final Instrument CPCACAD =      createCfdInstrument("CP.CA",      "CAD");        // Canadian Pacific Railway Ltd
    @Deprecated public static final Instrument MRUCACAD =     createCfdInstrument("MRU.CA",     "CAD");        // Metro Inc
    @Deprecated public static final Instrument HSECACAD =     createCfdInstrument("HSE.CA",     "CAD");        // Husky Energy Inc
    @Deprecated public static final Instrument BMOCACAD =     createCfdInstrument("BMO.CA",     "CAD");        // Bank of Montreal
    @Deprecated public static final Instrument BNSCACAD =     createCfdInstrument("BNS.CA",     "CAD");        // Bank of Nova Scotia/The
    @Deprecated public static final Instrument CMCACAD =      createCfdInstrument("CM.CA",      "CAD");        // Canadian Imperial Bank of Commerce/Canada
    @Deprecated public static final Instrument NACACAD =      createCfdInstrument("NA.CA",      "CAD");        // National Bank of Canada
    @Deprecated public static final Instrument TDCACAD =      createCfdInstrument("TD.CA",      "CAD");        // Toronto-Dominion Bank/The
    @Deprecated public static final Instrument CPGCACAD =     createCfdInstrument("CPG.CA",     "CAD");        // Crescent Point Energy Corp
    @Deprecated public static final Instrument SUCACAD =      createCfdInstrument("SU.CA",      "CAD");        // Suncor Energy Inc
    @Deprecated public static final Instrument ABXCACAD =     createCfdInstrument("ABX.CA",     "CAD");        // Barrick Gold Corp
    @Deprecated public static final Instrument BCECACAD =     createCfdInstrument("BCE.CA",     "CAD");        // BCE Inc
    @Deprecated public static final Instrument CCTCACAD =     createCfdInstrument("CCT.CA",     "CAD");        // Catamaran Corp
    @Deprecated public static final Instrument CVECACAD =     createCfdInstrument("CVE.CA",     "CAD");        // Cenovus Energy Inc
    @Deprecated public static final Instrument POTCACAD =     createCfdInstrument("POT.CA",     "CAD");        // Potash Corp of Saskatchewan Inc
    @Deprecated public static final Instrument TRPCACAD =     createCfdInstrument("TRP.CA",     "CAD");        // TransCanada Corp
    @Deprecated public static final Instrument VRXCACAD =     createCfdInstrument("VRX.CA",     "CAD");        // Valeant Pharmaceuticals International Inc
    @Deprecated public static final Instrument IMOCACAD =     createCfdInstrument("IMO.CA",     "CAD");        // Imperial Oil Ltd
    @Deprecated public static final Instrument POWCACAD =     createCfdInstrument("POW.CA",     "CAD");        // Power Corp of Canada
    @Deprecated public static final Instrument ATDBCACAD =    createCfdInstrument("ATD-B.CA",   "CAD");        // Alimentation Couche-Tard Inc
    @Deprecated public static final Instrument RYCACAD =      createCfdInstrument("RY.CA",      "CAD");        // Royal Bank of Canada
    @Deprecated public static final Instrument AEMCACAD =     createCfdInstrument("AEM.CA",     "CAD");        // Agnico Eagle Mines Ltd
    @Deprecated public static final Instrument BBDBCACAD =    createCfdInstrument("BBD-B.CA",   "CAD");        // Bombardier Inc
    @Deprecated public static final Instrument TLMCACAD =     createCfdInstrument("TLM.CA",     "CAD");        // Talisman Energy Inc
    @Deprecated public static final Instrument TCACAD =       createCfdInstrument("T.CA",       "CAD");        // TELUS Corp
    @Deprecated public static final Instrument CNQCACAD =     createCfdInstrument("CNQ.CA",     "CAD");        // Canadian Natural Resources Ltd
    @Deprecated public static final Instrument CTCACACAD =    createCfdInstrument("CTC-A.CA",   "CAD");        // Canadian Tire Corp Ltd
    @Deprecated public static final Instrument GIBACACAD =    createCfdInstrument("GIB-A.CA",   "CAD");        // CGI Group Inc
    @Deprecated public static final Instrument QSRCACAD =     createCfdInstrument("QSR.CA",     "CAD");        // Restaurant Brands International Inc
    @Deprecated public static final Instrument BBCACAD =      createCfdInstrument("BB.CA",      "CAD");        // BlackBerry Ltd
    @Deprecated public static final Instrument FTSCACAD =     createCfdInstrument("FTS.CA",     "CAD");        // Fortis Inc/Canada
    @Deprecated public static final Instrument GCACAD =       createCfdInstrument("G.CA",       "CAD");        // Goldcorp Inc
    @Deprecated public static final Instrument ECACACAD =     createCfdInstrument("ECA.CA",     "CAD");        // Encana Corp
    @Deprecated public static final Instrument ENBCACAD =     createCfdInstrument("ENB.CA",     "CAD");        // Enbridge Inc
    @Deprecated public static final Instrument MGCACAD =      createCfdInstrument("MG.CA",      "CAD");        // Magna International Inc
    @Deprecated public static final Instrument SJRBCACAD =    createCfdInstrument("SJR-B.CA",   "CAD");        // Shaw Communications Inc
    @Deprecated public static final Instrument SNCCACAD =     createCfdInstrument("SNC.CA",     "CAD");        // SNC-Lavalin Group Inc
    @Deprecated public static final Instrument TCKBCACAD =    createCfdInstrument("TCK-B.CA",   "CAD");        // Teck Resources Ltd
    @Deprecated public static final Instrument TRICACAD =     createCfdInstrument("TRI.CA",     "CAD");        // Thomson Reuters Corp
    @Deprecated public static final Instrument AGUCACAD =     createCfdInstrument("AGU.CA",     "CAD");        // Agrium Inc
    @Deprecated public static final Instrument IPLCACAD =     createCfdInstrument("IPL.CA",     "CAD");        // Inter Pipeline Ltd
    @Deprecated public static final Instrument RCIBCACAD =    createCfdInstrument("RCI-B.CA",   "CAD");        // Rogers Communications Inc
    @Deprecated public static final Instrument KCACAD =       createCfdInstrument("K.CA",       "CAD");        // Kinross Gold Corp
    @Deprecated public static final Instrument TACACAD =      createCfdInstrument("TA.CA",      "CAD");        // TransAlta Corp
    @Deprecated public static final Instrument YRICACAD =     createCfdInstrument("YRI.CA",     "CAD");        // Yamana Gold Inc
    @Deprecated public static final Instrument SLWCACAD =     createCfdInstrument("SLW.CA",     "CAD");        // Silver Wheaton Corp

    @Deprecated public static final Instrument DBSSGSGD =     createCfdInstrument("DBS.SG",     "SGD");        // DBS Group Holdings Ltd
    @Deprecated public static final Instrument OCBCSGSGD =    createCfdInstrument("OCBC.SG",    "SGD");        // Oversea-Chinese Banking Corp Ltd
    @Deprecated public static final Instrument STSGSGD =      createCfdInstrument("ST.SG",      "SGD");        // Singapore Telecommunications Ltd
    @Deprecated public static final Instrument UOBSGSGD =     createCfdInstrument("UOB.SG",     "SGD");        // United Overseas Bank Ltd
    @Deprecated public static final Instrument JMSGSGD =      createCfdInstrument("JM.SG",      "SGD");        // Jardine Matheson Holdings Ltd
    @Deprecated public static final Instrument KEPSGSGD =     createCfdInstrument("KEP.SG",     "SGD");        // Keppel Corp Ltd
    @Deprecated public static final Instrument HKLSGSGD =     createCfdInstrument("HKL.SG",     "SGD");        // Hongkong Land Holdings Ltd
    @Deprecated public static final Instrument JSSGSGD =      createCfdInstrument("JS.SG",      "SGD");        // Jardine Strategic Holdings Ltd
    @Deprecated public static final Instrument CAPLSGSGD =    createCfdInstrument("CAPL.SG",    "SGD");        // CapitaLand Ltd
    @Deprecated public static final Instrument GLPSGSGD =     createCfdInstrument("GLP.SG",     "SGD");        // Global Logistic Properties Ltd
    @Deprecated public static final Instrument THBEVSGSGD =   createCfdInstrument("THBEV.SG",   "SGD");        // Thai Beverage PCL
    @Deprecated public static final Instrument WILSGSGD =     createCfdInstrument("WIL.SG",     "SGD");        // Wilmar International Ltd
    @Deprecated public static final Instrument SPHSGSGD =     createCfdInstrument("SPH.SG",     "SGD");        // Singapore Press Holdings Ltd
    @Deprecated public static final Instrument GENSSGSGD =    createCfdInstrument("GENS.SG",    "SGD");        // Genting Singapore PLC
    @Deprecated public static final Instrument SGXSGSGD =     createCfdInstrument("SGX.SG",     "SGD");        // Singapore Exchange Ltd
    @Deprecated public static final Instrument SIASGSGD =     createCfdInstrument("SIA.SG",     "SGD");        // Singapore Airlines Ltd
    @Deprecated public static final Instrument CITSGSGD =     createCfdInstrument("CIT.SG",     "SGD");        // City Developments Ltd
    @Deprecated public static final Instrument CDSGSGD =      createCfdInstrument("CD.SG",      "SGD");        // ComfortDelGro Corp Ltd
    @Deprecated public static final Instrument CTSGSGD =      createCfdInstrument("CT.SG",      "SGD");        // CapitaMall Trust
    @Deprecated public static final Instrument STESGSGD =     createCfdInstrument("STE.SG",     "SGD");        // Singapore Technologies Engineering Ltd
    @Deprecated public static final Instrument HPHTSGSGD =    createCfdInstrument("HPHT.SG",    "SGD");        // Hutchison Port Holdings Trust
    @Deprecated public static final Instrument AREITSGSGD =   createCfdInstrument("AREIT.SG",   "SGD");        // Ascendas Real Estate Investment Trust
    @Deprecated public static final Instrument NOBLSGSGD =    createCfdInstrument("NOBL.SG",    "SGD");        // Noble Group Ltd
    @Deprecated public static final Instrument JCNCSGSGD =    createCfdInstrument("JCNC.SG",    "SGD");        // Jardine Cycle & Carriage Ltd
    @Deprecated public static final Instrument SCISGSGD =     createCfdInstrument("SCI.SG",     "SGD");        // Sembcorp Industries Ltd
    @Deprecated public static final Instrument GGRSGSGD =     createCfdInstrument("GGR.SG",     "SGD");        // Golden Agri-Resources Ltd
    @Deprecated public static final Instrument SMMSGSGD =     createCfdInstrument("SMM.SG",     "SGD");        // Sembcorp Marine Ltd
    @Deprecated public static final Instrument STHSGSGD =     createCfdInstrument("STH.SG",     "SGD");        // StarHub Ltd
    @Deprecated public static final Instrument SIESGSGD =     createCfdInstrument("SIE.SG",     "SGD");        // SIA Engineering Co Ltd
    @Deprecated public static final Instrument OLAMSGSGD =    createCfdInstrument("OLAM.SG",    "SGD");        // Olam International Ltd


    @Deprecated public static final Instrument ADSDEEUR =     createCfdInstrument("ADS.DE",     "EUR");        // adidas AG
    @Deprecated public static final Instrument ALVDEEUR =     createCfdInstrument("ALV.DE",     "EUR");        // Allianz SE
    @Deprecated public static final Instrument BASDEEUR =     createCfdInstrument("BAS.DE",     "EUR");        // BASF SE
    @Deprecated public static final Instrument BAYNDEEUR =    createCfdInstrument("BAYN.DE",    "EUR");        // Bayer AG
    @Deprecated public static final Instrument BMWDEEUR =     createCfdInstrument("BMW.DE",     "EUR");        // Bayerische Motoren Werke AG
    @Deprecated public static final Instrument BEIDEEUR =     createCfdInstrument("BEI.DE",     "EUR");        // Beiersdorf AG
    @Deprecated public static final Instrument CBKDEEUR =     createCfdInstrument("CBK.DE",     "EUR");        // Commerzbank AG
    @Deprecated public static final Instrument CONDEEUR =     createCfdInstrument("CON.DE",     "EUR");        // Continental AG
    @Deprecated public static final Instrument DAIDEEUR =     createCfdInstrument("DAI.DE",     "EUR");        // Daimler AG
    @Deprecated public static final Instrument DBKDEEUR =     createCfdInstrument("DBK.DE",     "EUR");        // Deutsche Bank AG
    @Deprecated public static final Instrument DB1DEEUR =     createCfdInstrument("DB1.DE",     "EUR");        // Deutsche Boerse AG
    @Deprecated public static final Instrument LHADEEUR =     createCfdInstrument("LHA.DE",     "EUR");        // Deutsche Lufthansa AG
    @Deprecated public static final Instrument DPWDEEUR =     createCfdInstrument("DPW.DE",     "EUR");        // Deutsche Post AG
    @Deprecated public static final Instrument DTEDEEUR =     createCfdInstrument("DTE.DE",     "EUR");        // Deutsche Telekom AG
    @Deprecated public static final Instrument EOANDEEUR =    createCfdInstrument("EOAN.DE",    "EUR");        // E.ON SE
    @Deprecated public static final Instrument FMEDEEUR =     createCfdInstrument("FME.DE",     "EUR");        // Fresenius Medical Care AG & Co KGaA
    @Deprecated public static final Instrument FREDEEUR =     createCfdInstrument("FRE.DE",     "EUR");        // Fresenius SE & Co KGaA
    @Deprecated public static final Instrument HEIDEEUR =     createCfdInstrument("HEI.DE",     "EUR");        // HeidelbergCement AG
    @Deprecated public static final Instrument HEN3DEEUR =    createCfdInstrument("HEN3.DE",    "EUR");        // Henkel AG & Co KGaA
    @Deprecated public static final Instrument IFXDEEUR =     createCfdInstrument("IFX.DE",     "EUR");        // Infineon Technologies AG
    @Deprecated public static final Instrument SDFDEEUR =     createCfdInstrument("SDF.DE",     "EUR");        // K+S AG
    @Deprecated public static final Instrument LXSDEEUR =     createCfdInstrument("LXS.DE",     "EUR");        // LANXESS AG
    @Deprecated public static final Instrument LINDEEUR =     createCfdInstrument("LIN.DE",     "EUR");        // Linde AG
    @Deprecated public static final Instrument MRKDEEUR =     createCfdInstrument("MRK.DE",     "EUR");        // Merck KGaA
    @Deprecated public static final Instrument MUV2DEEUR =    createCfdInstrument("MUV2.DE",    "EUR");        // Munich Re
    @Deprecated public static final Instrument RWEDEEUR =     createCfdInstrument("RWE.DE",     "EUR");        // RWE AG
    @Deprecated public static final Instrument SAPDEEUR =     createCfdInstrument("SAP.DE",     "EUR");        // SAP SE
    @Deprecated public static final Instrument SIEDEEUR =     createCfdInstrument("SIE.DE",     "EUR");        // Siemens AG
    @Deprecated public static final Instrument TKADEEUR =     createCfdInstrument("TKA.DE",     "EUR");        // ThyssenKrupp AG
    @Deprecated public static final Instrument VOW3DEEUR =    createCfdInstrument("VOW3.DE",    "EUR");        // Volkswagen AG
    @Deprecated public static final Instrument DRW3DEEUR =    createCfdInstrument("DRW3.DE",    "EUR");        //
    @Deprecated public static final Instrument DUEDEEUR =     createCfdInstrument("DUE.DE",     "EUR");        //

    @Deprecated public static final Instrument BOSSDEEUR =    createCfdInstrument("BOSS.DE",    "EUR");        // HUGO BOSS AG
    @Deprecated public static final Instrument PAH3DEEUR =    createCfdInstrument("PAH3.DE",    "EUR");        // Porsche Automobil Holding SE
    @Deprecated public static final Instrument PSMDEEUR =     createCfdInstrument("PSM.DE",     "EUR");        // ProSiebenSat.1 Media AG
    @Deprecated public static final Instrument TUI1DEEUR =    createCfdInstrument("TUI1.DE",    "EUR");        // TUI AG
    @Deprecated public static final Instrument VNADEEUR =     createCfdInstrument("VNA.DE",     "EUR");        // Vonovia SE

    @Deprecated public static final Instrument AIRFREUR =     createCfdInstrument("AIR.FR",     "EUR");        // Airbus Group NV
    @Deprecated public static final Instrument ALUFREUR =     createCfdInstrument("ALU.FR",     "EUR");        // Alcatel-Lucent
    @Deprecated public static final Instrument ALOFREUR =     createCfdInstrument("ALO.FR",     "EUR");        // Alstom SA
    @Deprecated public static final Instrument MTNNLEUR =     createCfdInstrument("MTN.NL",     "EUR");        // ArcelorMittal
    @Deprecated public static final Instrument CSFREUR =      createCfdInstrument("CS.FR",      "EUR");        // AXA SA
    @Deprecated public static final Instrument BNPFREUR =     createCfdInstrument("BNP.FR",     "EUR");        // BNP Paribas SA
    @Deprecated public static final Instrument ENFREUR =      createCfdInstrument("EN.FR",      "EUR");        // Bouygues SA
    @Deprecated public static final Instrument CAPFREUR =     createCfdInstrument("CAP.FR",     "EUR");        // Cap Gemini SA
    @Deprecated public static final Instrument CAFREUR =      createCfdInstrument("CA.FR",      "EUR");        // Carrefour SA
    @Deprecated public static final Instrument SGOFREUR =     createCfdInstrument("SGO.FR",     "EUR");        // Cie de St-Gobain
    @Deprecated public static final Instrument MLFREUR =      createCfdInstrument("ML.FR",      "EUR");        // Cie Generale des Etablissements Michelin
    @Deprecated public static final Instrument ACAFREUR =     createCfdInstrument("ACA.FR",     "EUR");        // Credit Agricole SA
    @Deprecated public static final Instrument BNFREUR =      createCfdInstrument("BN.FR",      "EUR");        // Danone SA
    @Deprecated public static final Instrument EDFFREUR =     createCfdInstrument("EDF.FR",     "EUR");        // Electricite de France SA
    @Deprecated public static final Instrument EIFREUR =      createCfdInstrument("EI.FR",      "EUR");        // Essilor International SA
    @Deprecated public static final Instrument GSZFREUR =     createCfdInstrument("GSZ.FR",     "EUR");        // GDF Suez
    @Deprecated public static final Instrument KERFREUR =     createCfdInstrument("KER.FR",     "EUR");        // Kering
    @Deprecated public static final Instrument ORFREUR =      createCfdInstrument("OR.FR",      "EUR");        // L'Oreal SA
    @Deprecated public static final Instrument LGFREUR =      createCfdInstrument("LG.FR",      "EUR");        // Lafarge SA
    @Deprecated public static final Instrument LRFREUR =      createCfdInstrument("LR.FR",      "EUR");        // Legrand SA
    @Deprecated public static final Instrument MCFREUR =      createCfdInstrument("MC.FR",      "EUR");        // LVMH Moet Hennessy Louis Vuitton SA
    @Deprecated public static final Instrument ORAFREUR =     createCfdInstrument("ORA.FR",     "EUR");        // Orange SA
    @Deprecated public static final Instrument RIFREUR =      createCfdInstrument("RI.FR",      "EUR");        // Pernod Ricard SA
    @Deprecated public static final Instrument PUBFREUR =     createCfdInstrument("PUB.FR",     "EUR");        // Publicis Groupe SA
    @Deprecated public static final Instrument RNOFREUR =     createCfdInstrument("RNO.FR",     "EUR");        // Renault SA
    @Deprecated public static final Instrument SAFFREUR =     createCfdInstrument("SAF.FR",     "EUR");        // Safran SA
    @Deprecated public static final Instrument SANFREUR =     createCfdInstrument("SAN.FR",     "EUR");        // Sanofi
    @Deprecated public static final Instrument SUFREUR =      createCfdInstrument("SU.FR",      "EUR");        // Schneider Electric SE
    @Deprecated public static final Instrument GLEFREUR =     createCfdInstrument("GLE.FR",     "EUR");        // Societe Generale SA
    @Deprecated public static final Instrument SOLBLEUR =     createCfdInstrument("SOL.BL",     "EUR");        // Solvay SA
    @Deprecated public static final Instrument TECFREUR =     createCfdInstrument("TEC.FR",     "EUR");        // Technip SA
    @Deprecated public static final Instrument FPFREUR =      createCfdInstrument("FP.FR",      "EUR");        // Total SA
    @Deprecated public static final Instrument ULNAEUR =      createCfdInstrument("UL.NA",      "EUR");        // Unibail-Rodamco SE
    @Deprecated public static final Instrument FRFREUR =      createCfdInstrument("FR.FR",      "EUR");        // Valeo SA
    @Deprecated public static final Instrument VIEFREUR =     createCfdInstrument("VIE.FR",     "EUR");        // Veolia Environnement SA
    @Deprecated public static final Instrument DGFREUR =      createCfdInstrument("DG.FR",      "EUR");        // Vinci SA
    @Deprecated public static final Instrument VIVFREUR =     createCfdInstrument("VIV.FR",     "EUR");        // Vivendi SA
    @Deprecated public static final Instrument UGFREUR =      createCfdInstrument("UG.FR",      "EUR");        // Peugeot S.A.
    @Deprecated public static final Instrument ACFREUR =      createCfdInstrument("AC.FR",      "EUR");
    @Deprecated public static final Instrument AFFREUR =      createCfdInstrument("AF.FR",      "EUR");
    @Deprecated public static final Instrument AIFREUR =      createCfdInstrument("AI.FR",      "EUR");
    @Deprecated public static final Instrument LIFREUR =      createCfdInstrument("LI.FR",      "EUR");
    @Deprecated public static final Instrument VKFREUR =      createCfdInstrument("VK.FR",      "EUR");

    @Deprecated public static final Instrument ENGIFREUR =    createCfdInstrument("ENGI.FR",    "EUR");
    @Deprecated public static final Instrument LHNFREUR =     createCfdInstrument("LHN.FR",     "EUR");

    @Deprecated public static final Instrument ABBNCHCHF =    createCfdInstrument("ABBN.CH",    "CHF");
    @Deprecated public static final Instrument ADENCHCHF =    createCfdInstrument("ADEN.CH",    "CHF");
    @Deprecated public static final Instrument CLNCHCHF =     createCfdInstrument("CLN.CH",     "CHF");
    @Deprecated public static final Instrument CSGNCHCHF =    createCfdInstrument("CSGN.CH",    "CHF");
    @Deprecated public static final Instrument GIVNCHCHF =    createCfdInstrument("GIVN.CH",    "CHF");
    @Deprecated public static final Instrument BAERCHCHF =    createCfdInstrument("BAER.CH",    "CHF");
    @Deprecated public static final Instrument KNINCHCHF =    createCfdInstrument("KNIN.CH",    "CHF");
    @Deprecated public static final Instrument LHNCHCHF =     createCfdInstrument("LHN.CH",     "CHF");
    @Deprecated public static final Instrument LONNCHCHF =    createCfdInstrument("LONN.CH",    "CHF");
    @Deprecated public static final Instrument NESNCHCHF =    createCfdInstrument("NESN.CH",    "CHF");
    @Deprecated public static final Instrument NOVNCHCHF =    createCfdInstrument("NOVN.CH",    "CHF");
    @Deprecated public static final Instrument ROGCHCHF =     createCfdInstrument("ROG.CH",     "CHF");
    @Deprecated public static final Instrument SGSNCHCHF =    createCfdInstrument("SGSN.CH",    "CHF");
    @Deprecated public static final Instrument SIKCHCHF =     createCfdInstrument("SIK.CH",     "CHF");
    @Deprecated public static final Instrument SOONCHCHF =    createCfdInstrument("SOON.CH",    "CHF");
    @Deprecated public static final Instrument UHRCHCHF =     createCfdInstrument("UHR.CH",     "CHF");
    @Deprecated public static final Instrument SLHNCHCHF =    createCfdInstrument("SLHN.CH",    "CHF");
    @Deprecated public static final Instrument SRENCHCHF =    createCfdInstrument("SREN.CH",    "CHF");
    @Deprecated public static final Instrument SCMNCHCHF =    createCfdInstrument("SCMN.CH",    "CHF");
    @Deprecated public static final Instrument RIGNCHCHF =    createCfdInstrument("RIGN.CH",    "CHF");
    @Deprecated public static final Instrument UBSGCHCHF =    createCfdInstrument("UBSG.CH",    "CHF");
    @Deprecated public static final Instrument ZURNCHCHF =    createCfdInstrument("ZURN.CH",    "CHF");

    @Deprecated public static final Instrument AALGBGBX =     createCfdInstrument("AAL.GB",     "GBX");
    @Deprecated public static final Instrument ANTOGBGBX =    createCfdInstrument("ANTO.GB",    "GBX");
    @Deprecated public static final Instrument ARMGBGBX =     createCfdInstrument("ARM.GB",     "GBX");
    @Deprecated public static final Instrument AHTGBGBX =     createCfdInstrument("AHT.GB",     "GBX");
    @Deprecated public static final Instrument ABFGBGBX =     createCfdInstrument("ABF.GB",     "GBX");
    @Deprecated public static final Instrument AZNGBGBX =     createCfdInstrument("AZN.GB",     "GBX");
    @Deprecated public static final Instrument AVGBGBX =      createCfdInstrument("AV.GB",      "GBX");
    @Deprecated public static final Instrument BABGBGBX =     createCfdInstrument("BAB.GB",     "GBX");
    @Deprecated public static final Instrument BAGBGBX =      createCfdInstrument("BA.GB",      "GBX");
    @Deprecated public static final Instrument BARCGBGBX =    createCfdInstrument("BARC.GB",    "GBX");
    @Deprecated public static final Instrument BGGBGBX =      createCfdInstrument("BG.GB",      "GBX");
    @Deprecated public static final Instrument BLTGBGBX =     createCfdInstrument("BLT.GB",     "GBX");
    @Deprecated public static final Instrument BPGBGBX =      createCfdInstrument("BP.GB",      "GBX");
    @Deprecated public static final Instrument BATSGBGBX =    createCfdInstrument("BATS.GB",    "GBX");
    @Deprecated public static final Instrument BLNDGBGBX =    createCfdInstrument("BLND.GB",    "GBX");
    @Deprecated public static final Instrument BTGBGBX =      createCfdInstrument("BT.GB",      "GBX");
    @Deprecated public static final Instrument BNZLGBGBX =    createCfdInstrument("BNZL.GB",    "GBX");
    @Deprecated public static final Instrument BRBYGBGBX =    createCfdInstrument("BRBY.GB",    "GBX");
    @Deprecated public static final Instrument CPIGBGBX =     createCfdInstrument("CPI.GB",     "GBX");
    @Deprecated public static final Instrument CCLGBGBX =     createCfdInstrument("CCL.GB",     "GBX");
    @Deprecated public static final Instrument CANGBGBX =     createCfdInstrument("CAN.GB",     "GBX");
    @Deprecated public static final Instrument CPGGBGBX =     createCfdInstrument("CPG.GB",     "GBX");
    @Deprecated public static final Instrument CRHGBGBX =     createCfdInstrument("CRH.GB",     "GBX");
    @Deprecated public static final Instrument CRDAGBGBX =    createCfdInstrument("CRDA.GB",    "GBX");
    @Deprecated public static final Instrument DGEGBGBX =     createCfdInstrument("DGE.GB",     "GBX");
    @Deprecated public static final Instrument EZJGBGBX =     createCfdInstrument("EZJ.GB",     "GBX");
    @Deprecated public static final Instrument EXPNGBGBX =    createCfdInstrument("EXPN.GB",    "GBX");
    @Deprecated public static final Instrument FRESGBGBX =    createCfdInstrument("FRES.GB",    "GBX");
    @Deprecated public static final Instrument GFSGBGBX =     createCfdInstrument("GFS.GB",     "GBX");
    @Deprecated public static final Instrument GKNGBGBX =     createCfdInstrument("GKN.GB",     "GBX");
    @Deprecated public static final Instrument GSKGBGBX =     createCfdInstrument("GSK.GB",     "GBX");
    @Deprecated public static final Instrument GLENGBGBX =    createCfdInstrument("GLEN.GB",    "GBX");
    @Deprecated public static final Instrument HMSOGBGBX =    createCfdInstrument("HMSO.GB",    "GBX");
    @Deprecated public static final Instrument HSBAGBGBX =    createCfdInstrument("HSBA.GB",    "GBX");
    @Deprecated public static final Instrument IMTGBGBX =     createCfdInstrument("IMT.GB",     "GBX");
    @Deprecated public static final Instrument ISATGBGBX =    createCfdInstrument("ISAT.GB",    "GBX");
    @Deprecated public static final Instrument IHGGBGBX =     createCfdInstrument("IHG.GB",     "GBX");
    @Deprecated public static final Instrument ITRKGBGBX =    createCfdInstrument("ITRK.GB",    "GBX");
    @Deprecated public static final Instrument ITVGBGBX =     createCfdInstrument("ITV.GB",     "GBX");
    @Deprecated public static final Instrument KGFGBGBX =     createCfdInstrument("KGF.GB",     "GBX");
    @Deprecated public static final Instrument LANDGBGBX =    createCfdInstrument("LAND.GB",    "GBX");
    @Deprecated public static final Instrument LGENGBGBX =    createCfdInstrument("LGEN.GB",    "GBX");
    @Deprecated public static final Instrument LLOYGBGBX =    createCfdInstrument("LLOY.GB",    "GBX");
    @Deprecated public static final Instrument LSEGBGBX =     createCfdInstrument("LSE.GB",     "GBX");
    @Deprecated public static final Instrument MKSGBGBX =     createCfdInstrument("MKS.GB",     "GBX");
    @Deprecated public static final Instrument MNDIGBGBX =    createCfdInstrument("MNDI.GB",    "GBX");
    @Deprecated public static final Instrument NGGBGBX =      createCfdInstrument("NG.GB",      "GBX");
    @Deprecated public static final Instrument NXTGBGBX =     createCfdInstrument("NXT.GB",     "GBX");
    @Deprecated public static final Instrument OMLGBGBX =     createCfdInstrument("OML.GB",     "GBX");
    @Deprecated public static final Instrument PSONGBGBX =    createCfdInstrument("PSON.GB",    "GBX");
    @Deprecated public static final Instrument PSNGBGBX =     createCfdInstrument("PSN.GB",     "GBX");
    @Deprecated public static final Instrument PFCGBGBX =     createCfdInstrument("PFC.GB",     "GBX");
    @Deprecated public static final Instrument PRUGBGBX =     createCfdInstrument("PRU.GB",     "GBX");
    @Deprecated public static final Instrument RRSGBGBX =     createCfdInstrument("RRS.GB",     "GBX");
    @Deprecated public static final Instrument RBGBGBX =      createCfdInstrument("RB.GB",      "GBX");
    @Deprecated public static final Instrument RELGBGBX =     createCfdInstrument("REL.GB",     "GBX");
    @Deprecated public static final Instrument REXGBGBX =     createCfdInstrument("REX.GB",     "GBX");
    @Deprecated public static final Instrument RIOGBGBX =     createCfdInstrument("RIO.GB",     "GBX");
    @Deprecated public static final Instrument RRGBGBX =      createCfdInstrument("RR.GB",      "GBX");
    @Deprecated public static final Instrument RBSGBGBX =     createCfdInstrument("RBS.GB",     "GBX");
    @Deprecated public static final Instrument RDSAGBGBX =    createCfdInstrument("RDSA.GB",    "GBX");
    @Deprecated public static final Instrument RDSBGBGBX =    createCfdInstrument("RDSB.GB",    "GBX");
    @Deprecated public static final Instrument RMGGBGBX =     createCfdInstrument("RMG.GB",     "GBX");
    @Deprecated public static final Instrument RSAGBGBX =     createCfdInstrument("RSA.GB",     "GBX");
    @Deprecated public static final Instrument SABGBGBX =     createCfdInstrument("SAB.GB",     "GBX");
    @Deprecated public static final Instrument SGEGBGBX =     createCfdInstrument("SGE.GB",     "GBX");
    @Deprecated public static final Instrument SBRYGBGBX =    createCfdInstrument("SBRY.GB",    "GBX");
    @Deprecated public static final Instrument SVTGBGBX =     createCfdInstrument("SVT.GB",     "GBX");
    @Deprecated public static final Instrument SHPGBGBX =     createCfdInstrument("SHP.GB",     "GBX");
    @Deprecated public static final Instrument SKYGBGBX =     createCfdInstrument("SKY.GB",     "GBX");
    @Deprecated public static final Instrument SNGBGBX =      createCfdInstrument("SN.GB",      "GBX");
    @Deprecated public static final Instrument SMINGBGBX =    createCfdInstrument("SMIN.GB",    "GBX");
    @Deprecated public static final Instrument SSEGBGBX =     createCfdInstrument("SSE.GB",     "GBX");
    @Deprecated public static final Instrument STANGBGBX =    createCfdInstrument("STAN.GB",    "GBX");
    @Deprecated public static final Instrument TATEGBGBX =    createCfdInstrument("TATE.GB",    "GBX");
    @Deprecated public static final Instrument TSCOGBGBX =    createCfdInstrument("TSCO.GB",    "GBX");
    @Deprecated public static final Instrument TPKGBGBX =     createCfdInstrument("TPK.GB",     "GBX");
    @Deprecated public static final Instrument TLWGBGBX =     createCfdInstrument("TLW.GB",     "GBX");
    @Deprecated public static final Instrument ULVRGBGBX =    createCfdInstrument("ULVR.GB",    "GBX");
    @Deprecated public static final Instrument UUGBGBX =      createCfdInstrument("UU.GB",      "GBX");
    @Deprecated public static final Instrument VODGBGBX =     createCfdInstrument("VOD.GB",     "GBX");
    @Deprecated public static final Instrument WEIRGBGBX =    createCfdInstrument("WEIR.GB",    "GBX");
    @Deprecated public static final Instrument WTBGBGBX =     createCfdInstrument("WTB.GB",     "GBX");
    @Deprecated public static final Instrument MRWGBGBX =     createCfdInstrument("MRW.GB",     "GBX");
    @Deprecated public static final Instrument WOSGBGBX =     createCfdInstrument("WOS.GB",     "GBX");
    @Deprecated public static final Instrument WPPGBGBX =     createCfdInstrument("WPP.GB",     "GBX");
    @Deprecated public static final Instrument ADMGBGBX =     createCfdInstrument("ADM.GB",     "GBX");
    @Deprecated public static final Instrument AGKGBGBX =     createCfdInstrument("AGK.GB",     "GBX");
    @Deprecated public static final Instrument IAGGBGBX =     createCfdInstrument("IAG.GB",     "GBX");
    @Deprecated public static final Instrument CNAGBGBX =     createCfdInstrument("CNA.GB",     "GBX");

    // Austria
    @Deprecated public static final Instrument EBSATEUR =     createCfdInstrument("EBS.AT",     "EUR");            // Erste Group Bank AG CFD
    @Deprecated public static final Instrument IIAATEUR =     createCfdInstrument("IIA.AT",     "EUR");            // Immofinanz AG CFD
    @Deprecated public static final Instrument RBIATEUR =     createCfdInstrument("RBI.AT",     "EUR");            // Raiffeisen Bank International AG CFD
    @Deprecated public static final Instrument VOEATEUR =     createCfdInstrument("VOE.AT",     "EUR");            // Voestalpine AG CFD

    // Belgium
    @Deprecated public static final Instrument ABIBEEUR =     createCfdInstrument("ABI.BE",     "EUR");            // Anheuser-Busch InBev NV
    @Deprecated public static final Instrument AGSBEEUR =     createCfdInstrument("AGS.BE",     "EUR");            // Ageas
    @Deprecated public static final Instrument BELGBEEUR =    createCfdInstrument("BELG.BE",    "EUR");            // Proximus
    @Deprecated public static final Instrument KBCBEEUR =     createCfdInstrument("KBC.BE",     "EUR");            // KBC Groep NV
    @Deprecated public static final Instrument SOLBBEEUR =    createCfdInstrument("SOLB.BE",    "EUR");            // Solvay SA
    @Deprecated public static final Instrument UCBBEEUR =     createCfdInstrument("UCB.BE",     "EUR");            // UCB SA
    @Deprecated public static final Instrument UMIBEEUR =     createCfdInstrument("UMI.BE",     "EUR");            // Umicore SA

    // Denmark
    @Deprecated public static final Instrument CARLBDKDKK =   createCfdInstrument("CARLB.DK",   "DKK");            // Carlsberg A/S
    @Deprecated public static final Instrument COLOBDKDKK =   createCfdInstrument("COLOB.DK",   "DKK");            // Coloplast A/S
    @Deprecated public static final Instrument DANSKEDKDKK =  createCfdInstrument("DANSKE.DK",  "DKK");            // Danske Bank A/S
    @Deprecated public static final Instrument MAERSKBDKDKK = createCfdInstrument("MAERSKB.DK", "DKK");            // AP Moeller - Maersk A/S
    @Deprecated public static final Instrument NOVOBDKDKK =   createCfdInstrument("NOVOB.DK",   "DKK");            // Novo Nordisk A/S
    @Deprecated public static final Instrument NZYMBDKDKK =   createCfdInstrument("NZYMB.DK",   "DKK");            // Novozymes A/S
    @Deprecated public static final Instrument PNDORADKDKK =  createCfdInstrument("PNDORA.DK",  "DKK");            // Pandora A/S
    @Deprecated public static final Instrument VWSDKDKK =     createCfdInstrument("VWS.DK",     "DKK");            // Vestas Wind Systems A/S

    // Finland
    @Deprecated public static final Instrument ELI1VFIEUR =   createCfdInstrument("ELI1V.FI",   "EUR");            // Elisa OYJ
    @Deprecated public static final Instrument NES1VFIEUR =   createCfdInstrument("NES1V.FI",   "EUR");            // Neste OYJ
    @Deprecated public static final Instrument NRE1VFIEUR =   createCfdInstrument("NRE1V.FI",   "EUR");            // Nokian Renkaat OYJ
    @Deprecated public static final Instrument OUT1VFIEUR =   createCfdInstrument("OUT1V.FI",   "EUR");            // Outokumpu OYJ
    @Deprecated public static final Instrument OTE1VFIEUR =   createCfdInstrument("OTE1V.FI",   "EUR");            // Outotec OYJ
    @Deprecated public static final Instrument STERVFIEUR =   createCfdInstrument("STERV.FI",   "EUR");            // Stora Enso OYJ
    @Deprecated public static final Instrument TLS1VFIEUR =   createCfdInstrument("TLS1V.FI",   "EUR");            // TeliaSonera AB

    // Netherlands
    @Deprecated public static final Instrument AGNNLEUR =     createCfdInstrument("AGN.NL",     "EUR");            // Aegon NV
    @Deprecated public static final Instrument AHNLEUR =      createCfdInstrument("AH.NL",      "EUR");            // Koninklijke Ahold Delhaize NV
    @Deprecated public static final Instrument AKZANLEUR =    createCfdInstrument("AKZA.NL",    "EUR");            // Akzo Nobel NV
    @Deprecated public static final Instrument ASMLNLEUR =    createCfdInstrument("ASML.NL",    "EUR");            // ASML Holding NV
    @Deprecated public static final Instrument DSMNLEUR =     createCfdInstrument("DSM.NL",     "EUR");            // Koninklijke DSM NV
    @Deprecated public static final Instrument GTONLEUR =     createCfdInstrument("GTO.NL",     "EUR");            // Gemalto NV
    @Deprecated public static final Instrument HEIANLEUR =    createCfdInstrument("HEIA.NL",    "EUR");            // Heineken NV
    @Deprecated public static final Instrument INGANLEUR =    createCfdInstrument("INGA.NL",    "EUR");            // ING Groep NV
    @Deprecated public static final Instrument KPNNLEUR =     createCfdInstrument("KPN.NL",     "EUR");            // Koninklijke KPN NV
    @Deprecated public static final Instrument MTNLEUR =      createCfdInstrument("MT.NL",      "EUR");            // ArcelorMittal
    @Deprecated public static final Instrument PHIANLEUR =    createCfdInstrument("PHIA.NL",    "EUR");            // Koninklijke Philips NV
    @Deprecated public static final Instrument RANDNLEUR =    createCfdInstrument("RAND.NL",    "EUR");            // Randstad Holding NV
    @Deprecated public static final Instrument RDSANLEUR =    createCfdInstrument("RDSA.NL",    "EUR");            // Royal Dutch Shell PLC
    @Deprecated public static final Instrument RENNLEUR =     createCfdInstrument("REN.NL",     "EUR");            // RELX NV
    @Deprecated public static final Instrument ULNLEUR =      createCfdInstrument("UL.NL",      "EUR");            // Unibail-Rodamco SE
    @Deprecated public static final Instrument UNANLEUR =     createCfdInstrument("UNA.NL",     "EUR");            // Unilever NV
    @Deprecated public static final Instrument VPKNLEUR =     createCfdInstrument("VPK.NL",     "EUR");            // Koninklijke Vopak NV
    @Deprecated public static final Instrument WKLNLEUR =     createCfdInstrument("WKL.NL",     "EUR");            // Wolters Kluwer NV

    // Norway
    @Deprecated public static final Instrument DNBNONOK =     createCfdInstrument("DNB.NO",     "NOK");            // DNB ASA
    @Deprecated public static final Instrument FRONONOK =     createCfdInstrument("FRO.NO",     "NOK");            // Frontline Ltd
    @Deprecated public static final Instrument MHGNONOK =     createCfdInstrument("MHG.NO",     "NOK");            // Marine Harvest ASA
    @Deprecated public static final Instrument NHYNONOK =     createCfdInstrument("NHY.NO",     "NOK");            // Norsk Hydro ASA
    @Deprecated public static final Instrument ORKNONOK =     createCfdInstrument("ORK.NO",     "NOK");            // Orkla ASA
    @Deprecated public static final Instrument STLNONOK =     createCfdInstrument("STL.NO",     "NOK");            // Statoil ASA
    @Deprecated public static final Instrument TELNONOK =     createCfdInstrument("TEL.NO",     "NOK");            // Telenor ASA

    // Portugal
    @Deprecated public static final Instrument EDPPTEUR =     createCfdInstrument("EDP.PT",     "EUR");            // EDP - Energias de Portugal SA
    @Deprecated public static final Instrument GALPPTEUR =    createCfdInstrument("GALP.PT",    "EUR");            // Galp Energia SGPS SA

    // Spain
    @Deprecated public static final Instrument ABEESEUR =     createCfdInstrument("ABE.ES",     "EUR");            // Abertis Infraestructuras SA
    @Deprecated public static final Instrument ACSESEUR =     createCfdInstrument("ACS.ES",     "EUR");            // ACS Actividades de Construccion y Servicios SA
    @Deprecated public static final Instrument ACXESEUR =     createCfdInstrument("ACX.ES",     "EUR");            // Acerinox SA
    @Deprecated public static final Instrument AENAESEUR =    createCfdInstrument("AENA.ES",    "EUR");            // Aena SA
    @Deprecated public static final Instrument AMSESEUR =     createCfdInstrument("AMS.ES",     "EUR");            // Amadeus IT Holding SA
    @Deprecated public static final Instrument BBVAESEUR =    createCfdInstrument("BBVA.ES",    "EUR");            // Banco Bilbao Vizcaya Argentaria SA
    @Deprecated public static final Instrument CABKESEUR =    createCfdInstrument("CABK.ES",    "EUR");            // CaixaBank
    @Deprecated public static final Instrument DIAESEUR =     createCfdInstrument("DIA.ES",     "EUR");            // Distribuidora Internacional de Alimentacion SA
    @Deprecated public static final Instrument ELEESEUR =     createCfdInstrument("ELE.ES",     "EUR");            // Endesa SA
    @Deprecated public static final Instrument ENGESEUR =     createCfdInstrument("ENG.ES",     "EUR");            // Enagas SA
    @Deprecated public static final Instrument FERESEUR =     createCfdInstrument("FER.ES",     "EUR");            // Ferrovial SA
    @Deprecated public static final Instrument GAMESEUR =     createCfdInstrument("GAM.ES",     "EUR");            // Gamesa Corporacion Tecnologica SA
    @Deprecated public static final Instrument GASESEUR =     createCfdInstrument("GAS.ES",     "EUR");            // Gas Natural SDG SA
    @Deprecated public static final Instrument IBEESEUR =     createCfdInstrument("IBE.ES",     "EUR");            // Iberdrola SA
    @Deprecated public static final Instrument ITXESEUR =     createCfdInstrument("ITX.ES",     "EUR");            // Inditex SA
    @Deprecated public static final Instrument REEESEUR =     createCfdInstrument("REE.ES",     "EUR");            // Red Electrica Corp SA
    @Deprecated public static final Instrument REPESEUR =     createCfdInstrument("REP.ES",     "EUR");            // Repsol SA
    @Deprecated public static final Instrument SANESEUR =     createCfdInstrument("SAN.ES",     "EUR");            // Banco Santander SA
    @Deprecated public static final Instrument TEFESEUR =     createCfdInstrument("TEF.ES",     "EUR");            // Telefonica SA

    // Sweden
    @Deprecated public static final Instrument ABBSESEK =     createCfdInstrument("ABB.SE",     "SEK");            // ABB Ltd
    @Deprecated public static final Instrument ALFASESEK =    createCfdInstrument("ALFA.SE",    "SEK");            // Alfa Laval AB
    @Deprecated public static final Instrument ATCOASESEK =   createCfdInstrument("ATCOA.SE",   "SEK");            // Atlas Copco AB
    @Deprecated public static final Instrument ELUXBSESEK =   createCfdInstrument("ELUXB.SE",   "SEK");            // Electrolux AB
    @Deprecated public static final Instrument ERICBSESEK =   createCfdInstrument("ERICB.SE",   "SEK");            // Telefonaktiebolaget LM Ericsson
    @Deprecated public static final Instrument GETIBSESEK =   createCfdInstrument("GETIB.SE",   "SEK");            // Getinge AB
    @Deprecated public static final Instrument HMBSESEK =     createCfdInstrument("HMB.SE",     "SEK");            // Hennes & Mauritz AB
    @Deprecated public static final Instrument INVEBSESEK =   createCfdInstrument("INVEB.SE",   "SEK");            // Investor AB
    @Deprecated public static final Instrument NDASESEK =     createCfdInstrument("NDA.SE",     "SEK");            // Nordea Bank AB
    @Deprecated public static final Instrument SANDSESEK =    createCfdInstrument("SAND.SE",    "SEK");            // Sandvik AB
    @Deprecated public static final Instrument SCABSESEK =    createCfdInstrument("SCAB.SE",    "SEK");            // Svenska Cellulosa AB
    @Deprecated public static final Instrument SEBASESEK =    createCfdInstrument("SEBA.SE",    "SEK");            // Skandinaviska Enskilda Banken AB
    @Deprecated public static final Instrument SECUBSESEK =   createCfdInstrument("SECUB.SE",   "SEK");            // Securitas AB
    @Deprecated public static final Instrument SKABSESEK =    createCfdInstrument("SKAB.SE",    "SEK");            // Skanska AB
    @Deprecated public static final Instrument SKFBSESEK =    createCfdInstrument("SKFB.SE",    "SEK");            // SKF AB
    @Deprecated public static final Instrument SWEDASESEK =   createCfdInstrument("SWEDA.SE",   "SEK");            // Swedbank AB
    @Deprecated public static final Instrument SWMASESEK =    createCfdInstrument("SWMA.SE",    "SEK");            // Swedish Match AB
    @Deprecated public static final Instrument TEL2BSESEK =   createCfdInstrument("TEL2B.SE",   "SEK");            // Tele2 AB
    @Deprecated public static final Instrument TLSNSESEK =    createCfdInstrument("TLSN.SE",    "SEK");            // Telia Company AB
    @Deprecated public static final Instrument VOLVBSESEK =   createCfdInstrument("VOLVB.SE",   "SEK");            // Volvo AB


    private ICurrency primaryCurrency;
    private ICurrency secondaryCurrency;
    private double pipValue;
    private int pipScale;
    private String stringValue;

    private int ordinal;
    private String name;
    
    // inherited from FinancialInstrument
    private Type type;
    private IInstrumentGroup group;
    private int tickScale;
    private double minTradeAmount;
    private double maxTradeAmount;
    private double tradeAmountIncrement;
    /** Amount of underlying asset per contract for CFD instruments */
    private double amountPerContract;
    private String description;
    private double marginUse;
    private String country;
    private IMarketInfo marketInfo;
    private boolean exotic;
    private boolean tradable;
    
    

    private static Instrument createForexInstrument(String primaryCurrency, String secondaryCurrency, double pipValue, int pipScale) {
        return createPredefinedInstrument(Type.FOREX, primaryCurrency, secondaryCurrency, pipValue, pipScale);
    }
    
    private static Instrument createCfdInstrument(String primaryCurrency, String secondaryCurrency) {
        return createPredefinedInstrument(Type.CFD, primaryCurrency, secondaryCurrency, 0.01, 2);
    }
    
    private static Instrument createPredefinedInstrument(Type type, String primaryCurrency, String secondaryCurrency, double pipValue, int pipScale) {
        Instrument instrument = new Instrument(num++, type, JFCurrency.getInstance(primaryCurrency), JFCurrency.getInstance(secondaryCurrency), pipValue, pipScale);
        INSTRUMENTS_BY_NAME.put(instrument.name(), instrument);
        INSTRUMENTS_BY_STRING.put(instrument.toString(), instrument);
        return instrument;
    }

    /*
     * List of values could be changed after relogin.
     */
    public static Instrument[] values() {
        return INSTRUMENTS_BY_NAME.values().toArray(new Instrument[INSTRUMENTS_BY_NAME.size()]);
    }
    
    public static int size() {
        return INSTRUMENTS_BY_NAME.size();
    }
    
    public static Instrument valueOf(String name) {
        Instrument res = INSTRUMENTS_BY_NAME.get(name);
        if (res == null) {
            res = INSTRUMENTS_BY_STRING.get(name);
        }
        return res;
    }

    
    private Instrument(int ordinal, Type type, ICurrency primaryCurrency, ICurrency secondaryCurrency, double pipValue, int pipScale) {
        this.ordinal = ordinal;
        this.type = type;
        this.name = (primaryCurrency.toString() + secondaryCurrency.toString()).replaceAll("[^a-zA-Z0-9]+", "");
        if (Character.isDigit(this.name.charAt(0))) {
            this.name = "_" + this.name;
        }
        this.primaryCurrency = primaryCurrency;
        this.secondaryCurrency = secondaryCurrency;
        this.pipValue = pipValue;
        this.pipScale = pipScale;
        this.tickScale = pipScale + 1;
        this.minTradeAmount = (type == Type.FOREX ? 1_000 : 1); // default, may be overridden in runtime after instrument subscription
        this.maxTradeAmount = 500_000_000; // default, may be overridden in runtime after instrument subscription
        this.tradeAmountIncrement = 1; // default, may be overridden in runtime after instrument subscription
        this.amountPerContract = 1; // default, may be overridden in runtime after instrument subscription
        this.stringValue = primaryCurrency + getPairsSeparator() + secondaryCurrency;
    }

    
    public int ordinal() {
        return this.ordinal;
    }

    public String name() {
        return this.name;
    }
    
    @Override
    public String getName() {
        return stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    /**
     * Returns currency separator
     *
     * @return currency separator
     */
    public static String getPairsSeparator() {
        return "/";
    }

    /**
     * Returns corresponding Instrument for string in "CUR1/CUR2" format
     *
     * @param instrumentStr string in "CUR1/CUR2" format
     * @return corresponding Instrument or null if no Instrument was found for specified string
     */
    public static Instrument fromString(String instrumentStr) {
        return INSTRUMENTS_BY_STRING.get(instrumentStr);
    }

    /**
     * Returns corresponding inverted Instrument for string in "CUR2/CUR1" format, e.g., string USD/EUR returns Instrument EUR/USD, but string EUR/USD returns null
     *
     * @param instrumentStr string in "CUR2/CUR1" format
     * @return corresponding Instrument or null if no Instrument was found for specified string
     */
    public static Instrument fromInvertedString(String instrumentStr) {
        for (Instrument instrument : INSTRUMENTS_BY_NAME.values()) {
            if (instrumentStr.equals(instrument.getSecondaryJFCurrency().getCurrencyCode() + getPairsSeparator() + instrument.getPrimaryJFCurrency().getCurrencyCode())) {
                return instrument;
            }
        }
        return null;
    }

    /**
     * Returns true if Instrument is inverted (such as USD/EUR or JPY/USD)
     *
     * @param instrumentStr Instrument string representation
     * @return true if inverted, false if not inverted or not Instrument
     */
    public static boolean isInverted(String instrumentStr) {
        return (fromString(instrumentStr) == null) && (fromInvertedString(instrumentStr) != null);
    }

    /**
     * Returns set of strings, which are Instruments in "CUR1/CUR2" format
     *
     * @param instruments collection of Instruments
     * @return set of strings in "CUR1/CUR2" format
     */
    public static Set<String> toStringSet(Collection<Instrument> instruments) {
        Set<String> set = new TreeSet<>();
        if (instruments != null && !instruments.isEmpty()) {
            for (Instrument instrument : instruments) {
                set.add(instrument.toString());
            }
        }
        return set;
    }

    public static Set<Instrument> fromStringSet(Set<String> instrumentsAsString){
        Set<Instrument> instruments = new TreeSet<>(COMPARATOR);
        if (instrumentsAsString != null && !instrumentsAsString.isEmpty()) {
            for (String instrumentStr : instrumentsAsString){
                try {
                    Instrument instrument = fromString(instrumentStr);
                    if (instrument == null){
                        instrument = fromInvertedString(instrumentStr);
                    }
                    if (instrument == null) {
                        instrument = valueOf(instrumentStr);
                    }
                    if (instrument != null){
                        instruments.add(instrument);
                    }
                } catch (Throwable t) {
                    // unsupported Instrument arrived
                }
            }
        }
        return instruments;
    }

    /**
     * Returns true if specified Instrument is one of the traded Instruments
     *
     * @param instrumentString Instrument to check
     * @return true if corresponding Instrument was found, false otherwise
     */
    public static boolean contains(String instrumentString) {
        boolean res = INSTRUMENTS_BY_STRING.containsKey(instrumentString);
        res = res || INSTRUMENTS_BY_NAME.containsKey(instrumentString);
        return res;
    }

    /**
     * @deprecated Use {@link Instrument#getPrimaryJFCurrency()} instead
     */
    @Deprecated
    public Currency getPrimaryCurrency() {
        return primaryCurrency.getJavaCurrency();
    }

    /**
     * @deprecated Use {@link Instrument#getSecondaryJFCurrency()} instead
     */
    @Deprecated
    public Currency getSecondaryCurrency() {
        return secondaryCurrency.getJavaCurrency();
    }

    /**
     * Returns primary currency of the Instrument
     *
     * @return primary currency of the Instrument
     */
    @Override
    public ICurrency getPrimaryJFCurrency(){
        return primaryCurrency;
    }

    /**
     * Returns secondary currency of the Instrument
     *
     * @return secondary currency of the Instrument
     */
    @Override
    public ICurrency getSecondaryJFCurrency(){
        return secondaryCurrency;
    }

    /**
     * Returns value of one pip for this currency pair
     *
     * @return pip
     */
    @Override
    public double getPipValue() {
        return pipValue;
    }

    /**
     * Returns decimal place count of one pip for the currency pair
     *
     * @return pip
     */
    @Override
    public int getPipScale() {
        return pipScale;
    }
    
    @Override
    public int getTickScale() {
        return tickScale;
    }
    
    @Override
    public Type getType() {
        return type;
    }
    
    @Override
    public IInstrumentGroup getGroup() {
        return group;
    }
    
    /**
     * Minimal trade amount in contracts.
     */
    @Override
    public double getMinTradeAmount() {
        return minTradeAmount;
    }
    
    public double getMaxTradeAmount() {
        return maxTradeAmount;
    }
    
    @Override
    public double getTradeAmountIncrement() {
        return tradeAmountIncrement;
    }
    
    @Override
    public double getAmountPerContract() {
        return amountPerContract;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public double getLeverageUse() {
        return marginUse;
    }
    
    @Override
    public String getCountry() {
        return country;
    }
    
    public IMarketInfo getMarketInfo() {
        return marketInfo;
    }

    @Override
    public boolean isExotic() {
        return exotic;
    }
    
    /**
     * Checks whether instrument is currently tradable.
     * If the instrument is not subscribed the method will always return false.
     *
     * @return true if instrument is currently tradable, false otherwise
     */
    public boolean isTradable() {
    	return tradable;
    }
    

    /**
     * Returns true if the string value represents the Instrument
     *
     * @param symbol String representation
     * @return true if the string value represents the Instrument
     */
    public boolean equals(String symbol) {
        return symbol != null && toString().equals(symbol);
    }

    private static class InstrumentComparator implements Comparator<Instrument> {

        @Override
        public int compare(Instrument o1, Instrument o2) {
            return o1 == null ? o2 == null ? 0
                                           : -1
                              : o2 == null ? 1
                                           : Instrument.compare(o1, o2);
        }
    }

    @Override
    public int compareTo(Instrument another) {
        return compare(this, another);
    }

    private static int compare(Instrument o1, Instrument o2) {
        return o1.toString().compareToIgnoreCase(o2.toString());
    }
}