package net.bleujin.searcher.search.processor;

import net.bleujin.searcher.search.SearchRequest;
import net.bleujin.searcher.search.SearchResponse;


public class SearchTask {

	private SearchResponse response ;
	private SearchRequest request ;
	
	public SearchTask(SearchRequest request, SearchResponse response) {
		this.request = request ;
		this.response = response ;
	}

	public SearchResponse getResult() {
		return response ;
	}
	
	public SearchRequest getRequest(){
		return request ;
	}

	public String toString(){
		return " Request : " + response.request() + " Result Count : " + response.totalCount() + " Elapsed Time(ms) : " + response.elapsedTime() ;
	}
}
