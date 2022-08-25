package net.bleujin.searcher.index;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.MatchAllDocsQuery;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectId;

public class TestIndexAsync extends TestCase {

	public void testRunAsync() throws Exception {
		SearchController sdc = SearchControllerConfig.newLocalFile("./resource/temp").defaultExecutorService(Executors.newCachedThreadPool()).build(OpenMode.CREATE);

		List<Future> futures = ListUtil.newList() ;
		for (int i = 0; i < 3; i++) {
			Future<Void> future = sdc.indexAsync(new IndexJob<Void>() {
				@Override
				public Void handle(IndexSession isession) throws Exception {
					for (int j = 0; j < 4; j++) {
						String key = new ObjectId().toString();
						isession.newDocument(key).keyword("key", "key").text("name", "bleujin").number("age", j).insert();
					}
					return null;
				}
			});
			futures.add(future) ;
		}
		Thread.sleep(500) ;
		sdc.search(new MatchAllDocsQuery()).debugPrint() ;
		
		futures.forEach(future -> {
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}) ;


		assertEquals(12, sdc.search(new MatchAllDocsQuery()).totalCount()) ;
		
		sdc.destroySelf(); // remove 
	}
	
	public void testSearch() throws Exception {
		SearchController sdc = SearchControllerConfig.newLocalFile("./resource/temp").build(OpenMode.CREATE_OR_APPEND);
		sdc.search(new MatchAllDocsQuery()).debugPrint() ;
		
		sdc.close() ;
	}
}
