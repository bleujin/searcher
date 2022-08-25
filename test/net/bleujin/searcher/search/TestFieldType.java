package net.bleujin.searcher.search;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;

public class TestFieldType extends AbTestCase {

	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.deleteAll() ;
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20L).stext("explain", "hello bleujin").update() ;
				isession.newDocument("hero").keyword("name", "hero").number("age", 30).text("explain", "hi hero").update() ;
				isession.newDocument("jin").keyword("name", "jin").number("age", 7).text("explain", "namaste jin").update() ;
				return null;
			}
		}) ;
	}
	
	public void testNumericSort() throws Exception {

		sdc.search(session->{
			session.createRequest("").ascendingNum("age").find().debugPrint(); 
			session.createRequest("").sort("age").find().debugPrint(); 
			return null ;
		}) ;
	}
	
	public void testCaseSensitive() throws Exception {
		assertEquals(1, sdc.search("explain", "bleujin").size()) ;
		assertEquals(0, sdc.search("Explain", "bleujin").size()) ;
	}
	
	public void testLongSaved() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument wdoc = isession.loadDocument("bleujin", true, "age").keyword("nfield", "new").update() ;
				return null;
			}
		}) ;
		sdc.search("age", "" + 20).debugPrint();
		sdc.search("age:[20 TO 20]").debugPrint();
		sdc.search("hello").debugPrint();

		assertEquals(1, sdc.search("age", "" + 20).size()) ;
		assertEquals(1, sdc.search("age:[20 TO 20]").size()) ;
		assertEquals(1, sdc.search("hello").size()) ;

		//		cen.newSearcher().createRequestByTerm("explain", "bleujin").find().debugPrint();
	}
}
