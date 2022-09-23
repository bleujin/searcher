package net.bleujin.searcher.search;

import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.util.QueryUtil;
import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.SetUtil;

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
	
	public void testInOpern() throws Exception {
		Set<Query>querys = SetUtil.newSyncSet() ;
		for (int artid : new int[]{30, 40, 50}) {
			querys.add(new TermQuery(new Term("int", ""+artid)));
		}

		sdc.newSearcher().createRequest(QueryUtil.or(querys)).find().debugPrint();
	}
	

}
