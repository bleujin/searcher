package net.bleujin.searcher.search;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.reader.InfoReader;
import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.StringUtil;

public class SearchSession {

	private SearchController sc;
	private SearchConfig sconfig;
	private IndexSearcher isearcher;
	private static final Query ALL_QUERY = new MatchAllDocsQuery() ;

	private SearchSession(SearchController sc, IndexSearcher isearcher, SearchConfig sconfig) throws IOException {
		this.sc = sc ;
		this.sconfig = sconfig ;
		this.isearcher = isearcher ;
		
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
			return new SearchRequest(this, parseQuery(query));
		} catch (ParseException e) {
			throw new IOException(e) ;
		}
	}

	private Query parseQuery(String query) throws ParseException {
		if (StringUtil.isBlank(query)) return ALL_QUERY ;
		return sconfig.queryParser().parse(query);
	}
	
	SearchResponse search(SearchRequest srequest) throws IOException {
		long startTime = System.currentTimeMillis() ;
		sconfig.emitPreListener(srequest);
		
		TopFieldDocs sresult = isearcher.search(srequest.query(), srequest.limit(), srequest.sort());
		SearchResponse sres = SearchResponse.create(this, srequest, sresult.scoreDocs, sresult.totalHits.value, startTime);

		sconfig.emitPostListener(sres);
		return sres ;
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

	public ReadDocument doc(int docId) throws IOException {
		return ReadDocument.loadDocument(isearcher.doc(docId));
	}

	
	public InfoReader infoReader() throws IOException {
		return InfoReader.create(isearcher.getIndexReader(), DirectoryReader.open(sc.dir())) ;
	}

	public IndexReader indexReader() {
		return isearcher.getIndexReader() ;
	}
	
}
