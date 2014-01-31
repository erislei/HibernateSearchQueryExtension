package de.hotware.hibernate.query;

import java.util.List;
import java.util.Map;

import org.hibernate.search.FullTextQuery;
import org.hibernate.search.query.facet.Facet;

public interface SearchResult {
	
	public FullTextQuery getFullTextQuery();
	
	public Map<String, List<Facet>> getFacets();
	
}