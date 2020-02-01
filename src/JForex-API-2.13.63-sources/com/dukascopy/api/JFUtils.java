package com.dukascopy.api;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.concurrent.Future;

import com.dukascopy.api.instrument.IFinancialInstrument;
import com.dukascopy.api.util.IEmailResponse;

public interface JFUtils {

	/**
	 * Converts the amount from one instrument to another with precision of 0.1 instrumentTo pips. 
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 * 
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom
     * @return converted amount
	 * 
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount) throws JFException;

	/**
	 * Converts the amount from one instrument to another.
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 *
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom
	 * @param decimalPlaces decimal places of the returned result
     * @return converted amount
	 *
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount, int decimalPlaces) throws JFException;

	/**
	 * Converts the amount from one instrument to another.
	 * 
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom 
	 * @param decimalPlaces decimal places of the returned result
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
     * @return converted amount
	 * 
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount, int decimalPlaces, OfferSide offerSide) throws JFException;

	/**
	 * Gets exchange rate between two currencies.
	 * For instance, the following code returns rate for EURUSD
	 * utils.getRate(JFCurrency.getInstance("EUR"), JFCurrency.getInstance("USD"));
	 * Rate is calculated based on medium price between ASK and BID prices
	 *
	 * @param from source the target currency of conversion
	 * @param to the target currency of conversion
     * @return exchange rate
	 * @throws JFException if an error occurred
	 */
	double getRate(ICurrency from, ICurrency to) throws JFException;

	/**
	 * Gets exchange rate between two currencies.
	 * For instance, the following code returns rate for EURUSD
	 * utils.getRate(JFCurrency.getInstance("EUR"), JFCurrency.getInstance("USD"), OfferSide.ASK);
	 *
	 * @param from source the target currency of conversion
	 * @param to the target currency of conversion
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
     * @return exchange rate
	 * @throws JFException if an error occurred
	 */
	double getRate(ICurrency from, ICurrency to, OfferSide offerSide) throws JFException;

	/**
	 * Converts the cost of one pip for particular instrument to specified currency.
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 *  
	 * @param instrument {@link Instrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException if an error occurred
	 */
	double convertPipToCurrency(Instrument instrument, ICurrency currency) throws JFException;

	/**
	 * Converts the cost of one pip for particular instrument to specified currency 
	 * @param instrument {@link Instrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException if an error occurred
	 */
	double convertPipToCurrency(Instrument instrument, ICurrency currency, OfferSide offerSide) throws JFException;

    /**
     * Converts profit/loss value from one currency to another.
     *
     * @param profitLoss profit/loss value
     * @param sourceCurrency source currency
     * @param targetCurrency target currency
     * @param scale calculation precision
     * @return converted profit/loss value
     */
    double convertProfitLoss(double profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale);

    /**
     * Converts profit/loss value from one currency to another.
     *
     * @param profitLoss profit/loss value
     * @param sourceCurrency source currency
     * @param targetCurrency target currency
     * @param scale calculation precision
     * @return converted profit/loss value
     */
    BigDecimal convertProfitLoss(BigDecimal profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale);

    /**
     * Returns localized number format corresponding to system settings.
     *
     * @return number format
     */
    DecimalFormat getLocalizedDecimalFormat();

    /**
     * Returns formatted amount value with units corresponding to instrument type.
     *
     * @param amount amount value
     * @param instrument instrument
     * @return formatted amount value
     */
	String getFormattedAmount(BigDecimal amount, Instrument instrument);

	/**
	 * Returns starting time point of the time period that is <code><b>(numberOfPeriods - 1)</b></code> back in time to the time period that
	 * includes time specified in <code><b>to</b></code> parameter.<br> 
     * 
     * @param period {@link Period} time period (Tick period is not supported).
     * @param to time included to the last period unit
     * @param numberOfPeriods number of time periods back
     * @return starting time of the first period unit  (<code><b>(numberOfPeriods - 1)</b></code> backward from the <code><b>to</b></code> time point)
     * @throws JFException when period is not supported
     * @see IDataService#getFXSentimentIndex(ICurrency, long)
     * @see IDataService#getFXSentimentIndex(Instrument, long)
     * @see IHistory#getTimeForNBarsBack(Period, long, int)
     * @see IHistory#getTimeForNBarsForward(Period, long, int)
	 */
	long getTimeForNPeriodsBack(final Period period, final long to, final int numberOfPeriods) throws JFException;
	
	/**
     * Returns starting time point of the time period that is + <code><b>(numberOfPeriods - 1)</b></code> in the future to the time period that
     * includes time specified in <code><b>from</b></code> parameter.<br>
     * 
     * @param period {@link Period} time period (Tick period is not supported).
     * @param from  time included to the first period unit
     * @param numberOfPeriods number of time periods forward
     * @return starting time of the last period unit (<code><b>(numberOfPeriods - 1)</b></code> forward from the <code><b>from</b></code> time point)
     * @throws JFException when period is not supported
     * @see IDataService#getFXSentimentIndex(ICurrency, long)
     * @see IDataService#getFXSentimentIndex(Instrument, long)
     * @see IHistory#getTimeForNBarsBack(Period, long, int)
     * @see IHistory#getTimeForNBarsForward(Period, long, int)
     */
	long getTimeForNPeriodsForward(final Period period, final long from, final int numberOfPeriods) throws JFException;

	/**
	 * Sends an e-mail to the given addressee.
	 * <p> The following restrictions apply:
     * <ul>
	 * <li>Maximum 1 e-mail per minute,
	 * <li>Maximum contents length 500,
	 * <li>Maximum subject length 200.
	 * </ul>
	 * If a condition breach is detected on client side, a {@link JFException} gets thrown, 
	 * if on server side - find the details in the returned {@link IEmailResponse}.
	 * 
	 * <pre>{@code
	 * //sending an e-mail without processing the server response
	 * context.getUtils().sendMail("to@host.com", "Test mail subject", "Test <b>mail</b> html contents \n Bye.")
	 * 
	 * //sending an e-mail and processing the server response
	 * try {
	 *     // Note: not to hold up the strategy execution, consider calling the sending logic from another thread
	 *     Future<IEmailResponse> future = utils.sendMail("to@host.com", "Test mail subject", "Test <b>mail</b> \n Bye.");
	 *     console.getOut().println("Waiting on response");
	 *     IEmailResponse response = future.get(30, TimeUnit.SECONDS);
	 *     if (response.isError()) {
	 *         console.getErr().println(response);
	 *     } else {
	 *         console.getOut().println("e-mail successfully sent!");
	 *     }
	 * } catch (Exception e) {
	 *     e.printStackTrace(console.getErr());
	 * }
	 * }</pre>
	 * 	 
	 * @param to receiver's address
	 * @param subject e-mail subject
	 * @param content e-mail content as html text
	 * @return {@link Future} of the e-mail response {@link IEmailResponse}
	 * @throws JFException when:
     * <ul>
	 * <li>Some error has occurred while sending the e-mail,
	 * <li>text size exceeds 500 characters,
	 * <li>it is attempted to send the e-mail sooner than 1 second after the last send attempt.
	 * </ul>
	 */
	Future<IEmailResponse> sendMail(String to, String subject, String content) throws JFException;
	
	
	/**
	 * Obtains the "amount per contract" information for specified {@link Instrument}
	 * @param instrument {@link Instrument} which amount per contract information should be obtained
	 * @return amount per contract value
	 * @deprecated use {@link Instrument#getAmountPerContract()}
	 */
	@Deprecated
	BigDecimal getAmountPerContract(final Instrument instrument);
	
	/**
	 * Obtains the "minimum trade amount" information for specified {@link Instrument}
	 * @param instrument {@link Instrument} which minimum trade amount information should be obtained
	 * @return minimum trade amount value
	 * @deprecated use {@link Instrument#getMinTradeAmount()} which returns amount in units.
	 */
	@Deprecated
	BigDecimal getMinTradeAmount(final Instrument instrument);

	/**
	 * Deprecated methods
	 */

	/**
	 * Converts the amount from one instrument to another with precision of 0.1 instrumentTo pips.
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 *
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom
     * @return converted amount
	 *
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	@Deprecated
	double convert(IFinancialInstrument instrumentFrom, IFinancialInstrument instrumentTo, double amount) throws JFException;

	/**
	 * Converts the amount from one instrument to another.
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 *
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom
	 * @param decimalPlaces decimal places of the returned result
     * @return converted amount
	 *
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	@Deprecated
	double convert(IFinancialInstrument instrumentFrom, IFinancialInstrument instrumentTo, double amount, int decimalPlaces) throws JFException;

	/**
	 * Converts the amount from one instrument to another.
	 *
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom
	 * @param decimalPlaces decimal places of the returned result
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
     * @return converted amount
	 *
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	@Deprecated
	double convert(IFinancialInstrument instrumentFrom, IFinancialInstrument instrumentTo, double amount, int decimalPlaces, OfferSide offerSide) throws JFException;

	/**
	 * Converts the cost of one pip for particular instrument to specified currency.
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 *
	 * @param instrument {@link Instrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException if an error occurred
	 * @deprecated use {@link #convertPipToCurrency(Instrument, ICurrency)} instead
	 */
	@Deprecated
	double convertPipToCurrency(Instrument instrument, Currency currency) throws JFException;

	/**
	 * Converts the cost of one pip for particular instrument to specified currency.
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 *
	 * @param financialInstrument {@link IFinancialInstrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException if an error occurred
	 */
	@Deprecated
	double convertPipToCurrency(IFinancialInstrument financialInstrument, ICurrency currency) throws JFException;

	/**
	 * Converts the cost of one pip for particular instrument to specified currency
	 * @param instrument {@link Instrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException if an error occurred
	 * @deprecated use {@link #convertPipToCurrency(Instrument, ICurrency, OfferSide)} instead
	 */
	@Deprecated
	double convertPipToCurrency(Instrument instrument, Currency currency, OfferSide offerSide) throws JFException;

	/**
	 * Converts the cost of one pip for particular instrument to specified currency
	 * @param financialInstrument {@link IFinancialInstrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException if an error occurred
	 */
	@Deprecated
	double convertPipToCurrency(IFinancialInstrument financialInstrument, ICurrency currency, OfferSide offerSide) throws JFException;

	/**
	 * Sends an e-mail to the given addressee.
	 *
	 * @param smtpServer SMTP server
	 * @param from sender address
	 * @param to receiver's address
	 * @param subject e-mail subject
	 * @param content e-mail content as html text
	 * @throws JFException when:
     * <ul>
	 * <li>Some error has occurred while sending the e-mail,
	 * <li>text size exceeds 500 characters,
	 * <li>it is attempted to send the e-mail sooner than 1 second after the last send attempt.
	 * </ul>
	 * @deprecated please use {@link #sendMail(String, String, String)}
	 */
	@Deprecated
	void sendMail(String smtpServer, String from, String to, String subject, String content) throws JFException;


}
