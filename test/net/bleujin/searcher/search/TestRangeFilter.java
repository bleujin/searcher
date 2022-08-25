package net.bleujin.searcher.search;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.util.QueryUtil;

public class TestRangeFilter extends AbTestCase {
	
	public void testBetween() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().keyword("start", "20160117-000000").keyword("end", "99991231-000000").insert();
				isession.newDocument().keyword("start", "20160118-000000").keyword("end", "99991231-000000").insert();
				return null;
			}
		}) ;
		
		sdc.newSearcher().createRequest(QueryUtil.newBuilder()
					.gt("start", "20160117-000000")
					.andBuild()).find().debugPrint(); 
		
	}

}
