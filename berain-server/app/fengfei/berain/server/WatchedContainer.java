package fengfei.berain.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.MoreExecutors;

public class WatchedContainer {

	private final static Logger logger = LoggerFactory.getLogger(WatchedContainer.class);

	private WatchableContainer watchableContainer;
	private final Lock lock = new ReentrantLock();
	private final Map<String, Set<WatchedEvent>> watchedEvents = new ConcurrentHashMap<>();

	public WatchedContainer(WatchableContainer watchableContainer) {
		this.watchableContainer = watchableContainer;
	}

	public Set<WatchedEvent> getWatchedEvents(String path) {
		return watchedEvents.get(path);
	}

	public void addWatchedEvent(String path, int eventType) {
		try {
			lock.lock();
			WatchableEvent event = watchableContainer.getWatchableEvent(path, eventType);
			if (event != null) {
				Set<WatchedEvent> events = watchedEvents.get(event.getPath());
				if (events == null) {
					events = new HashSet<>();
				}
				events.add(event.wrap());
				watchedEvents.put(event.getPath(), events);
			}

		} catch (Throwable e) {
			logger.error("addWatchedEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void addWatchedEvent(WatchedEvent event) {
		addWatchedEvent(event.getPath(), event.getEventType().getIntValue());
	}

	public void removeWatchedEvent(WatchedEvent event) {

		try {
			lock.lock();
			Set<WatchedEvent> events = watchedEvents.get(event.getPath());
			if (events == null) {
				events = new HashSet<>();
			}
			for (Iterator iterator = events.iterator(); iterator.hasNext();) {
				WatchedEvent watchedEvent = (WatchedEvent) iterator.next();
				if (watchedEvent.equals(event)) {
					iterator.remove();
				}

			}
			// events.remove(event);
			System.out.println(events);
			System.out.println(event);
			watchedEvents.put(event.getPath(), events);
		} catch (Throwable e) {
			logger.error("addWatchedEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void clearAllWatchedEvent() {
		watchedEvents.clear();
	}

	public int size() {
		return watchedEvents.size();
	}

	public Map<String, Set<WatchedEvent>> getWatchedEvents() {
		return watchedEvents;
	}

}