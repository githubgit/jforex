package com.dukascopy.api.feed.util;

import com.dukascopy.api.*;
import com.dukascopy.api.feed.*;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public class PointAndFigureFeedInfo extends FeedInfo implements ITailoredFeedInfo<IPointAndFigure> {

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
     * Base period is set to default (one week).
     *
	 * @param financialInstrument instrument
	 * @param boxSize box size
	 * @param reversalAmount reversal amount
	 * @param offerSide offer side
     */
	public PointAndFigureFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange boxSize,
            ReversalAmount reversalAmount,
            OfferSide offerSide
    ) {
        this(financialInstrument, boxSize, reversalAmount, offerSide, null);
    }

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation.
     * The period represents the base period.
     *
	 * @param financialInstrument instrument
	 * @param boxSize box size
	 * @param reversalAmount reversal amount
	 * @param offerSide offer side
     * @param basePeriod the period, in which the point and figure bars are calculated. If base period is given {@link Period#INFINITY}, then point and figure bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     */
	public PointAndFigureFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange boxSize,
            ReversalAmount reversalAmount,
            OfferSide offerSide,
            Period basePeriod
    ) {
        this(financialInstrument, boxSize, reversalAmount, offerSide, basePeriod, null);
    }

    /**
     * Constructor, that sets all required fields.
     *
	 * @param financialInstrument instrument
	 * @param boxSize box size
	 * @param reversalAmount reversal amount
	 * @param offerSide offer side
     * @param basePeriod the period, in which the point and figure bars are calculated. If base period is given {@link Period#INFINITY}, then point and figure bars are calculated from the beginning of the history.
     * Everything else at data loading time is considered as a default value (one week), including null.
     * @param interpolationDescriptor tick interpolation descriptor. If null, the DataInterpolationDescriptor.DEFAULT interpolation is used.
     * To get most suitable interpolation descriptor, use {@link DataInterpolationDescriptor#getSuitableDataInterpolationDescriptor(PriceRange priceRange, ReversalAmount reversalAmount)} method.
     * To find more about tick interpolation from candles and it's purpose, see {@link DataInterpolationDescriptor}.
     */
	public PointAndFigureFeedInfo(
            IFinancialInstrument financialInstrument,
            PriceRange boxSize,
            ReversalAmount reversalAmount,
            OfferSide offerSide,
            Period basePeriod,
            DataInterpolationDescriptor interpolationDescriptor
    ) {
        setDataType(DataType.POINT_AND_FIGURE);
        setFinancialInstrument(financialInstrument);
        setPriceRange(boxSize);
        setReversalAmount(reversalAmount);
        setOfferSide(offerSide);
        super.setPeriod(validateBasePeriod(basePeriod));
        setDataInterpolationDescriptor(interpolationDescriptor);
    }

    @Override
    public void setPeriod(Period period) {
        super.setPeriod(validateBasePeriod(period));
    }

}
