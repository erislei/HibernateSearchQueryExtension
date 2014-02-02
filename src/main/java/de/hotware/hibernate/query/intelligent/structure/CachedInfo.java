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
