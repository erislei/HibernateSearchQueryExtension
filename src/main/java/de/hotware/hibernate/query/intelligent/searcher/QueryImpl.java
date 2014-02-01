package de.hotware.hibernate.query.intelligent.searcher;

import java.util.ArrayList;
import java.util.List;

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
			QueryBuilder queryBuilder, Object bean, CachedInfo cachedInfo) {
		@SuppressWarnings("rawtypes")
		BooleanJunction<BooleanJunction> subJunction = queryBuilder.bool();
		boolean addedStuff = false;
		for (QueryElement elem : this.queryElements) {
			if (elem.constructQuery(subJunction, queryBuilder, bean, cachedInfo)) {
				addedStuff = true;
			}
		}
		return addedStuff;
	}

}
