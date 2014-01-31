package de.hotware.hibernate.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
/**
 * a method annotated with this will
 * be considered as a value to be searched
 * for when used with Searcher.search(...)
 * 
 * This should be used when multiple SearchFields are needed
 * 
 * @author Martin Braun
 */
public @interface SearchFields {

	/**
	 * the searchFields array
	 */
	SearchField[] searchFields();

	/**
	 * how these SearchFields will be combined with the main query
	 */
	Junction topLevel() default Junction.MUST;

}
