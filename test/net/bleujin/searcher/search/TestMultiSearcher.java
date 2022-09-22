package net.bleujin.searcher.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.SearchControllerConfig;
import net.ion.framework.util.Debug;

public class TestMultiSearcher extends AbTestCase {

	private SearchController c1;
	private SearchController c2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.c1 =  SearchControllerConfig.newRam().build(OpenMode.CREATE_OR_APPEND);
		c1.index(createIndexJob("jin", 3));

		this.c2 =  SearchControllerConfig.newRam().build(OpenMode.CREATE_OR_APPEND);
		c2.index(createIndexJob("hero", 2));
	}
	
	@Override
	protected void tearDown() throws Exception {
		c1.close(); 
		c2.close(); 
		super.tearDown();
	}

	public void testCreate() throws Exception {
		assertEquals(3, c1.newSearcher().search("").size());
		assertEquals(2, c2.newSearcher().search("").size());
	}

	public void testSearchLucene() throws Exception {
		
//		MultiReader mreader = new MultiReader(c1.indexReader(), c2.indexReader());
//		IndexSearcher isearcher = new IndexSearcher(mreader);
//
//		Query query = new MatchAllDocsQuery();
//		TopDocs tdoc = isearcher.search(query, 100);
//		ScoreDoc[] sdoc = tdoc.scoreDocs;
//		for (ScoreDoc d : sdoc) {
//			Document fdoc = isearcher.doc(d.doc);
//			Debug.line(fdoc);
//		}
		
//		c1.newSearcher().search("").debugPrint(); 
	}
	
}
	
