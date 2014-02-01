package de.hotware.hibernate.query.intelligent.searcher;

import java.util.Map;

import de.hotware.hibernate.query.QueryBean;

public interface QueryHierarchyExtractor {
	
	public Map<String, QueryElement> getHierarchy(Class<? extends QueryBean<?>> clazz);

}
