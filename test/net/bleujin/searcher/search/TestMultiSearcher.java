package net.bleujin.searcher.search;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;

public class TestMultiSearcher extends AbTestCase {

	private SearchController c1;
	private SearchController c2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.c1 =  SearchControllerConfig.newRam().build(OpenMode.CREATE_OR_APPEND);
		c1.index(createIndexJob("jin", 3));

		this.c2 =  SearchControllerConfig.newRam().build(OpenMode.CREATE_OR_APPEND);
		c2.index(createIndexJob("hero", 2));
	}
	
	@Override
	protected void tearDown() throws Exception {
		c1.close(); 
		c2.close(); 
		super.tearDown();
	}

	public void testCreate() throws Exception {
		assertEquals(3, c1.newSearcher().search("").size());
		assertEquals(2, c2.newSearcher().search("").size());
	}

	public void testMultiSearchFrom() throws Exception {
		
	
		// c1.newSearcher(c2).createRequest("").find().debugPrint(); 
		assertEquals(5, c1.newSearcher(c2).createRequest("").find().totalCount()) ; // 3+2
		assertEquals(7, c1.newSearcher(c2, c2).createRequest("").find().totalCount()) ; // 3+2+2
		
		
		c1.newSearcher(c2).createRequest("").skip(3).find().debugPrint(); 
	}
	
}
	
