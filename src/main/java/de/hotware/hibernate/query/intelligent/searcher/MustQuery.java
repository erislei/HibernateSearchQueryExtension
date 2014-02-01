package de.hotware.hibernate.query.intelligent.searcher;

import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.hotware.hibernate.query.intelligent.annotations.Junction;

public class MustQuery extends BaseQueryElement {

	private final String fieldName;
	private final String property;
	private final QueryType queryType;
	private final boolean not;

	public MustQuery(Query subQuery, boolean not, Junction betweenValues) {
		super(subQuery, betweenValues);
		this.fieldName = null;
		this.property = null;
		this.queryType = null;
		this.not = not;
	}

	public MustQuery(String fieldName, String property, QueryType queryType,
			boolean not, Junction betweenValues) {
		super(null, betweenValues);
		this.fieldName = fieldName;
		this.property = property;
		this.queryType = queryType;
		this.not = not;
	}

	@Override
	public boolean constructQuery(
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			QueryBuilder queryBuilder, Object bean, CachedInfo cachedInfo) {
		boolean ret = false;
		if (this.hasSubQuery()) {
			@SuppressWarnings("rawtypes")
			BooleanJunction<BooleanJunction> subJunction = queryBuilder.bool();
			if (super.constructQuery(subJunction, queryBuilder, bean,
					cachedInfo)) {
				if (this.not) {
					junction.must(subJunction.createQuery()).not();
				} else {
					junction.must(subJunction.createQuery());
				}
				ret = true;
			}
		} else {
			Object value = Util.getProperty(cachedInfo.cachedClasses, bean,
					this.property);
			if (value != null) {
				@SuppressWarnings("rawtypes")
				BooleanJunction<BooleanJunction> valuesJunction = queryBuilder
						.bool();
				if (buildValueQuery(queryBuilder, valuesJunction, value,
						queryType, property, fieldName, cachedInfo,
						this.getBetweenValues())) {
					if (this.hasBoostSet()) {
						valuesJunction.boostedTo(this.getBoost());
					}
					if (this.not) {
						junction.must(valuesJunction.createQuery()).not();
					} else {
						junction.must(valuesJunction.createQuery());
					}
					ret = true;
				}
			}
		}
		return ret;
	}

}
