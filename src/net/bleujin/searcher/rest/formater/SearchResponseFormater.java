package net.bleujin.searcher.rest.formater;

import java.io.IOException;

import javax.ws.rs.core.StreamingOutput;

import org.apache.lucene.index.CorruptIndexException;

import net.bleujin.searcher.search.SearchResponse;

public interface SearchResponseFormater {
	StreamingOutput outputStreaming(SearchResponse iresult) throws CorruptIndexException, IOException ;
}
