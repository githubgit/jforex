package com.dukascopy.api;

import java.lang.annotation.*;

/**
 * Identifies injectable IStrategy fields.
 * Appropriate strategy services will be injected into strategy fields that have this annotation.
 * Supported types include IEngine, IContext, IConsole, IAccount, IDataService, IHistory, IIndicators, IUserInterface, JFUtils.
 *
 * @author Kaspars Rinkevics
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JFXInject {
}
