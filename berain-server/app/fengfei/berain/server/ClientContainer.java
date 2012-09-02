package fengfei.berain.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientContainer {

	private final static Logger logger = LoggerFactory.getLogger(ClientContainer.class);
	private static ClientContainer clientContainer = new ClientContainer();
	private final Lock lock = new ReentrantLock();
	private Map<String, WatchableContainer> watchables = new HashMap<>();
	private Map<String, WatchedContainer> watcheds = new HashMap<>();
	private Map<String, Long> lastUpdated = new HashMap<>();

	public static ClientContainer get() {
		return clientContainer;
	}

	private ClientContainer() {

	}

	public Map<String, WatchableContainer> getWatchables() {
		return watchables;
	}

	public WatchableEvent varifyWatchableEvent(String clientId, String path, int eventType) {
		WatchableContainer container = watchables.get(clientId);
		if (container == null) {
			container = new WatchableContainer();
			WatchableEvent event = new WatchableEvent(eventType, path);
			container.addWatchableEvent(event);
			watchables.put(clientId, container);
			return event;
		}
		WatchableEvent event = container.getWatchableEvent(path, eventType);
		if (event == null) {
			event = new WatchableEvent(eventType, path);
			container.addWatchableEvent(event);

		}
		lastUpdated.put(clientId, System.currentTimeMillis());
		return event;
	}

	public Map<String, Long> getLastUpdated() {
		return lastUpdated;
	}

	public void cleanLastUpdated(String clientId) {
		lastUpdated.remove(clientId);
	}

	public Map<String, Set<WatchedEvent>> getAllWatchedEvents(String clientId) {
		WatchedContainer container = watcheds.get(clientId);
		if (container == null) {
			return null;
		}
		lastUpdated.put(clientId, System.currentTimeMillis());
		return container.getWatchedEvents();
	}

	public Map<String, WatchedContainer> getWatcheds() {
		return watcheds;
	}

	public void addWatchedEvent(String path, int eventType) {
		try {
			lock.lock();

			for (Entry<String, WatchableContainer> entry : watchables.entrySet()) {
				String clientId = entry.getKey();
				WatchableContainer watchableContainer = entry.getValue();
				WatchableEvent event = watchableContainer.getWatchableEvent(path, eventType);
				if (event != null) {
					WatchedContainer watchedContainer = watcheds.get(clientId);
					if (watchedContainer == null) {
						watchedContainer = new WatchedContainer(watchableContainer);
					}
					watchedContainer.addWatchedEvent(path, eventType);

					watcheds.put(clientId, watchedContainer);
				}
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

	public void removeWatchedEvent(String clientId, String path, int eventType) {

		try {
			lock.lock();

			WatchedContainer watchedContainer = watcheds.get(clientId);
			if (watchedContainer != null) {
				watchedContainer.removeWatchedEvent(new WatchedEvent(eventType, path));
				if (watchedContainer.size() <= 0) {
					watcheds.remove(clientId);
				}
			}

		} catch (Throwable e) {
			logger.error("removeWatchedEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void addWatchableEvent(String clientId, WatchableEvent event) {
		try {
			lock.lock();
			WatchableContainer watchableContainer = watchables.get(clientId);
			if (watchableContainer == null) {
				watchableContainer = new WatchableContainer();
			}
			watchableContainer.addWatchableEvent(event);
			watchables.put(clientId, watchableContainer);
			lastUpdated.put(clientId, System.currentTimeMillis());
		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void removeWatchableEvent(String clientId, WatchableEvent event) {

		try {
			lock.lock();
			WatchableContainer watchableContainer = watchables.get(clientId);
			if (watchableContainer != null) {
				watchableContainer.removeWatchableEvent(event);
			}
			lastUpdated.put(clientId, System.currentTimeMillis());
		} catch (Throwable e) {
			logger.error("removeWatchableEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void clearAllWatchableEvent(String clientId) {
		try {
			lock.lock();
			WatchableContainer watchableContainer = watchables.get(clientId);
			if (watchableContainer != null) {
				watchableContainer.clearAllWatchableEvent();
			}
			watchables.remove(clientId);
		} catch (Throwable e) {
			logger.error("clearAllWatchableEvents error", e);

		} finally {
			lock.unlock();
		}

	}

	public void clearAllWatchedEvent(String clientId) {
		try {
			lock.lock();
			WatchedContainer watchedContainer = watcheds.get(clientId);
			if (watchedContainer != null) {
				watchedContainer.clearAllWatchedEvent();
			}
			watchables.remove(clientId);

		} catch (Throwable e) {
			logger.error("clearAllWatchedEvent error", e);

		} finally {
			lock.unlock();
		}
	}

}
