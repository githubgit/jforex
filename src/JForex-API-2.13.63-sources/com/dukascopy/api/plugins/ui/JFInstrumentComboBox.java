package com.dukascopy.api.plugins.ui;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.dukascopy.api.Instrument;

/**
 * Wrapper for component for Instrument selection.
 */
public interface JFInstrumentComboBox extends JFComponent<JComponent> {

    /**
     * Returns currently selected instrument.
     *
     * @return selected instrument
     */
    Instrument getSelectedInstrument();

    /**
     * Sets new selected instrument.
     *
     * @param instrument new instrument
     */
    void setSelectedInstrument(Instrument instrument);

    /**
     * Returns linkage to other panels status.
     *
     * @return {@code true} if instrument is automatically switched on signals from other panels, {@code false} otherwise
     */
    boolean isLinkedWithOtherPanels();

    /**
     * Sets linkage to other panels status.
     *
     * @param linkedWithOtherPanels {@code true} if instrument should be automatically switched on signals from other panels, {@code false} otherwise
     */
    void setLinkedWithOtherPanels(boolean linkedWithOtherPanels);

    /**
     * Adds action listener to combo box.
     *
     * @param listener action listener
     */
    void addActionListener(ActionListener listener);

    /**
     * Removes action listener from combo box.
     *
     * @param listener action listener
     */
    void removeActionListener(ActionListener listener);

}
