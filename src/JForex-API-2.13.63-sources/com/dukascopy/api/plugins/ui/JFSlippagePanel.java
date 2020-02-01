package com.dukascopy.api.plugins.ui;

import java.math.BigDecimal;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

import com.dukascopy.api.Instrument;

/**
 * Wrapper for component for editing slippage value.
 */
public interface JFSlippagePanel extends JFComponent<JComponent> {

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
     * Enables/disables slippage value editing.
     *
     * @param enabled {@code true} if editing is enabled, {@code false} otherwise
     */
    void setEnabled(boolean enabled);

    /**
     * Sets instrument for which the slippage is selected.
     *
     * @param instrument new instrument
     */
    void setInstrument(Instrument instrument);

    /**
     * Adds change listener to spinner.
     *
     * @param listener change listener
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Removes change listener from spinner.
     *
     * @param listener change listener
     */
    void removeChangeListener(ChangeListener listener);

}
