package com.dukascopy.api.plugins;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Properties for the plugin appearance in the platform.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface PluginProperties {

	/**
	 * Path to an icon inside the jar file. Icon should should be of type .png and of size 16*16.
	 * available with JForex-API 3.0
	 * @return path to an icon
	 */
    String iconFilePath() default "";
    
    /**
     * Plugin name as it will appear in the Workspace tree
     * available with JForex-API 3.0
     * @return plugin name
     */
    String name() default "";
	
}