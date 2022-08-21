package net.bleujin.searcher;

import java.io.IOException;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;

import junit.framework.TestCase;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexConfig;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.search.SearchConfig;
import net.bleujin.searcher.search.SearchJob;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.search.SearchSession;
import net.ion.framework.util.Debug;

public class TestFirst extends TestCase {

	private SearchController sdc;

	@Override
	public void setUp() throws Exception {
		this.sdc = new SearchControllerConfig().build(OpenMode.CREATE_OR_APPEND) ;
	}
	
	public void tearDown() throws Exception  {
		this.sdc.close() ;
	}
	
	public void testInterface() throws Exception {
		
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws IOException {
				IndexConfig iconfig = isession.indexConfig() ;
				
				WriteDocument wdoc = isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).text("content", "Hello Bleujin") ;
				isession.insertDocument(wdoc) ;
				return null;
			}
		}) ;

		sdc.search(new SearchJob<Void>() {
			@Override
			public Void handle(SearchSession ss) throws IOException {
				SearchResponse sres = ss.createRequest("bleujin").find();
				Debug.debug(sres, sres.totalCount(), ss.searcherHashCode()) ;
				return null;
			}
		}) ;
		
		sdc.close();
	}
	
	public void testExpectThread() throws Exception {
		sdc.search(SAMPLE_SEARCH) ;
		sdc.index(IndexJob.SAMPLE_INSERT) ;
		sdc.search(SAMPLE_SEARCH) ;
	}
	
	
	
	
	private SearchJob SAMPLE_SEARCH = new SearchJob<Void>() {
		@Override
		public Void handle(SearchSession ss) throws IOException {
			SearchConfig sconfig = ss.searchConfig() ;
			SearchResponse sres = ss.createRequest("bleujin").find();
			Debug.debug(sres, sres.totalCount(), ss.searcherHashCode()) ;
			return null;
		}
	} ;
}




