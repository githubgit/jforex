package com.dukascopy.api.plugins.ui;

import java.math.BigDecimal;

import javax.swing.JSpinner;

import com.dukascopy.api.IEngine;
import com.dukascopy.api.Instrument;

/**
 * Wrapper for JSpinner component for editing price value.
 */
public interface JFPriceSpinner extends JFComponent<JSpinner> {

    /**
     * Returns current value of spinner.
     *
     * @return current value
     */
    BigDecimal getValue();

    /**
     * Sets value of spinner.
     *
     * @param value new value
     */
    void setValue(BigDecimal value);

    /**
     * Checks validity of current spinner value.
     *
     * @return {@code true} if current value is valid, {@code false} otherwise
     */
    boolean isValidValue();

    /**
     * Returns last valid value of spinner.
     *
     * @return last valid value
     */
    BigDecimal getLastValidValue();

    /**
     * Sets instrument for which the price is selected.
     *
     * @param instrument new instrument
     */
    void setInstrument(Instrument instrument);

    /**
     * Sets value of spinner corresponding to settings for default order entry price.
     *
     * @param curPrice current market price
     * @param command order command
     */
    void setDefaultEntryPrice(BigDecimal curPrice, IEngine.OrderCommand command);

}
