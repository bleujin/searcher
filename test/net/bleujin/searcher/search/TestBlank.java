package net.bleujin.searcher.search;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.ion.framework.util.Debug;

public class TestBlank extends TestCase{

	public void testBlankSearcher() throws Exception {
		SearchController c = SearchControllerConfig.newRam().newBuild() ;
		c.search(session ->{
			session.createRequest("").find().debugPrint() ;
			return null ;
		}) ;
	}
	
	public void testSearch() throws Exception {
		final SearchController cen = SearchControllerConfig.newRam().newBuild() ;

		cen.index(isession -> {
			isession.indexConfig().indexAnalyzer(new CJKAnalyzer()) ;
			isession.deleteAll() ;
			isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20L).text("explain", "hello bleujin").update() ;
			isession.newDocument("hero").keyword("name", "hero").number("age", 30).text("explain", "hi hero").update() ;
			isession.newDocument("jin").keyword("name", "jin").number("age", 7).text("explain", "namaste jin").update() ;
			return null;
		}) ;
		
		cen.search(session ->{
			SearchRequest request = session.createRequest("bleujin") ;
			Debug.line(request.query());
			request.find().debugPrint();
			return null ;
		}) ;
		
	}

}
