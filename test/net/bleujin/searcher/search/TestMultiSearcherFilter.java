package net.bleujin.searcher.search;

import junit.framework.TestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.search.processor.PostProcessor;
import net.ion.framework.util.Debug;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.IndexJobs;
import net.ion.nsearcher.search.filter.TermFilter;

public class TestMultiSearcherFilter extends TestCase {

	private Central c1;
	private Central c2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.c1 = CentralConfig.newRam().build();
		c1.newIndexer().index(IndexJobs.create("jin", 3));
		c1.newIndexer().index(IndexJobs.create("hero", 3));

		this.c2 = CentralConfig.newRam().build();
		c2.newIndexer().index(IndexJobs.create("hero", 2));
	}
	
	
	
	public void testAddFilter() throws Exception {
		Searcher searcher = c1.newSearcher(c2) ;
		searcher.andFilter(new TermFilter("prefix", "hero")).search("").debugPrint();

		Debug.line();
		c1.newIndexer().index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("hero3").keyword("prefix", "hero").number("idx", 3).insert();
				return null;
			}
		}) ;

		searcher.addPostListener(new PostProcessor() {
			@Override
			public void postNotify(SearchRequest sreq, SearchResponse sres) {
				Debug.line(sreq.query());
			}
		});
		
		SearchResponse response = searcher.search("");
		response.debugPrint(); 
	}
	
	public void testResetFilter() throws Exception {
		Searcher before = c1.newSearcher(c2) ;
		assertEquals(5, before.andFilter(new TermFilter("prefix", "hero")).search("").size()) ;
		
		
		Searcher after = c1.newSearcher(c2) ;
		after.search("").debugPrint();
	}
	
}
