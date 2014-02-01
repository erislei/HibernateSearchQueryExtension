package de.hotware.hibernate.query.intelligent.searcher;

import java.util.Map;

import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.lucene.analysis.Analyzer;
import org.hibernate.search.bridge.StringBridge;

public class CachedInfo {
	
	public Map<Class<?>, WrapDynaClass> cachedClasses;
	public Map<String, StringBridge> stringBridges;
	public Map<String, Analyzer> analyzers;

}
