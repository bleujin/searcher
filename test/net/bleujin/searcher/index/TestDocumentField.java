package net.bleujin.searcher.index;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.ReadDocument;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.ArrayUtil;

public class TestDocumentField extends AbTestCase {

	public void testFromJson() throws Exception {
		final JsonObject json = JsonObject.fromString("{name:'bleujin', age:20}");

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("json").add(json).update();
				return null;
			}
		});

		ReadDocument doc = sdc.search(session ->{
			return session.createRequest("name:bleujin").findOne();
		}) ;
		
		assertEquals(true, ArrayUtil.contains(doc.fieldNames(), "name"));
		assertEquals(true, ArrayUtil.contains(doc.fieldNames(), "age"));
	}

}
