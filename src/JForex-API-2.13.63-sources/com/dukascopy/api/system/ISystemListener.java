/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

/**
 * Listener to the system events
 *
 * @author Denis Larka
 */
public interface ISystemListener {
    /**
     * Called on new strategy start
     *
     * @param processId id of the started strategy
     */
    void onStart(long processId);

    /**
     * Called on the strategy stop
     *
     * @param processId id of the strategy stopped
     */
    void onStop(long processId);

    /**
     * Called on successful connect
     */
    void onConnect();

    /**
     * Called on disconnect
     */
    void onDisconnect();
}