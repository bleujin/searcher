package net.bleujin.searcher.search;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;

public class TestTotalCount extends AbTestCase {

	public void testAfterReloadingIndex() throws Exception {
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				isession.insertDocument(isession.newDocument().keyword("name", "bleujin")) ;
				return null;
			}
		}) ;

		SearchResponse response = sdc.search(session ->{
			return session.createRequest("").find();
		}) ;
		

		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				isession.insertDocument(isession.newDocument().keyword("name", "hero")) ;
				return null;
			}
		}) ;

		assertEquals(1, response.totalCount()) ;
	}
}
