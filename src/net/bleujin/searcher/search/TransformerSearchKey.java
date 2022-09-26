package net.bleujin.searcher.search;

import java.util.List;

public class TransformerSearchKey {

	private SearchSession session;
	private List<Integer> docs;
	private SearchRequest sreq;

	public TransformerSearchKey(SearchSession session, List<Integer> docs, SearchRequest sreq) {
		this.session = session;
		this.docs = docs;
		this.sreq = sreq;
	}

	public SearchSession session() {
		return session;
	}

	public List<Integer> docs() {
		return docs;
	}

	public SearchRequest request() {
		return sreq;
	}

}
