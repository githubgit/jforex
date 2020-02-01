package com.dukascopy.api.feed.util;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public class KagiFeedInfo extends FeedInfo implements ITailoredFeedInfo<IKagi> {

    /**
     * Constructor, that uses minimal set of parameters.
     * Constructor for Kagi data with static turnaround price in pips (<i>turnaroundAmount</i>), which uses default values for:
     * <ul>
     * <li> base period - {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}
     * <li> creation point - {@link CreationPoint#CLOSE}
     * <li> time session - {@link Period#TICK}
     * <li> turnaround amount - {@link PriceRange#ONE_PIP}
     * </ul>
     *
     * @param financialInstrument instrument
     * @param offerSide offer side
     */
    public KagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide
    ){
        this(financialInstrument, offerSide, PriceRange.ONE_PIP);
    }

    /**
     * Constructor for Kagi data with static turnaround price in pips (<i>turnaroundAmount</i>), which uses default values for:
     * <ul>
     * <li> base period - {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}
     * <li> creation point - {@link CreationPoint#CLOSE}
     * <li> time session - {@link Period#TICK}
     * </ul>
     *
     * @param financialInstrument instrument
     * @param offerSide offer side
     * @param turnaroundAmount amount of price change in pips that is required to create a new Kagi line with opposite trend.
     */
    public KagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount
    ){
        this(financialInstrument, offerSide, turnaroundAmount, Period.TICK);
    }

    /**
     * Constructor for Kagi data with static turnaround price in pips (<i>turnaroundAmount</i>), which uses default values for:
     * <ul>
     * <li> base period - {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}
     * <li> creation point - {@link CreationPoint#CLOSE}
     * </ul>
     *
     * @param financialInstrument instrument
     * @param offerSide offer side
     * @param turnaroundAmount amount of price change in pips that is required to create a new Kagi line with opposite trend.
     * @param timeSession indicates how often we take a price for Kagi construction. Can be any valid {@link Period}. If null, then default {@link Period#TICK} is used.
     */
    public KagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount,
            Period timeSession
    ){
        this(financialInstrument, offerSide, turnaroundAmount, timeSession, CreationPoint.CLOSE);
    }

    /**
     * Constructor for Kagi data with static turnaround price in pips (<i>turnaroundAmount</i>), which uses default values for:
     * <ul>
     * <li> base period - {@link IFeedDescriptor#DEFAULT_BASE_PERIOD}
     * </ul>
     *
     * @param financialInstrument instrument
     * @param offerSide offer side
     * @param turnaroundAmount amount of price change in pips that is required to create a new Kagi line with opposite trend.
     * @param timeSession indicates how often we take a price for Kagi construction. Can be any valid {@link Period}. If null, then default {@link Period#TICK} is used.
     * @param creationPoint price point of the time session. If null, then default {@link CreationPoint#CLOSE} is used. If time session is {@link Period#TICK}, then this parameter has no effect.
     */
    public KagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount,
            Period timeSession,
            CreationPoint creationPoint
    ){
        this(financialInstrument, offerSide, turnaroundAmount, timeSession, creationPoint, DEFAULT_BASE_PERIOD);
    }


    /**
     * Constructor, that sets all the required fields. This feed descriptor will use a static turnaround amount ({@link PriceRange}).
     * @param financialInstrument instrument
     * @param offerSide offer side
     * @param turnaroundAmount amount of price change in pips that is required to create a new Kagi line with opposite trend.
     * @param timeSession indicates how often we take a price for Kagi construction. Can be any valid {@link Period}. If null, then default {@link Period#TICK} is used.
     * @param creationPoint price point of the time session. If null, then default {@link CreationPoint#CLOSE} is used. If time session is {@link Period#TICK}, then this parameter has no effect.
     * @param basePeriod the period, in which lines are calculated. If base period is given {@link Period#INFINITY}, then new lines are calculated from the beginning of the history.
     */
    public KagiFeedInfo(
            IFinancialInstrument financialInstrument,
            OfferSide offerSide,
            PriceRange turnaroundAmount,
            Period timeSession,
            CreationPoint creationPoint,
            Period basePeriod
    ){
        setDataType(DataType.KAGI);
        setFinancialInstrument(financialInstrument);
        setOfferSide(offerSide);
        super.setTimeSession(validateTimeSession(timeSession));
        super.setCreationPoint(validateCreationPoint(creationPoint));
        super.setPriceRange(validateTurnaroundAmount(turnaroundAmount));
        super.setPeriod(validateBasePeriod(basePeriod));
        setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
    }


    @Override
    public void setPeriod(Period period) {
        super.setPeriod(validateBasePeriod(period));
    }

    @Override
    public void setTimeSession(Period timeSession) {
        super.setTimeSession(validateTimeSession(timeSession));
    }

    @Override
    public void setCreationPoint(CreationPoint creationPoint) {
        super.setCreationPoint(validateCreationPoint(creationPoint));
    }

    @Override
    public void setPriceRange(PriceRange turnaroundAmount){
        super.setPriceRange(validateTurnaroundAmount(turnaroundAmount));
    }
}
