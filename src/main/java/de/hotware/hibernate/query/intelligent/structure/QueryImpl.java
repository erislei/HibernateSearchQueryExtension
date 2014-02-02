package de.hotware.hibernate.query.intelligent.structure;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.search.SearchFactory;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

public class QueryImpl implements Query {

	private List<QueryElement> queryElements = new ArrayList<>();

	public void addQueryElement(QueryElement queryElement) {
		this.queryElements.add(queryElement);
	}

	@Override
	public boolean constructQuery(
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			QueryBuilder queryBuilder, Object bean, CachedInfo cachedInfo, SearchFactory searchFactory) {
		boolean addedStuff = false;
		for (QueryElement elem : this.queryElements) {
			if (elem.constructQuery(junction, queryBuilder, bean, cachedInfo, searchFactory)) {
				addedStuff = true;
			}
		}
		return addedStuff;
	}

}
