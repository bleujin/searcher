package net.bleujin.searcher.index;

import java.io.IOException;
import java.util.Map;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.reader.InfoHandler;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

public class TestIndexSession extends AbTestCase {

	public void testAppend() throws Exception {
		final SearchController tsdc = SearchControllerConfig.newRam().newBuild() ;

		tsdc.indexAsync(new AddFiveEntryJob("hero")).get();
		sdc.indexAsync(new AddFiveEntryJob("bleujin")).get();

		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession session) throws Exception {
				session.appendFrom(tsdc.dir());
				return null;
			}
		});

		assertEquals(10, sdc.newSearcher().createRequest("").find().size());
	}

	public void testCommitUserData() throws Exception {
		sdc.index(new AddFiveEntryJob("hero"));
		
		Map<String, String> map = sdc.info(InfoHandler.COMMIT_DATA);
		assertEquals(true, map.containsKey("lastmodified"));
		assertEquals(true, map.containsKey("name"));
	}

	public void testBeforeCommit() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).update();

				Debug.line(isession.newDocument("bleujin").fields());
				return null;
			}
		});
	}

}

class AddFiveEntryJob implements IndexJob<Boolean> {
	String name;

	AddFiveEntryJob(String name) {
		this.name = name;
	}

	public Boolean handle(IndexSession isession) throws IOException {
		isession.indexConfig().commitData("name", name).commitData("lastmodified", DateUtil.currentDateString()) ;
		for (int i : ListUtil.rangeNum(5)) {
			isession.insertDocument(isession.newDocument().add(MyField.number("index", i)).add(MyField.keyword("name", name)));
		}
		return true;
	}
}