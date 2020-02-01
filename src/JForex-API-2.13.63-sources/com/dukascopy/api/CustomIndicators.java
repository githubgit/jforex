/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.lang.annotation.*;

/**
 * Pass indicator paths separated by File.pathSeparator for custom indicators
 * that need to be packaged into the compiled strategy .jfx file.
 * Pass only file name for the indicators that are placed in IContext.getFilesDir.
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomIndicators {
    /**
     * Full pathname of each custom indicator
     * 
     * @return library
     */
    String value();
}
