package de.hotware.hibernate.query.intelligent.structure;

import org.hibernate.search.SearchFactory;
import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.hotware.hibernate.query.intelligent.annotations.Junction;

public class MustQuery extends BaseQueryElement {

	private final String fieldName;
	private final String property;
	private final QueryType queryType;
	private final StringBridge stringBridge;
	private final String analyzer;
	private final boolean not;
	private final Junction betweenValues;

	public MustQuery(Query subQuery, boolean not) {
		super(subQuery);
		this.fieldName = null;
		this.property = null;
		this.queryType = null;
		this.not = not;
		this.stringBridge = null;
		this.analyzer = null;
		this.betweenValues = null;
	}

	public MustQuery(String fieldName, String property, QueryType queryType,
			StringBridge stringBridge, String analyzer, boolean not,
			Junction betweenValues) {
		super(null);
		this.fieldName = fieldName;
		this.property = property;
		this.queryType = queryType;
		this.not = not;
		this.stringBridge = stringBridge;
		this.analyzer = analyzer;
		this.betweenValues = betweenValues;
	}

	@Override
	public boolean constructQuery(
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			QueryBuilder queryBuilder, Object bean, CachedInfo cachedInfo,
			SearchFactory searchFactory) {
		boolean ret = false;
		if (this.hasSubQuery()) {
			@SuppressWarnings("rawtypes")
			BooleanJunction<BooleanJunction> subJunction = queryBuilder.bool();
			if (super.constructQuery(subJunction, queryBuilder, bean,
					cachedInfo, searchFactory)) {
				if (this.not) {
					junction.must(subJunction.createQuery()).not();
				} else {
					junction.must(subJunction.createQuery());
				}
				ret = true;
			}
		} else {
			Object value = Util.getProperty(cachedInfo, bean, this.property);
			if (value != null) {
				@SuppressWarnings("rawtypes")
				BooleanJunction<BooleanJunction> valuesJunction = queryBuilder
						.bool();
				if (buildValueQuery(queryBuilder, valuesJunction, value,
						queryType, this.stringBridge, cachedInfo.getAnalyzer(this.analyzer, searchFactory), property,
						fieldName, this.betweenValues)) {
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
