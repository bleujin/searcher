package net.bleujin.searcher.index;

import java.util.concurrent.ExecutorService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriterConfig;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.FieldIndexingStrategy;

public class IndexConfig {

	private SearchController sc;
	private FieldIndexingStrategy indexingStrategy;
	private Analyzer analyzer ;
	private String name = "NoName" ;
	private ExecutorService es ;
	private int maxBufferedDocs = 0 ;
	private double ramBufferSizeMB = 0D;

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

	
	public String name() {
		return name ;
	}
	
	public Analyzer indexAnalyzer() {
		return this.analyzer ;
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

	void param(IndexWriterConfig iwc) {
		if (this.maxBufferedDocs > 0) iwc.setMaxBufferedDocs(this.maxBufferedDocs) ;
		if (this.ramBufferSizeMB > 0) iwc.setRAMBufferSizeMB(this.ramBufferSizeMB) ;
	}



	
}
