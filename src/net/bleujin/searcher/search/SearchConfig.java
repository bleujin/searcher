package net.bleujin.searcher.search;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.index.IndexConfig;
import net.bleujin.searcher.index.IndexSession;

public class SearchConfig {
	
	private SearchController sc;
	private Directory dir;
	private Analyzer analyzer ;
	private String defaultFieldName;

	private SearchConfig(SearchController sc, Directory dir) {
		this.sc = sc ;
		this.dir = dir ;
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
	
}
