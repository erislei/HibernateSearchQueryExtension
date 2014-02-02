package de.hotware.hibernate.query.intelligent.structure;

import java.util.Map;

import de.hotware.hibernate.query.intelligent.searcher.QueryBean;

public interface QueryHierarchyExtractor {
	
	public Map<String, Query> getHierarchy(Class<? extends QueryBean<?>> clazz, CachedInfo cachedInfo);

}
