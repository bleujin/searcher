package net.bleujin.searcher.index;

import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.MyField.MyFieldType;
import net.bleujin.searcher.common.WriteDocument;
import net.ion.framework.util.DateUtil;

public class TestDocumentFieldType extends AbTestCase {

	
	public void testIndex() throws Exception {
		
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).text("text", "hello world").date("birth", new Date()).insert();
				return null;
			}
		});

		assertEquals(1, sdc.search("name:bleujin").totalCount());
		assertEquals(1, sdc.search("text:hello").totalCount());
		assertEquals(1, sdc.search("age:20").totalCount());
		assertEquals(1, sdc.search("birth:" + DateUtil.currentDateToString("yyyyMMdd")).totalCount());

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.loadDocument("bleujin", true, new FieldLoadable() {
					@Override
					public WriteDocument handle(WriteDocument result, Document findDoc) throws IOException {
						result.number("age", findDoc.getField("age").numericValue().longValue()).keyword("name", "hero").text("text", findDoc.getField("text").stringValue()).updateVoid();
						return null;
					}
				});
				return null;
			}
		});

		assertEquals(1, sdc.search("name:hero").totalCount());
		assertEquals(1, sdc.search("text:hello").totalCount());
		assertEquals(1, sdc.search("age:20").totalCount());
	}
	
	public void testKetyword() throws Exception {
		sdc.index(isession ->{  // indexing whitespaceAnalyzer
			return isession.newDocument("1").unknown("name", "H & P").insertVoid() ; // text
		}) ;
		
		sdc.search(session ->{
			session.searchConfig().queryAnalyzer(new KeywordAnalyzer()) ;
			assertEquals(0, session.createRequest("name:\"H & P\"").find().totalCount()) ; // if keywordAnalyzer used, not found
			return null;
		}) ;

		sdc.search(session ->{
			assertEquals(1, session.createRequest("name:\"H & P\"").find().totalCount()) ; // if whitespaceAnalyzer used, found
			return null;
		}) ;


		
		
		sdc.index(isession ->{ // indexing KeywordAnalzyer 
			isession.indexConfig().fieldAnalyzer("name", new KeywordAnalyzer()) ;
			return isession.newDocument("1").unknown("name", "H & P").insertVoid() ; // text
		}) ;
		sdc.search(session ->{
			session.searchConfig().queryAnalyzer(new KeywordAnalyzer()) ;
			assertEquals(1, session.createRequest("name:\"H & P\"").find().totalCount()) ; // if keywordAnalyzer used, found
			return null;
		}) ;

		sdc.search(session ->{
			assertEquals(1, session.createRequest("name:\"H & P\"").find().totalCount()) ; // if whitespaceAnalyzer used, not found
			return null;
		}) ;
	
	}
	
	public void testNumber() throws Exception {
		sdc.index(isession ->{  // indexing stringType
			return isession.newDocument("1").keyword("name", "7").insertVoid() ; // text
		}) ;
		
		sdc.search(session ->{
			assertEquals(1, session.createRequest("name:7").find().totalCount()) ; // 
			return null;
		}) ;

		sdc.search(session ->{
			assertEquals(0, session.createRequest(NumericDocValuesField.newSlowRangeQuery("name", 1, 10)).find().totalCount()) ; // numberRange not working
			return null;
		}) ;
		
		sdc.index(isession ->{
			isession.deleteAll() ;
			return null ;
		}) ;

		
		sdc.index(isession ->{  // indexing numberType
			isession.indexConfig().fieldType("name", MyFieldType.Number) ;
			return isession.newDocument("1").keyword("name", "7").insertVoid() ; // text
		}) ;
		
		sdc.search(session ->{
			assertEquals(1, session.createRequest("name:7").find().totalCount()) ; // 
			return null;
		}) ;

		sdc.search(session ->{
			assertEquals(1, session.createRequest(NumericDocValuesField.newSlowRangeQuery("name", 1, 10)).find().totalCount()) ; // numberRange working
			return null;
		}) ;

		
	}
}
