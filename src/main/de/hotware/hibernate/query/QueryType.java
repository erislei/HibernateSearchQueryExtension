package de.hotware.hibernate.query;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;

/**
 * Interface for wrapping the different types of Queries in Hibernate Search
 * 
 * @author Martin Braun
 */
public interface QueryType {

	/**
	 * @param queryBuilder
	 *            the queryBuilder to build the queries from
	 * @param searchField
	 *            the explicit searchField
	 * @param value
	 *            either a String or a general Object depending on
	 *            valueAsString()'s value
	 * @param pathToField
	 *            path to the field with a . at the end (just concatenated with
	 *            the fieldName)
	 */
	public Query query(QueryBuilder queryBuilder, String pathToField,
			SearchField searchField, Object value);

	/**
	 * @return true if the values should be passed as Strings into the query
	 *         method (if true, such a QueryType can only be used with a
	 *         StringBridge and should only be used with an Analyzer)
	 */
	public boolean valueAsString();

}