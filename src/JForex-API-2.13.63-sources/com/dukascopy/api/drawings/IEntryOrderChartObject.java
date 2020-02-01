package com.dukascopy.api.drawings;

/**
 * Chart object for displaying entry order preview line.
 */
public interface IEntryOrderChartObject extends IOrderLineChartObject {

    /**
     * Sets LONG/SHORT order flag.
     *
     * @param isLong {@code true} if order is LONG, {@code false} if it is SHORT
     */
	void setLong(boolean isLong);

    /**
     * Sets price slippage value.
     *
     * @param slippage slippage value
     */
	void setSlippage(double slippage);

}
