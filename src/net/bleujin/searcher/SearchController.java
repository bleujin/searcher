package net.bleujin.searcher;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.index.IndexConfig;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.search.SearchConfig;
import net.bleujin.searcher.search.SearchJob;
import net.bleujin.searcher.search.SearchSession;
import net.ion.framework.util.Debug;

public class SearchController {

	private SearchControllerConfig config;
	private Directory dir;
	private OpenMode openMode;
	private boolean isModified = true;
	private IndexSearcher isearcher = null; // recycle searcher
	private IndexSearcher olderSearcher = null;

	public SearchController(SearchControllerConfig config, Directory dir, OpenMode openMode) {
		this.config = config;
		this.dir = dir;
		this.openMode = openMode;
	}

	public SearchControllerConfig sconfig() {
		return config;
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
		IndexConfig iconfig = IndexConfig.create(this);

		begin(iconfig.name());
		IndexSession indexSession = null;
		T result = null;

		try {
			indexSession = IndexSession.create(this, iconfig); // create indexWriter
			result = indexJob.handle(indexSession);

			indexSession.commit();
		} catch (IOException e) {
			indexSession.rollback();
		} finally {
			indexSession.forceClose();
			end();
		}

		return result;
	}

	public <T> T search(SearchJob<T> searchJob) throws IOException {
		SearchConfig sconfig = SearchConfig.create(this);

		synchronized (this) { // if not modified, recycle IndexSearcher
			if (isModified()) {
				this.olderSearcher = this.isearcher;
				
				if (! DirectoryReader.indexExists(sconfig.dir())) {
					index(isession -> { // index blank
						return null ;
					}) ;
				}
				
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

}
