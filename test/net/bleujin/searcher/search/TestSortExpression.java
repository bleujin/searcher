package net.bleujin.searcher.search;

import java.util.List;

import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import net.bleujin.searcher.AbTestCase;
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
		assertEquals(0, HashMultimap.create().get(Type.LONG).size()) ;
		
		SearchConfig sconfig = sdc.search(session -> {return session.searchConfig() ;}) ;
		SortField[] sfs = new SortExpression(sconfig).parse("") ;
		
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0] == SortField.FIELD_SCORE) ;
	}
	
	public void testOneField() throws Exception {
		SearchConfig sconfig = sdc.search(session -> {return session.searchConfig() ;}) ;
		SortField[] sfs = new SortExpression(sconfig).parse("name desc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
	}

	
	public void testOneField2() throws Exception {
		SearchConfig sconfig = sdc.search(session -> {return session.searchConfig() ;}) ;
		SortField[] sfs = new SortExpression(sconfig).parse("name") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
		
	    sfs = new SortExpression(sconfig).parse("name asc") ;
		assertEquals(1, sfs.length) ;
		assertEquals(false, sfs[0].getReverse()) ;
	}


	
	public void testTwoField() throws Exception {
		SearchConfig sconfig = sdc.search(session -> {return session.searchConfig() ;}) ;
		SortField[] sfs = new SortExpression(sconfig).parse("name desc, address asc") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;

		assertEquals(false, sfs[1].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[1].getType()) ;

	}
	
	public void testKeyField() throws Exception {
		SearchConfig sconfig = sdc.search(session -> {return session.searchConfig() ;}) ;
		SortField[] sfs = new SortExpression(sconfig).parse("name desc, _score") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.STRING, sfs[0].getType()) ;

		assertEquals(SortField.FIELD_SCORE, sfs[1]) ;
	}
	
	public void testDefinedField() throws Exception {
		SearchConfig sconfig = sdc.search(session -> {return session.searchConfig().numFieldType("name") ;}) ;
		SortField[] sfs = new SortExpression(sconfig).parse("name desc, _score ") ;
		assertEquals(2, sfs.length) ;
		assertEquals(true, sfs[0].getReverse()) ;
		assertEquals(SortField.Type.LONG, sfs[0].getType()) ;

		assertEquals(SortField.FIELD_SCORE, sfs[1]) ;
	}
	
	
	public void testSort() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 30; i++) {
					isession.newDocument().keyword("name", "bleujin").number("int", RandomUtil.nextInt(200)).update(); 
				}
				return null;
			}
		}) ;
		
		sdc.search(session ->{
			SearchResponse result = session.createRequest("(name:bleujin) AND (int:[100 TO 120])").ascendingNum("int").offset(5).find() ;

			result.debugPrint("int"); 
			return null ;
		}) ;
	}


	public void testSortWhenTypeDefined() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 30; i++) {
					isession.newDocument().keyword("name", "bleujin").number("int", RandomUtil.nextInt(200)).update(); 
				}
				return null;
			}
		}) ;
		
		sdc.search(session ->{
			session.searchConfig().numFieldType("int") ;
			
			SearchResponse result = session.createRequest("(name:bleujin) AND (int:[100 TO 200])").ascending("int").offset(10).find() ;
			
			result.debugPrint("int");
			
			List<ReadDocument> docs = result.getDocument() ;
			Integer beforeValue = 0 ; // min
			for (ReadDocument doc : docs) {
				Integer currValue = Integer.valueOf(doc.asString("int")) ;
				assertEquals(true, beforeValue <= currValue) ;
				assertEquals(true, currValue >= 100) ;
				beforeValue = currValue ;
			}
			return null ;
		}) ;
	}


}
