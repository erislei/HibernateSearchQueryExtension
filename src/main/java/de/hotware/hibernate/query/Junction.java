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
package de.hotware.hibernate.query;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.BooleanJunction;

/**
 * enum that encapsulates the differnt types queries can be combined together
 * 
 * @author Martin Braun
 */
public enum Junction {
	MUST {

		@SuppressWarnings("rawtypes")
		@Override
		public void combine(BooleanJunction<BooleanJunction> mainQuery,
				Query query) {
			mainQuery.must(query);
		}
	},
	MUST_NOT {

		@SuppressWarnings({ "rawtypes" })
		@Override
		public void combine(BooleanJunction<BooleanJunction> mainQuery,
				Query query) {
			mainQuery.must(query).not();
		}
	},
	SHOULD {

		@SuppressWarnings({ "rawtypes" })
		@Override
		public void combine(BooleanJunction<BooleanJunction> mainQuery,
				Query query) {
			mainQuery.should(query);
		}
	};

	@SuppressWarnings({ "rawtypes" })
	public abstract void combine(BooleanJunction<BooleanJunction> mainQuery,
			Query query);

}
