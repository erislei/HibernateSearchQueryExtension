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

import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.Sort;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.facet.FacetingRequest;

/**
 * interface for beans used for passing search parameters to the search
 * engine
 * 
 * @author Martin Braun
 * 
 * @param <T>
 *            the type of the bean to return search requests
 */
public interface QueryBean<T> {

	/**
	 * @return a custom sort object or null
	 */
	public Sort getSort();

	/**
	 * @return the original BooleanJunction or a BooleanJunction that contains
	 *         the original one, <b>not null!</b>
	 */
	@SuppressWarnings("rawtypes")
	public BooleanJunction<BooleanJunction> customQuery(
			BooleanJunction<BooleanJunction> mainBooleanQuery,
			QueryBuilder queryBuilder);

	public Map<String, FacetingRequest> getFaceting(QueryBuilder queryBuilder);

	/**
	 * @return a Set of names of the Filters to use with the query or an empty
	 *         Set if none should be used
	 */
	public Set<String> getFilters();

	/**
	 * @return a Map with the mapping: <br>
	 *         FilterName -> Set of FilterParameters
	 */
	public Map<String, Set<FilterParameter>> getFilterParameters();
	
	public DetachedCriteria getCriteriaForFetchModes();

	public static class FilterParameter {

		public FilterParameter(String key, Object value) {
			this.key = key;
			this.value = value;
		}

		public final String key;

		public final Object value;

	}

}
