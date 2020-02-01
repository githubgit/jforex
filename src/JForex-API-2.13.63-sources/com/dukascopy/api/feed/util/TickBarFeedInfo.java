package com.dukascopy.api.feed.util;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public class TickBarFeedInfo extends FeedInfo implements ITailoredFeedInfo<ITickBar> {

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation.
     * Base period is set to default (one week).
     *
     * @param financialInstrument instrument
     * @param tickBarSize tick bar size
     * @param offerSide offer side
     */
    public TickBarFeedInfo(
            IFinancialInstrument financialInstrument,
            TickBarSize tickBarSize,
            OfferSide offerSide
    ) {
        this(financialInstrument, tickBarSize, offerSide, null);
    }

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation.
     * The period represents the base period.
     *
     * @param financialInstrument instrument
     * @param tickBarSize tick bar size
     * @param offerSide offer side
     * @param basePeriod the period, in which the tick bars are calculated. If base period is given {@link Period#INFINITY}, then tick bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     */
    public TickBarFeedInfo(
            IFinancialInstrument financialInstrument,
            TickBarSize tickBarSize,
            OfferSide offerSide,
            Period basePeriod
    ) {
        setDataType(DataType.TICK_BAR);
        setFinancialInstrument(financialInstrument);
        setTickBarSize(tickBarSize);
        setOfferSide(offerSide);
        super.setPeriod(validateBasePeriod(basePeriod));
        setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
    }

    @Override
    public void setPeriod(Period period) {
        super.setPeriod(validateBasePeriod(period));
    }

    @Override
    public void setDataInterpolationDescriptor(DataInterpolationDescriptor interpolationDescriptor){
        super.setDataInterpolationDescriptor(DataInterpolationDescriptor.ALL_TICKS);
    }

}
