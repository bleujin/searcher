package net.bleujin.searcher.search;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.SearchRequestWrapper;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.RandomUtil;

public class TestSort extends AbTestCase{
	
	public void testSortInSearch() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 20; i++) {
					isession.newDocument().number("idx", i).number("val", RandomUtil.nextInt(20)).insert() ;
				}
				return null;
			}
		}) ;
		
		sdc.search(session ->{
			session.searchConfig().numFieldType("val") ;
			
			Sort sort = session.createRequest("").sort("val=desc").sort() ;
			SortField sfield = sort.getSort()[0];
			assertEquals(true, sfield.getType() == Type.LONG) ;
			assertEquals(true, sfield.getReverse()) ;
			assertEquals("val", sfield.getField()) ;
			return null ;
		}) ;
	}
	
	
	
	public void testDescending() throws Exception {
		SearchRequestWrapper sreq = sdc.newSearcher().createRequest("");

		Sort sort = sreq.descending("val").sort() ;
		SortField sfield = sort.getSort()[0];
		assertEquals(true, sfield.getType() == Type.STRING) ;
		assertEquals(true, sfield.getReverse()) ;
		assertEquals("val", sfield.getField()) ;

	}

	public void testAscending() throws Exception {
		
		Sort sort = sdc.newSearcher().createRequest("").ascending("val").sort() ;
		SortField sfield = sort.getSort()[0];
		assertEquals(true, sfield.getType() == Type.STRING) ;
		assertEquals(false, sfield.getReverse()) ;
		assertEquals("val", sfield.getField()) ;

	}

	
	public void testLongSort() throws Exception {
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					isession.newDocument().number("index", RandomUtil.nextRandomInt(500)).insert() ; 
				}
				return null;
			}
		}) ;
		Searcher searcher = sdc.newSearcher();
		searcher.sconfig().numFieldType("index") ;
		SearchRequestWrapper sreq = searcher.createRequest("");
		
		sreq.sort("index desc").find().debugPrint("index");
	}
}
