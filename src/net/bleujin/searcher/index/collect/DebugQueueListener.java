package net.bleujin.searcher.index.collect;

import java.util.LinkedList;

import net.bleujin.searcher.index.event.ICollectorEvent;
import net.bleujin.searcher.index.event.ICollectorEvent.EventType;

public class DebugQueueListener implements ICollectListener {

	private LinkedList<ICollectorEvent> collects = new LinkedList<ICollectorEvent>();

	public void collected(ICollectorEvent event) {
		if (! event.getEventType().equals(EventType.Normal)) return ;

		collects.add(event) ;
	}
	
	public ICollectorEvent[] getCollectorEvents(){
		return (ICollectorEvent[])collects.toArray(new ICollectorEvent[0]) ;
	}

}
