package net.bleujin.searcher.extend;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.KeyValue;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.Debug;

public class TestSuggest extends AbTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(new StandardAnalyzer()) ;
				
				isession.newDocument().text("subject", "한글").insert();
				isession.newDocument().text("subject", "한민족").insert();
				isession.newDocument().text("subject", "대한민국").insert();
				isession.newDocument().text("subject", "한한령").insert();
				isession.newDocument().text("subject", "crawl").insert();
				isession.newDocument().text("subject", "한글날 한글을").insert();
				isession.newDocument().text("subject", "한한글 사랑").insert();
				return null;
			}
		});
		
	}
	
	public void testInterface() throws Exception {
		Suggester s = sdc.newSuggester();
		
		List<KeyValue> found = s.lookup("한", 3); // when do not build, 
		for (KeyValue entry : found) {
			Debug.debug(entry.getKey(), entry.getValue());
		}
		s.build("subject");
		
		found = sdc.newSuggester().lookup("한", 3);
		for (KeyValue entry : found) {
			Debug.line(entry.getKey(), entry.getValue());
		}
	}

	/*
	 * public void testSugg() throws Exception { AnalyzingSuggester suggester = new
	 * AnalyzingSuggester(sdc.indexConfig().indexAnalyzer()); Dictionary dict = new
	 * LuceneDictionary(sdc.newSearcher().indexReader(), "subject"); // new
	 * WordFreqArrayIterator(wordFreqs) suggester.build(dict);
	 * 
	 * List<LookupResult> results = suggester.lookup("한", false, 100);
	 * 
	 * for (LookupResult lookupResult : results) {
	 * System.out.println(lookupResult.key + ":" + lookupResult.value); } }
	 * 
	 * public void testFileDictionary() throws Exception {
	 * 
	 * AnalyzingSuggester suggester = new
	 * AnalyzingSuggester(sdc.indexConfig().indexAnalyzer());
	 * 
	 * Reader reader = new
	 * StringReader("word1\t100\r\nword2 word3\t101\r\nword2 word5\t102") ;
	 * Dictionary dict = new FileDictionary(reader, "\t") ; suggester.build(dict);
	 * 
	 * List<LookupResult> results = suggester.lookup("word3", false, 100);
	 * 
	 * for (LookupResult lookupResult : results) {
	 * System.out.println(lookupResult.key + ":" + lookupResult.value); } }
	 * 
	 * 
	 * public void testTerms() throws Exception { IndexReader reader =
	 * sdc.newSearcher().indexReader(); AtomicReader aReader =
	 * SlowCompositeReaderWrapper.wrap(reader); // Should use reader.leaves instead
	 * ? Terms terms = aReader.terms("subject"); TermsEnum termEnum =
	 * terms.iterator(null); TermFreqIterator wrapper = new
	 * TermFreqIterator.TermFreqIteratorWrapper(termEnum);
	 * 
	 * BytesRef br = null ; while( (br = wrapper.next()) != null){
	 * Debug.line(br.utf8ToString()); } }
	 */
}
