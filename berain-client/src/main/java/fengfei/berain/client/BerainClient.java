package fengfei.berain.client;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author wtt
 * 
 */
public interface BerainClient {

	/**
	 * if has password
	 */
	void login(String username, String password);

	// --------------------------write-----------------------------//
	boolean update(String path, String value);

	boolean create(String path, String value);

	boolean delete(String path);

	boolean copy(String originalPath, String newPath);

	// --------------------------read-----------------------------//
	List<BerainEntry> nextChildren(String parentPath);

	String get(String path);

	BerainEntry getFull(String path);

	boolean exists(String path);

	// --------------------------Event-----------------------------//
	void addWatchable(String path, int type, Wather wather);

	void addWatchable(String path, EventType type, Wather wather);

	void removeWatchable(String path, int type);

	Map<String, List<WatchedEvent>> listChangedNodes();

	void removeAllListener();

}