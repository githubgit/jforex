package com.dukascopy.api.strategy.remote;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Future;

import com.dukascopy.api.JFException;
import com.dukascopy.api.strategy.IStrategyManager;
import com.dukascopy.api.strategy.IStrategyResponse;

public interface IRemoteStrategyManager extends IStrategyManager<IRemoteStrategyDescriptor, RemoteStrategyListener>{
	
    
    /**
     * Starts a strategy with default parameters. 
     * 
     * <pre>
     * // start a strategy without fetching the id
     * strategyManager.startStrategy(jfxFile);
     *
     * // start and fetch the id
     * IStrategyResponse{@literal <UUID>} startResponse = strategyManager.startStrategy(jfxFile).get();
     * if (startResponse.isError()) {
     *     System.err.println("Strategy failed to start: " + startResponse.getErrorMessage());
     * } else {
     *     myStrategyId = startResponse.getResult();
     *     System.out.println("Strategy successfully started: " + myStrategyId);
     * }
     * </pre>
     * 
     * @param strategyFile strategy jfx file to run
     * @param stopOnError stop strategy on error
     * @return returns {@link Future} object for obtaining the server response
     * @throws IOException in case of strategyFile read/load error
     */
    Future<IStrategyResponse<UUID>> startStrategy(File strategyFile, boolean stopOnError) throws IOException;

    /**
     * Starts a strategy with parameters. Note that the parameter array size should match the 
     * strategy public <code>Configurable</code> field count, likewise the value types should match.
     * 
     * <pre>
     * // start a strategy without fetching the id
     * strategyManager.startStrategy(jfxFile, new Object[]{"param value", 1, "another param value"});
     *
     * // start and fetch the id
     * IStrategyResponse{@literal <UUID>} startResponse = strategyManager.startStrategy(jfxFile, new Object[]{"param value", 1, "another param value"}).get();
     * if (startResponse.isError()) {
     *     System.err.println("Strategy failed to start: " + startResponse.getErrorMessage());
     * } else {
     *     myStrategyId = startResponse.getResult();
     *     System.out.println("Strategy successfully started: " + myStrategyId);
     * }
     * </pre>
     * 
     * @param strategyFile strategy jfx file to run
     * @param params strategy parameters
     * @param stopOnError stop strategy on error
     * @return returns {@link Future} object for obtaining the server response
     * @throws IOException in case of strategyFile read/load error
     * @throws JFException in case of parameter error, i.e., incorrect array size or wrong value type.
     */
    Future<IStrategyResponse<UUID>> startStrategy(File strategyFile, Object[] params, boolean stopOnError) throws IOException, JFException;
}