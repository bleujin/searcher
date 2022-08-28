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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.document.Document;

import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.HashFunction;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;

public class WriteDocument extends AbDocument {

	private static final long serialVersionUID = -8187265793444923069L;
	private String docId;
	private Action action = Action.Unknown;

	private Map<String, MyField> fields = MapUtil.newCaseInsensitiveMap() ;
	private IndexSession isession;
	private boolean newDoc = false;
	
	public WriteDocument(IndexSession indexSession, String docId) {
		this(indexSession, docId, (Document)null) ;
	}
	
	public WriteDocument(IndexSession indexSession) {
		this(indexSession, new ObjectId().toString(), (Document)null) ;
	}

	public WriteDocument(IndexSession indexSession, String docId, Document rdoc) {
		this.isession = indexSession ;
		this.docId = docId;
		if (rdoc != null) {
			rdoc.getFields().forEach(ifield -> {
				if (IKeywordField.Field.reservedId(ifield.name())) return ;
				fields.put(ifield.name(), MyField.forWriteDoc(ifield)) ;
			});
		}
	}


	
	public String idValue() {
		return docId;
	}

	public boolean isNewDoc(){
		return newDoc ;
	}
	
	
	
	
	public Document toLuceneDoc() {
		Document doc = new Document() ;
		
		FieldIndexingStrategy strategy = isession.fieldIndexingStrategy(); 
		StringBuilder bodyBuilder = new StringBuilder(512);
		bodyBuilder.append(docId + " ") ;

		// make bodyBuilder from doc
		Set<String> fieldNames = SetUtil.newSet() ; 
		doc.getFields().forEach(ifield -> fieldNames.add(ifield.name()));
		for(String fieldName: fieldNames){
			bodyBuilder.append(doc.get(fieldName) + " ") ;
		}

		for (MyField field : fields.values()) {
			if (field == null || isReservedField(field.name()))
				continue;
			
			isession.indexConfig().fieldTypeMap().entrySet().forEach(entry -> {
				if (StringUtil.equalsIgnoreCase(field.name(), entry.getKey())) {
					field.changeType(entry.getValue()) ;
				}
			});
			
			field.indexField(strategy, doc) ;
			
			if (isession.handleBody() && (!field.ignoreBody())) bodyBuilder.append(field.stringValue() + " ");
		}

		final String bodyString = bodyBuilder.toString();
		
		MyField.keyword(DocKey, idValue()).indexField(strategy, doc);
		MyField.noIndex(BodyHash, ""+HashFunction.hashGeneral(bodyString)).indexField(strategy, doc);
		MyField.noIndex(TIMESTAMP, ""+System.currentTimeMillis()).indexField(strategy, doc);
		if (isession.handleBody()) MyField.notext(ISALL_FIELD, bodyString).indexField(strategy, doc);

		
//		doc.forEach(ifield ->{
//			Debug.line(fields.size(), ifield.name(), ifield.stringValue(), ifield.numericValue(), ifield.fieldType().docValuesType()) ;
//		});
		
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
		return StringUtil.toString(get(name)) ;
	}

	@Deprecated
	public WriteDocument name(String name) {
		add(MyField.text(ISEventName, name));
		return this;
	}

	public WriteDocument add(Map<String, ? extends Object> values) {
		return add(JsonObject.fromObject(values));
	}

	public WriteDocument unknown(Map<String, Object> values) {
		for (Entry<String, Object> entry : values.entrySet()) {
			this.add(MyField.unknown(entry.getKey(), entry.getValue())) ;
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

	public WriteDocument number(String fieldName, long value) {
		add(MyField.number(fieldName, value));
		return this;
	}

	public WriteDocument number(String fieldName, Long value) {
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
		fields.put(field.name(), field);
		return this;
	}

	public Collection<MyField> fields() {
		return fields.values();
	}

	public void removeField(String name) {
		fields.remove(name);
	}

	public MyField get(String name) {
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
