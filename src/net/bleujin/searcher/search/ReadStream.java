package net.bleujin.searcher.search;

import java.util.stream.Stream;

import net.bleujin.searcher.common.ReadDocument;

public class ReadStream extends AbDocumentStream<ReadDocument, ReadStream> implements Stream<ReadDocument> {

	public ReadStream(SearchSession ssession, Stream<ReadDocument> stream) {
		super(stream) ;
	}


}
