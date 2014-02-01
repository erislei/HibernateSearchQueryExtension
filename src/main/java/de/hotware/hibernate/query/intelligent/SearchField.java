package de.hotware.hibernate.query.intelligent;

import de.hotware.hibernate.query.QueryType;
import de.hotware.hibernate.query.Junction;
import de.hotware.hibernate.query.StockQueryTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.search.annotations.Analyzer;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SearchField {
	
	String fieldName() default "";
	
	String propertyName() default "";
	
	Class<? extends QueryType> queryType() default StockQueryTypes.Term.class;
	
	Class<?> stringBridge() default void.class;
	
	Junction betweenValues() default Junction.MUST;
	
	Analyzer queryAnalyzer() default @Analyzer();

}
