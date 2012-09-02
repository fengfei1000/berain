package fengfei.berain.server;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import models.RainModel;

import org.apache.commons.codec.digest.DigestUtils;

public class DbPersistence implements Persistence {

 
	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.berain.server.Persistence#addNode(long, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public RainModel addNode(String pid, String key, String value) {

		String path = SEPARATOR + key;
		RainModel pm = RainModel.findById(pid);
		if (pm != null) {
			path = pm.path + path;
		}
		RainModel model = new RainModel();
		model.pid = pid;
		model.key = key;
		model.path = path;
		model.value = value;
		model.updateAt = new Date(System.currentTimeMillis());
		model.md5 = DigestUtils.md5Hex(model.key + model.value);
		model.updateAt = new Date(System.currentTimeMillis());
		model.createAt = new Date(System.currentTimeMillis());
		model.save();
		RainModel another = RainModel.findById(pid);
		if (another != null) {
			long count = RainModel.count(" pid=?", pid);
			another.leaf = (int) count;
			another.save();
		}
 
		System.out.println(model);
		return model;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.berain.server.Persistence#editNode(long, java.lang.String)
	 */
	@Override
	public RainModel editNode(String id, String value) {
		RainModel another = RainModel.findById(id);
		if (another != null) {
			another.updateAt = new Date(System.currentTimeMillis());
			another.value = value;
			another.md5 = DigestUtils.md5Hex(another.key + another.value);
			another.save();
			return another;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.berain.server.Persistence#removeNode(long)
	 */
	@Override
	public RainModel removeNode(String id) {
		RainModel another = RainModel.findById(id);
		if (another == null) {
			return null;
		} else {
			recursionRemoveNode(id);
			another.delete();

			return another;
		}

	}

	private void recursionRemoveNode(String id) {
		List<RainModel> models = RainModel.find("pid", id).fetch();
		if (models != null && models.size() > 0) {
			for (RainModel model : models) {
				recursionRemoveNode(model.id);
				model.delete();
			}
		}

	}

	@Override
	public RainModel getNode(String id) throws Exception {

		return RainModel.findById(id);
	}

	@Override
	public boolean exists(String path) throws Exception {
		List<RainModel> models = RainModel.find("path=?", path).fetch();
		if (models != null && models.size() > 0) {
			return true;
		} else {
			return false;
		}

	}

	public RainModel getNodeByPath(String path) throws Exception {
		List<RainModel> models = RainModel.find("path=?", path).fetch();
		if (models != null && models.size() > 0) {
			return models.get(models.size() - 1);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fengfei.berain.server.Persistence#nextNodes(long)
	 */
	@Override
	public List<RainModel> nextNodes(String id) {
		List<RainModel> models = null;
		if (START_ROOT_ID.equals(id)) {
			models = RainModel.find("path=?", Focus.namespace).fetch();
			if (models == null || models.size() == 0) {
				RainModel model = new RainModel();
				model.pid = "_";
				model.key = Focus.getKey(Focus.namespace);
				model.value = "Rain Root";
				model.path = Focus.namespace;
				model.updateAt = new Date(System.currentTimeMillis());
				model.md5 = DigestUtils.md5Hex(model.key + model.value);
				model.updateAt = new Date(System.currentTimeMillis());
				model.createAt = new Date(System.currentTimeMillis());
				model = model.merge();
				models = new ArrayList<>();
				models.add(model);
			}
			return models;
		} else {
			models = RainModel.find("byPid", id).fetch();
		}

		return models;
	}

	public void initNamespace() {
		Focus.namespace = Focus.removeLastSlash(Focus.namespace);
		List<RainModel> models = RainModel.find("path=?", Focus.namespace).fetch();
		if (models == null || models.size() == 0) {
			RainModel model = new RainModel();
			String key = Focus.getKey(Focus.namespace);
			if (ROOT_PATH.equals(Focus.namespace)) {
				key = "root";
			}
			model.pid = "_";
			model.key = key;
			model.value = "Rain Root";
			model.path = Focus.namespace;
			model.updateAt = new Date(System.currentTimeMillis());
			model.md5 = DigestUtils.md5Hex(model.key + model.value);
			model.updateAt = new Date(System.currentTimeMillis());
			model.createAt = new Date(System.currentTimeMillis());
			model = model.merge();

		}

	}

	static final String START_ROOT_ID = "_app_root";

}