package com.dukascopy.api.feed.util;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public class RangeBarFeedInfo extends FeedInfo implements ITailoredFeedInfo<IRangeBar> {

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
     *
     * @param financialInstrument instrument
     * @param priceRange price range
     * @param offerSide offer side
     */
    public RangeBarFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange priceRange,
            OfferSide offerSide
    ) {
        this(financialInstrument, priceRange, offerSide, null);
    }

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation.
     * The period represents the base period.
     *
     * @param financialInstrument instrument
     * @param priceRange price range
     * @param offerSide offer side
     * @param basePeriod the period, in which the range bars are calculated. If base period is given {@link Period#INFINITY}, then range bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     */
    public RangeBarFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange priceRange,
            OfferSide offerSide,
            Period basePeriod
    ) {
        this(financialInstrument, priceRange, offerSide, basePeriod, null);
    }

    /**
     * Constructor, that sets all required fields.
     *
     * @param financialInstrument instrument
     * @param priceRange price range
     * @param offerSide offer side
     * @param basePeriod the period, in which the range bars are calculated. If base period is given {@link Period#INFINITY}, then range bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @param interpolationDescriptor - tick interpolation descriptor. If null, the DataInterpolationDescriptor.DEFAULT interpolation is used.
     * To get most suitable interpolation descriptor, use {@link DataInterpolationDescriptor#getSuitableDataInterpolationDescriptor(PriceRange priceRange)} method.
     * To find more about tick interpolation from candles and it's purpose, see {@link DataInterpolationDescriptor}.
     */
    public RangeBarFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange priceRange,
            OfferSide offerSide,
            Period basePeriod,
            DataInterpolationDescriptor interpolationDescriptor
    ) {
        setDataType(DataType.PRICE_RANGE_AGGREGATION);
        setFinancialInstrument(financialInstrument);
        setPriceRange(priceRange);
        setOfferSide(offerSide);
        super.setPeriod(validateBasePeriod(basePeriod));
        setDataInterpolationDescriptor(interpolationDescriptor);
    }

    @Override
    public void setPeriod(Period period) {
        super.setPeriod(validateBasePeriod(period));
    }
}
