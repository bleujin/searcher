package net.bleujin.searcher.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import net.bleujin.searcher.AbTestCase;
import net.bleujin.searcher.common.AbDocument.Action;
import net.bleujin.searcher.common.FieldIndexingStrategy;
import net.bleujin.searcher.common.IKeywordField;
import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.common.ReadDocument;
import net.bleujin.searcher.common.SearchConstant;
import net.bleujin.searcher.common.WriteDocument;
import net.ion.framework.util.Debug;

public class TestDocument extends AbTestCase {

	public void testWriteAndRead() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.newDocument("doctest").keyword("int", "4").number("age", 20).insert();
				return null;
			}
		});

		assertEquals(1, sdc.search("int:4").size());
		assertEquals(1, sdc.search("age:20").size());
	}

	public void testModifyInSession() throws Exception {
		sdc.index(isession -> {
			WriteDocument mdoc = isession.newDocument("bleujin").keyword("name", "bleujin").text("test", "he programmer").number("age", 30);
			assertEquals("bleujin", mdoc.idValue());
			assertEquals(10, mdoc.toLuceneDoc().getFields().size()); // 2 + 1 + 3 + 4
			
			mdoc.number("age", 20) ; // change value
			
			assertEquals(10, mdoc.toLuceneDoc().getFields().size()); // 2 + 1 + 3 + 4, when call twice
			mdoc.insert();
			return null;
		});
		
		sdc.search("").debugPrint(); 
		
		ReadDocument rdoc = sdc.search("name", "bleujin").first();
		assertEquals(3, rdoc.fieldNames().length); 
		assertEquals("20", rdoc.getField("age").stringValue());
	}
	
	

	public void testBodyValue() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.fieldIndexingStrategy(FieldIndexingStrategy.DEFAULT);
				WriteDocument writedoc = isession.newDocument("bleujin").text("test", "he programmer").number("age", 20);

				Document doc = writedoc.toLuceneDoc();
				assertEquals(8, doc.getFields().size()); // 1 + 3 + 4

				assertEquals(writedoc.idValue(), doc.get(IKeywordField.DocKey));

				ReadDocument loadDoc = ReadDocument.loadDocument(doc);
				assertEquals("bleujin", loadDoc.idValue());

				assertEquals(doc.get(IKeywordField.ISALL_FIELD), loadDoc.reserved(IKeywordField.ISALL_FIELD));
				assertEquals(doc.get(IKeywordField.BodyHash), loadDoc.bodyValue());
				return null;
			}
		});

	}

	public void testSameReadWrite() throws Exception {
		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument writeDoc = isession.newDocument("bleujin").text("test", "he programmer").number("age", 20);
				Document doc = writeDoc.toLuceneDoc();
				ReadDocument loadDoc = ReadDocument.loadDocument(doc);

				for (MyField field : writeDoc.fields()) {
					assertEquals(field.stringValue(), loadDoc.asString(field.name()));
				}

				for (IndexableField field : loadDoc.fields()) {
					assertEquals(field.stringValue(), doc.get(field.name()));
				}

				return null;
			}
		});

	}

	public void testAllSameOnIndex() throws Exception {
		Document doc = sdc.index(new IndexJob<Document>() {
			public Document handle(IndexSession isession) throws Exception {
				final WriteDocument writeDoc = isession.newDocument("bleujin").unknown("test", "he programmer").unknown("age", 20);
				isession.insertDocument(writeDoc);
				return writeDoc.toLuceneDoc();
			}
		});

		ReadDocument findDoc = sdc.search("20").first();

		for (IndexableField field : findDoc.fields()) {
			assertEquals(field.stringValue(), doc.get(field.name()));
		}
	}

	public void testSlash() throws Exception {
		Document doc = sdc.index(new IndexJob<Document>() {
			public Document handle(IndexSession isession) throws Exception {
				final WriteDocument writeDoc = isession.newDocument("bleujin").keyword("@path", "_emp").keyword("@path", "/ion");
				isession.insertDocument(writeDoc);
				return writeDoc.toLuceneDoc();
			}
		});

		assertEquals(true, sdc.search("@path:\"/ion\"").first() != null);
		assertEquals(true, sdc.search("@path", "/ion").first() != null);
	}

	public void testLoadDocument() throws Exception {
		sdc.index(new IndexJob<Action>() {
			public Action handle(IndexSession isession) throws Exception {
				final WriteDocument writeDoc = isession.newDocument("bleujin").keyword("@path", "_emp").number("age", 30).keyword("@path", "/ion").text("explain", "hello bleujin");
				return isession.insertDocument(writeDoc);
			}
		});

//		cen.newSearcher().createRequestByKey("bleujin").find().debugPrint(); 

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				isession.loadDocument("bleujin").number("age", 20).update();
				return null;
			}
		});

		ReadDocument rdoc = sdc.search("").first() ;
		assertEquals("hello bleujin", rdoc.asString("explain"));
		assertEquals(true, sdc.search("explain:bleujin").first() != null);
		assertEquals(true, sdc.search("age:20").first() != null);
		assertEquals(true, sdc.search("age:30").first() == null);
		assertEquals(true, sdc.search("30").first() == null);
	}

	public void testEmptyUpdate() throws Exception {
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				return isession.loadDocument("bleujin", true).keyword("name", "bleujin").text("explain", "hello bleujin").updateVoid();
			}
		});

		sdc.index(new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument wdoc = isession.loadDocument("bleujin", true);
				wdoc.update();
				wdoc.update();
				return null;
			}
		});

		sdc.search("name:bleujin").debugPrint();

	}

	public void testMergeUpdate() throws Exception {
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument("bleujin").keyword("name", "bleujin").text("greeting", "hello world").updateVoid();
			}
		});
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				return isession.loadDocument("bleujin").keyword("name", "hero").updateVoid();
			}
		});

		assertEquals(0, sdc.search("name:bleujin").size());
		assertEquals(1, sdc.search("name:hero").size());
		assertEquals(1, sdc.search("greeting:world").size());
	}

	public void testReplaceDocument() throws Exception {
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				return isession.newDocument("bleujin").keyword("name", "bleujin").text("greeting", "hello world").updateVoid();
			}
		});
		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				return isession.loadDocument("bleujin", true).keyword("name", "hero").updateVoid();
			}
		});

		assertEquals(1, sdc.search("name:hero").size());
		assertEquals(0, sdc.search("name:bleujin").size());
		assertEquals(1, sdc.search("greeting:world").size());

		sdc.index(new IndexJob<Void>() {
			public Void handle(IndexSession isession) throws Exception {
				WriteDocument wdoc = isession.loadDocument("bleujin", true);
				wdoc.keyword("name", "hero");
				wdoc.keyword("name", "jin"); // replace
				return wdoc.updateVoid();
			}
		});

		// but only remove old
		assertEquals(1, sdc.search("name:jin").size());
		assertEquals(0, sdc.search("name:hero").size());

	}

}
