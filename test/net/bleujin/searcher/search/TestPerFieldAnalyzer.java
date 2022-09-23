package net.bleujin.searcher.search;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.Searcher;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.index.VTextField;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;

public class TestPerFieldAnalyzer extends AbTestCase {

	public void testWhenIndex() throws Exception {
		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("id", new KeywordAnalyzer());
		mapAnal.put("name", new CJKAnalyzer());
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new KoreanAnalyzer(), mapAnal);

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(analyzer);
				isession.newDocument("perfield").text("id", "태극기").text("name", "태극기").update();
				return null;
			}
		});

		sdc.search(session -> {
			ReadDocument rdoc = session.createRequest("").find().first();
			Debug.line(rdoc.getField("id"), rdoc.getField("name"));
			Debug.debug(session.createRequest("id:태극기").query());
			session.createRequest("id:태극기").find().debugPrint(); // not found
			Debug.debug(session.createRequest("name:태극기").query());
			session.createRequest("name:태극기").find().debugPrint(); // found

			return null;
		});

	}

	public void testWhenSelect() throws Exception {
		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("id", new KeywordAnalyzer());
		mapAnal.put("name", new CJKAnalyzer());
		Analyzer sanalyzer = new PerFieldAnalyzerWrapper(new KoreanAnalyzer(), mapAnal);

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(sanalyzer);
				isession.newDocument("perfield").text("id", "태극기").text("name", "태극기").update();
				return null;
			}
		});
		sdc.search(session -> {
			ReadDocument rdoc = session.createRequest("").find().first();
			assertEquals(0, session.createRequest("id:태극").find().size());
			assertEquals(1, session.createRequest("name:태극").find().size());
			return null;
		});
	}


	public void testLucene() throws Exception {
		Directory dir = sdc.dir();

		Analyzer analyzer = new StandardAnalyzer();

		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("title", new KeywordAnalyzer());
		mapAnal.put("name", new CJKAnalyzer());
		analyzer = new PerFieldAnalyzerWrapper(new KoreanAnalyzer(), mapAnal);

		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(dir, iwc);
		addDoc(w, "Lucene in Action", "193398817");
		addDoc(w, "Lucene for Dummies", "55320055Z");
		addDoc(w, "Managing Gigabytes", "55063554A");
		addDoc(w, "The Art of Computer Science", "9900333X");
		w.close();

		DirectoryReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		int hitsPerPage = 10;
		Query q = new QueryParser("title", new KeywordAnalyzer()).parse("title:\"Managing Gigabytes\"");
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, 10);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		Debug.debug("Found " + hits.length + " hits. query:" + q);
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			Debug.debug((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
		}

	}

	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
		Document doc = new Document();
		doc.add(new VTextField("title", title, Store.YES));
		doc.add(new StringField("isbn", isbn, Store.YES));
		w.addDocument(doc);
	}

	public void testParse() throws Exception {

		Map<String, Analyzer> mapAnal = MapUtil.newMap();
		mapAnal.put("id", new KeywordAnalyzer());
		mapAnal.put("name", new KoreanAnalyzer());
		Analyzer analyzer = new PerFieldAnalyzerWrapper(new KoreanAnalyzer(), mapAnal);

		analyzer = new StandardAnalyzer();
		analyzer = new CJKAnalyzer();

		TokenStream tokenStream = analyzer.tokenStream("id", "태극기가 바람에 펄럭");
		OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();

		JsonArray result = new JsonArray();
		while (tokenStream.incrementToken()) {
			int startOffset = offsetAttribute.startOffset();
			int endOffset = offsetAttribute.endOffset();
			result.add(new JsonObject().put("term", charTermAttribute.toString()).put("start", startOffset).put("end", endOffset));
		}
		IOUtil.close(tokenStream);
		IOUtil.close(analyzer);
		Debug.line(result);
	}
	
	public void testIndex() throws Exception {
		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new CJKAnalyzer(), MapUtil.<String, Analyzer>create("name", new KeywordAnalyzer()));
		
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.indexConfig().indexAnalyzer(analyzer) ;
				
				isession.newDocument("123").unknown("name", "태극기").insert() ;
				return null;
			}
		}) ;
		
		Searcher searcher = sdc.newSearcher() ;
		assertEquals(1, searcher.createRequest("").find().size()) ; 
		
		assertEquals(1, searcher.createRequest("name:태극기", new KeywordAnalyzer()).find().size()) ;
		assertEquals(0, searcher.createRequest("name:태극기", new CJKAnalyzer()).find().size()) ;
		assertEquals(1, searcher.createRequest("태극기", new CJKAnalyzer()).find().size()) ;
		assertEquals(0, searcher.createRequest("태극기", new KeywordAnalyzer()).find().size()) ;
		

		assertEquals(1, searcher.createRequest("name:태극기", new PerFieldAnalyzerWrapper(new CJKAnalyzer(), MapUtil.<String, Analyzer>create("name", new KeywordAnalyzer()))).find().size()) ;
		assertEquals(1, searcher.createRequest("태극기", analyzer).find().size()) ;

	}
}
