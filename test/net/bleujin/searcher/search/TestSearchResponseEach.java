package net.bleujin.searcher.search;

import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.IndexableField;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.db.Row;
import net.ion.framework.db.Rows;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.Debug;

public class TestSearchResponseEach extends AbTestCase {

	
	public void testToJson() throws Exception {
		sdc.index(SAMPLE) ;
		
		JsonObject json = sdc.newSearcher().search("").eachDoc(EachDocHandler.TOJSON) ;
		Debug.line(json);
	}
	

	public void testToRows() throws Exception {
		sdc.index(SAMPLE) ;
		
		Rows rows = sdc.newSearcher().search("").eachDoc(EachDocHandler.TOROWS) ;
		rows.debugPrint(); 
		
		ResultSetMetaData meta = rows.getMetaData();
		Debug.line(meta.getColumnName(1), meta.getColumnType(1)) ;
		Debug.line(meta.getColumnName(2), meta.getColumnType(2)) ;
		
		Row row = rows.firstRow() ;
		Debug.line(row.getInt("age"));
	}

}
