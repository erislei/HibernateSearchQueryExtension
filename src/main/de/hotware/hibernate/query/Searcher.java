package de.hotware.hibernate.query;



import org.hibernate.search.FullTextSession;

/**
 * The Searcher interface.
 * 
 * @author Martin Braun
 * 
 * @param <T>
 *            the type of the bean to return search requests
 */
public interface Searcher<T, U extends QueryBean<T>> {
	
	public SearchResult search(U queryBean, FullTextSession fullTextSession, String profile);
	
	/**
	 * same as {@link #search(QueryBean, FullTextSession, String)} but with {@link SearchField#DEFAULT_PROFILE}
	 * as profile
	 */
	public SearchResult search(U queryBean, FullTextSession fullTextSession);

}
