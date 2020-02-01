package com.dukascopy.api.system.tester;

import java.util.Map;

import com.dukascopy.api.IStrategy;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.LoadingProgressListener;

/**
 * Defines tester visual mode parameters
 * 
 * @see  com.dukascopy.api.system.ITesterClient#startStrategy(IStrategy, LoadingProgressListener, ITesterVisualModeParameters, ITesterExecution, ITesterUserInterface)
 */
public interface ITesterVisualModeParameters {
    
    /**
     * @return map of tester indicator parameters and their corresponding instruments.<br><br>
     * Returned map will be used to determine if tester indicators should be enabled on a chart that corresponds to a given instrument.<br>
     * The map must contain instruments and the corresponding indicator parameter.<br>
     * The Instruments contained in this map should correspond to instruments passed in {@link
     * com.dukascopy.api.system.IClient#setSubscribedInstruments(java.util.Set) }
     */
    Map<Instrument, ITesterIndicatorsParameters> getTesterIndicatorsParameters();
}
