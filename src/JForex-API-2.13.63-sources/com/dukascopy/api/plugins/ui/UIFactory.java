package com.dukascopy.api.plugins.ui;

import java.awt.Component;
import java.math.BigDecimal;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.table.TableModel;

import com.dukascopy.api.plugins.IPluginContext;
import com.dukascopy.api.plugins.widget.IPluginWidget;

/**
 * The factory for widget UI objects. 
 * Create and manage platform UI elements from IPluginContext.
 */
public interface UIFactory {	

    /**
     * Creates custom JPanel component.
     *
     * @return wrapper for created component
     */
	JFPanel createPanel();

    /**
     * Creates custom JPanel component with labeled border.
     *
     * @param title label for panel border
     * @return wrapper for created component
     */
	JFPanel createPanel(String title);

    /**
     * Creates custom JScrollPane component.
     *
     * @param component the component to display in the scrollpane's viewport
     * @return wrapper for created component
     * @deprecated use {@link #createScrollPane(java.awt.Component)} instead
     */
    @Deprecated
	JFScrollPane createScrollPanel(Component component);

    /**
     * Creates custom JScrollPane component.
     *
     * @param component the component to display in the scrollpane's viewport
     * @return wrapper for created component
     */
    JFScrollPane createScrollPane(Component component);

    /**
     * Creates custom JScrollPane component with specified vertical/horizontal scrollbar policy.
     *
     * @param component the component to display in the scrollpane's viewport
     * @param vsbPolicy an integer that specifies the vertical scrollbar policy
     * @param hsbPolicy an integer that specifies the horizontal scrollbar policy
     * @return wrapper for created component
     */
	JFScrollPane createScrollPane(Component component, int vsbPolicy, int hsbPolicy);

    /**
     * Creates custom JTable component.
     *
     * @return wrapper for created component
     */
	JFTable createTable();

    /**
     * Creates custom JTable component with specified data model.
     *
     * @param tableModel the data model for the table
     * @return wrapper for created component
     */
	JFTable createTable(TableModel tableModel);

    /**
     * Creates custom JPopupMenu component.
     *
     * @return wrapper for created component
     */
	JFPopupMenu createPopupMenu();

    /**
     * Creates custom JMenuItem component with specified text.
     *
     * @param text the string used to set the text
     * @return wrapper for created component
     */
	JFMenuItem createMenuItem(String text);

    /**
     * Creates custom JMenuItem component with specified text and icon.
     *
     * @param text the string used to set the text
     * @param icon the icon used as the default image
     * @return wrapper for created component
     */
	JFMenuItem createMenuItem(String text, Icon icon);

    /**
     * Creates custom JMenuItem component with specified text and icon URL.
     *
     * @param text the string used to set the text
     * @param iconURL URL of the icon used as the default image
     * @return wrapper for created component
     */
	JFMenuItem createMenuItem(String text, URL iconURL);

    /**
     * Creates custom JLabel component with specified text.
     *
     * @param text the text to be displayed by the label
     * @return wrapper for created component
     */
	JFLabel createLabel(String text);

    /**
     * Creates custom JButton component with specified text.
     *
     * @param text the string used to set the text
     * @return wrapper for created component
     */
	JFButton createButton(String text);

    /**
     * Creates custom JButton component with specified text and type.
     *
     * @param text the string used to set the text
     * @param type type of button used to set its appropriate look
     * @return wrapper for created component
     */
	JFButton createButton(String text, JFButton.ButtonType type);

    /**
     * Creates custom JTextField component.
     *
     * @return wrapper for created component
     */
	JFTextField createTextField();

    /**
     * Creates custom JComboBox component.
     *
     * @param <T> the type of the elements of this combo box
     * @param clazz class of the type T
     * @return wrapper for created component
     */
	<T> JFComboBox<T> createComboBox(Class<T> clazz);

    /**
     * Creates custom JCheckBox component with specified text.
     *
     * @param text the text of the check box
     * @return wrapper for created component
     */
	JFCheckBox createCheckBox(String text);

    /**
     * Creates custom JRadioButton component with specified text.
     *
     * @param text the string used to set the text
     * @return wrapper for created component
     */
	JFRadioButton createRadioButton(String text);

    /**
     * Creates custom JToggleButton component with specified icon.
     *
     * @param icon the icon used as the default image
     * @return wrapper for created component
     */
	JFToggleButton createToggleButton(Icon icon);

    /**
     * Creates custom JToggleButton component with specified icon URL.
     *
     * @param iconURL URL of the icon used as the default image
     * @return wrapper for created component
     */
	JFToggleButton createToggleButton(URL iconURL);

    /**
     * Creates custom JToggleButton component with specified icon and tool tip text.
     *
     * @param icon the icon used as the default image
     * @param tooltipText the string to display tool tip
     * @return wrapper for created component
     */
	JFToggleButton createToggleButton(Icon icon, String tooltipText);

    /**
     * Creates custom JToggleButton component with specified icon URL and tool tip text.
     *
     * @param iconURL URL of the icon used as the default image
     * @param tooltipText the string to display tool tip
     * @return wrapper for created component
     */
	JFToggleButton createToggleButton(URL iconURL, String tooltipText);

    /**
     * Creates custom JSpinner component with number data model.
     *
     * @param value the current value of the model
     * @param minimum the first number in the sequence
     * @param maximum the last number in the sequence
     * @param stepSize the difference between elements of the sequence
     * @return wrapper for created component
     */
	JFNumberSpinner createNumberSpinner(double value, double minimum, double maximum, double stepSize);

    /**
     * Creates custom JSpinner component with number data model.
     *
     * @param value the current value of the model
     * @param minimum the first number in the sequence
     * @param maximum the last number in the sequence
     * @param stepSize the difference between elements of the sequence
     * @param maximumFractionDigits number of displayed fraction digits
     * @return wrapper for created component
     */
	JFNumberSpinner createNumberSpinner(double value, double minimum, double maximum, double stepSize, int maximumFractionDigits);

    /**
     * Creates custom JSpinner component with number data model.
     *
     * @param value the current value of the model
     * @param minimum the first number in the sequence
     * @param maximum the last number in the sequence
     * @param stepSize the difference between elements of the sequence
     * @return wrapper for created component
     */
	JFNumberSpinner createNumberSpinner(long value, long minimum, long maximum, long stepSize);

    /**
     * Creates custom JSpinner component with number data model.
     *
     * @param value the current value of the model
     * @param minimum the first number in the sequence
     * @param maximum the last number in the sequence
     * @param stepSize the difference between elements of the sequence
     * @return wrapper for created component
     */
	JFNumberSpinner createNumberSpinner(int value, int minimum, int maximum, int stepSize);

    /**
     * Creates custom JSpinner component with number data model.
     *
     * @param value the current value of the model
     * @param minimum the first number in the sequence
     * @param maximum the last number in the sequence
     * @param stepSize the difference between elements of the sequence
     * @return wrapper for created component
     */
	JFNumberSpinner createNumberSpinner(BigDecimal value, BigDecimal minimum, BigDecimal maximum, BigDecimal stepSize);

    /**
     * Creates custom component for Instrument selection.
     *
     * @return wrapper for created component
     */
	JFInstrumentComboBox createInstrumentComboBox();

	/**
     * Creates custom component for Instrument selection.
     *
     * @return wrapper for created component
     */
	JFInstrumentComboBox createInstrumentComboBoxSmall();

    /**
     * Creates custom component (with instrument type specific label) for editing order amount.
     *
     * @return wrapper for created component
     */
    JFOrderAmountPanel createOrderAmountPanel();

    /**
     * Creates custom JSpinner component for editing price value.
     *
     * @return wrapper for created component
     */
	JFPriceSpinner createPriceSpinner();

    /**
     * Creates custom component (enabled by check box) for editing slippage value.
     *
     * @param text the text of the check box
     * @return wrapper for created component
     */
	JFSlippagePanel createSlippagePanel(String text);

    /**
     * Creates custom JPanel component with fields for editing order time to live value.
     *
     * @return wrapper for created component
     */
	JFOrderTimeLimitationPanel createOrderTimeLimitationPanel();

    /**
     * Creates custom component for managing plugin presets.
     *
     * @param context plugin context
     * @param widget plugin widget
     * @param parametersEditor object implementing {@link JFPresetsPanel.IParametersEditor} interface
     * @return wrapper for created component
     */
	JFPresetsPanel createPresetsPanel(IPluginContext context, IPluginWidget widget, JFPresetsPanel.IParametersEditor parametersEditor);

}
