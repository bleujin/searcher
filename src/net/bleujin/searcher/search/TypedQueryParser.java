package net.bleujin.searcher.search;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import com.google.common.collect.SetMultimap;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class TypedQueryParser extends ExtendableQueryParser {

	private SearchConfig sconfig ;
	public TypedQueryParser(String defaultField, Analyzer defaultAnalyzer, SearchConfig searchConfig) {
		super(defaultField, defaultAnalyzer);
		this.sconfig = searchConfig ;
	}
	
//	protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {
//		return super.getFieldQuery(field, queryText, slop) ;
//	}
//	
//	protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
//		return super.getFieldQuery(field, queryText, quoted) ;
//	}
//	
//	protected Query getFunnzyQuery(String field, String termStr, float minSimilarity) throws ParseException{
//		return super.getFuzzyQuery(field, termStr, minSimilarity) ;
//	}
//	
//	protected Query getPrefixQuery(String field, String termStr) throws ParseException{
//		return super.getPrefixQuery(field, termStr) ;
//	}
//	
//	protected Query getRegexpQuery(String field, String termStr) throws ParseException{
//		return super.getRegexpQuery(field, termStr) ;
//	}
//	
//	protected Query getWildcardQuery(String field, String termStr) throws ParseException{
//		return super.getWildcardQuery(field, termStr) ;
//	}
	
	@Override
	public Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
		if (sconfig.isNumField(field)) { 
			return NumericDocValuesField.newSlowRangeQuery(field, toLong(part1) + (startInclusive ? 0 : 1 ), toLong(part2) + (endInclusive ? 0 : -1) );
		} else if (part1 != null && part2 != null && isNumeric(part1) && isNumeric(part2)) {
			return NumericDocValuesField.newSlowRangeQuery(field, toLong(part1) + (startInclusive ? 0 : 1 ), toLong(part2) + (endInclusive ? 0 : -1) );
		} else {
			return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
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