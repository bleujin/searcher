package net.bleujin.searcher.index.policy;

import java.io.IOException;

import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;

public class RecreatePolicy extends AbstractWritePolicy {

	@Override
	public void begin(IndexSession session){
		try {
			session.deleteAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Action apply(IndexSession session, WriteDocument doc) throws IOException {
		return session.insertDocument(doc);
	}

}
