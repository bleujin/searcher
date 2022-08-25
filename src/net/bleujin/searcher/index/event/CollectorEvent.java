package net.bleujin.searcher.index.event;

import java.io.IOException;

import net.bleujin.searcher.common.WriteDocument;
import net.bleujin.searcher.index.IndexSession;
import net.bleujin.searcher.index.collect.ICollector;

public abstract class CollectorEvent implements ICollectorEvent{

	private static final long serialVersionUID = 1081027866981631957L;
	private long startTime ;
	public CollectorEvent(){
		startTime = System.currentTimeMillis() ;
	}
	
	public abstract ICollector getCollector() ;
	
	
	
	public EventType getEventType() {
		return EventType.Normal;
	}

	public WriteDocument[] makeDocument(IndexSession isession) throws IOException {
		return getCollector().getDocumentHandler().makeDocument(isession, this) ;
	}
	
	public long getStartTime() {
		return startTime ;
	}

}
