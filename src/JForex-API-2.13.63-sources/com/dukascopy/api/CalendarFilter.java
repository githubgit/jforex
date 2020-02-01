/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link Serializable} implementation of {@link INewsFilter} for {@link INewsFilter.NewsSource#DJ_LIVE_CALENDAR}
 * 
 * @author viktor.sjubajev
 */
public class CalendarFilter extends NewsFilter {

	private static final long serialVersionUID = 2L;

	/**
	 * Utility enum for end date calculations
	 */
	public enum TimeFrame implements ITimeFrame {
		DAY ("Day ahead", 1, TimeUnit.DAYS),
		WEEK ("Week ahead", 7, TimeUnit.DAYS);

		private final String name;
		private final int interval;
		private final TimeUnit timeUnit;

		private TimeFrame(String name, int interval, TimeUnit timeUnit) {
			this.name = name;
			this.interval = interval;
			this.timeUnit = timeUnit;
		}

		@Override
		public int getInterval() {
			return interval;
		}

		@Override
		public TimeUnit getTimeUnit() {
			return timeUnit;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private Type type;

	public CalendarFilter() {
		super();
		timeFrame = TimeFrame.DAY;
		type = Type.IEP;
	}

	public CalendarFilter(final CalendarFilter calendarFilter) {
		super(calendarFilter);
		timeFrame = calendarFilter.getTimeFrame();
		type = calendarFilter.getType();
	}

	@Override
	public Set<NewsSource> getNewsSources() {
		Set<NewsSource> result = new HashSet<>();
		result.add(NewsSource.DJ_LIVE_CALENDAR);
		return result;
	}

	@Override
	public Type getType() {
		return type;
	}

	/**
	 * Sets calendar event's type
	 * 
	 * @param type as {@link INewsFilter.Type}
	 */
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object another) {
		if (super.equals(another)) {
			return true;
		}

		if (another == null) {
			return false;
		}

		if (another instanceof INewsFilter) {
			INewsFilter anotherNewsFilter = (INewsFilter) another;

			if (getTo().equals(anotherNewsFilter.getTo())) {
				return false;
			}

			if (type != anotherNewsFilter.getType()) {
				return false;
			}

			return true;
		}

		return false;
	}

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
	public String toString() {
		return "[CalendarFilter]\n" +
			"countries : "           + getCountries() 				+ "\n" +
			"stock indicies : "      + getStockIndicies() 			+ "\n" +
            "event categories : "    + getEventCategories()         + "\n" +
			"time frame : "          + timeFrame						+ "\n" +
			"from date : "           + DATE_FORMAT.format(getFrom())	+ "\n" +
			"to date : "             + DATE_FORMAT.format(getTo())	+ "\n" +
			"keywords : "            + getKeywords()					+ "\n" +
			"type : "                + type;
	}
}