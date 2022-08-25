package net.bleujin.searcher.search;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import net.bleujin.searcher.AbTestCase;
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
			Sort sort = session.createRequest("").sort("val=desc").sort() ;
			SortField sfield = sort.getSort()[0];
			assertEquals(true, sfield.getType() == Type.LONG) ;
			assertEquals(true, sfield.getReverse()) ;
			assertEquals("val", sfield.getField()) ;
			return null ;
		}) ;
	}
	
	
	
	public void testDescending() throws Exception {
		
		Sort sort = sdc.newSearcher().createRequest("").descending("val").sort() ;
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
					isession.newDocument().number("index", RandomUtil.nextLong()).insert() ; 
				}
				return null;
			}
		}) ;
		
		sdc.newSearcher().createRequest("").sort("index desc").find().debugPrint("index");
	}
}
