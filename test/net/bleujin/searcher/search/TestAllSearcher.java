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
		ts.addTestSuite(TestInterface.class);
		ts.addTestSuite(TestFieldType.class);

		ts.addTestSuite(TestKoreanAnalyzer.class);
		ts.addTestSuite(TestLimitedChannel.class);
		ts.addTestSuite(TestMultiSearcher.class);
		ts.addTestSuite(TestPerFieldAnalyzer.class);
		ts.addTestSuite(TestQueryParser.class);

		ts.addTestSuite(TestRangeFilter.class);
		ts.addTestSuite(TestReader.class);
		ts.addTestSuite(TestRequestSelection.class);

		ts.addTestSuite(TestSearcher.class);
		ts.addTestSuite(TestSearcherPaging.class) ;
		ts.addTestSuite(TestSearchRequest.class);
		ts.addTestSuite(TestSearchResponse.class);
		ts.addTestSuite(TestSearchSchema.class);
		ts.addTestSuite(TestSearchSort.class);
		
		ts.addTestSuite(TestSort.class) ;
		ts.addTestSuite(TestSortExpression.class);
		ts.addTestSuite(TestTotalCount.class) ;

		return ts;
	}
}
