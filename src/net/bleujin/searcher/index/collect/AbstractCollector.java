package net.bleujin.searcher.index.collect;

import java.util.EventListener;
import java.util.List;

import net.bleujin.searcher.index.event.BeginEvent;
import net.bleujin.searcher.index.event.EndEvent;
import net.bleujin.searcher.index.event.ICollectorEvent;
import net.bleujin.searcher.index.event.ShutdownEvent;
import net.ion.framework.util.ListUtil;

public abstract class AbstractCollector implements ICollector, Runnable {

	private final List<EventListener> listenerList = ListUtil.newList();
	private String name;
	private volatile boolean isShutDown;

	protected AbstractCollector(String name) {
		this.name = name;
	}

	public String getCollectName() {
		return this.name;
	}

	public void run() {
		collect();
	}

	public synchronized void shutdown(String cause) {
		fireCollectEvent(new ShutdownEvent(name, cause));
		isShutDown = true;
	}

	public synchronized boolean isShutDownState() {
		return isShutDown;
	}

	protected EventListener[] getEventListenerList() {
		return (EventListener[]) listenerList.toArray(new EventListener[0]);
	}

	public void addListener(EventListener listener) {
		listenerList.add(listener);
	}

	public void removeListener(EventListener listener) {
		listenerList.remove(listener);
	}

	protected void fireCollectEvent(ICollectorEvent event) {
		if (listenerList.size() == 0) {
			return;
		}
		// Process the listeners last to first, notifying those that are interested in this event
		for (EventListener listener : listenerList) {
			if (listener instanceof ICollectListener) {
				((ICollectListener) listener).collected(event);
			}
		}
	}

	protected void fireStart() {
		fireCollectEvent(new BeginEvent(this.name));
	}

	protected synchronized void fireEnd() {
		fireCollectEvent(new EndEvent(this.name));
		isShutDown = false;
	}

}
