package net.bleujin.searcher;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.bleujin.searcher.extend.TestAllExtend;
import net.bleujin.searcher.index.TestAllIndexer;
import net.bleujin.searcher.search.TestAllSearcher;
import net.ion.framework.util.Debug;

public class TestAll {

	public static Test suite(){
		System.setProperty(Debug.PROPERTY_KEY, "off") ;
		TestSuite ts = new TestSuite("ISearcher ALL") ;
		
		ts.addTestSuite(TestFirst.class) ;
		ts.addTest(TestAllIndexer.suite()) ;
		ts.addTest(TestAllSearcher.suite()) ;

		ts.addTest(TestAllExtend.suite()) ;

		return ts ;
	}
}
