package com.dukascopy.api;

import java.util.Map;

public interface IDownloadableStrategies {

    IDownloadableStrategy init (String id, String name, IContext context, IDownloadableStrategy.ComponentType type, IEngine.StrategyMode mode, Map<String, Object> configurables) throws JFException;
}
