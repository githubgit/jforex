/**
 * Copyright 2018 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.instrument;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public interface IMarketInfo {
    
    String getShortName();
    String getName();
    
    DayOfWeek getFirstWorkingDay();
    DayOfWeek getLastWorkingDay();
    
    /**
     * Open time at market time zone {@link #getZoneId()}
     * @return open time
     */
    LocalTime getOpenTime();

    /**
     * Close time at market time zone {@link #getZoneId()}
     * @return close time
     */
    LocalTime getCloseTime();
    
    ZoneId getZoneId();
    
    boolean isOpenNow();
    boolean isOpenAt(Instant dateTime);
    boolean isWeekendNow();
    boolean isWeekendAt(Instant dateTime);
    
    Duration untilOpen();
    Duration untilClose();
}
