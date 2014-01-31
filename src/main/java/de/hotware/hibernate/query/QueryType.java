/*  
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.*
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

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;

/**
 * Interface for wrapping the different types of Queries in Hibernate Search
 * 
 * @author Martin Braun
 */
public interface QueryType {

	/**
	 * @param queryBuilder
	 *            the queryBuilder to build the queries from
	 * @param searchField
	 *            the explicit searchField
	 * @param value
	 *            either a String or a general Object depending on
	 *            valueAsString()'s value
	 * @param pathToField
	 *            path to the field with a . at the end (just concatenated with
	 *            the fieldName)
	 */
	public Query query(QueryBuilder queryBuilder, String pathToField,
			SearchField searchField, Object value);

	/**
	 * @return true if the values should be passed as Strings into the query
	 *         method (if true, such a QueryType can only be used with a
	 *         StringBridge and should only be used with an Analyzer)
	 */
	public boolean valueAsString();

}