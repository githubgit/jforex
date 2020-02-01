/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.dukascopy.api.DataType;

public interface IOhlcChartObject extends IWidgetChartObject {
	
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_ALIGNMENT = "ohlc.alignment";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_PARAM_VISIBILITY = "ohlc.param.visibility";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_POSX = "ohlc.posx";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_POSY = "ohlc.posy";
	/**
	 * String constant to be used in PropertyChangeListener
	 */
	public static final String PROPERTY_OHLC_SHOW_INDICATOR_INFO = "ohlc.show.indicator.info";
	
	
	public enum OhlcAlignment {
		HORIZONTAL,
		VERTICAL,
		AUTO
	}
	
	public enum CandleInfoParams {
	    DATE,
	    TIME,
	    @Deprecated
	    PLATFORM_TIME,
		OPEN,
		HIGH,
		LOW,
		CLOSE,
		VOL,
		INDEX,
		TTL
	}
	
	public enum TickInfoParams {
	    DATE,
	    TIME,
		ASK,
		BID,
		ASK_VOL,
		BID_VOL
	}
	
	public enum PriceAgregatedInfoParams {
	    DATE,
	    START_TIME,
		END_TIME,
		OPEN,
		HIGH,
		LOW,
		CLOSE,
		VOL,
        WICK(DataType.LINE_BREAK, DataType.RENKO),
        OPPOSITE_WICK(DataType.RENKO),
        YIN_SPAN(DataType.KAGI),
        YANG_SPAN(DataType.KAGI),
        TURNAROUND_PRICE(DataType.LINE_BREAK, DataType.KAGI),
        FEC,
		INDEX;
        
        private final DataType[] dataTypes;
        
        private PriceAgregatedInfoParams() {
            this((DataType[]) null);
        }
        
        private PriceAgregatedInfoParams(DataType... dataTypes) {
            this.dataTypes = dataTypes;
        }
        
        public static PriceAgregatedInfoParams[] values(DataType dataType) {
            List<PriceAgregatedInfoParams> res = new ArrayList<>();
            for (PriceAgregatedInfoParams val : values()) {
                if (val.dataTypes == null) {
                    res.add(val);
                } else {
                    for (DataType type : val.dataTypes) {
                        if (type == dataType) {
                            res.add(val);
                            break;
                        }
                    }
                }
            }
            return res.toArray(new PriceAgregatedInfoParams[res.size()]);
        }
	}
	
	
	/**
     * Returns all InfoParams values for specified DataType.
     * @param dataType one of <code>DataType</code> values
     * @return {@code Enum<?>[]}
     */
    Enum<?>[] getAllInfoParamsByDataType(DataType dataType);
	
	/**
	 * Returns current alignment mode.
	 * @return <code>OhlcAlignment</code> value.
	 */
	OhlcAlignment getAlignment();
	
	/**
	 * Sets <code>OhlcAlignment</code> mode for this OHLC Informer.
	 * Use <code>OhlcAlignment.AUTO</code> to determine alignment automatically. 
	 * @param alignment <code>OhlcAlignment</code> value.
	 */
	void setAlignment(OhlcAlignment alignment);
	
	/**
	 * Returns whether parameter is displaying in current OHLC Informer or not.
     * @param <E> type of parameter
	 * @param param  one of <code>IOhlcChartObject.CandleInfoParams</code>, <code>IOhlcChartObject.TickInfoParams</code> or <code>IOhlcChartObject.PriceAgregatedInfoParams</code> 
	 * @return {@code true} if parameter is visible, {@code false} otherwise
	 * @throws NullPointerException  in case of unsupported parameter type
	 */
	<E extends Enum<E>> boolean getParamVisibility(Enum<E> param);
	
	/**
	 * Setup visibility property for specified parameter.
     * @param <E> type of parameter
	 * @param param  one of <code>IOhlcChartObject.CandleInfoParams</code>, <code>IOhlcChartObject.TickInfoParams</code> or <code>IOhlcChartObject.PriceAgregatedInfoParams</code>
     * @param visible {@code true} if parameter should be visible, {@code false} otherwise
	 * @throws NullPointerException  in case of unsupported parameter type
	 */
	<E extends Enum<E>> void setParamVisibility(Enum<E> param, boolean visible);
	
	/**
	 * Returns whether indicator values are displaying or not.
	 * @return {@code true} if indicator values are visible, {@code false} otherwise
	 */
	boolean getShowIndicatorInfo();
	
	/**
	 * Sets property allowing to show indicator values.
	 * Default value: <b>true</b>
	 * @param showIndicatorInfo {@code true} if indicator values should be visible, {@code false} otherwise
	 */
	void setShowIndicatorInfo(boolean showIndicatorInfo);

	/**
	 * Returns whether indicator names are displaying or not.
	 * @return {@code true} if indicator names are visible, {@code false} otherwise
	 */
	boolean getShowIndicatorNames();

	/**
	 * Sets property allowing to show indicator names.
	 * Default value: <b>true</b>
	 * @param showIndicatorNames {@code true} if indicator names should be visible, {@code false} otherwise
	 */
	void setShowIndicatorNames(boolean showIndicatorNames);
	
	
	/**
	 * Clears all user's custom messages.
	 */
	void clearUserMessages();
	
	/**
	 * Adds user message.
	 * @param label  displaying at the left
	 * @param value  displaying at the right
	 * @param color  text color
	 */
	void addUserMessage(String label, String value, Color color);
	
	/**
	 * Adds user message.
	 * @param message message text
	 * @param color  text color
	 * @param textAlignment One of <code>SwingConstants.CENTER, SwingConstants.LEFT or SwingConstants.RIGHT</code>. Any other value will be replaced with SwingConstants.LEFT by default. 
	 * @param bold {@code true} if text should be displayed in bold, {@code false} otherwise
	 */
	void addUserMessage(String message, Color color, int textAlignment, boolean bold);
	
}
