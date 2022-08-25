package net.bleujin.searcher.index;

import java.io.IOException;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.SearchConstant;
import net.bleujin.searcher.common.WriteDocument;
import net.ion.framework.util.Debug;

public class TestRollback extends AbTestCase {

	public void testFindByKey() throws IOException {
		sdc.index(SAMPLE) ;
		
		sdc.search(SearchConstant.DocKey, "bleujin").debugPrint();
	}
	
	public void testNormalUpdate() throws Exception {
		sdc.index(SAMPLE) ;
		assertEquals(1, sdc.newSearcher().createRequest("name:bleujin").find().size()) ; 
		
		
		// sdc.search("").debugPrint();
		sdc.index(isession -> {
			WriteDocument doc = isession.loadDocument("bleujin").keyword("name", "jj").update() ;
			return null;
		});

		sdc.search(isession->{
			isession.createRequest("").find().debugPrint("name", "age", "mod") ;
			return null ;
		}) ;
		
		
		
//		sdc.search("").debugPrint("name", "age", "mod"); 
		
//		sdc.search("").debugPrint();
//		
//		
//		sdc.newSearcher().createRequest("name:bleuhero").find().debugPrint("name");
		assertEquals(1, sdc.newSearcher().createRequest("name:jj").find().size()) ;
		
		
		
	}
	
	public void testUpdateRollback() throws Exception {
		sdc.index(SAMPLE) ;
		assertEquals(1, sdc.newSearcher().createRequest("name:bleujin").find().size()) ; 
		
		sdc.index(isession -> {
			isession.loadDocument("bleujin").keyword("name", "bleuher").update() ;
			isession.rollback();
			return null;
		});

		assertEquals(1, sdc.search("name:bleujin").size()) ;
	}

	public void testDeleteRollback() throws Exception {
		sdc.index(SAMPLE) ;
		assertEquals(1, sdc.newSearcher().createRequest("name:bleujin").find().size()) ; 
		
		sdc.index(isession -> {
			isession.deleteAll() ;
			isession.rollback();
			return null;
		});

		assertEquals(1, sdc.search("name:bleujin").size()) ;
	}

	public void testExceptionRollback() throws Exception {
		sdc.index(SAMPLE) ;
		assertEquals(1, sdc.search("name:bleujin").size()) ; 
		
		try {
		sdc.index(isession -> {
			isession.deleteAll() ;
			throw new Exception("") ;
		});
		} catch(Exception ignore) {
			
		}

		assertEquals(1, sdc.search("name:bleujin").size()) ;
	}

}
