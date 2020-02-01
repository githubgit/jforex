/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Default {@link Serializable} implementation of {@link INewsFilter}
 * 
 * @author viktor.sjubajev
 */
public class NewsFilter implements INewsFilter, Serializable {

	private static final long serialVersionUID = 2L;
	protected static final TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT0");
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss") {{setTimeZone(TIME_ZONE);}};
	
	public interface ITimeFrame {
		int getInterval();
		TimeUnit getTimeUnit();
	}
	
	/**
	 * Utility enum for end date calculations
	 */
	public enum TimeFrame implements ITimeFrame {
		ONLINE ("Online", 0, null),
		LAST_10_MINUTES ("Last 10 minutes", -10, TimeUnit.MINUTES),
		LAST_30_MINUTES ("Last 30 minutes", -30, TimeUnit.MINUTES),
		LAST_HOUR ("Last hour", -1, TimeUnit.HOURS),
		TODAY ("Today", 1, TimeUnit.DAYS),
		SPECIFIC_DATE ("Specific date", 1, TimeUnit.DAYS);

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

	protected ITimeFrame timeFrame;
	protected Date fromDate;
	private boolean onlyHot;
	private final Set<Country> countries;
	private final Set<StockIndex> stockIndicies;
	private final Set<MarketSector> marketSectors;
	private final Set<Currency> currencies;
	private final Set<EventCategory> eventCategories;
	private final Set<String> keywords;
	
	public NewsFilter() {
		timeFrame = TimeFrame.ONLINE;
		fromDate = new Date();
		onlyHot = false;
		countries = EnumSet.noneOf(Country.class);
		stockIndicies = EnumSet.noneOf(StockIndex.class);
		marketSectors = EnumSet.noneOf(MarketSector.class);
		currencies = new HashSet<>();
		keywords = new HashSet<>();
		eventCategories = new HashSet<>();
	}

	public NewsFilter(final NewsFilter newsFilter) {
		timeFrame = newsFilter.timeFrame;
		onlyHot = newsFilter.isOnlyHot();
		fromDate = newsFilter.fromDate;

		if (!isNullOrEmpty(newsFilter.getCountries())) {
			countries = EnumSet.copyOf(newsFilter.getCountries());
		} else {
			countries = EnumSet.noneOf(Country.class);
		}

		if (!isNullOrEmpty(newsFilter.getStockIndicies())) {
			stockIndicies = EnumSet.copyOf(newsFilter.getStockIndicies());
		} else {
			stockIndicies = EnumSet.noneOf(StockIndex.class);
		}

		if (!isNullOrEmpty(newsFilter.getMarketSectors())) {
			marketSectors = EnumSet.copyOf(newsFilter.getMarketSectors());
		} else {
			marketSectors = EnumSet.noneOf(MarketSector.class);
		}
		
		if (!isNullOrEmpty(newsFilter.getCurrencies())) {
			currencies = new HashSet<>(newsFilter.getCurrencies());
		} else {
			currencies = EnumSet.noneOf(Currency.class);
		}

		if (!isNullOrEmpty(newsFilter.getKeywords())) {
			keywords = new HashSet<>(newsFilter.getKeywords());
		} else {
			keywords = new HashSet<>();
		}
		
		if (!isNullOrEmpty(newsFilter.getEventCategories())) {
		    eventCategories = EnumSet.copyOf(newsFilter.getEventCategories());
		}else {
		    eventCategories = EnumSet.noneOf(EventCategory.class);
        }
	}

	@Override
	public Set<NewsSource> getNewsSources() {
		Set<NewsSource> result = new LinkedHashSet<>();
		result.add(NewsSource.FXSPIDER_NEWS);
		result.add(NewsSource.DUKASCOPY_WEBINAR);
		return result;
	}
	
	@Override
	public NewsSource getNewsSource() {
		Set<NewsSource> sources = getNewsSources();
		if (sources == null || sources.isEmpty()) {
			return null;
		}
		else {
			NewsSource result = sources.iterator().next();
			return result;
		}
	}

	@Override	
	public final boolean isOnlyHot() {
		return onlyHot;
	}

	public final void setOnlyHot(boolean value) {
		this.onlyHot = value;
	}

	@Override
	public final Set<Country> getCountries() {
		return countries;
	}
	
	@Override
	public final Set<StockIndex> getStockIndicies() {
		return stockIndicies;
	}

	@Override
	public final Set<MarketSector> getMarketSectors() {
		return marketSectors;
	}

	@Override
	public final Set<Currency> getCurrencies() {
		return currencies;
	}

    @Override
    public Set<EventCategory> getEventCategories() {
        return eventCategories;
    }   

	@Override
	public final Date getFrom() {
		Calendar calendar = Calendar.getInstance(TIME_ZONE);
		calendar.setTimeInMillis(System.currentTimeMillis());

		TimeUnit timeUnit = timeFrame.getTimeUnit();
		
		if (timeUnit != null) {
			switch (timeUnit) {
				case MINUTES:
					calendar.add(Calendar.MINUTE, timeFrame.getInterval());
					break;
				case HOURS:
					calendar.add(Calendar.HOUR_OF_DAY, timeFrame.getInterval());
					break;
				case DAYS:
					calendar.setTime(midnight(fromDate == null ? calendar.getTime():fromDate));
					break;
			}
		} else {
			return null;
		}

		return calendar.getTime();
	}

	public final void setFrom(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	@Override
	public final Date getTo() {
		Calendar calendar = Calendar.getInstance(TIME_ZONE);

		TimeUnit timeUnit = timeFrame.getTimeUnit();
		
		if (timeUnit != null) {
			switch (timeUnit) {
				case MINUTES:
				case HOURS:
					calendar.setTimeInMillis(System.currentTimeMillis());
					break;
				case DAYS:
					calendar.setTime(getFrom());
					calendar.add(Calendar.DAY_OF_YEAR, timeFrame.getInterval());
					calendar.add(Calendar.MILLISECOND, -1);
			}
		} else {
			return null;
		}

		return calendar.getTime();		
	}

	@Override
	public final Set<String> getKeywords() {
		return keywords;
	}

	@Override
	public Type getType() {
		return null;
	}

	public final void setTimeFrame(ITimeFrame timeFrame) {
		this.timeFrame = timeFrame;
		if (timeFrame instanceof NewsFilter.TimeFrame && timeFrame != TimeFrame.SPECIFIC_DATE) {
			setFrom(null);
		}
	}

	public final ITimeFrame getTimeFrame() {
		return timeFrame;
	}

	@Override
	public boolean equals(Object other) {
		if (super.equals(other)) {
			return true;
		}

		if (other == null) {
			return false;
		}

		if (other instanceof NewsFilter) {
			NewsFilter otherNewsFilter = (NewsFilter) other;

			if (timeFrame != otherNewsFilter.timeFrame) {
				return false;
			}

			if (onlyHot != otherNewsFilter.isOnlyHot()) {
				return false;
			}
			
			if (!countries.equals(otherNewsFilter.getCountries())) {
				return false;
			}

			if (!stockIndicies.equals(otherNewsFilter.getStockIndicies())) {
				return false;
			}
			
			if (!marketSectors.equals(otherNewsFilter.getMarketSectors())) {
				return false;
			}

			if (!currencies.equals(otherNewsFilter.getCurrencies())) {
				return false;
			}

			if (!eventCategories.equals(otherNewsFilter.getEventCategories())) {
			    return false;
			}

            if (!keywords.equals(otherNewsFilter.getKeywords())) {
				return false;
			}

			return true;
		}

		return false;
	}

    @Override
    public int hashCode() {
        int result = timeFrame.hashCode();
        result = 31 * result + fromDate.hashCode();
        result = 31 * result + (onlyHot ? 1 : 0);
        result = 31 * result + countries.hashCode();
        result = 31 * result + stockIndicies.hashCode();
        result = 31 * result + marketSectors.hashCode();
        result = 31 * result + currencies.hashCode();
        result = 31 * result + eventCategories.hashCode();
        result = 31 * result + keywords.hashCode();
        return result;
    }

    @Override
	public String toString() {
		return "[NewsFilter]\n" +
			"time frame : " 	+ timeFrame 	+ "\n" +
			"from date : "		+ (getFrom() == null ? "null" : DATE_FORMAT.format(getFrom())) + "\n" +
			"to date : "		+ (getTo() == null ? "null" : DATE_FORMAT.format(getTo()))	+ "\n" +
			"only hot :"		+ onlyHot	 	+ "\n" +
			"countries : "	 	+ countries 	+ "\n" +
			"stock indicies : "	+ stockIndicies + "\n" +
			"market sectors : " + marketSectors + "\n" +
			"currencies : " 	+ currencies	+ "\n" +
			"event categories: "+ eventCategories + "\n" + 
			"keywords : " 		+ keywords;
	}

	/**
	 * Calculates midnight of given date based on GMT0 time zone
	 * 
	 * @param date as {@link Date}
	 * @return midnight as {@link Date}
	 */
	private Date midnight(Date date) {
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.setTimeZone(TIME_ZONE);//IMPORTANT : don't move it before fields reset

		return calendar.getTime();
	}
	
	/**
     * Test given collection for null and empty.
     * @param col {@link Collection} to test
     * @return <code>true</code> if given collection is null or empty, otherwise return <code>false</code>
     */
    private boolean isNullOrEmpty(Collection col){
        return col == null || col.isEmpty();
    }
}