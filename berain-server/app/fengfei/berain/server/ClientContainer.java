package fengfei.berain.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.protobuf.netty.NettyRpcProto;
import com.googlecode.protobuf.netty.NettyRpcProto.RpcResponse;

import fengfei.berain.server.protobuf.BerainProto;
import fengfei.berain.server.protobuf.BerainProto.StatusResult;
import fengfei.berain.server.protobuf.BerainProto.WatchedResponse;

public class ClientContainer {

	private final static Logger logger = LoggerFactory
			.getLogger(ClientContainer.class);
	private static ClientContainer clientContainer = new ClientContainer();
	private ChannelGroup channelGroup;
	private final Lock lock = new ReentrantLock();
	private final WatchingEventContainer eventContainer = new WatchingEventContainer();

	private Map<String, Integer> watchableClients = new HashMap<>();// //key=path,value=clientId
	private Map<String, WatchedContainer> watcheds = new HashMap<>();
	private Map<String, Long> lastUpdated = new HashMap<>();

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

	// public WatchableEvent varifyWatchableEvent(Integer clientId, String path,
	// int eventType) {
	// eventContainer.getWatchableEvent(path, eventType)
	// WatchableContainer container = watchables.get(clientId);
	// if (container == null) {
	// container = new WatchableContainer();
	// WatchableEvent event = new WatchableEvent(eventType, path);
	// container.addWatchableEvent(event);
	// watchables.put(clientId, container);
	// return event;
	// }
	// WatchableEvent event = container.getWatchableEvent(path, eventType);
	// if (event == null) {
	// event = new WatchableEvent(eventType, path);
	// container.addWatchableEvent(event);
	//
	// }
	// lastUpdated.put(clientId, System.currentTimeMillis());
	// return event;
	// }

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

	public void fireWatchedEvent(String path, int eventType) {
		try {
			lock.lock();

			Set<String> clients = eventContainer.getClientsByPath(path);
			for (String clientId : clients) {
				Set<WatchableEvent> events = eventContainer.getWatchableEvents(
						path, clientId);
				if (events != null && events.size() > 0) {
					WatchedContainer watchedContainer = watcheds.get(clientId);
					if (watchedContainer == null) {
						watchedContainer = new WatchedContainer(eventContainer);
					}
					watchedContainer.addWatchedEvent(path, eventType);
					watcheds.put(clientId, watchedContainer);

					Set<WatchedEvent> watchedEvents = watchedContainer
							.getWatchedEvents(path);
					int id = Integer.parseInt(clientId);
					StatusResult sr = StatusResult.newBuilder().setCode(0)
							.setMessage("successed.").build();
					WatchedResponse.Builder builder = BerainProto.WatchedResponse
							.newBuilder().setResult(sr);
					for (WatchedEvent watchedEvent : watchedEvents) {
						BerainProto.WatchedEvent event = BerainProto.WatchedEvent
								.newBuilder()
								.setEventType(
										BerainProto.EventType
												.valueOf(watchedEvent
														.getEventType()
														.getIntValue()))
								.setPath(watchedEvent.getPath()).build();
						builder.addWatchedEvents(event);
					}

					WatchedResponse watchedResponse = builder.build();
					RpcResponse response = NettyRpcProto.RpcResponse
							.newBuilder().setId(id)
							.setResponseMessage(watchedResponse.toByteString())
							.build();
					Channel channel = getChannel(id);
					channel.write(response);

				}

			}

		} catch (Throwable e) {
			logger.error("addWatchedEvent error", e);

		} finally {
			lock.unlock();
		}
	}

	//
	// public void addWatchedEvent(String path, int eventType) {
	// try {
	// lock.lock();
	//
	// for (Entry<String, WatchableContainer> entry : watchables
	// .entrySet()) {
	// String clientId = entry.getKey();
	//
	// WatchableContainer watchableContainer = entry.getValue();
	// WatchableEvent event = watchableContainer.getWatchableEvent(
	// path, eventType);
	// if (event != null) {
	// WatchedContainer watchedContainer = watcheds.get(clientId);
	// if (watchedContainer == null) {
	// watchedContainer = new WatchedContainer(
	// watchableContainer);
	// }
	// watchedContainer.addWatchedEvent(path, eventType);
	//
	// watcheds.put(clientId, watchedContainer);
	// }
	// }
	//
	// } catch (Throwable e) {
	// logger.error("addWatchedEvent error", e);
	//
	// } finally {
	// lock.unlock();
	// }
	// }

	public void fireWatchedEvent(WatchedEvent event) {
		fireWatchedEvent(event.getPath(), event.getEventType().getIntValue());
	}

	// public void addWatchedEvent(WatchedEvent event) {
	// addWatchedEvent(event.getPath(), event.getEventType().getIntValue());
	// }

	public void removeWatchedEvent(String clientId, String path, int eventType) {

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

	public void addWatchableEvent(String clientId, WatchableEvent event) {
		try {
			lock.lock();
			eventContainer.addWatchableEvent(clientId, event);

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
			eventContainer.removeWatchableEventByClient(clientId, event);
			watcheds.remove(clientId);
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
			eventContainer.removeWatchableEventByClient(clientId);

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

		} catch (Throwable e) {
			logger.error("clearAllWatchedEvent error", e);

		} finally {
			lock.unlock();
		}
	}

}
