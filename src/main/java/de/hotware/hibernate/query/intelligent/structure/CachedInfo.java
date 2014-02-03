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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.lucene.analysis.Analyzer;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.bridge.StringBridge;

public class CachedInfo {

	private Map<Class<?>, WrapDynaClass> cachedClasses = new HashMap<>();
	private Map<Class<? extends StringBridge>, StringBridge> stringBridges = new HashMap<>();
	private Map<Class<?>, QueryType> queryTypes = new HashMap<>();

	public WrapDynaClass getCachedClass(Class<?> clazz) {
		WrapDynaClass ret = this.cachedClasses.get(clazz);
		if (ret == null) {
			ret = WrapDynaClass.createDynaClass(clazz);
			this.cachedClasses.put(clazz, ret);
		}
		return ret;
	}

	public StringBridge getCachedStringBridge(
			Class<? extends StringBridge> clazz) {
		StringBridge ret = this.stringBridges.get(clazz);
		if (ret == null) {
			try {
				ret = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			this.stringBridges.put(clazz, ret);
		}
		return ret;
	}
	
	public QueryType getCachedQueryType(Class<? extends QueryType> clazz) {
		QueryType ret = this.queryTypes.get(clazz);
		if(ret == null) {
			try {
				ret = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			this.queryTypes.put(clazz, ret);
		}
		return ret;
	}

	public Analyzer getAnalyzer(String analyzer, SearchFactory searchFactory) {
		Analyzer ret = null;
		if(!analyzer.equals("")) {
				ret = searchFactory.getAnalyzer(analyzer);
		}
		return ret;
	}

}
