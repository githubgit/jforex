package com.dukascopy.api;

import java.math.BigDecimal;

/**
 * Provides methods for amount conversion from one currency to another.
 */
public interface ICurrencyConverter {

    /**
     * Converts given amount from one currency to another.
     * This method doesn't block - {@code Double.NaN} is returned when price for conversion is unavailable.
     *
     * @param amount amount which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param side offer side used to determine price for conversion
     * @param scale result value scale
     * @return converted amount in target currency with specified scale
     */
    double convert(double amount, ICurrency sourceCurrency, ICurrency targetCurrency, OfferSide side, int scale);

    /**
     * Converts given amount from one currency to another.
     *
     * @param amount amount which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param side offer side used to determine price for conversion
     * @param scale result value scale
     * @param blocking pass {@code true} if calling thread is allowed to block when price for conversion need to be loaded, {@code false} otherwise
     * @return converted amount in target currency with specified scale
     */
    double convert(double amount, ICurrency sourceCurrency, ICurrency targetCurrency, OfferSide side, int scale, boolean blocking);

    /**
     * Converts given amount from one currency to another using price at specified time.
     * This method block until price for conversion is loaded.
     *
     * @param amount amount which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param side offer side used to determine price for conversion
     * @param scale result value scale
     * @param time time at which price for conversion is determined
     * @return converted amount in target currency with specified scale
     */
    double convert(double amount, ICurrency sourceCurrency, ICurrency targetCurrency, OfferSide side, int scale, long time);

    /**
     * Converts given amount from one currency to another.
     * This method doesn't block - {@code null} is returned when price for conversion is unavailable.
     *
     * @param amount amount which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param side offer side used to determine price for conversion
     * @param scale result value scale
     * @return converted amount in target currency with specified scale
     */
    BigDecimal convert(BigDecimal amount, ICurrency sourceCurrency, ICurrency targetCurrency, OfferSide side, int scale);

    /**
     * Converts given amount from one currency to another.
     *
     * @param amount amount which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param side offer side used to determine price for conversion
     * @param scale result value scale
     * @param blocking pass {@code true} if calling thread is allowed to block when price for conversion need to be loaded, {@code false} otherwise
     * @return converted amount in target currency with specified scale
     */
    BigDecimal convert(BigDecimal amount, ICurrency sourceCurrency, ICurrency targetCurrency, OfferSide side, int scale, boolean blocking);

    /**
     * Converts given amount from one currency to another using price at specified time.
     * This method block until price for conversion is loaded.
     *
     * @param amount amount which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param side offer side used to determine price for conversion
     * @param scale result value scale
     * @param time time at which price for conversion is determined
     * @return converted amount in target currency with specified scale
     */
    BigDecimal convert(BigDecimal amount, ICurrency sourceCurrency, ICurrency targetCurrency, OfferSide side, int scale, long time);

    /**
     * Converts commission from one currency to another.
     * This method doesn't block - {@code Double.NaN} is returned when price for conversion is unavailable.
     *
     * @param commission commission which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @return converted commission in target currency with specified scale
     */
    double convertCommission(double commission, ICurrency sourceCurrency, ICurrency targetCurrency, int scale);

    /**
     * Converts commission from one currency to another.
     *
     * @param commission commission which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param blocking pass {@code true} if calling thread is allowed to block when price for conversion need to be loaded, {@code false} otherwise
     * @return converted commission in target currency with specified scale
     */
    double convertCommission(double commission, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, boolean blocking);

    /**
     * Converts commission from one currency to another using price at specified time.
     * This method block until price for conversion is loaded.
     *
     * @param commission commission which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param time time at which price for conversion is determined
     * @return converted commission in target currency with specified scale
     */
    double convertCommission(double commission, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, long time);

    /**
     * Converts commission from one currency to another.
     * This method doesn't block - {@code null} is returned when price for conversion is unavailable.
     *
     * @param commission commission which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @return converted commission in target currency with specified scale
     */
    BigDecimal convertCommission(BigDecimal commission, ICurrency sourceCurrency, ICurrency targetCurrency, int scale);

    /**
     * Converts commission from one currency to another.
     *
     * @param commission commission which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param blocking pass {@code true} if calling thread is allowed to block when price for conversion need to be loaded, {@code false} otherwise
     * @return converted commission in target currency with specified scale
     */
    BigDecimal convertCommission(BigDecimal commission, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, boolean blocking);

    /**
     * Converts commission from one currency to another using price at specified time.
     * This method block until price for conversion is loaded.
     *
     * @param commission commission which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param time time at which price for conversion is determined
     * @return converted commission in target currency with specified scale
     */
    BigDecimal convertCommission(BigDecimal commission, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, long time);

    /**
     * Converts P/L from one currency to another.
     * This method doesn't block - {@code Double.NaN} is returned when price for conversion is unavailable.
     *
     * @param profitLoss P/L which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @return converted P/L in target currency with specified scale
     */
    double convertProfitLoss(double profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale);

    /**
     * Converts P/L from one currency to another.
     *
     * @param profitLoss P/L which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param blocking pass {@code true} if calling thread is allowed to block when price for conversion need to be loaded, {@code false} otherwise
     * @return converted P/L in target currency with specified scale
     */
    double convertProfitLoss(double profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, boolean blocking);

    /**
     * Converts P/L from one currency to another using price at specified time.
     * This method block until price for conversion is loaded.
     *
     * @param profitLoss P/L which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param time time at which price for conversion is determined
     * @return converted P/L in target currency with specified scale
     */
    double convertProfitLoss(double profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, long time);

    /**
     * Converts P/L from one currency to another.
     * This method doesn't block - {@code null} is returned when price for conversion is unavailable.
     *
     * @param profitLoss P/L which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @return converted P/L in target currency with specified scale
     */
    BigDecimal convertProfitLoss(BigDecimal profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale);

    /**
     * Converts P/L from one currency to another.
     *
     * @param profitLoss P/L which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param blocking pass {@code true} if calling thread is allowed to block when price for conversion need to be loaded, {@code false} otherwise
     * @return converted P/L in target currency with specified scale
     */
    BigDecimal convertProfitLoss(BigDecimal profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, boolean blocking);

    /**
     * Converts P/L from one currency to another using price at specified time.
     * This method block until price for conversion is loaded.
     *
     * @param profitLoss P/L which is to be converted
     * @param sourceCurrency the currency from which conversion is made
     * @param targetCurrency the currency to which conversion is made
     * @param scale result value scale
     * @param time time at which price for conversion is determined
     * @return converted P/L in target currency with specified scale
     */
    BigDecimal convertProfitLoss(BigDecimal profitLoss, ICurrency sourceCurrency, ICurrency targetCurrency, int scale, long time);

}
