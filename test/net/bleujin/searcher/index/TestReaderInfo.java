package net.bleujin.searcher.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.reader.InfoHandler;
import net.ion.framework.util.Debug;

public class TestReaderInfo extends AbTestCase {

	public void testIndexCommitUserData() throws Exception {
		sdc.index(createIndexJob("/hero", 2));
		sdc.index(createIndexJob("/jin", 3));
		sdc.index(isession ->{
			isession.indexConfig().commitData("last author", "bleujin") ;
			return null ;
		});

		sdc.info(new InfoHandler<Void>() {
			@Override
			public Void view(IndexReader ireader, Directory dreader) throws IOException {
				List<IndexCommit> cms = DirectoryReader.listCommits(dreader);

				for (IndexCommit ic : cms) {
					Debug.line(ic.getUserData(), ic.getSegmentsFileName(), ic);
				}
				return null;
			}
		});
		
		
		
	}
}
