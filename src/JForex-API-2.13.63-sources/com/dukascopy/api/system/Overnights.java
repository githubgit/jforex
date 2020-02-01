/*
 * Copyright 1998-2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import com.dukascopy.api.Instrument;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Allows to set fixed overnight values for every Instrument for historical testing.
 * Default values of this class differs from values used in real (DEMO/LIVE environments) orders processing.
 */
public class Overnights implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Map<Instrument, Double> longValues = new HashMap<>();
    public Map<Instrument, Double> shortValues = new HashMap<>();
    
    
    /**
     * @deprecated use {@link #Overnights()}
     * Creates Overnights object with default values (prices taken at 26.04.2010)
     *
     * @param institutional if true, then institutional prices will be filled in as defaults
     */
    public Overnights(boolean institutional) {
    	this();
    }

    /**
     * Creates Overnights object with default values (prices taken at 22.08.2018)
     */
    public Overnights() {
    	
    	longValues.put(Instrument.AUDCAD, -0.02d);
    	shortValues.put(Instrument.AUDCAD, -0.17d);

    	longValues.put(Instrument.AUDCHF, 0.44d);
    	shortValues.put(Instrument.AUDCHF, -0.58d);
    	
    	longValues.put(Instrument.AUDJPY, 0.35d);
    	shortValues.put(Instrument.AUDJPY, -0.49d);
    	
    	longValues.put(Instrument.AUDNZD, -0.27d);
    	shortValues.put(Instrument.AUDNZD, 0.06d);
    	
    	longValues.put(Instrument.AUDSGD, -0.06d);
    	shortValues.put(Instrument.AUDSGD, -0.22d);
    	
    	longValues.put(Instrument.AUDUSD, -0.11d);
    	shortValues.put(Instrument.AUDUSD, 0.04d);
    	
    	longValues.put(Instrument.CADCHF, 0.40d);
    	shortValues.put(Instrument.CADCHF, -0.55d);
    	
    	longValues.put(Instrument.CADHKD, -0.17d);
    	shortValues.put(Instrument.CADHKD, -1.49d);
    	
    	longValues.put(Instrument.CADJPY, 0.30d);
    	shortValues.put(Instrument.CADJPY, -0.44d);
    	
    	longValues.put(Instrument.CHFJPY, -0.30d);
    	shortValues.put(Instrument.CHFJPY, 0.12d);
    	
    	longValues.put(Instrument.CHFSGD, -1.05d);
    	shortValues.put(Instrument.CHFSGD, 0.67d);
    	
    	longValues.put(Instrument.EURAUD, -1.03d);
    	shortValues.put(Instrument.EURAUD, 0.78d);
    	
    	longValues.put(Instrument.EURCAD, -0.87d);
    	shortValues.put(Instrument.EURCAD, 0.64d);
    	
    	longValues.put(Instrument.EURCHF, 0.05d);
    	shortValues.put(Instrument.EURCHF, -0.23d);
    	
    	longValues.put(Instrument.EURCZK, -1.56d);
    	shortValues.put(Instrument.EURCZK, -0.14d);

    	longValues.put(Instrument.EURDKK, -0.92d);
    	shortValues.put(Instrument.EURDKK, -1.76d);
    	
    	longValues.put(Instrument.EURGBP, -0.34d);
    	shortValues.put(Instrument.EURGBP, 0.21d);
    	
    	longValues.put(Instrument.EURHKD, -4.64d);
    	shortValues.put(Instrument.EURHKD, 2.46d);

    	longValues.put(Instrument.EURHUF, -0.80d);
    	shortValues.put(Instrument.EURHUF, -0.37d);

    	longValues.put(Instrument.EURJPY, -0.16d);
    	shortValues.put(Instrument.EURJPY, -0.00d);

    	longValues.put(Instrument.EURNOK, -4.00d);
    	shortValues.put(Instrument.EURNOK, 1.67d);

    	longValues.put(Instrument.EURNZD, -1.39d);
    	shortValues.put(Instrument.EURNZD, 1.12d);
    	
    	longValues.put(Instrument.EURPLN, -2.51d);
    	shortValues.put(Instrument.EURPLN, 1.48d);
    	
    	longValues.put(Instrument.EURRUB, -145.99d);
    	shortValues.put(Instrument.EURRUB, 127.31d);

    	longValues.put(Instrument.EURSEK, -0.66d);
    	shortValues.put(Instrument.EURSEK, -1.86d);
    	
    	longValues.put(Instrument.EURSGD, -0.97d);
    	shortValues.put(Instrument.EURSGD, 0.59d);
    	
    	longValues.put(Instrument.EURTRY, -37.68d);
    	shortValues.put(Instrument.EURTRY, 33.05d);
    	
    	longValues.put(Instrument.EURUSD, -0.83d);
    	shortValues.put(Instrument.EURUSD, 0.76d);
    	
    	longValues.put(Instrument.GBPAUD, -0.63d);
    	shortValues.put(Instrument.GBPAUD, 0.32d);
    	
    	longValues.put(Instrument.GBPCAD, -0.48d);
    	shortValues.put(Instrument.GBPCAD, 0.18d);
    	
    	longValues.put(Instrument.GBPCHF, 0.43d);
    	shortValues.put(Instrument.GBPCHF, -0.66d);
    	
    	longValues.put(Instrument.GBPJPY, 0.24d);
    	shortValues.put(Instrument.GBPJPY, -0.46d);
    	
    	longValues.put(Instrument.GBPNZD, -0.99d);
    	shortValues.put(Instrument.GBPNZD, 0.64d);
    	
    	longValues.put(Instrument.GBPUSD, -0.54d);
    	shortValues.put(Instrument.GBPUSD, 0.43d);
    	
    	longValues.put(Instrument.HKDJPY, 2.85d);
    	shortValues.put(Instrument.HKDJPY, -6.39d);
    	
    	longValues.put(Instrument.NZDCAD, 0.12d);
    	shortValues.put(Instrument.NZDCAD, -0.28d);
    	
    	longValues.put(Instrument.NZDCHF, 0.50d);
    	shortValues.put(Instrument.NZDCHF, -0.63d);

    	longValues.put(Instrument.NZDJPY, 0.43d);
    	shortValues.put(Instrument.NZDJPY, -0.55d);

    	longValues.put(Instrument.NZDUSD, -0.00d);
    	shortValues.put(Instrument.NZDUSD, -0.06d);
    	
    	longValues.put(Instrument.SGDJPY, 0.25d);
    	shortValues.put(Instrument.SGDJPY, -0.45d);
    	
    	longValues.put(Instrument.TRYJPY, 0.85d);
    	shortValues.put(Instrument.TRYJPY, -0.97d);
    	
    	longValues.put(Instrument.USDCAD, 0.18d);
    	shortValues.put(Instrument.USDCAD, -0.30d);
    	
    	longValues.put(Instrument.USDCHF, 0.75d);
    	shortValues.put(Instrument.USDCHF, -0.85d);
    	
    	longValues.put(Instrument.USDCNH, -0.43d);
    	shortValues.put(Instrument.USDCNH, -2.03d);
    	
    	longValues.put(Instrument.USDCZK, 0.24d);
    	shortValues.put(Instrument.USDCZK, -1.58d);

    	longValues.put(Instrument.USDDKK, 3.81d);
    	shortValues.put(Instrument.USDDKK, -5.74d);

    	longValues.put(Instrument.USDHKD, 1.59d);
    	shortValues.put(Instrument.USDHKD, -3.01d);

    	longValues.put(Instrument.USDHUF, 1.31d);
    	shortValues.put(Instrument.USDHUF, -2.15d);

    	longValues.put(Instrument.USDILS, 1.33d);
    	shortValues.put(Instrument.USDILS, -2.43d);

    	longValues.put(Instrument.USDJPY, 0.65d);
    	shortValues.put(Instrument.USDJPY, -0.73d);
    	
    	longValues.put(Instrument.USDMXN, -34.51d);
    	shortValues.put(Instrument.USDMXN, 23.13d);

    	longValues.put(Instrument.USDNOK, 2.55d);
    	shortValues.put(Instrument.USDNOK, -4.06d);

    	longValues.put(Instrument.USDPLN, 0.48d);
    	shortValues.put(Instrument.USDPLN, -1.14d);

    	longValues.put(Instrument.USDRON, -1.63d);
    	shortValues.put(Instrument.USDRON, 0.18d);

    	longValues.put(Instrument.USDRUB, -78.09d);
    	shortValues.put(Instrument.USDRUB, 65.98d);
    	
    	longValues.put(Instrument.USDSEK, 5.92d);
    	shortValues.put(Instrument.USDSEK, -7.56d);

    	longValues.put(Instrument.USDSGD, 0.14d);
    	shortValues.put(Instrument.USDSGD, -0.38d);
    	
    	longValues.put(Instrument.USDTHB, -0.49d);
    	shortValues.put(Instrument.USDTHB, -0.49d);

    	longValues.put(Instrument.USDTRY, -28.27d);
    	shortValues.put(Instrument.USDTRY, 24.63d);
    	
    	longValues.put(Instrument.USDZAR, -19.90d);
    	shortValues.put(Instrument.USDZAR, 15.59d);

    	longValues.put(Instrument.ZARJPY, 0.13d);
    	shortValues.put(Instrument.ZARJPY, -0.16d);
    }

    /**
     * Sets overnights for instrument
     *
     * @param instrument instrument
     * @param longValue long value
     * @param shortValue short value
     */
    public void setOvernights(Instrument instrument, double longValue, double shortValue) {
        longValues.put(instrument, longValue);
        shortValues.put(instrument, shortValue);
    }

    /**
     * Returns Map with overnight long values for all instruments. Changing values in array will change values in Overnights class
     *
     * @return overnight values
     */
    public Map<Instrument, Double> getLongOvernights() {
        return longValues;
    }

    /**
     * Returns Map with overnight short values for all instruments. Changing values in array will change values in Overnights class
     *
     * @return overnight values
     */
    public Map<Instrument, Double> getShortOvernights() {
        return shortValues;
    }

	public void save(Preferences node) throws IOException {
    	Preferences longValuesNode = node.node("longValues");
		Preferences shortValuesNode = node.node("shortValues");
    	for(Instrument instrument : longValues.keySet()) {
			longValuesNode.putDouble(instrument.toString(), longValues.get(instrument));
		}

		for(Instrument instrument : shortValues.keySet()) {
			shortValuesNode.putDouble(instrument.toString(), shortValues.get(instrument));
		}
	}

	public static Overnights read(Preferences node) throws BackingStoreException {
		Preferences longValuesNode = node.node("longValues");
		Overnights overnights = new Overnights();

		for(String instrument : longValuesNode.keys()) {
			overnights.longValues.put(Instrument.fromString(instrument), longValuesNode.getDouble(instrument, 0));
		}

		Preferences shortValuesNode = node.node("shortValues");
		for(String instrument : shortValuesNode.keys()) {
			overnights.shortValues.put(Instrument.fromString(instrument), shortValuesNode.getDouble(instrument, 0));
		}

		return overnights;
	}
}
