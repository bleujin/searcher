package net.bleujin.searcher;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import net.bleujin.searcher.common.IKeywordField;
import net.bleujin.searcher.common.SearchConstant;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.WithinThreadExecutor;

public class SearchControllerConfig {
	private Analyzer defaultAnalyzer = new WhitespaceAnalyzer() ;
	private ExecutorService defaultExecutor = new WithinThreadExecutor() ;
	
	private QueryParser defaultParser;
	private final Directory dir ;
	private SearchControllerConfig(Directory dir) {
		this.dir = dir ;
		this.defaultParser = new QueryParser(SearchConstant.ISALL_FIELD, defaultAnalyzer) ;
	}
	
	public SearchController newBuild() throws IOException {
		return build(OpenMode.CREATE_OR_APPEND) ;
	}

	public static SearchControllerConfig newLocalFile(String path) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(path)) ;
		return new SearchControllerConfig(dir);
	}

	public static SearchControllerConfig newLocalFile(Path path) throws IOException {
		Directory dir = FSDirectory.open(path) ;
		return new SearchControllerConfig(dir);
	}


	public static SearchControllerConfig newRam() {
		Directory dir = new ByteBuffersDirectory();
		
		return new SearchControllerConfig(dir);
	}

	public SearchController build() throws IOException {
		return build(OpenMode.CREATE_OR_APPEND) ;
	}
	
	public SearchController build(OpenMode openMode) throws IOException {
		
		if (! DirectoryReader.indexExists(dir)) {
			IndexWriterConfig iwc = new IndexWriterConfig(defaultAnalyzer);  // index blank
			iwc.setOpenMode(openMode) ;
			IndexWriter iwriter = null ;
			try {
				iwriter = new IndexWriter(dir, iwc);
				iwriter.commit() ;
			} finally {
				IOUtil.close(iwriter); 
			}
		}
		
		SearchController result = new SearchController(this, this.dir, openMode);
		return result;
	}

	
	public Analyzer defaultAnalyzer() {
		return defaultAnalyzer ;
	}
	

	public QueryParser defaultParser() {
		return defaultParser ;
	}

	public SearchControllerConfig defaultAnalyzer(Analyzer analyzer) {
		this.defaultAnalyzer = analyzer ;
		return this ;
	}
	
	public SearchControllerConfig defaultExecutorService(ExecutorService executorService) {
		this.defaultExecutor = executorService ;
		return this;
	}

	public String defaultFieldName() {
		return SearchConstant.ISALL_FIELD ;
	}

	
	public final ExecutorService defaultExecutor() {
		return defaultExecutor ;
	}






}
