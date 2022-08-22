package net.bleujin.searcher.search.processor;

import net.bleujin.searcher.search.SearchRequest;
import net.bleujin.searcher.search.SearchResponse;
import net.ion.framework.util.Debug;


// test 
public class StdOutProcessor implements PostProcessor{

	public void process(SearchRequest sreq, SearchResponse sres) {
		Debug.debug(getClass().getName(), sreq, sres) ;
	}
	
}
