package com.dukascopy.api.feed;

import com.dukascopy.api.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public interface IFeedInfoProvider {

     IFeedInfo createFeed(IFeedInfo feedInfo);

     IFeedInfo createFeed(DataType dataType, IFinancialInstrument financialInstrument, Period period, OfferSide offerSide, PriceRange priceRange, ReversalAmount reversalAmount, TickBarSize tickBarSize, Filter filter);

    /**
     * Create Time Period feed info, default values are taken from:
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period}
     * @return {@link IFeedInfo}
     */
    IFeedInfo createTimePeriodFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            Period timeSession);

    /**
     * Create Time Period feed info, default values are taken from:
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period}
     * @param filter {@link com.dukascopy.api.Filter}
     * @return {@link IFeedInfo}
     */
    IFeedInfo createTimePeriodFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            Period timeSession,
            Filter filter);

    /**
     * Create Line Break feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @return {@link IFeedInfo}
     */
    IFeedInfo createLineBreakFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide);

    /**
     * Create Line Break feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period} indicates how often we take a price. Can be any valid {@link com.dukascopy.api.Period} smaller or equal than {@link com.dukascopy.api.Period#DAILY}. If null, then default {@link com.dukascopy.api.Period#TICK} is used.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createLineBreakFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            Period timeSession);

    /**
     * Create Line Break feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period} indicates how often we take a price. Can be any valid {@link com.dukascopy.api.Period} smaller or equal than {@link com.dukascopy.api.Period#DAILY}. If null, then default {@link com.dukascopy.api.Period#TICK} is used.
     * @param creationPoint {@link com.dukascopy.api.feed.CreationPoint} price point of the time session. If null, then default {@link com.dukascopy.api.feed.CreationPoint#CLOSE} is used.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createLineBreakFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            Period timeSession,
            CreationPoint creationPoint);

    /**
     * Create Line Break feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period} indicates how often we take a price. Can be any valid {@link com.dukascopy.api.Period} smaller or equal than {@link com.dukascopy.api.Period#DAILY}. If null, then default {@link com.dukascopy.api.Period#TICK} is used.
     * @param creationPoint {@link com.dukascopy.api.feed.CreationPoint} price point of the time session. If null, then default {@link com.dukascopy.api.feed.CreationPoint#CLOSE} is used.
     * @param lookbackLines {@link com.dukascopy.api.LineBreakLookback} number of lines which will define turnaround price. If null, then default {@link com.dukascopy.api.LineBreakLookback#THREE_LINES} is used.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createLineBreakFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            Period timeSession,
            CreationPoint creationPoint,
            LineBreakLookback lookbackLines);

    /**
     * Create Line Break feed info
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period} indicates how often we take a price. Can be any valid {@link com.dukascopy.api.Period} smaller or equal than {@link com.dukascopy.api.Period#DAILY}. If null, then default {@link com.dukascopy.api.Period#TICK} is used.
     * @param creationPoint {@link com.dukascopy.api.feed.CreationPoint} price poinnes which will define turnaround price. If null, then default {@link com.dukascopy.api.LineBreakLookback#THREE_LINES} is used.
     * @param lookbackLines {@link com.dukascopy.api.LineBreakLookback} number of lie calculated. If base period is given {@link com.dukascopy.api.Period#INFINITY}, then new lines are calculated from the beginning of the history.
     * @param basePeriod {@link com.dukascopy.api.Period} the period, in which lines art of the time session. If null, then default {@link com.dukascopy.api.feed.CreationPoint#CLOSE} is used.
     * Everything else at data loading time is considered as a default value ({@link com.dukascopy.api.feed.IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createLineBreakFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            Period timeSession,
            CreationPoint creationPoint,
            LineBreakLookback lookbackLines,
            Period basePeriod);

    /**
     * Create Kagi feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @return {@link IFeedInfo}
     */
    IFeedInfo createKagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide);

    /**
     * Create Kagi feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param turnaroundAmount {@link PriceRange} the amount which price must exceed to construct new line with opposite direction.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createKagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount);
    
    /**
     * Create Kagi feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param turnaroundAmount {@link PriceRange} the amount which price must exceed to construct new line with opposite direction.
     * @param timeSession {@link com.dukascopy.api.Period} indicates how often we take a price. Can be any valid {@link com.dukascopy.api.Period} smaller or equal than {@link com.dukascopy.api.Period#DAILY}. If null, then default {@link com.dukascopy.api.Period#TICK} is used.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createKagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount,
            Period timeSession);

    /**
     * Create Kagi feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param turnaroundAmount {@link PriceRange} the amount which price must exceed to construct new line with opposite direction.
     * @param timeSession {@link com.dukascopy.api.Period} indicates how often we take a price. Can be any valid {@link com.dukascopy.api.Period} smaller or equal than {@link com.dukascopy.api.Period#DAILY}. If null, then default {@link com.dukascopy.api.Period#TICK} is used.
     * @param creationPoint {@link com.dukascopy.api.feed.CreationPoint} price point of the time session. If null, then default {@link com.dukascopy.api.feed.CreationPoint#CLOSE} is used.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createKagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount,
            Period timeSession,
            CreationPoint creationPoint);

    /**
     * Create Kagi feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param turnaroundAmount {@link PriceRange} the amount which price must exceed to construct new line with opposite direction.
     * @param timeSession {@link com.dukascopy.api.Period} indicates how often we take a price. Can be any valid {@link com.dukascopy.api.Period} smaller or equal than {@link com.dukascopy.api.Period#DAILY}. If null, then default {@link com.dukascopy.api.Period#TICK} is used.
     * @param creationPoint {@link com.dukascopy.api.feed.CreationPoint} price point of the time session. If null, then default {@link com.dukascopy.api.feed.CreationPoint#CLOSE} is used.
     * @param basePeriod {@link com.dukascopy.api.Period} the period, in which lines art of the time session. If null, then default {@link com.dukascopy.api.feed.CreationPoint#CLOSE} is used.
     * Everything else at data loading time is considered as a default value ({@link com.dukascopy.api.feed.IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createKagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount,
            Period timeSession,
            CreationPoint creationPoint,
            Period basePeriod);

    /**
     * Create Point and Figure feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param boxSize {@link com.dukascopy.api.PriceRange}
     * @param reversalAmount {@link com.dukascopy.api.ReversalAmount}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @return {@link IFeedInfo}
     */
    IFeedInfo createPointAndFigureFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange boxSize,
            ReversalAmount reversalAmount,
            OfferSide offerSide);


    /**
     * Create Point and Figure feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param boxSize {@link com.dukascopy.api.PriceRange}
     * @param reversalAmount {@link com.dukascopy.api.ReversalAmount}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param basePeriod {@link com.dukascopy.api.Period} the period, in which the point and figure bars are calculated. If base period is given {@link Period#INFINITY}, then point and figure bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createPointAndFigureFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange boxSize,
            ReversalAmount reversalAmount,
            OfferSide offerSide,
            Period basePeriod);

    /**
     * Create Point and Figure feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param boxSize {@link com.dukascopy.api.PriceRange}
     * @param reversalAmount {@link com.dukascopy.api.ReversalAmount}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param basePeriod {@link com.dukascopy.api.Period} the period, in which the point and figure bars are calculated. If base period is given {@link Period#INFINITY}, then point and figure bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @param interpolationDescriptor {@link com.dukascopy.api.feed.DataInterpolationDescriptor } tick interpolation descriptor. If null, the DataInterpolationDescriptor.DEFAULT interpolation is used.
     * To get most suitable interpolation descriptor, use {@link DataInterpolationDescriptor#getSuitableDataInterpolationDescriptor(PriceRange priceRange, ReversalAmount reversalAmount)} method.
     * To find more about tick interpolation from candles and it's purpose, see {@link DataInterpolationDescriptor}.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createPointAndFigureFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange boxSize,
            ReversalAmount reversalAmount,
            OfferSide offerSide,
            Period basePeriod,
            DataInterpolationDescriptor interpolationDescriptor);

    /**
     * Create Range bar feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param priceRange {@link com.dukascopy.api.PriceRange}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createRangeBarFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange priceRange,
            OfferSide offerSide);

    /**
     * Create Range bar feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param priceRange {@link com.dukascopy.api.PriceRange}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param basePeriod {@link Period} the period, in which the range bars are calculated. If base period is given {@link Period#INFINITY}, then range bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createRangeBarFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange priceRange,
            OfferSide offerSide,
            Period basePeriod);

    /**
     * Create Range bar feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param priceRange {@link com.dukascopy.api.PriceRange}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param basePeriod {@link Period} the period, in which the range bars are calculated. If base period is given {@link Period#INFINITY}, then range bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @param interpolationDescriptor {@link com.dukascopy.api.feed.DataInterpolationDescriptor} tick interpolation descriptor. If null, the DataInterpolationDescriptor.DEFAULT interpolation is used.
     * To get most suitable interpolation descriptor, use {@link DataInterpolationDescriptor#getSuitableDataInterpolationDescriptor(PriceRange priceRange)} method.
     * To find more about tick interpolation from candles and it's purpose, see {@link DataInterpolationDescriptor}.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createRangeBarFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange priceRange,
            OfferSide offerSide,
            Period basePeriod,
            DataInterpolationDescriptor interpolationDescriptor);


    /**
     * Create Renko feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param brickSize {@link com.dukascopy.api.PriceRange}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @return {@link IFeedInfo}
     */
    IFeedInfo createRenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide
    );

    /**
     * Create Renko feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param brickSize {@link com.dukascopy.api.PriceRange}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period}
     * @return {@link IFeedInfo}
    */
    IFeedInfo createRenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide,
            Period timeSession
    );

    /**
     * Create Renko feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param brickSize {@link com.dukascopy.api.PriceRange}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period}
     * @param creationPoint {@link com.dukascopy.api.feed.CreationPoint}
     * Everything else at data loading time is considered as a default value ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createRenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide,
            Period timeSession,
            CreationPoint creationPoint
    );

    /**
     * Create Renko feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param brickSize {@link com.dukascopy.api.PriceRange}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param timeSession {@link com.dukascopy.api.Period}
     * @param creationPoint {@link com.dukascopy.api.feed.CreationPoint}
     * @param basePeriod {@link com.dukascopy.api.Period} the period, in which the renko bars are calculated. If base period is given {@link Period#INFINITY}, then renko bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createRenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide,
            Period timeSession,
            CreationPoint creationPoint,
            Period basePeriod
    );

    /**
     * Create Tick Bar info
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param tickBarSize {@link com.dukascopy.api.TickBarSize}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createTickBarInfo(
            IFinancialInstrument financialInstrument,
            TickBarSize tickBarSize,
            OfferSide offerSide);

    /**
     * Create Tick Bar info
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @param tickBarSize {@link com.dukascopy.api.TickBarSize}
     * @param offerSide {@link com.dukascopy.api.OfferSide}
     * @param basePeriod the period, in which the tick bars are calculated. If base period is given {@link Period#INFINITY}, then tick bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @return {@link IFeedInfo}
     */
    IFeedInfo createTickBarInfo(
            IFinancialInstrument financialInstrument,
            TickBarSize tickBarSize,
            OfferSide offerSide,
            Period basePeriod);

    /**
     * Create Time Period Aggregation feed info, default values are taken from:
     * <ul>
     * <li>base period, {@link com.dukascopy.api.feed.IFeedInfo#DEFAULT_BASE_PERIOD}.
     * </ul>
     *
     * @param financialInstrument {@link com.dukascopy.api.instrument.IFinancialInstrument}
     * @return {@link IFeedInfo}
     */
    IFeedInfo createTicksFeedInfo(
            IFinancialInstrument financialInstrument
    );

}
