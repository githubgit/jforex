package com.dukascopy.api.feed;

import com.dukascopy.api.ITick;
import com.dukascopy.api.instrument.IFinancialInstrument;

/**
 * @author Kaspars Rinkevics
 * @deprecated use {@link ITickFeedListener}
 */
@Deprecated
public interface IFinancialTickFeedListener {

	/**
	 * The method is being called when next Tick arrives
	 *
	 * @param financialInstrument instrument
	 * @param tick tick
	 */
	void onTick(IFinancialInstrument financialInstrument, ITick tick);

}
