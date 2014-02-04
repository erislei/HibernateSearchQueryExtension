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
package de.hotware.hibernate.query.intelligent.structure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.search.bridge.StringBridge;

import de.hotware.hibernate.query.intelligent.annotations.Must;
import de.hotware.hibernate.query.intelligent.annotations.SearchField;
import de.hotware.hibernate.query.intelligent.annotations.Should;
import de.hotware.hibernate.query.intelligent.annotations.SubQueries;
import de.hotware.hibernate.query.intelligent.annotations.SubQuery;
import de.hotware.hibernate.query.intelligent.searcher.QueryBean;

public class QueryHierarchyExtractorImpl implements QueryHierarchyExtractor {

	@Override
	public Map<String, Query> getHierarchy(Class<? extends QueryBean<?>> clazz,
			CachedInfo cachedInfo) {
		de.hotware.hibernate.query.intelligent.annotations.Queries queries = clazz
				.getAnnotation(de.hotware.hibernate.query.intelligent.annotations.Queries.class);
		SubQueries subQueries = clazz
				.getAnnotation(de.hotware.hibernate.query.intelligent.annotations.SubQueries.class);
		Map<String, SubQuery> subQueriesMap = new HashMap<>();
		if (subQueries != null) {
			for (SubQuery subQuery : subQueries.value()) {
				subQueriesMap.put(subQuery.id(), subQuery);
			}
		}
		Map<String, Query> ret = new HashMap<>();
		for (de.hotware.hibernate.query.intelligent.annotations.Query queryAnnotation : queries
				.value()) {
			Query query = this.initializeWithSubQueriesRecursively(
					queryAnnotation, null, subQueriesMap,
					new HashSet<String>(), cachedInfo);
			ret.put(queryAnnotation.profile(), query);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private Query initializeWithSubQueriesRecursively(
			de.hotware.hibernate.query.intelligent.annotations.Query mainQueryAnnotation,
			SubQuery subQuery, Map<String, SubQuery> subQueriesMap,
			Set<String> passedSubQueries, CachedInfo cachedInfo) {
		if (subQuery != null) {
			if (passedSubQueries.contains(subQuery.id())) {
				throw new IllegalArgumentException(
						"loop in subqueries detected!");
			}
			passedSubQueries.add(subQuery.id());
		}
		QueryImpl ret = new QueryImpl();
		de.hotware.hibernate.query.intelligent.annotations.Query queryAnnotation;
		if (mainQueryAnnotation != null) {
			queryAnnotation = mainQueryAnnotation;
		} else {
			queryAnnotation = subQuery.query();
		}
		for (Must must : queryAnnotation.must()) {
			if (!must.subQuery().equals("")) {
				// subQuery found
				Set<String> newPassedQueries = new HashSet<>();
				newPassedQueries.addAll(passedSubQueries);
				Query finishedSubQuery = this
						.initializeWithSubQueriesRecursively(null,
								subQueriesMap.get(must.subQuery()),
								subQueriesMap, newPassedQueries, cachedInfo);
				MustQuery mustQuery = new MustQuery(finishedSubQuery,
						must.not());
				ret.addQueryElement(mustQuery);
			} else if (check(must.value(), cachedInfo)) {
				SearchField searchField = must.value();
				String analyzer = searchField.queryType().queryAnalyzer()
						.definition();
				QueryType queryType = cachedInfo.getCachedQueryType(searchField
						.queryType().value());
				StringBridge stringBridge = null;
				if (!searchField.queryType().stringBridge().equals(void.class)) {
					// TODO: maybe typecheck?
					stringBridge = cachedInfo
							.getCachedStringBridge((Class<? extends StringBridge>) searchField
									.queryType().stringBridge());
				}
				MustQuery mustQuery = new MustQuery(searchField.fieldName(),
						searchField.propertyName(), queryType, stringBridge,
						analyzer, must.not(), searchField.betweenValues(),
						Arrays.asList(searchField.queryType().parameters()),
						Arrays.asList(searchField.queryType()
								.propertyParameters()));
				ret.addQueryElement(mustQuery);
			}
		}
		for (Should should : queryAnnotation.should()) {
			if (!should.subQuery().equals("")) {
				// subQuery found
				Set<String> newPassedQueries = new HashSet<>();
				newPassedQueries.addAll(passedSubQueries);
				Query finishedSubQuery = this
						.initializeWithSubQueriesRecursively(null,
								subQueriesMap.get(should.subQuery()),
								subQueriesMap, newPassedQueries, cachedInfo);
				ShouldQuery shouldQuery = new ShouldQuery(finishedSubQuery);
				ret.addQueryElement(shouldQuery);
			} else if (check(should.value(), cachedInfo)) {
				SearchField searchField = should.value();
				String analyzer = searchField.queryType().queryAnalyzer()
						.definition();
				QueryType queryType = cachedInfo.getCachedQueryType(searchField
						.queryType().value());
				StringBridge stringBridge = null;
				if (!searchField.queryType().stringBridge().equals(void.class)) {
					// TODO: maybe typecheck?
					stringBridge = cachedInfo
							.getCachedStringBridge((Class<? extends StringBridge>) searchField
									.queryType().stringBridge());
				}
				ShouldQuery shouldQuery = new ShouldQuery(
						searchField.fieldName(), searchField.propertyName(),
						queryType, stringBridge, analyzer,
						searchField.betweenValues(), Arrays.asList(searchField
								.queryType().parameters()),
						Arrays.asList(searchField.queryType()
								.propertyParameters()));
				ret.addQueryElement(shouldQuery);
			}
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	private static boolean check(SearchField searchField, CachedInfo cachedInfo) {
		QueryType queryType = cachedInfo.getCachedQueryType(searchField
				.queryType().value());
		Class<?> stringBridgeClass = searchField.queryType().stringBridge();
		StringBridge stringBridge = null;
		if (!stringBridgeClass.equals(void.class)) {
			try {
				stringBridge = cachedInfo
						.getCachedStringBridge((Class<? extends StringBridge>) stringBridgeClass);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException(
						"stringBridges must implement the StringBridge interface of Hibernate Search");
			}
		}
		if (queryType.valueAsString() && stringBridge == null) {
			throw new IllegalArgumentException(queryType.getClass()
					+ " needs a stringBridge");
		}
		return !searchField.fieldName().equals("")
				&& !searchField.propertyName().equals("");
	}

}
