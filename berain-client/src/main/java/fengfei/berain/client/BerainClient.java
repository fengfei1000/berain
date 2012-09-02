package fengfei.berain.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

public class BerainClient implements Runnable {

	public final static String COOKIE_USERNAME = "berain_user";
	public final static String COOKIE_PASSWORD = "berain_pwd";
	public final String ROOT_PATH = "/";
	public final String SEPARATOR = "/";
	//
	private static WatchedService watchedService = WatchedService.get();
	private String clientId;
	public WatchedContainer watched = new WatchedContainer();

	private static Queue<DefaultHttpClient> httpClients = new ConcurrentLinkedQueue<>();
	String baseurl;
	private String username;
	private String password;
	private boolean isLogon = false;
	ObjectMapper mapper = new ObjectMapper();

	public BerainClient(String baseurl, String username, String password) {
		super();
		this.baseurl = baseurl;
		this.username = username;
		this.password = password;
		clientId = UUID.randomUUID().toString();
		watchedService.addBerainClient(this);
	}

	public void start() {
		watchedService.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				BerainClient.this.stop();
			}
		});
	}

	public void stop() {
		removeAllListener();
		watchedService.stop();
	}

	public void login() {
		DefaultHttpClient httpclient = getHttpClient();
		try {

			List<Cookie> cookies = httpclient.getCookieStore().getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
			HttpPost httpost = new HttpPost(baseurl + "/logon");
			System.out.println("executing request " + httpost.getURI());
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", username));
			nvps.add(new BasicNameValuePair("password", password));
			nvps.add(new BasicNameValuePair("remember", "1"));
			httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpost, responseHandler);
			isLogon = Boolean.parseBoolean(responseBody);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnHttpClient(httpclient);
		}
	}

	private static DefaultHttpClient getHttpClient() {
		DefaultHttpClient httpclient = httpClients.poll();
		if (httpclient == null) {
			httpclient = new DefaultHttpClient();
		}
		return httpclient;
	}

	private static void returnHttpClient(DefaultHttpClient httpclient) {
		httpClients.offer(httpclient);
	}

	public static void main(String[] args) throws Exception {
		BerainClient client = new BerainClient("http://127.0.0.1:8021/", "admin", "passowrd");
		client.start();
		// client.login();
		client.addWatchable("/berain/w1", EventType.DataChanged, new Wather() {

			@Override
			public void call(BerainEntry data) {
				System.out.println("DataChanged:================ " + data);

			}
		});
		client.addWatchable("/berain/w1", EventType.ChildrenChanged, new Wather() {

			@Override
			public void call(BerainEntry data) {
				System.out.println("ChildrenChanged:================ " + data);

			}
		});

		List<Map<String, String>> s = client.nextChildren("/berain");
		System.out.println(s);
	}

	// --------------------------write-----------------------------//
	public boolean update(String id, String value) {

		try {
			BerainResult<Boolean> br = httpRequest(
					null,
					"/berain/update",
					"id",
					id,
					"value",
					value);
			return br.data;
		} catch (Throwable e) {

			e.printStackTrace();

		}
		return false;
	}

	public <T, E> BerainResult<T> httpRequest(
			Class<? extends BerainResult<T>> clazz,
			String httpPath,
			String... params) throws Exception {
		DefaultHttpClient httpclient = getHttpClient();
		BerainResult<T> br = null;
		HttpGet httpget = null;
		try {
			URIBuilder builder = new URIBuilder(baseurl);
			builder.setPath(httpPath);
			if (params != null && params.length % 2 == 0) {
				for (int i = 0; i < params.length; i++) {
					builder.setParameter(params[i++], params[i]);
				}
			}

			httpget = new HttpGet(builder.build());

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpget, responseHandler);
			System.out.println(responseBody);
			if (clazz == null) {
				br = mapper.readValue(responseBody, BerainResult.class);
			} else {
				br = mapper.readValue(responseBody, clazz);
			}

		} finally {
			httpget.releaseConnection();
			returnHttpClient(httpclient);
		}
		return br;

	}

	public boolean create(String id, String value) {

		try {
			BerainResult<Boolean> br = httpRequest(
					null,
					"/berain/create",
					"id",
					id,
					"value",
					value);
			return br.data;
		} catch (Throwable e) {

			e.printStackTrace();

		}
		return false;

	}

	public boolean delete(String id) {

		try {
			BerainResult<Boolean> br = httpRequest(null, "/berain/delete", "id", id);
			return br.data;
		} catch (Throwable e) {

			e.printStackTrace();

		}
		return false;

	}

	public boolean copy(String originalId, String newid) {

		try {
			BerainResult<Boolean> br = httpRequest(
					null,
					"/berain/copy",
					"originalId",
					originalId,
					"newid",
					newid);
			return br.data;
		} catch (Throwable e) {

			e.printStackTrace();

		}
		return false;

	}

	// --------------------------read-----------------------------//
	public List<Map<String, String>> nextChildren(String parentId) {

		try {
			BerainResult<List<Map<String, String>>> br = httpRequest(
					null,
					"/berain/nextChildren",
					"parentId",
					parentId);
			return br.data;
		} catch (Throwable e) {

			e.printStackTrace();

		}
		return null;

	}

	public String get(String id) {

		try {
			BerainResult<String> br = httpRequest(null, "/berain/get", "id", id);
			return br.data;
		} catch (Throwable e) {

			e.printStackTrace();

		}
		return null;

	}

	public BerainEntry getFull(String id) {
		try {
			BerainResult<BerainEntry> br = httpRequest(
					BerainEntryResult.class,
					"/berain/getFull",
					"id",
					id);
			return br.data;
		} catch (Throwable e) {

			e.printStackTrace();

		}
		return null;

	}

	private static class BerainEntryResult extends BerainResult<BerainEntry> {
	}

	public boolean exists(String id) {
		try {
			BerainResult<Boolean> br = httpRequest(null, "/berain/getFull", "id", id);
			return br.data;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}

	// --------------------------Event-----------------------------//
	public void addWatchable(String id, int type, Wather wather) {

		try {
			BerainResult<Boolean> br = httpRequest(
					null,
					"/berain/addWatchable",
					"clientId",
					clientId,
					"id",
					id,
					"type",
					String.valueOf(type));
			watched.addWatchedEvent(new WatchedEvent(EventType.fromInt(type), id, wather));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void addWatchable(String id, EventType type, Wather wather) {

		try {
			BerainResult<Boolean> br = httpRequest(
					null,
					"/berain/addWatchable",
					"clientId",
					clientId,
					"id",
					id,
					"type",
					String.valueOf(type.getIntValue()));
			watched.addWatchedEvent(new WatchedEvent(type, id, wather));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void removeWatchable(String id, int type) {
		try {
			BerainResult<Boolean> br = httpRequest(
					null,
					"/berain/removeWatchable",
					"clientId",
					clientId,
					"id",
					id,
					"type",
					String.valueOf(type));
			watched.removeWatchedEvent(new WatchedEvent(EventType.fromInt(type), id));
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	private void removeRemoteWatchedEvent(String id, int type) {
		try {
			BerainResult<Boolean> br = httpRequest(
					null,
					"/berain/removeWatchedEvent",
					"clientId",
					clientId,
					"id",
					id,
					"type",
					String.valueOf(type));
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public Map<String, List<WatchedEvent>> listChangedNodes() {
		DefaultHttpClient httpclient = getHttpClient();
		try {

			HttpPost httpost = new HttpPost(baseurl + "/berain/listChangedNodes");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			Map<String, Set<WatchedEvent>> watchedEvents = watched.getWatchedEvents();
			for (Entry<String, Set<WatchedEvent>> entry : watchedEvents.entrySet()) {
				Set<WatchedEvent> ents = entry.getValue();
				for (WatchedEvent event : ents) {
					nvps.add(new BasicNameValuePair("paths", event.getPath()));
					nvps.add(new BasicNameValuePair("types", String.valueOf(event
							.getEventType()
							.getIntValue())));
				}
			}
			nvps.add(new BasicNameValuePair("clientId", clientId));
			httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httpost, responseHandler);
			BerainResult<Map<String, List<WatchedEvent>>> br = mapper.readValue(
					responseBody,
					WatchedEventResult.class);
			return br.data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			returnHttpClient(httpclient);
		}
		// try {
		//
		//
		//
		// BerainResult<Map<String, List<WatchedEvent>>> br = httpRequest(
		// WatchedEventResult.class,
		// "/berain/listChangedNodes",
		// "clientId",
		// clientId);
		//
		// return br.data;
		//
		// } catch (Throwable e) {
		// e.printStackTrace();
		// }

		return null;
	}

	public static class WatchedEventResult
			extends
			BerainResult<Map<String, List<WatchedEvent>>> {

	}

	public void removeAllListener() {
		try {
			try {
				BerainResult<Boolean> br = httpRequest(
						null,
						"/berain/removeAllListener",
						"clientId",
						clientId);
				watched.clearAllWatchedEvent();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {
			Map<String, Set<WatchedEvent>> all = watched.getWatchedEvents();
			Map<String, List<WatchedEvent>> watchedEvents = this.listChangedNodes();
			if (watchedEvents == null) {
				return;
			}
			for (Entry<String, List<WatchedEvent>> entry : watchedEvents.entrySet()) {
				String path = entry.getKey();
				List<WatchedEvent> events = entry.getValue();
				Set<WatchedEvent> ableEvents = all.get(path);
				if (ableEvents == null) {
					continue;
				}
				for (WatchedEvent event : events) {
					for (WatchedEvent ableEvent : ableEvents) {
						// System.out.printf(
						// " %s ==  %s\n",
						// event.toString(),
						// ableEvent.toString());
						if (ableEvent.equals(event)) {

							BerainEntry data = this.getFull(path);
							Wather wather = ableEvent.getWather();
							// sync
							wather.call(data);
							// async
							// wather.setData(data);
							// watchedService.execute(wather);
							// remove
							removeRemoteWatchedEvent(event.getPath(), event
									.getEventType()
									.getIntValue());
						}
					}

				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
