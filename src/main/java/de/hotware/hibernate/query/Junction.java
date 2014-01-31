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
