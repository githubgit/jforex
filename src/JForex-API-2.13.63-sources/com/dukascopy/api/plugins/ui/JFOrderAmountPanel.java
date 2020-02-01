package com.dukascopy.api.plugins.ui;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.math.BigDecimal;

import javax.swing.JComponent;

import com.dukascopy.api.Instrument;

/**
 * Wrapper for component for for editing order amount.
 */
public interface JFOrderAmountPanel extends JFComponent<JComponent> {

    /**
     * Returns current value of spinner.
     *
     * @return current value
     */
    BigDecimal getValue();

    /**
     * Returns last valid amount value in millions.
     *
     * @return value in millions
     */
    BigDecimal getValueInMillions();

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
     * Sets instrument for which the amount is selected.
     *
     * @param instrument new instrument
     */
    void setInstrument(Instrument instrument);

    /**
     * Adds action listener to spinner.
     *
     * @param listener action listener
     */
    void addActionListener(ActionListener listener);

    /**
     * Removes action listener from spinner.
     *
     * @param listener action listener
     */
    void removeActionListener(ActionListener listener);

    /**
     * Switches "Amount" component between label and checkbox
     * @param selectable if true - checkbox, label otherwise
     */
    void setSelectable(boolean selectable);

    /**
     * When setSelectable(true) was called
     * @return selection state of "Amount" checkbox
     */
    boolean isSelected();

    /**
     * When setSelectable(true) was called
     * @param selected sets selection state of "Amount" checkbox
     */
    void setSelected(boolean selected);

    /**
     * If "Amount" checkbox is visible, added itemListener is fired on any action.
     * @param itemListener listener to add
     */
    void addSelectableItemListener(ItemListener itemListener);

    /**
     * If "Amount" checkbox is visible, added itemListener is fired on any action.
     * @param itemListener listener to remove
     */
    void removeSelectableItemListener(ItemListener itemListener);
}
