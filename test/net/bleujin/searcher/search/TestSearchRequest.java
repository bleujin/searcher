package net.bleujin.searcher.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.util.QueryUtil;

public class TestSearchRequest extends AbTestCase {

	
	private Searcher searcher;

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
		this.searcher = sdc.newSearcher() ;
	}
	
	public void testGetField() throws Exception {
		ReadDocument doc = searcher.createRequest("bleujin").findOne();
		String[] fields = doc.fieldNames() ;
		assertEquals(4, fields.length); 
	}
	
	public void testRequestByKey() throws Exception {
		ReadDocument doc = searcher.createRequestByKey("bleujin").findOne();
		assertEquals("bleujin", doc.IdString());
	}
	
	public void testTerm() throws Exception {
		searcher.createRequest(QueryUtil.newBuilder().gte("int", 3).andBuild()).descendingNum("int").find().debugPrint("int");
	}
	
	public void testSearchQuery() throws Exception {
		assertEquals(2, searcher.createRequest(QueryUtil.newBuilder().gte("int", 3).andBuild()).find().size()) ;
		assertEquals(4, sdc.newSearcher().createRequest("").find().size()) ;
	}
	
	public void testSearchFilterApplied() throws Exception {
		sdc.newSearcher().createRequest(QueryUtil.newBuilder().term("name", "bleujin").term("age", "30").gte("age", 40).andBuild()).find().debugPrint();
	}
	
	public void testSort() throws Exception {
		sdc.newSearcher().createRequest("").ascendingNum("age").find().debugPrint("age");
	}
	
	
	public void testMultiFieldSearch() throws Exception {
		Analyzer analyzer = new StandardAnalyzer() ;
		Query query = MultiFieldQueryParser.parse("jin", new String[]{"name","explain"}, new Occur[] {Occur.MUST, Occur.SHOULD}, analyzer);
		
		sdc.search(session ->{
			session.createRequest(query).find().debugPrint() ;
			return null ;
		}) ;
	}
}
