package com.dukascopy.api.plugins.ui;

import javax.swing.JComboBox;

/**
 * Wrapper for JComboBox component.
 *
 * @param <T> the type of the elements of this combo box
 */
public interface JFComboBox<T> extends JFComponent<JComboBox<T>> {

    @Override
    JComboBox<T> getComponent();
}
