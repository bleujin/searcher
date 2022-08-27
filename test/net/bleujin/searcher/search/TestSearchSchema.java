package net.bleujin.searcher.search;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;

public class TestSearchSchema extends AbTestCase{

	public void testSearch() throws Exception {
		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("eng", new KeywordAnalyzer());
		mapAnal.put("cjk", new CJKAnalyzer());
		mapAnal.put("stan", new StandardAnalyzer());
		
		final Analyzer sanalyzer = new PerFieldAnalyzerWrapper(new KoreanAnalyzer(), mapAnal);
		
		
		final JsonObject json = new JsonObject().put("eng", "bleujin").put("cjk", "태극기가 바람에").put("stan", "태극기가") ;
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(sanalyzer) ;
				return isession.newDocument("bleujin").add(json).insertVoid() ;
			}
		}) ;
		
		sdc.search(session ->{
			session.searchConfig().queryAnalyzer(sanalyzer) ;
			assertEquals(1, session.createRequest("eng:bleujin").find().size()) ;
			
			assertEquals(1, session.createRequest("태극").find().size()) ; // in body builder
			assertEquals(1, session.createRequest("cjk:태극기가").find().size()) ; // used cjk
			
			assertEquals(0, session.createRequest("stan:태극").find().size()) ; // used stan
			assertEquals(1, session.createRequest("stan:태극기가").find().size()) ; // used stan
			
			return null ;
		}) ;
		
	}

}
