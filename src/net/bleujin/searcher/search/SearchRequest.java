package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ecs.xml.XML;
import org.apache.lucene.document.DocumentStoredFieldVisitor;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import net.bleujin.searcher.SearchRequestWrapper;
import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.db.Page;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.SetUtil;


public class SearchRequest {

	private SearchSession ssession;
	private Query query;
	private int skip  = 0 ;
	private int offset = 100;
	private Map<String, Object> param = MapUtil.newCaseInsensitiveMap() ;
	private Set<SortField> sortFields = SetUtil.newSet() ;
	private Set<String> columns = SetUtil.newSet() ;

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
	
	
	public Sort sort() {
		if (sortFields.size() == 0) return Sort.RELEVANCE ;
		return new Sort(sortFields.toArray(new SortField[0])) ;	
	}

	

	public XML toXML() {
		XML request = new XML("request");
		request.addElement(new XML("query").addElement(query.toString()));
		request.addElement(new XML("sort").addElement(sortFields.toString()));

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
		this.sortFields = SetUtil.create(new SortExpression(ssession.searchConfig()).parse(expr)) ;
		return this ;
	}

	public SearchRequest ascending(String field) {
		if (ssession.searchConfig().numFields().contains(field)) {
			sortFields.add(new SortField(field, Type.LONG));
		} else {
			sortFields.add(new SortField(field, Type.STRING));
		}
		return this ;
	}

	public SearchRequest descending(String field) {
		if (ssession.searchConfig().numFields().contains(field)) {
			sortFields.add(new SortField(field, Type.LONG, true));
		} else {
			sortFields.add(new SortField(field, Type.STRING, true));
		}
		return this ;
	}
	
	

	public SearchRequest ascendingNum(String field) {
		sortFields.add(new SortField(field, Type.LONG));
		return this ;
	}

	public SearchRequest descendingNum(String field) {
		sortFields.add(new SortField(field, Type.LONG, true));
		return this ;
	}


	public SearchRequest selections(String... cols) {
		for (String col : cols) {
			this.columns.add(col) ;
		}
		return this;
	}

	
	public Set<String> selectorField(){
		return columns ;
	}
	
	public StoredFieldVisitor selector(){
		return columns.size() == 0 ? null : new DocumentStoredFieldVisitor(columns) ;
	}

	public SearchRequest mapping(SearchRequestWrapper wrequest) {
		this.sortFields =  wrequest.sortField() ;
		this.skip = wrequest.skip() ;
		this.offset = wrequest.offset() ;
		this.columns = wrequest.selectorField() ;
		wrequest.paramKeys().forEach(key -> this.setParam(key, wrequest.getParam(key)));
		this.sortFields =  wrequest.sortField() ;
		return this ;
	}
}
