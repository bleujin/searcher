package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.List;

import org.apache.ecs.xml.XML;
import org.apache.lucene.search.ScoreDoc;

import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.util.Debug;
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

	private static List<Integer> makeDocument(SearchRequest sreq, ScoreDoc[] docIds) {
		List<Integer> result = ListUtil.newList();

		for (int i = sreq.skip(); i < Math.min(sreq.limit(), docIds.length); i++) {
			result.add(docIds[i].doc);
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

	public long elapsedTime() {
		return endTime - startTime;
	}

	public SearchRequest request() {
		return sreq;
	}
	

	public XML toXML() {
		XML result = new XML("response");

		result.addAttribute("startTime", String.valueOf(startTime));
		result.addAttribute("elapsedTime", String.valueOf(elapsedTime()));
		result.addAttribute("totalCount", String.valueOf(totalCount()));
		result.addAttribute("size", String.valueOf(docIds.size()));

		return result;
	}

	public String toString() {
		return toXML().toString();
	}

	public void debugPrint() {
		eachDoc(EachDocHandler.DEBUG);
	}
	
	public void debugPrint(final String... fields) throws IOException {
		eachDoc(new EachDocHandler<Void>() {

			@Override
			public <T> T handle(EachDocIterator iter) {
				while (iter.hasNext()) {
					ReadDocument next = iter.next();
					List list = ListUtil.newList();
					list.add(next.toString());
					for (String field : fields) {
						list.add(next.asString(field));
					}
					Debug.line(list.toArray(new Object[0]));
				}
				return null;
			}
		});
	}
	
	public <T> T eachDoc(EachDocHandler<T> handler) {
		EachDocIterator iter = new EachDocIterator(ssession, sreq, docIds);
		return handler.handle(iter);
	}

	public int size() {
		return Math.min(docIds.size(), sreq.offset());
	}

}
