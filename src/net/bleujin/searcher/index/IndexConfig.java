package net.bleujin.searcher.index;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.IndexWriterConfig;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.common.MyField.MyFieldType;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;

public class IndexConfig {

	private SearchController sc;
	private FieldIndexingStrategy indexingStrategy;
	private Analyzer analyzer ;
	private String name = "NoName" ;
	private ExecutorService es ;
	private int maxBufferedDocs = 0 ;
	private double ramBufferSizeMB = 0D;
	private Map<String, String> commitDatas = MapUtil.newMap() ;
	private final Map<String, Analyzer> analMap = MapUtil.newCaseInsensitiveMap() ;
	private final Map<String, MyField.MyFieldType> typeMap = MapUtil.newCaseInsensitiveMap() ;
	
	private IndexConfig(SearchController sc, FieldIndexingStrategy indexingStrategy) {
		this.sc = sc ;
		this.indexingStrategy = indexingStrategy ;
		this.analyzer = sc.sconfig().analyzer() ;
		this.es = sc.sconfig().defaultExecutor() ;
	}

	
	public static IndexConfig create(SearchController searchController) {
		return new IndexConfig(searchController, FieldIndexingStrategy.DEFAULT) ;
	}
	
	public FieldIndexingStrategy indexingStrategy() {
		return indexingStrategy ;
	}


	public IndexConfig commitData(String key, String value) {
		commitDatas.put(key, value) ;
		return this ;
	}

	public Map<String, String> commitData() {
		return Collections.unmodifiableMap(commitDatas) ;
	}
	
	public String name() {
		return name ;
	}
	
	public Analyzer indexAnalyzer() {
		return this.analyzer ;
	}

	public IndexConfig indexAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer ;
		return this ;
	}

	public ExecutorService executorService() {
		return es;
	}


	public IndexConfig maxBufferedDocs(int maxBufferedDocs) {
		this.maxBufferedDocs = maxBufferedDocs ;
		return this ;
	}

	public int maxBufferedDocs() {
		return maxBufferedDocs;
	}
	
	public IndexConfig ramBufferSizeMB(double ramBufferSizeMB) {
		this.ramBufferSizeMB = ramBufferSizeMB ;
		return this ;
	}

	void attributes(IndexWriterConfig iwc) {
		if (this.maxBufferedDocs > 0) iwc.setMaxBufferedDocs(this.maxBufferedDocs) ;
		if (this.ramBufferSizeMB > 0) iwc.setRAMBufferSizeMB(this.ramBufferSizeMB) ;
	}


	public IndexConfig fieldAnalyzer(String fieldName, Analyzer analyzer) {
		analMap.put(fieldName, analyzer) ;
		this.analyzer = new PerFieldAnalyzerWrapper(this.analyzer, this.analMap) ;
		return this;
	}
	
	public IndexConfig fieldType(String fieldName, MyFieldType type) {
		typeMap.put(fieldName, type) ;
		return this ;
	}

	public IndexConfig removeFieldAnalyzer(String fieldName) {
		if (! analMap.containsKey(fieldName)) return this ;
		
		analMap.remove(fieldName) ;
		this.analyzer = new PerFieldAnalyzerWrapper(this.analyzer, this.analMap) ;
		return this;
	}


	public Map<String, MyFieldType> fieldTypeMap() {
		return typeMap ;
	}


	
}
