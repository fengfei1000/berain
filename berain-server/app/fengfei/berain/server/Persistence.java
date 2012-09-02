package fengfei.berain.server;

import java.util.List;
import java.util.Map;

import models.RainModel;

public interface Persistence {

	public static final String ROOT_PATH = "/";
	public static final String SEPARATOR = "/";

	RainModel addNode(String pid, String key, String value) throws Exception;

	RainModel editNode(String id, String value) throws Exception;

	RainModel removeNode(String id) throws Exception;

	RainModel getNode(String id) throws Exception;

	boolean exists(String path) throws Exception;

	List<RainModel> nextNodes(String id) throws Exception;

	void initNamespace() throws Exception;;

	RainModel getNodeByPath(String path) throws Exception;

}