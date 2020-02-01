package com.dukascopy.api.indicators;

import com.dukascopy.api.IHistory;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period;

/**
 * The class contains methods for second calculation step for indicator calculation on feed.
 *
 * @param <T> output for calculation by shift
 * @param <U> output for calculation by candle or time interval
 */
public interface IIndicatorCalculator<T, U> {

	/**
	 * The second indicator calculation step on feed for a single feed element by shift
	 * 
	 * <pre>
     * {@code
     * public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {
     *     try{
     *         double[] macd = indicators.macd(feedDescriptor, AppliedPrice.CLOSE, OfferSide.BID, 12, 26, 9).calculate(1);
     *         double sma = indicators.sma(feedDescriptor, AppliedPrice.CLOSE, OfferSide.BID, 30).calculate(1);
     *         console.getOut().format("macdShift: MACD=%.5f MACD Signal=%.5f MACD Hist=%.5f \n smaShift=%.5f\n ",
     *                 macd[0], macd[1], macd[2], sma
     *         ).println();
     *     } catch (JFException e){
     *         console.getErr().println(e);
     *     }
     * }
     * }
     * </pre>
     *
	 * @param shift
	 *            number of candles back in time staring from current bar. 0 - current bar (currently generated from ticks), 1 - previous
	 *            bar (last formed bar), 2 - current bar minus 2 bars and so on
	 * @return calculation result over a single feed element
	 * @throws JFException when parameters are not valid
	 */
	T calculate(int shift) throws JFException;

	/**
	 *The second indicator calculation step on feed for the given time interval
	 *
	 * <pre>
     * {@code
     * public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {
     *     try{
     *         long from = history.getFeedData(feedDescriptor, 2).getTime();
     *         long to = feedData.getTime();
     *         double[][] macdTimeInterval = indicators.macd(feedDescriptor, AppliedPrice.CLOSE, OfferSide.BID, 12, 26, 9).calculate(from, to);
     *         double[] smaTimeInterval = indicators.sma(feedDescriptor, AppliedPrice.CLOSE, OfferSide.BID, 30).calculate(from, to);
     *         console.getOut().format("macd: MACD=%s MACD Signal=%s MACD Hist=%s \n sma=%s\n ",
     *                 toString(macdTimeInterval[0]), toString(macdTimeInterval[1]), toString(macdTimeInterval[2]), toString(smaTimeInterval)
     *         ).println();
     *     } catch (JFException e){
     *         console.getErr().println(e);
     *     }
     * }
     * }
     * </pre>
     * 
	 * @param from
	 *            start of the time interval for which bars or ticks should be loaded. The value must be equal to the exact starting time of
	 *            the bar for the specified period. Method {@link IHistory#getBarStart(Period, long)} returns the starting time of the bar
	 *            that includes the specified time
	 * @param to
	 *            end time of the time interval for which bars or ticks should be loaded. This is the starting time of the last bar/tick
	 *            that should be loaded
	 * @return calculation result over the given time interval
	 * @throws JFException when parameters are not valid
	 */
	U calculate(long from, long to) throws JFException;

	/**
	 * The second indicator calculation step on feed for the given feed element interval
	 *
	 * <pre>
     * {@code
     * public void onFeedData(IFeedDescriptor feedDescriptor, ITimedData feedData) {
     *     try{
     *         double[][] macd = indicators.macd(feedDescriptor, AppliedPrice.CLOSE, OfferSide.BID, 12, 26, 9).calculate(2, feedData.getTime(), 0);
     *         double[] sma = indicators.sma(feedDescriptor, AppliedPrice.CLOSE, OfferSide.BID, 30).calculate(2, feedData.getTime(), 0);
     *         console.getOut().format("macd: MACD=%s MACD Signal=%s MACD Hist=%s \n sma=%s\n ",
     *                 toString(macd[0]), toString(macd[1]), toString(macd[2]), toString(sma)
     *         ).println();
     *     } catch (JFException e){
     *         console.getErr().println(e);
     *     }
     * }
     * }
     * </pre>
     * 
	 * @param candlesBefore
	 *            how much candles to load before and including the candle with time specified in the <code>time</code> parameter
	 * @param time
	 *            time of the last candle in the period specified with the <code>numberOfCandlesBefore</code> parameter or/and time of the
	 *            candle prior to the first candle in the period specified with the <code>numberOfCandlesAfter</code> parameter
	 * @param candlesAfter
	 *            how much candles to load after (and not including) the candle with time specified in the <code>time</code> parameter
	 * @return calculation result over the given interval of feed elements
	 * @throws JFException when parameters are not valid
	 */
	U calculate(int candlesBefore, long time, int candlesAfter) throws JFException;

}
