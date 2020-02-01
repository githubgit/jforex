package com.dukascopy.api.indicators;

import com.dukascopy.api.Instrument;

public class InstrumentListDescription implements OptInputDescription {
	private Instrument defaultValue;
	private Instrument[] values;
	
	public InstrumentListDescription(Instrument defaultValue, Instrument[] values) {
        this.defaultValue = defaultValue;
        this.values = values;
    }

	public void setDefaultValue(Instrument defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public Instrument getDefaultValue() {
		return this.defaultValue;
	}
	
	@Override
	public Object getOptInputDefaultValue() {
		return defaultValue;
	}
	
	public Instrument[] getValues() {
		return values;
	}

	public void setValues(Instrument[] values) {
		this.values = values;
	}
}
