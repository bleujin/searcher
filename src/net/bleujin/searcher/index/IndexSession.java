package net.bleujin.searcher.index;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.common.SearchConstant;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.search.SearchSession;
import net.ion.framework.util.MapUtil;

public class IndexSession {

	private final SearchSession ssession;
	private IndexWriter iwriter;
	private IndexConfig iconfig;
	private FieldIndexingStrategy fieldIndexingStrategy;
	private boolean ignoreBody;

	public final static String VERSION = "version" ;
	public final static String LASTMODIFIED = "lastmodified" ;

	private IndexSession(SearchController scontroller, IndexWriter iwriter, IndexConfig iconfig) throws IOException {
		this.ssession = null;
		this.iwriter = iwriter ;
		this.iconfig = iconfig ;
		this.fieldIndexingStrategy = iconfig.indexingStrategy();
	}

	public static IndexSession create(SearchController scontroller, IndexConfig iconfig) throws IOException {
		IndexWriterConfig iwc = new IndexWriterConfig(iconfig.analyzer());
		iwc.setOpenMode(scontroller.openMode());
		// iwc.setRAMBufferSizeMB(256.0);
		IndexWriter iwriter = new IndexWriter(scontroller.dir(), iwc);

		return new IndexSession(scontroller, iwriter, iconfig);
	}

	
	
	public WriteDocument newDocument(String docId) {
		return new WriteDocument(this, docId) ;
	}
	
	public WriteDocument newDocument(){
		return new WriteDocument(this) ;
	}

	/*
	Document findById(String id) throws IOException{
		return ssession.findById(id) ;
	}
	
	public WriteDocument loadDocument(String docId, boolean replaceValue, String... numfieldnames) throws IOException, ParseException {
		ReadDocument rdoc = ssession.central().newSearcher().createRequestByKey(docId).findOne();
		Document findDoc = (rdoc == null) ? new Document() : rdoc.toLuceneDoc() ;
		WriteDocument result = new WriteDocument(this, docId, findDoc, replaceValue);
		
		
		List<String> numFieldName = ListUtil.newList(); // find numeric field
		for(IndexableField field : findDoc.getFields()){
			IndexableFieldType type = field.fieldType() ;
			if ( (!type.indexed()) && field.numericValue() != null){
				numFieldName.add(field.name()) ;
			}
		}

		for (String nfield : numFieldName) {
			IndexableField field = findDoc.getField(nfield);
			if (field == null)
				continue;
			result.number(nfield, field.numericValue().longValue());
		}
		
		return result;
	}
		
	
	public WriteDocument loadDocument(String docId) throws IOException, ParseException {
		return loadDocument(docId, false) ;
	}

	public WriteDocument loadDocument(String docId, boolean replaceValue, FieldLoadable floadable) throws IOException, ParseException {
		ReadDocument rdoc = ssession.central().newSearcher().createRequestByKey(docId).findOne();
		Document findDoc = (rdoc == null) ? new Document() : rdoc.toLuceneDoc() ;
		WriteDocument result = new WriteDocument(this, docId, findDoc, replaceValue);
		
		return floadable.handle(result, findDoc);
	}
	

	public IndexReader reader() throws IOException {
		return ssession.indexReader();
	}
	*/
	
	public FieldIndexingStrategy fieldIndexingStrategy() {
		return fieldIndexingStrategy;
	}
	
	
	
	
	public IndexSession fieldIndexingStrategy(FieldIndexingStrategy fieldIndexingStrategy) {
		this.fieldIndexingStrategy = fieldIndexingStrategy ;
		return this ;
	}
	
	public IndexSession setIgnoreBody(boolean ignoreBody){
		this.ignoreBody = ignoreBody ;
		return this ;
	}
	
	public boolean handleBody(){
		return ! this.ignoreBody ;
	}


	public Action insertDocument(WriteDocument doc) throws IOException {
		iwriter.addDocument(doc.toLuceneDoc());
		return Action.Insert;
	}

	public Action updateDocument(WriteDocument doc) throws IOException {
		final Document idoc = doc.toLuceneDoc();
		
		if (doc.isNewDoc()) iwriter.addDocument(idoc);
		else iwriter.updateDocument(new Term(SearchConstant.DocKey, doc.idValue()), idoc);
		
		return Action.Update;
	}

//	public Action copy(Directory src) throws IOException {
//		for (String fileName : src.listAll()) {
//			src.copy(searcher.central().dir(), fileName, fileName, IOContext.DEFAULT);
//		}
//
//		return Action.Update;
//	}
	

	// public IndexSession commit() throws IOException{
	// commit() ;
	//		
	// return this ;
	// }


	public void commit() throws CorruptIndexException, IOException {
		if (alreadyCancelled)
			return;
		if (iwriter != null) {
//			writer.prepareCommit(); 
			iwriter.forceMerge(10000, true);
			iwriter.prepareCommit();
			
			final String lastmodified = String.valueOf(System.currentTimeMillis());
			iwriter.setLiveCommitData(MapUtil.<String>chainKeyMap().put(VERSION, SearchConstant.Version.toString()).put(LASTMODIFIED, lastmodified).toMap().entrySet()) ;
			iwriter.commit();
		}
	}

	private boolean alreadyCancelled = false;

	public void cancel() throws IOException {
		this.alreadyCancelled = true;
		iwriter.rollback();
	}

	public IndexSession rollback() {
		if (alreadyCancelled)
			return this;
		this.alreadyCancelled = true;
		if (iwriter != null) {
			try {
				iwriter.rollback();
			} catch (IOException ignore) {
				ignore.printStackTrace();
			}
		}
		return this;
	}

	public Action deleteDocument(WriteDocument doc) throws IOException {
		iwriter.deleteDocuments(new Term(SearchConstant.DocKey, doc.idValue()));
		return Action.Delete;
	}

	public Action deleteById(String idValue) throws IOException {
		iwriter.deleteDocuments(new Term(SearchConstant.DocKey, idValue));
		return Action.Delete;
	}

	
	public Action deleteAll() throws IOException {
		iwriter.deleteAll();
		return Action.DeleteAll;
	}

	public Action deleteTerm(Term term) throws IOException {
		iwriter.deleteDocuments(term);
		return Action.DeleteAll;
	}

	public Action deleteQuery(Query query) throws IOException {
		iwriter.deleteDocuments(query);
		return Action.DeleteAll;
	}

	public String getIdValue(Document doc) {
		return doc.get(SearchConstant.DocKey);
	}

	public String getBodyValue(Document doc) {
		return doc.get(SearchConstant.BodyHash);
	}

	public void appendFrom(Directory... dirs) throws CorruptIndexException, IOException {
		iwriter.addIndexes(dirs);
	}

	public IndexSession continueUnit() throws IOException {
		commit();
		// begin(this.owner) ;
		return this;
	}

	public IndexConfig indexConfig() {
		return this.iconfig;
	}

	public void forceClose() {
		try {
			iwriter.close();
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} 
	}

}
