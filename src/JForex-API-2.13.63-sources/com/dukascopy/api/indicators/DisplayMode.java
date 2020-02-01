package com.dukascopy.api.indicators;

/**
 * Display mode for indicators with custom period, larger than chart period.
 * Determines where indicator values must be drawn on chart.
 */
public enum DisplayMode {
    /** Last chart bar in custom period. */
    CUSTOM_PERIOD_END,
    /** First chart bar in custom period. */
    CUSTOM_PERIOD_START,
    /** Middle chart bar in custom period. */
    CUSTOM_PERIOD_MIDDLE
}
