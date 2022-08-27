package net.bleujin.searcher.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.MapUtil;

public class TestAnalyzerWrapper extends AbTestCase{

	public void testIndex() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(new PerFieldAnalyzerWrapper(new CJKAnalyzer(), MapUtil.<String, Analyzer>create("name", new KeywordAnalyzer()))) ;
				
				isession.newDocument("123").unknown("name", "태극기").insert() ;
				return null;
			}
		}) ;
		
		Searcher searcher = sdc.newSearcher() ;
		assertEquals(1, searcher.createRequest("").find().size()) ; 
		
		assertEquals(1, searcher.createRequest("name:태극기", new KeywordAnalyzer()).find().size()) ;
		assertEquals(1, searcher.createRequest("name:태극기", new CJKAnalyzer()).find().size()) ;
		assertEquals(1, searcher.createRequest("태극기", new CJKAnalyzer()).find().size()) ;
		assertEquals(0, searcher.createRequest("태극기", new KeywordAnalyzer()).find().size()) ;
		

		assertEquals(1, searcher.createRequest("name:태극기", new PerFieldAnalyzerWrapper(new CJKAnalyzer(), MapUtil.<String, Analyzer>create("name", new KeywordAnalyzer()))).find().size()) ;
		assertEquals(1, searcher.createRequest("태극기", new PerFieldAnalyzerWrapper(new CJKAnalyzer(), MapUtil.<String, Analyzer>create("name", new KeywordAnalyzer()))).find().size()) ;

	}
}
