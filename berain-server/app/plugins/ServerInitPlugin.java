package plugins;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.PlayPlugin;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryNTimes;

import fengfei.berain.server.DbPersistence;
import fengfei.berain.server.Focus;
import fengfei.berain.server.Persistence;
import fengfei.berain.server.WatchedCleaner;
import fengfei.berain.server.ZKPersistence;

public class ServerInitPlugin extends PlayPlugin {

	static Logger logger = LoggerFactory.getLogger(ServerInitPlugin.class);
	final static String Persistence = "berain.persistence";
	final static String Namespace = "berain.namespace";
	final static String Zookeeper = "zk";
	final static String Database = "db";
	static Persistence persistence = null;

	@Override
	public void onApplicationStart() {
		Properties p = Play.configuration;
		String pst = p.getProperty(Persistence);
		String namespace = p.getProperty(Namespace);
		Focus.namespace = namespace;
		if (pst.equalsIgnoreCase(Zookeeper)) {
			startZookeeperClient();
			logger.info("start zookeeper persistence");

		} else if (pst.equalsIgnoreCase(Database)) {
			persistence = new DbPersistence();

			logger.info("start db persistence");

		} else {
			if (persistence == null) {
				throw new RuntimeException("Persistence is not initialize.");
			}
		}
		Focus.persistence = persistence;
		new WatchedCleaner().start();
	}

	private void startZookeeperClient() {
		Properties p = Play.configuration;
		String host = p.getProperty("zk.host");
		String namespace = Focus.namespace;
		int timeout = 5000;
		int retryTimes = Integer.MAX_VALUE;
		int sleepRetry = 1000;

		String stimeout = p.getProperty("zk.connectionTimeoutMs");
		String sretryTimes = p.getProperty("zk.RetryNTimes");
		String ssleepRetry = p.getProperty("zk.sleepMsBetweenRetries");
		CuratorFramework client;

		try {
			if (null == namespace || "".equals(namespace)) {
				namespace = "/berain";
			}
			if (null != stimeout && !"".equals(stimeout)) {
				timeout = Integer.parseInt(stimeout);
			}
			if (null != sretryTimes && !"".equals(sretryTimes)) {
				retryTimes = Integer.parseInt(sretryTimes);
			}
			if (null != ssleepRetry && !"".equals(ssleepRetry)) {
				sleepRetry = Integer.parseInt(ssleepRetry);
			}
			client = CuratorFrameworkFactory.builder().connectString(host)
					.namespace(namespace)
					.retryPolicy(new RetryNTimes(retryTimes, sleepRetry))
					.connectionTimeoutMs(timeout).build();
			client.start();
			persistence = new ZKPersistence(client);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		CuratorFramework client = CuratorFrameworkFactory.builder()
				.connectString("localhost:2181").namespace("/test")
				.retryPolicy(new RetryNTimes(100, 1222))
				.connectionTimeoutMs(12222).build();
		client.start();
		client.inTransaction().create().forPath("/k1", "v1".getBytes()).and()
				.commit();
	}

}
