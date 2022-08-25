package net.bleujin.searcher.search;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.search.processor.StdOutProcessor;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;

public class TestSearcher extends ISTestCase {

	private Searcher searcher;
	private Central central = null ; 
	public void setUp() throws Exception {
		central = sampleTestDocument() ;
		searcher = central.newSearcher() ;
	}

	public void tearDown() throws Exception {
		central.destroySelf() ;
	}


	public void testSearchCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search("bleujin");
		List<ReadDocument> docs = result.getDocument();
		assertEquals(6, result.size());
	}
	

	public void testSearchFieldCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search("mysub:(bleujin novision) OR subject:(bleujin novision)");
		List<ReadDocument> docs = result.getDocument();
//		for (MyDocument doc : docs) {
//			Debug.line(doc) ;
//		}
	}

	public void testPage() throws Exception {
		SearchResponse result = searcher.createRequest("bleujin").offset(3).find() ;
		
		List<ReadDocument> docs = result.getDocument() ;
		assertEquals(3, docs.size()) ;
	}

	public void testSkip() throws Exception {
		SearchResponse result = searcher.createRequest("").skip(2).offset(3).find() ;
		
		List<ReadDocument> docs = result.getDocument() ;
		assertEquals(3, docs.size()) ;
	}
	

	public void testAllDoc() throws Exception {
		List<ReadDocument> docs = searcher.search("").getDocument();
		assertEquals(24, docs.size()) ;
	}
}

