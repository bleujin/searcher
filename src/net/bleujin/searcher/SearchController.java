package net.bleujin.searcher;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.extend.Suggester;
import net.bleujin.searcher.index.IndexConfig;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.reader.InfoHandler;
import net.bleujin.searcher.search.SearchConfig;
import net.bleujin.searcher.search.SearchJob;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.search.SearchSession;
import net.ion.framework.util.IOUtil;

public class SearchController implements Closeable{

	private final SearchControllerConfig config;
	private final OpenMode openMode;
	
	private boolean isModified = true;
	private DirectoryReader dreader = null ;
	private IndexSearcher isearcher = null; // recycle searcher
	private final ReadWriteLock locker = new ReentrantReadWriteLock() ;
	
	private DefaultIndexConfig defaultIndexConfig;
	private DefaultSearchConfig defaultSearchConfig;

	SearchController(SearchControllerConfig config, Directory dir, OpenMode openMode) throws IOException {
		this.config = config;
		this.openMode = openMode;
		this.dreader = DirectoryReader.open(dir) ;
		
		
		this.defaultIndexConfig = new DefaultIndexConfig(config) ;
		this.defaultSearchConfig = new DefaultSearchConfig(config) ;
	}

	public SearchControllerConfig sconfig() {
		return config;
	}

	public ReadWriteLock locker() {
		return locker ;
	}
	
	public void close() throws IOException {
		IOUtil.close(dreader);
		IOUtil.close(dreader.directory()); 
	}

	public Directory dir() {
		return this.dreader.directory();
	}

	
	@Deprecated // OnlyTest
	public void destroySelf() throws IOException { 
		try {
			IndexWriterConfig conf = new IndexWriterConfig() ;
			IndexWriter indexWriter  = new IndexWriter(dir(), conf);
            indexWriter.deleteAll();
            indexWriter.commit();
            IOUtil.close(indexWriter);
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} finally {
			close() ;
		}
	}


	
	public void begin(String string) {

	}

	public synchronized void end() {
		this.isModified = true;

	}

	private boolean isModified() {
		return this.isModified;
	}

	private void reloaded() {
		this.isModified = false;
	}
	
	public <T> CompletableFuture<T> indexTran(IndexJob<T> indexJob) {
		return tran(indexJob, config.defaultExecutor()) ;
	}
	
	public <T> CompletableFuture<T> tran(IndexJob<T> indexJob, ExecutorService eservice) {
		if (eservice.isTerminated() || eservice.isShutdown()) return CompletableFuture.completedFuture(null) ;
		
		return CompletableFuture.supplyAsync(() -> {
			try {
				return index(indexJob) ;
			} catch (IOException e) {
				throw new IllegalStateException(e) ;
			}
		}, eservice) ;
	}

	public <T> T index(IndexJob<T> indexJob) throws IOException {
		try {
			return indexAsync(indexJob).get() ;
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace(); 
			throw new IOException(e.getCause()) ;
		}
	}
	
	public <T> Future<T> indexAsync(final IndexJob<T> indexJob) throws IOException {
		IndexConfig iconfig = IndexConfig.create(this);
		
		Future<T> result = iconfig.executorService().submit(() -> {
			begin(iconfig.name());
			Lock lock = locker.writeLock() ;
			
			IndexSession indexSession = null;
			T rtn = null ;
			try {
				lock.lock() ;
				
				indexSession = IndexSession.create(this, iconfig); // create indexWriter
				rtn = indexJob.handle(indexSession);

				indexSession.commit();
			} catch (Throwable e) {
				e.printStackTrace() ;
				indexSession.rollback();
			} finally {
				indexSession.forceClose();
				end();
				lock.unlock() ;
			}
			return rtn ;
		}) ;

		return result;
	}
	

	public SearchResponse search(final String query) throws IOException {
		return search(session ->{
			return session.createRequest(query).find() ;
		}) ;
	}
	
	public <T> T search(SearchJob<T> searchJob) throws IOException {
		SearchConfig sconfig = SearchConfig.create(this);
		return search(currentSearcher(), sconfig, searchJob);
	}
	

	synchronized IndexSearcher currentSearcher() throws IOException {
		if (isModified()) {
			forceOlderClose();

			this.dreader = DirectoryReader.open(dreader.directory());
			this.isearcher = new IndexSearcher(dreader);
			
			reloaded();
		}
		return isearcher ;
		
	}

	
	SearchResponse search(IndexSearcher csearcher, SearchConfig sconfig, final SearchRequestWrapper wrequest) throws IOException {
		return search(csearcher, sconfig, session ->{
			return session.createRequest(wrequest).find() ;
		}) ;
	}
	
	
	private <T> T search(IndexSearcher currentSearcher, SearchConfig sconfig, SearchJob<T> searchJob) throws IOException {
		SearchSession ssession = SearchSession.create(this, currentSearcher, sconfig); // create IndexSearcher
		T result = searchJob.handle(ssession);
		return result;
	}
	
	
	public SearchResponse search(String field, String value) throws IOException {
		return search(new TermQuery(new Term(field, value)));
	}

	public SearchResponse search(final Query query) throws IOException {
		return search(session ->{
			return session.createRequest(query).find() ;
		}) ;
	}
	
	private void forceOlderClose() {
		if (isearcher != null && isearcher.getIndexReader() != null) {
			try {
				isearcher.getIndexReader().decRef();
			} catch (IOException ignore) {
				ignore.printStackTrace();
			} finally {
				IOUtil.close(isearcher.getIndexReader());
				IOUtil.close(dreader);
			}
		}
	}

	public OpenMode openMode() {
		return openMode;
	}

	public Searcher newSearcher() throws IOException {
		return new Searcher(this);
	}

	public Searcher newSearcher(SearchController... appendController) throws IOException {
		return new Searcher(this, appendController) ;
	}

	public <T> T info(InfoHandler<T> infoHandler) throws IOException {
		return search(session ->{
			T result = infoHandler.view(session.indexReader(), dir());
			return result ;
		}) ;
	}
	

	
	
	
	
	
	private Suggester suggester = null ;
	public synchronized Suggester newSuggester(Analyzer analyzer) {
		if (suggester == null){
			this.suggester = new Suggester(this, analyzer);
		}
		return suggester ;
	}
	public Suggester newSuggester() {
		return newSuggester(IndexConfig.create(this).indexAnalyzer()) ;
	}

	
	public DefaultIndexConfig defaultIndexConfig() {
		return defaultIndexConfig;
	}

	public DefaultSearchConfig defaultSearchConfig() {
		return defaultSearchConfig;
	}




}
