package net.bleujin.searcher;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.common.SearchConstant;

public class SearchControllerConfig {
	private Analyzer defaultAnalyzer = new WhitespaceAnalyzer() ;


	public SearchController build(OpenMode openMode) {
		Directory dir = new ByteBuffersDirectory();
		
		return new SearchController(this, dir, openMode);
	}

	
	public Analyzer analyzer() {
		return defaultAnalyzer ;
	}


	public String defaultFieldName() {
		return SearchConstant.ISALL_FIELD ;
	}

}
