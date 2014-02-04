package de.hotware.hibernate.query.intelligent.annotations;

import org.hibernate.search.annotations.Analyzer;

import de.hotware.hibernate.query.intelligent.structure.StockQueryTypes;

public @interface QueryType {

	Class<? extends de.hotware.hibernate.query.intelligent.structure.QueryType> value() default StockQueryTypes.Term.class;

	/**
	 * will only be applied if the chosen queryType requires a needs Strings as
	 * values (i.e. StockQueryTypes.Phrase or StockQueryTypes.WildCardStarEnd)
	 */
	Class<?> stringBridge() default void.class;

	/**
	 * will only be applied if the chosen queryType requires a needs Strings as
	 * values (i.e. StockQueryTypes.Phrase or StockQueryTypes.WildCardStarEnd)
	 */
	Analyzer queryAnalyzer() default @Analyzer();
	
	Parameter[] parameters() default {};
	
	PropertyParameter[] propertyParameters() default {};

}
