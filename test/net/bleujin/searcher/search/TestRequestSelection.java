package net.bleujin.searcher.search;

import java.util.List;

import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;

public class TestRequestSelection extends ISTestCase {

	
	private Central cen = null ; 
	public void setUp() throws Exception {
		super.setUp() ;
		cen = sampleTestDocument() ;
	}
	
	public void testSelection() throws Exception {
		Searcher searcher = cen.newSearcher() ;
		
		SearchResponse response = searcher.createRequest("").selections("name").find();
		
		List<ReadDocument> docs = response.getDocument();
		for (ReadDocument doc : docs) {
			Debug.debug(doc.asString("name"), doc.asString("int"), doc) ;
		}
		
	}
	
}
