package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import net.bleujin.searcher.common.ReadDocument;


public class SearchRequest {

	private SearchSession ssession;
	private Query query;

	SearchRequest(SearchSession ssession, Query query) {
		this.ssession = ssession ;
		this.query = query ;
	}
	
	public Query query() {
		return query;
	}
	
	public ReadDocument findOne() throws IOException {
		
		final List<ReadDocument> docs = find().getDocument();
		if (docs.size() == 0) return null ;
		return docs.get(0) ;
	}

	public int maxResult() {
		return 100;
	}

	public int limit() {
		return 100;
	}

	public int skip() {
		return 0;
	}

	
	public Sort sort() {
		return Sort.INDEXORDER;
	}

	public SearchResponse find() throws IOException {
		return ssession.search(this) ;
	}



}
