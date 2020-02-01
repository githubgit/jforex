package com.dukascopy.api.feed.util;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.FeedInfo;
import com.dukascopy.api.feed.ITailoredFeedInfo;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public class TimePeriodAggregationFeedInfo extends FeedInfo implements ITailoredFeedInfo<IBar> {

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
     *
     * @param financialInstrument instrument
     * @param period period
     * @param offerSide offer side
     * @param filter filter
     */
    public TimePeriodAggregationFeedInfo(
            IFinancialInstrument financialInstrument,
            Period period,
            OfferSide offerSide,
            Filter filter
    ) {
        setDataType(DataType.TIME_PERIOD_AGGREGATION);
        setFinancialInstrument(financialInstrument);
        setPeriod(period);
        setOfferSide(offerSide);
        setFilter(filter);
    }

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
     *
     * @param financialInstrument instrument
     * @param period period
     * @param offerSide offer side
     */
    public TimePeriodAggregationFeedInfo(
            IFinancialInstrument financialInstrument,
            Period period,
            OfferSide offerSide
    ) {
        this(
                financialInstrument,
                period,
                offerSide,
                Filter.NO_FILTER
        );
    }

}
