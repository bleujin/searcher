package net.bleujin.searcher.index;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

import net.bleujin.searcher.SearchController;
import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.common.SearchConstant;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.search.SearchSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

public class IndexSession {

	private final SearchController scontroller;
	private SearchSession ssession;
	private IndexWriter iwriter;
	private IndexConfig iconfig;
	private FieldIndexingStrategy fieldIndexingStrategy;
	private boolean ignoreBody;

	public final static String VERSION = "version" ;
	public final static String LASTMODIFIED = "lastmodified" ;

	private IndexSession(SearchController scontroller, IndexConfig iconfig) throws IOException {
		this.scontroller = scontroller ;
		this.iconfig = iconfig ;
		this.fieldIndexingStrategy = FieldIndexingStrategy.create(iconfig);

//		this.ssession = scontroller.search(searcher -> searcher) ;
		
	}

	public static IndexSession create(SearchController scontroller, IndexConfig iconfig) throws IOException {
		return new IndexSession(scontroller, iconfig);
	}

	
	
	public WriteDocument newDocument(String docId) {
		return new WriteDocument(this, docId) ;
	}
	
	public WriteDocument newDocument(){
		return new WriteDocument(this) ;
	}
	

	private IndexWriter iwriter() throws IOException {
		if (this.iwriter != null) {
			return iwriter ;
		} else {
			IndexWriterConfig iwc = new IndexWriterConfig(iconfig.indexAnalyzer());
			iconfig.attributes(iwc) ;
			
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			this.iwriter = new IndexWriter(scontroller.dir(), iwc);
			return this.iwriter ;
		}
	}
	
	
	Document findById(String docId) throws IOException{
		ReadDocument first = searchSession().createRequest(new TermQuery(new Term(SearchConstant.DocKey, docId))).findOne();
		return first == null ? null : first.toLuceneDoc() ;
	}
	
	public WriteDocument loadDocument(String docId) throws IOException {
		
		ReadDocument rdoc = searchSession().createRequest(new TermQuery(new Term(SearchConstant.DocKey, docId))).findOne();
		Document findDoc = (rdoc == null) ? new Document() : rdoc.toLuceneDoc() ;
		WriteDocument result = new WriteDocument(this, docId, findDoc);
		
		
		List<String> numFieldName = ListUtil.newList(); // find numeric field
		for(IndexableField field : findDoc.getFields()){
			IndexableFieldType type = field.fieldType() ;
			if ( (type.indexOptions() != IndexOptions.NONE) && field.numericValue() != null){
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


	public WriteDocument loadDocument(String docId, boolean replaceValue, FieldLoadable floadable) throws IOException {
		ReadDocument rdoc = searchSession().createRequest(new TermQuery(new Term(SearchConstant.DocKey, docId))).findOne();
		Document findDoc = (rdoc == null) ? new Document() : rdoc.toLuceneDoc() ;
		WriteDocument result = new WriteDocument(this, docId, findDoc);
		
		return floadable.handle(result, findDoc);
	}
	
	
	private SearchSession searchSession() throws IOException {
		if (this.ssession == null) this.ssession = scontroller.search(searcher -> searcher) ;
		return ssession ;
	}
	
	public IndexReader reader() throws IOException {
		return searchSession().indexReader();
	}

	
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
		iwriter().addDocument(doc.toLuceneDoc());
		return Action.Insert;
	}

	public Action updateDocument(WriteDocument doc) throws IOException {
		final Document idoc = doc.toLuceneDoc();

		if (doc.isNewDoc()) iwriter().addDocument(idoc);
		else {
			iwriter().updateDocument(new Term(SearchConstant.DocKey, doc.idValue()), idoc);
		}
		
		return Action.Update;
	}

//	public Action copy(Directory src) throws IOException {
//		for (String fileName : src.listAll()) {
//			src.copy(searcher.central().dir(), fileName, fileName, IOContext.DEFAULT);
//		}
//
//		return Action.Update;
//	}


	public void commit() throws CorruptIndexException, IOException {
		if (alreadyCancelled)
			return;
//			writer.prepareCommit(); 
		iwriter().forceMerge(10000, true);
		iwriter().prepareCommit();
		
		iwriter().setLiveCommitData(iconfig.commitData().size()>0 ? iconfig.commitData().entrySet() : MapUtil.<String>chainKeyMap().put(LASTMODIFIED, String.valueOf(System.currentTimeMillis())).toMap().entrySet()) ;
		iwriter().commit();
	}

	private boolean alreadyCancelled = false;

	public void cancel() throws IOException {
		this.alreadyCancelled = true;
		iwriter().rollback();
	}

	public IndexSession rollback() {
		if (alreadyCancelled)
			return this;
		this.alreadyCancelled = true;
		try {
			iwriter().rollback();
		} catch (IOException ignore) {
			ignore.printStackTrace();
		}
		return this;
	}

	public Action deleteDocument(WriteDocument doc) throws IOException {
		iwriter().deleteDocuments(new Term(SearchConstant.DocKey, doc.idValue()));
		return Action.Delete;
	}

	public Action deleteById(String idValue) throws IOException {
		iwriter().deleteDocuments(new Term(SearchConstant.DocKey, idValue));
		return Action.Delete;
	}

	
	public Action deleteAll() throws IOException {
		iwriter().deleteAll();
		return Action.DeleteAll;
	}

	public Action deleteTerm(Term term) throws IOException {
		iwriter().deleteDocuments(term);
		return Action.DeleteAll;
	}

	public Action deleteQuery(Query query) throws IOException {
		iwriter().deleteDocuments(query);
		return Action.DeleteAll;
	}

	public String getIdValue(Document doc) {
		return doc.get(SearchConstant.DocKey);
	}

	public String getBodyValue(Document doc) {
		return doc.get(SearchConstant.BodyHash);
	}

	public void appendFrom(Directory... dirs) throws CorruptIndexException, IOException {
		iwriter().addIndexes(dirs);
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
			IOUtil.closeQuietly(iwriter()) ;
		} catch (IOException ignore) {
			ignore.printStackTrace();
		} 
	}


}
