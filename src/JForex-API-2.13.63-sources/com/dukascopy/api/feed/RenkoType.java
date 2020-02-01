package com.dukascopy.api.feed;

/**
 * Types of Renko bricks.
 * Regular Renko bricks use close price of previous bricks as a reversal, while Median use midpoint price.
 */
public enum RenkoType {
    REGULAR,
    MEDIAN
}
