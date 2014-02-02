package de.hotware.hibernate.query.intelligent.structure;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.hotware.hibernate.query.intelligent.annotations.Junction;

public abstract class BaseQueryElement implements QueryElement {

	private final Query subQuery;
	private int boost = Integer.MIN_VALUE;
	private final Junction betweenValues;

	protected BaseQueryElement(Query subQuery, Junction betweenValues) {
		this.subQuery = subQuery;
		this.betweenValues = betweenValues;
	}

	/**
	 * @throws IllegalStateException
	 *             if boost has not been set, yet
	 */
	public int getBoost() {
		if (!this.hasBoostSet()) {
			throw new IllegalStateException("boost has not been set, yet");
		}
		return boost;
	}

	public void setBoost(int boost) {
		if (boost == Integer.MIN_VALUE) {
			throw new IllegalArgumentException(
					"boost may not be equal to Integer.MIN_VALUE");
		}
		this.boost = boost;
	}

	public boolean hasBoostSet() {
		return this.boost != Integer.MIN_VALUE;
	}

	@Override
	public boolean hasSubQuery() {
		return this.subQuery != null;
	}	

	public Junction getBetweenValues() {
		return betweenValues;
	}

	@Override
	public boolean constructQuery(
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			QueryBuilder queryBuilder, Object bean, CachedInfo cachedInfo) {
		return this.subQuery != null ? this.subQuery.constructQuery(junction,
				queryBuilder, bean, cachedInfo) : false;
	}

	protected static boolean buildValueQuery(
			QueryBuilder queryBuilder,
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			Object initial, QueryType queryType, String property,
			String fieldName, CachedInfo cachedInfo, Junction betweenValues) {
		List<Object> values = new ArrayList<>();
		// either add the object values to the list or convert them to
		// strings first this is handled in the addValueToList method
		// depending on queryType.valueAsString();
		{
			StringBridge stringBridge = cachedInfo.stringBridges.get(property);
			Analyzer analyzer = cachedInfo.analyzers.get(property);
			List<Object> initialValues = new ArrayList<>();
			if (initial instanceof Iterable<?>) {
				for (Object val : ((Iterable<?>) initial)) {
					if (val != null) {
						initialValues.add(val);
					}
				}
			} else {
				initialValues.add(initial);
			}
			for (Object val : initialValues) {
				if (queryType.valueAsString()) {
					String string = stringBridge.objectToString(val);
					values.addAll(applyAnalyzer(string, analyzer));
				} else {
					values.add(val);
				}
			}
		}
		for (Object val : values) {
			org.apache.lucene.search.Query query = queryType.query(
					queryBuilder, fieldName, val);
			betweenValues.combine(junction, query);
		}
		return values.size() > 0;
	}

	private static List<String> applyAnalyzer(String string, Analyzer analyzer) {
		List<String> ret = new ArrayList<>();
		if (analyzer != null) {
			try (TokenStream ts = analyzer.tokenStream("myfield",
					new StringReader(string))) {
				while (ts.incrementToken()) {
					ret.add(ts.getAttribute(CharTermAttribute.class).toString());
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			ret.add(string);
		}
		return ret;
	}

}
