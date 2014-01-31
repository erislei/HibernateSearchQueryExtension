package de.hotware.hibernate.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter annotation
 * 
 * @author Martin Braun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Parameter {
	
	/**
	 * the name of the parameter
	 */
	String name();
	
	/**
	 * the value of the parameter
	 */
	String value();

}
