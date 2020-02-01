/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.io.PrintStream;

/**
 * Allows to print messages to Messages table
 * 
 * @author Denis Larka
 */
public interface IConsole {
    /**
     * Returns {@link PrintStream} that prints messages with normal priority
     * 
     * @return {@link PrintStream} to print messages
     */
    PrintStream getOut();

    /**
     * Returns {@link PrintStream} that prints messages with error priority. Messages are shown in red color
     * 
     * @return {@link PrintStream} to print messages
     */
    PrintStream getErr();
    
    /**
     * Returns {@link PrintStream} that prints messages with warning priority. Messages by default are shown in yellow color
     * 
     * @return {@link PrintStream} to print messages
     */
    PrintStream getWarn();
    
    /**
     * Returns {@link PrintStream} that prints messages with info priority. Messages by default are shown in green color
     * 
     * @return {@link PrintStream} to print messages
     */
    PrintStream getInfo();

    /**
     * Returns {@link PrintStream} that prints messages with notification priority. Messages by default are shown in blue color
     * 
     * @return {@link PrintStream} to print messages
     */
    PrintStream getNotif();
    
    
    /**
     * Sets the maximum number of messages. Unrestricted by default.
     * @param maxNumber maximum number of messages in Messages table. Passing <b>-1</b> makes it unrestricted.
     */
    void setMaxMessages(int maxNumber);
}
