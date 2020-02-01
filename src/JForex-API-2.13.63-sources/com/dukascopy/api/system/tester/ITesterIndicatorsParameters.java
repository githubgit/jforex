package com.dukascopy.api.system.tester;

/**
 * Contains methods to control tester indicators
 * 
 * @see  ITesterVisualModeParameters
 */
public interface ITesterIndicatorsParameters {
    
    /**
     * @return true if Equity indicator should be enabled, false otherwise
     */
    boolean isEquityIndicatorEnabled();
    
    /**
     * @return true if ProfitLoss indicator should be enabled, false otherwise
     */
    boolean isProfitLossIndicatorEnabled();
    
    /**
     * @return true if Balance indicator should be enabled, false otherwise
     */
    boolean isBalanceIndicatorEnabled();
}
