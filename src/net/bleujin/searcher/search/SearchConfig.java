package net.bleujin.searcher.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import net.bleujin.searcher.DefaultSearchConfig;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.index.PerFieldAnalyzer;
import net.bleujin.searcher.search.processor.PostProcessor;
import net.bleujin.searcher.search.processor.PreProcessor;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;

public class SearchConfig {
	
	private final SearchController sc;
	private final Directory dir;
	private ExecutorService es ;
	
	private String defaultFieldName;
	private PerFieldAnalyzer perFieldAnalyzer;
	
	private final List<PostProcessor> postListeners = new ArrayList<PostProcessor>();
	private final List<PreProcessor> preListeners = new ArrayList<PreProcessor>();
	private SetMultimap<SortField.Type, String> definedFieldType = HashMultimap.create() ;
	private SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span class='matched'>", "</span>");
	private Map<String, Object> attrs = MapUtil.newMap();

	private SearchConfig(SearchController sc, DefaultSearchConfig defaultSearchConfig) {
		this.sc = sc ;
		this.dir = sc.dir() ;
		this.es = defaultSearchConfig.executor() ;
		this.defaultFieldName = defaultSearchConfig.defaultFieldName() ;
		
		this.perFieldAnalyzer = defaultSearchConfig.perFieldAnalyzer().copyAnalyzer(defaultSearchConfig.queryAnalyzer()) ;
	}

	
	public static SearchConfig create(SearchController searchController) {
		return new SearchConfig(searchController, searchController.defaultSearchConfig()) ;
	}

	public Directory dir() {
		return dir;
	}
	
	public SearchConfig formatter(SimpleHTMLFormatter formatter) {
		this.formatter = formatter ;
		return this ;
	}
	
	public SimpleHTMLFormatter formatter() {
		return formatter ;
	}

	public SearchConfig numFieldType(String... fieldNames) {
		for(String fieldName : fieldNames) definedFieldType.put(Type.LONG, fieldName) ;
		return this ;
	}

	public SearchConfig stringFieldType(String... fieldNames) {
		for(String fieldName : fieldNames) definedFieldType.put(Type.STRING, fieldName) ;
		return this ;
	}
	
	public Set<String>  numFields(){
		return definedFieldType.get(Type.LONG) ;
	}

	public Set<String>  stringFields(){
		return definedFieldType.get(Type.STRING) ;
	}

	public boolean isNumField(String field) {
		return definedFieldType.get(Type.LONG).contains(field) ;
	}

	public boolean isStringField(String field) {
		return ! isNumField(field) ;
	}

	
	public QueryParser queryParser() {
		return new TypedQueryParser(defaultFieldName, this.perFieldAnalyzer, this) ;  // always create..
	}

	public final SearchConfig executorService(ExecutorService es) {
		this.es = es ;
		return this ;
	}
	
	public ExecutorService executorService() {
		return es ;
	}
	
	public final SearchConfig queryAnalyzer(Analyzer analyzer) {
		this.perFieldAnalyzer.copyAnalyzer(analyzer) ;
		return this ;
	}
	
	public Analyzer queryAnalyzer() {
		return this.perFieldAnalyzer;
	}
	
	public final SearchConfig defaultFieldName(String defaultFieldName) {
		this.defaultFieldName = defaultFieldName ;
		return this ;
	}
	
	public final String defaultFieldName() {
		return defaultFieldName ;
	}
	
	
	public SearchConfig addPostListener(PostProcessor... processors) {
		SetUtil.create(processors).forEach(processor -> postListeners.add(processor)) ;
		return this ;
	}
	
	public SearchConfig addPreListener(PreProcessor... processors) {
		SetUtil.create(processors).forEach(processor -> preListeners.add(processor)) ;
		return this ;
	}
	
	public void emitPreListener(SearchRequest sreq) {
		es.submit(() ->{
			preListeners.forEach(processor ->{
				processor.process(sreq) ;
			});
		}) ;
	}
	
	public Future<Void> emitPostListener(SearchResponse sres) {
		return es.submit(() ->{
			postListeners.forEach(processor ->{
				processor.process(sres.request(), sres) ;
			});
			return null ;
		}) ;
	}

	
	public SearchConfig attr(String name, int value) {
		attrs.put(name, value);
		return this;
	}

	public SearchConfig attr(String name, String value) {
		attrs.put(name, value);
		return this;
	}

	public String attrAsString(String name, String dftValue) {
		return StringUtil.coalesce(StringUtil.toString(attrs.get(name)), dftValue);
	}

	public int attrAsInt(String name, int dftValue) {
		return NumberUtil.toInt(StringUtil.toString(attrs.get(name)), dftValue);
	}

	
	public SearchConfig fieldAnalyzer(String fieldName, Analyzer analyzer) {
		perFieldAnalyzer.defineAnalyzer(fieldName, analyzer) ;
		return this;
	}

	public SearchConfig removeFieldAnalyzer(String fieldName) {
		perFieldAnalyzer.removeField(fieldName) ;
		return this;
	}

}
