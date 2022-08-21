package net.bleujin.searcher.common;

import static net.bleujin.searcher.common.IKeywordField.BodyHash;
import static net.bleujin.searcher.common.IKeywordField.DocKey;
import static net.bleujin.searcher.common.IKeywordField.ISALL_FIELD;
import static net.bleujin.searcher.common.IKeywordField.ISEventName;
import static net.bleujin.searcher.common.IKeywordField.KEYWORD_FIELD;
import static net.bleujin.searcher.common.IKeywordField.TIMESTAMP;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;

import com.google.common.collect.ArrayListMultimap;

import net.bleujin.searcher.common.MyField.MyFieldType;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.HashFunction;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;

public class WriteDocument extends AbDocument {

	private static final long serialVersionUID = -8187265793444923069L;
	private String docId;
	private Action action = Action.Unknown;

	private ArrayListMultimap<String, MyField> fields = ArrayListMultimap.create() ;
	private IndexSession isession;
	private boolean newDoc = false;
	private float boost = 1.0f ;
	private Document doc;
	private boolean replaceField;
	
	public WriteDocument(IndexSession indexSession, String docId) {
		this(indexSession, docId, new Document(), false) ;
	}
	public WriteDocument(IndexSession indexSession, String docId, Document doc) {
		this(indexSession, docId, doc, false) ;
	}
	
	public WriteDocument(IndexSession indexSession, String docId, Document doc, boolean replaceField) {
		this.isession = indexSession ;
		this.docId = docId;
		this.doc = (doc == null) ? new Document() : doc;
		
		this.replaceField = replaceField;
	}

	public WriteDocument(IndexSession indexSession) {
		this(indexSession, new ObjectId().toString(), new Document(), true) ;
	}

	public String idValue() {
		return docId;
	}

	public boolean isNewDoc(){
		return newDoc ;
	}
	
	public WriteDocument boost(float boost){
		this.boost = boost ;
		return this ;
	}
	
	public Document toLuceneDoc() {
		
		FieldIndexingStrategy strategy = isession.fieldIndexingStrategy(); 
		StringBuilder bodyBuilder = new StringBuilder(512);
		bodyBuilder.append(docId + " ") ;

		for(IndexableField field : doc.getFields()){
			bodyBuilder.append(field.stringValue() + " ") ;
		}

		for (MyField field : fields.values()) {
			if (field == null || isReservedField(field.name()))
				continue;
			field.indexField(strategy, doc) ;
			
			if (isession.handleBody() && (!field.ignoreBody())) bodyBuilder.append(field.stringValue() + " ");
		}

		MyField.keyword(DocKey, idValue(), Store.YES).indexField(strategy, doc);
		final String bodyString = bodyBuilder.toString();
		MyField.number(BodyHash, HashFunction.hashGeneral(bodyString)).indexField(strategy, doc);
		MyField.number(TIMESTAMP, System.currentTimeMillis()).indexField(strategy, doc);

		if (isession.handleBody()) MyField.text(ISALL_FIELD, bodyString, Store.NO).indexField(strategy, doc);

		
		return doc;
	}
	
	
	public String bodyValue(){
		StringBuilder bodyBuilder = new StringBuilder(docId + " ");

		for (MyField field : fields.values()) {
			if (field == null)
				continue;
			if (isReservedField(field.name())) // except timestamp
				continue;
			bodyBuilder.append(field.stringValue() + " ");
		}
		return String.valueOf(HashFunction.hashGeneral(bodyBuilder.toString())) ;
	}
	
	private static final boolean isReservedField(String fieldName){
		return ArrayUtils.contains(KEYWORD_FIELD, fieldName);
	}
	

	public WriteDocument setAction(Action action) {
		this.action = action;
		return this ;
	}

	public Action getAction() {
		return this.action;
	}

	
	public String asString(String name) {
		return firstField(name) == null ? null : firstField(name).stringValue() ;
	}

	@Deprecated
	public WriteDocument name(String name) {
		add(MyField.text(ISEventName, name));
		return this;
	}

	public WriteDocument add(Map<String, ? extends Object> values) {
		return add(JsonObject.fromObject(values));
	}

	public WriteDocument unknown(Map<String, String> values) {
		for (Entry<String, String> entry : values.entrySet()) {
			this.add(new MyField(new TextField(entry.getKey(), entry.getValue(), Store.NO), MyFieldType.Unknown)) ;
		}
		return this ;
	}


	public WriteDocument add(JsonObject jso) {
		recursiveField(this, "", jso);
		return this;
	}

	private static void recursiveField(WriteDocument mydoc, String prefix, JsonElement jso) {
		if (jso.isJsonPrimitive()) {
			mydoc.add(MyField.unknown(StringUtil.defaultIfEmpty(prefix, "_root"), JsonUtil.toSimpleObject(jso)));
		} else if (jso.isJsonArray()) {
			JsonElement[] eles = jso.getAsJsonArray().toArray();
			for (JsonElement ele : eles) {
				recursiveField(mydoc, prefix, ele);
			}
		} else if (jso.isJsonObject()) {
			for (Entry<String, JsonElement> entry : jso.getAsJsonObject().entrySet()) {
				if (StringUtil.isBlank(entry.getKey()))
					continue;
				String fieldKey = StringUtil.isBlank(prefix) ? entry.getKey() : (prefix + "." + entry.getKey());
				JsonElement value = entry.getValue();
				recursiveField(mydoc, fieldKey, value);
			}
			if (!StringUtil.isBlank(prefix))
				mydoc.add(MyField.unknown(prefix, jso));
		}
	}

	public WriteDocument keyword(String fieldName, String value) {
		add(MyField.keyword(fieldName, value));
		return this;
	}

	public WriteDocument text(String fieldName, String value) {
		if (StringUtil.isBlank(value)) return this ;
		add(MyField.text(fieldName, value));
		return this;
	}
	
	public WriteDocument stext(String fieldName, String value) {
		if (StringUtil.isBlank(value)) return this ;
		add(MyField.text(fieldName, value, Store.YES));
		return this;
	}

	public WriteDocument vtext(String fieldName, String value) {
		if (StringUtil.isBlank(value)) return this ;
		add(MyField.vtext(fieldName, value, Store.YES));
		
		return this;
	}


	public WriteDocument number(String fieldName, long value) {
		add(MyField.number(fieldName, value));
		return this;
	}

	public WriteDocument date(String fieldName, Date date) {
		add(MyField.date(fieldName, date));
		return this;
	}

	public WriteDocument unknown(String name, Object value) {
		add(MyField.unknown(name, value));
		return this;
	}

	public WriteDocument unknown(String name, String value) {
		add(MyField.unknown(name, value));
		return this;
	}

	public WriteDocument add(MyField field) {
		if (replaceField) {
//			fields.removeAll(field.name()) ;
			doc.removeField(field.name());
		}
		fields.put(field.name(), field);
		return this;
	}
	
	@Deprecated
	public WriteDocument addField(Field field){
		doc.add(field);
		return this ;
	}

	public Collection<MyField> fields() {
		return fields.values();
	}

	public MyField firstField(String name){
		return fields.get(name).size() < 1 ? null : fields.get(name).get(0)  ;  
	}
	
	public void removeField(String name) {
		fields.removeAll(name);
	}

	public List<MyField> fields(String name) {
		return fields.get(name) ;
	}

	public WriteDocument update() throws IOException {
		isession.updateDocument(this) ;
		return this ;
	}

	public WriteDocument insert() throws IOException {
		isession.insertDocument(this) ;
		return this ;
	}

	public Void updateVoid() throws IOException {
		update() ;
		return null ;
	}

	public Void insertVoid() throws IOException {
		insert() ;
		return null ;
	}


}
