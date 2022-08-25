package net.bleujin.searcher.index.collect;

import java.util.EventListener;

import net.bleujin.searcher.index.event.ICollectorEvent;


public interface ICollectListener extends EventListener{
	
	public void collected(ICollectorEvent event);

}
