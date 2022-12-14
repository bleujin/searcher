package net.bleujin.searcher.util;

import java.util.Collection;
import java.util.Set;

import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;

import net.ion.framework.util.SetUtil;

public class QueryUtil {

	public static QueryBuilder newBuilder(){
		return new QueryBuilder() ;
	} 
	
	public final static Query and(Query query1, Query query2) {
		return and(new Query[] { query1, query2 });
	}

	public final static Query and(Query... querys) {
		if (querys == null || querys.length == 0) return null ;
		
		Builder result = new BooleanQuery.Builder() ;
		int queryCount = 0 ;
		for (Query query : querys) {
			if (query == null) continue ;
			queryCount++ ;
			result.add(query, BooleanClause.Occur.MUST);
		}
		if (queryCount < 1) return null ;
		return result.build();
	}


	public final static Query or(Query... querys) {
		if (querys == null || querys.length == 0) return null ;

		Builder result = new BooleanQuery.Builder() ;
		int queryCount = 0 ;
		for (Query query : querys) {
			if (query == null) continue ;
			queryCount++ ;
			result.add(query, Occur.SHOULD);
		}
		if (queryCount < 1) return null ;
		return result.build();
	}
	

	public final static Query not(Query... querys) {
		if (querys == null || querys.length == 0) return null ;

		Builder result = new BooleanQuery.Builder() ;
		int queryCount = 0 ;
		for (Query query : querys) {
			if (query == null) continue ;
			queryCount++ ;
			result.add(query, Occur.MUST_NOT);
		}
		if (queryCount < 1) return null ;
		return result.build();
	}


	public static Query and(Collection<Query> querys) {
		return and (querys.toArray(new Query[0]));
	}

	public static Query or(Set<Query> querys) {
		return or (querys.toArray(new Query[0]));
	}

	
	public static Query term(String fname, String... values){
		Set<Query> set = SetUtil.newSet() ;
		for (String value : values) {
			set.add(new TermQuery(new Term(fname, value))) ;
		}
		return and(set) ;
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
		return between(fname, lowerTerm+1, Long.MAX_VALUE) ;
	}

	public static Query gte(String fname, long lowerTerm){
		return between(fname, lowerTerm, Long.MAX_VALUE) ;
	}

	public static Query lt(String fname, long upperTerm){
		return between(fname, Long.MIN_VALUE, upperTerm-1) ;
	}

	public static Query lte(String fname, long upperTerm){
		return between(fname, Long.MIN_VALUE, upperTerm) ;
	}


	
	
	
	

	
}
