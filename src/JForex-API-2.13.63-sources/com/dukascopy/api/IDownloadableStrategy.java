package com.dukascopy.api;

import java.util.List;

public interface IDownloadableStrategy {
    
    enum ComponentType {
        BLOCK_COMPONENT(0),
        BLOCK_INDICATOR(1),
        BLOCK_STRATEGY(2),
        BLOCK_OWN_STRATEGY(3);
        
        private int code;
        
        public static ComponentType valueOf(int code) {
            for (ComponentType type : ComponentType.values()) {
                if (type.code == code) {
                    return type;
                }
            }
            
            return null;
        }        
        
        private ComponentType(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
    }
    
    String getName();
    
    String getId();
    
    ComponentType getComponentType();

    IEngine.StrategyMode getStrategyMode();

    void onStop() throws JFException;

    void onAccount(IAccount account) throws JFException;

    List<ISignal> onMessage(IMessage message) throws JFException;

    List<ISignal> onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException;

    List<ISignal> onTick(Instrument instrument, ITick tick) throws JFException;

    void start() throws JFException;
}
