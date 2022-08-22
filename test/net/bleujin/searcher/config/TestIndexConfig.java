package net.bleujin.searcher.config;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;


public class TestIndexConfig extends TestCase{

	
	public void testSetIndexConfig() throws Exception {
		
		SearchController cen = SearchControllerConfig.newRam().newBuild() ;
		cen.index(session -> {
			session.indexConfig().maxBufferedDocs(100);
			assertEquals(100, session.indexConfig().maxBufferedDocs()) ;
			return null;
		}) ;
		
		cen.close(); 
	}
	
	public void testDefaultIndexConfig() throws Exception {
		SearchController cen = SearchControllerConfig.newRam().newBuild() ;
		cen.index(session ->{
			assertEquals(WhitespaceAnalyzer.class, session.indexConfig().indexAnalyzer().getClass()) ;
			assertEquals(true, session.indexConfig().executorService() != null) ; // single
			return null ;
		}) ;
		
		cen.close(); 
	}

}
