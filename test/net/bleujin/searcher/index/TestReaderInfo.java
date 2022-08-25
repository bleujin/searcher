package net.bleujin.searcher.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.reader.InfoReader.InfoHandler;
import net.ion.framework.util.Debug;

public class TestReaderInfo extends AbTestCase {

	public void testIndexCommitUserData() throws Exception {
		sdc.index(createIndexJob("/hero", 2));
		sdc.index(createIndexJob("/jin", 3));
		sdc.index(isession ->{
			isession.indexConfig().commitData("last author", "bleujin") ;
			return null ;
		});

		sdc.infoReader().info(new InfoHandler<Void>() {
			@Override
			public Void view(IndexReader ireader, DirectoryReader dreader) throws IOException {
				List<IndexCommit> cms = DirectoryReader.listCommits(dreader.directory());

				for (IndexCommit ic : cms) {
					Debug.line(ic.getUserData(), ic.getSegmentsFileName(), dreader.getIndexCommit().getSegmentsFileName());
				}
				return null;
			}
		});
	}
}
