package net.bleujin.searcher.search;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.ion.framework.util.Debug;

public class TestRequestSelection extends AbTestCase {

	
	public void setUp() throws Exception {
		super.setUp() ;
	}
	
	public void testSelection() throws Exception {
		
		sdc.index(IndexJob.SAMPLE_INSERT) ;
		
		Searcher searcher = sdc.newSearcher() ;
		
		ReadDocument doc = searcher.createRequest("").selections("name").findOne() ;
		Debug.debug(doc.asString("name"), doc.asString("age"), doc, doc.IdString()) ; 
		
	}
	
}
