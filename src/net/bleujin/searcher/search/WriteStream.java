package net.bleujin.searcher.search;

import java.util.stream.Stream;

import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;

public class WriteStream extends AbDocumentStream<WriteDocument, WriteStream> implements Stream<WriteDocument> {

	public WriteStream(IndexSession isession, Stream<WriteDocument> stream) {
		super(stream) ;
	}

}
