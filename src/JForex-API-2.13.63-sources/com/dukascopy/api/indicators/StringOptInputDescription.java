package com.dukascopy.api.indicators;

/**
 * Describes optional input as a string value
 */
public class StringOptInputDescription implements OptInputDescription {
    private String defaultValue;   

    /**
     * Creates an object
     */
	public StringOptInputDescription() {
    }

    /**
     * Creates an object with default value
     * 
     * @param defaultValue default value
     */
    public StringOptInputDescription(String defaultValue) {
        this.defaultValue = defaultValue;        
    }

    /**
     * Returns the default value
     * 
     * @return default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }
    
    /**
	 * The method sets the default value
	 * 
	 * @param defaultValue default value
	 */
    public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
    
    /**
     * Returns parameter's default value
     * 
     * @return default value
     */
    @Override
    public Object getOptInputDefaultValue() {
        return String.valueOf(defaultValue);
    }
}
