package net.bleujin.lucene;

import java.nio.ByteBuffer;
import java.util.Date;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FilterNumericDocValues;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import junit.framework.TestCase;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

public class TestField extends TestCase {

	
	public void testFirst() throws Exception{
		Directory dir =  new ByteBuffersDirectory() ;
		
		IndexWriterConfig conf = new IndexWriterConfig(new WhitespaceAnalyzer());
		IndexWriter iwriter = new IndexWriter(dir, conf) ;
		iwriter.addDocument(makeDocument("bleujin", "bleujin hi", 20, DateUtil.stringToDate("2001010-112233"))) ;
		iwriter.addDocument(makeDocument("zhero", "hero hi", 30, DateUtil.stringToDate("20010103-112233"))) ;
		iwriter.addDocument(makeDocument("jin", "jin hi", 7, DateUtil.stringToDate("20010101-112233"))) ;
		iwriter.commit() ;
		
		IndexSearcher isearcher = new IndexSearcher(DirectoryReader.open(dir)) ;
		QueryParser qparser = new QueryParser("name", new WhitespaceAnalyzer()) ;
		
		Debug.line(isearcher.search(qparser.parse("age:20"), 20).totalHits);
		Debug.line(isearcher.search(qparser.parse("birth:20010101"), 20).totalHits);
		Debug.line(isearcher.search(NumericDocValuesField.newSlowRangeQuery("age", 1, 10), 20).totalHits);

		
		ScoreDoc docid = isearcher.search(qparser.parse("name:bleujin"), 20).scoreDocs[0] ;
		Document doc = isearcher.doc(docid.doc) ;
		
		
		doc.getFields().forEach(ifield ->{
			Debug.debug(doc.getFields().size(), ifield.fieldType().omitNorms(), ifield.numericValue(), ifield.name(), ifield.stringValue(),    
					ifield.fieldType().indexOptions(), ifield.stringValue()) ;
		});

		// Debug.debug(doc.getField("age"));
		Debug.line(new StringField("name", "bleujin", Store.YES).fieldType()) ;
				

		Debug.debug(isearcher.search(new MatchAllDocsQuery(), 20, new Sort(new SortField("name", Type.STRING, true))).scoreDocs);
		Debug.debug(isearcher.search(new MatchAllDocsQuery(), 20, new Sort(new SortField("age", Type.LONG, true))).scoreDocs);
		Debug.debug(isearcher.search(new MatchAllDocsQuery(), 20, new Sort(new SortField("birth", Type.STRING, true))).scoreDocs);
		
		IOUtil.close(dir);
	}
	
	
	private Document makeDocument(String name, String content, long age, Date birth) throws Exception {
		
		Document doc = new Document();
		
		doc.add(new StringField("name", name, Store.YES)); // keyword
		doc.add(new SortedDocValuesField("name", new BytesRef(name)));
		
		doc.add(new TextField("content", content, Store.YES)); // text
		
		doc.add(new NumericDocValuesField("age", age)); // number
		doc.add(new StringField("age", "" + age, Store.NO)); 
		doc.add(new StoredField("age", age));

		doc.add(new StringField("birth", DateUtil.dateToString(birth, "yyyyMMdd-HHmmss"), Store.YES)); //date
		doc.add(new StringField("birth", DateUtil.dateToString(birth, "yyyyMMdd"), Store.NO)); 
		doc.add(new SortedDocValuesField("birth", new BytesRef(DateUtil.dateToString(birth, "yyyyMMdd-HHmmss"))));
		
		
		
		
		return doc ;
	}
	
	public void testIsDateFormat() {
		Debug.line( DateUtil.stringToCalendar("20201232")) ;
	}
	
	
	
	public void testToWriteDocument() throws Exception {
		Debug.line(new NumericDocValuesField("age", 30).numericValue()) ;
		
	}
	
	
	public void testToReadDocument() throws Exception {
		
		
	}
	
	
}
