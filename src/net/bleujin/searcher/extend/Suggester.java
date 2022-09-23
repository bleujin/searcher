package net.bleujin.searcher.extend;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.suggest.Lookup.LookupResult;
import org.apache.lucene.search.suggest.analyzing.AnalyzingSuggester;

import net.bleujin.searcher.SearchController;
import net.ion.framework.util.ListUtil;

public class Suggester {

	private SearchController sdc;
	private AnalyzingSuggester inner;
	
	public Suggester(SearchController central, Analyzer analyzer) {
		this.sdc = central ;
		this.inner = new AnalyzingSuggester(sdc.dir(), "suggester_temp", analyzer);
	}
	
	
	public Suggester build(String fieldName) throws IOException {
		sdc.search(session ->{
			Dictionary dict = new LuceneDictionary(session.indexReader(), fieldName); // new WordFreqArrayIterator(wordFreqs)
			inner.build(dict);
			return null ;
		}) ;

		return this ;
	}


	public List<KeyValue> lookup(String key, int num) throws IOException {
		List<KeyValue> result = ListUtil.newList() ;
		List<LookupResult> founds = inner.lookup(key, false, num);
		
		for(LookupResult found : founds){
			result.add(new DefaultKeyValue(found.key, found.value));
		}
		
		return result ;
	}

}
