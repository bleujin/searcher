package net.bleujin.searcher.search;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;

public class TestSearchResponse extends AbTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 100 ; i++) {
					isession.newDocument("bleujin_" + i).keyword("name", "bleujin").number("int", i).unknown("age", 30).text("explain", "my name is bleujin").update() ;
				}
				return null;
			}
		}) ;
	}
	
	public void testPageList() throws Exception {
		final SearchResponse sres = sdc.newSearcher().createRequest("int:[10 TO 50]").sort("int desc").find();
		for(ReadDocument rdoc : sres.getDocument(Page.create(5, 2, 5))) {
			Debug.line(rdoc);
		}
		
		ReadDocument doc15 = sres.documentById("bleujin_23") ;
		
		Debug.line(sres.preDocBy(doc15), sres.nextDocBy(doc15)) ;
		
	}
	
	public void testInOpern() throws Exception {
		BooleanQuery bq = new BooleanQuery() ;
		for (int artid : new int[]{30, 40, 50}) {
			bq.add(new TermQuery(new Term("int", ""+artid)), Occur.SHOULD);
		}
		
		sdc.newSearcher().createRequest(bq).find().debugPrint();
	}
	
	
	

}
