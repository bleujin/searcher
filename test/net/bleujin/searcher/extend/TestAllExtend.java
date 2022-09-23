package net.bleujin.searcher.extend;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.bleujin.lucene.TestField;
import net.bleujin.searcher.index.TestReaderInfo;
import net.ion.radon.aclient.filter.TestFilter;

public class TestAllExtend extends TestSuite{

	public static Test suite() {
		TestSuite ts = new TestSuite("All Extend Function");
		
		ts.addTestSuite(TestHighlight.class) ;
		ts.addTestSuite(TestSimilarity.class);
		ts.addTestSuite(TestSuggest.class);

		return ts;
	}
}
