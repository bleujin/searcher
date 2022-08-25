package net.bleujin.searcher.search;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;

public class TestSearchSort extends AbTestCase{

	
	public void setUp() throws Exception {
		super.setUp();

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 21 ; i++) {
					isession.newDocument("" + i).keyword("str", "" + i).number("num", i).insert() ;
				}
				return null;
			}
		}) ;
	}

	public void testAscendingString() throws Exception {
		SearchResponse result = sdc.newSearcher().createRequest("").ascending("str").find() ;
		assertEquals(0, result.getDocument().get(0).asLong("num", 0));
		assertEquals(1, result.getDocument().get(1).asLong("num", 0));
		assertEquals(10, result.getDocument().get(2).asLong("num", 0));
	}
	
	public void testDescendingString() throws Exception {
		SearchResponse result = sdc.newSearcher().createRequest("").descending("str").find() ;
		assertEquals(9, result.getDocument().get(0).asLong("num", 0));
		assertEquals(8, result.getDocument().get(1).asLong("num", 0));
		assertEquals(7, result.getDocument().get(2).asLong("num", 0));
	}

	
	public void testAscendingNumber() throws Exception {
		SearchResponse result = sdc.newSearcher().createRequest("").ascendingNum("num").find() ;
		assertEquals(0, result.getDocument().get(0).asLong("num", 0));
		assertEquals(1, result.getDocument().get(1).asLong("num", 0));
		assertEquals(2, result.getDocument().get(2).asLong("num", 0));
	}
	

	public void testDescendingNumber() throws Exception {
		SearchResponse result = sdc.newSearcher().createRequest("").descendingNum("num").find() ;
		assertEquals(20, result.getDocument().get(0).asLong("num", 0));
		assertEquals(19, result.getDocument().get(1).asLong("num", 0));
		assertEquals(18, result.getDocument().get(2).asLong("num", 0));
	}
	
	
	
	
	
}
