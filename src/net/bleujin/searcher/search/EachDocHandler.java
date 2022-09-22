package net.bleujin.searcher.search;

import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.index.IndexableField;

import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.db.Rows;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

public interface EachDocHandler<T> {

	public final static EachDocHandler<Void> DEBUG = new EachDocHandler<Void>() {

		@Override
		public Void handle(EachDocIterator iter) {
			while (iter.hasNext()) {
				ReadDocument doc = iter.next();
				Debug.line(doc);
			}
			return null;
		}
	};

	public final static EachDocHandler<List<ReadDocument>> TOLIST = new EachDocHandler<List<ReadDocument>>() {
		@Override
		public List<ReadDocument> handle(EachDocIterator iter) {
			List<ReadDocument> result = ListUtil.newList();
			while (iter.hasNext()) {
				result.add(iter.next());
			}
			return result;
		}
	};
	

	public final static EachDocHandler<JsonObject> TOJSON = new EachDocHandler<JsonObject>() {
		@Override
		public JsonObject handle(EachDocIterator iter) {
			JsonObject result = new JsonObject() ;
			JsonArray rows = new JsonArray() ;
			result.add("rows", rows);
			
			while (iter.hasNext()) {
				JsonObject row = new JsonObject() ;
				ReadDocument doc = iter.next() ;
				List<IndexableField> fields = doc.fields() ;
				for (IndexableField field : fields) {
					if (field.numericValue() != null) {
						row.addProperty(field.name(), doc.asLong(field.name(), 0)) ;
					} else {
						row.addProperty(field.name(), doc.asString(field.name())) ;
					}
				}
				rows.add(row) ;
			}
			
			return result;
		}
	};

	
	public final static EachDocHandler<Rows> TOROWS = new EachDocHandler<Rows>() {
		@Override
		public Rows handle(EachDocIterator iter) {
			try {
				JsonObject json = TOJSON.handle(iter) ;
				return JsonUtil.toRows(json.get("rows")) ;
			} catch (SQLException e) {
				throw new IllegalArgumentException(e.getCause()) ;
			}
		}
	};



	public T handle(EachDocIterator iter);
}
