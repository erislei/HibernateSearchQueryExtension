package de.hotware.hibernate.query;

import org.hibernate.search.FullTextSession;

/**
 * a version of {@link Searcher} that can handle more than one Bean-QueryBean
 * combination (and it can be injected :))
 * 
 * @author Martin Braun
 */
public interface MultiClassSearcher {

	public <T, U extends QueryBean<T>> SearchResult search(U queryBean,
			FullTextSession fullTextSession, Class<T> indexedClass,
			Class<U> queryBeanClass, String profile);

	/**
	 * same as {@link #search(QueryBean, FullTextSession, Class, Class, String)}
	 * but with {@link SearchField#DEFAULT_PROFILE} as profile
	 */
	public <T, U extends QueryBean<T>> SearchResult search(U queryBean,
			FullTextSession fullTextSession, Class<T> indexedClass,
			Class<U> queryBeanClass);

}
