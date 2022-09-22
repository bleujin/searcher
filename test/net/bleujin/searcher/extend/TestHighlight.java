package net.bleujin.searcher.extend;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.search.SearchResponse;
import net.ion.framework.util.Debug;

public class TestHighlight extends AbTestCase {

	public void testHighlight() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().text("tv",
						"The highlight package contains classes to provide keyword in context features typically used to highlight search terms in the text of results pages. The Highlight fox The highlight package contains classes to provide keyword in context features typically used to highlight search terms in the text of results pages. The Highlight")
						.insert();
				isession.newDocument().text("tv", "slow fox white fox").insert();
				isession.newDocument().text("tv", "fast wolf red wolf").insert();

				return null;
			}
		});
		
		
		sdc.search(session ->{
			SearchResponse response = session.createRequest("tv:fox").highlight("tv", "fox").find() ;

			response.eachDoc(iter -> {
				while (iter.hasNext()) {
					ReadDocument rdoc = iter.next();
					Debug.line(rdoc.highlightString(), rdoc.asString("tv")) ;
				}
				return null;
			});
			
			return null ;
		}) ;
		
		

	}

}
