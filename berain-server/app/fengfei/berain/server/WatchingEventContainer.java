package fengfei.berain.server;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchingEventContainer {

	private final static Logger logger = LoggerFactory
			.getLogger(WatchingEventContainer.class);
	private final Lock lock = new ReentrantLock();
	// private final Set<WatchableEvent> allEvents = new HashSet<>();
	/**
	 * key=clientId+:+path
	 */
	private final Map<String, Set<WatchableEvent>> watchableEvents = new ConcurrentHashMap<>();
	/**
	 * <pre>
	 * path
	 * 	  - client 1
	 *    - client 2
	 *    - client 3
	 * 
	 * </pre>
	 */
	private final Map<String, Set<String>> clientsForPath = new ConcurrentHashMap<>();// key=path,value=clients
	/**
	 * <pre>
	 * client
	 * 	  - path 1
	 *    - path 2
	 *    - path 3
	 * 
	 * </pre>
	 */
	private final Map<String, Set<String>> pathsForClient = new ConcurrentHashMap<>();// key=clientId,value=paths

	public WatchingEventContainer() {
	}

	public Set<WatchableEvent> getWatchableEvents(String path, String clientId) {
		return watchableEvents.get(key(path, clientId));
	}

	private String key(String path, String clientId) {
		return clientId + ":" + path;
	}

	public Set<String> getClients() {
		return pathsForClient.keySet();
	}

	public Set<String> getPaths() {
		return clientsForPath.keySet();
	}

	public Set<String> getClientsByPath(String path) {
		return clientsForPath.get(path);
	}

	public Set<String> getPathsByClient(String clientId) {
		return pathsForClient.get(clientId);
	}

	private void put(Map<String, Set<String>> map, String key, String value) {
		Set<String> values = map.get(key);
		if (values == null) {
			values = new HashSet<>();
		}
		values.add(value);
		map.put(key, values);
	}

	public void addWatchableEvent(String clientId, WatchableEvent event) {
		try {
			String path = event.getPath();

			lock.lock();
			Set<WatchableEvent> events = getWatchableEvents(path, clientId);
			if (events == null) {
				events = new HashSet<>();
			}
			events.add(event);
			watchableEvents.put(key(event.getPath(), clientId), events);
			put(clientsForPath, path, clientId);
			put(pathsForClient, clientId, path);

		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void addWatchableEvent(String clientId, String path, int eventType) {
		WatchableEvent event = new WatchableEvent(eventType, path);
		addWatchableEvent(clientId, event);
	}

	public WatchableEvent getWatchableEvent(String clientId, String path,
			int eventType) {
		try {
			lock.lock();
			Set<WatchableEvent> events = watchableEvents.get(key(path, clientId));
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

	public void removeWatchableEventByClient(String clientId) {

		try {
			lock.lock();
			Set<String> paths = pathsForClient.remove(clientId);
			for (String path : paths) {
				watchableEvents.remove(key(path, clientId));
				clientsForPath.remove(path);
			}
		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);

		} finally {
			lock.unlock();
		}

	}

	public void removeWatchableEventByPath(String path) {

		try {
			lock.lock();
			Set<String> clients = clientsForPath.remove(path);
			for (String clientId : clients) {
				watchableEvents.remove(key(path, clientId));
				pathsForClient.remove(clientId);
			}

		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);

		} finally {
			lock.unlock();
		}

	}

	public void removeWatchableEventByClient(String clientId,
			WatchableEvent event) {
		try {
			lock.lock();

			Set<WatchableEvent> events = getWatchableEvents(event.getPath(),
					clientId);
			if (events == null) {
				events = new HashSet<>();
			}
			events.remove(event);
			watchableEvents.put(event.getPath(), events);
			// allEvents.remove(event);
		} catch (Throwable e) {
			logger.error("addWatchableEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	public void removeWatchableEvent(WatchableEvent event) {

		Set<String> clients = clientsForPath.get(event.getPath());
		for (String clientId : clients) {
			removeWatchableEventByClient(clientId, event);
		}

	}

	public Set<WatchableEvent> getWatchableEventByClient(String clientId) {

		Set<WatchableEvent> events = new HashSet<>();
		Set<String> paths = pathsForClient.get(clientId);
		for (String path : paths) {

			Set<WatchableEvent> eventsTemp = watchableEvents.get(key(path,
					clientId));
			events.addAll(eventsTemp);
		}
		return events;

	}

	public Set<WatchableEvent> getWatchableEventByPath(String path) {
		Set<WatchableEvent> events = new HashSet<>();

		Set<String> clients = clientsForPath.get(path);
		for (String clientId : clients) {

			Set<WatchableEvent> eventsTemp = watchableEvents.get(key(path,
					clientId));
			events.addAll(eventsTemp);
		}
		return events;
	}

	public void clearAllWatchableEvent() {
		watchableEvents.clear();
		clientsForPath.clear();
		pathsForClient.clear();
	}

	public int eventSize() {
		return watchableEvents.size();
	}

	public int clientSize() {
		return pathsForClient.size();
	}

	public int pathSize() {
		return clientsForPath.size();
	}

	public Map<String, Set<WatchableEvent>> getWatchableEvents() {
		return watchableEvents;
	}

	public Map<String, Set<String>> getClientsForPath() {
		return clientsForPath;
	}

	public Map<String, Set<String>> getPathsForClient() {
		return pathsForClient;
	}

	public static void main(String[] args) {
		WatchingEventContainer wec = new WatchingEventContainer();
		wec.addWatchableEvent("1", new WatchableEvent(1, "/root/1"));
		wec.addWatchableEvent("1", new WatchableEvent(2, "/root/1"));
		wec.addWatchableEvent("1", new WatchableEvent(3, "/root/1"));
		wec.addWatchableEvent("1", new WatchableEvent(1, "/root/2"));
		wec.addWatchableEvent("1", new WatchableEvent(3, "/root/2"));

		wec.addWatchableEvent("2", new WatchableEvent(1, "/root/1"));
		wec.addWatchableEvent("2", new WatchableEvent(2, "/root/3"));
		wec.addWatchableEvent("2", new WatchableEvent(3, "/root/1"));
		wec.addWatchableEvent("2", new WatchableEvent(1, "/root/2"));
		wec.addWatchableEvent("2", new WatchableEvent(3, "/root/2"));
		System.out.println(wec.eventSize());
		System.out.println(wec.clientSize());
		System.out.println(wec.pathSize());
		System.out.println(wec.getClients());
		System.out.println(wec.getPaths());
		System.out.println(wec.getPathsForClient());
		System.out.println(wec.getClientsForPath());
		System.out.println(wec.getWatchableEvents());
	}
}