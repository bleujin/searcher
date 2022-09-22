package net.bleujin.searcher.search;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilter;
import org.apache.lucene.analysis.ko.KoreanTokenizer.DecompoundMode;
import org.apache.lucene.analysis.ko.dict.UserDictionary;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.queryparser.classic.QueryParser;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.search.processor.StdOutProcessor;
import net.ion.framework.util.Debug;
import net.ion.framework.util.SetUtil;

public class TestKoreanAnalyzer extends AbTestCase {

	public void testAnalyzer() throws Exception {
		KoreanAnalyzer anal = new KoreanAnalyzer();
		String stmt = "재주_기예 사람 은존재한다. 서울e플러스펀드 SCH_B500 1(주식)종류A";
		TokenStream tokenStream = anal.tokenStream("abc", new StringReader(stmt));
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		
		tokenStream.reset();
		while (tokenStream.incrementToken()) {
		    int startOffset = offsetAttribute.startOffset();
		    int endOffset = offsetAttribute.endOffset();
		    String term = charTermAttribute.toString();
		    Debug.line(startOffset, endOffset, term);
		}
		
		Debug.line(new QueryParser("field", new KoreanAnalyzer()).parse("e플러스")) ;
	}
	
	public void testSearchQuery() throws Exception{
		String stmt = "재주_기예 사람 은존재한다. 서울e플러스펀드 SCH_B500 1(주식)종류A";
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(new KoreanAnalyzer()) ;
				return isession.newDocument().text("name", stmt).insertVoid();
			}
		}) ;

		SearchResponse res = sdc.search(session -> {
			StdOutProcessor stdOutProcessor = new StdOutProcessor();
			session.searchConfig().addPostListener(stdOutProcessor).queryAnalyzer(new KoreanAnalyzer());
			
			return session.createRequest("e플러스").find();
		}) ;
		
		res.debugPrint();
	}

	
	public void testToken() throws Exception {
		// Analyzer analyzer = new MyKoreanAnalyzer(Version.LUCENE_44, new CharArraySet(Version.LUCENE_44, ListUtil.toList("생각"), true)) ;// new CJKAnalyzer(Version.LUCENE_44, new CharArraySet(Version.LUCENE_44, ListUtil.toList("생각"), true)) ;
		
		Analyzer analyzer = new KoreanAnalyzer(UserDictionary.open(new StringReader("")), DecompoundMode.NONE, KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS, true) ;
		TokenStream tokenStream = analyzer.tokenStream("text", "짜라투스는 키보드를 쳤다. 사람이 존재하는 이유는 생각하기 때문인가");
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

		tokenStream.reset();
		while (tokenStream.incrementToken()) {
		    int startOffset = offsetAttribute.startOffset();
		    int endOffset = offsetAttribute.endOffset();
		    String term = charTermAttribute.toString();
		    Debug.line(startOffset, endOffset, term);
		}
		analyzer.close(); 
	}
	
	
	
	
	public boolean findKeyword(boolean printTerm, final String stmt, String term) throws Exception {
		return sdc.search(session -> {
			StdOutProcessor stdOutProcessor = new StdOutProcessor();
			session.searchConfig().addPostListener(stdOutProcessor).queryAnalyzer(new KoreanAnalyzer());
			
			return session.createRequest(term).find().size() > 0;
		}) ;
	}
	
	
	public void testStopWord() throws Exception {
		CharArraySet swords = CharArraySet.copy(SetUtil.create("hero"));
		final StandardAnalyzer analyzer = new StandardAnalyzer(swords) ;
		
		sdc.index(isesson ->{
			isesson.indexConfig().indexAnalyzer(analyzer) ;
			return isesson.newDocument().text("names", "bleujin hero jin").insertVoid() ;
		}) ;
		
		assertEquals(1, sdc.newSearcher().createRequest("bleujin").find().size()) ;
		assertEquals(0, sdc.newSearcher().createRequest("hero").find().size()) ;
		assertEquals(1, sdc.newSearcher().createRequest("jin").find().size()) ;
	}
	
	
	
	
	
	
	public void xtestPrintType() throws Exception {
		Debug.debug("COMBINING_SPACING_MARK", Character.COMBINING_SPACING_MARK) ;
		Debug.debug("CONNECTOR_PUNCTUATION", Character.CONNECTOR_PUNCTUATION) ;
		Debug.debug("CONTROL", Character.CONTROL) ;
		Debug.debug("CURRENCY_SYMBOL", Character.CURRENCY_SYMBOL) ;
		Debug.debug("DASH_PUNCTUATION", Character.DASH_PUNCTUATION) ; 
		Debug.debug("DECIMAL_DIGIT_NUMBER", Character.DECIMAL_DIGIT_NUMBER) ;
		Debug.debug("ENCLOSING_MARK", Character.ENCLOSING_MARK) ;
		Debug.debug("END_PUNCTUATION", Character.END_PUNCTUATION) ;
		Debug.debug("FINAL_QUOTE_PUNCTUATION", Character.FINAL_QUOTE_PUNCTUATION) ; 
		Debug.debug("FORMAT", Character.FORMAT) ;
		Debug.debug("INITIAL_QUOTE_PUNCTUATION", Character.INITIAL_QUOTE_PUNCTUATION) ;
		Debug.debug("LETTER_NUMBER", Character.LETTER_NUMBER) ;
		Debug.debug("LINE_SEPARATOR", Character.LINE_SEPARATOR) ;
		Debug.debug("LOWERCASE_LETTER", Character.LOWERCASE_LETTER) ; 
		Debug.debug("MATH_SYMBOL", Character.MATH_SYMBOL) ;
		Debug.debug("MODIFIER_LETTER", Character.MODIFIER_LETTER) ;
		Debug.debug("MODIFIER_SYMBOL", Character.MODIFIER_SYMBOL) ;
		Debug.debug("NON_SPACING_MARK", Character.NON_SPACING_MARK) ; 
		Debug.debug("OTHER_LETTER", Character.OTHER_LETTER) ; 
		Debug.debug("OTHER_NUMBER", Character.OTHER_NUMBER) ; 
		Debug.debug("OTHER_PUNCTUATION", Character.OTHER_PUNCTUATION) ;
		Debug.debug("OTHER_SYMBOL", Character.OTHER_SYMBOL) ;
		Debug.debug("PARAGRAPH_SEPARATOR", Character.PARAGRAPH_SEPARATOR) ;
		Debug.debug("PRIVATE_USE", Character.PRIVATE_USE) ;
		Debug.debug("SPACE_SEPARATOR", Character.SPACE_SEPARATOR) ;
		Debug.debug("START_PUNCTUATION", Character.START_PUNCTUATION ) ; 
		Debug.debug("SURROGATE", Character.SURROGATE) ; 
		Debug.debug("TITLECASE_LETTER", Character.TITLECASE_LETTER) ;
		Debug.debug("UNASSIGNED", Character.UNASSIGNED) ;
		Debug.debug("UPPERCASE_LETTER", Character.UPPERCASE_LETTER) ;
	}
	
}
