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

import de.hotware.hibernate.query.intelligent.structure.StockQueryTypes;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SearchField {

	String fieldName() default "";

	String propertyName() default "";
	
	QueryType queryType() default @QueryType(StockQueryTypes.Term.class);

	Junction betweenValues() default Junction.MUST;

	int boost() default Integer.MIN_VALUE;

}
