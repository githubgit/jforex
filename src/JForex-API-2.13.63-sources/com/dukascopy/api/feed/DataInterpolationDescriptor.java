package com.dukascopy.api.feed;

import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.system.ITesterClient.InterpolationMethod;

/**
 * This enum specifies types of tick interpolation together with candle periods. 
 * With interpolation it is possible to speed up data loading significantly. If interpolation is used, then Bars (DataType.PRICE_RANGE_AGGREGATION and DataType.POINT_AND_FIGURE)
 * are created not from real ticks, but from interpolated ticks. This applies to historical data. Current or real time bars are still built from real ticks.  
 * 
 * @author janis.garsils
 *
 */
public enum DataInterpolationDescriptor {
	
	/**
	 * No interpolation is applied. All ticks are real ticks.
	 */
	ALL_TICKS				(null,			Period.TICK),
	/**
	 * From each Period.ONE_MIN candle is interpolated one tick according to candle's open price.
	 */
	ONE_MIN_OPEN_TICK		(InterpolationMethod.OPEN_TICK,		Period.ONE_MIN ),
	/**
	 * From each Period.ONE_MIN candle is interpolated one tick according to candle's close price.
	 */
	ONE_MIN_CLOSE_TICK		(InterpolationMethod.CLOSE_TICK,		Period.ONE_MIN ),
	/**
	 * From each Period.ONE_MIN candle is interpolated four ticks according to candle's open/high/low/close values.
	 */
	ONE_MIN_FOUR_TICKS		(InterpolationMethod.FOUR_TICKS,		Period.ONE_MIN ),
	/**
	 * From each Period.ONE_MIN candle is interpolated n ticks according to CubicSpline algorithm.
	 */
	ONE_MIN_CUBIC_SPLINE	(InterpolationMethod.CUBIC_SPLINE,	Period.ONE_MIN ),
	/**
	 * From each Period.ONE_HOUR candle is interpolated one tick according to candle's open price.
	 */
	ONE_HOUR_OPEN_TICK		(InterpolationMethod.OPEN_TICK, 		Period.ONE_HOUR ),
	/**
	 * From each Period.ONE_HOUR candle is interpolated one tick according to candle's close price. 
	 */
	ONE_HOUR_CLOSE_TICK		(InterpolationMethod.CLOSE_TICK,		Period.ONE_HOUR ),
	/**
	 * From each Period.ONE_HOUR candle is interpolated four ticks according to candle's open/high/low/close values.
	 */
	ONE_HOUR_FOUR_TICKS		(InterpolationMethod.FOUR_TICKS,		Period.ONE_HOUR ),
	/**
	 * From each Period.ONE_HOUR candle is interpolated n ticks according to CubicSpline algorithm.
	 */
	ONE_HOUR_CUBIC_SPLINE	(InterpolationMethod.CUBIC_SPLINE,	Period.ONE_HOUR ),
	/**
	 * By default ticks are interpolated according to CubicSpline algorithm.
	 */
	DEFAULT					(InterpolationMethod.CUBIC_SPLINE,	null);
	
	private DataInterpolationDescriptor(InterpolationMethod method, Period interpolateFromCandlePeriod){
		this.interpolationMethod = method;
		this.interpolateFromPeriod = interpolateFromCandlePeriod;
	}
	
	private final InterpolationMethod interpolationMethod;
	private final Period interpolateFromPeriod;
	
	
	/**
	 * Returns InterpolationMethod of this DataInterpolationDescriptor. Can return null (in case of DataInterpolationDescriptor.ALL_TICKS).
	 * @return InterpolationMethod
	 */
	public InterpolationMethod getInterpolationMethod() {
		return interpolationMethod;
	}
	
	
	/**
	 * Returns the candle period from which the ticks are interpolated. In ALL_TICKS case this method returns Period.TICK (no interpolation).
	 * PriceRange and ReversalAmount is required if one wants to get the default values. Both can be null.
	 * 
	 * @param priceRange PriceRange
	 * @param reversalAmount ReversalAmount
	 * @return period
	 */
	public Period getInterpolateFromPeriod(PriceRange priceRange, ReversalAmount reversalAmount) {
		if (interpolateFromPeriod == null){ //DEFAULT
			return getSuitableDataInterpolationDescriptor(priceRange, reversalAmount).interpolateFromPeriod;
		}
		return interpolateFromPeriod;
	}	
	
	
	/**
	 * Returns suitable DataInterpolationDescriptor according to the given PriceRange.
	 * @param priceRange PriceRange
	 * @return DataInterpolationDescriptor
	 */
	public static DataInterpolationDescriptor getSuitableDataInterpolationDescriptor(PriceRange priceRange) {
		return getSuitableDataInterpolationDescriptor(priceRange.getPipCount());
	}
	
	/**
	 * Returns suitable DataInterpolationDescriptor according to the given PriceRange and ReversalAmount.
	 * @param priceRange PriceRange
	 * @param reversalAmount ReversalAmount
	 * @return DataInterpolationDescriptor
	 */
	public static DataInterpolationDescriptor getSuitableDataInterpolationDescriptor(PriceRange priceRange, ReversalAmount reversalAmount) {
		return getSuitableDataInterpolationDescriptor( priceRange == null? 0 : priceRange.getPipCount(), reversalAmount == null ? 0 : reversalAmount.getAmount());
	}
	
	private static DataInterpolationDescriptor getSuitableDataInterpolationDescriptor(int pipsCount, int reversalAmount) {
		int count = pipsCount + reversalAmount;
		return getSuitableDataInterpolationDescriptor(count);
	}
	
	private static DataInterpolationDescriptor getSuitableDataInterpolationDescriptor(int pipsCount) {
		if (pipsCount <= 2) {
			return DataInterpolationDescriptor.ALL_TICKS;
		}
		else if (pipsCount <= 10) {
			return DataInterpolationDescriptor.ONE_MIN_CUBIC_SPLINE;
		}
		else {
			return DataInterpolationDescriptor.ONE_HOUR_CUBIC_SPLINE;
		}
	}
	
}
