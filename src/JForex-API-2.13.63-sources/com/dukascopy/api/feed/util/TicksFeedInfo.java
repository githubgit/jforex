package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Period;
import com.dukascopy.api.feed.FeedDescriptor;
import com.dukascopy.api.feed.FeedInfo;
import com.dukascopy.api.feed.ITailoredFeedInfo;
import com.dukascopy.api.instrument.IFinancialInstrument;

@Deprecated
public class TicksFeedInfo extends FeedInfo implements ITailoredFeedInfo<ITick> {

    /**
     * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
     *
     * @param financialInstrument instrument
     */
    public TicksFeedInfo(IFinancialInstrument financialInstrument) {
        setDataType(DataType.TICKS);
        setPeriod(Period.TICK);
        setFinancialInstrument(financialInstrument);
    }
}
