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
