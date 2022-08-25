package net.bleujin.searcher.index.policy;

import java.io.IOException;

import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;

public class UpdatePolicy extends AbstractWritePolicy {

	public Action apply(final IndexSession session, WriteDocument doc) throws IOException {
		return session.updateDocument(doc);
	}

}
