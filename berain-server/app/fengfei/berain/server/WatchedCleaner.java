package fengfei.berain.server;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchedCleaner extends Thread {

	private final static Logger logger = LoggerFactory.getLogger(WatchedCleaner.class);
	private static ClientContainer container = ClientContainer.get();
	private static long timeMillis = 600000;

	@Override
	public void run() {
		logger.info("Starting  Watcher clean...");
		while (true) {
			Map<String, Long> lastUpdated = container.getLastUpdated();
			for (Entry<String, Long> entry : lastUpdated.entrySet()) {
				String clientId = entry.getKey();
				long ms = entry.getValue();
				if (ms + timeMillis <= System.currentTimeMillis()) {
					container.clearAllWatchableEvent(clientId);
					logger.info("Clean all WatchableEvent for client:" + clientId);
					container.clearAllWatchedEvent(clientId);
					logger.info("Clean all WatchedEvent for client:" + clientId);
					container.cleanLastUpdated(clientId);
					logger.info("Clean clean LastUpdated for client:" + clientId);
				}
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
