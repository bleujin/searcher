package net.bleujin.searcher.index.policy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.IKeywordField;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.index.event.ICollectorEvent;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;

public class OutTestPolicy extends AbstractWritePolicy implements IKeywordField {

	private Map<String, HashBean> hashData = new HashMap<String, HashBean>();

	public void begin(IndexSession session) throws IOException {

		IndexReader reader = session.reader();
		for (int i = 0, last = reader.maxDoc(); i < last; i++) {
			// if (reader.isDeleted(i))
			// continue;
			Document doc = reader.document(i);
			HashBean bean = new HashBean(session.getIdValue(doc), session.getBodyValue(doc));
			hashData.put(session.getIdValue(doc), bean);
		}
	}

	public Action apply(final IndexSession writer, WriteDocument doc) throws IOException {

		String idValue = doc.idValue();
		if (hashData.containsKey(idValue)) {
			String oldValue = hashData.get(idValue).getBodyValue();
			String newValue = doc.asString(ICollectorEvent.BodyHash);

			Debug.debug("UPDATE", idValue, "Modified:" + StringUtil.equals(oldValue, newValue));
			return Action.Update;
		} else {
			Debug.debug("INSERT", idValue, doc.asString(DocKey), hashData.size());
			return Action.Insert;
		}

	}
}
