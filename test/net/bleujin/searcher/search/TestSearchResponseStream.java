package net.bleujin.searcher.search;

import java.io.IOException;

import net.bleujin.searcher.AbTestCase;

public class TestSearchResponseStream extends AbTestCase{

	
	public void testStreamFirst() throws Exception {
		sdc.index(SAMPLE) ;
		
		// readStream(select && filtering)
		sdc.newSearcher().createRequest("").find().readStream().gte("age", 20L).eq("name", "bleujin").forEach(System.out::println);

	
		
		// writeStream(update )
		sdc.index(isession ->{
			isession.createRequest("").find().writeStream(isession).gte("age", 30L).forEach(wdoc ->{
				try {
					wdoc.keyword("name", "new " + wdoc.asString("name")).updateVoid() ;
				} catch (IOException ignore) {
				}
			});
			
			return null ;
		}) ;

		sdc.newSearcher().createRequest("").find().debugPrint("name", "age");
		
	}
}
