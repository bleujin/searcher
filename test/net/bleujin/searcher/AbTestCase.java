package net.bleujin.searcher;

import java.io.IOException;

import org.apache.lucene.index.IndexWriterConfig.OpenMode;

import junit.framework.TestCase;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;

public class AbTestCase extends TestCase {

	protected SearchController sdc;

	@Override
	protected void setUp() throws Exception {
		this.sdc = SearchControllerConfig.newRam().build(OpenMode.CREATE_OR_APPEND);
	}

	protected void tearDown() throws Exception {
		this.sdc.close();
	}

	public final static IndexJob<Void> createIndexJob(final String prefix, final int count) {
		return new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < count; i++) {
					WriteDocument wdoc = isession.newDocument(prefix + i).keyword("prefix", prefix).number("idx", i);
					isession.updateDocument(wdoc);
				}
				return null;
			}
		};
	}

	protected IndexJob<Void> SAMPLE = new IndexJob<Void>() {
		public Void handle(IndexSession isession) throws IOException {
			isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).insert();
			isession.newDocument("hero").keyword("name", "hero").number("age", 30).insert();
			isession.newDocument("jin").keyword("name", "jin").number("age", 40).insert();
			return null;
		};
	};
}
