package net.bleujin.searcher.search;

import java.util.List;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.search.processor.StdOutProcessor;

public class TestSearcher extends AbTestCase {

	private Searcher searcher;
	public void setUp() throws Exception {
		super.setUp(); 
		sdc.index(TEST100);
		searcher = sdc.newSearcher() ;
	}

	public void testSearchCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search("bleujin");
		List<ReadDocument> docs = result.getDocument();
		assertEquals(25, result.size());
	}
	

	public void testSearchFieldCount() throws Exception {
		searcher.addPostListener(new StdOutProcessor()) ;
		SearchResponse result = searcher.search("mysub:(bleujin novision) OR subject:(bleujin novision)");
		
		result.debugPrint(); 
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
		List<ReadDocument> docs = searcher.createRequest("").find().getDocument();
		assertEquals(100, docs.size()) ;
	}
}

