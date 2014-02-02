package de.hotware.hibernate.query.intelligent.structure;

import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

public interface QueryElement {

	public boolean constructQuery(
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			QueryBuilder queryBuilder, Object bean, CachedInfo cachedInfo);

	public boolean hasSubQuery();

}