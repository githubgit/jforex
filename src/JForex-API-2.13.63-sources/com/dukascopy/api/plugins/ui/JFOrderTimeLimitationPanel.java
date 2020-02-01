package com.dukascopy.api.plugins.ui;

import java.awt.event.ItemListener;

import javax.swing.JPanel;

import com.dukascopy.api.Instrument;

/**
 * Wrapper for JPanel component with fields for editing order time to live value.
 */
public interface JFOrderTimeLimitationPanel extends JFComponent<JPanel> {

    /**
     * Return selected order time to live value.
     *
     * @return selected value
     */
    Long getTimeValue();

    /**
     * Sets instrument for which the order time to live is selected.
     *
     * @param instrument new instrument
     */
    void setInstrument(Instrument instrument);

    /**
     * Switches "Expiration" component between label and checkbox
     * @param selectable if true - checkbox, label otherwise
     */
    void setSelectable(boolean selectable);

    /**
     * When setSelectable(true) was called
     * @return selection state of "Expiration" checkbox
     */
    boolean isSelected();

    /**
     * When setSelectable(true) was called
     * @param selected sets selection state of "Expiration" checkbox
     */
    void setSelected(boolean selected);

    /**
     * If "Expiration" checkbox is visible, added itemListener is fired on any action.
     * @param itemListener listener to add
     */
    void addSelectableItemListener(ItemListener itemListener);

    /**
     * If "Expiration" checkbox is visible, added itemListener is fired on any action.
     * @param itemListener listener to remove
     */
    void removeSelectableItemListener(ItemListener itemListener);
}
