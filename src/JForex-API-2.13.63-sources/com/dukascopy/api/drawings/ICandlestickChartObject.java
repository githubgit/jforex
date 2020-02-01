package com.dukascopy.api.drawings;

import java.awt.Color;

public interface ICandlestickChartObject extends IFillableChartObject {

	public enum CustomCandleParams {
	    START_TIME(Long.class),
	    END_TIME(Long.class),
	    NUM_OF_PERIODS(Integer.class),
		OPEN_PRICE(Double.class),
		HIGH_PRICE(Double.class),
		LOW_PRICE(Double.class),
		CLOSE_PRICE(Double.class),
		VOLUME(Double.class),
		HIGH_LOW_RANGE(Double.class);

        private final Class<?> type;

        private CustomCandleParams(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }
	}

    void setTrendUpColor(Color color);

    Color getTrendUpColor();

    void setTrendDownColor(Color color);

    Color getTrendDownColor();

	void setTrendUpFillColor(Color color);

	Color getTrendUpFillColor();

	void setTrendDownFillColor(Color color);

	Color getTrendDownFillColor();

}
