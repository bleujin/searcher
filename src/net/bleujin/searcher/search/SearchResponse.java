package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.ecs.xml.XML;
import org.apache.lucene.search.ScoreDoc;

import com.google.common.base.Predicate;

import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;

public class SearchResponse {

	private SearchSession ssession;
	private SearchRequest sreq;
	private List<Integer> docIds;
	private long totalCount;
	private long startTime;
	private long endTime;

	private SearchResponse(SearchSession ssession, SearchRequest sreq, List<Integer> docIds, long totalCount, long startTime) {
		this.ssession = ssession;
		this.sreq = sreq;
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

		for (int docId : docIds) {
			ReadDocument rdoc = ssession.readDocument(docId, sreq);
			result.add(rdoc);
		}
		return result;
	}

	public PageResponse getDocument(Page page) {
		List<Integer> result = ListUtil.newList();

		for (int i = page.getStartLoc(); i < Math.min(page.getEndLoc(), docIds.size()); i++) {
			result.add(docIds.get(i));
		}

		return PageResponse.create(this, result, page, docIds);
	}

	ReadDocument documentById(int docId) throws IOException {
		return ssession.readDocument(docId, sreq);
	}

	public ReadDocument documentById(final String docIdValue) throws IOException {
		return eachDoc(new EachDocHandler<ReadDocument>() {
			@Override
			public ReadDocument handle(EachDocIterator iter) {
				while (iter.hasNext()) {
					ReadDocument rdoc = iter.next();
					if (StringUtil.equals(docIdValue, rdoc.idValue()))
						return rdoc;
				}
				throw new IllegalArgumentException("not found doc : " + docIdValue);
			}
		});
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

	public void debugPrint(final String... fields) {
		eachDoc(new EachDocHandler<Void>() {

			@Override
			public Void handle(EachDocIterator iter) {
				while (iter.hasNext()) {
					ReadDocument next = iter.next();
					List<String> list = ListUtil.newList();
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

	public SearchSession searchSession() {
		return ssession;
	}

	public Integer[] docIds() {
		return docIds.toArray(new Integer[0]);
	}

	
	Iterable<ReadDocument> readIterable() {
		List<Integer> myDocIds = this.docIds ;
		
		return new Iterable<ReadDocument>() {
			@Override
			public Iterator<ReadDocument> iterator() {
				Iterator<Integer> docIter = myDocIds.iterator() ;
				
				return new Iterator<ReadDocument>() {
					public boolean hasNext() {
						return docIter.hasNext();
					}

					public ReadDocument next() {
						try {
							return documentById(docIter.next()) ;
						} catch (IOException e) {
							throw new IllegalArgumentException(e.getCause()) ;
						}
					}
				};
			}
			
		};
	}
	

	Iterable<WriteDocument> writeIterable(IndexSession isession) {
		List<Integer> myDocIds = this.docIds ;
		
		return new Iterable<WriteDocument>() {
			@Override
			public Iterator<WriteDocument> iterator() {
				Iterator<Integer> docIter = myDocIds.iterator() ;
				
				return new Iterator<WriteDocument>() {
					public boolean hasNext() {
						return docIter.hasNext();
					}

					public WriteDocument next() {
						try {
							ReadDocument rdoc = documentById(docIter.next());
							return new WriteDocument(isession, rdoc.IdString(), rdoc.toLuceneDoc()) ;
						} catch (IOException e) {
							throw new IllegalArgumentException(e.getCause()) ;
						}
					}
				};
			}
			
		};
	}
	
	public ReadStream readStream() {
		return new ReadStream(ssession, StreamSupport.stream(this.readIterable().spliterator(), false)) ;
	}

	public WriteStream writeStream(IndexSession isession) {
		return new WriteStream(isession, StreamSupport.stream(this.writeIterable(isession).spliterator(), false)) ;
	}

	public long startTime() {
		return startTime ;
	}

}
