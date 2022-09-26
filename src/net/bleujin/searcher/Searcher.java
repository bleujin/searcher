package net.bleujin.searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import net.bleujin.searcher.common.IKeywordField;
import net.bleujin.searcher.common.SearchConstant;
import net.bleujin.searcher.search.SearchConfig;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.search.SearchSession;
import net.bleujin.searcher.search.processor.PostProcessor;
import net.bleujin.searcher.search.processor.PreProcessor;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;

public class Searcher {

	private SearchController sdc;
	private QueryParser parser;
	private SearchController[] appendController;
	private SearchConfig sconfig;
	public final static Query MatchAllDocsQuery = new MatchAllDocsQuery() ;

	public Searcher(SearchController sdc) throws IOException {
		this(sdc, new SearchController[0]) ;
	}

	public Searcher(SearchController sdc, SearchController[] appendController) throws IOException {
		this.sdc = sdc ;
		this.sconfig = SearchConfig.create(sdc) ;
		this.parser = sconfig.queryParser()  ;
		this.appendController = appendController ;
	}

	public Searcher parser(QueryParser parser) {
		this.parser = parser ;
		return this ;
	}
	
	public QueryParser parser() {
		return this.parser ;
	}
	
	public SearchConfig sconfig() {
		return sconfig ;
	}
	
	SearchResponse search(SearchConfig sconfig, SearchRequestWrapper searchRequest) throws IOException {
		if (appendController.length == 0) { // single searcher
			return sdc.search(sdc.currentSearcher(), sconfig, searchRequest);
		} else { // multi searcher
			List<IndexReader> readers = ListUtil.newList() ;
			readers.add(sdc.currentSearcher().getIndexReader()) ;
			for (SearchController appender : appendController) {
				readers.add(appender.currentSearcher().getIndexReader()) ;
			}
			
			MultiReader mreader = new MultiReader(readers.toArray(new IndexReader[0]));
			return sdc.search(new IndexSearcher(mreader), sconfig, searchRequest);
		}
	}

	
	public SearchRequestWrapper createRequest(String query) throws ParseException {
		Query pquery = StringUtil.isBlank(query) ? MatchAllDocsQuery : parser.parse(query);
		
		return createRequest(pquery);
	}
	
	public SearchRequestWrapper createRequestByKey(String docId) throws ParseException {
		return createRequestByTerm(IKeywordField.DocKey, docId);
	}



	public SearchRequestWrapper createRequestByTerm(String term, String value) throws ParseException {
		return createRequest(new TermQuery(new Term(term, value)));
	}


	public SearchRequestWrapper createRequest(Query query) {
		return new SearchRequestWrapper(this, sdc, query);
	}

	public SearchResponse search(String query) throws IOException, ParseException {
		return createRequest(query).find() ;
	}

	public Searcher addPostListener(final PostProcessor processor) {
		sconfig.addPostListener(processor) ;
		return this ;
	}
	
	public Searcher addPreListener(final PreProcessor processor) {
		sconfig.addPreListener(processor) ;
		return this ;
	}


}
