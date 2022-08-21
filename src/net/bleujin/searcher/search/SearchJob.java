package net.bleujin.searcher.search;

import java.io.IOException;

import org.apache.lucene.search.MatchAllDocsQuery;

import net.bleujin.searcher.common.ReadDocument;

public interface SearchJob<T> {
	
	public final static SearchJob<ReadDocument> SAMPLE_SEARCH = new SearchJob<ReadDocument>() {
		@Override
		public ReadDocument handle(SearchSession ssession) throws IOException {
			ReadDocument wdoc = ssession.createRequest(new MatchAllDocsQuery()).findOne() ;
			return wdoc;
		}
	};
	
	public T handle(SearchSession ssession) throws IOException ;
}
