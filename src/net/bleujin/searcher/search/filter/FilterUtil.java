package net.bleujin.searcher.search.filter;

import java.util.Collection;
import java.util.Set;

import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.bleujin.searcher.Searcher;
import net.ion.framework.util.SetUtil;

public class FilterUtil {

	public static FilterBuilder newBuilder(){
		return new FilterBuilder() ;
	} 
	
	public final static Query and(Query filter1, Query filter2) {
		return and(new Query[] { filter1, filter2 });
	}

	public final static Query and(Query... filters) {
		if (filters == null || filters.length == 0) return null ;

		BooleanQuery.Builder result = new BooleanQuery.Builder() ;
		int filterCount = 0 ;
		for (Query filter : filters) {
			if (filter == null) continue ;
			filterCount++ ;
			result.add(new BooleanClause(filter, Occur.MUST));
		}
		if (filterCount < 1) return null ;
		return result.build();
	}


	public final static Query or(Query... filters) {
		if (filters == null || filters.length == 0) return null ;

		BooleanQuery.Builder result = new BooleanQuery.Builder() ;
		int filterCount = 0 ;
		for (Query filter : filters) {
			if (filter == null) continue ;
			filterCount++ ;
			result.add(new BooleanClause(filter, Occur.SHOULD));
		}
		if (filterCount < 1) return null ;
		return result.build();
	}
	

	public final static Query not(Query... filters) {
		if (filters == null || filters.length == 0) return null ;

		BooleanQuery.Builder result = new BooleanQuery.Builder() ;
		int filterCount = 0 ;
		for (Query filter : filters) {
			if (filter == null) continue ;
			filterCount++ ;
			result.add(new BooleanClause(filter, Occur.MUST_NOT));
		}
		if (filterCount < 1) return null ;
		return result.build();
	}


	public static Query and(Collection<Query> filters) {
		return and (filters.toArray(new Query[0]));
	}

	public static Query or(Set<Query> filters) {
		return or (filters.toArray(new Query[0]));
	}

	
	public static Query term(String fname, String... values){
		Set<BytesRef> set = SetUtil.newSet() ;
		for (String value : values) {
			set.add(new BytesRef(value)) ;
		}
		return new TermInSetQuery(fname, set) ;
	}

	public static Query query(String query, Searcher searcher) throws ParseException{
		return and(searcher.parser().parse(query)) ;
	}
	
	public static TermRangeQuery between(String fname, String lowerTerm, String upperTerm){
		return new TermRangeQuery(fname, new BytesRef(lowerTerm), new BytesRef(upperTerm), true, true) ;
	}
	
	public static TermRangeQuery gt(String fname, String lowerTerm){
		return new TermRangeQuery(fname, new BytesRef(lowerTerm), null, false, true) ;
	}

	public static TermRangeQuery gte(String fname, String lowerTerm){
		return new TermRangeQuery(fname, new BytesRef(lowerTerm), null, true, true) ;
	}

	public static TermRangeQuery lt(String fname, String upperTerm){
		return new TermRangeQuery(fname, null, new BytesRef(upperTerm), true, false) ;
	}

	public static TermRangeQuery lte(String fname, String upperTerm){
		return new TermRangeQuery(fname, null, new BytesRef(upperTerm), true, true) ;
	}


	public static Query between(String fname, long lowerTerm, long upperTerm){
		return NumericDocValuesField.newSlowRangeQuery(fname, lowerTerm, upperTerm) ;
	}
	
	public static Query gt(String fname, long lowerTerm){
		return NumericDocValuesField.newSlowRangeQuery(fname, lowerTerm+1, Long.MAX_VALUE) ;
	}

	public static Query gte(String fname, long lowerTerm){
		return NumericDocValuesField.newSlowRangeQuery(fname, lowerTerm, Long.MAX_VALUE) ;
	}

	public static Query lt(String fname, long upperTerm){
		return NumericDocValuesField.newSlowRangeQuery(fname, Long.MIN_VALUE, upperTerm-1) ;
	}

	public static Query lte(String fname, long upperTerm){
		return NumericDocValuesField.newSlowRangeQuery(fname, Long.MIN_VALUE, upperTerm) ;
	}


	
	
	
	

	
}
