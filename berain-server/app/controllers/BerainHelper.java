package controllers;

import static models.Status.NonExists;
import static models.Status.ServerError;
import static models.Status.Success;
import static models.Status.Exists;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.BerainResult;
import models.RainModel;

import org.apache.commons.codec.digest.DigestUtils;

import fengfei.berain.server.ClientContainer;
import fengfei.berain.server.Focus;
import fengfei.berain.server.WatchableEvent;

public class BerainHelper {

	public static final String ROOT_PATH = "/";
	public static final String SEPARATOR = "/";
	private static ClientContainer container = ClientContainer.get();

	// --------------------------write-----------------------------//
	public static BerainResult<Boolean> update(String path, String value) {
		try {
			List<RainModel> models = RainModel.find("path=?", path).fetch();
			if (models != null && models.size() >= 1) {
				RainModel model = models.get(models.size() - 1);
				model.value = value;
				model.save();

				return new BerainResult(Success, true);
			} else {
				return new BerainResult(NonExists, false);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}
	}
	@play.db.jpa.Transactional
	public static BerainResult<Boolean> create(String path, String value) {

		try {
			RainModel exist = RainModel.find("path=?", path).first();
			if (exist != null) {
				return new BerainResult(Exists, false);
			}
			String parentPath = Focus.getParent(path);
			List<RainModel> models = RainModel.find("path=?", parentPath)
					.fetch();
			String pid = "0";
			if (models != null && models.size() >= 1) {
				pid = models.get(models.size() - 1).id;
			}
			RainModel model = new RainModel();
			model.pid = pid;
			model.key = Focus.getKey(path);
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

			return new BerainResult(Success, true);
		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}

	}

	public static BerainResult<Boolean> delete(String path) {

		try {
			List<RainModel> models = RainModel.find("path=?", path).fetch();
			if (models != null && models.size() == 1) {
				RainModel model = models.get(models.size() - 1);
				model.delete();
				return new BerainResult(Success, true);

			} else {
				return new BerainResult(NonExists, false);
			}

		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}
	}

	public static BerainResult<Boolean> copy(String originalPath, String newPath) {

		try {
			List<RainModel> models = RainModel.find("path=?", originalPath)
					.fetch();
			RainModel orig = null;
			if (models != null && models.size() == 1) {
				orig = models.get(0);
				orig.path = newPath;
				orig.id = null;
				orig.save();

				return new BerainResult(Success, true);
			} else {
				return new BerainResult(NonExists, false);
			}

		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}

	}

	// --------------------------read-----------------------------//
	public static BerainResult<List<Map<String, String>>> nextChildren(
			String parentPath) {
		try {
			List<RainModel> models = RainModel.find("path=?", parentPath)
					.fetch();
			if (models != null && models.size() >= 1) {
				RainModel model = models.get(models.size() - 1);
				String id = model.id;
				List<RainModel> children = RainModel.find("byPid", id).fetch();
				List<Map<String, String>> datas = new ArrayList<>();
				if (children != null && children.size() > 0) {
					for (RainModel m : children) {
						Map<String, String> data = new HashMap<String, String>();
						data.put("key", m.key);
						data.put("value", m.value);
						data.put("path", m.path);
						datas.add(data);
					}
					return new BerainResult(Success, datas);
				} else {
					return new BerainResult(NonExists);
				}

			} else {
				return new BerainResult(NonExists);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError);
		}
	}

	public static BerainResult<String> get(String path) {
		try {
			List<RainModel> models = RainModel.find("path=?", path).fetch();
			if (models != null && models.size() == 1) {
				RainModel model = models.get(models.size() - 1);
				return new BerainResult(Success, model.value);
			} else {
				return new BerainResult(NonExists);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError);
		}

	}

	public static BerainResult<Map<String, String>> getFull(String path) {

		try {
			List<RainModel> models = RainModel.find("path=?", path).fetch();
			if (models != null && models.size() == 1) {
				RainModel model = models.get(models.size() - 1);
				Map<String, String> data = new HashMap<String, String>();
				data.put("key", model.key);
				data.put("value", model.value);
				data.put("path", model.path);
				return new BerainResult(Success, data);
			} else {
				return new BerainResult(NonExists);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError);
		}

	}

	public static BerainResult<Boolean> exists(String path) {

		try {
			List<RainModel> models = RainModel.find("path=?", path).fetch();
			if (models != null && models.size() >= 1) {
				return new BerainResult(Success, true);
			} else {
				return new BerainResult(NonExists, false);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}
	}

	// --------------------------Event-----------------------------//
	public static BerainResult<Boolean> addWatchable(String clientId,
			String path, int type) {

		try {
			container.addWatchableEvent(clientId.toString(),
					new WatchableEvent(type, path));
			return new BerainResult(Success, true);

		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}
	}

	public static BerainResult<Boolean> removeWatchable(String clientId,
			String path, int type) {
		try {
			container.removeWatchableEvent(clientId.toString(),
					new WatchableEvent(type, path));
			return new BerainResult(Success, true);

		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}

	}

	public static BerainResult<Boolean> removeWatchedEvent(String clientId,
			String path, int type) {
		try {
			container.removeWatchedEvent(clientId, path, type);
			return new BerainResult(Success, true);

		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}

	}

	public static BerainResult<Boolean> removeAllListener(String clientId) {

		try {
			container.clearAllWatchableEvent(clientId.toString());
			container.clearAllWatchedEvent(clientId.toString());
			return new BerainResult(Success, true);

		} catch (Throwable e) {
			e.printStackTrace();
			return new BerainResult(ServerError, false);
		}
	}

	public static BerainResult dump(String clientId) {
		if (clientId == null || "".equals(clientId)) {
			return new BerainResult(Success, container);
		} else {

			return new BerainResult(Success, container.getWatcheds().get(
					clientId));
		}

	}
}
