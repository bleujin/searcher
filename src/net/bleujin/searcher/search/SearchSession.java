package net.bleujin.searcher.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.ReadDocument;

public class SearchSession {

	private SearchController sc;
	private SearchConfig sconfig;
	private IndexSearcher isearcher;
	private QueryParser qparser;

	private SearchSession(SearchController sc, IndexSearcher isearcher, SearchConfig sconfig) throws IOException {
		this.sc = sc ;
		this.sconfig = sconfig ;
		this.isearcher = isearcher ;
		this.qparser = sconfig.queryParser();
	}

	public static SearchSession create(SearchController sc, IndexSearcher isearcher, SearchConfig sconfig) throws IOException {
		return new SearchSession(sc, isearcher, sconfig);
	}

	public SearchRequest createRequest(Query query) {
		return new SearchRequest(this, query);
	}

	public SearchRequest createRequest(Term query) {
		return new SearchRequest(this, new TermQuery(query));
	}


	public SearchRequest createRequest(String query) throws IOException {
		try {
			return new SearchRequest(this, qparser.parse(query));
		} catch (ParseException e) {
			throw new IOException(e) ;
		}
	}
	
	SearchResponse search(SearchRequest srequest) throws IOException {
		long startTime = System.currentTimeMillis() ; 
		TopFieldDocs sresult = isearcher.search(srequest.query(), srequest.maxResult(), srequest.sort());
		return SearchResponse.create(this, srequest, sresult.scoreDocs, sresult.totalHits.value, startTime) ;
	}
	
	

	public ReadDocument readDocument(int docId) throws IOException {
		Document doc = isearcher.doc(docId) ;
		return ReadDocument.loadDocument(docId, doc);
	}

	public SearchConfig searchConfig() {
		return sconfig;
	}

	public int searcherHashCode() {
		return isearcher.hashCode();
	}

}
