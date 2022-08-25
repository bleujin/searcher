package net.bleujin.searcher.index.handler;

import java.io.File;
import java.io.IOException;

import net.bleujin.searcher.common.MyField;
import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.index.event.CollectorEvent;
import net.bleujin.searcher.index.event.FileEvent;

public class FileDocumentHandler implements DocumentHandler {

	public FileDocumentHandler() {
	}


	public WriteDocument[] makeDocument(IndexSession isession, CollectorEvent _event) throws IOException {
		if (! (_event instanceof FileEvent)) return new WriteDocument[0] ;

		FileEvent event = (FileEvent)_event ;

		File file = event.getFile();
		WriteDocument doc = isession.newDocument(String.valueOf(event.getEventId())).name(file.getName());

		MyField name = MyField.text("name", file.getName());
		doc.add(name);
		// doc.add(new Field("content", IOUtils.toString(new FileInputStream(file)), Store.YES, Index.ANALYZED));
		doc.add(MyField.number("size", file.length()));
		doc.add(MyField.text("path", file.getAbsolutePath()));

		return new WriteDocument[]{doc};
	}
}
