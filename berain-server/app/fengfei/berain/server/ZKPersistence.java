package fengfei.berain.server;

import java.util.ArrayList;
import java.util.List;

import models.RainModel;

import com.netflix.curator.framework.CuratorFramework;

public class ZKPersistence implements Persistence {

	private CuratorFramework client;

	public ZKPersistence(CuratorFramework client) {
		super();
		this.client = client;

	}

	public void initNamespace() {

	}

	@Override
	public RainModel addNode(String pid, String key, String value) throws Exception {
		String parentPath = Focus.id2path(pid);

		String path = parentPath + SEPARATOR + key;
		System.out.println("-path----------------------------:  " + path);
		client.inTransaction().create().forPath(path, value.getBytes()).and().commit();
		parentPath = "".equals(parentPath) ? ROOT_PATH : parentPath;
		RainModel model = new RainModel();
		model.id = path.replaceAll("[/]", "_");
		model.pid = parentPath.replaceAll("[/]", "_");
		model.key = key;
		model.path = path;
		model.value = value;
		System.out.println("model:  " + model);
		return model;
	}

	@Override
	public RainModel editNode(String id, String value) throws Exception {

		String path = Focus.id2path(id);
		String parentPath = Focus.getParent(path);
		String key = Focus.getKey(path);
		client.inTransaction().setData().forPath(path, value.getBytes()).and().commit();

		RainModel model = new RainModel();
		model.id = path.replaceAll("[/]", "_");
		model.pid = parentPath.replaceAll("[/]", "_");
		model.key = key;
		model.path = path;
		model.value = value;
		return model;
	}

	@Override
	public RainModel removeNode(String id) throws Exception {
		String path = Focus.id2path(id);
		String parentPath = Focus.getParent(path);
		String key = Focus.getKey(path);
		byte[] data = client.getData().forPath(path);
		client.inTransaction().delete().forPath(path).and().commit();
		RainModel model = new RainModel();
		model.id = path.replaceAll("[/]", "_");
		model.pid = parentPath.replaceAll("[/]", "_");
		model.key = key;
		model.path = path;
		model.value = new String(data);
		return model;
	}

	public RainModel getNodeByPath(String path) throws Exception {
		String key = Focus.getKey(path);
		byte[] data = client.getData().forPath(path);
		String parentPath = Focus.getParent(path);
		RainModel model = new RainModel();
		model.id = path.replaceAll("[/]", "_");
		model.pid = parentPath.replaceAll("[/]", "_");
		model.key = key;
		model.path = path;
		model.value = new String(data);
		return model;
	}

	@Override
	public RainModel getNode(String id) throws Exception {
		// System.out.println("===========================================");
		// System.out.println(id);
		String path = Focus.id2path(id);
		String tmpPath = path;

		if (path.endsWith("/")) {
			tmpPath = path.substring(0, path.length() - 1);
		}
		if ("".equals(path)) {
			path = "/";
		}
		String parentPath = Focus.getParent(tmpPath);
		String key = Focus.getKey(tmpPath);
		byte[] data = client.getData().forPath(tmpPath);
		RainModel model = new RainModel();
		model.id = tmpPath.replaceAll("[/]", "_");
		model.pid = parentPath.replaceAll("[/]", "_");
		model.key = key;
		model.path = tmpPath;
		model.value = new String(data);
		return model;

	}

	@Override
	public boolean exists(String path) throws Exception {
		return client.checkExists().forPath(path) != null;

	}

	@Override
	public List<RainModel> nextNodes(String id) throws Exception {
		List<RainModel> models = new ArrayList<>();
		System.out.println("===========================================");
		System.out.println(id);
		String path = Focus.id2path(id);
		String tmpPath = path;

		if (path.endsWith("/")) {
			tmpPath = path.substring(0, path.length() - 1);
		}
		if ("".equals(path)) {
			path = "/";
		}
		System.out.printf("%s %s %s \n", id, path, tmpPath);
		if (client.checkExists().forPath(tmpPath) != null) {
			List<String> paths = client.getChildren().forPath(tmpPath);
			for (String cpath : paths) {
				String ppath = tmpPath + SEPARATOR + cpath;
				byte[] data = client.getData().forPath(ppath);
				RainModel model = new RainModel();
				model.id = ppath.replaceAll("[/]", "_");
				model.pid = (path).replaceAll("[/]", "_");
				model.key = Focus.getKey(cpath);
				model.path = ppath;
				model.value = new String(data);
				models.add(model);
			}
		}
		System.out.println(models);
		return models;
	}

}
