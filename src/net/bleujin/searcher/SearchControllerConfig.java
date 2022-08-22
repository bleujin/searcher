package net.bleujin.searcher;

import java.util.concurrent.ExecutorService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.common.SearchConstant;
import net.ion.framework.util.WithinThreadExecutor;

public class SearchControllerConfig {
	private Analyzer defaultAnalyzer = new WhitespaceAnalyzer() ;
	private final Directory dir ;
	private final static ExecutorService DFT_EXECUTOR = new WithinThreadExecutor() ;  

	private SearchControllerConfig(Directory dir) {
		this.dir = dir ;
	}

	
	public SearchController newBuild() {
		return build(OpenMode.CREATE_OR_APPEND) ;
	}


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

	
	public final ExecutorService defaultExecutor() {
		return DFT_EXECUTOR ;
	}

	public static SearchControllerConfig newRam() {
		Directory dir = new ByteBuffersDirectory();
		
		return new SearchControllerConfig(dir);
	}


}
