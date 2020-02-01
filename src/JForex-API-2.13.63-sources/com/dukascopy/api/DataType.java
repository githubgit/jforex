/**
 * The file DataType.java was created on Mar 19, 2010 at 11:06:44 AM
 * by @author Marks Vilkelis
 */
package com.dukascopy.api;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * The enumeration describes several data types used in JForex  
 * 
 * @author Mark Vilkel
 */
public enum DataType {
	/**
	 * Just ticks
	 */
	TICKS(DataPresentationType.AREA, DataPresentationType.LINE, DataPresentationType.BAR, DataPresentationType.TABLE),
	
	/**
	 * Bar which consists of exact number of ticks 
	 */
	TICK_BAR(DataPresentationType.BAR, DataPresentationType.CANDLE, DataPresentationType.TABLE),
	
	/**
	 * Candles for periods 1 sec, 10 sec, 1 min, etc
	 */
	TIME_PERIOD_AGGREGATION(DataPresentationType.AREA, DataPresentationType.LINE, DataPresentationType.BAR, DataPresentationType.CANDLE, DataPresentationType.TABLE),
	
	/**
	 * Range Bars for 2 pips, 3 pips, 4 pips, etc
	 */
	PRICE_RANGE_AGGREGATION(DataPresentationType.RANGE_BAR, DataPresentationType.CANDLE, DataPresentationType.TABLE),
	
	/**
	 * Point and figure charts are composed of a number of columns
	 * that either consist of a series of stacked Xs or Os.
	 */
	POINT_AND_FIGURE(DataPresentationType.BOX, DataPresentationType.BAR, DataPresentationType.TABLE),

	/**
	 * A Renko bars (bricks) are constructed by placing a brick
	 * in the next column once the price surpasses the top or
	 * bottom of the previous brick by a predefined amount
	 */
	RENKO(DataPresentationType.BRICK, DataPresentationType.WICK, DataPresentationType.OPPOSITE_WICK, DataPresentationType.TABLE),
	
	/**
	 * Line Break lines (bars)
	 */
	LINE_BREAK(DataPresentationType.RANGE_BAR, DataPresentationType.WICK, DataPresentationType.TABLE),
	
	/**
	 * Kagi lines (bars)
	 */
	KAGI(DataPresentationType.LINE, DataPresentationType.TABLE);
	
	private Set<DataPresentationType> presentationTypes;
	
	private DataType(DataPresentationType... presentationTypes) {
	    this.presentationTypes = EnumSet.noneOf(DataPresentationType.class);
        Collections.addAll(this.presentationTypes, presentationTypes);
	}

	public Set<DataPresentationType> getSupportedPresentationTypes() {
	    return EnumSet.copyOf(presentationTypes);
	}
	
	public boolean isPresentationTypeSupported(DataPresentationType presentationType) {
	    return presentationTypes.contains(presentationType);
	}

	public enum DataPresentationType {
	    /** Supported by following DataTypes: Ticks, TimePeriodAggregation */
        AREA,
	    /** Supported by following DataTypes: Ticks, TimePeriodAggregation */
	    LINE,
	    /** Supported by following DataTypes: Ticks, TickBar, TimePeriodAggregation, PointAndFigure */
	    BAR,
	    /** Supported by following DataTypes: PointAndFigure */
	    BOX,
	    /** Supported by following DataTypes: PriceRangeAggregation */
	    RANGE_BAR,
	    /** Supported by following DataTypes: TickBar, TimePeriodAggregation, PriceRangeAggregation */
	    CANDLE,
	    /** Supported by following DataTypes: Renko */
	    BRICK,
	    /** Supported by following DataTypes: Renko, Line Break */
	    WICK,
	    /** Supported by following DataTypes: Renko */
	    OPPOSITE_WICK,
	    /** Supported by following DataTypes: Ticks, TickBar, TimePeriodAggregation, PriceRangeAggregation, PointAndFigure, Renko, Kagi, Line Break */
	    TABLE;
	}
}
