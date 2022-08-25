package net.bleujin.searcher.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class TestMerge extends TestCase {

	public void testFirst() throws Exception {
		IndexWriterConfig iwconfig = new IndexWriterConfig(new StandardAnalyzer());
		MergePolicy mpolicy = iwconfig.getMergePolicy();
		Debug.line(mpolicy);

	}

}
