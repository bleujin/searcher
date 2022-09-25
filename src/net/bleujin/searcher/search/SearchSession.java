package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.util.BytesRef;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchRequestWrapper;
import net.bleujin.searcher.common.IKeywordField;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.extend.HighlightTerm;
import net.bleujin.searcher.extend.SimilaryDoc;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.util.ArrayVector;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;

public class SearchSession {

	private SearchController sc;
	private SearchConfig sconfig;
	private IndexSearcher isearcher;
	private static final Query ALL_QUERY = new MatchAllDocsQuery();

	private SearchSession(SearchController sc, IndexSearcher isearcher, SearchConfig sconfig) throws IOException {
		this.sc = sc;
		this.sconfig = sconfig;
		this.isearcher = isearcher;

	}

	public SearchRequest createRequest(SearchRequestWrapper wrequest) {
		return createRequest(wrequest.compatableQuery()).mapping(wrequest);
	}

	public static SearchSession create(SearchController sc, IndexSearcher isearcher, SearchConfig sconfig) throws IOException {
		return new SearchSession(sc, isearcher, sconfig);
	}

	public SearchRequest createRequest(Query query) {
		return new SearchRequest(this, query);
	}

	public SearchRequest createRequest(Term query) {
		return createRequest(new TermQuery(query));
	}
	
	public SearchRequest createRequestByKey(String idString) {
		return createRequestByTerm(IKeywordField.DocKey, idString) ;
	}

	public SearchRequest createRequestByTerm(String field, String value) {
		return createRequest(new TermQuery(new Term(field, value)));
	}

	public SearchRequest createRequest(String query) throws IOException {
		try {
			return createRequest(parseQuery(query));
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	private Query parseQuery(String query) throws ParseException {
		if (StringUtil.isBlank(query))
			return ALL_QUERY;
		return sconfig.queryParser().parse(query);
	}

	SearchResponse search(SearchRequest srequest) throws IOException {
		long startTime = System.currentTimeMillis();
		sconfig.emitPreListener(srequest);

		TopFieldDocs sresult = isearcher.search(srequest.query(), srequest.limit(), srequest.sort());
		SearchResponse sres = SearchResponse.create(this, srequest, sresult.scoreDocs, sresult.totalHits.value, startTime);

		sconfig.emitPostListener(sres);
		return sres;
	}

	public ReadDocument readDocument(int docId, SearchRequest sreq) throws IOException {
		Document doc = sreq.selectorField().size() > 0 ? isearcher.doc(docId, SetUtil.add(sreq.selectorField(), IKeywordField.DocKey)) : isearcher.doc(docId);
		ReadDocument rdoc = ReadDocument.loadDocument(docId, doc);

		if (sreq.highlightTerm() != HighlightTerm.NONE) {
			HighlightTerm hterm = sreq.highlightTerm();
			Highlighter highlighter = new Highlighter(searchConfig().formatter(), new QueryScorer(new TermQuery(new Term(hterm.field(), hterm.matcString()))));

			String savedText = rdoc.asString(hterm.field());
			TokenStream tstream = TokenSources.getAnyTokenStream(indexReader(), docId, hterm.field(), searchConfig().queryAnalyzer());

			try {
				TextFragment[] frag = highlighter.getBestTextFragments(tstream, savedText, false, 3);
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < frag.length; j++) {
					if ((frag[j] != null) && (frag[j].getScore() > 0)) {
						sb.append(frag[j].toString());
					}
				}
				rdoc.highlightString(StringUtil.defaultIfEmpty(sb.toString(), savedText));
			} catch (InvalidTokenOffsetsException e) {
				throw new IOException(e.getCause());
			}
		}

		return rdoc;
	}

	public SearchConfig searchConfig() {
		return sconfig;
	}

	public int searcherHashCode() {
		return isearcher.hashCode();
	}

	public IndexReader indexReader() {
		return isearcher.getIndexReader();
	}

	
	
	
	public SimilaryDocs similaryDocs(ReadDocument fdoc, String field, SearchResponse targets) throws IOException {
		
		Set<String> allTerms = SetUtil.newSet() ;
		
		Map<String, Integer> targetMap = termFrequencies(allTerms, fdoc.docId(), field) ;
		Map<Integer, Map<String, Integer>> temp = MapUtil.newMap() ;
		for(int docId : targets.docIds()) {
			if (docId == fdoc.docId()) continue ;
			Map<String, Integer> tf = termFrequencies(allTerms, docId, field) ;
			temp.put(docId, tf) ;
		}
		
		ArrayVector targetVector = realVector(allTerms, targetMap) ;
		List<SimilaryDoc> result = ListUtil.newList() ;
		for (int docId : temp.keySet()) {
			Map<String, Integer> tf = temp.get(docId) ;
			ArrayVector rv = realVector(allTerms, tf) ;
			double simValue = (targetVector.dotProduct(rv)) / (targetVector.getNorm() * rv.getNorm()) ;
			result.add(new SimilaryDoc(docId, simValue));
		}
		
		Collections.sort(result);
		Collections.reverse(result);
		
		return new SimilaryDocs(targets, fdoc, result);
	}

	
	private Map<String, Integer> termFrequencies(Set<String> allTerms, int docId, String field) throws IOException {
		Terms vector = indexReader().getTermVector(docId, field);
		if (vector == null) throw new IllegalStateException("field not exist or, not indexed with vector") ;
		TermsEnum termsEnum = vector.iterator();
		Map<String, Integer> frequencies = MapUtil.newMap() ;
		BytesRef text = null;
		while ((text = termsEnum.next()) != null) {
			String term = text.utf8ToString();
			int freq = (int) termsEnum.totalTermFreq();
			frequencies.put(term, freq);
			allTerms.add(term);
		}
		return frequencies;
	}
	
	private ArrayVector realVector(Set<String> allTerms, Map<String, Integer> tfmap) {
		ArrayVector vector = new ArrayVector(allTerms.size());
		int i = 0;
		for (String term : allTerms) {
			int value = tfmap.containsKey(term) ? tfmap.get(term) : 0;
			vector.setEntry(i++, value);
		}
		return vector.mapDivide(vector.getL1Norm());
	}

}
