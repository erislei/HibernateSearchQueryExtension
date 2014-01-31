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

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.Query;
import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.search.FullTextFilter;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.EntityContext;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.QueryContextBuilder;
import org.hibernate.search.query.engine.spi.FacetManager;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetingRequest;

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

	private final Class<T> indexedClass;
	private final Class<U> queryBeanClass;
	private final Map<java.lang.reflect.Method, String> pathToSearchField;
	private final Map<java.lang.reflect.Method, Class<?>> returnTypeForEmbedded;
	private final Map<Class<?>, Map<java.lang.reflect.Method, SearchFieldEmbedded>> embeddedSearchFields;
	private final Map<Class<?>, Map<java.lang.reflect.Method, SearchField>> singleSearchFields;
	private final Map<Class<?>, Map<java.lang.reflect.Method, SearchFields>> multipleSearchFields;
	private final Map<Class<? extends StringBridge>, StringBridge> bridges;
	private final Map<Class<? extends QueryType>, QueryType> queryTypes;
	private final Map<Class<? extends Analyzer>, Analyzer> analyzers;

	public SearcherImpl(Class<T> indexedClass, Class<U> queryBeanClass) {
		this.indexedClass = indexedClass;
		this.queryBeanClass = queryBeanClass;
		this.singleSearchFields = new HashMap<>();
		this.multipleSearchFields = new HashMap<>();
		this.embeddedSearchFields = new HashMap<>();
		this.pathToSearchField = new HashMap<>();
		this.returnTypeForEmbedded = new HashMap<>();
		this.bridges = new HashMap<>();
		this.queryTypes = new HashMap<>();
		this.analyzers = new HashMap<>();
		this.initializeRecursive(queryBeanClass, new HashSet<Class<?>>(), "");
	}

	@Override
	public SearchResult search(U queryBean, FullTextSession fullTextSession) {
		return this.search(queryBean, fullTextSession,
				SearchField.DEFAULT_PROFILE);
	}

	@SuppressWarnings("rawtypes")
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

		BooleanJunction<BooleanJunction> mainJunction = queryBuilder.bool();

		this.buildQueryRecursive(this.queryBeanClass, searchFactory,
				queryBuilder, mainJunction, queryBean, profile);

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
			DetachedCriteria criteria= queryBean.getCriteriaForFetchModes();
			if(criteria != null) {
				query.setCriteriaQuery(criteria.getExecutableCriteria(fullTextSession));
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

	private void initializeRecursive(Class<?> clazz, Set<Class<?>> alreadyDone,
			String path) {
		if (alreadyDone.contains(clazz)) {
			throw new IllegalArgumentException(clazz
					+ " may not be used more than once");
		} else if (!Iterable.class.isAssignableFrom(clazz)) {
			// iterables are allowed to appear more than once
			alreadyDone.add(clazz);
		}
		Map<java.lang.reflect.Method, SearchFields> searchFieldsMap = new HashMap<>();
		this.multipleSearchFields.put(clazz, searchFieldsMap);
		Map<java.lang.reflect.Method, SearchField> singleSearchFieldMap = new HashMap<>();
		this.singleSearchFields.put(clazz, singleSearchFieldMap);
		Map<java.lang.reflect.Method, SearchFieldEmbedded> embeddedMap = new HashMap<>();
		this.embeddedSearchFields.put(clazz, embeddedMap);
		for (java.lang.reflect.Method method : clazz.getMethods()) {
			method.setAccessible(true);
			SearchFieldEmbedded searchFieldEmbedded = method
					.getAnnotation(SearchFieldEmbedded.class);
			if (searchFieldEmbedded != null) {
				Class<?> nextClass = method.getReturnType();
				StringBuilder newPath = new StringBuilder(path)
						.append(searchFieldEmbedded.pathToField());
				if (newPath.length() > 0) {
					newPath.append(".");
				}
				if (Iterable.class.isAssignableFrom(nextClass)) {
					nextClass = getActualClassForGenericIterable(method);
				}
				this.returnTypeForEmbedded.put(method, nextClass);
				this.initializeRecursive(nextClass, alreadyDone,
						newPath.toString());
				embeddedMap.put(method, searchFieldEmbedded);
			} else {
				SearchFields searchFields = method
						.getAnnotation(SearchFields.class);
				if (searchFields != null) {
					for (SearchField searchField : searchFields.searchFields()) {
						this.initializeForSearchField(searchField, method, path);
					}
					searchFieldsMap.put(method, searchFields);
				}
				SearchField singleSearchField = method
						.getAnnotation(SearchField.class);
				if (singleSearchField != null) {
					this.initializeForSearchField(singleSearchField, method,
							path);
					singleSearchFieldMap.put(method, singleSearchField);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private final void buildQueryRecursive(Class<?> clazz,
			SearchFactory searchFactory, QueryBuilder queryBuilder,
			BooleanJunction<BooleanJunction> mainJunction, Object queryBean,
			String profile) {
		// combine the query with the multiple SearchFields
		for (Map.Entry<java.lang.reflect.Method, SearchFields> entry : this.multipleSearchFields
				.get(clazz).entrySet()) {
			SearchFields searchFields = entry.getValue();
			java.lang.reflect.Method method = entry.getKey();
			BooleanJunction<BooleanJunction> searchFieldsJunction = queryBuilder
					.bool();
			boolean success = false;
			for (SearchField search : entry.getValue().searchFields()) {
				boolean tmp = this.combineWithJunction(searchFactory,
						searchFieldsJunction, queryBean, queryBuilder, method,
						search, profile);
				if (!success) {
					success = tmp;
				}
			}
			if (success) {
				// only add if searchFieldsJunction isn't empty
				searchFields.topLevel().combine(mainJunction,
						searchFieldsJunction.createQuery());
			}
		}

		// combine the query with the single searchFields
		for (Map.Entry<java.lang.reflect.Method, SearchField> entry : this.singleSearchFields
				.get(clazz).entrySet()) {
			SearchField search = entry.getValue();
			java.lang.reflect.Method method = entry.getKey();
			// this already handles the adding to the mainJunction correctly
			// (throw away the returned value)
			this.combineWithJunction(searchFactory, mainJunction, queryBean,
					queryBuilder, method, search, profile);
		}

		// no need to check for circular recursion, as we have checked for that
		// while constructing
		for (Map.Entry<java.lang.reflect.Method, SearchFieldEmbedded> entry : this.embeddedSearchFields
				.get(clazz).entrySet()) {
			try {
				java.lang.reflect.Method method = entry.getKey();
				Class<?> nextClass = this.returnTypeForEmbedded.get(method);
				Object nextBean = method.invoke(queryBean);
				if (nextBean != null) {
					if (nextBean instanceof Iterable<?>) {
						for (Object val : (Iterable<?>) nextBean) {
							if (val != null) {
								this.buildQueryRecursive(nextClass,
										searchFactory, queryBuilder,
										mainJunction, val, profile);
							}
						}
					} else {
						this.buildQueryRecursive(nextClass, searchFactory,
								queryBuilder, mainJunction, nextBean, profile);
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private final boolean combineWithJunction(SearchFactory searchFactory,
			BooleanJunction<BooleanJunction> mainJunction, Object queryBean,
			QueryBuilder queryBuilder, java.lang.reflect.Method method,
			SearchField search, String profile) {
		if (search.fieldNames().length > 0) {
			QueryType queryType = this.queryTypes.get(search.queryType());
			BooleanJunction<BooleanJunction> betweenValuesJunction = queryBuilder
					.bool();
			org.hibernate.search.annotations.Analyzer analyzerAnnotation = search
					.queryAnalyzer();
			Analyzer analyzer = null;
			if (!analyzerAnnotation.definition().equals("")) {
				analyzer = searchFactory.getAnalyzer(analyzerAnnotation
						.definition());
			} else if (!analyzerAnnotation.impl().equals(void.class)) {
				analyzer = this.analyzers.get(analyzerAnnotation.impl());
			} else {
				analyzer = null;
			}
			List<Object> values = new ArrayList<>();
			try {
				Object object = null;
				try {
					object = method.invoke(queryBean);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new RuntimeException(e);
				}

				// either add the object values to the list or convert them to
				// strings first this is handled in the addValueToList method
				// depending on queryType.valueAsString();
				{
					StringBridge stringBridge = this.bridges.get(search
							.stringBridge());
					if (object instanceof Iterable<?>) {
						for (Object val : ((Iterable<?>) object)) {
							if (val != null) {
								addValueToList(values, analyzer, stringBridge,
										queryType, val);
							}
						}
					} else {
						if (object != null) {
							addValueToList(values, analyzer, stringBridge,
									queryType, object);
						}
					}
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			for (Object val : values) {
				if (search.profile().equals(profile)) {
					Query query = queryType.query(queryBuilder,
							this.pathToSearchField.get(method), search, val);
					search.betweenValues()
							.combine(betweenValuesJunction, query);
				}
			}
			// only combine with the junction if there were values
			if (values.size() > 0) {
				search.topLevel().combine(mainJunction,
						betweenValuesJunction.createQuery());
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private final void initializeForSearchField(SearchField searchField,
			java.lang.reflect.Method method, String path) {
		this.pathToSearchField.put(method, path);
		if (!this.queryTypes.containsKey(searchField.queryType())) {
			try {
				this.queryTypes.put(searchField.queryType(), searchField
						.queryType().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException(
						"QueryTypes must have a 0-args constructor");
			}
		}

		if (!searchField.stringBridge().equals(void.class)
				&& !this.bridges.containsKey(searchField.stringBridge())) {
			try {
				this.bridges.put((Class<? extends StringBridge>) searchField
						.stringBridge(), (StringBridge) searchField
						.stringBridge().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException(
						"StringBridges must have a 0-args constructor");
			}
		}

		if (!searchField.queryAnalyzer().impl().equals(void.class)
				&& !this.analyzers.containsKey(searchField.queryAnalyzer()
						.impl())) {
			try {
				this.analyzers.put((Class<? extends Analyzer>) searchField
						.queryAnalyzer().impl(), (Analyzer) searchField
						.queryAnalyzer().impl().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException(
						"analyzers must have a 0-args constructor");
			}
		}

	}

	private static final void addValueToList(List<Object> values,
			Analyzer analyzer, StringBridge stringBridge, QueryType queryType,
			Object value) throws IOException {
		if (queryType.valueAsString()) {
			if (stringBridge == null) {
				throw new IllegalArgumentException("for "
						+ queryType.getClass() + " you need a stringBridge");
			}
			String string = stringBridge.objectToString(value);
			values.addAll(applyAnalyzer(string, analyzer));
		} else {
			values.add(value);
		}
	}

	private static List<String> applyAnalyzer(String string, Analyzer analyzer)
			throws IOException {
		List<String> ret = new ArrayList<>();
		if (analyzer != null) {
			try (TokenStream ts = analyzer.tokenStream("myfield",
					new StringReader(string))) {
				while (ts.incrementToken()) {
					ret.add(ts.getAttribute(CharTermAttribute.class).toString());
				}
			}
		} else {
			ret.add(string);
		}
		return ret;
	}

	private static Class<?> getActualClassForGenericIterable(
			java.lang.reflect.Method method) {
		Class<?> actualClass;
		java.lang.reflect.Type[] typeArguments = ((ParameterizedType) method
				.getGenericReturnType()).getActualTypeArguments();
		if (typeArguments.length > 0) {
			actualClass = (Class<?>) typeArguments[0];
		} else {
			throw new IllegalArgumentException(
					"non-generic generic types not allowed");
		}
		return actualClass;
	}

}
