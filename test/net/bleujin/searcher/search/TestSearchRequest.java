package net.bleujin.searcher.search;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.nsearcher.search.filter.FilterUtil;
import net.ion.nsearcher.search.filter.TermFilter;

public class TestSearchRequest extends AbTestCase {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").keyword("name", "bleujin").number("int", 1).unknown("age", 30).text("explain", "my name is bleujin").update() ;
				isession.newDocument("hero").keyword("name", "hero").number("int", 2).unknown("age", 25).text("explain", "my address is seoul").insert() ;
				isession.newDocument("jin").keyword("name", "jin").number("int", 3).unknown("age", "35").text("explain", "this is not evil and not good").insert(); ;
				isession.newDocument("jini").keyword("name", "jini").number("int", 4).unknown("age", 40).text("explain", "orange for oracle").update(); ;
				return null;
			}
		}) ;
	}
	
	public void testGetField() throws Exception {
		ReadDocument doc = searcher.createRequest("bleujin").findOne();
		String[] fields = doc.fieldNames() ;
		assertEquals(3, fields.length); // except text field(default strategy not store texttype)
	}
	
	public void testTerm() throws Exception {
		searcher.createRequest("").setFilter(QueryUtil.newBuilder().term("name", "jin").gte("int", 3).andBuild()).find().debugPrint();
	}
	
	public void testSearchFilter() throws Exception {
		assertEquals(2, searcher.andFilter(QueryUtil.newBuilder().gte("int", 3).andBuild()).createRequest("").find().size()) ;
		assertEquals(2, searcher.createRequest("").find().size());
		
		assertEquals(4, central.newSearcher().createRequest("").find().size()) ;
	}
	
	public void testSearchFilterApplied() throws Exception {
//		central.newSearcher().andFilter(new TermFilter("name", "bleujin")).createRequest("").find().debugPrint();
		central.newSearcher().andFilter(new TermFilter("name", "bleujin")).createRequest("")
			.setFilter(QueryUtil.newBuilder().term("age", "30").gte("age", 40).andBuild()).find().debugPrint();
	}
	
	public void testSort() throws Exception {
		central.newSearcher().createRequest("").ascendingNum("age").find().debugPrint("age");
	}
	
}
