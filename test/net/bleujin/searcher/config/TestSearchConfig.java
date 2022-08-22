package net.bleujin.searcher.config;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.ion.framework.util.WithinThreadExecutor;

public class TestSearchConfig extends TestCase{

	public void testDefault() throws Exception {
		SearchController sc = SearchControllerConfig.newRam().newBuild() ;
		
		sc.search(session ->{
			assertEquals(WhitespaceAnalyzer.class, session.searchConfig().queryAnalyzer().getClass()) ;
			assertEquals(WithinThreadExecutor.class, session.searchConfig().executorService().getClass()) ;
			
			return null ;
		}) ;
		
		
	}
	
	
}
