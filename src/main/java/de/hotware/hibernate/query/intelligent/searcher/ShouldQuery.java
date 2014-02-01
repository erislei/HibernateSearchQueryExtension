package de.hotware.hibernate.query.intelligent.searcher;

import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.hotware.hibernate.query.intelligent.annotations.Junction;

public class ShouldQuery extends BaseQueryElement {

	private final String fieldName;
	private final String property;
	private final QueryType queryType;

	public ShouldQuery(Query subQuery, boolean not, Junction betweenJunction) {
		super(subQuery, betweenJunction);
		this.fieldName = null;
		this.property = null;
		this.queryType = null;
	}

	public ShouldQuery(String fieldName, String property, QueryType queryType,
			Junction betweenJunction) {
		super(null, betweenJunction);
		this.fieldName = fieldName;
		this.property = property;
		this.queryType = queryType;
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
				junction.should(subJunction.createQuery());
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
					junction.should(valuesJunction.createQuery());
					ret = true;
				}
			}
		}
		return ret;
	}

}
