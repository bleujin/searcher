package net.bleujin.searcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;

import junit.framework.TestCase;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexJob;
import net.bleujin.searcher.index.IndexSession;
import net.ion.framework.util.RandomUtil;

public class AbTestCase extends TestCase {

	protected SearchController sdc;

	@Override
	protected void setUp() throws Exception {
		this.sdc = SearchControllerConfig.newRam().build(OpenMode.CREATE_OR_APPEND);
	}

	protected void tearDown() throws Exception {
		this.sdc.close();
	}

	public final static IndexJob<Void> createIndexJob(final String prefix, final int count) {
		return new IndexJob<Void>() {
			@Override
			public Void handle(IndexSession isession) throws Exception {
				for (int i = 0; i < count; i++) {
					WriteDocument wdoc = isession.newDocument(prefix + i).keyword("prefix", prefix).number("idx", i);
					isession.updateDocument(wdoc);
				}
				return null;
			}
		};
	}

	protected IndexJob<Void> SAMPLE = new IndexJob<Void>() {
		public Void handle(IndexSession isession) throws IOException {
			isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).insert();
			isession.newDocument("hero").keyword("name", "hero").number("age", 30).insert();
			isession.newDocument("jin").keyword("name", "jin").number("age", 40).insert();
			return null;
		};
	};
	
	protected IndexJob<Void> TEST100 = new IndexJob<Void>() {
		public Void handle(IndexSession isession) throws IOException{
			int count = 100 ;
			LocalDateTime today = LocalDateTime.now();
			String[] ranName = new String[]{"bleujin", "novision", "iihi", "k2sun"} ;
			for (int j = 0; j < count; j++) {
				WriteDocument myDoc = isession.newDocument() ;
				myDoc.number("int", 100 + RandomUtil.nextInt(100)).date("date",  Date.from(today.plusDays(j).atZone(ZoneId.systemDefault()).toInstant())) ;
				myDoc.keyword("name", ranName[j % ranName.length]).text("subject", RandomStringUtils.randomAlphabetic(20)).insertVoid() ;
			}
			return null ;
		}
	};
	
	protected IndexJob<Void> DELETE_ALL = new IndexJob<Void>() {
		public Void handle(IndexSession isession) throws IOException {
			isession.deleteAll(); 
			return null ;
		}
	} ;
	
	
}
