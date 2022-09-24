package net.bleujin.searcher.index;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import net.bleujin.searcher.AbTestCase;
import net.ion.framework.util.Debug;

public class TestIndexTran extends AbTestCase{
	
	public void testIndexTran() throws Exception {
		
		Object result = sdc.indexTran( isession ->{
			isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).updateVoid() ;
			isession.newDocument("jin").keyword("name", "jin").number("age", 30).updateVoid() ;
			isession.newDocument("hero").keyword("name", "hero").number("age", 40).updateVoid() ;
			
			return "indexed" ;
		}).exceptionally(ex ->{
			ex.printStackTrace() ;
			return "exception" ;
		}).thenApply( rtn ->{
			try {
				 return "confirmed " + sdc.newSearcher().createRequest("").find().totalCount() ;
			} catch (IOException | ParseException e) {
				return "fail" ;
			}
		}).get();
		
		assertEquals("confirmed 3", result);
	}
	
}
