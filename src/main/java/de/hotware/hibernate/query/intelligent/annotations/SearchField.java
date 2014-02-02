package de.hotware.hibernate.query.intelligent.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.search.annotations.Analyzer;

import de.hotware.hibernate.query.intelligent.structure.QueryType;
import de.hotware.hibernate.query.intelligent.structure.StockQueryTypes;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SearchField {

	String fieldName() default "";

	String propertyName() default "";

	Class<? extends QueryType> queryType() default StockQueryTypes.Term.class;

	/**
	 * will only be applied if the chosen queryType requires a needs Strings as
	 * values (i.e. StockQueryTypes.Phrase or StockQueryTypes.WildCardStarEnd)
	 */
	Class<?> stringBridge() default void.class;

	Junction betweenValues() default Junction.MUST;

	/**
	 * will only be applied if the chosen queryType requires a needs Strings as
	 * values (i.e. StockQueryTypes.Phrase or StockQueryTypes.WildCardStarEnd)
	 */
	Analyzer queryAnalyzer() default @Analyzer();

	int boost() default Integer.MIN_VALUE;

}
