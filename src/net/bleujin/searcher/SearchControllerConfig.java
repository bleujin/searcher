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
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import net.bleujin.searcher.common.SearchConstant;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.WithinThreadExecutor;

public class SearchControllerConfig {
	private Analyzer defaultAnalyzer = new WhitespaceAnalyzer() ;
	private final Directory dir ;
	private ExecutorService defaultExecutor = new WithinThreadExecutor() ;  

	private SearchControllerConfig(Directory dir) {
		this.dir = dir ;
	}

	
	public SearchController newBuild() throws IOException {
		return build(OpenMode.CREATE_OR_APPEND) ;
	}

	public static SearchControllerConfig newLocalFile(String path) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(path)) ;
		return new SearchControllerConfig(dir);
	}

	public static SearchControllerConfig newRam() {
		Directory dir = new ByteBuffersDirectory();
		
		return new SearchControllerConfig(dir);
	}

	public SearchController build(OpenMode openMode) throws IOException {
		SearchController result = new SearchController(this, this.dir, openMode);
		
		if (! DirectoryReader.indexExists(dir)) {
			IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());  // index blank
			iwc.setOpenMode(openMode) ;
			IndexWriter iwriter = null ;
			try {
				iwriter = new IndexWriter(dir, iwc);
				iwriter.commit() ;
			} finally {
				IOUtil.close(iwriter); 
			}
		}
		return result;
	}

	
	public Analyzer analyzer() {
		return defaultAnalyzer ;
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
