package com.dukascopy.api.indicators;

import com.dukascopy.api.Period;

public class PeriodListDescription implements OptInputDescription {
	private Period defaultValue;
	private Period[] values;
	
	public PeriodListDescription(Period defaultValue, Period[] values) {
        this.defaultValue = defaultValue;
        this.values = values;
    }

	public void setDefaultValue(Period defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Period getDefaultValue() {
		return this.defaultValue;
	}
	
	@Override
	public Object getOptInputDefaultValue() {
		return defaultValue;
	}
	
	public Period[] getValues() {
		return values;
	}

	public void setValues(Period[] values) {
		this.values = values;
	}
}
