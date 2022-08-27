package net.bleujin.searcher.search;

import java.util.Iterator;
import java.util.List;

import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.db.Page;

public class PageResponse implements Iterable<ReadDocument> {

	private SearchResponse sres;
	private List<Integer> pageDocIds;
	private Page page;
	private List<Integer> inList;

	private PageResponse(SearchResponse sres, List<Integer> pageDocIds, Page page, List<Integer> inList) {
		this.sres = sres;
		this.pageDocIds = pageDocIds;
		this.page = page;
		this.inList = inList ;
	}

	public static PageResponse create(SearchResponse sres, List<Integer> pageDocIds, Page page, List<Integer> totalDocIds) {
		return new PageResponse(sres, pageDocIds, page, totalDocIds);
	}

	@Override
	public Iterator<ReadDocument> iterator() {
		return new EachDocIterator(sres.searchSession(), sres.request(), pageDocIds) ;
	}

	public void debugPrint() {
		eachDoc(EachDocHandler.DEBUG);
	}

	public <T> T eachDoc(EachDocHandler<T> handler) {
		EachDocIterator iter = new EachDocIterator(sres.searchSession(), sres.request(), pageDocIds);
		return handler.handle(iter);
	}

	

}
