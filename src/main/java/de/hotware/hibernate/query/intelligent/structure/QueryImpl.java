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
