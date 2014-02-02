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
package de.hotware.hibernate.query.intelligent.searcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.Sort;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.facet.FacetingRequest;

/**
 * Base implementation of a QueryBean
 * 
 * @author Martin Braun
 *
 * @param <T>
 *            the type of the bean to return search requests
 */
public abstract class BaseQueryBean<T> implements QueryBean<T> {

	@Override
	public Sort getSort() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public BooleanJunction<BooleanJunction> customQuery(
			BooleanJunction<BooleanJunction> mainBooleanQuery,
			QueryBuilder queryBuilder) {
		return mainBooleanQuery;
	}

	@Override
	public Map<String, FacetingRequest> getFaceting(QueryBuilder queryBuilder) {
		return null;
	}

	@Override
	public Set<String> getFilters() {
		return new HashSet<>();
	}

	@Override
	public Map<String, Set<QueryBean.FilterParameter>> getFilterParameters() {
		return new HashMap<>();
	}

	@Override
	public DetachedCriteria getCriteriaForFetchModes() {
		return null;
	}

}
