package net.bleujin.searcher.index;

import java.io.IOException;

import org.apache.lucene.document.Document;

import net.bleujin.searcher.common.WriteDocument;

public interface FieldLoadable {

	WriteDocument handle(WriteDocument result, Document findDoc) throws IOException;

}
