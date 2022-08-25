package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.ListUtil;

public class TestSearcherPaging extends AbTestCase {

	public void testSkip() throws Exception {
		sdc.index(isession -> {
			List<WriteDocument> docs = ListUtil.newList();
			for (int i : ListUtil.rangeNum(100)) {
				docs.add(isession.newDocument().unknown("idx", i).unknown("name", "bleujin"));
			}
			Collections.shuffle(docs);

			for (WriteDocument doc : docs) {
				isession.insertDocument(doc);
			}

			return null;
		});
		
		List<ReadDocument> list = sdc.search(session->{
			SearchResponse response = session.createRequest("bleujin").descending("idx _number").skip(4).offset(3).find();
			assertEquals(3, response.size());
			assertEquals(100, response.totalCount());

			response.debugPrint();
			return response.getDocument();
		}) ;
		

		assertEquals("95", list.get(0).asString("idx"));
		assertEquals("94", list.get(1).asString("idx"));
		assertEquals("93", list.get(2).asString("idx"));
	}

	public void testStringOrder() throws Exception {
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws IOException {
				List<WriteDocument> docs = ListUtil.newList();
				docs.add(isession.newDocument().keyword("idx", "2"));
				docs.add(isession.newDocument().keyword("idx", "10"));
				docs.add(isession.newDocument().keyword("idx", "9"));
				Collections.shuffle(docs);

				for (WriteDocument doc : docs) {
					isession.insertDocument(doc);
				}
				return null;
			}
		});
		
		List<ReadDocument> list = sdc.search(session->{
			SearchResponse response = session.createRequest("").descending("idx").find();
			assertEquals(3, response.size());
			response.debugPrint();
			return response.getDocument();

		}) ;

		assertEquals("9", list.get(0).asString("idx"));
		assertEquals("2", list.get(1).asString("idx"));
		assertEquals("10", list.get(2).asString("idx"));
	}

}
