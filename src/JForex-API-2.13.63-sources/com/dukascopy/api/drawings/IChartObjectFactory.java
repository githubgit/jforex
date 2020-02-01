/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import com.dukascopy.api.IChartObject;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.instrument.IFinancialInstrument;

public interface IChartObjectFactory {

    /**
     * Returns default instrument used for creating chart objects or <code>null</code> for using primary chart instrument.
     *
     * @return default instrument for creating chart objects
     */
    Instrument getInstrument();

    /**
     * Sets default instrument used for creating chart objects or <code>null</code> for using primary chart instrument.
     *
     * @param instrument default instrument for creating chart objects
     */
    void setInstrument(Instrument instrument);

	/**
     * Creates <code>IRectangleChartObject</code> with default parameters.
     *
     * @return instance of <code>IRectangleChartObject</code>
     */
    IRectangleChartObject createRectangle();

	/**
	 * Creates <code>IRectangleChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IRectangleChartObject</code>
	 */
	IRectangleChartObject createRectangle(String key);

	/**
     * Constructs <code>IRectangleChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IRectangleChartObject</code>
     */
    IRectangleChartObject createRectangle(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IEllipseChartObject</code> with default parameters.
     *
     * @return instance of <code>IEllipseChartObject</code>
     */
	IEllipseChartObject createEllipse();

	/**
	 * Creates <code>IEllipseChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IEllipseChartObject</code>
	 */
	IEllipseChartObject createEllipse(String key);

	/**
     * Constructs <code>IEllipseChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IEllipseChartObject</code>
     */
    IEllipseChartObject createEllipse(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>ITriangleChartObject</code> with default parameters.
     *
     * @return instance of <code>ITriangleChartObject</code>
     */
    ITriangleChartObject createTriangle();

	/**
	 * Creates <code>ITriangleChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ITriangleChartObject</code>
	 */
	ITriangleChartObject createTriangle(String key);

	/**
     * Constructs <code>ITriangleChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @param time3 3rd point value in ms
     * @param price3 3rd point price value
     * @return instance of <code>ITriangleChartObject</code>
     */
    ITriangleChartObject createTriangle(String key, long time1, double price1, long time2, double price2, long time3, double price3);

	/**
     * Creates <code>IHorizontalLineChartObject</code> with default parameters.
     *
     * @return instance of <code>IHorizontalLineChartObject</code>
     */
    IHorizontalLineChartObject createHorizontalLine();

	/**
	 * Creates <code>IHorizontalLineChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IHorizontalLineChartObject</code>
	 */
	IHorizontalLineChartObject createHorizontalLine(String key);

	/**
     * Constructs <code>IHorizontalLineChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param price price value
     * @return instance of <code>IHorizontalLineChartObject</code>
     */
    IHorizontalLineChartObject createHorizontalLine(String key, double price);

	/**
     * Creates <code>IVerticalLineChartObject</code> with default parameters.
     *
     * @return instance of <code>IVerticalLineChartObject</code>
     */
    IVerticalLineChartObject createVerticalLine();

	/**
	 * Creates <code>IVerticalLineChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IVerticalLineChartObject</code>
	 */
	IVerticalLineChartObject createVerticalLine(String key);

	/**
     * Constructs <code>IVerticalLineChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time time value in ms
     * @return instance of <code>IVerticalLineChartObject</code>
     */
    IVerticalLineChartObject createVerticalLine(String key, long time);

	/**
     * Creates <code>ILongLineChartObject</code> with default parameters.
     *
     * @return instance of <code>ILongLineChartObject</code>
     */
    ILongLineChartObject createLongLine();

	/**
	 * Creates <code>ILongLineChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ILongLineChartObject</code>
	 */
	ILongLineChartObject createLongLine(String key);

	/**
     * Constructs <code>ILongLineChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>ILongLineChartObject</code>
     */
    ILongLineChartObject createLongLine(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>ITextChartObject</code> with default parameters.
     *
     * @return instance of <code>ITextChartObject</code>
     */
    ITextChartObject createText();

	/**
	 * Creates <code>ITextChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ITextChartObject</code>
	 */
	ITextChartObject createText(String key);

	/**
     * Constructs <code>ITextChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @param price price value
     * @return instance of <code>ITextChartObject</code>
     */
    ITextChartObject createText(String key, long time, double price);

	/**
     * Creates <code>ISignalUpChartObject</code> with default parameters.
     *
     * @return instance of <code>ISignalUpChartObject</code>
     */
    ISignalUpChartObject createSignalUp();

	/**
	 * Creates <code>ISignalUpChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ISignalUpChartObject</code>
	 */
	ISignalUpChartObject createSignalUp(String key);

	/**
     * Constructs <code>ISignalUpChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @param price price value
     * @return instance of <code>ISignalUpChartObject</code>
     */
    ISignalUpChartObject createSignalUp(String key, long time, double price);

	/**
     * Creates <code>ISignalDownChartObject</code> with default parameters.
     *
     * @return instance of <code>ISignalDownChartObject</code>
     */
    ISignalDownChartObject createSignalDown();

	/**
	 * Creates <code>ISignalDownChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ISignalDownChartObject</code>
	 */
	ISignalDownChartObject createSignalDown(String key);

	/**
     * Constructs <code>ISignalDownChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @param price price value
     * @return instance of <code>ISignalDownChartObject</code>
     */
    ISignalDownChartObject createSignalDown(String key, long time, double price);

	/**
     * Creates <code>IShortLineChartObject</code> with default parameters.
     *
     * @return instance of <code>IShortLineChartObject</code>
     */
    IShortLineChartObject createShortLine();

	/**
	 * Creates <code>IShortLineChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IShortLineChartObject</code>
	 */
	IShortLineChartObject createShortLine(String key);

	/**
     * Constructs <code>IShortLineChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IShortLineChartObject</code>
     */
    IShortLineChartObject createShortLine(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IRayLineChartObject</code> with default parameters.
     *
     * @return instance of <code>IRayLineChartObject</code>
     */
    IRayLineChartObject createRayLine();

	/**
	 * Creates <code>IRayLineChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IRayLineChartObject</code>
	 */
	IRayLineChartObject createRayLine(String key);

	/**
     * Constructs <code>IRayLineChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IRayLineChartObject</code>
     */
    IRayLineChartObject createRayLine(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>ITimeMarkerChartObject</code> with default parameters.
     *
     * @return instance of <code>ITimeMarkerChartObject</code>
     */
    ITimeMarkerChartObject createTimeMarker();

	/**
	 * Creates <code>ITimeMarkerChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ITimeMarkerChartObject</code>
	 */
	ITimeMarkerChartObject createTimeMarker(String key);

	/**
     * Constructs <code>ITimeMarkerChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @return instance of <code>ITimeMarkerChartObject</code>
     */
    ITimeMarkerChartObject createTimeMarker(String key, long time);

	/**
     * Creates <code>IPriceMarkerChartObject</code> with default parameters.
     *
     * @return instance of <code>IPriceMarkerChartObject</code>
     */
    IPriceMarkerChartObject createPriceMarker();

	/**
	 * Creates <code>IPriceMarkerChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IPriceMarkerChartObject</code>
	 */
	IPriceMarkerChartObject createPriceMarker(String key);

	/**
     * Constructs <code>IPriceMarkerChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param price price value
     * @return instance of <code>IPriceMarkerChartObject</code>
     */
    IPriceMarkerChartObject createPriceMarker(String key, double price);

	/**
     * Creates <code>IPolyLineChartObject</code> with default parameters.
     *
     * @return instance of <code>IPolyLineChartObject</code>
     */
    IPolyLineChartObject createPolyLine();

	/**
	 * Creates <code>IPolyLineChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IPolyLineChartObject</code>
	 */
	IPolyLineChartObject createPolyLine(String key);

	/**
     * Creates <code>IChannelChartObject</code> with default parameters.
     *
     * @return instance of <code>IChannelChartObject</code>
     */
    IChannelChartObject createChannel();

	/**
	 * Creates <code>IChannelChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IChannelChartObject</code>
	 */
	IChannelChartObject createChannel(String key);

	/**
     * Constructs <code>IChannelChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @param time3 3rd point value in ms
     * @param price3 3rd point price value
     * @return instance of <code>IChannelChartObject</code>
     */
    IChannelChartObject createChannel(String key, long time1, double price1, long time2, double price2, long time3, double price3);

	/**
     * Creates <code>ICyclesChartObject</code> with default parameters.
     *
     * @return instance of <code>ICyclesChartObject</code>
     */
    ICyclesChartObject createCycles();

	/**
	 * Creates <code>ICyclesChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ICyclesChartObject</code>
	 */
	ICyclesChartObject createCycles(String key);

	/**
     * Constructs <code>ICyclesChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @param timeStep cycle interval in ms
     * @return instance of <code>ICyclesChartObject</code>
     */
    ICyclesChartObject createCycles(String key, long time, long timeStep);

	/**
     * Creates <code>IPercentChartObject</code> with default parameters.
     *
     * @return instance of <code>IPercentChartObject</code>
     */
    IPercentChartObject createPercent();

	/**
	 * Creates <code>IPercentChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IPercentChartObject</code>
	 */
	IPercentChartObject createPercent(String key);

	/**
     * Constructs <code>IPercentChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IPercentChartObject</code>
     */
    IPercentChartObject createPercent(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IFiboArcChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboArcChartObject</code>
     */
    IFiboArcChartObject createFiboArc();

	/**
	 * Creates <code>IFiboArcChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboArcChartObject</code>
	 */
	IFiboArcChartObject createFiboArc(String key);

	/**
     * Constructs <code>IFiboArcChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IFiboArcChartObject</code>
     */
    IFiboArcChartObject createFiboArc(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IFiboCirclesChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboCirclesChartObject</code>
     */
    IFiboCirclesChartObject createFiboCircles();

	/**
	 * Creates <code>IFiboCirclesChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboCirclesChartObject</code>
	 */
	IFiboCirclesChartObject createFiboCircles(String key);

	/**
     * Constructs <code>IFiboCirclesChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IFiboCirclesChartObject</code>
     */
    IFiboCirclesChartObject createFiboCircles(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IFiboFanChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboFanChartObject</code>
     */
    IFiboFanChartObject createFiboFan();

	/**
	 * Creates <code>IFiboFanChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboFanChartObject</code>
	 */
	IFiboFanChartObject createFiboFan(String key);

	/**
     * Constructs <code>IFiboFanChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IFiboFanChartObject</code>
     */
    IFiboFanChartObject createFiboFan(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IFiboRetracementChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboRetracementChartObject</code>
     */
    IFiboRetracementChartObject createFiboRetracement();

	/**
	 * Creates <code>IFiboRetracementChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboRetracementChartObject</code>
	 */
	IFiboRetracementChartObject createFiboRetracement(String key);

	/**
     * Constructs <code>IFiboRetracementChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IFiboRetracementChartObject</code>
     */
    IFiboRetracementChartObject createFiboRetracement(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IFiboTimeZonesChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboTimeZonesChartObject</code>
     */
    IFiboTimeZonesChartObject createFiboTimeZones();

	/**
	 * Creates <code>IFiboTimeZonesChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboTimeZonesChartObject</code>
	 */
	IFiboTimeZonesChartObject createFiboTimeZones(String key);

	/**
     * Constructs <code>IFiboTimeZonesChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IFiboTimeZonesChartObject</code>
     */
    IFiboTimeZonesChartObject createFiboTimeZones(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IFiboTimeZonesPercentsChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboTimeZonesPercentsChartObject</code>
     */
    IFiboTimeZonesPercentsChartObject createFiboTimeZonesPercents();

	/**
	 * Creates <code>IFiboTimeZonesPercentsChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboTimeZonesPercentsChartObject</code>
	 */
	IFiboTimeZonesPercentsChartObject createFiboTimeZonesPercents(String key);

	/**
     * Constructs <code>IFiboTimeZonesPercentsChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @return instance of <code>IFiboTimeZonesPercentsChartObject</code>
     */
    IFiboTimeZonesPercentsChartObject createFiboTimeZonesPercents(String key, long time1, double price1, long time2, double price2);

	/**
     * Creates <code>IFiboExpansionChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboExpansionChartObject</code>
     */
    IFiboExpansionChartObject createFiboExpansion();

	/**
	 * Creates <code>IFiboExpansionChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboExpansionChartObject</code>
	 */
	IFiboExpansionChartObject createFiboExpansion(String key);

	/**
     * Constructs <code>IFiboExpansionChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @param time3 3rd point value in ms
     * @param price3 3rd point price value
     * @return instance of <code>IFiboExpansionChartObject</code>
     */
    IFiboExpansionChartObject createFiboExpansion(String key, long time1, double price1, long time2, double price2, long time3, double price3);

	/**
     * Creates <code>IFiboChannelChartObject</code> with default parameters.
     *
     * @return instance of <code>IFiboChannelChartObject</code>
     */
    IFiboChannelChartObject createFiboChannel();

	/**
	 * Creates <code>IFiboChannelChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IFiboChannelChartObject</code>
	 */
	IFiboChannelChartObject createFiboChannel(String key);

	/**
     * Constructs <code>IFiboChannelChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @param time3 3rd point value in ms
     * @param price3 3rd point price value
     * @return instance of <code>IFiboChannelChartObject</code>
     */
    IFiboChannelChartObject createFiboChannel(String key, long time1, double price1, long time2, double price2, long time3, double price3);

	/**
     * Creates <code>IAndrewsPitchforkChartObject</code> with default parameters.
     *
     * @return instance of <code>IAndrewsPitchforkChartObject</code>
     */
    IAndrewsPitchforkChartObject createAndrewsPitchfork();

	/**
	 * Creates <code>IAndrewsPitchforkChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IAndrewsPitchforkChartObject</code>
	 */
	IAndrewsPitchforkChartObject createAndrewsPitchfork(String key);

	/**
     * Constructs <code>IAndrewsPitchforkChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param price1 1st point price value
     * @param time2 2nd point value in ms
     * @param price2 2nd point price value
     * @param time3 3rd point value in ms
     * @param price3 3rd point price value
     * @return instance of <code>IAndrewsPitchforkChartObject</code>
     */
    IAndrewsPitchforkChartObject createAndrewsPitchfork(String key, long time1, double price1, long time2, double price2, long time3, double price3);

    /**
     * @deprecated use {@link #createPriceMarker()} instead
     * Creates <code>IOrderLineChartObject</code> with default parameters.
     *
     * @return instance of <code>IOrderLineChartObject</code>
     */
    @Deprecated
    IOrderLineChartObject createOrderLine();

    /**
     * @deprecated use {@link #createPriceMarker(String)} instead
     * Creates <code>IOrderLineChartObject</code> with default parameters.
     *
     * @param key String identifier
     * @return instance of <code>IOrderLineChartObject</code>
     */
    @Deprecated
    IOrderLineChartObject createOrderLine(String key);

    /**
     * @deprecated use {@link #createPriceMarker(String, double)} instead
     * Constructs <code>IOrderLineChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param price price value
     * @return instance of <code>IOrderLineChartObject</code>
     */
    @Deprecated
    IOrderLineChartObject createOrderLine(String key, double price);

    /**
     * Creates <code>IEntryOrderChartObject</code> with default parameters.
     *
     * @return instance of <code>IEntryOrderChartObject</code>
     */
    IEntryOrderChartObject createEntryOrderLine();

    /**
     * Creates <code>IEntryOrderChartObject</code> with default parameters.
     *
     * @param key String identifier
     * @return instance of <code>IEntryOrderChartObject</code>
     */
    IEntryOrderChartObject createEntryOrderLine(String key);

    /**
     * Constructs <code>IEntryOrderChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param price price value
     * @return instance of <code>IEntryOrderChartObject</code>
     */
    IEntryOrderChartObject createEntryOrderLine(String key, double price);

    /**
     * Creates <code>IStopLossChartObject</code> with default parameters.
     *
     * @return instance of <code>IStopLossChartObject</code>
     */
    IStopLossChartObject createStopLossLine();

    /**
     * Creates <code>IStopLossChartObject</code> with default parameters.
     *
     * @param key String identifier
     * @return instance of <code>IStopLossChartObject</code>
     */
    IStopLossChartObject createStopLossLine(String key);

    /**
     * Constructs <code>IStopLossChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param price price value
     * @return instance of <code>IStopLossChartObject</code>
     */
    IStopLossChartObject createStopLossLine(String key, double price);

    /**
     * Creates <code>ITakeProfitChartObject</code> with default parameters.
     *
     * @return instance of <code>ITakeProfitChartObject</code>
     */
    ITakeProfitChartObject createTakeProfitLine();

    /**
     * Creates <code>ITakeProfitChartObject</code> with default parameters.
     *
     * @param key String identifier
     * @return instance of <code>ITakeProfitChartObject</code>
     */
    ITakeProfitChartObject createTakeProfitLine(String key);

    /**
     * Constructs <code>ITakeProfitChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param price price value
     * @return instance of <code>ITakeProfitChartObject</code>
     */
    ITakeProfitChartObject createTakeProfitLine(String key, double price);

	/**
     * Creates <code>ILabelChartObject</code> with default parameters.
     *
     * @return instance of <code>ILabelChartObject</code>
     */
    ILabelChartObject createLabel();

	/**
	 * Creates <code>ILabelChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ILabelChartObject</code>
	 */
	ILabelChartObject createLabel(String key);

	/**
     * Constructs <code>ILabelChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @param price price value
     * @return instance of <code>ILabelChartObject</code>
     */
    ILabelChartObject createLabel(String key, long time, double price);

    /**
     * Creates <code>IScreenLabelChartObject</code> with default parameters.
     *
     * @return instance of <code>IScreenLabelChartObject</code>
     */
    IScreenLabelChartObject createScreenLabel();

    /**
     * Creates <code>IScreenLabelChartObject</code> with default parameters.
     *
     * @param key String identifier
     * @return instance of <code>IScreenLabelChartObject</code>
     */
    IScreenLabelChartObject createScreenLabel(String key);

	/**
     * Creates OHLC Informer chart object with default parameters.
     *
     * @return instance of <code>IOhlcChartObject</code>
     */
    IOhlcChartObject createOhlcInformer();

	/**
	 * Creates OHLC Informer chart object with specified String id.
	 *
	 * @param key String identifier
	 * @return instance of <code>IOhlcChartObject</code>
	 */
	IOhlcChartObject createOhlcInformer(String key);

	/**
     * Creates Gann Angles chart object with default parameters.
     *
     * @return instance of newly created <code>IGannAnglesChartObject</code>
     */
    IGannAnglesChartObject createGannAnglesChartObject();

	/**
	 * Creates Gann Angles chart object with specified String id.
	 *
	 * @param key String identifier
	 * @return instance of newly created <code>IGannAnglesChartObject</code>
	 */
	IGannAnglesChartObject createGannAnglesChartObject(String key);

	/**
     * Constructs <code>IGannAnglesChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @param price price value
     * @return instance of <code>IGannAnglesChartObject</code>
     */
    IGannAnglesChartObject createGannAnglesChartObject(String key, long time, double price);

	/**
     * Creates Gann Periods chart object with default parameters.
     *
     * @return instance of newly created <code>IGannGridChartObject</code>
     */
    IGannGridChartObject createGannGridChartObject();

    /**
     * Creates Gann Periods chart object with specified String id.
     *
     * @param key String identifier
     * @return instance of newly created <code>IGannGridChartObject</code>
     */
    IGannGridChartObject createGannGridChartObject(String key);

    /**
     * Constructs <code>IGannGridChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time value in ms
     * @param price price value
     * @return instance of <code>IGannGridChartObject</code>
     */
    IGannGridChartObject createGannGridChartObject(String key, long time, double price);

	/**
     * Creates <code>ICandlestickChartObject</code> with default parameters.
     *
     * @return instance of <code>ICandlestickChartObject</code>
     */
    ICandlestickChartObject createCandlestick();

	/**
	 * Creates <code>ICandlestickChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>ICandlestickChartObject</code>
	 */
	ICandlestickChartObject createCandlestick(String key);

	/**
     * Constructs <code>ICandlestickChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param time2 2nd point value in ms
     * @return instance of <code>ICandlestickChartObject</code>
     */
    ICandlestickChartObject createCandlestick(String key, long time1, long time2);

	/**
     * Creates <code>IVolumeProfileChartObject</code> with default parameters.
     *
     * @return instance of <code>IVolumeProfileChartObject</code>
     */
    IVolumeProfileChartObject createVolumeProfile();

	/**
	 * Creates <code>IVolumeProfileChartObject</code> with default parameters.
	 *
	 * @param key String identifier
	 * @return instance of <code>IVolumeProfileChartObject</code>
	 */
	IVolumeProfileChartObject createVolumeProfile(String key);

	/**
     * Constructs <code>IVolumeProfileChartObject</code> with mandatory parameters.
     *
     * @param key String identifier
     * @param time1 1st point value in ms
     * @param time2 2nd point value in ms
     * @return instance of <code>IVolumeProfileChartObject</code>
     */
    IVolumeProfileChartObject createVolumeProfile(String key, long time1, long time2);

	/**
     * Creates chart widget object with default parameters.
     *
     * @return instance of <code>ICustomWidgetChartObject</code>
     */
    ICustomWidgetChartObject createChartWidget();

	/**
     * Creates chart widget object with default parameters.
     *
     * @param key String identifier
     * @return instance of <code>ICustomWidgetChartObject</code>
     */
    ICustomWidgetChartObject createChartWidget(String key);

	/**
     * Factory method for creating drawing shapes of given type.
     *
     * @param <T> type of created chart object
     * @param objectClass <code>Class</code> of object to create
     * @param key String identifier
     * @exception IllegalArgumentException if object gets instantiated with <code>null</code> values
     * @return instance of <code>IChartObject</code> subtype
     */
    <T extends IChartObject> T create(Class<T> objectClass, String key);

	/**
     * Factory method for creating drawing shapes of given type.
     *
     * @param <T> type of created chart object
     * @param objectClass <code>Class</code> of object to create
     * @exception IllegalArgumentException if object gets instantiated with <code>null</code> values
     * @return instance of <code>IChartObject</code> subtype
     */
    <T extends IChartObject> T create(Class<T> objectClass);

	/**
	 * @deprecated
	 * Factory method for creating drawing shapes of given type. Optionally,
	 * accepts additional constructor parameters as <code>vararg</code> argument.
	 *
     * @param <T> type of created chart object
	 * @param objectClass <code>Class</code> of object to create
	 * @param args <code>vararg</code> constructor parameters. When specified,
	 *            <code>create()</code> method will try to find suitable
	 *            constructor and invoke it with <code>params</code> given.
	 *            Please note that currently <code>Object</code> classes that
	 *            belong to Java core libraries are converted to it's primitives
	 *            (i.e. <b>Integer.class -&gt; Integer.TYPE</b>). This is done due
	 *            to API compatibility issues.
	 * @exception IllegalArgumentException if object gets instantiated with <code>null</code> values
	 * @return instance of <code>IChartObject</code> subtype
	 */
    @Deprecated
	<T extends IChartObject> T create(Class<T> objectClass, Object... args);

	@Deprecated
	IFinancialInstrument getFinancialInstrument();

	@Deprecated
	void setFinancialInstrument(IFinancialInstrument financialInstrument);

}
