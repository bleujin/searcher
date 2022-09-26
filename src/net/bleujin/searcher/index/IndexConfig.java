package net.bleujin.searcher.index;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.print.attribute.standard.Copies;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.IndexWriterConfig;

import net.bleujin.searcher.DefaultIndexConfig;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.common.MyField.MyFieldType;
import net.ion.framework.util.MapUtil;

public class IndexConfig {

	private SearchController sc;
	private FieldIndexingStrategy indexingStrategy;
	private ExecutorService es ;
	
	private PerFieldAnalyzer perFieldAnalyzer ;
	private IndexFieldType indexFieldType;

	
	private String name = "NoName" ;
	private int maxBufferedDocs = 0 ;
	private double ramBufferSizeMB = 0D;
	private Map<String, String> commitDatas = MapUtil.newMap() ;
	
	private IndexConfig(SearchController sc, DefaultIndexConfig defaultIndexConfig) {
		this.sc = sc ;
		this.indexingStrategy = defaultIndexConfig.fieldIndexingStrategy() ;
		this.es = defaultIndexConfig.executor() ;
		
		this.perFieldAnalyzer = defaultIndexConfig.perFieldAnalyzer().copyAnalyzer(defaultIndexConfig.analyzer()) ;
		this.indexFieldType = defaultIndexConfig.copyIndexFieldTypeMap()  ;
	}

	
	public static IndexConfig create(SearchController searchController) {
		return new IndexConfig(searchController, searchController.defaultIndexConfig()) ;
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
		return perFieldAnalyzer ; 
	}

	public IndexConfig indexAnalyzer(Analyzer analyzer) {
		this.perFieldAnalyzer = perFieldAnalyzer.copyAnalyzer(analyzer) ;
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
		perFieldAnalyzer.defineAnalyzer(fieldName, analyzer) ;
		return this;
	}
	
	public IndexConfig removeFieldAnalyzer(String fieldName) {
		perFieldAnalyzer.removeField(fieldName) ;
		return this;
	}

	public IndexConfig fieldType(String fieldName, MyFieldType type) {
		indexFieldType.decideFieldType(fieldName, type) ;
		return this ;
	}
	
	public IndexFieldType indexFieldTypeMap() {
		return indexFieldType ;
	}

	
}
