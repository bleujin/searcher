package net.bleujin.searcher.search;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.search.Query;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.SearchRequestWrapper;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class TestQueryParser extends AbTestCase{

	public void testFieldRename() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().text("name", "bleujin hi").insert() ;
				isession.newDocument().text("name", "hero hi").insert() ;
				return null;
			}
		}) ;
		RenameQueryParser qparser = new RenameQueryParser("age", new StandardAnalyzer()).renameField("myname", "name") ;
		sdc.newSearcher().createRequest(qparser.parse("name:bleujin")).find().debugPrint("name");
		sdc.newSearcher().createRequest(qparser.parse("myname:bleujin")).find().debugPrint("name");
		
	}
	
	
	
	public void testTermRequest() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument().keyword("id", "/m/1234").insertVoid() ;
			}
		}) ;
		
		String qstring = "id:/m/1234";
		SearchRequestWrapper request = sdc.newSearcher().createRequestByTerm("id", "/m/1234") ;
		assertEquals(qstring, request.query().toString());
	}
	
	public void testBlankTerm() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument().insert() ;
				isession.newDocument("has").keyword("id", "").insert() ;
				return null ;
			}
		}) ;
		
		sdc.newSearcher().createRequest("*:* AND -id:[* TO *]").find().debugPrint();
		
		
	}
	
	public void testRange() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < 10; i++) {
					isession.newDocument(i * 3 + "").unknown("num", i * 3).insert() ;
				}
				return null ;
			}
		}) ;
		RenameQueryParser qparser = new RenameQueryParser("age", new StandardAnalyzer()).renameField("myname", "name") ;
		SearchResponse sres = new Searcher(sdc).parser(qparser).createRequest("num:[2 TO 12]").find() ;
		sres.debugPrint();
	}
		
}

//getFieldQuery
//getFuzzyQuery
//getPrefixQuery
//getRangeQuery
//getRegexpQuery
//getWildcardQuery
class RenameQueryParser extends ExtendableQueryParser {

	private Map<String, String> renameField = MapUtil.newCaseInsensitiveMap() ;

	public RenameQueryParser(String defaultField, Analyzer defaultAnalyzer) {
		super(defaultField, defaultAnalyzer);
	}

	
	public RenameQueryParser renameField(String newName, String sourceName){
		renameField.put(newName, sourceName) ;
		return this ;
	}
	
	private String fieldName(String fname){
		return ObjectUtil.coalesce(renameField.get(fname), fname) ;
	}
	
	// 
	protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {
		return super.getFieldQuery(fieldName(field), queryText, slop) ;
	}
	
	protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
		return super.getFieldQuery(fieldName(field), queryText, quoted) ;
	}
	
	protected Query getFunnzyQuery(String field, String termStr, float minSimilarity) throws ParseException{
		return super.getFuzzyQuery(fieldName(field), termStr, minSimilarity) ;
	}
	
	protected Query getPrefixQuery(String field, String termStr) throws ParseException{
		return super.getPrefixQuery(fieldName(field), termStr) ;
	}
	
	protected Query getRegexpQuery(String field, String termStr) throws ParseException{
		return super.getRegexpQuery(fieldName(field), termStr) ;
	}
	
	protected Query getWildcardQuery(String field, String termStr) throws ParseException{
		return super.getWildcardQuery(fieldName(field), termStr) ;
	}
	
	
	
	@Override
	public Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
		if (part1 != null && part2 != null && isNumeric(part1) && isNumeric(part2)) {
			return NumericDocValuesField.newSlowRangeQuery(fieldName(field), toLong(part1) + (startInclusive ? 0 : 1 ), toLong(part2) + (endInclusive ? 0 : -1) );
		} else {
			return super.getRangeQuery(fieldName(field), part1, part2, startInclusive, endInclusive);
		}
	}
	
	private static boolean isNumeric(String str) {
		return str.matches("[-+]*\\d+"); // match a number with optional '-' and decimal.
	}

	private static long toLong(String part) {
		if (StringUtil.isBlank(part))
			return 0L;
		return part.startsWith("+") ? Long.parseLong(part.substring(1)) : Long.parseLong(part);
	}

}
