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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.search.FullTextFilter;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.EntityContext;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetingRequest;

import de.hotware.hibernate.query.intelligent.structure.CachedInfo;
import de.hotware.hibernate.query.intelligent.structure.Query;
import de.hotware.hibernate.query.intelligent.structure.QueryHierarchyExtractor;
import de.hotware.hibernate.query.intelligent.structure.QueryHierarchyExtractorImpl;

/**
 * the implementation of the Searcher-Interface
 * 
 * @author Martin Braun
 * 
 * @param <T>
 *            the type of the indexed Entity
 * @param <U>
 *            the type the QueryBean to use for the indexed Entity
 */
public class SearcherImpl<T, U extends QueryBean<T>> implements Searcher<T, U> {

	private final CachedInfo cachedInfo;
	private final Class<T> indexedClass;
	private final Map<String, Query> hierarchies;

	public SearcherImpl(Class<T> indexedClass, Class<U> queryBeanClass) {
		this.indexedClass = indexedClass;
		this.cachedInfo = new CachedInfo();
		QueryHierarchyExtractor extractor = new QueryHierarchyExtractorImpl();
		this.hierarchies = extractor.getHierarchy(queryBeanClass, cachedInfo);
	}

	@Override
	public SearchResult search(U queryBean, FullTextSession fullTextSession) {
		return this
				.search(queryBean,
						fullTextSession,
						de.hotware.hibernate.query.intelligent.annotations.Query.DEFAULT_PROFILE);
	}

	@Override
	public SearchResult search(U queryBean, FullTextSession fullTextSession,
			String profile) {
		fullTextSession.setFlushMode(FlushMode.MANUAL);
		SearchFactory searchFactory = fullTextSession.getSearchFactory();

		QueryBuilder queryBuilder;
		{
			QueryContextBuilder queryContextBuilder = searchFactory
					.buildQueryBuilder();
			EntityContext entityContext = queryContextBuilder
					.forEntity(this.indexedClass);
			queryBuilder = entityContext.get();
		}

		@SuppressWarnings("rawtypes")
		BooleanJunction<BooleanJunction> mainJunction = queryBuilder.bool();

		Query hierarchicalQuery = this.hierarchies.get(profile);
		if (hierarchicalQuery == null) {
			throw new IllegalArgumentException("profile " + profile
					+ " not available!");
		}
		hierarchicalQuery.constructQuery(mainJunction, queryBuilder, queryBean,
				cachedInfo);

		// apply the custom query
		mainJunction = queryBean.customQuery(mainJunction, queryBuilder);
		final FullTextQuery query = fullTextSession.createFullTextQuery(
				mainJunction.createQuery(), this.indexedClass);

		// apply the filters
		if (queryBean.getFilters() != null) {
			for (String filter : queryBean.getFilters()) {
				FullTextFilter fullTextFilter = query
						.enableFullTextFilter(filter);
				Set<QueryBean.FilterParameter> filterParameters = queryBean
						.getFilterParameters().get(filter);
				if (filterParameters != null) {
					for (QueryBean.FilterParameter parameter : filterParameters) {
						fullTextFilter.setParameter(parameter.key,
								parameter.value);
					}
				}
			}
		}

		// apply the custom sort (if available)
		if (queryBean.getSort() != null) {
			query.setSort(queryBean.getSort());
		}

		{
			DetachedCriteria criteria = queryBean.getCriteriaForFetchModes();
			if (criteria != null) {
				query.setCriteriaQuery(criteria
						.getExecutableCriteria(fullTextSession));
			}
		}

		// faceting
		final Map<String, FacetingRequest> faceting = queryBean
				.getFaceting(queryBuilder);
		final Map<String, List<Facet>> facets = new HashMap<>();
		if (faceting != null) {
			FacetManager facetManager = query.getFacetManager();
			for (FacetingRequest request : faceting.values()) {
				facetManager.enableFaceting(request);
			}
			for (String name : faceting.keySet()) {
				facets.put(name, facetManager.getFacets(name));
			}
		}
		return new SearchResult() {

			@Override
			public FullTextQuery getFullTextQuery() {
				return query;
			}

			@Override
			public Map<String, List<Facet>> getFacets() {
				return facets;
			}

		};
	}

}
