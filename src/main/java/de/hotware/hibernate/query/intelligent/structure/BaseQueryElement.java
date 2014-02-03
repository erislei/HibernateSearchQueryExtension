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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.bridge.StringBridge;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import de.hotware.hibernate.query.intelligent.annotations.Junction;

public abstract class BaseQueryElement implements QueryElement {

	private final Query subQuery;
	private int boost = Integer.MIN_VALUE;

	protected BaseQueryElement(Query subQuery) {
		this.subQuery = subQuery;
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

	@Override
	public boolean constructQuery(
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			QueryBuilder queryBuilder, Object bean, CachedInfo cachedInfo, SearchFactory searchFactory) {
		return this.subQuery != null ? this.subQuery.constructQuery(junction,
				queryBuilder, bean, cachedInfo, searchFactory) : false;
	}

	protected static boolean buildValueQuery(
			QueryBuilder queryBuilder,
			@SuppressWarnings("rawtypes") BooleanJunction<BooleanJunction> junction,
			Object initial, QueryType queryType, StringBridge stringBridge,
			Analyzer analyzer, String property, String fieldName,
			Junction betweenValues) {
		List<Object> values = new ArrayList<>();
		// either add the object values to the list or convert them to
		// strings first this is handled in the addValueToList method
		// depending on queryType.valueAsString();
		{
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
					if(analyzer == null) {
						throw new IllegalArgumentException("you need an analyzer for this queryType");
					}
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
