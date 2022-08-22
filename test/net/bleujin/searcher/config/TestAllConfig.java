package net.bleujin.searcher.config;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAllConfig {
	
	public static Test suite(){
//		System.setProperty(Debug.PROPERTY_KEY, "off") ;
		TestSuite ts = new TestSuite("ALL Central") ;
		
		ts.addTestSuite(TestConfig.class) ;
		ts.addTestSuite(TestIndexConfig.class) ;
		ts.addTestSuite(TestSearchConfig.class);
		
		
		return ts ;
	}

}
