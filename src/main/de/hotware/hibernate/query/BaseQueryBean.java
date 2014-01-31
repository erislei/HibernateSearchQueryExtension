package de.hotware.hibernate.query;

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
