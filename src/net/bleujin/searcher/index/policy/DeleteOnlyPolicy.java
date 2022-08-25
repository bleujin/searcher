package net.bleujin.searcher.index.policy;

import java.io.IOException;

import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;

public class DeleteOnlyPolicy extends AbstractWritePolicy{

	public DeleteOnlyPolicy(){
	}
	
	public Action apply(IndexSession session, WriteDocument doc) throws IOException {
		return session.deleteDocument(doc);
	}

}
