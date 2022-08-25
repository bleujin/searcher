package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ecs.xml.XML;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.db.Page;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;


public class SearchRequest {

	private SearchSession ssession;
	private Query query;
	private int skip  = 0 ;
	private int offset = 100;
	private Map<String, Object> param = MapUtil.newCaseInsensitiveMap() ;
	private List<String> sortExpression = ListUtil.newList();

	SearchRequest(SearchSession ssession, Query query) {
		this.ssession = ssession ;
		this.query = query ;
	}
	
	public Query query() {
		return query;
	}
	
	public ReadDocument findOne() throws IOException {
		
		final List<ReadDocument> docs = find().getDocument();
		if (docs.size() == 0) return null ;
		return docs.get(0) ;
	}

	public SearchResponse find() throws IOException {
		return ssession.search(this) ;
	}

	
	public int offset() {
		return offset;
	}

	public int skip() {
		return skip;
	}

	public int limit() {
		return skip + offset;
	}
	
	public SearchRequest page(Page page){
		this.skip(page.getStartLoc()).offset(page.getListNum()) ;
		return this ;
	}

	public SearchRequest skip(int skip){
		this.skip = skip ;
		return this ;
	}
	
	public SearchRequest offset(int offset){
		this.offset = offset ;
		return this ;
	}
	
	public SearchRequest setParam(String key, Object value) {
		param.put(key, value);
		return this ;
	}

	public Set<String> paramKeys(){
		return param.keySet() ;
	}
	
	public Object getParam(String key) {
		return param.get(key);
	}
	
	
	// TODO
	public Sort sort() {
		return Sort.RELEVANCE ;
//		if (sortExpression.size() == 0) return Sort.RELEVANCE ;
//		return new Sort(SortExpression.parse(indexFieldType, sortExpression.toArray(new String[0]))) ;	
	}

	

	public XML toXML() {
		XML request = new XML("request");
		request.addElement(new XML("query").addElement(query.toString()));
		request.addElement(new XML("sort").addElement(sortExpression.toString()));

		XML page = new XML("page");
		page.addAttribute("skip", String.valueOf(skip()));
		page.addAttribute("offset", String.valueOf(offset()));
		request.addElement(page);

		XML params = new XML("params");
		Set<Entry<String, Object>> entrys = param.entrySet();
		for (Entry<String, Object> entry : entrys) {
			String value = entry.getValue() == null ? "" : entry.getValue().toString();
			params.addElement(new XML(entry.getKey()).addElement(value));
		}
		request.addElement(params);

		return request;
	}

	public String toString() {
		return toXML().toString() ;
	}
	
	public SearchRequest sort(String expr){
		if (StringUtil.isBlank(expr)) return this ;
		for (String e : StringUtil.split(expr, ",")) {
			sortExpression.add(e) ;
		}
		return this ;
	}

	public SearchRequest ascending(String field) {
		sortExpression.add(field);
		return this ;
	}

	public SearchRequest descending(String field) {
		sortExpression.add(field + " desc");
		return this ;
	}

	public SearchRequest ascendingNum(String field) {
		sortExpression.add(field + " _number");
		return this ;
	}

	public SearchRequest descendingNum(String field) {
		sortExpression.add(field + " _number desc");
		return this ;
	}


}
