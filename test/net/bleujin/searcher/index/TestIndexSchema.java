package net.bleujin.searcher.index;

import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.ion.framework.parse.gson.JsonObject;

public class TestIndexSchema extends TestCase {

	public void testAnalyzerPerField() throws Exception {
		SearchController sdc = SearchControllerConfig.newRam().defaultAnalyzer(new StandardAnalyzer()).newBuild() ;
		sdc.index(isession ->{
			assertEquals(StandardAnalyzer.class, isession.indexConfig().indexAnalyzer().getClass());
			isession.indexConfig().fieldAnalyzer("eng", new KeywordAnalyzer()).fieldAnalyzer("stan", new StandardAnalyzer());

			isession.indexConfig().fieldAnalyzer("eng", new KeywordAnalyzer()).fieldAnalyzer("stan", new StandardAnalyzer());
			assertEquals(PerFieldAnalyzerWrapper.class, isession.indexConfig().indexAnalyzer().getClass());

			return null ;
		}) ;

		sdc.destroySelf();
	}

	public void testSearch() throws Exception {
		SearchController sdc = SearchControllerConfig.newRam().defaultAnalyzer(new StandardAnalyzer()).newBuild() ;
		
		
		final JsonObject json = new JsonObject().put("eng", "bleujin").put("cjk", "태극기가 바람에").put("stan", "태극기가");
		sdc.index(isession -> {
			isession.indexConfig().fieldAnalyzer("eng", new KeywordAnalyzer()).fieldAnalyzer("cjk", new CJKAnalyzer()).fieldAnalyzer("stan", new StandardAnalyzer());
			return isession.newDocument("bleujin").add(json).insertVoid();
		});

		sdc.search(session ->{
			assertEquals(1, session.createRequest("eng:bleujin").find().size());

			assertEquals(0, session.createRequest("태극").find().size()); // in body builder
			assertEquals(1, session.createRequest("cjk:태극").find().size()); // used cjk

			assertEquals(0, session.createRequest("stan:태극").find().size()); // used cjk
			assertEquals(1, session.createRequest("stan:태극기가").find().size()); // used cjk
			return null ;
		}) ;
		
	}

}
