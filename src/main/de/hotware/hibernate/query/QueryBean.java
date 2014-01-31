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
