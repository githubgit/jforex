package com.dukascopy.api.feed.util;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public class RenkoFeedInfo extends FeedInfo implements ITailoredFeedInfo<IRenkoBar> {

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
     *  This constructor sets base period to default ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), renko session to Period.TICK and renko creation point to {@link RenkoCreationPoint#CLOSE}.
     *
     * @param financialInstrument instrument
     * @param brickSize brick size
     * @param offerSide offer side
     */
    public RenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide
    ) {
        this(financialInstrument, brickSize, offerSide, null);
    }

    /**
     * This constructor sets renko session to Period.TICK and renko creation point to {@link RenkoCreationPoint#CLOSE}.
     *
     * @param financialInstrument instrument
     * @param brickSize brick size
     * @param offerSide offer side
     * @param basePeriod the period, in which the renko bars are calculated. If base period is given {@link Period#INFINITY}, then renko bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (o{@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
     */
    public RenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide,
            Period basePeriod
    ) {
        this(financialInstrument, brickSize, offerSide, null, validateCreationPoint(null), basePeriod);
    }

    /**
     * This constructor sets base period to default ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD})
     *
     * @param financialInstrument instrument
     * @param brickSize brick size
     * @param offerSide offer side
     * @param timeSession time session
     * @param creationPoint creation point
     */
    public RenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide,
            Period timeSession,
            CreationPoint creationPoint
    ) {
        this(financialInstrument, brickSize, offerSide, timeSession, creationPoint, null);
    }

    /**
     * Constructor, that sets all required fields.
     *
     * @param financialInstrument instrument
     * @param brickSize brick size
     * @param offerSide offer side
     * @param timeSession time session
     * @param creationPoint creation point
     * @param basePeriod the period, in which the renko bars are calculated. If base period is given {@link Period#INFINITY}, then renko bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value ({@link IFeedDescriptor#DEFAULT_BASE_PERIOD}), including null.
     */
    public RenkoFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange brickSize,
            OfferSide offerSide,
            Period timeSession,
            CreationPoint creationPoint,
            Period basePeriod
    ) {
        setDataType(DataType.RENKO);
        setFinancialInstrument(financialInstrument);
        setPriceRange(brickSize);
        setOfferSide(offerSide);
        super.setTimeSession(validateTimeSession(timeSession));
        super.setCreationPoint(validateCreationPoint(creationPoint));
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

}
