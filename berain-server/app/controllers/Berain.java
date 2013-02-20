package controllers;

import static models.Status.NonExists;
import static models.Status.ServerError;
import static models.Status.Success;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.BerainResult;
import models.RainModel;

import org.apache.commons.codec.digest.DigestUtils;

import play.mvc.Controller;
import fengfei.berain.server.ClientContainer;
import fengfei.berain.server.Focus;
import fengfei.berain.server.WatchableEvent;
import fengfei.berain.server.WatchedEvent;

public class Berain extends Controller {

	public static final String ROOT_PATH = "/";
	public static final String SEPARATOR = "/";
	private static ClientContainer container = ClientContainer.get();

	// --------------------------write-----------------------------//
	public static void update(String id, String value) {
		try {
			List<RainModel> models = RainModel.find("path=?", id).fetch();
			if (models != null && models.size() >= 1) {
				RainModel model = models.get(models.size() - 1);
				model.value = value;
				model.save();

				renderJSON(new BerainResult(Success, true));
			} else {
				renderJSON(new BerainResult(NonExists, false));
			}
		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}
	}

	public static void create(String id, String value) {

		try {
			String path = id;
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

			renderJSON(new BerainResult(Success, true));
		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}

	}

	public static void delete(String id) {

		try {
			List<RainModel> models = RainModel.find("path=?", id).fetch();
			if (models != null && models.size() == 1) {
				RainModel model = models.get(models.size() - 1);
				model.delete();
				renderJSON(new BerainResult(Success, true));

			} else {
				renderJSON(new BerainResult(NonExists, false));
			}

		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}
	}

	public static void copy(String originalId, String newid) {

		try {
			List<RainModel> models = RainModel.find("path=?", originalId)
					.fetch();
			RainModel orig = null;
			if (models != null && models.size() == 1) {
				orig = models.get(0);
				orig.path = newid;
				orig.id = null;
				orig.save();

				renderJSON(new BerainResult(Success, true));
			} else {
				renderJSON(new BerainResult(NonExists, false));
			}

		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}

	}

	// --------------------------read-----------------------------//
	public static void nextChildren(String parentId) {
		try {
			List<RainModel> models = RainModel.find("path=?", parentId).fetch();
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
					renderJSON(new BerainResult(Success, datas));
				} else {
					renderJSON(new BerainResult(NonExists));
				}

			} else {
				renderJSON(new BerainResult(NonExists));
			}
		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError));
		}
	}

	public static void get(String id) {
		try {
			List<RainModel> models = RainModel.find("path=?", id).fetch();
			if (models != null && models.size() == 1) {
				RainModel model = models.get(models.size() - 1);
				renderJSON(new BerainResult(Success, model.value));
			} else {
				renderJSON(new BerainResult(NonExists));
			}
		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError));
		}

	}

	public static void getFull(String id) {

		try {
			List<RainModel> models = RainModel.find("path=?", id).fetch();
			if (models != null && models.size() == 1) {
				RainModel model = models.get(models.size() - 1);
				Map<String, String> data = new HashMap<String, String>();
				data.put("key", model.key);
				data.put("value", model.value);
				data.put("path", model.path);
				renderJSON(new BerainResult(Success, data));
			} else {
				renderJSON(new BerainResult(NonExists));
			}
		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError));
		}

	}

	public static void exists(String id) {

		try {
			List<RainModel> models = RainModel.find("path=?", id).fetch();
			if (models != null && models.size() >= 1) {
				renderJSON(new BerainResult(Success, true));
			} else {
				renderJSON(new BerainResult(NonExists, false));
			}
		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}
	}

	// --------------------------Event-----------------------------//
	public static void addWatchable(String clientId, String id, int type) {

		try {
			container.addWatchableEvent(clientId, new WatchableEvent(type, id));
			renderJSON(new BerainResult(Success, true));

		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}
	}

	public static void removeWatchable(String clientId, String id, int type) {
		try {
			container.removeWatchableEvent(clientId, new WatchableEvent(type,
					id));
			renderJSON(new BerainResult(Success, true));

		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}

	}

	public static void removeWatchedEvent(String clientId, String id, int type) {
		try {
			container.removeWatchedEvent(clientId, id, type);
			renderJSON(new BerainResult(Success, true));

		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}

	}

	public static void removeAllListener(String clientId) {

		try {
			container.clearAllWatchableEvent(clientId);
			container.clearAllWatchedEvent(clientId);
			renderJSON(new BerainResult(Success, true));

		} catch (Throwable e) {
			e.printStackTrace();
			renderJSON(new BerainResult(ServerError, false));
		}
	}

	//
	// public static void listChangedNodes(String clientId, List<String> paths,
	// List<Integer> types) {
	// Map<String, Set<WatchedEvent>> data = new HashMap<>();
	// try {
	// int psize = paths == null ? 0 : paths.size();
	// int tsize = types == null ? 0 : types.size();
	// int size = psize;
	// if (psize > tsize) {
	// size = tsize;
	// }
	// for (int i = 0; i < size; i++) {
	// String path = paths.get(i);
	// int eventType = types.get(i);
	// WatchableEvent event = container.varifyWatchableEvent(clientId,
	// path, eventType);
	// }
	//
	// Map<String, Set<WatchedEvent>> edata = container
	// .getAllWatchedEvents(clientId);
	// if (edata != null)
	// data.putAll(edata);
	// renderJSON(new BerainResult(Success, data));
	// } catch (Throwable e) {
	// e.printStackTrace();
	// renderJSON(new BerainResult(ServerError, null));
	// }
	// }

	public static void dump(String clientId) {
		if (clientId == null || "".equals(clientId)) {
			renderJSON(new BerainResult(Success, container));
		} else {

			renderJSON(new BerainResult(Success, container.getWatcheds().get(
					clientId)));
		}

	}

}
