package com.dukascopy.api.strategy;

/**
 * Class for listening to strategy start/stop events
 */
public abstract class StrategyListener<DESCRIPTOR extends IStrategyDescriptor> {

    /**
     * Called when a strategy has began executing
     *
     * @param strategyDescriptor descriptor of the strategy
     */
    public void onStrategyRun(DESCRIPTOR strategyDescriptor){}

    /**
     * Called on the strategy stop
     *
     * @param strategyDescriptor descriptor of the stopped strategy
     */
    public void onStrategyStop(DESCRIPTOR strategyDescriptor){}
}
