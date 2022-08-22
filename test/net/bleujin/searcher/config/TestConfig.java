package net.bleujin.searcher.config;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.search.processor.StdOutProcessor;

public class TestConfig extends TestCase {

	
	public void testCreateCentral() throws Exception {
		SearchController sc = SearchControllerConfig.newRam().newBuild() ;
		sc.destroySelf(); 
	}
	
	public void testMakeIndexer() throws Exception {
		SearchController sc = SearchControllerConfig.newRam().newBuild() ;
		sc.index(IndexJob.SAMPLE_INSERT) ;
		sc.destroySelf(); 
	}
	
	public void testMakeSearcher() throws Exception {
		SearchController sc = SearchControllerConfig.newRam().newBuild() ;
		
		sc.index(IndexJob.SAMPLE_INSERT) ;
		
		sc.search(session ->{
			session.searchConfig().addPostListener(new StdOutProcessor()) ;
			assertEquals(true, session.createRequest("bleujin").find().getDocument().size() > 0) ;	
			return null ;
		}) ;
		
		sc.close(); 
	}
	

}

