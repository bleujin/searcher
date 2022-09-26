package net.bleujin.searcher;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.queryparser.classic.QueryParser;

import net.bleujin.searcher.index.PerFieldAnalyzer;
import net.ion.framework.util.MapUtil;

public class DefaultSearchConfig {

	private ExecutorService executor;
	private String defaultFieldName;
	private QueryParser defaultParser;

	private Analyzer queryAnalyzer;
	private PerFieldAnalyzer perFieldAnalyzer;
	
	DefaultSearchConfig(SearchControllerConfig sconfig) {
		this.executor = sconfig.defaultExecutor() ;
		this.defaultFieldName = sconfig.defaultFieldName() ;
		this.defaultParser = sconfig.defaultParser() ;
		
		this.queryAnalyzer = sconfig.defaultAnalyzer() ;
		this.perFieldAnalyzer = new PerFieldAnalyzer(queryAnalyzer, MapUtil.newCaseInsensitiveMap());
	}
	

	public Analyzer queryAnalyzer() {
		return queryAnalyzer;
	}

	public DefaultSearchConfig queryAnalyzer(Analyzer analyzer) {
		this.queryAnalyzer = analyzer;
		this.perFieldAnalyzer = perFieldAnalyzer.copyAnalyzer(analyzer) ;
		return this ;
	}

	public ExecutorService executor() {
		return executor;
	}

	public DefaultSearchConfig executor(ExecutorService executor) {
		this.executor = executor;
		return this ;
	}

	public String defaultFieldName() {
		return defaultFieldName;
	}

	public DefaultSearchConfig defaultFieldName(String defaultFieldName) {
		this.defaultFieldName = defaultFieldName;
		return this ;
	}

	public QueryParser defaultParser() {
		return defaultParser;
	}

	public DefaultSearchConfig defaultParser(QueryParser defaultParser) {
		this.defaultParser = defaultParser;
		return this ;
	}

	public DefaultSearchConfig fieldAnalyzer(String fieldName, Analyzer analyzer) {
		perFieldAnalyzer.defineAnalyzer(fieldName, analyzer);
		return this;
	}

	public DefaultSearchConfig removeFieldAnalyzer(String fieldName) {
		perFieldAnalyzer.removeField(fieldName);
		return this;
	}
	
	public PerFieldAnalyzer perFieldAnalyzer() {
		return perFieldAnalyzer ;
	}
	
}
