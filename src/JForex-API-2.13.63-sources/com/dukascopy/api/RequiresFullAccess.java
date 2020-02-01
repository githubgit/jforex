/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When strategy loader sees this annotation for strategy class it asks user if he wants to allow the strategy full access.
 * If such acceptance receives strategy classes, they are loaded with full access policy allowing to read/write any files, use sockets etc.
 * Always try to avoid  using this annotation, if only need to read/write to file - you have full access to 
 * "~/My Documents/My Strategies/files" folder. In this folder you can read or write any file without requesting the full access.
 * 
 * @author Dmitry Shohov
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequiresFullAccess {

}
