package fengfei.berain.server;

import java.util.Collections;
import java.util.HashSet;
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

public class WatchableContainer {

	private final static Logger logger = LoggerFactory.getLogger(WatchableContainer.class);
	private final Lock lock = new ReentrantLock();
	private final Map<String, Set<WatchableEvent>> watchableEvents = new ConcurrentHashMap<>();

	public WatchableContainer() {
	}

	public Set<WatchableEvent> getWatchableEvents(String path) {
		return watchableEvents.get(path);
	}

	public WatchableEvent getWatchableEvent(String path, int eventType) {
		try {
			lock.lock();
			Set<WatchableEvent> events = watchableEvents.get(path);
			if (events != null) {
				for (WatchableEvent watchableEvent : events) {
					if (watchableEvent.getEventType() == eventType) {
						return watchableEvent;
					}
				}
			}

			return null;
		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);
			return null;
		} finally {
			lock.unlock();
		}
	}

	public void addWatchableEvent(WatchableEvent event) {
		try {
			lock.lock();
			Set<WatchableEvent> events = watchableEvents.get(event.getPath());
			if (events == null) {
				events = new HashSet<>();
			}
			events.add(event);
			watchableEvents.put(event.getPath(), events);
		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void removeWatchableEvent(WatchableEvent event) {

		try {
			lock.lock();
			Set<WatchableEvent> events = watchableEvents.get(event.getPath());
			if (events == null) {
				events = new HashSet<>();
			}
			events.remove(event);

			watchableEvents.put(event.getPath(), events);
		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void clearAllWatchableEvent() {
		watchableEvents.clear();
	}

	public int size() {
		return watchableEvents.size();
	}

	public Map<String, Set<WatchableEvent>> getWatchableEvents() {
		return watchableEvents;
	}

}