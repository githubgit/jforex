/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import com.dukascopy.api.JFException;

/**
 * @author Dmitry Shohov
 */
public class JFAuthenticationException extends JFException {
    public JFAuthenticationException(Error errorCode) {
        super(errorCode);
    }

    public JFAuthenticationException(Error errorCode, String message) {
        super(errorCode, message);
    }

    public JFAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JFAuthenticationException(String message) {
        super(message);
    }

    public JFAuthenticationException(Throwable cause) {
        super(cause);
    }
}
