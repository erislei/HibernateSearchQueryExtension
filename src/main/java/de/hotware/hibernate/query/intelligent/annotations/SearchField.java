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
