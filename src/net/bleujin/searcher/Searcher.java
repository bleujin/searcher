package net.bleujin.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import net.bleujin.searcher.common.SearchConstant;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.search.processor.PostProcessor;
import net.bleujin.searcher.search.processor.PreProcessor;
import net.ion.framework.util.StringUtil;

public class Searcher {

	private SearchController sdc;
	private QueryParser parser;
	private final List<PostProcessor> postListeners = new ArrayList<PostProcessor>();
	private final List<PreProcessor> preListeners = new ArrayList<PreProcessor>();

	public Searcher(SearchController sdc) {
		this.sdc= sdc ;
		this.parser = new QueryParser(SearchConstant.ISALL_FIELD, sdc.sconfig().analyzer()) ;
	}

	public Searcher(SearchController sdc, QueryParser parser) {
		this.sdc= sdc ;
		this.parser = parser ;
	}

	
	public SearchRequestWrapper createRequest(String query) throws ParseException {
		Query pquery = StringUtil.isBlank(query) ? new MatchAllDocsQuery() : parser.parse(query);
		
		return createRequest(pquery);
	}

	public SearchRequestWrapper createRequest(String term, String value) throws ParseException {
		return createRequest(new TermQuery(new Term(term, value)));
	}

	public SearchRequestWrapper createRequest(String query, Analyzer analyzer) throws ParseException {
		QueryParser newParser = new QueryParser(SearchConstant.ISALL_FIELD, analyzer) ;
		Query pquery = StringUtil.isBlank(query) ? new MatchAllDocsQuery() : newParser.parse(query);
		
		return createRequest(pquery);
	}

	public SearchRequestWrapper createRequest(Query query) {
		return new SearchRequestWrapper(this, sdc, query);
	}

	public SearchResponse search(String query) throws IOException, ParseException {
		return createRequest(query).find() ;
	}

	public Query parseQuery(String query) throws ParseException {
		return parser.parse(query);
	}

	public Searcher addPostListener(final PostProcessor processor) {
		postListeners.add(processor) ;
		return this ;
	}
	
	public Searcher addPreListener(final PreProcessor processor) {
		preListeners.add(processor) ;
		return this ;
	}

	public List<PostProcessor> postListeners() {
		return postListeners ;
	}

	public List<PreProcessor> preListeners() {
		return preListeners ;
	}


	

}
