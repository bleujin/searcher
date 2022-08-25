package net.bleujin.searcher.index.policy;

import java.io.IOException;

import org.apache.lucene.index.Term;

import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;

public class DeleteTermPolicy extends AbstractWritePolicy {
	private Term term;

	public DeleteTermPolicy(Term term) {
		this.term = term;
	}

	@Override
	public void begin(IndexSession session){
		try {
			session.deleteTerm(term);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Action apply(IndexSession session, WriteDocument doc) throws IOException {
		return Action.Delete;
	}

}
