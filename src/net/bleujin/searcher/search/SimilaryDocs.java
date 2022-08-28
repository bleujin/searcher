package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.List;

import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.extend.SimilaryDoc;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

public class SimilaryDocs {

	private SearchResponse sres;
	private ReadDocument mydoc;
	private List<SimilaryDoc> result;

	public SimilaryDocs(SearchResponse searchResponse, ReadDocument mydoc, List<SimilaryDoc> result) {
		this.sres = searchResponse ;
		this.mydoc = mydoc ;
		this.result = result ;
	}

	public void debugPrint() {
		for (SimilaryDoc sdoc : result) {
			Debug.line(sdoc);
		}
	}

	public List<ReadDocument> docs() throws IOException{
		List<ReadDocument> rtn = ListUtil.newList() ;
		for (SimilaryDoc sdoc : result) {
			rtn.add(sres.searchSession().readDocument(sdoc.docId(), sres.request())) ;
		}
		return rtn ;
	}
	
	public SimilaryDocs limit(int i) {
		return new SimilaryDocs(sres, mydoc, result.subList(0, Math.min(i, result.size()))) ;
	}

	public <T> T eachDoc(EachDocHandler<T> eachDocHandler) {
		List<Integer> docIds = ListUtil.newList() ;
		for (SimilaryDoc sd : result) {
			docIds.add(sd.docId()) ;
		}
		return eachDocHandler.handle(new EachDocIterator(sres.searchSession(), sres.request(), docIds)) ;
	}

	public SimilaryDocs overWeight(double d) {
		List<SimilaryDoc> newList = ListUtil.newList() ;
		for (SimilaryDoc sd : result) {
			if (sd.simValue() >= d) newList.add(sd) ;
		}
		
		return new SimilaryDocs(sres, mydoc, newList) ;
	}

}
