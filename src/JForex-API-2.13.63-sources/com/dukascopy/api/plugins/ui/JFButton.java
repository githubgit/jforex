package com.dukascopy.api.plugins.ui;

import javax.swing.JButton;

/**
 * Wrapper for JButton component.
 */
public interface JFButton extends JFComponent<JButton> {

    /**
     * Button type used to set its appropriate look.
     */
    public enum ButtonType {
        /** Default look. */
        DEFAULT,
        /** Green button. */
        GREEN,
        /** Red button. */
        RED,
        /** Blue button. */
        BLUE
    }

}
