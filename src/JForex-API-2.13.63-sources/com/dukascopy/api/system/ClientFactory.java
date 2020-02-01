/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

/**
 * Factory to create IClient instance
 *
 * @author Dmitry Shohov
 */
public class ClientFactory {
    private static IClient client;

    /**
     * Returns default instance of Dukascopy IClient. Instance is created only once, each call will return the same instance
     *
     * @return instance of IClient
     * @throws ClassNotFoundException if jar file with implementation was not found
     * @throws IllegalAccessException if there is some security problems
     * @throws InstantiationException if it's not possible to instantiate new instance of Dukascopy IClient
     */
    public static IClient getDefaultInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        synchronized (ClientFactory.class) {
            if (client == null) {
                Class dcClientImpl = Thread.currentThread().getContextClassLoader().loadClass("com.dukascopy.api.impl.connect.DCClientImpl");
                client = (IClient) dcClientImpl.newInstance();
            }
            return client;
        }
    }
}
