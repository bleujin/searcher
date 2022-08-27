package net.bleujin.searcher;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.index.IndexConfig;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.reader.InfoReader;
import net.bleujin.searcher.search.SearchConfig;
import net.bleujin.searcher.search.SearchJob;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.search.SearchSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

public class SearchController {

	private SearchControllerConfig config;
	private Directory dir;
	private OpenMode openMode;
	private boolean isModified = true;
	private IndexSearcher isearcher = null; // recycle searcher
	private IndexSearcher olderSearcher = null;
	private final ReadWriteLock locker = new ReentrantReadWriteLock() ;

	SearchController(SearchControllerConfig config, Directory dir, OpenMode openMode) {
		this.config = config;
		this.dir = dir;
		this.openMode = openMode;
	}

	public SearchControllerConfig sconfig() {
		return config;
	}

	public ReadWriteLock locker() {
		return locker ;
	}
	
	public Directory dir() {
		return dir;
	}

	public IndexConfig indexConfig() {
		return IndexConfig.create(this);
	}

	public void close() throws IOException {
		dir.close();
	}

	@Deprecated // OnlyTest
	public void destroySelf() throws IOException { 
		try {
			IndexWriterConfig conf = new IndexWriterConfig() ;
			IndexWriter indexWriter  = new IndexWriter(dir, conf);
            indexWriter.deleteAll();
            indexWriter.commit();
            IOUtil.close(indexWriter);
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} finally {
			dir.close();
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
				final IndexSession inner = indexSession ;
				rtn = indexJob.handle(inner);

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
	
	
	
	
	public <T> T search(SearchJob<T> searchJob) throws IOException {
		SearchConfig sconfig = SearchConfig.create(this);
		return search(sconfig, searchJob);
	}

	public SearchResponse search(final String query) throws IOException {
		return search(session ->{
			return session.createRequest(query).find() ;
		}) ;
	}
	
	
	SearchResponse search(SearchConfig sconfig, final SearchRequestWrapper wrequest) throws IOException {
		return search(sconfig, session ->{
			return session.createRequest(wrequest).find() ;
		}) ;
	}
	
	
	
	<T> T search(SearchConfig sconfig, SearchJob<T> searchJob) throws IOException {
		synchronized (this) { // if not modified, recycle IndexSearcher
			if (isModified()) {
				this.olderSearcher = this.isearcher;

				
				DirectoryReader dreader = DirectoryReader.open(sconfig.dir());
				isearcher = new IndexSearcher(dreader);
				reloaded();
			}
		}

		SearchSession ssession = SearchSession.create(this, this.isearcher, sconfig); // create IndexSearcher
		T result = searchJob.handle(ssession);
		forceOlderClose();
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
	
	public InfoReader infoReader() throws IOException {
		return search(session ->{
			return session.infoReader() ;
		}) ;
	}

	public IndexReader indexReader() throws IOException {
		return search(session ->{
			return session.indexReader() ;
		}) ;
	}

	
	private void forceOlderClose() {
		if (this.olderSearcher != null) {
			try {
				this.olderSearcher.getIndexReader().close();
			} catch (IOException ignore) {
			}
		}
	}

	public OpenMode openMode() {
		return openMode;
	}

	public Searcher newSearcher() {
		return new Searcher(this);
	}


}
