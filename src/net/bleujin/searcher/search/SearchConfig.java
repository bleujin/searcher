package net.bleujin.searcher.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.store.Directory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.search.processor.PostProcessor;
import net.bleujin.searcher.search.processor.PreProcessor;
import net.ion.framework.util.SetUtil;

public class SearchConfig {
	
	private final SearchController sc;
	private final Directory dir;
	private ExecutorService es ;
	
	private Analyzer analyzer ;
	private String defaultFieldName;
	
	private final List<PostProcessor> postListeners = new ArrayList<PostProcessor>();
	private final List<PreProcessor> preListeners = new ArrayList<PreProcessor>();
	private SetMultimap<SortField.Type, String> definedFieldType = HashMultimap.create() ;

	private SearchConfig(SearchController sc, Directory dir) {
		this.sc = sc ;
		this.dir = dir ;
		this.es = sc.sconfig().defaultExecutor() ;
		this.analyzer = sc.sconfig().analyzer() ;
		this.defaultFieldName = sc.sconfig().defaultFieldName() ;
	}

	
	public static SearchConfig create(SearchController searchController) {
		return new SearchConfig(searchController, searchController.dir()) ;
	}

	public Directory dir() {
		return dir;
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
		return new TypedQueryParser(defaultFieldName, analyzer, this) ;  // always create..
	}

	public final SearchConfig executorService(ExecutorService es) {
		this.es = es ;
		return this ;
	}
	
	public ExecutorService executorService() {
		return es ;
	}
	
	public final SearchConfig queryAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer ;
		return this ;
	}
	
	public Analyzer queryAnalyzer() {
		return analyzer ;
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


}
