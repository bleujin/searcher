package net.bleujin.searcher.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.search.processor.PostProcessor;
import net.bleujin.searcher.search.processor.PreProcessor;

public class SearchConfig {
	
	private final SearchController sc;
	private final Directory dir;
	private ExecutorService es ;
	
	private Analyzer analyzer ;
	private String defaultFieldName;
	
	private final List<PostProcessor> postListeners = new ArrayList<PostProcessor>();
	private final List<PreProcessor> preListeners = new ArrayList<PreProcessor>();

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


	public QueryParser queryParser() {
		return new QueryParser(defaultFieldName, analyzer) ;
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
	
	
	public SearchConfig addPostListener(final PostProcessor processor) {
		postListeners.add(processor) ;
		return this ;
	}
	
	public SearchConfig addPreListener(final PreProcessor processor) {
		preListeners.add(processor) ;
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
