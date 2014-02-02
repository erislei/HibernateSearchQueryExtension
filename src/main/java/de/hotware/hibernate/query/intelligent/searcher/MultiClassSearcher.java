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
package de.hotware.hibernate.query.intelligent.searcher;

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
	 * but with the default profile
	 */
	public <T, U extends QueryBean<T>> SearchResult search(U queryBean,
			FullTextSession fullTextSession, Class<T> indexedClass,
			Class<U> queryBeanClass);

}
