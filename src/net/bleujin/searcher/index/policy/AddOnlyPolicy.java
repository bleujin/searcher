package net.bleujin.searcher.index.policy;

import java.io.IOException;

import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;

public class AddOnlyPolicy extends AbstractWritePolicy {

	public Action apply(final IndexSession writer, WriteDocument doc) throws IOException {
		writer.insertDocument(doc);
		return Action.Insert ;
	}

}