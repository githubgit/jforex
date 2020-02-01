package com.dukascopy.api.plugins.ui;

import java.math.BigDecimal;

import javax.swing.JSpinner;

/**
 * Wrapper for JSpinner component with number data model.
 */
public interface JFNumberSpinner extends JFComponent<JSpinner> {

    /**
     * Returns current value of spinner.
     *
     * @return current value
     */
    BigDecimal getValue();

    /**
     * Returns last valid value of spinner as int.
     *
     * @return last valid value
     */
    int getIntValue();

    /**
     * Returns last valid value of spinner as long.
     *
     * @return last valid value
     */
    long getLongValue();

    /**
     * Returns last valid value of spinner as double.
     *
     * @return last valid value
     */
    double getDoubleValue();

    /**
     * Sets value of spinner.
     *
     * @param value new value
     */
    void setValue(BigDecimal value);

    /**
     * Sets value of spinner as int.
     *
     * @param value new value
     */
    void setValue(int value);

    /**
     * Sets value of spinner as long.
     *
     * @param value new value
     */
    void setValue(long value);

    /**
     * Sets value of spinner as double.
     *
     * @param value new value
     */
    void setValue(double value);

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

}
