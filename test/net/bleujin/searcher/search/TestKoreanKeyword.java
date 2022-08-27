package net.bleujin.searcher.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.AnalyzerUtil;
import net.bleujin.searcher.search.processor.StdOutProcessor;
import net.ion.framework.util.Debug;

public class TestKoreanKeyword extends AbTestCase {

	public void testImsi() throws Exception {
		final String val1 = "abc 서울E플러스 펀드 SCH-B500 1(주식) 종류A 2000년 9월 30일 그 일이 일어났다. 4.19 의거 발생일  급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다";
		StringBuilder sb = new StringBuilder();
		sb.append("7756 LG U+ 알리안츠Best중소형증권투자신탁[주] 미래에셋ASEAN업종대표증권자투자신탁 1(주식)종류A 필요가 없다 正道 한요가 서울E플러스 펀드 SCH-B500 1(주식)종류A 2000년 9월 30일 그 일이 일어났다. 4.19의거 발생일 급락조짐을 보였으며 살펴 보기에는 아마도 그럴것이다");
		
		StdOutProcessor stdOutProcessor = new StdOutProcessor();
		
		sdc.index(isession ->{
			isession.indexConfig().indexAnalyzer(new KoreanAnalyzer()) ;
			isession.newDocument("s1").text("val", val1).insert() ;
			isession.newDocument("s2").text("val", sb.toString()).insert() ;
			return null ;
		}) ;
		
		
		sdc.search(session ->{
			session.searchConfig().queryAnalyzer(new KoreanAnalyzer()); //.addPostListener(stdOutProcessor);

			assertEquals(1, session.createRequest("LG U+").find().size());
			assertEquals(2, session.createRequest("2000년 9월").find().size());
			assertEquals(1, session.createRequest("BEST 중소형").find().size());
			assertEquals(2, session.createRequest("급락").find().size());
			assertEquals(2, session.createRequest("조짐").find().size());
			assertEquals(2, session.createRequest("4.19 의거").find().size());
			assertEquals(1, session.createRequest("正道").find().size());
			assertEquals(2, session.createRequest("E플러스").find().size());
			assertEquals(2, session.createRequest("val:E플러스").find().size());
			assertEquals(2, session.createRequest("B500").find().size());
			return null ;
		}) ;
		
		printTerm(new KoreanAnalyzer(), val1) ;

	}

	public void testQuery() throws Exception {
		Query query = new QueryParser("", new KoreanAnalyzer()).parse("E플러스");
		Debug.debug(query.toString());
	}

	private void printTerm(Analyzer analyzer, String source) throws Exception {
		String[] tokens = AnalyzerUtil.toToken(analyzer, source);

		Debug.line(tokens);
	}

}
