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

import java.util.HashMap;
import java.util.Map;

import org.hibernate.search.FullTextSession;

/**
 * a SpringSearcher implementation
 * 
 * @author Martin Braun
 */
public class MultiClassSearcherImpl implements MultiClassSearcher {

	private final Map<SearcherClassWrapper<?, ?>, Searcher<?, ?>> searchers;

	public MultiClassSearcherImpl() {
		this.searchers = new HashMap<>();
	}

	@Override
	public <T, U extends QueryBean<T>> SearchResult search(U queryBean,
			FullTextSession fullTextSession, Class<T> indexedClass,
			Class<U> queryBeanClass) {
		return this
				.search(queryBean,
						fullTextSession,
						indexedClass,
						queryBeanClass,
						de.hotware.hibernate.query.intelligent.annotations.Query.DEFAULT_PROFILE);
	}

	@Override
	public <T, U extends QueryBean<T>> SearchResult search(U queryBean,
			FullTextSession fullTextSession, Class<T> indexedClass,
			Class<U> queryBeanClass, String profile) {
		SearcherClassWrapper<T, U> wrapper = new SearcherClassWrapper<>(
				indexedClass, queryBeanClass);
		@SuppressWarnings("unchecked")
		Searcher<T, U> searcher = (Searcher<T, U>) this.searchers.get(wrapper);
		if (searcher == null) {
			searcher = new SearcherImpl<T, U>(indexedClass, queryBeanClass);
			this.searchers.put(wrapper, searcher);
		}
		return searcher.search(queryBean, fullTextSession, profile);
	}

	private static class SearcherClassWrapper<T, U> {

		public SearcherClassWrapper(Class<T> indexedClass,
				Class<U> queryBeanClass) {
			this.indexedClass = indexedClass;
			this.queryBeanClass = queryBeanClass;
		}

		private final Class<T> indexedClass;
		private final Class<U> queryBeanClass;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((indexedClass == null) ? 0 : indexedClass.hashCode());
			result = prime
					* result
					+ ((queryBeanClass == null) ? 0 : queryBeanClass.hashCode());
			return result;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SearcherClassWrapper other = (SearcherClassWrapper) obj;
			if (indexedClass == null) {
				if (other.indexedClass != null)
					return false;
			} else if (!indexedClass.equals(other.indexedClass))
				return false;
			if (queryBeanClass == null) {
				if (other.queryBeanClass != null)
					return false;
			} else if (!queryBeanClass.equals(other.queryBeanClass))
				return false;
			return true;
		}

	}

}
