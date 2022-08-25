package net.bleujin.searcher;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import net.bleujin.searcher.common.SearchConstant;
import net.ion.framework.util.StringUtil;

public class Searcher {

	private SearchController sdc;
	private QueryParser parser;
	
	public Searcher(SearchController sdc) {
		this.sdc= sdc ;
		this.parser = new QueryParser(SearchConstant.ISALL_FIELD, sdc.sconfig().analyzer()) ;
	}
	
	public SearchRequestWrapper createRequest(String query) throws ParseException {
		Query pquery = StringUtil.isBlank(query) ? new MatchAllDocsQuery() : parser.parse(query);
		
		return createRequest(pquery);
	}

	public SearchRequestWrapper createRequest(Query query) {
		return new SearchRequestWrapper(this, sdc, query);
	}

	public Query parseQuery(String query) throws ParseException {
		return parser.parse(query);
	}
	
	

}
