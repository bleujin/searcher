package net.bleujin.searcher.index.handler;

import java.io.IOException;

import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.index.event.CollectorEvent;
import net.bleujin.searcher.index.event.DataRowEvent;
import net.bleujin.searcher.index.event.KeyValues;

public class DataRowDocumentHandler implements DocumentHandler {

	public DataRowDocumentHandler() {
	}

	public WriteDocument[] makeDocument(IndexSession isession, CollectorEvent _event) throws IOException {
		if (! (_event instanceof DataRowEvent)) return new WriteDocument[0] ;
		
		DataRowEvent event = (DataRowEvent)_event ;
		KeyValues keyValues = event.getKeyValues();
		String[] keyColumns = event.getKeyColumns() ;
		
		String docName = "" ;
		for (String key : keyColumns) {
			docName += keyValues.get(key) + "_" ;
		}
		
		WriteDocument doc = isession.newDocument(String.valueOf(event.getEventId())).name(docName);
		for (String colName : keyValues.getKeySet()) {
			Object value = keyValues.get(colName);
			if (value != null) {
//				MyField myfield = MyField.text(colName, value.toString()) ;
//				if (ArrayUtils.contains(keyColumns, colName)) myfield.setBoost(HEAD_BOOST) ;
				
				doc.add(MyField.unknown(colName, value));
			}
		}
		return new WriteDocument[]{doc};
	}


}
