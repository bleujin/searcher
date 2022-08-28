package net.bleujin.searcher.rest.formater;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import org.apache.lucene.index.CorruptIndexException;

import net.bleujin.searcher.common.ReadDocument;

public interface SearchDocumentFormater {
	StreamingOutput outputStreaming(List<ReadDocument> docs) throws CorruptIndexException, IOException ;
	
}
