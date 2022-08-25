package net.bleujin.searcher;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.search.SearchConfig;
import net.bleujin.searcher.search.SearchRequest;
import net.bleujin.searcher.search.SearchResponse;

public class SearchRequestWrapper {

	private Searcher searcher;
	private SearchController sdc;
	private Query query;
	private SearchConfig sconfig;

	public SearchRequestWrapper(Searcher searcher, SearchController sdc, Query query) {
		this.searcher = searcher ;
		this.sdc = sdc ;
		this.query = query ;
		this.sconfig = SearchConfig.create(sdc) ;
	}

	
	public ReadDocument findOne() throws IOException {
		
		final List<ReadDocument> docs = find().getDocument();
		if (docs.size() == 0) return null ;
		return docs.get(0) ;
	}
	
	public SearchResponse find() throws IOException {
		return sdc.search(sconfig, query);
	}

	
	
	
	public SearchRequestWrapper ascending(String sort) {
		
		return this;
	}
	
	public SearchRequestWrapper descending(String string) {
		// TODO Auto-generated method stub
		return this ;
	}



	public SearchRequestWrapper ascendingNum(String string) {
		// TODO Auto-generated method stub
		return this;
	}



	public SearchRequestWrapper descendingNum(String string) {
		// TODO Auto-generated method stub
		return this;
	}



	public Sort sort() {
		// TODO Auto-generated method stub
		return null;
	}


	public SearchRequest sort(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
