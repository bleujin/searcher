package net.bleujin.searcher.search;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.lucene.search.FieldCacheTermsFilter;

import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.ListUtil;
import net.ion.nsearcher.ISTestCase;
import net.ion.nsearcher.config.Central;
import net.ion.nsearcher.config.CentralConfig;
import net.ion.nsearcher.index.Indexer;

public class TestFilter extends ISTestCase {

	public void testTermFilterAtRequest() throws Exception {
		Central center = sampleTestDocument();

		// no filter
		Searcher newSearcher = center.newSearcher();

		SearchResponse result = newSearcher.createRequest("novision").offset(20).find();
		List<ReadDocument> docs = result.getDocument();
		assertEquals(6, docs.size());

		// set filter
		Filter filter = new FieldCacheTermsFilter("name", new String[] { "bleujin" });
		newSearcher.andFilter(filter);
		result = newSearcher.createRequest("novision").offset(20).find();
		docs = result.getDocument();
		assertEquals(1, docs.size());

//		Debug.debug(newSearcher.confirmFilterSet(searchRequest));

		// reset filter
		newSearcher = center.newSearcher();
		result = newSearcher.createRequest("novision").offset(20).find();
		docs = result.getDocument();
		assertEquals(6, docs.size());
	}

	public void testTermFilterAtSearcher() throws Exception {
		Central center = sampleTestDocument();

		// no filter
		Searcher newSearcher = center.newSearcher();
		final SearchRequest searchRequest = newSearcher.createRequest("novision").offset(20);
		SearchResponse result = searchRequest.find();
		List<ReadDocument> docs = result.getDocument();
		assertEquals(6, docs.size());

		// set filter
		Filter filter = new FieldCacheTermsFilter("name", new String[] { "bleujin" });
		newSearcher.andFilter(filter);
		result = searchRequest.find() ;
		docs = result.getDocument();
		assertEquals(1, docs.size());

		// reset filter
		newSearcher = center.newSearcher();
		result = newSearcher.search(searchRequest);
		docs = result.getDocument();
		assertEquals(6, docs.size());
	}

	public void testReopen() throws Exception {
		final Central c = CentralConfig.newRam().build() ;
		Searcher searcher = c.newSearcher();

		new Thread() {
			public void run() {
				try {
					for (int i : ListUtil.rangeNum(10)) {
						Indexer iw = c.newIndexer();
						final int idx = i ;
						iw.index(new IndexJob<Void>() {
							public Void handle(IndexSession isession) throws Exception {
								isession.insertDocument(isession.newDocument().add(MyField.keyword("name", "bleujin")).add(MyField.number("index", idx))) ;
								return null;
							}
						}) ;
						Thread.sleep(50) ;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}.start();

		Thread.sleep(200) ;
		for (int i : ListUtil.rangeNum(10)) {
//			searcher.reopen() ;
//			Debug.debug(searcher.search("bleujin").size());
			assertEquals(true, searcher.search("bleujin").size() > 0);
			Thread.sleep(50) ;
		}
	}
	
	
	
	

}
