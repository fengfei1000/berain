package fengfei.berain.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchedContainer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
 
	private final Lock lock = new ReentrantLock();
	private final Map<String, Set<WatchedEvent>> watchedEvents = new ConcurrentHashMap<>();

 

	public WatchedContainer() {
	}

	public Set<WatchedEvent> getWatchedEvents(String path) {
		return watchedEvents.get(path);
	}

	public void addWatchedEvent(WatchedEvent event) {
		try {
			lock.lock();

			if (event != null) {
				Set<WatchedEvent> events = watchedEvents.get(event.getPath());
				if (events == null) {
					events = new HashSet<>();
				}
				events.add(event);
				watchedEvents.put(event.getPath(), events);
			}

		} catch (Throwable e) {
			logger.error("addWatchedEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void removeWatchedEvent(WatchedEvent event) {

		try {
			lock.lock();
			Set<WatchedEvent> events = watchedEvents.get(event.getPath());
			if (events == null) {
				events = new HashSet<>();
			}
			events.remove(event);

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