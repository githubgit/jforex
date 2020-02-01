package com.dukascopy.api.instrument;

import java.util.Set;

import com.dukascopy.api.instrument.IFinancialInstrument.Type;

/**
 * The IInstrumentGroup interface  holds instrument group information 
 * and the instruments which are within the group.
 */
public interface IInstrumentGroup {
	
	public final static String FX_MAJOR 	= "FX_MAJOR";
	public final static String FX_CROSS 	= "FX_CROSS";
	public final static String FX_RSRV 		= "FX_RSRV";
	public final static String FX_METAL 	= "FX_METAL";
	public final static String STK_CASH 	= "STK_CASH";
	public final static String COM_SPOT 	= "COM_SPOT";
	public final static String IDX_CASH 	= "IDX_CASH";
	public final static String BONDS        = "BONDS";
	public final static String CRYPTO_CURR 	= "CRYPTO_CURR";
	public final static String ETF          = "ETF";
	public final static String I18KEY_PREFIX = "instrument.group.type.";

	
	/**
	 * Returns instruments of the group
	 * 
	 * @return instruments of the group
	 */
	Set<IFinancialInstrument> getInstruments();
	
	/**
	 * Returns the name of the instrument group
	 * 
	 * @return the name of the instrument group
	 */
	String getName();
	
	
	/**
	 * Returns localized description of the instrument group
	 * 
	 * @return the description of the instrument group
	 */
	String getDescription();
	
	
	Type getType();
	boolean isForex();
	boolean isMetal();
	boolean isCfd();
	boolean isCrypto();

	boolean isCfdCommodities();
	boolean isCfdIndices();
	boolean isCfdStocks();
	boolean isCfdETF();
	boolean isCfdBonds();
}
