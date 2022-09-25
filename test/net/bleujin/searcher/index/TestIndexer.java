package net.bleujin.searcher.index;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import junit.framework.TestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.bleujin.searcher.search.SearchResponse;
import net.ion.framework.parse.gson.JsonObject;

public class TestIndexer extends TestCase {

	private SearchController sdc;

	protected void setUp() throws Exception {
		super.setUp();
		this.sdc = SearchControllerConfig.newRam().defaultAnalyzer(new StandardAnalyzer()).newBuild();
	}

	protected void tearDown() throws Exception {
		sdc.close();
		super.tearDown();
	}

	public void testCreate() throws Exception {
		assertEquals(StandardAnalyzer.class, sdc.sconfig().defaultAnalyzer().getClass());
	}

	public void testAfterIndex() throws Exception {
		assertEquals(0, sdc.search("").size());

		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws IOException {
				isession.newDocument().insert();
				return null;
			}
		});

		SearchResponse sr = sdc.search("");
		assertEquals(1, sr.size());
	}

	public void testIndexJson() throws Exception {
		final JsonObject json = JsonObject.fromString("{name:'bleujin', age:20}");

		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws IOException {
				isession.newDocument().add(json).insert();
				return null;
			}
		});

		assertEquals(1, sdc.search("name:bleujin").size());
		assertEquals(1, sdc.search("age:20").size());

		assertEquals(1, sdc.search("bleujin").size());
		assertEquals(1, sdc.search("20").size());
	}

}
