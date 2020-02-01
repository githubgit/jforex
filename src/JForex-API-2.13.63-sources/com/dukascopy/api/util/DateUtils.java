/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author Mark Vilkel, Artur Fyodorov
 */
public class DateUtils {

	public static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT 0");
	
    private final static ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            simpleDateFormat.setTimeZone(GMT_TIME_ZONE);
            return simpleDateFormat;
        }
    };
    
    private final static ThreadLocal<SimpleDateFormat> TO_SECONDS_DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(GMT_TIME_ZONE);
            return simpleDateFormat;
        }
    };
	
	/**
	 * Returns formatted time, passed as milliseconds (long), with millisecond precision
	 * 
	 * @param time Time to format
	 * @return Formatted time
	 */
	public static String format(long time) {
		SimpleDateFormat simpleDateFormat = DATE_FORMAT_THREAD_LOCAL.get();
		String result = simpleDateFormat.format(time);
		return result;
	}
	
	public static long parse(String time) throws ParseException {
	    SimpleDateFormat simpleDateFormat = DATE_FORMAT_THREAD_LOCAL.get();
	    long result = simpleDateFormat.parse(time).getTime();
		return result;
	}

	public static String format(Object object) {
	    SimpleDateFormat simpleDateFormat = DATE_FORMAT_THREAD_LOCAL.get();
	    String result = simpleDateFormat.format(object);
		return result;
	}

	/**
	 * Formats passed time, passed as milliseconds, with second precision
	 * 
	 * @param time Time to format
	 * @return Formatted time
	 */
	public static String formatToSeconds(long time) {
	    SimpleDateFormat simpleDateFormat = TO_SECONDS_DATE_FORMAT_THREAD_LOCAL.get();
	    String result = simpleDateFormat.format(time);
		return result;
	}

	public static long parseAsSeconds(String time) throws ParseException {
	    SimpleDateFormat simpleDateFormat = TO_SECONDS_DATE_FORMAT_THREAD_LOCAL.get();
	    long result = simpleDateFormat.parse(time).getTime();
		return result;
	}
	
	public static String formatToSeconds(Object object) {
	    SimpleDateFormat simpleDateFormat = TO_SECONDS_DATE_FORMAT_THREAD_LOCAL.get();
	    String result = simpleDateFormat.format(object);
		return result;
	}
}
