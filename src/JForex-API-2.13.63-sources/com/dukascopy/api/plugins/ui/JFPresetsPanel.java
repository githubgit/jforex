package com.dukascopy.api.plugins.ui;

import javax.swing.JComponent;
import javax.swing.event.ChangeListener;

/**
 * Wrapper for component for managing plugin presets.
 */
public interface JFPresetsPanel extends JFComponent<JComponent> {

    /**
     * Updates parameters values from active preset.
     */
    void updateParameters();

    /**
     * Applies changes to selected preset.
     * Current parameters values are saved in presets file. New preset created if necessary.
     */
    void applySelectedPreset();

    /**
     * Notifies wrapped component about changes of parameters values from editor.
     *
     * @return {@code true} if changes were applied, {@code false} if component is in process of updating parameters values
     */
    boolean parameterChanged();

    /**
     * Adds listener to external changes of parameters values.
     *
     * @param listener change listener
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Removes listener to external changes of parameters values.
     *
     * @param listener change listener
     */
    void removeChangeListener(ChangeListener listener);


    /**
     * Editor for preset parameters.
     */
    public interface IParametersEditor {

        /**
         * Extracts parameter value from corresponding editor field.
         *
         * @param parameterId ID of parameter
         * @param curValue current parameter value
         * @return new parameter value
         */
        Object extractParameterValue(String parameterId, Object curValue);

        /**
         * Updates parameter value in corresponding editor field.
         *
         * @param parameterId ID of parameter
         * @param newValue new parameter value
         */
        void updateParameterValue(String parameterId, Object newValue);

    }

}
