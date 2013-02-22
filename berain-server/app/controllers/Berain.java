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
	public static void update(String path, String value) {

		BerainResult<Boolean> br = BerainHelper.update(path, value);
		renderJSON(br);
	}

	public static void create(String path, String value) {
		BerainResult<Boolean> br = BerainHelper.create(path, value);
		renderJSON(br);

	}

	public static void delete(String path) {
		BerainResult<Boolean> br = BerainHelper.delete(path);
		renderJSON(br);
	}

	public static void copy(String originalPath, String newPath) {
		BerainResult<Boolean> br = BerainHelper.copy(originalPath, newPath);
		renderJSON(br);

	}

	// --------------------------read-----------------------------//
	public static void nextChildren(String parentPath) {
		BerainResult<List<Map<String, String>>> br = BerainHelper
				.nextChildren(parentPath);
		renderJSON(br);
	}

	public static void get(String path) {
		BerainResult<String> br = BerainHelper.get(path);
		renderJSON(br);

	}

	public static void getFull(String path) {
		BerainResult<Map<String, String>> br = BerainHelper.getFull(path);
		renderJSON(br);

	}

	public static void exists(String path) {
		BerainResult<Boolean> br = BerainHelper.exists(path);
		renderJSON(br);

	}

	// --------------------------Event-----------------------------//
	public static void addWatchable(String clientId, String path, int type) {
		BerainResult<Boolean> br = BerainHelper.addWatchable(clientId, path,
				type);
		renderJSON(br);

	}

	public static void removeWatchable(String clientId, String path, int type) {
		BerainResult<Boolean> br = BerainHelper.removeWatchable(clientId, path,
				type);
		renderJSON(br);

	}

	public static void removeWatchedEvent(String clientId, String path, int type) {
		BerainResult<Boolean> br = BerainHelper.removeWatchedEvent(clientId,
				path, type);
		renderJSON(br);

	}

	public static void removeAllListener(String clientId) {
		BerainResult<Boolean> br = BerainHelper.removeAllListener(clientId);
		renderJSON(br);

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
