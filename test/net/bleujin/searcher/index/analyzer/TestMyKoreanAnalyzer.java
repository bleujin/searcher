package net.bleujin.searcher.index.analyzer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class TestMyKoreanAnalyzer extends TestCase {
	
	public void testInit() throws Exception {
		try (InputStream in = getClass().getResourceAsStream("dictionary.dic");
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
		    Debug.line(reader.readLine()) ;
		}
	}

}
