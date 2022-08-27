package net.bleujin.searcher.util;

import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;

import net.bleujin.searcher.Searcher;
import net.ion.framework.util.SetUtil;

public class QueryBuilder {


	private Set<Query> querys = SetUtil.newOrdereddSet();

	public QueryBuilder term(String fname, String... values){
		return add(QueryUtil.term(fname, values)) ;
	}


	public QueryBuilder termNot(String fname, String... values){
		return add(QueryUtil.not(QueryUtil.term(fname, values))) ;
	}


	public QueryBuilder query(String query, Searcher searcher) throws ParseException{
		return add(QueryUtil.query(query, searcher)) ;
	}
	
	
	private QueryBuilder add(Query query) {
		querys.add(query) ;
		return this ;
	}


	public QueryBuilder filter(Query query){
		add(query) ;
		return this ;
	}
	
	public QueryBuilder between(String fname, String lowerTerm, String upperTerm){
		return add(QueryUtil.between(fname, lowerTerm, upperTerm)) ;
	}
	
	public QueryBuilder gt(String fname, String lowerTerm){
		return add(QueryUtil.gt(fname, lowerTerm)) ;
	}

	public QueryBuilder gte(String fname, String lowerTerm){
		return add(QueryUtil.gte(fname, lowerTerm)) ;
	}

	public QueryBuilder lt(String fname, String upperTerm){
		return add(QueryUtil.lt(fname, upperTerm)) ;
	}

	public QueryBuilder lte(String fname, String upperTerm){
		return add(QueryUtil.lte(fname, upperTerm)) ;
	}

	public QueryBuilder between(String fname, long lowerTerm, long upperTerm){
		return add(QueryUtil.between(fname, lowerTerm, upperTerm)) ;
	}
	
	public QueryBuilder gt(String fname, long lowerTerm){
		return add(QueryUtil.gt(fname, lowerTerm)) ;
	}

	public QueryBuilder gte(String fname, long lowerTerm){
		return add(QueryUtil.gte(fname, lowerTerm)) ;
	}

	public QueryBuilder lt(String fname, long upperTerm){
		return add(QueryUtil.lt(fname, upperTerm)) ;
	}

	public QueryBuilder lte(String fname, long upperTerm){
		return add(QueryUtil.lte(fname, upperTerm)) ;
	}

	public Query andBuild() {
		return QueryUtil.and(querys) ;
	}

	public Query orBuild() {
		return QueryUtil.or(querys) ;
	}


}
