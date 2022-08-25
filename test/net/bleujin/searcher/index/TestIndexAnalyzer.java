package net.bleujin.searcher.index;

import java.io.StringReader;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.bleujin.searcher.common.WriteDocument;
import net.ion.framework.util.Debug;

public class TestIndexAnalyzer extends TestCase {

	public void testDefaultIndexerAnalyzer() throws Exception {
		SearchController c = SearchControllerConfig.newRam().defaultAnalyzer(new StandardAnalyzer()).newBuild();
		c.index(isession ->{
			assertEquals(StandardAnalyzer.class, isession.indexConfig().indexAnalyzer().getClass());
			return null ;
		}) ;

		c.search(isession ->{
			assertEquals(StandardAnalyzer.class, isession.searchConfig().queryAnalyzer().getClass());
			return null ;
		}) ;
	}

	public void testAfterChangeIndexAnalyzer() throws Exception {
		SearchController c = SearchControllerConfig.newRam().defaultAnalyzer(new CJKAnalyzer()).newBuild();

		c.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				assertEquals(CJKAnalyzer.class, c.indexConfig().indexAnalyzer().getClass());
				
				WriteDocument doc = isession.newDocument("bleujin");
				doc.text("flag", "태극기가");
				isession.updateDocument(doc);
				return null;
			}
		});

		assertEquals(1, c.search("기가").size());
		c.destroySelf();
	}

	public void testAfterChangeQueryAnalyzer() throws Exception {
		SearchController c = SearchControllerConfig.newRam().newBuild();

		c.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(new CJKAnalyzer()) ;
				isession.newDocument("bleujin").text("flag", "태극기가").update() ;
				return null;
			}
		});
		
		c.search(session ->{
			assertEquals(WhitespaceAnalyzer.class, session.searchConfig().queryAnalyzer().getClass());
			assertEquals(1, session.createRequest("기가").find().size());
			return null ;
		}) ;

		c.destroySelf();
	}
	
	public void testAssignIndexerAnalyzer() throws Exception {
		SearchController c = SearchControllerConfig.newRam().defaultAnalyzer(new StandardAnalyzer()).newBuild();
		c.index(isession ->{
			isession.indexConfig().fieldAnalyzer("flag2", new CJKAnalyzer()) ; // flag2 analyzer
			isession.newDocument("bleujin").text("flag1", "태극기가").text("flag2", "태극기가").update() ;
			return null ;
		}) ;
		
		assertEquals(0, c.newSearcher().createRequest("flag1:태극").find().size()) ; // not found
		assertEquals(1, c.newSearcher().createRequest("flag2:태극").find().size()) ; // found

	}

}
