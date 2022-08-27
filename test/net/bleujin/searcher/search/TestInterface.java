package net.bleujin.searcher.search;

import net.bleujin.searcher.AbTestCase;
import net.ion.framework.util.Debug;

public class TestInterface extends AbTestCase{

	
	public void testFirst() throws Exception {
		sdc.index(isession -> {
			for (int i = 0; i < 5; i++) {
				isession.newDocument("bleujin" + i).unknown("id", "bleujin" + i).unknown("content", "hello world").unknown("age", 20).update() ;
			}
			return null;
		}) ;

		sdc.search(session ->{
			SearchResponse response = session.createRequest("20").descending("id").find() ;
			response.debugPrint() ;
			return null ;
		}) ;
		
	}
}
