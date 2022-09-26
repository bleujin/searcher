package net.bleujin.searcher;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.common.MyField.MyFieldType;
import net.bleujin.searcher.index.IndexFieldType;
import net.bleujin.searcher.index.PerFieldAnalyzer;
import net.ion.framework.util.MapUtil;

public class DefaultIndexConfig {

	private Analyzer analyzer;
	private ExecutorService executor;
	private FieldIndexingStrategy fieldIndexingStrategy ;
	
	private PerFieldAnalyzer perFieldAnalyzer ;
	private IndexFieldType indexFieldType;

	DefaultIndexConfig(SearchControllerConfig sconfig) {
		this.analyzer = sconfig.defaultAnalyzer() ;
		this.executor = sconfig.defaultExecutor() ;
		this.fieldIndexingStrategy = FieldIndexingStrategy.DEFAULT ;
		
		this.perFieldAnalyzer = new PerFieldAnalyzer(sconfig.defaultAnalyzer(), MapUtil.newCaseInsensitiveMap()) ;
		this.indexFieldType = new IndexFieldType() ;
	}
	
	public Analyzer analyzer() {
		return this.analyzer ;
	}
	
	public ExecutorService executor() {
		return this.executor ;
	}
	
	public DefaultIndexConfig analyzer(Analyzer analyzer) {
		this.analyzer = analyzer ;
		this.perFieldAnalyzer = perFieldAnalyzer.copyAnalyzer(analyzer) ;
		return this ;
	}
	
	public DefaultIndexConfig executor(ExecutorService executor) {
		this.executor = executor ;
		return this ;
	}

	public FieldIndexingStrategy fieldIndexingStrategy() {
		return fieldIndexingStrategy;
	}

	public DefaultIndexConfig fieldIndexingStrategy(FieldIndexingStrategy fieldIndexingStrategy) {
		this.fieldIndexingStrategy = fieldIndexingStrategy;
		return this ;
	}
	
	
	public DefaultIndexConfig fieldType(String fieldName, MyFieldType type) {
		indexFieldType.decideFieldType(fieldName, type) ;
		return this ;
	}
	
	
	public IndexFieldType copyIndexFieldTypeMap() {
		IndexFieldType copyIndexFieldType = new IndexFieldType() ;
		indexFieldType.entrySet().forEach(entry ->{
			copyIndexFieldType.decideFieldType(entry.getKey(), entry.getValue()) ;
		});
		
		return copyIndexFieldType ;
	}

	public DefaultIndexConfig fieldAnalyzer(String fieldName, Analyzer analyzer) {
		perFieldAnalyzer.defineAnalyzer(fieldName, analyzer) ;
		return this;
	}
	
	public DefaultIndexConfig removeFieldAnalyzer(String fieldName) {
		perFieldAnalyzer.removeField(fieldName) ;
		return this;
	}
	
	public PerFieldAnalyzer perFieldAnalyzer() {
		return perFieldAnalyzer ;
	}

}
