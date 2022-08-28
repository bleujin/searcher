package net.bleujin.searcher.index;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllIndexer {

	public static Test suite() {
		TestSuite ts = new TestSuite("All Indexer");

		ts.addTestSuite(TestDocument.class);
		ts.addTestSuite(TestDocumentField.class);
		ts.addTestSuite(TestDocumentFieldType.class);
		
		ts.addTestSuite(TestIndexAnalyzer.class);
		ts.addTestSuite(TestIndexAsync.class);
		ts.addTestSuite(TestIndexer.class);
		ts.addTestSuite(TestIndexSchema.class);
		ts.addTestSuite(TestIndexSession.class);

		ts.addTestSuite(TestMerge.class);
		ts.addTestSuite(TestReaderInfo.class);
		ts.addTestSuite(TestRollback.class);
		ts.addTestSuite(TestWriteDocument.class);

		ts.addTestSuite(TestPromise.class);
		
		return ts;
	}
}
