package net.bleujin.searcher.extend;

import java.io.IOException;

import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.search.DocHighlighter;
import net.bleujin.searcher.search.EachDocHandler;
import net.bleujin.searcher.search.EachDocIterator;
import net.bleujin.searcher.search.SearchResponse;
import net.ion.framework.util.Debug;

public class TestHighlight extends AbTestCase {

	public void testHighlight() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().text("tv", "The highlight package contains classes to provide keyword in context features typically used to highlight search terms in the text of results pages. The Highlight fox The highlight package contains classes to provide keyword in context features typically used to highlight search terms in the text of results pages. The Highlight").insert();
				isession.newDocument().text("tv", "slow fox white fox").insert();
				isession.newDocument().text("tv", "fast wolf red wolf").insert();

				return null;
			}
		});

		final Searcher searcher = sdc.newSearcher();
		final SearchResponse response = searcher.createRequest("").find();
		final DocHighlighter hl = response.createHighlighter("tv", "fox") ;
		
		response.eachDoc(new EachDocHandler<Void>() {
			public Void handle(EachDocIterator iter) {
				try {
					while (iter.hasNext()) {
						Debug.line(hl.asString(iter.next()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InvalidTokenOffsetsException e) {
					e.printStackTrace();
				}
				return null;
			}
		});

	}

}
