package net.bleujin.searcher.search;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.reader.InfoReader;
import net.bleujin.searcher.reader.InfoReader.InfoHandler;
import net.ion.framework.util.Debug;

public class TestReader extends AbTestCase {

	public void testCommit() throws Exception {
		sdc.index(IndexJob.SAMPLE_INSERT) ;
		
		InfoReader reader = sdc.search(session ->{
			return session.infoReader() ;
		}) ;
		
		reader.info(new InfoHandler<Void>() {
			@Override
			public Void view(IndexReader ireader, DirectoryReader dreader) throws IOException {
				for (IndexCommit commit : DirectoryReader.listCommits(dreader.directory())) {
					Debug.debug(commit.getDirectory(), commit.getFileNames(), commit.getSegmentsFileName(), commit.getUserData());
					Debug.debug(commit.isDeleted(), commit.getGeneration());
				}
				return null;
			}
		});

		// central.testIndexer(getAnalyzer()).end() ;
	}
}
