package net.bleujin.searcher.index;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.FieldIndexingStrategy;

public class IndexConfig {

	private SearchController sc;
	private FieldIndexingStrategy indexingStrategy;
	private Analyzer analyzer ;
	private String name = "NoName" ;

	private IndexConfig(SearchController sc, FieldIndexingStrategy indexingStrategy) {
		this.sc = sc ;
		this.indexingStrategy = indexingStrategy ;
		this.analyzer = sc.sconfig().analyzer() ;
	}

	
	public static IndexConfig create(SearchController searchController) {
		return new IndexConfig(searchController, FieldIndexingStrategy.DEFAULT) ;
	}
	
	public FieldIndexingStrategy indexingStrategy() {
		return indexingStrategy ;
	}

	
	public String name() {
		return name ;
	}
	
	public Analyzer analyzer() {
		return this.analyzer ;
	}

	
}
