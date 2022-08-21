package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;

import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.util.ListUtil;

public class SearchResponse {

	private SearchSession ssession;
	private SearchRequest sreq;
	private List<Integer> docIds;
	private long totalCount;
	private long startTime;
	private long endTime;

	private SearchResponse(SearchSession ssession, SearchRequest sreq, List<Integer> docIds, long totalCount, long startTime) {
		this.ssession = ssession ;
		this.sreq = sreq ;
		this.startTime = startTime;
		this.endTime = System.currentTimeMillis();
		this.docIds = docIds;
		this.totalCount = totalCount;
	}
	
	public static SearchResponse create(SearchSession ssession, SearchRequest sreq, ScoreDoc[] docs, long totalCount, long startTime) throws IOException {
		return new SearchResponse(ssession, sreq, makeDocument(sreq, docs), totalCount, startTime);
	}

	private static List<Integer> makeDocument(SearchRequest sreq, ScoreDoc[] docs) {
		List<Integer> result = ListUtil.newList();

		for (int i = sreq.skip(); i < Math.min(sreq.limit(), docs.length); i++) {
			result.add(docs[i].doc);
		}
		
		return result;
	}

	public List<ReadDocument> getDocument() throws IOException {
		List<ReadDocument> result = ListUtil.newList();
		
		for(int docId : docIds) {
			result.add(ssession.readDocument(docId)) ;
		}
		return result;
	}

	public ReadDocument first() throws IOException {
		List<ReadDocument> list = getDocument();
		return list.isEmpty() ? null : list.get(0);
	}

	public long totalCount() {
		return this.totalCount;
	}

	

}
