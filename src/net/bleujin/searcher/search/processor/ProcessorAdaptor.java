package net.bleujin.searcher.search.processor;

import net.bleujin.searcher.index.channel.RelayChannel;
import net.bleujin.searcher.search.SearchRequest;
import net.bleujin.searcher.search.SearchResponse;

public class ProcessorAdaptor implements PostProcessor {

	private RelayChannel<SearchTask> channel ;
	private ThreadProcessor threadProcessor ;
	public ProcessorAdaptor(RelayChannel<SearchTask> channel, ThreadProcessor threadProcessor) {
		this.channel = channel ;
		this.threadProcessor = threadProcessor ;
		
		threadProcessor.start() ;
	}

	public void process(SearchRequest sreq, SearchResponse sres) {
		channel.addMessage(new SearchTask(sreq, sres)) ;
	}
	
	

}
