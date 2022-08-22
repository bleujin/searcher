package net.bleujin.searcher.search.processor;

import net.bleujin.searcher.search.SearchRequest;
import net.bleujin.searcher.search.SearchResponse;


public interface PostProcessor {

	void process(SearchRequest sreq, SearchResponse sres);
	
	
}
