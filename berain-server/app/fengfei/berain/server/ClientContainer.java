package fengfei.berain.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientContainer {

	private final static Logger logger = LoggerFactory
			.getLogger(ClientContainer.class);
	private static ClientContainer clientContainer = new ClientContainer();
	private ChannelGroup channelGroup;
	private final Lock lock = new ReentrantLock();
	private Map<Integer, WatchableContainer> watchables = new HashMap<>();
	private Map<Integer, WatchedContainer> watcheds = new HashMap<>();
	private Map<Integer, Long> lastUpdated = new HashMap<>();

	public static ClientContainer get() {
		return clientContainer;
	}

	private ClientContainer() {

	}

	public void setChannelGroup(ChannelGroup channelGroup) {
		this.channelGroup = channelGroup;
	}

	public Channel getChannel(Integer id) {
		return channelGroup.find(id);
	}

	public Map<Integer, WatchableContainer> getWatchables() {
		return watchables;
	}

	public WatchableEvent varifyWatchableEvent(Integer clientId, String path,
			int eventType) {
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

	public Map<Integer, Long> getLastUpdated() {
		return lastUpdated;
	}

	public void cleanLastUpdated(Integer clientId) {
		lastUpdated.remove(clientId);
	}

	public Map<String, Set<WatchedEvent>> getAllWatchedEvents(Integer clientId) {
		WatchedContainer container = watcheds.get(clientId);
		if (container == null) {
			return null;
		}
		lastUpdated.put(clientId, System.currentTimeMillis());
		return container.getWatchedEvents();
	}

	public Map<Integer, WatchedContainer> getWatcheds() {
		return watcheds;
	}

	public void addWatchedEvent(String path, int eventType) {
		try {
			lock.lock();

			for (Entry<Integer, WatchableContainer> entry : watchables
					.entrySet()) {
				Integer clientId = entry.getKey();
				Channel channel=getChannel(clientId);
		 
				WatchableContainer watchableContainer = entry.getValue();
				WatchableEvent event = watchableContainer.getWatchableEvent(
						path, eventType);
				if (event != null) {
					WatchedContainer watchedContainer = watcheds.get(clientId);
					if (watchedContainer == null) {
						watchedContainer = new WatchedContainer(
								watchableContainer);
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

	public void removeWatchedEvent(Integer clientId, String path, int eventType) {

		try {
			lock.lock();

			WatchedContainer watchedContainer = watcheds.get(clientId);
			if (watchedContainer != null) {
				watchedContainer.removeWatchedEvent(new WatchedEvent(eventType,
						path));
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

	public void addWatchableEvent(Integer clientId, WatchableEvent event) {
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

	public void removeWatchableEvent(Integer clientId, WatchableEvent event) {

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

	public void clearAllWatchableEvent(Integer clientId) {
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

	public void clearAllWatchedEvent(Integer clientId) {
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
