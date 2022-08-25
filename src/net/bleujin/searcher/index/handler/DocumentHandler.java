package net.bleujin.searcher.index.handler;

import java.io.IOException;

import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.index.event.CollectorEvent;

public interface DocumentHandler {
	
	public final static float HEAD_BOOST = 2f;
	

	WriteDocument[] makeDocument(IndexSession isession, CollectorEvent event) throws IOException;

}
