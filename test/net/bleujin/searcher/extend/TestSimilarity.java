package net.bleujin.searcher.extend;


import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Field.Store;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.IKeywordField;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.index.VTextField;
import net.bleujin.searcher.search.SearchRequest;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.search.SimilaryDocs;
import net.ion.framework.util.Debug;

public class TestSimilarity extends AbTestCase {

	public void testStoreVector() throws Exception {
		Debug.line(new VTextField("name", "hello bleujin", Store.YES).fieldType().storeTermVectorPositions()) ;
		
	}
	
	public void testFindSimilaryDoc() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("111").text("cook", "육류, 치즈").text("note", "깊고 짙은 레드 컬러로 믿을 수 없을 정도로 진한 체리, 블랙 커런트, 흙 자두의 풍미가 토스트된 오크향과 함께 조화롭게 어우러져 피어오른다. 농익은 과일류의 풍미와 탄닌 균형감이 인상적이다.").insertVoid();
				isession.newDocument("222").text("cook", "육류, 치즈").text("note", "블랙 커런트와 절인 체리와 같은 아주 짙은 아로마를 느낄 수 있다. 잼과 같은 리치한 맛과 신선한 과실의 피니시를 동시에 느낄 수 있다. ").insertVoid();
				isession.newDocument("333").text("cook", "샐러드, 해산물, 치즈").text("note", "눈부신 페일 옐로우 계열, 아몬드 계열의 컬러감을 느낄 수 있다. 파인애플, 매우 신선한 레몬과 자몽향에 화이트 플로랄 계열로 소비뇽 블랑 특유의 향를 잘 나타낸다. 신선하고 아로마틱한 향과 더불어 크리스피한 느낌을 느낄 수 있다. 가벼운 바디감으로 음식과의 매칭이나 가볍게 즐기기에 제격이다.").insertVoid();
				return null;
			}
		}) ;
		
		SimilaryDocs sdocs = sdc.search(session ->{
			ReadDocument fdoc = session.createRequestByKey("111").findOne() ;
			SearchResponse sr = session.createRequest("").find() ;
			
			SimilaryDocs sd = session.similaryDocs(fdoc, "cook", sr) ;
			
			return sd.limit(5).overWeight(0.01d);
		}) ;
		
		
		sdocs.debugPrint(); 
		sdocs.docs().forEach(doc -> Debug.line(doc));
	}
	
}