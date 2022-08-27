package net.bleujin.searcher.search;

import java.util.List;

import org.apache.lucene.search.SortField;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.RandomUtil;

public class TestSortExpression extends AbTestCase{
	
	@Override
	protected void tearDown() throws Exception {
		sdc.index(DELETE_ALL) ;
		super.tearDown();
	}
	
	public void testEmpty() throws Exception {
		
		SortField[] sfs = new SortExpression().parseTest("") ;
		
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0] == SortField.FIELD_SCORE) ;
	}
	
	public void testOneField() throws Exception {
		SortField[] sfs = new SortExpression().parseTest("name _number desc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.DOUBLE, sfs[0].getType()) ;
	}

	
	public void testOneField2() throws Exception {
		SortField[] sfs = new SortExpression().parseTest("name _number") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.DOUBLE, sfs[0].getType()) ;
	}

	public void testOneField3() throws Exception {
		SortField[] sfs = new SortExpression().parseTest("name asc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;
	}


	
	public void testTwoField() throws Exception {
		SortField[] sfs = new SortExpression().parseTest("name desc" ,"address asc") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;

		assertEquals(false, sfs[1].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[1].getType()) ;

	}
	
	public void testKeyField() throws Exception {
		SortField[] sfs = new SortExpression().parseTest("name desc", "_score") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;

		assertEquals(SortField.FIELD_SCORE, sfs[1]) ;
	}
	
	public void testAtSearchRequest() throws Exception {
		sdc.index(TEST100) ;

		Searcher newSearcher = sdc.newSearcher() ;
		SearchResponse result = newSearcher.createRequest("(name:bleujin) AND (int:[100 TO 200])").ascending("int").offset(5).find() ;

		result.debugPrint(); 
	}


	public void testSort() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					isession.newDocument().keyword("name", "bleuji").number("int", RandomUtil.nextInt(300)).update(); 
				}
				return null;
			}
		}) ;
		
		Searcher newSearcher = sdc.newSearcher() ;
		SearchResponse result = newSearcher.createRequest("(name:bleujin) AND (int:[100 TO 200])").descending("int").offset(5).find() ;
		
		result.debugPrint("int");
		
		
		List<ReadDocument> docs = result.getDocument() ;
		Integer beforeValue = 200 ; // max
		for (ReadDocument doc : docs) {
			Integer currValue = Integer.valueOf(doc.asString("int")) ;
			assertEquals(true, beforeValue >= currValue) ;
			beforeValue = currValue ;
		}
	}


}
