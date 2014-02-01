/*  
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.*
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   (C) Martin Braun 2014
 */
package de.hotware.hibernate.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.search.annotations.Analyzer;

import de.hotware.hibernate.query.intelligent.annotations.Junction;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
/**
 * a method annotated with this will
 * be considered as a value to be searched
 * for when used with Searcher.search(...)
 * 
 * @author Martin Braun
 */
public @interface SearchField {

	public static final float DEFAULT_BOOST = Float.MIN_VALUE;
	public static final String DEFAULT_PROFILE = "DEFAULT_PROFILE";
	
	String profile() default "DEFAULT_PROFILE";

	/**
	 * the names of the fields to query from
	 */
	String[] fieldNames();

	/**
	 * will only be applied if the chosen queryType requires a needs Strings as
	 * values (i.e. StockQueryTypes.Phrase or StockQueryTypes.WildCardStarEnd)
	 */
	Class<?> stringBridge() default void.class;

	/**
	 * will only be applied if the chosen queryType requires a needs Strings as
	 * values (i.e. StockQueryTypes.Phrase or StockQueryTypes.WildCardStarEnd)
	 */
	Analyzer queryAnalyzer() default @Analyzer;

	/**
	 * how this SearchField will be combined with its parent query
	 */
	Junction topLevel() default Junction.MUST;

	/**
	 * how this SearchField will combine multiple values in the query (if there
	 * are any being returned from the queryBean or the Analyzer splits the
	 * original value into more)
	 */
	Junction betweenValues() default Junction.MUST;

	/**
	 * how this SeachField will combine multiple fields in the query
	 */
	Junction betweenFields() default Junction.MUST;

	/**
	 * the boost to apply
	 */
	float boost() default DEFAULT_BOOST;

	/**
	 * additional parameters to be passed to the QueryTypes handling function
	 */
	Parameter[] queryParameters() default {};

	/**
	 * the queryType to use
	 */
	Class<? extends QueryType> queryType() default StockQueryTypes.Term.class;

}
