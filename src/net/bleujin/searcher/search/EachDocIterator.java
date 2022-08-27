package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.bleujin.searcher.common.ReadDocument;

public class EachDocIterator implements Iterator<ReadDocument>{


	private final SearchSession session;
	private Iterator<Integer> docIter;
	private SearchRequest req;
	private int count;

	public EachDocIterator(SearchSession session, SearchRequest req, List<Integer> docIds) {
		this.session = session ;
		this.req = req ;
		this.count = docIds.size() ;
		this.docIter = docIds.iterator() ;
	}

	@Override
	public boolean hasNext() {
		return docIter.hasNext();
	}

	public int count(){
		return count ;
	}
	
	@Override
	public ReadDocument next() {
		try {
			return session.readDocument(docIter.next(), req);
		} catch (IOException e) {
			throw new IllegalStateException(e) ;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("readOnly") ;
	}

}
