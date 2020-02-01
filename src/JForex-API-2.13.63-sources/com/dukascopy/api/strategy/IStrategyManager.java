package com.dukascopy.api.strategy;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;

import com.dukascopy.api.JFException;

/**
 * Tailored-for-SDK remote strategy functionality
 */
public interface IStrategyManager<DESCRIPTOR extends IStrategyDescriptor, LISTENER extends StrategyListener<DESCRIPTOR>> {

    /**
     * Sets the listener for remote strategies.
     * 
     * <pre>
     * //add a listener to a remote strategy manager, for local strategy manager use LocalStrategyListener
     * strategyManager.addStrategyListener(new RemoteStrategyListener() {
     *     {@literal @}Override
     *     public void onStrategyRun(IRemoteStrategyDescriptor descriptor) {
     *         System.out.println("strategy launched: " + descriptor);
     *     };
     *
     *     {@literal @}Override
     *     public void onStrategyStop(IRemoteStrategyDescriptor descriptor) {
     *         System.out.println("strategy stopped: " + descriptor);
     *     };
     * });
     * </pre>
     *
     * @param strategyListener listener
     */
    void addStrategyListener(LISTENER strategyListener);
    
    /**
     * Removes the listener if it exists
     * 
     * @param strategyListener the removable listener
     */
    void removeStrategyListener(LISTENER strategyListener);

    /**
     * Stops the remote strategy of the specified id.
     *
     * <pre>
     * // stop without processing the server response
     * strategyManager.stopStrategy(myStrategyId);
     *
     * // stop with processing the server response
     * IStrategyResponse{@literal <Void>} stopResponse = strategyManager.stopStrategy(myStrategyId).get();
     * if (stopResponse.isError()) {
     *     System.err.println("Strategy failed to stop: " + startResponse.getErrorMessage());
     * } else {
     *     myStrategyId = startResponse.getResult();
     *     System.out.println("Strategy successfully stopped: " + myStrategyId);
     * }
     * </pre>
     *
     * @param processId id of the strategy
     * @return returns {@link Future} object for obtaining the server response
     */
    Future<IStrategyResponse<Void>> stopStrategy(UUID processId);

    /**
     * Returns a set of started strategies.
     * 
     * <pre>
     * //Retrieve remote strategies from a remote strategy manager, to retrieve local strategies use ILocalStrategyManager
     * IStrategyResponse{@literal <Set<IRemoteStrategyDescriptor>>} listResponse = strategyManager.getStartedStrategies().get();
     * if (listResponse.isError()) {
     *     System.err.println("Failed to retrieve remote strategy list");
     * } else {
     *     Set{@literal <IRemoteStrategyDescriptor>} strategyDescriptors = listResponse.getResult();
     *     System.out.println("Remotely started " + strategyDescriptors.size() + " strategies: ");
     *     for (IStrategyDescriptor strategyDescriptor : strategyDescriptors) {
     *         System.out.println(strategyDescriptor.toString());
     *     }
     * }
     * </pre>
     * 
     * @return returns {@link Future} object for obtaining the server response
     */    
    Future<IStrategyResponse<Set<DESCRIPTOR>>> getStartedStrategies();
}