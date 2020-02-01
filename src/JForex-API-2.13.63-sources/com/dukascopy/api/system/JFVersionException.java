/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import com.dukascopy.api.JFException;

/**
 * @author Dmitry Shohov
 */
public class JFVersionException extends JFException {
    public JFVersionException(Error errorCode) {
        super(errorCode);
    }

    public JFVersionException(Error errorCode, String message) {
        super(errorCode, message);
    }

    public JFVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JFVersionException(String message) {
        super(message);
    }

    public JFVersionException(Throwable cause) {
        super(cause);
    }
}
