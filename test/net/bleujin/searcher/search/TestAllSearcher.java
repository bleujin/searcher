package net.bleujin.searcher.search;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.bleujin.lucene.TestField;
import net.bleujin.searcher.index.TestReaderInfo;
import net.ion.radon.aclient.filter.TestFilter;

public class TestAllSearcher extends TestSuite{

	public static Test suite() {
		TestSuite ts = new TestSuite("All Searcher");
		
		ts.addTestSuite(TestBlank.class) ;
		ts.addTestSuite(TestField.class);

		ts.addTestSuite(TestFilter.class);

		ts.addTestSuite(TestSearcher.class);
		ts.addTestSuite(TestSort.class) ;
		ts.addTestSuite(TestSearcherPaging.class) ;
		ts.addTestSuite(TestQueryParser.class);
		
		ts.addTestSuite(TestMultiSearcher.class);

		ts.addTestSuite(TestSortExpression.class);
		ts.addTestSuite(TestSearchSort.class);
		
		ts.addTestSuite(TestSearchRequest.class);
		ts.addTestSuite(TestSearchResponse.class);
		
		ts.addTestSuite(TestReader.class);
		ts.addTestSuite(TestReaderInfo.class) ;

		return ts;
	}
}
