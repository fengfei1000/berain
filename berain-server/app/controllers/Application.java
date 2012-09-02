package controllers;

import japidviews.Application.Index;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.RainModel;

import org.apache.commons.beanutils.BeanUtils;

import cn.bran.play.JapidResult;
import fengfei.berain.server.Focus;

public class Application extends Admin {

	public static void index() {
		throw new JapidResult(new Index().render());
	}

	public static void namespace() {
		Map<String, Object> data = new HashMap<>();
		try {
			Focus.persistence.initNamespace();

			RainModel model = Focus.persistence.getNodeByPath(Focus.namespace);
			data.put("_id", model.id);
			data.put("namespace", Focus.namespace);

		} catch (Exception e) {

			e.printStackTrace();
		}
		renderJSON(data);
	}

	@play.db.jpa.Transactional
	public static void addNode(String pid) {

		String key = params.get("key");
		String value = params.get("value");

		RainModel model;
		try {
			model = Focus.persistence.addNode(pid, key, value);

			if (model == null) {
				renderText("Add Fail.");
			} else {
				renderText("Add Success. ");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Throwable e2 = getLastException(e);
			String msg = e2 == null ? e.getMessage() : e2.getMessage();
			renderText("Add Fail." + msg);
		}

	}

	private static Throwable getLastException(Throwable e) {
		Throwable e2 = e.getCause();
		if (e2 == null) {
			return e;
		} else if (e.getClass() == e2.getClass()) {
			return e;
		} else {
			return getLastException(e2);
		}

	}

	@play.db.jpa.Transactional
	public static void editNode(String id) {
		String value = params.get("pvalue");

		try {
			RainModel another = Focus.persistence.editNode(id, value);
			System.out.println(another);
			if (another == null) {
				renderText("Update fail. ");
			} else {
				renderText("Update success. ");
			}

		} catch (Exception e) {
			renderText("Update faile, server error. ");
			e.printStackTrace();
		}

	}

	@play.db.jpa.Transactional
	public static void removeNode(String id) {
		try {
			RainModel another = Focus.persistence.removeNode(id);
			if (another == null) {
				renderText("Remove Node Fail, Not exists.");
			} else {
				renderText("Remove Node Success. ");
			}
		} catch (Exception e) {
			renderText("Remove Node Fail, server error.");
			e.printStackTrace();
		}

	}

	@play.db.jpa.Transactional
	public static void getNode(String id) {
		try {
			RainModel model = Focus.persistence.getNode(id);
			if (model == null) {
				renderText("{status:'fail'}");
			} else {
				Map<String, Object> data = new HashMap<>();
				model.md5 = null;
				model.updateAt = null;
				model.createAt = null;
				model.path = Focus.toPath(model.path);
				Map<String, String> m = BeanUtils.describe(model);
				m.remove("class");
				m.remove("value");
				m.put("id", "rain" + model.id);
				m.put("svalue", model.value);
				m.put("_id", String.valueOf(model.id));

				data.put("data", model.key);
				data.put("attr", m);
				data.put("state", "closed");
				data.put("status", "success");
				data.put("children", new Object[] {});
				renderJSON(data);
			}
		} catch (Exception e) {
			renderText("{status:'fail'}");
			e.printStackTrace();
		}
	}

	@play.db.jpa.Transactional
	public static void nextNodes(String id) {
		List<Map<String, Object>> datas = new ArrayList<>();

		try {
			List<RainModel> models = Focus.persistence.nextNodes(id);
			if (models != null && models.size() > 0) {
				for (RainModel model : models) {
					try {
						Map<String, Object> data = new HashMap<>();
						model.md5 = null;
						model.updateAt = null;
						model.createAt = null;
						model.path = Focus.toPath(model.path);
						Map<String, String> m = BeanUtils.describe(model);
						m.remove("class");
						m.remove("value");
						m.put("id", "rain" + model.id);
						m.put("svalue", model.value);
						m.put("_id", String.valueOf(model.id));
						data.put("data", model.key);
						data.put("attr", m);
						data.put("state", "closed");
						data.put("children", new Object[] {});
						datas.add(data);
					} catch (
							IllegalAccessException
							| InvocationTargetException
							| NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
				System.out.println(datas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		renderJSON(datas);
	}
}