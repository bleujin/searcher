package net.bleujin.searcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.search.SearchConfig;
import net.bleujin.searcher.search.SearchResponse;
import net.bleujin.searcher.search.SortExpression;
import net.bleujin.searcher.search.processor.PostProcessor;
import net.bleujin.searcher.search.processor.PreProcessor;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.SetUtil;

public class SearchRequestWrapper {

	private Searcher searcher;
	private SearchController sdc;
	private Query query;
	private SearchConfig sconfig;
	private Set<SortField> sortFields = SetUtil.create() ;
	private int skip = 0;
	private int offset = 100;
	private Map<String, Object> param = MapUtil.newCaseInsensitiveMap() ;
	private Set<String> columns = SetUtil.newSet() ;

	SearchRequestWrapper(Searcher searcher, SearchController sdc, Query query) {
		this.searcher = searcher ;
		this.sdc = sdc ;
		this.query = query ;
		this.sconfig = SearchConfig.create(sdc) ;
		this.sconfig.addPreListener(searcher.preListeners().toArray(new PreProcessor[0])) ;
		this.sconfig.addPostListener(searcher.postListeners().toArray(new PostProcessor[0])) ;
	}


	public Query query() {
		return query ;
	}
	
	public ReadDocument findOne() throws IOException {
		
		final List<ReadDocument> docs = find().getDocument();
		if (docs.size() == 0) return null ;
		return docs.get(0) ;
	}
	
	public SearchResponse find() throws IOException {
		
		return sdc.search(sconfig, this);
	}

	
	
	
	public SearchRequestWrapper ascending(String field) {
		if (sconfig.numFields().contains(field)) {
			sortFields.add(new SortField(field, Type.LONG)) ;
		} else {
			sortFields.add(new SortField(field, Type.STRING)) ;
		}
		return this;
	}
	
	public SearchRequestWrapper descending(String field) {
		if (sconfig.numFields().contains(field)) {
			sortFields.add(new SortField(field, Type.LONG, true)) ;
		} else {
			sortFields.add(new SortField(field, Type.STRING, true)) ;
		}
		return this ;
	}



	public SearchRequestWrapper ascendingNum(String field) {
		sortFields.add(new SortField(field, Type.LONG)) ;
		return this;
	}



	public SearchRequestWrapper descendingNum(String field) {
		sortFields.add(new SortField(field, Type.LONG, true)) ;
		return this;
	}



	public Sort sort() {
		return new Sort(sortFields.toArray(new SortField[0]));
	}


	public SearchRequestWrapper sort(String expr) {
		this.sortFields = SetUtil.create(new SortExpression(sconfig).parse(expr)) ;
		return this;
	}


	public SearchRequestWrapper offset(int offset) {
		this.offset = offset ;
		return this;
	}


	public SearchRequestWrapper skip(int skip) {
		this.skip = skip ;
		return this ;
	}


	public Set<SortField> sortField() {
		return this.sortFields ;
	}


	public int skip() {
		return skip;
	}

	public int offset() {
		return offset;
	}

	public SearchRequestWrapper setParam(String key, Object value) {
		param.put(key, value);
		return this ;
	}

	public Set<String> paramKeys(){
		return param.keySet() ;
	}
	
	public Object getParam(String key) {
		return param.get(key);
	}

	public SearchRequestWrapper selections(String... cols) {
		for (String col : cols) {
			this.columns.add(col) ;
		}
		return this ;
	}
	public Set<String> selectorField(){
		return columns ;
	}

	public SearchConfig searchConfig() {
		return sconfig ;
	}
	

}
